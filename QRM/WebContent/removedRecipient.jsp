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
				<h1 class="title_h no_icon">Quay Risk Manager - Removed
					Recipient from Report Distribution</h1>
			</div>
			<div id="standard_status" class="MessageBox status">
				<h2 class="main_message">Recipient successfully removed from
					report distribution</h2>
			</div>
			<br />
			<table width="100%" border="0">
				<tr>
					<td width="51%" valign='top'>
						<h1 class="title_h no_icon" style="color: gray">
							The email address <strong style="color: black">
								<% out.print(request.getAttribute("email")); %>
							</strong> was successfully removed from the distribution of report <strong
								style="color: black">
								<% out.print(request.getAttribute("reportName")); %>
							</strong>
						</h1>
					</td>

				</tr>
			</table>
		</div>
	</div>
</body>
</html>