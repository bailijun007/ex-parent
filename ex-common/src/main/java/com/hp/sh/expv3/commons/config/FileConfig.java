package com.hp.sh.expv3.commons.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wangjg
 * 
 * 文件配置
 */
public class FileConfig implements SysConfig{
	private static final Logger logger = LoggerFactory.getLogger(FileConfig.class);
	
	private Properties settings = new Properties();
	private File file ;
	protected String configPath = "config.properties";//classloader不能以'/'开头
	
	public FileConfig(){}

	public void loadConfig() {
		InputStream is = null;
		try {
			URL url = this.getResource(configPath);
			this.file = new File(url.toURI());
			is = new FileInputStream(file);
			
			int start = configPath.lastIndexOf(".");
			if (configPath.substring(start).toLowerCase().equals(".xml")) {
				settings.loadFromXML(is);
			} else {
				settings.load(is);
			}
		} catch (Exception e) {
			throw new RuntimeException("*load configuration error, filepath:"+configPath ,e);
		}finally{
			try {
				if(is!=null)is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected URL getResource(String path){
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		logger.debug(cl.getClass().getName());
		URL url = cl.getResource(path);
		return url;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public String getConfigPath() {
		return configPath;
	}
	
	public String getString(String key) {
		if(settings==null){
			return null;
		}
		String value = settings.getProperty(key);
		if(value==null){
			if(key!=null && key.startsWith("{") && key.endsWith("}")){
				value = System.getProperty(key.substring(1, key.length()-1));
			}
		}
		return value;
	}

}