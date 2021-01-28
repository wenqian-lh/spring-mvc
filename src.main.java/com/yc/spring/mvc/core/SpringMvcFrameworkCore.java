/**
 * 
 */
package com.yc.spring.mvc.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;

/**
 * @author lh
 * @data 2021年1月24日
 * Email 2944862497@qq.com
 */
public class SpringMvcFrameworkCore {
	
	private String contextConfigLocation; // 配置文件路径
	private String basePackage; // 配置文件中要扫描的基址路径
	private Set<String> classNames = new HashSet<String>(); // 保存扫描获取到的类路径信息
	private Map<String, Object> instanceObject = new HashMap<String, Object>(); // 保存IoC容器实例化的对象
	private Map<String, HandleMapperInfo> handlerMapper = new HashMap<String, HandleMapperInfo>(); // url请求地址与对应的处理对象
	
	public static ServletContext servletContext;
	
	/**
	 * @param contextConfigLocation
	 */
	public SpringMvcFrameworkCore(String contextConfigLocation) {
		this.contextConfigLocation = contextConfigLocation;
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		
		// 1.读取配置文件
		doLoadConfig();
		
		// 2.扫描包，获取类路径
		doScannerPackage();
		
		// 3.实例化需要IoC容器管理的类（带有@Component注解的类），并交给IoC容器管理
		
		// 4.执行依赖注入，将带有@AutoWired的对象完成自动注值
		
		// 5.构建HandlerMapping, 完成URL与对应方法之间的关联映射@Controller @RequestMapping
		
			
	}


	/**
	 * 读取配置文件
	 */
	private void doLoadConfig() {
		
		// 配置文件是否存在类路径里面，如果是则在类路径下读取，如果不是则在WEB-INF下读取
		if (contextConfigLocation.startsWith("classpath:")) {			
			try(InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation.substring(contextConfigLocation.indexOf(":")))) {
				Properties properties = new Properties();
				properties.load(is);
				basePackage = properties.getProperty("basePackage");
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
		if (!StringUtil.checkNull(basePackage)) {
			throw new RuntimeException("读取配置文件失败，请配置contextConfigLocation参数以及basePackage属性...");
		}
		
		URL url = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));
		
		File dist = new File(url.getFile());
		
		getClassInfo(basePackage, dist);
		
	}

	/**
	 *获取指定路径下所有的文件和子目录
	 * @param basePackage2
	 * @param dist
	 */
	private void getClassInfo(String basePackage, File dist) {
		if (dist.exists() && dist.isDirectory()) {
			for (File fl : dist.listFiles()) {
				if (fl.isDirectory()) {
					getClassInfo(basePackage + "." + fl.getName(), fl);
				} else {
					classNames.add(basePackage + "." + fl.getName().replace("class", ""));
				}
			}
		}
		
	}

}
