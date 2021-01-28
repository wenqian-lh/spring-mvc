/**
 * 
 */
package com.yc.spring.mvc.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lh
 * @data 2021年1月23日
 * Email 2944862497@qq.com
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
	
	public String value() default ""; // 可以指定一个注入的名称

}
