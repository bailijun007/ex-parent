package com.hp.sh.expv3.commons.bean;


import java.util.Collections;
import java.util.List;

import com.gitee.hupadev.commons.bean.BeanHelper;
import com.gitee.hupadev.commons.page.Page;

/**
 * 分页查询结果
 */
public class PageResult<T>{
    private List<T> list;

    private Long rowTotal;
    
    private Integer pageNo;
    
    private Integer pageCount;

	public PageResult() {
	}

	public PageResult(List<T> list, Page page) {
		this.list = list;
		this.rowTotal = page.getRowTotal();
		this.pageNo = page.getPageNo();
		this.pageCount = page.getPageCount();
	}

	public List<T> getList() {
		return (List<T>) list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public Long getRowTotal() {
		return rowTotal;
	}

	public void setRowTotal(Long rowTotal) {
		this.rowTotal = rowTotal;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getPageCount() {
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}

	public void setPage(PageResult<?> pr) {
		this.pageNo = pr.getPageNo();
		this.pageCount = pr.getPageCount();
		this.rowTotal = pr.getRowTotal();
	}
	
	public static <N> PageResult<N> convert(PageResult<?> pr, Class<N> clazz){
		List<?> origList = pr.getList();
		List<N> newList = BeanHelper.copyList(origList, clazz);
		
		PageResult<N> result = new PageResult<N>();
		result.setList(newList);
		result.setPage(pr);
		return result;
	}

	/**
	 * @return
	 */
	public static <R> PageResult<R> empty() {
		PageResult<R> rr = new PageResult<R>();
		rr.setList(Collections.emptyList());
		rr.setPageNo(1);
		rr.setPageCount(0);
		rr.setRowTotal(0L);
		return rr;
	}
    
}
