/**
 * 
 */
package com.hp.sh.expv3.pj.module.admin.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.hp.sh.expv3.pj.module.admin.vo.MenuItem;

/**
 * @author wangjg
 */
public class Resources {
	private static List<MenuItem> menuItems = new ArrayList<MenuItem>();
	
	private static List<Resource> resources = new ArrayList<Resource>();
	
	static{
		menuItems.add(item("主页", "../admin/home/main.html", Authority.OPERATOR, "fa fa-home"));
		menuItems.add(item("用户", "../admin/user/list.html", Authority.OPERATOR, "fa fa-user"));
		menuItems.add(item("群组", "../admin/group/list.html", Authority.ADMIN, "fa fa-users"));
		menuItems.add(item("公告", "../admin/sysnotice/list.html", Authority.ADMIN, "fa fa-bell"));
		menuItems.add(item("举报", "../admin/sysreport/list.html", Authority.ADMIN, "fa fa-hand-stop-o"));
		
		MenuItem setting = item("设置", "", Authority.ADMIN, "fa fa-cog");
		setting.setType(0);
		
		MenuItem ncSetting = item("短信区号", "../admin/nationcode/list.html", Authority.ADMIN, "fa");
		MenuItem funSetting = item("功能设置", "../admin/syssetting/list.html", Authority.ADMIN, "fa");
		MenuItem sysSetting = item("系统设置", "../admin/syssetting/list2.html", Authority.SUPER, "fa");
		setting.setList(Arrays.asList(ncSetting, funSetting, sysSetting));
		menuItems.add(setting);
//		menuItems.add(funSetting);
//		menuItems.add(sysSetting);
//		menuItems.add(ncSetting);
//		menuItems.add(item("头条热股", "../stock/admin/hot/list.html", Authority.ADMIN, "fa fa-bar-chart"));
//		menuItems.add(item("大盘收评", "../stock/admin/daycomment/list.html", Authority.ADMIN, "fa fa-line-chart"));
//		menuItems.add(item("资讯", "../stock/admin/article/list.html", Authority.ADMIN, "fa fa-newspaper-o"));
//		menuItems.add(item("快讯", "../stock/admin/newsflash/list.html", Authority.ADMIN, "fa fa-bolt"));
		
		/////////////

		createResources(menuItems);
		
	}
	
	private static void createResources(List<MenuItem> menuItems){
		for(MenuItem item : menuItems){
			if(StringUtils.isNotBlank(item.getUrl())){
				int pos = item.getUrl().indexOf("/admin/");
				pos = item.getUrl().indexOf('/', pos+"/admin/".length());
				int pos1 = item.getUrl().indexOf('/');
				String url = item.getUrl().substring(pos1, pos+1);
				resources.add(new Resource(url, item.getAuthority()));
			}
			
			if(item.getList()!=null && !item.getList().isEmpty()){
				createResources(item.getList());
				return;
			}
		}
	}
	
	public static List<Resource> getResources() {
		return resources;
	}

	public static Resource findResource(String url) {
		for(Resource resource : resources){
			if(url.startsWith(resource.getUrl())){
				return resource;
			}
		}
		return null;
	}

	public static List<MenuItem> getMenuItems() {
		return new ArrayList<MenuItem>(menuItems);
	}
	
	private static MenuItem item(String name, String url, Integer authority){
		return item(name, url, authority, null);
	}
	
	private static MenuItem item(String name, String url, Integer authority, String icon){
		MenuItem mi = new MenuItem();
		mi.setType(1);
		mi.setName(name);
		mi.setUrl(url);
		mi.setList(Collections.emptyList());
		mi.setAuthority(authority);
		mi.setIcon(icon);
		return mi;
	}
	
}
