package com.hp.sh.expv3.fund.extension.api;

import com.hp.sh.expv3.fund.extension.service.WithdrawalRecordExtService;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalRecordVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@RestController
@Api(tags = "体现记录扩展Api")
public class WithdrawalRecordExtApiAction implements WithdrawalRecordExtApi {
    @Autowired
    private WithdrawalRecordExtService withdrawalRecordExtService;

    @Override
    public List<WithdrawalRecordVo> queryHistory(Long userId, String asset, Long queryId, Integer pageSize, Integer pageStatus) {
        List<WithdrawalRecordVo> voList = withdrawalRecordExtService.queryHistory(userId, asset, queryId, pageSize, pageStatus);

        return voList;
    }
}
