/**
 * @author 10086
 * @date 2019/12/14
 */
package com.hp.sh.expv3.match.util;

import com.hp.sh.expv3.match.bo.BbOrder4MatchBo;
import com.hp.sh.expv3.match.enums.BbOrderTimeInForceEnum;
import com.hp.sh.expv3.match.mqmsg.BbOrderMqMsgDto;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

public class BbOrder4MatchBoUtil {

    public final static BbOrder4MatchBo deepClone(BbOrder4MatchBo bo) {
        BbOrder4MatchBo newBo = new BbOrder4MatchBo();
        BeanUtils.copyProperties(bo, newBo);
        return newBo;
    }

    public final static BbOrder4MatchBo convert(BbOrderMqMsgDto dto) {
        BbOrder4MatchBo newBo = new BbOrder4MatchBo();
        BeanUtils.copyProperties(dto, newBo);
        return newBo;
    }

    public final static void extendSetter(BbOrder4MatchBo bo) {
        if (null == bo.getTimeInForce()) {
            bo.setTimeInForce(BbOrderTimeInForceEnum.GOOD_TILL_CANCEL.getCode());
        }
        if (null == bo.getFilledNumber()) {
            bo.setFilledNumber(BigDecimal.ZERO);
        }
        if (null == bo.getDisplayNumber()) {
            bo.setDisplayNumber(bo.getNumber());
        }
    }

    public final static void extendSetter(BbOrderMqMsgDto dto) {
        if (null == dto.getFilledNumber()) {
            dto.setFilledNumber(BigDecimal.ZERO);
        }
        if (null == dto.getDisplayNumber()) {
            dto.setDisplayNumber(dto.getNumber());
        }
        if (null == dto.getTimeInForce()) {
            dto.setTimeInForce(BbOrderTimeInForceEnum.GOOD_TILL_CANCEL.getCode());
        }
    }


}