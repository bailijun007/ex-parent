/**
 * 
 */
package com.hp.sh.expv3.fund.cash.component;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.hp.sh.chainserver.client.AddressRequest;
import com.hp.sh.chainserver.client.AddressResponse;
import com.hp.sh.chainserver.client.ChainClient;
import com.hp.sh.chainserver.client.CheckAddressRequest;
import com.hp.sh.chainserver.client.CheckAddressResponse;
import com.hp.sh.chainserver.client.NotifyCreateParams;
import com.hp.sh.chainserver.client.NotifyResultParams;
import com.hp.sh.chainserver.client.WithDrawRequest;
import com.hp.sh.chainserver.client.WithDrawResponse;
import com.hp.sh.chainserver.utils.ChainUtil;

/**
 * @author wangjg
 */
@Component
@CacheConfig(cacheNames="chain")
public class ExChainService{

	@Value("${bys.server.host}")
	private String apiHost;
	
	@Value("${bys.client.merchantId}")
	private String merchantId;
	
	@Value("${bys.client.secret}")
	private String secret;
	
	private ChainClient chainClient;
	
	@Autowired
	private ExChainService self;
	
	@Cacheable(unless="#result==null")
	public String getAddress(Long userId, Integer symbol){
		AddressRequest request = new AddressRequest();
		request.setAccount(""+userId);
		request.setMerchantId(merchantId);
		request.setSymbol(symbol);
		AddressResponse resp = this.client().getAddress(request);
		return resp.getAddress();
	}
	
	public Boolean checkAddress(Integer symbol){
		CheckAddressRequest request = new CheckAddressRequest();
		request.setAddress(request.getAddress());
		request.setMerchantId(merchantId);
		request.setSymbol(symbol);
		CheckAddressResponse response = chainClient.checkAddress(request);
		return response.getIsOk();
	}
	
	public WithDrawResponse draw(Long userId, Integer symbol, BigDecimal amount, String merWithdrawId){
		WithDrawRequest request = new WithDrawRequest();
		request.setAccount(""+userId);
		request.setAddress(self.getAddress(userId, symbol));
		request.setMerchantId(merchantId);
		request.setSymbol(symbol);
		request.setValue(amount.toString());
		request.setMerWithdrawId(merWithdrawId);
		WithDrawResponse response = this.client().draw(request);
		return response;
	}
	
	private ChainClient client(){
		if(this.chainClient==null){
			this.chainClient = new ChainClient();
			this.chainClient.setApiServer(apiHost);
			this.chainClient.setSecret(secret);
		}
		return chainClient;
	}
	
	public NotifyCreateParams getCreateParams(Map map){
		NotifyCreateParams cp = ChainUtil.getCreateParams(map, secret);
		return cp;
	}
	
	public NotifyResultParams getNotifyParams(Map map){
		NotifyResultParams np = ChainUtil.getNotifyParams(map, secret);
		return np;
	}
}
