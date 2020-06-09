//wjg
//jqGrid的配置信息
if($.jgrid){
	$.jgrid.defaults.width = 1000;
	$.jgrid.defaults.responsive = true;
	$.jgrid.defaults.styleUI = 'Bootstrap';
}

//common
var StringUtil = {};

StringUtil.getUrlParamStr=function(map){
	var params = '';
	for(var name in map){
		params += name+'='+map[name]+'&';
	}
	if(params.length>0){
		return params.substr(0, params.length-1);
	}else{
		return params;
	}
}

var BeanUtil = {};

BeanUtil.listToMap = function(list, uniqueKey){
	var map = {};
	for(var i=0; i<list.length; i++){
		var e = list[i];
		var uniqueVal = e[uniqueKey];
		if(map[uniqueVal]){
			throw '属性不唯一！'+uniqueKey+'='+uniqueVal;
		}
		map[uniqueVal] = e;
	}
	
	return map;
}

BeanUtil.copyProperties = function(source, target){
	if(!source||!target){
		return;
	}
	for(name in source){
		target[name] = source[name];
	}
}

BeanUtil.mergeProperties = function(source, target){
	for(name in source){
		if(!target[name]){
			target[name] = source[name];
		}
	}
}

BeanUtil.getPropertyList = function(list, pname){
	var plist = [];
	for(var i=0;i<list.length;i++){
		plist.push(list[i][pname]);
	}
	return plist;
}

BeanUtil.setPrefix = function(bean, prefix){
	for(name in bean){
		bean[name] = prefix+bean[name];
	}
}

var AppUtil = {};

AppUtil.confirm = function (title, fun){
	var ok = window.confirm(title);
	if(ok){
		fun();
	}
};

// 错误处理
function handleError(result) {
	if (result.code==-5010) {
		alert('登陆超时!');
		top.location.href ='/admin/login.html';
	} else {
		alert(result.message);
	}
}

function ajaxPost(url, data, fun) {
	$.ajax({
		type : "POST",
		url : url,
		crossDomain: true,
        xhrFields: {
            withCredentials: true
        },
		headers : {'Authorization': ''},
		contentType : "application/json; charset=utf-8",
		data : JSON.stringify(data||{}),
		dataType : "json",
		success : function (result) {
			if (result.code==0) {
				fun(result.data);
			} else {
				handleError(result);
			}
		},
		error:function(rs){
			console.log(rs);
		}
	});
}

function ajaxGet(url, data, fun) {
	if(data){
		if(url.indexOf('?')==-1){
			url += '?';
		}else{
			url += '&';
		}
		url += StringUtil.getUrlParamStr(data);
	}
	$.ajax({
		type : "GET",
		url : url,
        xhrFields: {
            withCredentials: true
        },
		headers : {'Authorization': ''},
//        beforeSend: function(xhr) {
//            xhr.setRequestHeader('Authorization', '111');
//        },
		//contentType : "application/json; charset=utf-8",
		//dataType : "json",
		success : function (result) {
			if (result.code==0) {
				fun(result.data);
			}else{
				handleError(result)
			}
		},
		error:function(rs){
			console.log(rs);
		}
	});
}


function ajaxRequest(url, data, fun, method){
	if(method=='POST'){
		ajaxPost(url, data, fun);
	}else{
		ajaxGet(url, data, fun);
	}
}

//工具集合Tools
window.T = {};

// 获取请求参数
// 使用示例
// location.href = http://localhost:8080/index.html?id=123
// T.p('id') --> 123;
var urlParam = function(name) {
	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if(r!=null)return  unescape(r[2]); return null;
};
T.p = urlParam;

//全局配置
$.ajaxSetup({
	dataType: "json",
	contentType: "application/json",
	cache: false
});

function add0(m) {
	return m < 10 ? '0' + m : m
}

function formatDateTime(value, options, row) {
	var time = new Date(value);
	var y = time.getFullYear();
	var m = time.getMonth() + 1;
	var d = time.getDate();
	var h = time.getHours();
	var mm = time.getMinutes();
	var s = time.getSeconds();
	return y + '-' + add0(m) + '-' + add0(d) + ' ' + add0(h) + ':' + add0(mm) + ':' + add0(s);
}

function formatDate(value, options, index) {
	var time = new Date(value);
	var y = time.getFullYear();
	var m = time.getMonth() + 1;
	var d = time.getDate();
	var h = time.getHours();
	var mm = time.getMinutes();
	var s = time.getSeconds();
	return y + '-' + add0(m) + '-' + add0(d);
	return unixTimestamp.toLocaleString();
}

function listToMap(list, uniqueKey){
	var map = {};
	for(var i=0; i<list.length; i++){
		var e = list[i];
		var uniqueVal = e[uniqueKey];
		if(map[uniqueVal]){
			throw '属性不唯一！'+uniqueKey+'='+uniqueVal;
		}
		map[uniqueVal] = e;
	}
	return map;
}

function form2json(formId) {
    var paramArray = $('#' + formId).serializeArray();  
     var jsonObj={};
      $(paramArray).each(function(){
          jsonObj[this.name] = this.value;
      });
     return jsonObj;
}

function json2form(jsonObj, formId) {
	if(!jsonObj || !formId){
		return;
	}
	var form = $('#' + formId);
	var paramArray = form.serializeArray();  
	$(paramArray).each(function(a){
		var name = this.name;
		var val = jsonObj[name];
		var input = form.find('[name='+name+']');
		input.val(val);
	});
}

function json2form2(jsonObj, formId) {
	if(!jsonObj || !formId){
		return;
	}
	var form = document.getElementById(formId);
	var els = form.elements;
	for(var i=0;i<els.length;i++){
		var el = els[i];
		var name = el.name;
		var val = jsonObj[el.name];
		el.value = val;
	}
}

function toKvPair(map){
	var list = [];
	for(k in map){
		list.push({name:k, value:map[k]});
	}
	return list;
}

if(typeof(Dialog)=='undefined'){
	Dialog = {};
}

//依赖layer.js,jquery.js
Dialog.open = function(viewElement, title, width, buttons){
	this.layer = layer||layui.layer;
	var opts = {
	  	type:1,
	  	title:title,
	  	area:width+'px',
	  	content : $(viewElement),
        shadeClose: true,
        shade: 0.8
	};

	this.setButtons(opts, buttons);
	  
	this.index = this.layer.open(opts);
}

Dialog.openUrl = function(url, title, width, buttons){
	this.layer = layer||layui.layer;
	var opts = {
	  	type:2,
	  	title:title,
	  	area:[width+'px', '65%'],
	  	content : url,
        shadeClose: true,
        shade: 0.8
	};

	this.setButtons(opts, buttons);
	  
	this.index = this.layer.open(opts);
}

Dialog.close = function(){
	if(this.layer){
		this.layer.close(this.index);
	}
}

Dialog.setButtons = function(opts, buttons){
	if(!buttons){
		return;
	}
	var btnNames = [];
	var i = 1;
	for(var name in buttons){
		var evtFun = buttons[name];
		btnNames.push(name);
		opts['btn'+(i++)] = evtFun;
	}
	opts.btn = btnNames;
}

var gridFormat = {};

gridFormat.text = function (textMap, cssMap){
	return function(value, options, row){
		var text = null;
		if(textMap){
			text = textMap[value];
		}
		var clz = '';
		if(cssMap){
			clz = ' class="'+cssMap[value]||''+'"';
		}
		if(!text){
			text = value;
		}
		text = text||'';
		return '<span'+clz+'>'+text+'</span>';
	}
}

gridFormat.enum = function(nameMap){
	return gridFormat.text(nameMap, null);
}

gridFormat.date = function(format){
	return function(value, options, row){
		var ds = value;
		if(/^\d+$/.test(value)){
			ds = StringUtil.getFormatDate2(new Date(value),format);
		}
		return '<span >'+ds+'</span>';
	}
}

function textFmt(textMap, cssMap){
	return gridFormat.text(textMap, cssMap);
}

function enumFmt(nameMap){
	return textFmt(nameMap, null);
}

function dateFmt(format){
	return gridFormat.date(format);
}

$(document).ready(function(){
	$('body').keydown(function(e){
		if(e.which==27){
			Dialog.close();
		}
	});
});

var _______table_________;


function TableConfig(elementId, url, columns){
	this.elementId = elementId;
	this.url = url;
	this.columns = [];
	if(columns && columns.length>0){
		if(columns[0].field || columns[0].field){
			
		}
	}
	this.columns = columns;
}

TableConfig.prototype.bootstrapTableColumns = function(){
	return this.columns;
}

function MyDataTable(elementId, url, columns, noinit){
	this.elementId = elementId;
	this.url = url;
	this.columns = columns;
	this.ajaxOptions = {};
	this.filterParams = {};
	if(!noinit){
		this.init();
	}
}

MyDataTable.prototype.getPageParams = function(params) {
	var pageParams = {
		pageNo : params.offset == 0 ? 1 : (params.offset / params.limit) + 1, 
		pageSize : params.limit
	};
	return pageParams;
}

MyDataTable.prototype.getQueryParams = function(params) {
	var pageParams = this.getPageParams(params);
	BeanUtil.mergeProperties(this.filterParams, pageParams);
	console.log('filterParams', this.filterParams);
	console.log('pageParams', pageParams);
	return pageParams;
};

MyDataTable.prototype.httpRequest = function(params){
	console.log('params', params);
	ajaxRequest(params.url, params.data, function(result){
		params.success(result);
	},'GET');

//	$.get(params.url + '?' + $.param(params.data)).then(function (res) {
//		params.success(res);
//	})	
	
}

MyDataTable.prototype.responseHandler = function(result) {
	var data = result.code==='0'?result.data:result;
    var tableData = {
    	rows : data.list,
    	total : data.rowTotal
    };
    console.log('tableData', tableData);
    return tableData;
}

MyDataTable.prototype.btOptions = {
	uniqueId : "id", //每一行的唯一标识，一般为主键列
	pagination : true, //是否显示分页（*）
	sidePagination : "server", //分页方式：client客户端分页，server服务端分页（*）
	pageNumber : 1, //初始化加载第一页，默认第一页
	pageSize : 10, //每页的记录行数（*）
	pageList : [ 10, 20, 50, 100 ], //可供选择的每页的行数（*）
	showRefresh : true,
	clickToSelect: true
}

MyDataTable.prototype.getToken = function(){
	return localStorage.getItem("xtoken")||'';
}

MyDataTable.prototype.init = function(){
	var oThis = this;
	var options = {
			url: oThis.url,
			method: oThis.ajaxMethod||'get',
			columns : oThis.columns,
		    ajaxOptions: oThis.ajaxOptions,
	        responseHandler : function(response){
	        	return oThis.responseHandler(response);
	        },
	        queryParams: function(params){
	        	return oThis.getQueryParams(params);
	        },
	        ajax1: function(params){
	        	oThis.httpRequest(params);
	        }
		};
	
	options.ajaxOptions.headers = {'Authorization': oThis.getToken()}

	BeanUtil.mergeProperties(this.btOptions, options);
	
	this._initBt(options);
}

MyDataTable.prototype._initBt = function(options){
	var $table = $('#'+this.elementId);
	if($.fn.bootstrapTable){
		$table.bootstrapTable(options);
	}
}

MyDataTable.prototype.getSelectedIds = function(){
	var $table = $('#'+this.elementId);
	var selections = $table.bootstrapTable('getSelections');
	var idArray = BeanUtil.getPropertyList(selections, this.btOptions.uniqueId);
	return idArray;
}

MyDataTable.prototype.reload = function(){
	var $table = $('#'+this.elementId);
	//$table.bootstrapTable('resetSearch');
	$table.bootstrapTable('destroy');
	this.init();
}


//////////////////

//设置时间控件
$(function(){
	$('.time-input').each(function(index,element){
	    $(element).datetimepicker({
			format: 'yyyy-MM-dd 00:00:00',
			minView: "month",
	        autoclose: true,
	        clearBtn: true,//清除按钮
	        todayBtn: true,//今日按钮
	        language: 'zh-CN'
	    });
	});
});

$(function () {
	if($.fn.Validform){
		$(".form-horizontal").Validform({ tiptype: 2 });
	}
});
