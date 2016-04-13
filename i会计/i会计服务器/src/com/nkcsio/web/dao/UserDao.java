package com.nkcsio.web.dao;

import com.nkcsio.web.po.Company;
import com.nkcsio.web.po.User;

public interface UserDao {
    //检查用户名是否存在
	User checkUsername(String username);
	//插入数据到user表
	boolean insertUser(User user);
	
	//公司名是否存在
	Company checkCompanyname(String company_name);
	//插入数据到company表
	boolean insertCompany(Company company);
	
	//插入数据到user_company表
	boolean insertUserCompany(int user_id,int company_id);
	
	//登录查找用户
	User findUserAfterLogin(String username,String password);
	
	//查找公司名
	String findCompanynameByUserid(int user_id);
}
