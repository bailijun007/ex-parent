/**
 * @author 10086
 * @date 2019/12/14
 */
package com.hp.sh.expv3.match.util;

import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.enums.PcOrderTimeInForceEnum;
import com.hp.sh.expv3.match.mqmsg.PcOrderMqMsgDto;
import org.springframework.beans.BeanUtils;

public class PcOrder4MatchBoUtil {

    public final static PcOrder4MatchBo deepClone(PcOrder4MatchBo bo) {
        PcOrder4MatchBo newBo = new PcOrder4MatchBo();
        BeanUtils.copyProperties(bo, newBo);
        return newBo;
    }

    public final static PcOrder4MatchBo convert(PcOrderMqMsgDto dto) {
        PcOrder4MatchBo newBo = new PcOrder4MatchBo();
        BeanUtils.copyProperties(dto, newBo);
        return newBo;
    }

    public final static void extendSetter(PcOrder4MatchBo bo) {
        if (null == bo.getTimeInForce()) {
            bo.setTimeInForce(PcOrderTimeInForceEnum.GOOD_TILL_CANCEL.getCode());
        }
    }


}