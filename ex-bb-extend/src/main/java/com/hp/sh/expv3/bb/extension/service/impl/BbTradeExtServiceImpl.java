package com.hp.sh.expv3.bb.extension.service.impl;

import com.hp.sh.expv3.bb.extension.service.BbTradeExtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author BaiLiJun  on 2020/2/15
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbTradeExtServiceImpl implements BbTradeExtService {
}
