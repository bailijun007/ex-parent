package com.hp.sh.expv3.utils.math;

import java.math.RoundingMode;

/**
 * 计算精度
 * @author wangjg
 *
 */
public class Precision {

    public static final int COMMON_PRECISION = 18;
    
    public static final int PERCENT_PRECISION = 4;
    
    public static final int LEVERAGE_PRECISION = 2;
    
    public static final int INTEGER_PRECISION = 0;
    
    public static final int LIQ_MARGIN_RATIO = 4;

	public static final RoundingMode LESS = RoundingMode.DOWN;
	
	public static final RoundingMode MORE = RoundingMode.UP;
	
}
