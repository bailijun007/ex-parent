package com.hp.sh.expv3.fund.extension.api;

import com.hp.sh.expv3.fund.extension.service.CapitalAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 资金账户相关请求
 * @author BaiLiJun  on 2019/12/13
 */
@RestController
@RequestMapping("/baseUrl/account/total")
public class CapitalAccountApi {
    @Autowired
    private CapitalAccountService capitalAccountService;



}
