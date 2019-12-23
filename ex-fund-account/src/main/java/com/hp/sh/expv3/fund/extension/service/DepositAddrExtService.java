package com.hp.sh.expv3.fund.extension.service;

import com.hp.sh.expv3.fund.extension.dao.DepositAddrExtMapper;
import com.hp.sh.expv3.fund.extension.vo.AddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 充币地址扩展服务
 *
 * @author BaiLiJun  on 2019/12/14
 */
public interface DepositAddrExtService {

    public String getAddressByUserIdAndAsset(Long userId, String asset);

    AddressVo getAddresses(Long userId, String asset);


}
