/**
 * 
 */
package com.yc.spring.mvc.core;

/**
 * 检查参数
 * @author lh
 * @data 2020年10月25日
 * Email 2944862497@qq.com
 */
public class StringUtil {
	
	/**
	 * 检查参数是否为空
	 * @param str
	 * @return
	 */
	public static boolean checkNull(String ... strs) {
		
		if(strs == null || strs.length <= 0) {
			return true;
		}
		
		for( String str : strs) {
			if(str == null || "".equals(str)) {
				return true;
			}
		}
		return false;
	}

}
