package test;

import com.hp.sh.expv3.bb.grab3rdData.component.WsClient;

public class ClientTest {
	
	static final String url = "wss://api.zb.live/websocket";

	public static void main(String[] args) throws InterruptedException {
		WsClient client = new WsClient(url);
		client.connect();
		
		Thread.sleep(3000);
	}

}
