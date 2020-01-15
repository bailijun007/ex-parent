package com.hp.sh.expv3.dev;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 冗余的
 * @author wangjg
 *
 */
@Deprecated
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface Redundant {

}
