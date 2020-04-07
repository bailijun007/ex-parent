package com.hp.sh.expv3.bb.grab3rdData.component;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WsClient extends WebSocketListener {
	private static final Logger logger = LoggerFactory.getLogger(WsClient.class);

	private String wsurl = "wss://api.zb.live/websocket";

	private WebSocket ws;
	
	public WsClient(String wsurl) {
		this.wsurl = wsurl;
	}

	public synchronized void connect() {
		OkHttpClient mOkHttpClient = new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS)// 设置读取超时时间
				.writeTimeout(10, TimeUnit.SECONDS)// 设置写的超时时间
				.connectTimeout(3, TimeUnit.SECONDS)// 设置连接超时时间
				.build();

		Request request = new Request.Builder().url(wsurl).build();
		WebSocketListener socketListener = this;
		this.ws = mOkHttpClient.newWebSocket(request, socketListener);

	}
	
	public synchronized void close(){
		try{
			this.ws.cancel();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public synchronized void send(Object msg) {
		String text = msg.toString();
		this.ws.send(text);
	}

	public void onOpen(WebSocket webSocket, Response response) {
		logger.debug("open={}", webSocket);
		this.ws.send("{'event':'addChannel', 'channel':'ltcbtc_ticker',}");
	}

	public void onMessage(WebSocket webSocket, String text) {
		logger.debug("text={}", text);
	}

	public void onFailure(WebSocket webSocket, Throwable t, Response response) {
		logger.debug("t={}", t.getMessage(), t);
	}

	public void onClosing(WebSocket webSocket, int code, String reason) {
		System.out.println("onClosing");
	}

	public void onClosed(WebSocket webSocket, int code, String reason) {
		System.out.println("onClosed");
	}

}
