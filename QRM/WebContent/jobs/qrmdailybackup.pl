#!/usr/bin/perl

use DBI;
use DBD::mysql;

my @months = qw(Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec);

# CONFIG VARIABLES
my $platform = "mysql";
my $database = "qrmlogin";
my $host = "localhost";
my $port = "3306";
my $user = "root";
my $pw = "root";
my $root =  "/home/david/mysqlbackup/";
my $backUpRoot = "/home/david/mysqlbackup/QRMBackup_";
my $dsn = "dbi:$platform:$database:$host:$port";
my ($second, $minute, $hour, $dayOfMonth, $month, $yearOffset, $dayOfWeek, $dayOfYear, $daylightSavings) = localtime(time);

$year = 1900 + $yearOffset;
$hour = "0".$hour if ($hour < 10);
$minute = "0".$minute if ($minute < 10);

&saveDatabase($database);

# PREPARE THE QUERY
$query_handle = DBI->connect($dsn, $user, $pw)->prepare("SELECT cat, databaseID from qrmdatabase");
$query_handle->execute();
$query_handle->bind_columns(undef, \$cat, \$databaseID);

# LOOP THROUGH RESULTS
while($query_handle->fetch()) {
	&saveDatabase($cat);
    $conn =  DBI->connect("dbi:$platform:$cat:$host:$port", $user, $pw);
	$query_handle2 = $conn->prepare("SELECT jobID FROM jobqueue WHERE executedDate < DATE_SUB(NOW(), INTERVAL 2 WEEK)");
	$query_handle2->execute();
	$query_handle2->bind_columns(undef, \$jobID);
	while($query_handle2->fetch()) {
		$conn->prepare("DELETE FROM reportdata WHERE jobID = $jobID")->execute();
	}	
	$conn->prepare("DELETE FROM jobqueue WHERE executedDate < DATE_SUB(NOW(), INTERVAL 2 week)")->execute();
	$conn->prepare("DELETE FROM reportsession WHERE timeInit < DATE_SUB(NOW(), INTERVAL 2 week)")->execute();
}
DBI->connect($dsn, $user, $pw)->prepare("DELETE FROM reportdata WHERE dateEmailSent < DATE_SUB(NOW(), INTERVAL 2 WEEK)")->execute();

&flushDB();

sub flushDB {
	$DBIconnect = DBI->connect($dsn, $user, $pw);
	$DBIconnect->prepare("FLUSH QUERY CACHE")->execute();
	$DBIconnect->prepare("FLUSH LOGS")->execute();
	`mysqlcheck -Aa -u$user -p$pw > /dev/null`;
}

sub saveDatabase {
	my($cat) = @_;    
	$file = "$backUpRoot$year$months[$month]$dayOfMonth"."_".$hour.$minute."_".$cat.".sql";
	`mysqldump --opt --routines --dump-date --single-transaction --complete-insert --add-locks --add-drop-database --add-drop-database --databases --create-options -u$user -p$pw $cat > $file`;
	`tar -czf $file.tar.gz $file`;
	`rm $file`;
}
