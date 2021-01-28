/**
 * 
 */
package com.yc.spring.mvc.biz.impl;

import java.util.Map;

import com.yc.spring.mvc.bean.UserInfo;
import com.yc.spring.mvc.biz.IUserInfoBiz;
import com.yc.spring.mvc.core.annotation.Autowired;
import com.yc.spring.mvc.core.annotation.Component;
import com.yc.spring.mvc.dao.IUserInfoDao;

/**
 * @author lh
 * @data 2021年1月23日
 * Email 2944862497@qq.com
 */
@Component
public class UserInfoBizImpl implements IUserInfoBiz{
	
	@Autowired
	private IUserInfoDao userInfoDao;
	
	@Override
	public int add(UserInfo uf) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public UserInfo login(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return null;
	}

}
