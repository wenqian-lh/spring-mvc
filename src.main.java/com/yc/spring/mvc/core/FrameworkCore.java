/**
 * 
 */
package com.yc.spring.mvc.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;

import com.yc.spring.mvc.core.annotation.Autowired;
import com.yc.spring.mvc.core.annotation.Component;
import com.yc.spring.mvc.core.annotation.Controller;
import com.yc.spring.mvc.core.annotation.RequestMapping;

/**
 * @author lh
 * @data 2021年1月23日
 * Email 2944862497@qq.com
 */
public class FrameworkCore {

	private String contextConfigLocation; // 配置文件路径
	private String basePackage; // 要扫描的基址路径
	private Set<String> classNames = new HashSet<String>(); // 扫描获取到的类路径信息
	private Map<String, Object> instanceObject = new HashMap<String, Object>(); // 用来存放需要IoC容器管理实例化的类
	private Map<String, HandleMapperInfo> handlerMapper = new HashMap<String, HandleMapperInfo>(); // url请求地址对应的处理对象

	public static ServletContext servletContext;
		

	public static void main(String[] args) {
		FrameworkCore frameworkCore = new FrameworkCore("application.properties");
	}
	
	public FrameworkCore(String contextConfigLocation) {
		this.contextConfigLocation = contextConfigLocation;
		init(); // 初始化
		
	}

	/**
	 * 初始化
	 */
	private void init() {
		//TODO 1.读取配置文件 -> 获取要扫描的基址路径
		doLoadConfig();
		
		//TODO 2.扫描包，获取类路径
		doScannerPackage();
		
		//TODO 3.初始化需要IoC容器管理的类，并交给IoC容器管理 @Component
		doInstanceObject();
		
		//TODO 4.执行依赖注入，即完成@Autowiredn的解析
		doAutowired();
		
		//TODO 5.构建HandlerMapping, 完成URL与对应方法之间的关联映射@Controller @RequestMapping
		initHandlerMapping();
		
	}
	
	/**
	 * 读取配置文件
	 */
	private void doLoadConfig() {	
		
			// 配置文件是否存在类路径里面，如果是则在类路径下读取，如果不是则在WEB-INF下读取
			if (contextConfigLocation.startsWith("classpath:")) {			
				try(InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation.substring(contextConfigLocation.indexOf(":") + 1))) {
					Properties properties = new Properties();
					properties.load(is);
					basePackage = properties.getProperty("basePackage").trim();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try(InputStream is = servletContext.getResourceAsStream("WEB-INF/" + contextConfigLocation)) {
					Properties properties = new Properties();
					properties.load(is);
					basePackage = properties.getProperty("basePackage");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}


	/**
	 * 扫描包，获取类路径
	 */
	private void doScannerPackage() {
		if (StringUtil.checkNull(basePackage)) {
			throw new RuntimeException("读取配置文件失败，请配置contextConfigLocation参数以及basePackage属性...");
		}
		
		URL url = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));
		
		// 获取指定路径下的所有文件和子目录
		File dist = new File(url.getFile());
		
		getClassInfo(basePackage, dist);
		
	}

	/**
	 * 获取指定路径下的所有文件和子目录
	 * @param basePackage2
	 * @param dist
	 */
	private void getClassInfo(String basePackage, File dist) {
		
		if (dist.exists() && dist.isDirectory()) { // 说明当前文件是一个目录
			for (File fl : dist.listFiles()) {
				if (fl.isDirectory()) {
					getClassInfo(basePackage + "." + fl.getName(), fl);
				} else {
					classNames.add(basePackage + "." + fl.getName().replace(".class", ""));
					System.out.println(basePackage + "." + fl.getName());
				}
			}
		}
		
	}

	/**
	 * 实例化IoC容器管理的对象
	 */
	private void doInstanceObject() {
		if (classNames.isEmpty()) {
			return;
		}
		
		Class<?> cls = null;
		Object instance = null;
		String beanName = null;
		Class<?>[] interfaces = null;
		String temp = null;
		
		for (String className : classNames) {
			try {
				
				cls = Class.forName(className);
				beanName = this.toFirstLowserCase(cls.getSimpleName());
				
				// 判断有没有@Controller注解，说明是控制器
				if (cls.isAnnotationPresent(Controller.class)) {
					temp = cls.getAnnotation(Controller.class).value(); // 有无指定名称
					if (!StringUtil.checkNull(temp)) {
						beanName = temp;
					}
					instanceObject.put(beanName, cls.newInstance());
					// 判断有没有@Component注解，如果有说明需要IoC容器实例化对象
				} else if (cls.isAnnotationPresent(Component.class)) {
					temp = cls.getAnnotation(Component.class).value();
					if (!StringUtil.checkNull(temp)) {
						beanName = temp;
					}
					instance = cls.newInstance();
					instanceObject.put(beanName, cls.newInstance());
					
					// 这个类有实现其他接口吗？
					interfaces = cls.getInterfaces(); // 获取这个类实现的接口
					if (interfaces == null || interfaces.length <= 0) {
						continue;
					}
					
					for (Class<?> its : interfaces) {
						instanceObject.put(its.getSimpleName(), instance);
					}
				}
				
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * 将指定字符串的第一个字符转为小写
	 * @param simpleName
	 * @return
	 */
	private String toFirstLowserCase(String simpleName) {
		char[] arg = simpleName.toCharArray();
		arg[0] += 32;
		return String.valueOf(arg);
	}

	/**
	 * 实现依赖注入
	 */
	private void doAutowired() {
		if (instanceObject.isEmpty()) {
			return;
		}
		
		Field[] fields = null;
		Class<?> cls = null;
		Autowired awd = null;
		String beanName = null;
		
		for(Entry<String, Object> entry : instanceObject.entrySet()) {
			cls = entry.getValue().getClass();
			
			fields = cls.getDeclaredFields();
			if (fields == null || fields.length <= 0) {
				continue;
			}
			
			// 循环所有属性，判断有没有Autowired注解
			for (Field fd: fields) {
				if (!fd.isAnnotationPresent(Autowired.class)) {
					continue;
				}
				
				awd = fd.getAnnotation(Autowired.class);
				beanName = awd.value().trim(); // 获取配置的名字
				
				fd.setAccessible(true); // 俗称强吻，强制设置为可访问状态
				
				if (StringUtil.checkNull(beanName)) { // 说明用户没有指定对应的名字， n那么就根据类型来注值
					beanName = fd.getType().getSimpleName(); // 获取属性的类型名
					
					try {
						// 第一个参数是这个属性是哪个类的，第二个参数是这个属性的值
						fd.set(entry.getValue(), instanceObject.get(beanName));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					
				} else { // 如果指定了名字
					if (!instanceObject.containsKey(beanName)) {
						throw new RuntimeException(cls.getName() + "." + fd.getName() + "注值失败，没有对应的实体类 " + beanName);
					}
					
					try {
						// 第一个参数是这个属性是哪个类的，第二个参数是这个属性的值
						fd.set(entry.getValue(), instanceObject.get(beanName));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}

	/**
	 * 实现请求方法之间的关联映射
	 */
	private void initHandlerMapping() {
		if (instanceObject.isEmpty()) {
			return;
		}
		
		Method[] methods = null;
		Class<?> cls = null;
		RequestMapping requestMapper = null;
		String baseUrl = null;
		String url = null;
		
		for (Entry<String, Object> entry : instanceObject.entrySet()) {
			cls = entry.getValue().getClass();
			
			if (!cls.isAnnotationPresent(Controller.class)) { // 如果不是控制器，则不处理
				continue;
			}
			
			requestMapper = cls.getAnnotation(RequestMapping.class);
			
			if (requestMapper != null) {
				baseUrl = requestMapper.value(); // 获取配置在控制器类上的映射路径
				
				if (!baseUrl.startsWith("/")) {
					baseUrl = "/" + baseUrl;
				}
				
			}
			
			methods = cls.getDeclaredMethods();
			if (methods == null || methods.length <= 0) {
				continue;
			}
			
			for (Method method : methods) {
				if (!method.isAnnotationPresent(RequestMapping.class)) {
					continue;
				}
				
				requestMapper = method.getAnnotation(RequestMapping.class);
				
				url = requestMapper.value();
				
				if (!url.startsWith("/")) {
					url = "/" + url;
				}
				
				url = baseUrl + url;
				
				handlerMapper.put(url.replaceAll("/+", "/"),  new HandleMapperInfo(method, entry.getValue()));
				
			}
		}
		
		
		
	}

	public String getContextConfigLocation() {
		return contextConfigLocation;
	}

	public String getBasePackage() {
		return basePackage;
	}

	public Set<String> getClassNames() {
		return classNames;
	}

	public Map<String, Object> getInstanceObject() {
		return instanceObject;
	}

	public Map<String, HandleMapperInfo> getHandlerMapper() {
		return handlerMapper;
	}

	public HandleMapperInfo getHandlerMapper(String url) {
		return handlerMapper.getOrDefault(url, null);
	}

}
