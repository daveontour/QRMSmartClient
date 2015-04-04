#!/usr/bin/perl

# PERL MODULES WE WILL BE USING
use DBI;
use DBD::mysql;
use MIME::Lite;
use MIME::Base64;
use Authen::SASL;
use Archive::Zip;


# CONFIG VARIABLES
my $platform = "mysql";
my $database = "qrmlogin";
my $host     = "localhost";
my $port     = "3306";
my $user     = "root";
my $tempZipFile = "/opt/jetty/webapps/root/temp/someZip.zip";

my $dbpw         = "aiw2dihsf";
my $dsn        = "dbi:$platform:$database:$host:$port";
my $dbh = DBI->connect( $dsn, $user, $dbpw );

my $mailUser = "support@quaysystems.com.au";
my $mailPass = "aiw2dihsf";
my $mailHost = "localhost";

# PREPARE THE QUERY
my $query = "SELECT * FROM jobemailready";
my $statement = "SELECT cat FROM qrmdatabase";
my $updateStatement = "UPDATE reportdata set emailsent = 1, dateEmailsent = NOW() WHERE jobID = ?";

my $qh = $dbh->prepare($statement);

$qh->execute();
$qh->bind_columns(undef,\$cat);

while ( $qh->fetch() ) {

	my $dbh2 = DBI->connect( "dbi:$platform:$cat:$host:$port", $user, $dbpw );
	$dbh2->{'AutoCommit'} = 1;
	
	my $qh2 = $dbh2->prepare($query);
	$qh2->execute();
	$qh2->bind_columns( undef, \$jobID, \$sendEmail, \$emailSent, \$emailTitle, \$description1, \$emailFormat, \$emailContent, \$dateEmailSent, \$resultStr, \$additionalUsers, \$description2, \$readyToCollect, \$reportFormat, \$email, \$name );
	
	while ($qh2->fetch()){
		
		my $attachFileName = $description2.'.'.$reportFormat;

		my $msg = MIME::Lite->new(
			Subject => $emailTitle,
			From    => 'support@quaysystems.com.au',
			To      => $email,
			Cc      => $additionalUsers,
			Type    => 'multipart/mixed'
		);
	
		$msg->attach(Type => 'text/html',Data => $emailContent);
		
		if($reportFormat eq "PDF"){
			$msg->attach(Type=> 'application/pdf',Filename=> $attachFileName,Data=> $resultStr);			
		} else {
			my $zip = Archive::Zip->new();
			my $string_member = $zip->addString( $resultStr, $attachFileName );
		    	$string_member->desiredCompressionMethod( Archive::Zip::COMPRESSION_DEFLATED );
		    	$zip->writeToFileNamed($tempZipFile);			
			$msg->attach(Type=> 'application/zip',Filename=>'report.zip',Path=> $tempZipFile);
		}

		$msg->send('smtp', $mailHost, AuthUser=>$mailUser,AuthPass=>$mailPass,Debug => 0);
		
	        unlink($tempZipFile);
		
		#Update the database to indicate that the email has been sent		
		my $qh3 = $dbh2->prepare($updateStatement);
		$qh3->execute($jobID) or die "Cannot execute database update";
		$qh3->finish();
	}
	$dbh2->disconnect();
}

$qh->finish();
$dbh->disconnect();
