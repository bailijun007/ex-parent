package com.hp.sh.expv3.pc.extension.service.impl;

import com.hp.sh.expv3.pc.extension.service.PcAccountRecordExtendService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author BaiLiJun  on 2019/12/23
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class PcAccountRecordExtendServiceImpl implements PcAccountRecordExtendService {

}
