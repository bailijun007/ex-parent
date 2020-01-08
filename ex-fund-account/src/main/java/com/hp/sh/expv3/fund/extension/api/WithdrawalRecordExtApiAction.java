package com.hp.sh.expv3.fund.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.extension.error.DepositExtError;
import com.hp.sh.expv3.fund.extension.error.FundCommonError;
import com.hp.sh.expv3.fund.extension.error.WithdrawalExtError;
import com.hp.sh.expv3.fund.extension.service.WithdrawalAddrExtService;
import com.hp.sh.expv3.fund.extension.service.WithdrawalRecordExtService;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalAddrVo;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalRecordVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@RestController
@Api(tags = "体现记录扩展Api")
public class WithdrawalRecordExtApiAction implements WithdrawalRecordExtApi {
    @Autowired
    private WithdrawalRecordExtService withdrawalRecordExtService;

    @Autowired
    private WithdrawalAddrExtService withdrawalAddrExtService;

    @Override
    public List<WithdrawalRecordVo> queryHistory(Long userId, String asset, Long queryId, Integer pageSize, Integer pageStatus) {
        if (userId == null || pageSize == null || StringUtils.isEmpty(asset)) {
            throw new ExException(FundCommonError.PARAM_EMPTY);
        }

        List<WithdrawalRecordVo> voList = getWithdrawalRecordVos(userId, asset, queryId, pageSize, pageStatus);

        return voList;
    }

    @Override
    public List<WithdrawalRecordVo> queryHistoryByTime(Long userId, String asset, Long timestamp) {
        if (userId == null || timestamp == null || StringUtils.isEmpty(asset)) {
            throw new ExException(FundCommonError.PARAM_EMPTY);
        }
//        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));
        List<WithdrawalRecordVo> voList = getWithdrawalRecordVos(userId, asset, timestamp, null);
        return voList;
    }

    @Override
    public WithdrawalRecordVo queryLastHistory(Long userId, String asset) {
        WithdrawalRecordVo vo = withdrawalRecordExtService.queryLastHistory(userId, asset);
        return vo;
    }

    @Override
    public List<WithdrawalRecordVo> queryUserWithdrawal(Long userId, String asset, Long startTime, Long endTime) {
        if (userId == null || startTime == null || endTime == null || StringUtils.isEmpty(asset)) {
            throw new ExException(FundCommonError.PARAM_EMPTY);
        }
        List<WithdrawalRecordVo> voList = getWithdrawalRecordVos(userId, asset, startTime, endTime);
        return voList;
    }

    private List<WithdrawalRecordVo> getWithdrawalRecordVos(Long userId, String asset, Long startTime, Long endTime) {
        List<WithdrawalRecordVo> voList = withdrawalRecordExtService.findWithdrawalRecordList(userId, asset, startTime, endTime);
        if (!CollectionUtils.isEmpty(voList)) {
            for (WithdrawalRecordVo vo : voList) {
                WithdrawalAddrVo withdrawalAddrVo = withdrawalAddrExtService.getAddressByUserIdAndAsset(vo.getUserId(), vo.getAsset());
                vo.setTargetAddress(withdrawalAddrVo.getAddress());
            }
        }
        return voList;
    }

    @Override
    public PageResult<WithdrawalRecordVo> queryAllUserHistory(Long userId, String asset, Long startTime, Long endTime, Integer pageNo, Integer pageSize) {
        if (pageNo == null || pageSize == null) {
            throw new ExException(FundCommonError.PARAM_EMPTY);
        }
        Instant now = Instant.now();
        if (startTime == null && endTime == null) {
            endTime =now.toEpochMilli();
        }

        PageResult<WithdrawalRecordVo> result = withdrawalRecordExtService.pageQueryHistory(userId, asset, pageNo, pageSize, startTime ,endTime);
        if (!CollectionUtils.isEmpty(result.getList())) {
            for (WithdrawalRecordVo vo : result.getList()) {
                WithdrawalAddrVo withdrawalAddrVo = withdrawalAddrExtService.getAddressByUserIdAndAsset(vo.getUserId(), vo.getAsset());
                Optional<WithdrawalAddrVo> optional = Optional.ofNullable(withdrawalAddrVo);
                vo.setTargetAddress(optional.map(WithdrawalAddrVo::getAddress).orElse(null));
            }
        }

        return result;
    }

    private List<WithdrawalRecordVo> getWithdrawalRecordVos(Long userId, String asset, Long queryId, Integer pageSize, Integer pageStatus) {
        List<WithdrawalRecordVo> voList = withdrawalRecordExtService.queryHistory(userId, asset, queryId, pageSize, pageStatus);
        for (WithdrawalRecordVo vo : voList) {
            WithdrawalAddrVo withdrawalAddrVo = withdrawalAddrExtService.getAddressByUserIdAndAsset(vo.getUserId(), vo.getAsset());
            vo.setTargetAddress(withdrawalAddrVo.getAddress());
        }
        return voList;
    }

    private boolean isExsit(List<WithdrawalRecordVo> voList, List<WithdrawalAddrVo> withdrawalAddrVos, int i, int j) {
        return voList.get(i).getUserId().equals(withdrawalAddrVos.get(j).getUserId()) && voList.get(i).getAsset().equals(withdrawalAddrVos.get(j).getAsset());
    }


}
