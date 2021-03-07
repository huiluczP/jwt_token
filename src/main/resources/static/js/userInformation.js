$(function(){
    // ajax获取用户信息
    $.ajax({
        type : "post",// 请求方式
        url : "/user/userInfo",// 发送请求地址
        dataType : "json",
        async : false,
        // 将token加入头部
        beforeSend : function(request){
            var token = localStorage.getItem("token")
            if(token == null){
                window.location.href = "/login"
            }else{
                request.setRequestHeader("token", localStorage.getItem("token"))
            }
        },
        // data为用户信息json
        success : function(data) {
            console.log("data:" + data)
            if(data.userId != null){
                $('#user_id').html(data.userId)
            }
            if(data.userName != null){
                $('#user_name').html(data.userName)
            }
            if(data.userPhone != null){
                $('#user_phone').html(data.userPhone)
            }
            if(data.userBirthday != null){
                $('#user_birthday').html(data.userBirthday)
            }
        },
        // 当response的头部有token信息时，进行token的无痛刷新
        complete : function(xhr){
            var token = xhr.getResponseHeader('token')
            console.log("response token:" + token)
            if(token != null){
                localStorage.setItem("token", token)
            }
        },
        error : function(){
            window.location.href = "/login"
        }
    });
});
