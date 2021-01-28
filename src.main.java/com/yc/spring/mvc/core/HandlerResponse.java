/**
 * 
 */
package com.yc.spring.mvc.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 处理响应数据
 * @author lh
 * @data 2021年1月25日
 * Email 2944862497@qq.com
 */
public class HandlerResponse {
	
	protected static void handlerStaticResource(HttpServletResponse response, String url) throws IOException {
		File fl = new File(url);
		if (!fl.exists() || !fl.isFile()) {
			send404(response, url);
			return;
		}
		
		try(FileInputStream fis = new FileInputStream(fl);) {		
			byte[] bis = new byte[fis.available()];
			fis.read(bis);
			sendData(response, bis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


	protected static void sendData(HttpServletResponse response, byte[] bis) throws IOException {
		ServletOutputStream sos = response.getOutputStream();
		sos.write(bis);
		sos.flush();
		
	}
	
	protected static void sendJson(HttpServletResponse response, Object obj) throws IOException {
		PrintWriter out = response.getWriter();
		Gson gson = new GsonBuilder().serializeNulls().create();
		out.println(gson.toJson(obj));
		out.flush();
	}


	protected static void send404(HttpServletResponse response, String url) throws IOException {
		PrintWriter out = response.getWriter();
		out.println("<h1>HTTP/1.1 404 File Not Found! - " + url + "</h1>");
		out.flush();
		
	}

}
