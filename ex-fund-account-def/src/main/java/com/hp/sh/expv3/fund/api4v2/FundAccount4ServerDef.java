/**
 * @author zw
 * @date 2019/7/23
 */
package com.hp.sh.expv3.fund.api4v2;

import java.math.BigDecimal;

import javax.security.auth.login.AccountException;

/**
 * 地址
 * @author wangjg
 *
 */
//@Remote
@Deprecated
public interface FundAccount4ServerDef {

    /**
     * 创建资金账户
     *
     * @param operator
     * @param accountId
     * @param token
     * @return
     */
    boolean createFundAccount(String operator, long accountId, String asset, String token);

    /**
     * 从同一个用户的资金账户 转向 永续合约账户
     *
     * @param operator
     * @param accountId
     * @param asset
     * @param volume    只能大于0
     * @param token
     * @return
     * @throws AccountTransferException
     */
    boolean transfer2PcAccount(String operator, long accountId, String asset, BigDecimal volume, String token);

    /**
     * 获取充币地址
     *
     * @param operator
     * @param accountId
     * @param asset
     * @param token
     * @return
     * @throws AccountTransferException
     */
    String getDepositAddress(String operator, long accountId, String asset, String token);

    /**
     * 保存提币地址
     *
     * @param operator
     * @param accountId
     * @param asset
     * @param address
     * @param remark
     * @param token
     * @return
     * @throws AccountException
     */
    long saveWithdrawAddress(String operator, long accountId, String asset, String address, String remark, String token);

    /**
     * 更新提币地址备注
     *
     * @param operator
     * @param accountId
     * @param asset
     * @param withdrawAddrId
     * @param remark
     * @param token
     * @return
     * @throws AccountException
     */
    boolean updateWithdrawAddressRemark(String operator, long accountId, String asset, long withdrawAddrId, String remark, String token);

    /**
     * 删除提币地址
     *
     * @param operator
     * @param accountId
     * @param asset
     * @param withdrawAddrId
     * @param token
     * @return
     * @throws AccountException
     */
    boolean deleteWithdrawAddress(String operator, long accountId, String asset, long withdrawAddrId, String token);

    /**
     * 提币
     *
     * @param operator
     * @param accountId
     * @param asset
     * @param withdrawAddr
     * @param volume
     * @param token
     * @return withdrawId
     * @throws AccountException
     */
    long withdraw(String operator, long accountId, String asset, String withdrawAddr, BigDecimal volume, String token);

    /**
     * 验证地址
     *
     * @param operator
     * @param asset
     * @param address
     * @param token
     * @return
     * @throws AccountException
     */
    boolean verifyAddress(String operator, String asset, String address, String token);

    /**
     * 推荐手续费返佣，子推荐人返佣
     *
     * @param operator
     * @param accountId
     * @param asset
     * @param delta     增量，24 表示在原来基础上 加24，必须为正
     * @param token
     * @return
     * @throws FundAccountException
     */
    boolean addLeafFeeRebate(String operator, long accountId, String asset, BigDecimal delta, String token);

}