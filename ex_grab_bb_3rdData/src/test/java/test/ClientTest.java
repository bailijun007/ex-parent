package test;

import okhttp3.*;

import java.io.IOException;

public class ClientTest {
	
	static final String url = "wss://api.zb.live/websocket";

	public static void main(String[] args) throws InterruptedException {
//		ZbWsClient client = new ZbWsClient(url);
//		client.connect();
//
//		Thread.sleep(3000);
        OkHttpClient client = new OkHttpClient();

        String url = "http://api.zb.live/data/v1/ticker?market=btc_usdt";
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        Call call = client.newCall(request);

    //同步调用,返回Response,会抛出IO异常
        try {
            Response response = call.execute();
            final ResponseBody responseBody = response.body();
            final String string = responseBody.string();
            System.out.println("string = " + string);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
