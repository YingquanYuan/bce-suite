<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>Register——Broadcast Encryption Server</title>

<link rel="stylesheet" type="text/css" href="css/style.css" />

<script language="JavaScript" src="javascript/jquery-1.7.1.js" type="text/javascript"></script>
<script language="JavaScript" src="javascript/myjavascript.register.js" type="text/javascript"></script>
<script language="JavaScript" src="javascript/jquery.autosave.js" type="text/javascript"></script>
<script language="JavaScript" src="javascript/jquery.cookie.js" type="text/javascript"></script>
</head>
<body>
<form id="regForm" action="RegisterServlet.sl" method="post">
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
			<img alt="BCE--Free Register" src="images/logo_login02.png">
		</div>
		<div class="content_login">
			<div class="top_bg"></div>
			<ul class="login_top">
				<li><img src="images/pic_login01.gif" /></li>
				<li><img src="images/pic_login22.gif" /></li>
				<li><img src="images/pic_login33.gif" /></li>
			</ul>
			<div class="login_cont01">
				<h5>BCE Account Info</h5>
				<div class="table">
					<table border="0" width="100%">
						<tbody>
							<tr>
								<td width="150" class="right" valign="top">
									<dfn>*</dfn>User Name:
								</td>
								<td>
									<div class="oneline">
										<input name="un" type="text" id="un" class="inputbox" maxlength="50" />
										<p id="error_one" class="error_one"></p>
									</div>
									<div class="twoline">(5-20digits of alphabet, numbers or underline, the first character must be alphabet.)</div>
								</td>
							</tr>
							<tr>
								<td width="150" class="right" valign="top">
									<dfn>*</dfn>Password:
								</td>
								<td>
									<div class="oneline">
										<input name="p1" id="p1" type="password" class="inputbox" maxlength="50" />
										<ul id="pwd-strong" style="display: none;">
											<li>Weak</li>
											<li>Median</li>
											<li>Strong</li>
										</ul>
										<p id="pwd_error" class="error_one"></p>
									</div>
									<div class="clear"></div>
									<div class="twoline">(For security, we highly recommend you using the composition of character + numbers, and password should exceed 5 digits.)</div>
								</td>
							</tr>
							<tr>
								<td width="150" class="right" valign="top">
									<dfn>*</dfn>Confirm Password:
								</td>
								<td>
									<div class="oneline">
										<input name="p2" id="p2" type="password" class="inputbox" maxlength="50" />
										<p id="pwd2_error" class="error_one"></p>
									</div>
									<div class="twoline">(Ensure the correction of password.)</div>
								</td>
							</tr>
							<tr>
								<td width="150" class="right" valign="top">
									<dfn>*</dfn>Email：
								</td>
								<td>
									<div class="oneline">
										<input name="em" type="text" id="em" class="inputbox" maxlength="100" />
										<p id="email_error" class="error_one"></p>
									</div>
									<div class="twoline">(The confirmation of registration will be sent to your Email Box.)</div>
								</td>
							</tr>
							<tr>
								<td width="150" class="right" valign="top">
									<dfn>*</dfn>Authenticode:
								</td>
								<td>
									<div class="oneline">
										<input name="cd" type="text" id="cd" class="inputbox" maxlength="10" onblur="" />
									</div>
									<div style="clear: both;"></div>
									<div style="color: Red; font-size: 12px;">(While encountering successive input error, please check if cookies are disabled in your explorer.)</div>
								</td>
							</tr>
							<tr id="tr_vc">
								<td width="150" class="right" valign="top"></td>
								<td>
									<div id="vc_code" style="display: none;">
										<img id="vcImg" name="vcImg" alt="Authenticode" align="top" src="ValidateCodeServlet.sl" />
										<a id="aRecode" href="javascript: document.getElementById('vcImg').src='ValidateCodeServlet.sl?'+Math.random();" class="font_gray" >Too vague, change it!</a>
									</div>
								</td>
							</tr>
							<tr>
								<td width="150" class="right" valign="top">
									<dfn>*</dfn>Agreement:
								</td>
								<td>
									<div class="oneline">
										<p class="error_two">
											<input type="checkbox" id="chkTerms" name="chkTerms" value="1" />
											<label for="chkTerms">
												I have read and accepted the agreement of <a href="" class="font_gray14" target="_blank">BCE Privacy Announcement</a>.
											</label>
										</p>
									</div>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
			<div class="login_cont10">
				<div class="table">
					<table border="0">
						<tbody>
							<tr>
								<td width="150" class="right" valign="top"></td>
								<td>
									<input type="hidden" id="flag" name="flag" value="0" />
									<% if (request.getAttribute("errorInfo") != null && ((String)request.getAttribute("errorInfo")).equals("vc_error")) { %>
										<div>
											<p class="error_one"><span>Wrong Authenticode!</span></p>
											&nbsp;&nbsp;<a id="restoreForm" href="#" class="autosave_restore">Forget the inputs? Click here...</a>
										</div>
									<%} %>
									<a href="javascript: checkSubmit();" id="aReg" class="btn_logintwo">
										<span>Submit</span>
									</a>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
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
