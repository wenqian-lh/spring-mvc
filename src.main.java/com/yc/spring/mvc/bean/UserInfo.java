/**
 * 
 */
package com.yc.spring.mvc.bean;

import java.io.Serializable;

/**
 * @author lh
 * @data 2021年1月10日
 * Email 2944862497@qq.com
 */
public class UserInfo implements Serializable{

	private static final long serialVersionUID = 3651129929664482472L;
	private String uname;
	private String pwd;
	private String usid;
	
	@Override
	public String toString() {
		return "UserInfo [usid=" + usid + ", uname=" + uname + ", pwd=" + pwd + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pwd == null) ? 0 : pwd.hashCode());
		result = prime * result + ((uname == null) ? 0 : uname.hashCode());
		result = prime * result + ((usid == null) ? 0 : usid.hashCode());
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
		UserInfo other = (UserInfo) obj;
		if (pwd == null) {
			if (other.pwd != null)
				return false;
		} else if (!pwd.equals(other.pwd))
			return false;
		if (uname == null) {
			if (other.uname != null)
				return false;
		} else if (!uname.equals(other.uname))
			return false;
		if (usid == null) {
			if (other.usid != null)
				return false;
		} else if (!usid.equals(other.usid))
			return false;
		return true;
	}
	public String getUsid() {
		return usid;
	}
	public void setUsid(String usid) {
		this.usid = usid;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	/**
	 * 
	 */
	public UserInfo() {
		super();
	}
	/**
	 * @param usid
	 * @param uname
	 * @param pwd
	 */
	public UserInfo(String usid, String uname, String pwd) {
		super();
		this.usid = usid;
		this.uname = uname;
		this.pwd = pwd;
	}
	
	
	
}
