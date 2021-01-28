/**
 * 
 */
package com.yc.spring.mvc.dao;

import java.util.Map;

import com.yc.spring.mvc.bean.UserInfo;

/**
 * @author lh
 * @data 2021年1月23日
 * Email 2944862497@qq.com
 */
public interface IUserInfoDao {

public int add(UserInfo uf);
	
	public UserInfo login(Map<String, Object> map);
}
