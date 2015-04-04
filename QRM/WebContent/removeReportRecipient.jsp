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
<title>QRM | Remove Recipient</title>
<link type="text/css" rel="stylesheet" href="login.css" />
<link rel="shortcut icon" href="./images/icons/16/disk_blue.png" />
</head>

<body class="login_page" style='background: #C6DEFF'>
	<div class="Box_Container clearfix">
		<div style="height: 100%">

			<div class="title_header add_border"
				style='background: #C6DEFF; border-bottom: solid 1px #ccc'>
				<h1 class="title_h no_icon">Quay Risk Manager - Remove
					Recipient from Report Distribution</h1>
			</div>
			<div id="standard_status" class="MessageBox status">
				<h2 class="main_message">To remove yourself from the report
					distribution, please enter your email address</h2>
			</div>
			<br />
			<table width="100%" border="0">
				<tr>
					<td width="51%" valign='top'>
						<form method="POST"
							action="./QRMServer?OPERATION=removeReportRecipient"
							id="login_form">
							<div id="loginform">
								<div class="form_row clearfix ">
									<label for="email" id="label_email"> Email Address:</label> <input
										type="text" class="inputtext" id="email" name="NAME" />
								</div>

							</div>
							<div id="buttons" class="form_row clearfix" style="width: 400px">
								<input type="submit" value="Remove" name="login" id="login"
									onclick="document.getElementById('loginMessage').innerHTML='<span class='main_message'>Connecting to Server..</span>';this.form.submit(); return false;"
									class="inputsubmit" /> <input type="hidden" name="jobID"
									value="<% out.print(request.getParameter("jobID"));%>" /> <input
									type="hidden" name="repID"
									value="<% out.print(request.getParameter("repID"));%>" />

							</div>
						</form>
					</td>
				</tr>
			</table>
		</div>
	</div>
</body>
</html>