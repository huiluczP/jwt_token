$(function(){
    // 设置点击事件
    $("#login").click(getToken)
});

// ajax获取token，返回为空则提示
function getToken(){
    var username = $("#username").val()
    var password = $("#password").val()
    // ajax获取token信息
    $.ajax({
        type : "post",// 请求方式
        url : "/checkLogin",// 发送请求地址
        data: {
            username: username,
            password: password
        },
        dataType : "text",
        async : false,
        // data为token或空
        success : function(data) {
            console.log('data:' + data)
            // 为空，出错
            if(data == ""){
                $("#tip").html("username or password wrong")
            }else{
                // 将token存入storage，并请求用户页面
                localStorage.setItem("token", data)
                window.location.href = "/userInformation"
            }
        },
        error : function(){
            alert("error")
        }
    });
}