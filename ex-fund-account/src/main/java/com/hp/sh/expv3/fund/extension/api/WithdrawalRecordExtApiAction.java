package com.hp.sh.expv3.fund.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.extension.error.ExFundError;
import com.hp.sh.expv3.fund.extension.service.WithdrawalAddrExtService;
import com.hp.sh.expv3.fund.extension.service.WithdrawalRecordExtService;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalAddrVo;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalRecordByAdmin;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalRecordVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

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
        checkParam(userId, asset, pageSize, pageStatus);

        List<WithdrawalRecordVo> voList = getWithdrawalRecordVos(userId, asset, queryId, pageSize, pageStatus);

        return voList;
    }

    private void checkParam(Long userId, String asset, Integer pageSize, Integer pageStatus) {
        if (userId == null || pageSize == null || StringUtils.isEmpty(asset)||pageStatus==null) {
            throw new ExException(ExFundError.PARAM_EMPTY);
        }
    }

    @Override
    public PageResult<WithdrawalRecordByAdmin> queryHistoryByAdmin(Long userId, String asset, Integer status,Integer payStatus,Integer pageNo, Integer pageSize) {
        if ( pageSize == null ||pageNo==null) {
            throw new ExException(ExFundError.PARAM_EMPTY);
        }
        return  withdrawalRecordExtService.queryHistoryByAdmin(userId, asset,status, payStatus,pageNo, pageSize);

    }

    @Override
    public List<WithdrawalRecordVo> queryHistoryByTime(Long userId, String asset, Long startTime,Long endTime) {
        if (userId == null || startTime == null || StringUtils.isEmpty(asset)|| endTime == null ) {
            throw new ExException(ExFundError.PARAM_EMPTY);
        }
        List<WithdrawalRecordVo> voList = getWithdrawalRecordVos(userId, asset, startTime, endTime);
        return voList;
    }

    /**
     * 获取最新提币历史
     *
     * @param userId 用户id
     * @param asset  资产
     * @return
     */
    @Override
    public WithdrawalRecordVo queryLastHistory(Long userId, String asset) {
        WithdrawalRecordVo vo = withdrawalRecordExtService.queryLastHistory(userId, asset);
        return vo;
    }

    /**
     * 查询某个用户一段时间的提币数量
     * <p>
     * txHash 哈希值 数据库缺字段，后期让老王加上或者掉老王接口
     *
     * @param userId    用户id
     * @param asset     资产
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    @Override
    public List<WithdrawalRecordVo> queryUserWithdrawal(Long userId, String asset, Long startTime, Long endTime) {
        if (userId == null || startTime == null || endTime == null ) {
            throw new ExException(ExFundError.PARAM_EMPTY);
        }
        List<WithdrawalRecordVo> voList = getWithdrawalRecordVos(userId, asset, startTime, endTime);

        return voList;
    }

    private List<WithdrawalRecordVo> getWithdrawalRecordVos(Long userId, String asset, Long startTime, Long endTime) {
        List<WithdrawalRecordVo> voList = withdrawalRecordExtService.findWithdrawalRecordList(userId, asset, startTime, endTime);
        return voList;
    }

    @Override
    public PageResult<WithdrawalRecordVo> queryAllUserHistory(Long userId, String asset, Long startTime, Long endTime, Integer approvalStatus, Integer pageNo, Integer pageSize) {
        if (pageNo == null || pageSize == null) {
            throw new ExException(ExFundError.PARAM_EMPTY);
        }

        Instant now = Instant.now();
        if (startTime == null && endTime == null) {
            endTime = now.toEpochMilli();
            startTime = endTime - ((endTime + TimeZone.getDefault().getRawOffset()) % (24 * 60 * 60 * 1000L));

        }

        PageResult<WithdrawalRecordVo> result = withdrawalRecordExtService.pageQueryHistory(userId, asset, pageNo, pageSize, startTime, endTime, approvalStatus);

        return result;
    }

    @Override
    public BigDecimal queryTotalNumber(String asset, Integer payStatus) {
        if ( payStatus == null||StringUtils.isEmpty(asset)) {
            throw new ExException(ExFundError.PARAM_EMPTY);
        }
        return withdrawalRecordExtService.queryTotalNumber(asset,payStatus);
    }

    private List<WithdrawalRecordVo> getWithdrawalRecordVos(Long userId, String asset, Long queryId, Integer pageSize, Integer pageStatus) {
        List<WithdrawalRecordVo> voList = withdrawalRecordExtService.queryHistory(userId, asset, queryId, pageSize, pageStatus);
        return voList;
    }

    private boolean isExsit(List<WithdrawalRecordVo> voList, List<WithdrawalAddrVo> withdrawalAddrVos, int i, int j) {
        return voList.get(i).getUserId().equals(withdrawalAddrVos.get(j).getUserId()) && voList.get(i).getAsset().equals(withdrawalAddrVos.get(j).getAsset());
    }


}
