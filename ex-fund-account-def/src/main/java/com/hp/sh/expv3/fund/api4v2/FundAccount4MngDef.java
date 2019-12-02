/**
 * @author zw
 * @date 2019/7/23
 */
package com.hp.sh.expv3.fund.api4v2;

import java.math.BigDecimal;

import javax.security.auth.login.AccountException;

//@Remote
public interface FundAccount4MngDef {

    /**
     * 修改可用余额
     *
     * @param operator
     * @param accountId
     * @param asset
     * @param delta     增量，24 表示在原来基础上 加24
     * @param token
     * @return
     * @throws FundAccountException
     */
    boolean addAvailableByManager(String operator, long accountId, String asset, BigDecimal delta, String token);

    /**
     * 提币审核通过
     *
     * @param operator
     * @param accountId
     * @param asset
     * @param withdrawId
     * @param token
     * @return
     * @throws AccountException
     */
    boolean withdrawAuditPass(String operator, long accountId, String asset, long withdrawId, String token);

    /**
     * 提币审核失败
     *
     * @param operator
     * @param accountId
     * @param asset
     * @param withdrawId
     * @param reason
     * @param token
     * @return
     * @throws AccountException
     */
    boolean withdrawAuditFail(String operator, long accountId, String asset, long withdrawId, String reason, String token);

}