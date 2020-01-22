package com.hp.sh.expv3.fund.cash.component;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class Asset2SymbolMock {

	private static final Map<String,Integer> asset2symbolMap = new HashMap<String,Integer>();
	private static final Map<Integer, String> symbol2assetMap = new HashMap<Integer, String>();
	
	static {
		// BTC btc symbol
		asset2symbolMap.put("BTC", 10000);
		asset2symbolMap.put("USDT", 10001);
		asset2symbolMap.put("ETH", 20000);
		asset2symbolMap.put("EUSDT", 20001);
		asset2symbolMap.put("BYS1", 20002);
		asset2symbolMap.put("BYS2", 20003);

		  //  EOS 系列
		asset2symbolMap.put("EOS", 30000);
		asset2symbolMap.put("MainMax", 99999);

		  // Test BTC Net
		asset2symbolMap.put("TBTC", 100000);
		asset2symbolMap.put("TUSDT", 100001);

		  // Test Eth Net
		asset2symbolMap.put("TETH", 200000);
		asset2symbolMap.put("TEUSDT", 200001);
		asset2symbolMap.put("TBYS1", 200002);
		asset2symbolMap.put("TETHMAX", 299999);

		  // Test EOS net
		asset2symbolMap.put("TEOS", 300000);
		asset2symbolMap.put("TEOSMAX", 399999);
		
		asset2symbolMap.forEach((k,v) -> symbol2assetMap.put(v, k));
		
	}
	
	public Integer getSymbol(String asset){
		return asset2symbolMap.get(asset);
	}
	
	public String getAsset(Integer symbol){
		return symbol2assetMap.get(symbol); 
	}
	
}
