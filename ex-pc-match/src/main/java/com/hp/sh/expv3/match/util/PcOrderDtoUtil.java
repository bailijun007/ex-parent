/**
 * @author 10086
 * @date 2019/12/16
 */
package com.hp.sh.expv3.match.util;

import com.hp.sh.expv3.match.match.core.match.thread.PcMatchHandlerContext;

public class PcOrderDtoUtil {

    public static <T extends BasePcOrderDto> T baseSet(PcMatchHandlerContext context, T t, long currentMsgOffset) {

        t.setAsset(context.getAsset());
        t.setAssetSymbol(context.getAssetSymbol());
        t.setSymbol(context.getSymbol());

        t.setCurrentMsgOffset(currentMsgOffset);
        t.setLastPrice(context.getLastPrice());

        return t;
    }

}
