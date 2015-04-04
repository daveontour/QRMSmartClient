<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
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

<form method="POST"	action="./qrm?OPERATION=passwordReset"	id="pwreset_form2">
<div id="standard_status" class="MessageBox status">
<h2 class="main_message">Please enter your information to reset your password</h2>
</div>
<div id="loginform" style="">
<input type="hidden" class="inputtext" id="name" name="NAME" value="<% out.print(request.getAttribute("name")); %>"/>
<input type="hidden" class="inputtext" id="email" name="EMAIL" value="<% out.print(request.getAttribute("email")); %>" />
<div class="form_row clearfix "><label for="question" id="label_question">Secret Question:</label><span  id="question"><% out.println(request.getAttribute("secretQuestion")); %></span></div>
<div class="form_row clearfix "><label for="answer" id="label_answer">Answer:</label><input type="text" class="inputtext" id="answer" name="ANSWER" /></div>
<div class="form_row clearfix "><label for="pass1" id="label_email">New Password:</label><input type="password" class="inputtext" id="pass1" name="PASS1" /></div>
<div class="form_row clearfix "><label for="pass2" id="label_pass">Confirm Password:</label><input type="password" class="inputtext" id="pass2" name="PASS2" /></div>
<div id="buttons" class="form_row clearfix" style="width:275px"><input type="submit" value="Reset Password" name="reset" id="reset"	onclick="this.form.submit(); return false;" class="inputsubmit" /></div>
</div>
</form>
</div>
</div>
</body>

</html>