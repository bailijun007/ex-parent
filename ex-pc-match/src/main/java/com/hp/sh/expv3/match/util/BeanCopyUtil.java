/**
 * @author 10086
 * @date 2019/12/23
 */
package com.hp.sh.expv3.match.util;

import com.hp.sh.expv3.match.mqmsg.PcOrderSameSideCancelled4PosLockMqMsgDto;
import com.hp.sh.expv3.match.mqmsg.PcPosLockedMqMsgDto;
import org.springframework.beans.BeanUtils;

public class BeanCopyUtil {

    public static final PcOrderSameSideCancelled4PosLockMqMsgDto copy(PcPosLockedMqMsgDto dto) {
        PcOrderSameSideCancelled4PosLockMqMsgDto msg = new PcOrderSameSideCancelled4PosLockMqMsgDto();
        BeanUtils.copyProperties(dto, msg);
        return msg;
    }

}
