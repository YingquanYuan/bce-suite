document.write('\<script type=\"text\/javascript\" src=\"javascript\/jquery-1.7.1.js\"\>\<\/script\>');
document.write("\<script type=\"text\/javascript\" src=\"javascript\/jquery.autosave.js\"\>\<\/script\>");
document.write("\<script type=\"text\/javascript\" src=\"javascript\/jquery.cookie.js\"\>\<\/script\>");

function checkUserName() {
  var userName = $("#un").val().trim();
  if (userName == null || userName == "") {
    var imgStr = "\<img src = \"images\/pic_fail.gif\" style=\"vertical-align:middle\" \/\>";
    $("#error_one").html(imgStr).append(
        "\<span\>User name cannot be null!<\/span\>");
    return;
  }
  $.ajax({
    type : "post",
    url : "RegisterServlet.sl",
    dataType : "json",
    data : "userName=" + userName + "&flag=1",
    success : function(json) {
      var imgStr = "\<img alt=\"" + json.result.toString().trim()
          + "\" src = \"images\/pic_" + json.result.toString().trim()
          + ".gif\" style=\"vertical-align:middle\" \/\>";
      $("#error_one").html(imgStr);
    }
  });
}

function checkEmail(id) {
  str = $("#" + id).val().trim();
  if (str == null || str == "") {
    var img = "\<img alt=\"Please input Email!\" src=\"images\/pic_fail.gif\" style=\"vertical-align:middle\" \/>";
    $("#email_error").html(img).append(
        "\<span\>Please input Email!\<\/span\>");
    return;
  }
  var reg = /^[a-zA-Z]([a-zA-Z0-9]*[-_.]?[a-zA-Z0-9]+)+@([\w-]+\.)+[a-zA-Z]{2,}$/;
  if (!reg.test(str)) {
    var img = "\<img alt=\"Wrong Email!\" src=\"images\/pic_fail.gif\" style=\"vertical-align:middle\" \/>";
    $("#email_error").html(img).append(
        "\<span\>Please input correct Email!\<\/span\>");
    return;
  }
  $.ajax({
    type : "post",
    url : "RegisterServlet.sl",
    dataType : "json",
    data : "email=" + str + "&flag=2",
    success : function(json) {
      var img = "\<img alt=\"" + json.result.toString().trim()
          + "\" src=\"images\/pic_" + json.result.toString().trim()
          + ".gif\" style=\"vertical-align:middle\" \/>";
      if (json.result.toString().trim() == "success")
        $("#email_error").html(img);
      else
        $("#email_error").html(img).append(
            "\<span\>This email has been used!\<\/span\>");
    }
  });
}

function checkSubmit() {
  var flag = 0;
  $("input").each(function() {
    if (this.type == "text")
      if (this.value == "" || this.value == null) {
        alert(this.id + " is null, form cannot submit!");
        flag = 1;
        return;
      }
    if (this.type == "password")
      if (this.value == "" || this.value == null) {
        alert("Password is null, form cannot submit!");
        flag = 1;
        return;
      }
    if (this.type == "checkbox")
      if (!this.checked) {
        alert("Agreement unchecked!");
        flag = 1;
        return;
      }
  });
  if (flag == 0)
    $("#regForm").submit();
}

function checkPwdQty(id) {
  var pwd = $("#" + id).val().trim();
  var level = 0;
  if (pwd.length < 5) {
    $("#pwd-strong").attr("style", "display:none");
    var img = "\<img alt=\"Password too short!\" src=\"images\/pic_fail.gif\" style=\"vertical-align:middle\" \/\>";
    $("#pwd_error").html(img).append(
        "\<span\>Password too short, input a longer one!\<\/span\>");
    return;
  }
  if (pwd.match(/[a-z]/ig))
    level++;
  if (pwd.match(/[0-9]/ig))
    level++;
  if (pwd.match(/(.[^a-z0-9])/ig))
    level++;
  if (pwd.length < 7 && pwd.length >= 5)
    level--;
  switch (level) {
  case 1:
    $("#pwd-strong").attr("class", "pwds" + level).attr("style",
        "display:block");
    var img = "\<img alt=\"Qualified password!\" src=\"images\/pic_success.gif\" style=\"vertical-align:middle\" \/\>";
    $("#pwd_error").html(img);
    break;
  case 2:
    $("#pwd-strong").attr("class", "pwds" + level).attr("style",
        "display:block");
    var img = "\<img alt=\"Qualified password!\" src=\"images\/pic_success.gif\" style=\"vertical-align:middle\" \/\>";
    $("#pwd_error").html(img);
    break;
  case 3:
    $("#pwd-strong").attr("class", "pwds" + level).attr("style",
        "display:block");
    var img = "\<img alt=\"Qualified password!\" src=\"images\/pic_success.gif\" style=\"vertical-align:middle\" \/\>";
    $("#pwd_error").html(img);
    break;
  default:
    $("#pwd-strong").attr("class", "pwds1").attr("style", "display:block");
    var img = "\<img alt=\"Qualified password!\" src=\"images\/pic_success.gif\" style=\"vertical-align:middle\" \/\>";
    $("#pwd_error").html(img);
    break;
  }
}

function checkPwdAccd(prev, cur) {
  var prevPwd = $("#" + prev).val().trim();
  var curPwd = $("#" + cur).val().trim();
  if (prevPwd != curPwd) {
    var img = "\<img alt=\"Different password\" src=\"images\/pic_fail.gif\" style=\"vertical-align:middle\" \/\>";
    $("#pwd2_error").html(img).append(
        "\<span\>Different password, please correct it!\<\/span\>");
    return;
  }
  var img = "<img alt=\"Qualified password!\" src=\"images\/pic_success.gif\" style=\"vertical-align:middle\" />";
  $("#pwd2_error").html(img);
}

$(document).ready(function() {
  $("#un").blur(function() {
    checkUserName();
  });
  $("#em").blur(function() {
    checkEmail("em");
  });
  $("#p1").blur(function() {
    checkPwdQty("p1");
  });
  $("#p2").blur(function() {
    checkPwdAccd("p1", "p2");
  });
  $("#cd").focus(function() {
    $("#vc_code").slideDown("slow");
  });
  // $("#aRecode").click(function() {
  // $("#vcImg").src = "ValidateCodeServlet.sl";
  // });
  $("form *").autosave({
    'interval' : 10000,
    'unique' : 'registerpage',
  });
});
