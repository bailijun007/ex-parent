package com.hp.sh.expv3.pc.extension.api;

import io.swagger.annotations.Api;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author BaiLiJun  on 2019/12/24
 */
@Api(tags = "合约账户扩展接口")
@FeignClient(value = "ex-pc-extend")
public interface PcAccountRecordExtendApi {



}
