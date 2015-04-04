<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.FileInputStream"%>
<%@ page import="java.io.File"%>
<%@ page import="java.util.Properties"%>
<%@ page import="javax.servlet.ServletContextAttributeEvent"%>
<%@ page import="javax.servlet.ServletContextAttributeListener"%>
<%@ page import="javax.servlet.ServletContextEvent"%>
<%@ page import="javax.servlet.ServletContextListener"%>

<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml" dir="ltr" lang="en-US">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF8" />
<meta http-equiv="X-UA-Compatible" content="IE=10">
<title>Quay Risk Manager</title>
<link rel="StyleSheet" href="./style/qrm_styles.css" type="text/css" />
<link rel="shortcut icon" href="./images/icons/16/Q.png" />
<script type="text/javascript">
	
<%String requestURL = request.getRequestURL().toString();
			requestURL = requestURL.replace("QRMApplication.jsp", "pollNG");
			out.println("var pollURL = '" + requestURL + "/poll';");
			out.println("var userName = '" + request.getAttribute("userName")
					+ "';");
			Properties configProp = new Properties();
			configProp.load(new FileInputStream(application
					.getRealPath("/QRM.properties")));
			String helpURLRoot = configProp.getProperty("HELPURL");
			if (helpURLRoot != null) {
				out.println("var helpURLRoot = '" + helpURLRoot + "'");
			} else {
				out.println("var helpURLRoot = '"+ application.getContextPath() + "/QRMHelp';");
			}%>
			
			<% 
			     if (request.getAttribute("repMgr") != null ){
					out.println("var userRepMgr = true;");
				} else {
					out.println("var userRepMgr = false;");					
				}
			
				%>
	var isomorphicDir = "./isomorphic/";
</script>

<!--CSS for loading message at application Startup-->
<style type="text/css">
body {
	overflow: hidden
}

#loading {
	border: 1px solid #ccc;
	position: absolute;
	left: 45%;
	top: 40%;
	padding: 2px;
	z-index: 20001;
	height: auto;
}

#loading .loadingIndicator {
	background: white;
	font: bold 13px tahoma, arial, helvetica;
	padding: 10px;
	margin: 0;
	height: auto;
	color: #444;
}

#loadingMsg {
	font: normal 10px arial, tahoma, sans-serif;
}
div.upload {
    width: 195px;
    height: 34px;
    background: url(./images/select_file_btn.png);
    overflow: hidden;
}

div.upload input {
    display: block !important;
    width: 195px !important;
    height: 34px !important;
    opacity: 0 !important;
    overflow: hidden !important;
}

div.upload2 {
    width: 155px;
    height: 34px;
    background: url(./images/select_risk_file_btn.png);
    overflow: hidden;
}

div.upload2 input {
    display: block !important;
    width: 155px !important;
    height: 34px !important;
    opacity: 0 !important;
    overflow: hidden !important;
}
</style>
</head>

<body style="background: #C6DEFF" onunload="disconnectPoll()">
	<div id="loadingWrapper">
		<div id="loading">
			<div class="loadingIndicator" style="float: left; width: 180px">
				<img alt="" src="./wait30trans.gif" style="float: left" /> Quay
				Risk Manager<br /> <span id="loadingMsg">Loading Quay Risk
					Manager</span>
			</div>
		</div>
	</div>

	<iframe id="_qrmFormTarget" name="_qrmFormTarget"
		style="width: 0; height: 0; border: 0; background: #C6DEFF; visibility: hidden"></iframe>
	<iframe id="_qrmDownloadTarget" name="_qrmDownloadTarget"
		style="width: 0; height: 0; border: 0; background: #C6DEFF; visibility: hidden"></iframe>
	<script type="text/javascript"
		src="./isomorphic/system/modules/ISC_Core.js"></script>
	<script type="text/javascript"
		src="./isomorphic/system/modules/ISC_Foundation.js"></script>
	<script type="text/javascript"
		src="./isomorphic/system/modules/ISC_Containers.js"></script>
	<script type="text/javascript"
		src="./isomorphic/system/modules/ISC_Grids.js"></script>
	<script type="text/javascript"
		src="./isomorphic/system/modules/ISC_Forms.js"></script>
	<script type="text/javascript"
		src="./isomorphic/system/modules/ISC_DataBinding.js"></script>
	<script type="text/javascript"
		src="./isomorphic/system/modules/ISC_Calendar.js"></script>
	<script type="text/javascript"
		src="./isomorphic/skins/Enterprise/load_skin.js"></script>
	<script type="text/javascript" src="./js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="./js/qrm/qrmjs.js"></script>
</body>
</html>