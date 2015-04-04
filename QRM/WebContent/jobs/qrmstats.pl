#!/usr/bin/perl

$msgData = "<html><body><table>";
# PERL MODULES WE WILL BE USING
use DBI;
use DBD::mysql;
use MIME::Lite;

# CONFIG VARIABLES
$platform = "mysql";
$database = "qrmlogin";
$host = "localhost";
$port = "3306";
$user = "root";

$pw = "root";
$dsn = "dbi:$platform:$database:$host:$port";
$DBIconnect = DBI->connect($dsn, $user, $pw);

# PREPARE THE QUERY
$query = "SELECT rep FROM repositories";
$query_handle = $DBIconnect->prepare($query);
$query_handle->execute();
$query_handle->bind_columns(undef, \$rep);

# LOOP THROUGH RESULTS
$msgData = "<table style=\"font-size:8pt\"><tr><td colspan=\"2\"><strong>Maximimum Concurrent Sessions per Repository</strong></td></tr>";

while($query_handle->fetch()) {

        $query_handle2 = $DBIconnect->prepare("SELECT max(sessioncountatstart) as max FROM repositorysession WHERE  sessionstartdate > DATE_SUB(NOW(), INTERVAL 1 DAY) order by max desc");
        $query_handle2->execute();
        $query_handle2->bind_columns(undef, \$max);
        $query_handle2->fetch();
        $msgData = $msgData."<tr><td>$rep:</td><td style=\"color:red\">$max</td></tr>"
}

$msgData = $msgData."<tr><td colspan=\"2\"><strong>Number of Sessions per User</strong></td></tr>";
$query = "select COUNT(*) AS sessions, s2.name from repositorysession AS s1 JOIN stakeholders AS s2 ON s1.userid = s2.stakeholderID WHERE s1.sessionstartdate > DATE_SUB(NOW(), INTERVAL 1 DAY) group by userid order by sessions desc";
$query_handle = $DBIconnect->prepare($query);
$query_handle->execute();
$query_handle->bind_columns(undef, \$sessions, \$qrmuser);
while($query_handle->fetch()) {
        $msgData = $msgData."<tr><td>$qrmuser:</td><td style=\"color:red\">$sessions</td></tr>";
}
$msgData = $msgData."</table><br/>";  

$query = "select COUNT(*) AS num from stakeholders";
$query_handle = $DBIconnect->prepare($query);
$query_handle->execute();
$query_handle->bind_columns(undef, \$num);
while($query_handle->fetch()) {
        $msgData = $msgData."<table style=\"font-size:8pt\"><tr><td><strong>Total Number of Stakeholders:</strong></td><td style=\"color:red\">$num</td></tr></table><br/>";
}

$msgData = $msgData."<table style=\"font-size:8pt\"><tr><td><strong>Configured Repositories</strong></td><td></td></tr>";

$query = "select rep, orgcode, userlimit, sessionlimit, dbUser, dbPass, s1.databaseID, s2.cat from repositories AS s1 JOIN qrmdatabase AS s2 ON s1.databaseID = s2.databaseID";
$query_handle = $DBIconnect->prepare($query);
$query_handle->execute();
$query_handle->bind_columns(undef, \$rep, \$orgcode, \$userlimit, \$sessionlimit, \$dbUser, \$dbPass,  \$databaseID, \$cat);
while($query_handle->fetch()) {

        $msgData = $msgData."<tr><td colspan=\"2\">$rep</td></tr><tr><td>&nbsp;&nbsp;&nbsp;Org Code:</td><td style=\"color:red\">$orgcode</td></tr><tr><td>&nbsp;&nbsp;&nbsp;Database User:</td><td style=\"color:red\"> $dbUser</td></tr><tr><td>&nbsp;&nbsp;&nbsp;User Limit:</td><td style=\"color:red\">$userlimit</td></tr><tr><td>&nbsp;&nbsp;&nbsp;Session Limit:</td><td style=\"color:red\">$sessionlimit</td></tr><tr><td>&nbsp;&nbsp;&nbsp;Database</td><td style=\"color:red\">$cat</td></tr><tr><td>";

        $dsn2 = "dbi:$platform:$cat:$host:$port";
        $DBIconnect2 = DBI->connect($dsn2, $dbUser, $dbPass);

        $query_handle2 = $DBIconnect2->prepare("SELECT count(*) AS num from securerisk");
        $query_handle2->execute();
        $query_handle2->bind_columns(undef, \$num);
        while($query_handle2->fetch()) {
                $msgData = $msgData."<tr><td>&nbsp;&nbsp;&nbsp;Risks:</td><td style=\"color:red\">$num</td></tr>";
        }
        $query_handle2 = $DBIconnect2->prepare("SELECT count(*) AS num from secureauditcomments");
        $query_handle2->execute();
        $query_handle2->bind_columns(undef, \$num);
        while($query_handle2->fetch()) {
                $msgData = $msgData."<tr><td>&nbsp;&nbsp;&nbsp;Comments:</td><td style=\"color:red\">$num</td></tr>";
        }
        $query_handle2 = $DBIconnect2->prepare("SELECT count(*) AS num from secureincident");
        $query_handle2->execute();
        $query_handle2->bind_columns(undef, \$num);
        while($query_handle2->fetch()) {
                $msgData = $msgData."<tr><td>&nbsp;&nbsp;&nbsp;Incidents:</td><td style=\"color:red\">$num</td></tr>";
        }
        $query_handle2 = $DBIconnect2->prepare("SELECT count(*) AS num from secureschedjob");
        $query_handle2->execute();
        $query_handle2->bind_columns(undef, \$num);
        while($query_handle2->fetch()) {
                $msgData = $msgData."<tr><td>&nbsp;&nbsp;&nbsp;Scheduled Jobs:</td><td style=\"color:red\">$num</td></tr>";
        }

}
$msgData = $msgData."</table><br/>";  
$msgData = $msgData."<table style=\"font-size:8pt\"><tr><td colspan=\"2\"><strong>Configured Databases</strong></td></tr>";

$query = "select databaseID, cat, title from qrmdatabase order by databaseID ASC";
$query_handle = $DBIconnect->prepare($query);
$query_handle->execute();
$query_handle->bind_columns(undef, \$databaseID, \$cat, \$title);
while($query_handle->fetch()) {
        $msgData = $msgData."<tr><td colspan=\"2\">$title</td></tr><tr><td>&nbsp;&nbsp;&nbsp;Catalog:</td><td style=\"color:red\">$cat</td></tr><tr><td>&nbsp;&nbsp;&nbsp;ID:</td><td style=\"color:red\">$databaseID</td></tr>";
        
        $dsn2 = "dbi:$platform:$cat:$host:$port";
        $DBIconnect2 = DBI->connect($dsn2, $user, $pw);
        $query_handle2 = $DBIconnect2->prepare("SELECT count(*) AS num from jobqueue");
        $query_handle2->execute();
        $query_handle2->bind_columns(undef, \$num);
        while($query_handle2->fetch()) {
                $msgData = $msgData."<tr><td>&nbsp;&nbsp;&nbsp;Jobs:</td><td style=\"color:red\">$num</td></tr>";
        }
        
        $query_handle2 = $DBIconnect2->prepare("SELECT count(*) AS num from jobqueue WHERE collected != 1");
        $query_handle2->execute();
        $query_handle2->bind_columns(undef, \$num);
        while($query_handle2->fetch()) {
                $msgData = $msgData."<tr><td>&nbsp;&nbsp;&nbsp;Un Collected Jobs:</td><td style=\"color:red\">$num</td></tr>";
        }
        
        $query_handle2 = $DBIconnect2->prepare("SELECT count(*) AS num from jobqueue WHERE readyToCollect != 1");
        $query_handle2->execute();
        $query_handle2->bind_columns(undef, \$num);
        while($query_handle2->fetch()) {
                $msgData = $msgData. "<tr><td>&nbsp;&nbsp;&nbsp;Un Proccessed Jobs:</td><td style=\"color:red\">$num</td></tr>";
        }
  }
 
$msgData = $msgData."</table></body></html>";  

$output=$ARGV[0];

if ($output eq "-print") {
        print $msgData;
} else {
		# Prepare Mail
		$title='QRM Usage';
		$to=$ARGV[1];
		$from= 'info@qrm.quaysystems.com.au';
		$subject='QRM Usage Summary';

        # create a new MIME Lite based email
        my $msg = MIME::Lite->new
        (
        Subject => $subject,
        From    => $from,
        To      => $to,
        Type    => 'text/html',
        Data    => $msgData
        );

        $msg->send();
}
