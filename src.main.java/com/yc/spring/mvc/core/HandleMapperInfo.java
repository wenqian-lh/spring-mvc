/**
 * 
 */
package com.yc.spring.mvc.core;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author lh
 * @data 2021年1月23日
 * Email 2944862497@qq.com
 */
public class HandleMapperInfo {
	
	private Method method; // 处理这个请求的方法
	private Object obj; // 这个方法所属的对象
	private Object[] args; // 这个方法所需要的形参列表
	
	@Override
	public String toString() {
		return "HandleMapperInfo [method=" + method + ", obj=" + obj + ", args=" + Arrays.toString(args) + "]";
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public Object getObj() {
		return obj;
	}
	public void setObj(Object obj) {
		this.obj = obj;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
	/**
	 * 
	 */
	public HandleMapperInfo() {
		super();
	}
	/**
	 * @param method
	 * @param obj
	 * @param args
	 */
	public HandleMapperInfo(Method method, Object obj) {
		super();
		this.method = method;
		this.obj = obj;
		this.args = args;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(args);
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((obj == null) ? 0 : obj.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HandleMapperInfo other = (HandleMapperInfo) obj;
		if (!Arrays.deepEquals(args, other.args))
			return false;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (this.obj == null) {
			if (other.obj != null)
				return false;
		} else if (!this.obj.equals(other.obj))
			return false;
		return true;
	}
	
	

}
