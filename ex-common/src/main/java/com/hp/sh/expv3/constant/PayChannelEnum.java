/**
 * @author zw
 * @date 2019/8/21
 */
package com.hp.sh.expv3.constant;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 支付类型
 */
enum PayChannelEnum {

    /**
     * 支付宝
     */
	ALIPAY(1, "ALI_PAY"),
    /**
     * 微信
     */
	WEIXIN_PAY(2, "WEIXIN_PAY"),
	/**
	 * BYS
	 */
	BYS(3, "BYS"),
    ;

    private int code;
    private String name;

    private PayChannelEnum(int code, String constant) {
        this.code = code;
        this.name = constant;
    }

    public static Map<Integer, PayChannelEnum> code2Enum = EnumSet.allOf(PayChannelEnum.class).stream().collect(Collectors
            .toMap(vo -> vo.getCode(), Function.identity(), (oldValue, newValue) -> newValue));

    public int getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

}
