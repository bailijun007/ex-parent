/**
 * @author zw
 * @date 2019/7/23
 */
package com.hp.sh.expv3.fund.api4v2;

import java.math.BigDecimal;

public interface FundAccount4ChainServerDef {

    /**
     * 创建充币订单，首次探测出充币事件
     *
     * @param operator
     * @param accountId
     * @param asset
     * @param chainServerOrderId
     * @param txHash
     * @param volume
     * @param depositTime
     * @param address            币充入的地址，即用户的充币地址
     * @param token
     * @return
     * @throws FundAccountException
     */
    long createDepositOrder(String operator,long accountId, String asset, String chainServerOrderId, String txHash, BigDecimal volume, long depositTime, String address, String token);

    /**
     * 充币订单确认，可入账
     *
     * @param operator
     * @param accountId
     * @param asset
     * @param depositId
     * @param token
     * @return
     * @throws FundAccountException
     */
    boolean depositOrderConfirm(String operator,long accountId, String asset, String sn, String token);

    /**
     * 充币订单失败，充币失败
     *
     * @param operator
     * @param accountId
     * @param asset
     * @param depositId
     * @param token
     * @return
     * @throws FundAccountException
     */
    boolean depositOrderFail(String operator,long accountId, String asset, String sn, String token);

    /**
     * 更新提币状态
     *
     * @param operator
     * @param accountId
     * @param asset
     * @param token
     * @return
     * @throws FundAccountException
     */
    boolean updateWithdrawOrder(String operator,long accountId, String asset, int status, String token);

}