<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Help——Broadcast Encryption Server</title>

<link rel="stylesheet" type="text/css" href="css/style.css" />

</head>
<body>
<form id="regForm" action="RegisterServlet.sl" method="post">
	<div id="topnav" style="height: 16px; padding: 2px 4px; background-color: #fff; border-bottom: solid 1px #ccc;">
		<div class="topnav_left" style="float: left;">
			<a href="http://crypto.stanford.edu/pbc/bce/" target="_blank">About BCE</a>
		</div>
		<div class="topnav_right" style="float: right;">
			<script type="text/javascript"></script>
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
			<dl class="login_question">
				<dt>
					<span class="login">How to Use</span>
				</dt>
				<dd class="question">
					1. Register your account in the <a href="./bce_user_register.jsp">BCE Server Register Page</a>.
				</dd>
				<dd class="question">
					2. <a href="./bce_user_login.jsp">Login</a> into the BCE Server with your account.
				</dd>
				<dd class="question">
					3. If you are the newly registered user, you should apply a new BCE Private Key. If not, you could manage your BCE Private Key.
				</dd>
			</dl>
			<div class="btm_bg"></div>
		</div>
		<div style="text-align: center;">
			<script type="text/javascript" src="javascript/publib_footernew.js"></script>
		</div>
	</div>
</form>
</body>
</html>