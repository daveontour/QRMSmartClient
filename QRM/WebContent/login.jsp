<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*"%>
<%@page import="java.io.*"%>
<%@page import="java.net.URL"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="EXPIRES" content="0">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login | Quay Risk Manager</title>
<style type="text/css">
body {
	background: #fff;
	font-family: "lucida grande", tahoma, verdana, arial, sans-serif;
	font-size: 11px;
	color: #333;
	margin: 10;
	padding: 0;
	text-align: left;
	direction: ltr;
	unicode-bidi: embed
}

h1,h2,h3,h4,h5 {
	font-size: 13px;
	color: #333;
	margin: 0;
	padding: 0
}

h1 {
	font-size: 14px
}

h4,h5 {
	font-size: 11px
}

p {
	font-family: "lucida grande", tahoma, verdana, arial, sans-serif;
	font-size: 11px;
	text-align: left
}

a {
	cursor: pointer;
	color: #3b5998;
	text-decoration: none
}

a:hover {
	text-decoration: underline
}

.clearfix:after {
	clear: both;
	content: ".";
	display: block;
	font-size: 0;
	height: 0;
	line-height: 0;
	visibility: hidden
}

.clearfix {
	display: block
}

form {
	margin: 0;
	padding: 0
}

.signuplabel {
	color: #666;
	font-weight: bold;
	vertical-align: middle
}

label input {
	font-weight: normal
}

.formbuttons {
	text-align: center;
	margin: 10px 10px
}

.formbuttons .inputsubmit,.formbuttons .inputbutton {
	margin: 2px 4px
}

.inputtext {
	border: 1px solid #bdc7d8;
	font-family: "lucida grande", tahoma, verdana, arial, sans-serif;
	font-size: 11px;
	padding: 3px;
	width: 200px;
}

.inputradio {
	padding: 0;
	margin: 0 5px 0 0;
	vertical-align: middle
}

.inputbutton,.inputsubmit {
	padding: 2px 15px 3px 15px;
	border-style: solid;
	border-top-width: 1px;
	border-left-width: 1px;
	border-bottom-width: 1px;
	border-right-width: 1px;
	border-top-color: #d9dfea;
	border-left-color: #d9dfea;
	border-bottom-color: #0e1f5b;
	border-right-color: #0e1f5b;
	background-color: #3b5998;
	color: #fff;
	font-size: 11px;
	font-family: "lucida grande", tahoma, verdana, arial, sans-serif;
	text-align: center
}

.login_page .title_header {
	margin: 0 0 10px 0;
	padding: 0 0 10px 0
}

.login_page .login_page_spacer {
	height: 35px
}

.login_page .title_header h2.no_icon {
	background: transparent none repeat scroll 0;
	margin: 0;
	padding: 0
}

.login_page #loginform {
	clear: left;
	padding: 15px 0;
	text-align: left;
	width: 380px
}

.login_page #loginform p {
	line-height: 16px;
	margin: 10px 0;
	text-align: left
}

.login_page #loginform p.reset_password {
	margin-bottom: 0;
	padding-bottom: 0
}

.form_row {
	padding: 0 0 8px 0;
	text-align: left
}

.form_row div {
	display: block;
	float: left;
	padding: 3px 0;
	width: 130px;
	color: #666;
	font-weight: bold;
}

.form_row input {
	margin: 0
}

.form_row .inputtext,.inputpassword {
	width: 95%
}

#buttons {
	padding: 5px 0 0 130px;
	text-align: left
}

#buttons input {
	margin: 0 2px 0 0
}

#buttons label {
	float: none;
	width: auto
}

.reset_password {
	padding-left: 130px
}

.title_header {
	background: white;
	padding: 20px 20px 17px
}

.title_header h2.title_h {
	margin: 0;
	font-size: 14px;
	padding: 0 0 0 24px;
}

.title_header h2.no_icon {
	background: none;
	padding: 0
}

.title_header.add_border {
	border-bottom: solid 1px #ccc
}

.title_header h4.title_h {
	color: #666;
	font-size: 11px;
	font-weight: normal;
	padding: 3px 0 0 24px
}

.title_header h4.no_icon {
	padding: 3px 0 0 0
}

.Box_Container {
	padding-top: 10px;
	padding-right: 10px;
	padding-bottom: 10px;
	padding-left: 10px;
}

.MessageBox {
	padding: 10px;
	border-width: 1px;
	border-style: solid
}

.status {
	background-color: #3b5998;
	color: #fff
}

.main_message {
	color: white;
	font-size: 24px;
}

.inputbutton,.inputsubmit {
	width: 1;
	overflow: visible;
	padding: 2px 15px;
	background-color: #06C;
}
</style>
<link rel="shortcut icon" href="./images/icons/16/Q.png" />
<script type="text/javascript">
 function _qrmSubmitForm(){
	 document.getElementById('loginMessage').innerHTML='<span style="color:black;font-weight:bold;font-size:10pt">Connecting to Server..</span>';
	 document.getElementById('login_form').submit(); 
	 return true;
 }
 function _qrmclearMsg(){
	 document.getElementById('loginMessage').innerHTML='<h2 style="color:black;font-weight:normal"></h2>';
	  document.getElementById('signUpMsg').innerHTML='<h2 style="color:black;font-weight:normal"></h2>';
 }
 function showContent() {
	 var regDiv = document.getElementById('registerDiv');

	 	if(regDiv.style.display == "none") {
			regDiv.style.display = "block";
			document.getElementById("secode").src= "./simpleImg";
	 	} else {
			 regDiv.style.display = "none";
	 	}
	 	return;
 }
 </script>
<style type="text/css">
.inputbutton1 {
	padding: 2px 15px 3px 15px;
	border-style: solid;
	border-top-width: 1px;
	border-left-width: 1px;
	border-bottom-width: 1px;
	border-right-width: 1px;
	border-top-color: #d9dfea;
	border-left-color: #d9dfea;
	border-bottom-color: #0e1f5b;
	border-right-color: #0e1f5b;
	background-color: #3b5998;
	color: #fff;
	font-size: 11px;
	font-family: "lucida grande", tahoma, verdana, arial, sans-serif;
	text-align: center
}

.inputbutton1 {
	width: 1;
	overflow: visible;
	padding: 2px 15px;
	background-color: #06C;
}

.inputsubmit1 {
	padding: 2px 15px 3px 15px;
	border-style: solid;
	border-top-width: 1px;
	border-left-width: 1px;
	border-bottom-width: 1px;
	border-right-width: 1px;
	border-top-color: #d9dfea;
	border-left-color: #d9dfea;
	border-bottom-color: #0e1f5b;
	border-right-color: #0e1f5b;
	background-color: #3b5998;
	color: #fff;
	font-size: 11px;
	font-family: "lucida grande", tahoma, verdana, arial, sans-serif;
	text-align: center
}

.inputsubmit1 {
	width: 1;
	overflow: visible;
	padding: 2px 15px;
	background-color: #06C;
}
</style>
</head>

<body class="login_page Box_Container clearfix"
	style='background: #C6DEFF'>

	<div class="MessageBox status" style="height: 80px">
		<form method="POST"	action="./qrm?launch=<% out.print( new Date().getTime()); %>" id="login_form">
			<table width="850px">
				<tr>
					<td width="350" rowspan="3"><a	href="http://www.quaysystems.com.au" class="main_message" style="color: #C3DFED; font-family: 'Segoe UI', Arial, Helvetica, sans-serif; font-size: 22px; font-weight: bold;">
							QUAY RISK MANAGER</a></td>
					<td width="210">Email Address:</td>
					<td width="210">Password:</td>
					<td width="80">&nbsp;</td>

				</tr>
				<tr>
					<td><input type="text" class="inputtext" id="email"	name="NAME" onkeydown="_qrmclearMsg()"	value="<%if  (request.getAttribute("qrmName") != null){ out.println(request.getAttribute("qrmName"));}%>" /></td>
					<td><input type="password" class="inputtext" id="pass" name="PASS" value="" onkeydown="_qrmclearMsg()" /></td>
					<td><input type="submit" value="Login" name="login" id="login" onClick="return _qrmSubmitForm();" class="inputsubmit1" /></td>

				</tr>
				<tr>
					<td colspan="3"><a href="./resetpassword.jsp"
						id="reg_btn_link" target="_self" rel="nofollow"
						style="color: white">Forgot Your Password?</a> &nbsp;&nbsp;&nbsp;
						<a href="#" onclick="showContent();" style="color: white">Register
							With Quay Risk Manager</a></td>
				</tr>
			</table>
		</form>
	</div>


	<div style="height: 30px; padding-top: 5px">
		<% 
	     if (request.getAttribute("qrmPasswordIncorrect") != null || request.getAttribute("qrmUserNotFound") != null) {
			out.println("<h2 id='loginMessage' ><span style=\"color:red;font-weight:bold;font-size:10pt\">Username/Password Incorrect</span></h2>");
		} else if(request.getAttribute("qrmLoginDisabled") != null){
				out.println("<h2 id='loginMessage'><span style=\"color:red;font-weight:bold;font-size:10pt\">Your access to QRM has been disabled. Please contact the administrator</span></h2>");
		} else if(request.getAttribute("qrmMultipleUsers") != null){
			out.println("<h2 id='loginMessage'><span style=\"color:red;font-weight:bold;font-size:10pt\">Multiple Users Found - please use your email address to logon</span></h2>");
		} else if (request.getAttribute("qrmNoCatalogsDefined") != null){
			out.println("<h2 id='loginMessage'  style=\"color:red;font-weight:bold;font-size:10pt\">You have not been configured with accsss to any risk repositories. Please contact your risk repository administrator</h2>");
		} else if(request.getAttribute("qrmTooManySessions") != null){
			out.println("<h2 id='loginMessage'  style=\"color:red;font-weight:bold;font-size:10pt\">The number of logged on users for the selected repository has been exceeded.</h2>");
		} else if (request.getAttribute("qrmMsg") != null){
			out.println("<h2 id='loginMessage' style=\"color:black\">"+request.getAttribute("qrmMsg")+"</h2>");
		} else {
			out.println("<h2 id='loginMessage' style=\"color:black\"></h2>");
		} 
		%>
	</div>

	<table>
		<tr>
			<td>

				<div style="clear: left; width: 600">
					<%		
			try {
	       		BufferedReader buffreader = new BufferedReader(new InputStreamReader(config.getServletContext().getResource("/QRMLoginMessage.html").openStream())); 
	            String inputLine;
	             while ((inputLine = buffreader.readLine()) != null) {
	                	 out.println(inputLine);
	             }
	             buffreader.close();
			}  catch (Exception e2){ }
	        %>


				</div>
			</td>
			<td align="left" valign="top">
				<div
					style="width:350px;display:<% if (request.getAttribute("qrmMsgLogin") != null ||request.getAttribute("qrmBadSecurityCode") != null){
	out.println("block;");} else {out.println("none;");} %>"
					id="registerDiv">
					<!-- Start of Right Bottom Div -->


					<h2 style="margin-bottom: 5px;">Register With Quay Risk
						Manager</h2>


					<form method="POST"
						action="./signupUser" id="login_form">
						<table width="350" border="0">
							<tr>
								<td width="120" class="signuplabel">User Name:</td>
								<td width="230"><input type="text" class="inputtext"
									id="name" name="NAME" onkeydown="_qrmclearMsg()" /></td>
							</tr>
							<tr>
								<td class="signuplabel">Email:</td>
								<td><input type="text" class="inputtext" id="email2"
									name="EMAIL" onkeydown="_qrmclearMsg()" /></td>
							</tr>
							<tr>
								<td class="signuplabel">Password:</td>
								<td><input type="password" class="inputtext" id="pass3"
									name="PASS1" value="" onkeydown="_qrmclearMsg()" /></td>
							</tr>
							<tr>
								<td class="signuplabel">Password Verify:</td>
								<td><input type="password" class="inputtext" id="pass2"
									name="PASS2" onkeydown="_qrmclearMsg()" /></td>
							</tr>
							<tr>
								<td class="signuplabel">Organisation Code:</td>
								<td><input type="text" class="inputtext" id="orgcode"
									name="ORG" onkeydown="_qrmclearMsg()" /></td>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td class="signuplabel"><strong>Enter "GUEST" for
										a trial account</strong></td>
							</tr>
							<tr>
								<td class="signuplabel">Security Code:</td>
								<td><img alt="" id="secode" /></td>
							</tr>
							<tr>
								<td class="signuplabel">Enter Security Code:</td>
								<td><input type="text" class="inputtext" id="captcha"
									name="ANSWER" onkeydown="_qrmclearMsg()" /></td>
							</tr>
			<!-- 				
							<tr>
								<td>&nbsp;</td>
								<td class="signuplabel"><input type="checkbox" id="read"
									name="READ" onkeydown="_qrmclearMsg()"> I have read the
									condition of use</td>
							</tr>
			 -->
			 				<tr>
								<td>&nbsp;</td>
								<td>
								     <input type="submit" value="Sign Up" name="login2"	id="login2" onClick="this.form.submit(); return false;"	class="inputsubmit" />
									 <input type="button" value="Cancel" name="cancel" id="cancel"  onClick="window.location.href = './login.jsp';"	class="inputsubmit" />
								</td>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td>
									<div style="height: 25px; padding-top: 5px">
										<h2 id="signUpMsg" style="color: red; font-weight: normal">
											<% if (request.getAttribute("qrmMsgLogin") != null ){
			out.println(request.getAttribute("qrmMsgLogin"));
		} else if (request.getAttribute("qrmBadSecurityCode") != null){
			out.println("Security Code Invalid");
		}%>
										</h2>
									</div>
								</td>
							</tr>
						</table>
					</form>
				</div> <!-- End of right Bottom DIV  -->

			</td>
		</tr>
	</table>
</body>
</html>