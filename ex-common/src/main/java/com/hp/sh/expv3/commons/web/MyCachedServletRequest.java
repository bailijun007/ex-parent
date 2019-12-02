package com.hp.sh.expv3.commons.web;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;

public class MyCachedServletRequest extends ServletRequestWrapper{

	public MyCachedServletRequest(ServletRequest request) {
		super(request);
	}
	
	private ServletInputStream in;
	
	public ServletInputStream getInputStream() throws IOException{
		if(in!=null){
			return in;
		}
		return super.getInputStream(); 
	}

	class MyServletInputStream extends ServletInputStream{
		private ServletInputStream in;

		@Override
		public boolean isFinished() {
			return true;
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setReadListener(ReadListener listener) {
			
		}

		@Override
		public int read() throws IOException {
			return in.read();
		}
		
	}
}
