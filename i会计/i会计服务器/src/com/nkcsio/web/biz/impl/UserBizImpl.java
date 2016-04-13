package com.nkcsio.web.biz.impl;

import com.nkcsio.web.biz.UserBiz;
import com.nkcsio.web.dao.UserDao;
import com.nkcsio.web.dao.impl.UserDaoImpl;
import com.nkcsio.web.po.Company;
import com.nkcsio.web.po.User;

public class UserBizImpl implements UserBiz{

	private UserDao userDao = new UserDaoImpl();

	@Override
	public User usernameCheck(String username) {
		return userDao.checkUsername(username);
	}

	@Override
	public boolean addUser(User user) {
		return userDao.insertUser(user);
	}	

	@Override
	public Company companynameCheck(String company_name) {
		// TODO Auto-generated method stub
		return userDao.checkCompanyname(company_name);
	}

	@Override
	public boolean addCompany(Company company) {
		// TODO Auto-generated method stub
		return userDao.insertCompany(company);
	}

	@Override
	public boolean addUserCompany(int user_id, int company_id) {
		// TODO Auto-generated method stub
		return userDao.insertUserCompany(user_id, company_id);
	}
	
	@Override
	public User selectUser(String username, String password) {
		// TODO Auto-generated method stub
		return userDao.findUserAfterLogin(username, password);
	}

	@Override
	public String selectCompanynameByUserid(int user_id) {
		// TODO Auto-generated method stub
		return userDao.findCompanynameByUserid(user_id);
	}
	
}
