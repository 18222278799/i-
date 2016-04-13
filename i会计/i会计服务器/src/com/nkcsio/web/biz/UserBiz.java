package com.nkcsio.web.biz;

import com.nkcsio.web.po.Company;
import com.nkcsio.web.po.User;

public interface UserBiz {

	//检查用户名是否存在
	User usernameCheck(String username);
	//添加用户
	boolean addUser(User user);
	
	//公司名是否存在
	Company companynameCheck(String company_name);
	//插入数据到company表
	boolean addCompany(Company company);
		
	//插入数据到user_company表
	boolean addUserCompany(int user_id,int company_id);
		
	//登录查找用户
	User selectUser(String username,String password);
	
	//查找公司名
	String selectCompanynameByUserid(int user_id);
}
