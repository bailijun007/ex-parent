
$('.btn-loginout').click(function(){
    layer.confirm('确定退出？', {
	        title : "确定退出？",
	        skin : "my-skin",
	        btn: ['确定','取消'] //按钮
	    }, function(){
	        $.ajax({
	            type: "post",
	            url: appConfig.host + '/admin/api/passport/logout',
	            contentType: "application/json;charset=UTF-8",
	            async: false,
	            //dataType:"json",  // 预期服务器返回的数据类型。如果不指定，jQuery 将自动根据 HTTP 包 MIME 信息来智能判断，比如XML MIME类型就被识别为XML。
	            success: function (data) {
	
	            }
	        });
	        sessionStorage.removeItem("token");
	        sessionStorage.removeItem("x-token");
	        window.location.href = appConfig.host+"/admin/login.html";
	    }
    );

});
