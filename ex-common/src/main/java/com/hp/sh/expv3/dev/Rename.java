package com.hp.sh.expv3.dev;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface Rename {

	String value() default "";
	
	String OriginalName() default "";

}
