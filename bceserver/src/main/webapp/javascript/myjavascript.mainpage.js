document.write('\<script type=\"text\/javascript\" src=\"javascript\/jquery-1.7.1.js\"\>\<\/script\>');
document.write('\<script type=\"text\/javascript\" src=\"javascript\/jquery.cookie.js\"\>\<\/script\>');

function clearCookies(name) {
	$.cookie(name, null);
	return true;
}

function doApply() {
	
	$.ajax({
		type : "post",
		url : "MainPageServlet.sl",
		dataType : "json",
		data : "flag=2",
		success : function(json) {
			var newTr = "<tr><td><input name='radio' id='check_" + json.pkId.toString().trim() + "' value='" + json.pkId.toString().trim() + "' type='radio' /></td>" +
			"<td>" + json.pkId.toString().trim() + "</td>" +
			"<td>" + json.sysId.toString().trim() + "</td>" +
			"<td>" + json.uname.toString().trim() + "</td>" +
			"<td>" + json.legal.toString().trim() + "</td></tr>";
			$("#dataTable>tbody").append(newTr);
		}
	});
}

function checkAll() {
	if ($("#checkAll").attr("checked")) {
		$("input[name='check']").each(function() {
	        $(this).attr("checked", true);
	    });
	} else {
		$("input[name='check']").each(function() {
	        $(this).attr("checked", false);
	    });
	}
}

function checkDownload_bak() {
	var checkList = [];
	$("input[name='check']").each(function() {
        if ($(this).attr("checked")) {
        	checkList.push($(this).attr("id"));
        }
    });
//	for (var i = 0; i < checkList.length; i++) {
//		alert(checkList[i]);
//	}
	if (checkList.length < 1) {
		alert("You choose no private key!");
		return;
	}
}

function checkDownload() {
	var item = $(":radio:checked");
	// 判断是否选中
	if(item.length == 0) {
		alert("You choose no private key!");
		return false;
	}
	return true;
//	alert(item.val().split("_")[1]);
	
//	$.ajax({
//		type : "post",
//		url : "MainPageServlet.sl",
//		dataType : "json",
//		data : "flag=3&pkId=" + item.val().split("_")[1],
//		success : function(json) {
//			alert("success");
//		}
//	});
	
//	else {
//		var value = item.val();
////		var value = $("input[name='radio'][type='radio']:checked").val();//获得选中项的值
//		alert(value);
//	}
}

function downloadParams(sysId) {
	alert(sysId);
	$.ajax({
		type : "post",
		url : "MainPageServlet.sl",
		dataType : "json",
		data : "flag=4&sysId=" + sysId,
		success : function(json) {
			
		}
	});
}

$(document).ready(function() {
	$("#logout").attr("href", "MainPageServlet.sl?flag=1");
	$("#aApply").click(function() {
		$("#dialog").dialog("open");
	});
	$("input[name='radio']").each(function() {
        $(this).click(function() {
        	if ($(this).attr("checked")) {
        		$("#aDownload").attr("href", "MainPageServlet.sl?flag=3&pkId=" + $(this).val());
        	}
        });
    });
	$("#cd").focus(function() {
		$("#vc_code").slideDown("slow");
	});
	$("#dialog").dialog({ 
		autoOpen: false, 
		modal: true, 
		position: "center", 
		width: 350, 
		open: function(event, ui) {
			$("#vcImg").attr("src", "ValidateCodeServlet.sl?" + Math.random());
		},
		beforeClose: function(event, ui) {
			$("#vc_code").slideUp("fast");
			$("#errMsg").html("");
			$("#p1").attr("value", "");
			$("#cd").attr("value", "");
		},
		buttons: {
			"Submit": function() {
				var password = $("#p1").val();
				var vcode = $("#cd").val().trim();
				if (password == "") {
					$("#errMsg").html("password cannot be null!");
					return;
				}
				if (vcode == "") {
					$("#errMsg").html("vcode cannot be null!");
					return;
				}
				$.ajax({
					type : "post",
					url : "MainPageServlet.sl",
					dataType : "json",
					data : "flag=2&pwd=" + password + "&vc=" + vcode,
					success : function(json) {
						var result = json.result.toString().trim();
						if (result == "fail") {
							$("#errMsg").html(json.reason.toString());
						} else if (result == "success") {
							var newTr = "<tr><td><input name='radio' id='check_" + json.pkId.toString().trim() + "' value='" + json.pkId.toString().trim() + "' type='radio' /></td>" +
							"<td>" + json.pkId.toString().trim() + "</td>" +
							"<td>" + json.sysId.toString().trim() + "&nbsp;&nbsp;&nbsp;<a id='" + json.sysId.toString().trim() + "' href='MainPageServlet.sl?flag=4&sysId=" + json.sysId.toString().trim() + "'>download param file</a>" + "</td>" +
							"<td>" + json.uname.toString().trim() + "</td>" +
							"<td>" + json.legal.toString().trim() + "</td></tr>";
							$("#dataTable>tbody").append(newTr);
							$("input[name='radio']").each(function() {
						        $(this).click(function() {
						        	if ($(this).attr("checked")) {
						        		$("#aDownload").attr("href", "MainPageServlet.sl?flag=3&pkId=" + $(this).val());
						        	}
						        });
						    });
							$("#dialog").dialog("close");
						}
					}
				});
			},
			"Cancel": function() {
				$(this).dialog("close");
			}
		}
	});
});
