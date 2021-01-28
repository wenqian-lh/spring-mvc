/**
 * 
 */
package com.yc.spring.mvc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.yc.spring.mvc.bean.UserInfo;
import com.yc.spring.mvc.core.annotation.Controller;
import com.yc.spring.mvc.core.annotation.RequestMapping;
import com.yc.spring.mvc.core.annotation.RequestParam;
import com.yc.spring.mvc.core.annotation.ResponseBody;

/**
 * @author lh
 * @data 2021年1月23日
 * Email 2944862497@qq.com
 */
@Controller
@RequestMapping("/user")
public class UserInfoController {
	
	@RequestMapping("/")
	public String add(UserInfo uf, String info) {
		System.out.println(uf);
		System.out.println(info);
		return "redirect:/show.html";
	}
	
	@RequestMapping("/login")
	@ResponseBody // 说明以Json格式返回数据
	public UserInfo login(UserInfo uf, HttpSession session) {
		
		System.out.println(uf);
		System.out.println(session);
		return uf;
		
	}
	
	@RequestMapping("/finds")
	@ResponseBody
	public List<UserInfo> finds(Map<String, Object> map) {
		System.out.println(map);
		List<UserInfo> list = new ArrayList<UserInfo>();
		return list;
	}
	
	@RequestMapping("/check")
	@ResponseBody
	public UserInfo login(@RequestParam("account")String name, String pwd) {
		System.out.println(name + "   :   " + pwd);
		return null;
	}

}
