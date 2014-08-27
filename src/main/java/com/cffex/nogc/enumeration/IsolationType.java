/**
 * @time 2014年8月19日
 * @author sunke
 * @Description TODO
 */
package com.cffex.nogc.enumeration;

/**
 * @author sunke
 * @ClassName IsolationType
 * @Description: segment的隔离级别 
 */
public enum IsolationType {
	RESTRICT, //严格的隔离级别，限制merge时，不可以进行读操作。
	HIGH_PERFORMANCE //高性能，进行merge时，也可以进行读操作
}
