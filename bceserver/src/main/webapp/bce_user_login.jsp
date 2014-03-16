<%@page import="bce.server.entities.PersistentUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	if (session.getAttribute(PersistentUser.ATTRIBUTE_KEY) != null) {
		response.sendRedirect("MainPageServlet.sl");
		return;
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>Login——Broadcast Encryption Server</title>

<link rel="stylesheet" type="text/css" href="css/style.css" />

<script language="JavaScript" src="javascript/jquery-1.7.1.js" type="text/javascript"></script>
<script language="JavaScript" src="javascript/jquery.autosave.js" type="text/javascript"></script>
<script language="JavaScript" src="javascript/jquery.cookie.js" type="text/javascript"></script>
<script language="JavaScript" src="javascript/myjavascript.login.js" type="text/javascript"></script>
</head>
<body>
<form id="loginForm" action="LoginServlet.sl" method="post">
	<input id="flag" name="flag" type="hidden" value="0" />
	<div id="topnav" style="height: 16px; padding: 2px 4px; background-color: #fff; border-bottom: solid 1px #ccc;">
		<div class="topnav_left" style="float: left;">
			<a href="http://crypto.stanford.edu/pbc/bce/" target="_blank">About BCE</a>
		</div>
		<div class="topnav_right" style="float: right;">
			<script type="text/javascript"></script>
			|<a href="http://localhost:8080/bceserver/bce_user_login.jsp">Login</a>
			|<a href="http://localhost:8080/bceserver/bce_user_register.jsp">Free Register</a>
			|<a href="http://localhost:8080/bceserver/bce_user_help.jsp">Help</a>|
		</div>
		<div style="clear: both;"></div>
	</div>
	<div class="full">
		<div class="logo_login02">
			<img alt="BCE System Login" src="images/logo_login02.png" />
		</div>
		<div class="content_login">
			<div class="top_bg"></div>
			<div class="left_login">
				<div  id="div" class="div" style="width: 400px; height: 230px;"><!-- 290 -->
					<table>
						<tr>
							<td width="70">User Name: </td>
							<td >
								<input type="text" id="u" name="u" class="inputbox" maxlength="100" />
								<p id="error_u" class="error_one"></p>
							</td>
						</tr>
						<tr>
							<td>Password: </td>
							<td>
								<input type="password" id="p" name="p" class="inputbox" maxlength="50" />
								<p id="error_p" class="error_one"></p>
							</td>
						</tr>
						<tr id="tr_c" style="display: none;">
							<td>Authenticode: </td>
							<td>
								<input type="text" id="c" class="inputbox" maxlength="10" />
							</td>
						</tr>
						<tr id="tr_vc" style="display: none;">
							<td>
								<img id="vcImg" name="vcImg" alt="Authenticode" align="top" src="ValidateCodeServlet.sl" />
							</td>
							<td>
								<a id="aRecode" href="javascript: document.getElementById('vcImg').src='ValidateCodeServlet.sl'" class="font_gray" >Too vague, change it!</a>
							</td>
						</tr>
						<!--<tr>
							<td>
							</td>
							<td>
								<input type="radio" id="role" name="role" value="member" checked="checked" /><span style="font-size:12px;">会员</span>
								<input type="radio" id="role" name="role" value="admin" /><span style="font-size:12px;">管理员</span>
							</td>
						</tr>-->
						<tr>
							<td></td>
							<td>
								<input type="checkbox" name="chkRemember" id="chkRemember" style="vertical-align: middle;" class="checkbox" value="checked" />
								<label for="chkRemeber" class="font_red">Auto login in 2 weeks</label>
							</td>
						</tr>
						<% if (request.getAttribute("errorInfo") != null && ((String)request.getAttribute("errorInfo")).equals("unErr")) { %>
							<tr>
								<td colspan="2">
									<p class="error_one"><span>User Name not exists!</span></p>
									&nbsp;&nbsp;<a id="restoreFormUn" href="#" class="autosave_restore">Forget the inputs? Click here...</a>
								</td>
							</tr>
						<%} %>
						<% if (request.getAttribute("errorInfo") != null && ((String)request.getAttribute("errorInfo")).equals("pwdErr")) { %>
							<tr>
								<td colspan="2">
									<p class="error_one"><span>Wrong Password!</span></p>
									&nbsp;&nbsp;<a id="restoreFormPwd" href="#" class="autosave_restore">Forget the inputs? Click here...</a>
								</td>
							</tr>
						<%} %>
						<tr>
							<td></td>
							<td>
								<a id="aLogin" class="btn_login"><span>Login</span></a>
							</td>
						</tr>
					</table>
				</div>
				<input id="f" type="hidden" value="http://localhost:8080/bceserver/bce_user_login.jsp" />
			</div>
			<div class="center_login"></div>
			<div class="right_login">
				<p>
					<img alt="" src="images/pic_text.gif" />
				</p>
				<p class="button">
					<a class="btn_loginr" href="bce_user_register.jsp">
						<span>Register Now</span>
					</a>
				</p>
			</div>
			<div class="btm_bg"></div>
		</div>
		<div style="text-align: center;">
			<script type="text/javascript" src="javascript/publib_footernew.js"></script>
		</div>
	</div>
</form>
</body>
</html>
