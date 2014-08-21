/**
 * @time 2014年8月21日
 * @author sunke
 */
package com.cffex.nogc.memory.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author sunke
 * @ClassName PotentialProblem
 * @Description: 目前设计可能存在的问题，尚没有解决的时候使用
 */
@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface PotentialProblem {
	String reason() default "";
	String problem() default ""; 
}
