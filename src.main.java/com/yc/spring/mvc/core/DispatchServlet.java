/**
 * 
 */
package com.yc.spring.mvc.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yc.spring.mvc.core.annotation.ResponseBody;

/**
 * servlet分发器
 * @author lh
 * @data 2021年1月23日
 * Email 2944862497@qq.com
 */
public class DispatchServlet extends HttpServlet{
	private static final long serialVersionUID = 546934431563072543L;
	
	private String contextConfigLocation = "application.properties";
	private FrameworkCore frameworkCore = null;
	
	
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		
		String temp = config.getInitParameter("contextConfigLocation");
		if (!StringUtil.checkNull(temp)) { // 如果不为空则用用户指定的配置文件 
			contextConfigLocation = temp;
		}
			
		FrameworkCore.servletContext = config.getServletContext();
		
		// 从指定的包开始扫描，根据我们制定的规则，解析所有的注解
		frameworkCore = new FrameworkCore(contextConfigLocation);
	}
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 获取请求地址
		String url = request.getRequestURI();
		System.out.println(url);
		// 获取请求的项目名
		String contextPath = request.getContextPath();
		
		// 获取请求的资源路径
		url = url.replaceFirst(contextPath, "").replaceAll("/+", "/");
		System.out.println(url);
		// 判断请求地址中是否含有参数
		if (url.contains("?")) { // 说明请求地址中有参数
			url = url.substring(0, url.indexOf("?"));
		}
		System.out.println(request.getServletContext().getRealPath("") + url.substring(1));
		// 根据请求路径，和handlerMapper中获取处理的方法
		HandleMapperInfo mapperInfo = frameworkCore.getHandlerMapper(url);
		
		
		// 如果获取不到，则说明没有设置这个请求的处理类,则当成静态资源处理
		if (mapperInfo == null) {
			System.out.println(request.getServletContext().getRealPath("") + url.substring(1));
			HandlerResponse.handlerStaticResource(response, request.getServletContext().getRealPath("") + url.substring(1));
		}	

		
		try {
			
			// 如果有，则需要激活对应的方法来处理
			// 获取处理请求的具体方法
			Method method = mapperInfo.getMethod();
					
			// 获取这个方法的形参列表，然后从请求中获取对应的形参值， 即将这个请求中的参数注入到方法对应的形参中
			Object[] args = HandlerResquest.handle(request, method, response);
			
			// 反向激活这个方法，获取返回值
			Object obj = method.invoke(mapperInfo.getObj(), args);
			
			// 判断返回值以什么格式返回给前端
			// 判断这个方法上面有没有@ResponseBody注解
			if (method.isAnnotationPresent(ResponseBody.class)) {
				HandlerResponse.sendJson(response, obj);
				return;
			}
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}