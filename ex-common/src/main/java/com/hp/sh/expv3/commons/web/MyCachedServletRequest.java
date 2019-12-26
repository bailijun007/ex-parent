package com.hp.sh.expv3.commons.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;

public class MyCachedServletRequest extends HttpServletRequestWrapper{

	private ServletInputStream newServletInputStream;
	
	public MyCachedServletRequest(HttpServletRequest request) {
		super(request);
	}

	public ServletInputStream getInputStream() throws IOException{
		if(newServletInputStream==null){
			InputStream input = super.getInputStream();
			byte[] buf = IOUtils.toByteArray(input);
			ByteArrayInputStream newInputStream = new ByteArrayInputStream(buf);
			this.newServletInputStream = new MyServletInputStream(newInputStream);
		}
		return this.newServletInputStream; 
	}

	class MyServletInputStream extends ServletInputStream{
		private InputStream in;

		public MyServletInputStream(InputStream in) {
			this.in = in;
		}

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
