<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="EXPIRES" content="0">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Password Reset | Quay Risk Manager</title>
<link type="text/css" rel="stylesheet" href="login.css" />
<link rel="shortcut icon" href="./images/icons/16/disk_blue.png" />
</head>

<body class="login_page ie7 Locale_en_US UIPage_LoggedOut"	style='background: #C6DEFF'>
<div class="Box_Container clearfix">
<div>

<div class="title_header add_border" style='background: #C6DEFF; border-bottom: solid 1px #ccc'>
	<h2 class="title_h no_icon">Quay Risk Manager Password Reset</h2>
</div>

<form method="POST"	action="./qrm?OPERATION=passwordReset"	id="pwreset_form1">
<div id="standard_status" class="MessageBox status">
<h2 class="main_message">
<%  if (request.getAttribute("qrmMsg") != null){
	  out.println(request.getAttribute("qrmMsg")+" Please enter your login information to reset your password");
	} else {
	  out.println("Please enter your login information to reset your password");
	}
%>
</h2>
</div>
<div id="loginform" style="">
<div class="form_row clearfix "><label for="name" id="label_email">User Name:</label><input type="text" class="inputtext" id="name" name="NAME" value=""/></div>
<div class="form_row clearfix "><label for="email" id="label_pass">Email Address:</label><input type="text" class="inputtext" id="email" name="EMAIL" value="" /></div>
<div id="buttons" class="form_row clearfix" style="width:275px"><input type="submit" value="Reset Password" name="reset" id="reset"	onclick="this.form.submit(); return false;" class="inputsubmit" /></div>
</div>
</form>
</div>
</div>
</body>
</html>