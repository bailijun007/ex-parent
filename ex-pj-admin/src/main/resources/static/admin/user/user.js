
$(function(){
	
	// config
	var userCtx = {
		URL_PAGE_QUERY : "/admin/api/user/action/pageQueryUser",
		URL_GET : "/admin/user/get",
		URL_SAVE : "/admin/user/save",
		URL_BATCH_ENABLE : "/admin/user/enable/batch"
	};
	
	BeanUtil.setPrefix(userCtx, appConfig.host);
	
	var id = 'data_table';
	var tableUrl = userCtx.URL_PAGE_QUERY;
	var data_label = '用户';
	var columns = [
		{
			checkbox : true
		}, {
			title : '编号',
			field : 'id'
		}, {
			title : '用户名',
			field : 'username'
		},
		{
			title : '操作',
			field : '#',
			align : 'center',
			formatter : function(value, row, index) {
				return '<a data-id="'+row.id+'" class="btn-edit" href="javascript:void(0)">修改</a>';
			}
		}
	];
	
	//以下基本固定
	
	var table = new MyDataTable(id, tableUrl, columns, true);
	table.ajaxMethod = 'post';
	table.init();

	function add(){
		Dialog.openUrl('edit.html', '添加'+data_label, 800);
	}

	function edit(event){
		var id = $(event.target).data('id');
		Dialog.openUrl('edit.html?id='+id, '修改'+data_label, 800);
	}
	
	function other(event){
		console.log(event);
		console.log($(event));
		
		if($(event.target).hasClass('btn-edit')){
			edit(event);
		}
	}
	function del(){
		var idList = table.getSelectedIds();
		if(idList==null || idList.length==0){
			alert('没有选中');
			return;
		}
		var idseq = idList.join(',');
		if(idseq){
			AppUtil.confirm('确定删除吗', function(){
				ajaxRequest(userCtx.URL_BATCH_DEL, {idseq:idseq}, function(data){
					refreshQuery();
				});
			});
		}
	}

	function bindEditor(id) {
		if(id){
			ajaxRequest(userCtx.URL_GET, {id:id}, function(data){
				json2form(data, 'editForm');
			});
		}
	}

	function saveOrUpdate() {
		var postData = form2json('editForm');
		ajaxRequest(userCtx.URL_SAVE, postData, function(data){
			refreshTable();
		},'POST');
	}
	
	function refreshTable(){
		$('#queryForm')[0].reset();
		search();
	}

	function search(){
		var formData = form2json('queryForm');
		console.log(formData);
		table.filterParams=formData;
		table.reload();
		
		$('#queryForm')[0].reset();
	}

	$('.btn-add').click(add);
	
	$('.btn-save').click(saveOrUpdate);

	$('.btn-edit').click(edit);
	
	$('.btn-del').click(del);
	
	$('#btn_query').click(search);
	
	$('#data_table').click(other);
	
	if($('#editForm').attr('id')){
		bindEditor(T.p('id'));
	}
	
});

