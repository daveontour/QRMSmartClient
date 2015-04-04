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
<title>Login | Quay Risk Manager</title>
<link type="text/css" rel="stylesheet" href="login.css" />
</head>

<body class="login_page ie7 Locale_en_US UIPage_LoggedOut"
	style='background: #C6DEFF'>
	<div class="Box_Container clearfix">
		<div>

			<div class="title_header add_border"
				style='background: #C6DEFF; border-bottom: solid 1px #ccc'>
				<h2 class="title_h no_icon">Quay Risk Manager Login</h2>
			</div>

			<form method="POST" action="./qrm?OPERATION=selectRepository"
				id="login_form">
				<div id="standard_status" class="MessageBox status">
					<h2 class="main_message">You have access to multiple
						repositories. Please select desired repository</h2>
				</div>
				<div id="loginform"
					style="padding: 5px 0 0 130px; text-align: left; font-weight: bold">
					<% 
	ArrayList<String[]> reps = (ArrayList<String[]>)request.getAttribute("qrmSelectCatalog");
	for (String[] rep:reps){
		out.println("<input type='radio' name='ORG' checked=\"checked\" value='"+rep[1]+"'/>"+rep[0]+"<br/>");
	}
	
	
 	out.println("<div id='buttons' class='form_row clearfix' style='padding:5px 0 0 0px;'>");
 	out.println("<input type='submit' value='Login' name='login' onclick='this.form.submit(); return false;' class='inputsubmit' />");
 	out.println("<input type='submit' value='Cancel' name='cancel' onclick='window.open(\"./login.jsp\", \"_self\");return false;' class='inputsubmit' />");
  	if (request.getAttribute("admin") != null){
			out.println("<input type=\"hidden\" name=\"QRMADMIN\" value=\"admin\" />");
	}
  	if (request.getAttribute("repmgr") != null){
		out.println("<input type=\"hidden\" name=\"QRMADMIN\" value=\"rep\" />");
	} 
%>
				</div>
			</form>
		</div>
	</div>
</body>

</html>