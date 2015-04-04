#!/usr/bin/perl

use LWP::UserAgent; 
use HTTP::Request;
my $agent = LWP::UserAgent->new(env_proxy => 1,keep_alive => 1, timeout => 30); 
my $url = "http://qrm.quaysystems.com.au"; 
my $header = HTTP::Request->new(GET => $url); 
my $request = HTTP::Request->new('GET', $url, $header); 
my $response = $agent->request($request);

if ($response->code == 200){
	print "OK\n";
	exit 0;
}else {
	print "failure";
	exit 1;
}
