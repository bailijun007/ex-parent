package com.hp.sh.expv3.fund.extension.service;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.fund.extension.dao.FundAccountExtendMapper;
import com.hp.sh.expv3.fund.extension.vo.CapitalAccountVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/13
 */
public interface FundAccountExtendService {



    public CapitalAccountVo getCapitalAccount(Long userId, String asset) ;

    PageResult<CapitalAccountVo> pageQueryAccountList(Long userId, String asset, Integer pageNo, Integer pageSize);

    BigDecimal queryTotalNumber(String asset);
}
