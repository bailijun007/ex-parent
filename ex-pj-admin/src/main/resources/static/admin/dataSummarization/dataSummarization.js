$(function () {

    // config
    var userCtx = {
        // URL_PAGE_QUERY : "/admin/user/list",
        URL_ASSET_LIST: "/admin/dataSummarization/query",
        // URL_SAVE : "/admin/user/save",
        // URL_BATCH_ENABLE : "/admin/user/enable/batch"
    };

    BeanUtil.setPrefix(userCtx, appConfig.host);

    var url = userCtx.URL_ASSET_LIST;


    //1.初始化加载
    queryDataSummarization();

    $('#btn_query').click(search);

    function search(){
        var param = "?";
        param += "&asset=" + $("#asset").val();
        param += "&symbol=" + $("#symbol").val();
        param += "&begin_time=" + $("#beginTime").val();
        param += "&end_time=" + $("#endTime").val();
        $.ajax({
            type: "get",
            url: url + param,
            contentType: "application/json;charset=UTF-8",  //发送信息至服务器时内容编码类型。
            async: false,
            //dataType:"json",  // 预期服务器返回的数据类型。如果不指定，jQuery 将自动根据 HTTP 包 MIME 信息来智能判断，比如XML MIME类型就被识别为XML。
            //data:JSON.stringify({id:id}),
            success: function (data) {
                $("#user_register_count").text(data.data.userRegisterCount);
                $("#every_4_hours_user_trade_number").text(data.data.every4HoursUserTradeNumber);
                $("#current_user_hold_number").text(data.data.currentUserHoldNumber);
                $("#today_user_trade_number").text(data.data.todayUserTradeNumber);
            }
        })
    }

    function queryDataSummarization() {
        var param = "?";
        param += "&asset=" + $("#asset").val();
        param += "&symbol=" + $("#symbol").val();
        param += "&begin_time=" + $("#beginTime").val();
        param += "&end_time=" + $("#endTime").val();
        $.ajax({
            type: "get",
            url: url + param,
            contentType: "application/json;charset=UTF-8",  //发送信息至服务器时内容编码类型。
            async: false,
            //dataType:"json",  // 预期服务器返回的数据类型。如果不指定，jQuery 将自动根据 HTTP 包 MIME 信息来智能判断，比如XML MIME类型就被识别为XML。
            //data:JSON.stringify({id:id}),
            success: function (data) {
                $("#user_register_count").text(data.data.userRegisterCount);
                $("#every_4_hours_user_trade_number").text(data.data.every4HoursUserTradeNumber);
                $("#current_user_hold_number").text(data.data.currentUserHoldNumber);
                $("#today_user_trade_number").text(data.data.todayUserTradeNumber);
            }
        })
    }
});

