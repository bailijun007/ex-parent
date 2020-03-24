package com.hp.sh.expv3.bb.extension.service.impl;

import com.hp.sh.expv3.bb.extension.service.BbAccountLogExtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author BaiLiJun  on 2020/3/24
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbAccountLogExtServiceImpl implements BbAccountLogExtService {

}
