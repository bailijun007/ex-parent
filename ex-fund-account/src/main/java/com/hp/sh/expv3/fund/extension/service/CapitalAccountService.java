package com.hp.sh.expv3.fund.extension.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author BaiLiJun  on 2019/12/13
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class CapitalAccountService {
}
