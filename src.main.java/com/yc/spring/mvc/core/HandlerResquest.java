/**
 * 
 */
package com.yc.spring.mvc.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.yc.spring.mvc.core.annotation.RequestParam;

/**
 * 处理请求中的参数
 * @author lh
 * @data 2021年1月25日
 * Email 2944862497@qq.com
 */
public class HandlerResquest {

	/**
	 * @param request
	 * @param method
	 * @param response
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static Object[] handle(HttpServletRequest request, Method method, HttpServletResponse response) throws InstantiationException, IllegalAccessException {
		// 数组的长度，取决于这个方法的形参个数
		int count = method.getParameterCount();
		
		// 最终的目标是激活方法，所以方法需要几个形参就给几个
		Object[]args = new Object[count];
		
		// 获取这个方法的参数
		Parameter[] params = method.getParameters();
		
		String paramName = null; // 形参名
		Class<?> cls = null; // 形参类型
		RequestParam requestParam = null;
		String value = null;
		int index = 0;
		Map<String, String[]> paramValues = null;
		Map<String, Object> paramMap = null;
		
		Field[] fields = null;
		String attrName = null;
		Class<?> attrType = null;
		Object obj = null;
		
		// 循环获取这些参数信息
		for (Parameter param : params) {
			paramName = param.getName(); // 获取形参名
			cls = param.getType();
			
			// 判断形参有没有@RequestParam注解，如果有则根据注解的value值来从请求中获取属性值
			requestParam = param.getAnnotation(RequestParam.class);
			if (requestParam != null) {
				paramName = requestParam.value();
			}
			
			value = request.getParameter(paramName);
			
			// 将这个字符串强制转换成方法需要的类型
			
			if (cls == Integer.TYPE) {
				args[index] = Integer.parseInt(value);
			} else if (cls == Integer.class) {
				args[index] = Integer.valueOf(value);
			} else if (cls == Float.TYPE) {
				args[index] = Float.parseFloat(value);
			} else if (cls == Float.class) {
				args[index] = Float.valueOf(value);
			} if (cls == Double.TYPE) {
				args[index] = Double.parseDouble(value);
			} else if (cls == Double.class) {
				args[index] = Double.valueOf(value);
			} else if (cls == String.class) {
				args[index] = value;
			} else if (cls == Map.class) {
				paramValues = request.getParameterMap();
				paramMap = new HashMap<String, Object>();
				for (Entry<String, String[]> entry : paramValues.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue()[0]);
				}
				args[index] = paramMap;
			} else if (cls == ServletRequest.class || cls == HttpServletRequest.class) {
				args[index] = request;
			} else if (cls == ServletResponse.class || cls == HttpServletResponse.class) {
				args[index] = response;
			} else if (cls == HttpSession.class) {
				args[index] = request.getSession();
			} else if (cls == ServletContext.class) {
				args[index] = request.getServletContext();
			} else {
				// 当场对象处理
				// 获取这个对象的属性
				fields = cls.getDeclaredFields();
				obj = cls.newInstance(); // 实例化这个类的对象
				
				for (Field fd : fields) {
					// 强吻属性
					fd.setAccessible(true);
					
					attrName = fd.getName();
					attrType = fd.getType();
					
					value = request.getParameter(attrName); // 根据属性名从请求中获取属性值
					
					if (StringUtil.checkNull(value)) { // 说明这个属性没有给值
						continue;
					}

					if (attrType == Integer.TYPE) {
						fd.set(obj, Integer.parseInt(value));
					} else if ("class java.lang.Integer".equals(attrType)) {
						fd.set(obj, Integer.valueOf(value));
					} else if (attrType == Float.TYPE) {
						fd.set(obj, Float.parseFloat(value));
					} else if (attrType == Float.class) {
						fd.set(obj, Float.valueOf(value));
					} if (attrType == Double.TYPE) {
						fd.set(obj, Double.parseDouble(value));
					} else if (attrType == Double.class) {
						fd.set(obj, Double.valueOf(value));
					} else {
						fd.set(obj, value);
					}
				}
				args[index] = obj;
			}
			++index;
		}
				
		return args;
	}
	
	

}
