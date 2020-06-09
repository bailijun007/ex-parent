package com.hp.sh.expv3.pj.base;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.commons.bean.PageResult;

/**
 * 
 * @author wangjg
 *
 */
public class BaseBizAction extends WebSessionService{

	protected Page getPage(Integer pageNo) {
		String s = this.getRequest().getParameter("pageSize");
		int pageSize = 10;
		if(StringUtils.isNotBlank(s)){
			pageSize = Integer.parseInt(s);
		}
		return this.getPage(pageNo, pageSize);
	}
	
	protected Page getPage(Integer pageNo, Integer pageSize) {
		return this.getPage(pageNo, pageSize, null);
	}
	
	protected Page getPage(Integer pageNo, Integer pageSize, Long total) {
		if(pageNo==null){
			pageNo = 1;
		}
		if(pageSize==null){
			pageSize = 10;
		}
		Page page = new Page(pageNo, pageSize, total);
		return page;
	}

	protected <T> PageResult<T> getPageResult(Page page, List<T> list) {
		PageResult<T> result = new PageResult<T>();
		result.setList(list);
		result.setRowTotal(page.getRowTotal());
		result.setPageNo(page.getPageNo());
		result.setPageCount(page.getPageCount());
		return result;
	}

	protected void sendRedirect(HttpServletResponse response, String location) {
		try {
			response.sendRedirect(location);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	

	protected void checkOwner(Object anchorPrice1) {
		
	}

	protected void setOwner(Object anchorPrice) {
		
	}

}
