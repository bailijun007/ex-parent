package com.hp.sh.expv3.pc.extension.api;

import com.hp.sh.expv3.pc.extension.service.PcAccountRecordExtendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiLiJun  on 2019/12/24
 */
@RestController
public class PcAccountRecordExtendApiAction implements PcAccountRecordExtendApi {
    @Autowired
    private PcAccountRecordExtendService pcAccountRecordExtendService;


}
