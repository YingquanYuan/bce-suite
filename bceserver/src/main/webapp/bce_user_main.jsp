<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%
	if (request.getAttribute("dataList") == null) {
		response.sendRedirect("MainPageServlet.sl");
		return;
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Main——Broadcast Encryption Server</title>

<link rel="stylesheet" type="text/css" href="css/style.css" />
<link rel="stylesheet" type="text/css" href="css/jquery-ui-1.8.18.custom.css" />

<script language="JavaScript" src="javascript/jquery-1.7.1.js" type="text/javascript"></script>
<script language="JavaScript" src="javascript/jquery.autosave.js" type="text/javascript"></script>
<script language="JavaScript" src="javascript/jquery.cookie.js" type="text/javascript"></script>
<script language="JavaScript" src="javascript/myjavascript.mainpage.js" type="text/javascript"></script>
<script language="JavaScript" src="javascript/jquery-ui-1.8.18.custom.min.js" type="text/javascript"></script>
<!-- <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script> -->

</head>
<body>
<form id="mainForm" action="" method="post">
	<div id="topnav" style="height: 16px; padding: 2px 4px; background-color: #fff; border-bottom: solid 1px #ccc;">
		<div class="topnav_left" style="float: left;">
			<a href="http://crypto.stanford.edu/pbc/bce/" target="_blank">About BCE</a>&nbsp;|
			<a style="color: red;">Welcome to BCE Server, ${sessionScope.loginedUser.userName }</a>
		</div>
		<div class="topnav_right" style="float: right;">
			|<a id="logout" onclick="javascript: return clearCookies('JSESSIONID');" href="#">Logout</a>
			|<a href="./bce_user_login.jsp">Login</a>
			|<a href="./bce_user_register.jsp">Free Register</a>
			|<a href="./bce_user_help.jsp">Help</a>|
		</div>
		<div style="clear: both;"></div>
	</div>
	<div class="full">
		<div class="logo_login02">
			<img alt="" src="images/logo_login02.png" />
		</div>
		<div class="content_login">
			<div class="top_bg"></div>
			<ul class="login_top">
				<li><img src="images/pic_login11.gif" /></li>
				<li><img src="images/pic_login02.gif" /></li>
				<li><img src="images/pic_login33.gif" /></li>
			</ul>
			<div class="login_cont01">
				<h5>Apply your BCE Private Key</h5>
				<div class="table">
					<table border="0" width="100%">
						<tbody>
							<tr>
								<td width="50%" valign="top" style="text-align: center;">
									<a href="#" id="aApply" class="btn_logintwo">
										<span>Apply</span>
									</a>
								</td>
								<td width="50%" valign="top" style="text-align: center;">
									<a onclick="javascript: return checkDownload();" href="#" id="aDownload" class="btn_logintwo">
										<span>Download</span>
									</a>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
				<h5>Manage your BCE Private Key</h5>
				<div class="table">
					<table id="dataTable" border="1" bordercolor="pink" width="100%">
						<thead>
							<tr>
								<td></td>
								<td>Private Key ID</td>
								<td>BCE System ID</td>
								<td>User Name</td>
								<td>Is Legal</td>
							</tr>
						</thead>
						<c:set var="dataList" value="${requestScope.dataList}" />
						<tbody>
							<c:forEach var="privateKeyData" items="${dataList}">
								<tr>
									<td><input name="radio" id="radio_${privateKeyData.privateKeyId}" value="${privateKeyData.privateKeyId}" type="radio" /></td>
									<td>${privateKeyData.privateKeyId}</td>
									<td>${privateKeyData.belongedBCESystem.bceSystemId}&nbsp;&nbsp;&nbsp;<a id="${privateKeyData.belongedBCESystem.bceSystemId}" href="MainPageServlet.sl?flag=4&sysId=${privateKeyData.belongedBCESystem.bceSystemId}">download param file</a></td>
									<td>${privateKeyData.belongedUser.userName}</td>
									<td>${privateKeyData.isLegal == 1? 'Yes' : 'No'}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
			<div id="dialog" title="BCE Server - Apply New Key">
				<table>
					<tr>
						<td width="150" class="right" valign="top" style="color: orange;">
							<dfn>*</dfn>Your Password:
						</td>
						<td>
							<div class="oneline">
								<input name="p1" id="p1" type="password" class="inputbox" maxlength="50" />
								<p id="pwd_error" class="error_one"></p>
							</div>
							<div class="clear"></div>
						</td>
					</tr>
					<tr>
						<td width="150" class="right" valign="top" style="color: orange;">
							<dfn>*</dfn>Authenticode:
						</td>
						<td>
							<div class="oneline">
								<input name="cd" type="text" id="cd" class="inputbox" maxlength="10" />
							</div>
							<div style="clear: both;"></div>
						</td>
					</tr>
					<tr id="tr_vc">
						<td width="150" class="right" valign="top"></td>
						<td>
							<div id="vc_code" style="display: none;">
								<img id="vcImg" name="vcImg" alt="Authenticode" align="top" src="" />
								<a id="aRecode" href="javascript: document.getElementById('vcImg').src='ValidateCodeServlet.sl?'+Math.random(); return false;" class="font_gray" >change one</a>
							</div>
						</td>
					</tr>
				</table>
				<div id="errMsg" style="color: red;"></div>
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