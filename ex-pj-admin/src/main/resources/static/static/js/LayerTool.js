$.ajaxSettings.beforeSend = function (xhr, settings) {
    xhr.setRequestHeader("token", sessionStorage.getItem('token'));
}

//这里给所有ajax请求添加一个complete函数
$.ajaxSetup({
    complete : function(xhr, status) {
        //拦截器实现超时跳转到登录页面
        // 通过xhr取得响应头
        var REDIRECT = xhr.getResponseHeader("REDIRECT");
        //如果响应头中包含 REDIRECT 则说明是拦截器返回的
        if (REDIRECT == "REDIRECT")
        {
            var win = window;
            while (win != win.top)
            {
                win = win.top;
            }
            //重新跳转到 login.html
            win.location.href = xhr.getResponseHeader("CONTEXTPATH");
        }
    }
});

function removePageSize()
{
    $(".page-number").remove();
    $(".page-first").remove();
    $(".page-last").remove()
    $(".page-pre a").text("上一页")
    $(".page-next a").text("下一页")
}

/*
	参数解释：
	title	标题
	url		请求的url
	id		需要操作的数据id
	w		弹出层宽度（缺省调默认值）
	h		弹出层高度（缺省调默认值）
*/

function layer_show(title, url, w, h) {
   var index= layer.open({
        type: 2,
        title: title,
        shadeClose: false,
        shade: 0.5,
        maxmin: true, //开启最大化最小化按钮
        area: ['' + w + 'px', '' + h + 'px'],
        content: url
   });
   //layer.iframeAuto(index);
   return index;
}

function layer_show_auto(title, url, w, h)
{
    var index = layer.open({
        type: 2,
        title: title,
        shadeClose: false,
        shade: 0.5,
        maxmin: true, //开启最大化最小化按钮
        area: [w, h],
        content: url
    });
    layer.iframeAuto(index);
    return index;
}

function layer_showfull(title, url, w, h) {
    var index=layer.open({
        type: 2,
        title: title,
        shadeClose: false,
        shade: 0.5,
        maxmin: true, //开启最大化最小化按钮
        area: ['' + w + 'px', '' + h + 'px'],
        content: url
    });
    layer.full(index);
    return index;
}
function layer_showfull_refresh(title, url,tbname) {
    var index=layer.open({
        type: 2,
        title: title,
        shadeClose: false,
        shade: 0.5,
        maxmin: true, //开启最大化最小化按钮
        area: ['1000px', '500px'],
        content: url,
        success: function (layero, index) {

        },
        end: function () {
            $('#tbname').bootstrapTable('destroy');
            oTable.Init();
        }
    });
    layer.full(index);
    return index;
}


function layer_showMap(title, url, w, h, fal) {
    var index = layer.open({
        type: 2,
        title: title,
        shadeClose: false,
        shade: 0.5,
        area: ['' + w + 'px', '' + h + 'px'],
        maxmin: fal, //开启最大化最小化按钮
        content: url
    });
    layer.full(index);
    return index;
}

//执行回传无复选框确认函数(触发按钮的点击事件)
function layer_ProblemConFirm(title,url, objmsg) {
    layer.confirm(objmsg,
        { icon: 3 },
        function (index) {
            layer_showfull(title, url, '800', '500');
        });
    return false;
}

function GetProblemCount(Url, ProblemUrl)
{
    $.ajax({
        type: 'POST',
        url: url,
        dataType: 'json',
        success: function (data) {
            var count = data;
            if (count > 0)
            { 
                layer_ProblemConFirm("待接收", ProblemUrl, "您有"+count+"信息待接收，是否前往接收。")
            }
        },
        error: function (data) {
            console.log(data.msg);
        },
    })
}

//执行回传无复选框确认函数(触发按钮的点击事件)
function layer_PostBackConFirm(objId, objmsg) {

    var msg = "删除记录后不可恢复，您确定吗？";
    if (arguments.length == 2) {
        msg = objmsg;
    }
    layer.confirm(msg,
        { icon: 3},
        function (index) {
            __doPostBack(objId, '');
    });
    return false;
}
/*确认-删除*/
function del_confirm(obj, url, msg, msgtype) {
    if (arguments.length == 2) {
        msg = "确认要删除吗？";
        msgtype = "已删除";
    }
    layer.confirm(msg, { icon: 3 }, function (index) {
        $.ajax({
            type: 'POST',
            url: url,
            dataType: 'json',
            success: function (data) {
                $("button[name='refresh']").click();
                layer.msg(msgtype, { icon: 1, time: 2000 });
            },
            error: function (data) {
                console.log(data.msg);
            },
        });
    });
}
/*全选-删除*/
function del_ck_confirm(obj, url, msg, msgtype) {
    if ($("#" + obj + " input:checked").size() < 1) {
        layer.alert('对不起，请选中您要操作的记录！', {
            icon: 0,
            shade: 0.5,
            shadeClose: false
        })
        return;
    }
    var ids = "";
    var tps = "";
    $("#" + obj + " tbody input:checked").each(function () {
        ids += $(this).parents("tr").attr("data-uniqueid") + ",";
       
    });

    if (ids.length <= 0) return;
    ids = ids.substring(0, ids.length - 1);   
    if (arguments.length == 2) {
        msg = "确认要删除吗？";
        msgtype = "已删除";
    }
    layer.confirm(msg, { icon: 3 }, function (index) {
        $.ajax({
            type: 'POST',
            url: url + "?ids=" + ids + "",
            dataType: 'json',
            success: function (data) {
                $("button[name='refresh']").click();
                layer.msg(msgtype, { icon: 1, time: 2000 });
            },
            error: function (data) {
                console.log(data.msg);
            },
        });
    });
   
}


/*全选-删除*/
function enable_ck_confirm(obj, url,status, msg, msgtype) {
    if ($("#" + obj + " input:checked").size() < 1) {
        layer.alert('对不起，请选中您要操作的记录！', {
            icon: 0,
            shade: 0.5,
            shadeClose: false
        })
        return;
    }
    var ids = "";
    var tps = "";
    $("#" + obj + " tbody input:checked").each(function () {
        ids += $(this).parents("tr").attr("data-uniqueid") + ",";
    });

    if (ids.length <= 0) return;
    ids = ids.substring(0, ids.length - 1);
    if (status == 1) {
        msg = "确认要启用吗？";
        msgtype = "已启用";
    }else if (status == 0) {
        msg = "确认要禁用吗？";
        msgtype = "已禁用";
    }
    url +="?status="+status;
    url +="&ids="+ids;
    layer.confirm(msg, { icon: 3 }, function (index) {
        $.ajax({
            type: 'POST',
            url: url ,
            dataType: 'json',
            success: function (data) {
                $("button[name='refresh']").click();
                layer.msg(msgtype, { icon: 1, time: 2000 });
            },
            error: function (data) {
                console.log(data.msg);
            },
        });
    });

}
 
//导入
var ImportEvent = function (prex, url) {
    parent.layer.open({
        type: 2,
        title: '文件导入',
        shadeClose: true,
        shade: 0.8,
        area: ['650px', '70%'],
        content: prex + 'FileImport.aspx?url=' + prex + url,
        success: function (layero, index) {
        },
        end: function () {
            $('#tb_departments').bootstrapTable('destroy');
            oTable.Init();
        }
    });
}


//导入
var ImportEventByRoot = function (prex,url, exParam) {
    var exUrl = "";
    if (!!exParam) {
        exUrl = exParam;
    }
    parent.layer.open({
        type: 2,
        title: '文件导入',
        shadeClose: true,
        shade: 0.8,
        area: ['650px', '70%'],
        content: prex+'/HaiOuFileImport.aspx?url=' + url+exUrl,
        success: function (layero, index) {
        },
        end: function () {
            $('#tb_departments').bootstrapTable('destroy');
            oTable.Init();
        }
    });
}

/*导出*/
function ExportData(url,ID) {
    var ExportCheck = $(".dropdown-menu input:checked");

    if (ID != undefined)
    {
        ExportCheck = $("#" + ID + " .dropdown-menu input:checked")
    }

    console.log(ExportCheck.html());


    var index = layer.msg('处理中，请稍候...', {
        icon: 16,
        shade: 0.01,
        time: 1000000
    });
    setTimeout(function () {
        layer.close(index);
        var FiledArr = new Array();
        ExportCheck.each(function () {
            if ($(this).attr("data-field") != "#") {
                var FiledInfo = new Object();
                FiledInfo.Descption = $(this).parent().text();
                FiledInfo.FieldName = $(this).attr("data-field");
                FiledArr.push(FiledInfo);
            }
        })
        if (FiledArr.length > 0) {
            $.ajax({
                type: 'POST',
                url: url + '&FiledArr=' + JSON.stringify(FiledArr),
                dataType: 'json',
                success: function (data) {
                    if (data.ExportCode == "Error")
                        layer.msg(data.ExportMsg, { icon: 2, time: 2000 });
                    else {
                        layer.alert('<a id="exportdwn" href="' + data.ExportMsg + '" title="点击下载">导出成功，点击确定按钮下载</a>', {
                            icon: 1
                        }, function (alindex) {
                            document.getElementById("exportdwn").click();
                            layer.close(alindex);
                        });
                    }

                },
                error: function (data) {
                    console.log(data.msg);

                },
            });
        }
       

    }, 1500);
   
}
function SaveSort(url) {
    if ($("input[name='SortText']").size() <= 0) return;
    var reg = /^\d+(\.{0,1}\d+){0,1}$/;
    var SortArr = new Array();
    $("input[name='SortText']").each(function () {
        var idval = $(this).val();
        if (idval == "" || idval == null)
            idval = "0";
        var SortItem = new Object();
        if (reg.test(idval) && reg.test($(this).parents("tr").attr("data-uniqueid"))) {
            SortItem.id =  parseInt($(this).parents("tr").attr("data-uniqueid"));
            SortItem.SortNum = parseInt(idval);
            SortArr.push(SortItem);
        }
    })
    if (SortArr.length <= 0) return;
    $.ajax({
        type: 'POST',
        url: url + "&SortArr=" + JSON.stringify(SortArr),
        dataType: 'json',
        success: function (data) {
            $("button[name='refresh']").click();
            layer.msg("保存成功", { icon: 1, time: 2000 });
        },
        error: function (data) {
            console.log(data.msg);
        },
    });
}
function CloseLayer() {
    var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
    parent.layer.close(index);
}
//截取字符
function strSub(str, strfiter, len) {
    if (str.length > len) {
        return str.substr(0, len) + strfiter;
    }
    return str;
}
//获取url中的参数
function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);  //获取url中"?"符后的字符串并正则匹配
    var context = "";
    if (r != null)
        context = r[2];
    reg = null;
    r = null;
    return context == null || context == "" || context == "undefined" ? "" : context;
}
//打印link样式文件,打印内容html
function PrintContent(link, content,style) {
    var linkstr = '';
    $.each(link, function (i, v) {
        var rel = v.rel;
        if (rel == 'stylesheet') { linkstr += '<link href="' + v.href + '" rel="stylesheet">'; }
    });

    var ifr = document.getElementById("printIframe");
    if (ifr == undefined) {
        ifr = document.createElement("iframe");
    }
    ifr.setAttribute("id", "printIframe");
    ifr.setAttribute("style", "width:100%;display:none;");
    document.body.appendChild(ifr);

    var ifrdoc = ifr.contentWindow.document;

    var html = "<head>" + linkstr + "<script src='../../js/jquery.min.js?v=1'><\/script><style>" + style + "</style></head><BODY>" + content + "</BODY><script src='../../js/printHtml.js?v=5'><\/script>";

    ifrdoc.open();
    ifrdoc.write(html);
    /*ifrdoc.write("<BODY>");
    ifrdoc.write('<style>' + style + '</style>');
    ifrdoc.write(linkstr);
    ifrdoc.write("<script src='../../js/jquery.min.js?v=1'><\/script>");
    ifrdoc.write(content);
    ifrdoc.write("</BODY>");
    ifrdoc.write("<script src='../../js/printHtml.js?v=1'><\/script>");*/
    ifrdoc.close();

    //document.removeChild(ifr);
}
//获取选中行的唯一码(主键)
function GetCheckedRowUnicode(id) {
    var ids = new Array();
    $("#" + id + " tbody input:checked").each(function () {
        ids.push($(this).parents("tr").attr("data-uniqueid") + ",");
    });
    return ids;
}

function removeHTMLTag(str) {
    str = str.replace(/<\/?[^>]*>/g, ''); //去除HTML tag
    str = str.replace(/[ | ]*\n/g, '\n'); //去除行尾空白
    //str = str.replace(/\n[\s| | ]*\r/g,'\n'); //去除多余空行
    str = str.replace(/ /ig, '');//去掉 
    str = str.replace(/&nbsp;/ig, '');
    return str;
}
//表格初始化参数
function initTableParam(url, params, columns,isshowpage) {
    var ispage = true;
    if (isshowpage == false) { ispage = false;}
    return {
        url: url,         //请求后台的URL（*）
        method: 'get',                      //请求方式（*）
        toolbar: '#toolbar',                //工具按钮用哪个容器
        striped: true,                      //是否显示行间隔色
        cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        pagination: ispage,                   //是否显示分页（*）
        sortable: true,                     //是否启用排序
        sortOrder: "asc",                   //排序方式
        queryParams: params,//传递参数（*）
        sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
        pageNumber: 1,                       //初始化加载第一页，默认第一页
        pageSize: 10,                       //每页的记录行数（*）
        pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
        search: false,                       //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
        strictSearch: true,
        showColumns: true,                  //是否显示所有的列
        showRefresh: true,                  //是否显示刷新按钮
        minimumCountColumns: 2,             //最少允许的列数
        clickToSelect: false,               //是否启用点击选中行
        singleSelect: false,
        height: 500,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
        uniqueId: "id",                     //每一行的唯一标识，一般为主键列
        //showToggle: true,                    //是否显示详细视图和列表视图的切换按钮
        cardView: false,                    //是否显示详细视图
        detailView: false,                   //是否显示父子表
        columns: columns
    }
}

var dicTypeList;
function bindDrop(type,id)
{
    $.ajax({
        type: "get",
        url: "/v1/http/dic/query_type_list?dic_type_key="+type,
        contentType: "application/json;charset=UTF-8",  //发送信息至服务器时内容编码类型。
        async: false,
        //dataType:"json",  // 预期服务器返回的数据类型。如果不指定，jQuery 将自动根据 HTTP 包 MIME 信息来智能判断，比如XML MIME类型就被识别为XML。
        //data:JSON.stringify({id:id}),
        success: function (data) {
            //console.log(data);
            var list= data.data.dic_list;
            dicTypeList=list;
            if (list != null) {
                var html="<option value=''>请选择</option>";
                for(var i=0;i<list.length;i++)
                {
                    html+="<option value='"+list[i].key+"'>"+list[i].value+"</option>"
                }
                $("#"+id+"").html(html);
            }
        }
    })
}

function time(time) {
    var date = new Date(time+1000*60*60*8);
    return date.toJSON().substr(0, 19).replace('T', ' ');//.replace(/-/g, '.')
}
function getShortDate(time) {
    var date = new Date(time+1000*60*60*8);
    return date.toJSON().substr(0, 10).replace('T', ' ');//.replace(/-/g, '.')
}
/*
         去掉double类型小数点后面多余的0
         参数：old 要处理的字符串或double
         返回值：newStr 没有多余零的小数或字符串
         例： cutZero(123.000) -> 123
             cutZero(123.0001) -> 123.0001
             cutZero(10203000.0101000) -> 10203000.0101
             cutZero(10203000) -> 10203000
     */
function cutZero(old){
    //拷贝一份 返回去掉零的新串
    var newstr=old;
    //循环变量 小数部分长度
    var leng = old.length-old.indexOf(".")-1
    //判断是否有效数
    if(old.indexOf(".")>-1){
        //循环小数部分
        for(i=leng;i>0;i--){
            //如果newstr末尾有0
            if(newstr.lastIndexOf("0")>-1 && newstr.substr(newstr.length-1,1)==0){
                var k = newstr.lastIndexOf("0");
                //如果小数点后只有一个0 去掉小数点
                if(newstr.charAt(k-1)=="."){
                    return  newstr.substring(0,k-1);
                }else{
                    //否则 去掉一个0
                    newstr=newstr.substring(0,k);
                }
            }else{
                //如果末尾没有0
                return newstr;
            }
        }
    }
    return old;
}