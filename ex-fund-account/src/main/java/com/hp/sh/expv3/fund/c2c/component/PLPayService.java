package com.hp.sh.expv3.fund.c2c.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PLPayService {

	@Value("${plpay.server.host:}")
	private String apiHost;
	
	
}
