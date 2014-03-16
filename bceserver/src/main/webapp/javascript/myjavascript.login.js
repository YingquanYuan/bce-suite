var submited = false;

function checkSubmit() {
	var canSubmit = true;
	if (submited) {
		alert("Your login request is under processing, please do not submit again!");
		return;
	}
	var userName = $("#u").val().trim();
	var password = $("#p").val().trim();
	if (userName == null || userName == "") {
		var img_u = "\<img alt=\"User Name cannot be null!\" src=\"images\/pic_fail.gif\" style=\"vertical-align:middle\" \/\>";
		$("#error_u").html(img_u).append("\<span\>User Name cannot be null!\<\/span\>");
		canSubmit = false;
	}
	if (password == null || password == "") {
		var img_p = "\<img alt=\"Password cannot be null!\" src=\"images\/pic_fail.gif\" style=\"vertical-align:middle\" \/\>";
		$("#error_p").html(img_p).append("\<span\>Password cannot be null!\<\/span\>");
		canSubmit = false;
	}
	if (!canSubmit) {
		submited = false;
		return;
	} else {
		submited = true;
		// setTimeout(function() {
		// $("#loginForm").submit();
		// }, 3000);
		$("#loginForm").submit();
	}
}

function removeErrorMsg(id, idRmvd) {
	var nodeVal = $("#" + id).val().trim();
	if (nodeVal != null && nodeVal != "") {
		// $("#" + idRmvd).slideUp();
		$("#" + idRmvd).empty();
	}
}

$(document).ready(function() {
	$("#aLogin").click(function() {
		checkSubmit();
	});
	$("#u").blur(function() {
		removeErrorMsg("u", "error_u");
	});
	$("#p").blur(function() {
		removeErrorMsg("p", "error_p");
	});
	$("form *").autosave({
		'interval' : 10000,
		'unique' : 'loginpage'
	});
});
