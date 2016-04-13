package com.nkcsio.web.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.nkcsio.web.dao.UserDao;
import com.nkcsio.web.dbutil.DBHelper;
import com.nkcsio.web.po.Company;
import com.nkcsio.web.po.User;

public class UserDaoImpl implements UserDao {

	@Override
	public User checkUsername(String username) {
		String sql = "select * from user where username=?";
		DBHelper dbHelper = new DBHelper();
		ResultSet rst = dbHelper.execQuery(sql, username);
		try {
			if(rst.next()) 
			{
				User user = new User();
				user.setUser_id(rst.getInt(1));
				user.setUsername(rst.getString(2));
				user.setPassword(rst.getString(3));
				user.setReal_username(rst.getString(4));
				user.setIdentity_number(rst.getString(5));
				user.setJob(rst.getString(6));
				user.setQuestion(rst.getString(7));
				user.setAnswer(rst.getString(8));
				rst.close();
				return user;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			dbHelper.closeAll();
		}
		return null;
	}
	@Override
	public boolean insertUser(User user) {
		String sql = "insert into user(username,password,realusername,identity_number,job,question,answer) values(?,?,?,?,?,?,?)";
		DBHelper dbHelper = new DBHelper();
		int n = dbHelper.execOthers(sql, user.getUsername(),user.getPassword(),user.getReal_username(),user.getIdentity_number(),
				user.getJob(),user.getQuestion(),user.getAnswer());
		dbHelper.closeAll();
		if (n > 0)
			return true;
		return false;
		
	}
	
	@Override
	public Company checkCompanyname(String company_name) {
		String sql = "select * from company where company_name=?";
		DBHelper dbHelper = new DBHelper();
		ResultSet rst = dbHelper.execQuery(sql, company_name);
		try {
			if(rst.next())
			{
				Company company = new Company();
				company.setCompany_id(rst.getInt(1));
				company.setCompany_name(rst.getString(2));
				company.setBenweibi(rst.getString(3));
				rst.close();
				return company;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			dbHelper.closeAll();
		}
		return null;
	}
	@Override
	public boolean insertCompany(Company company) {
		String sql = "insert into company(company_name,benweibi) values(?,?)";
		DBHelper dbHelper = new DBHelper();
		int n = dbHelper.execOthers(sql, company.getCompany_name(),company.getBenweibi());
		dbHelper.closeAll();
		if (n > 0)
			return true;
		return false;
	}
	@Override
	public boolean insertUserCompany(int user_id, int company_id) {
		String sql = "insert into user_company values(?,?)";
		DBHelper dbHelper = new DBHelper();
		int n = dbHelper.execOthers(sql, user_id,company_id);
		dbHelper.closeAll();
		if (n > 0)
			return true;
		return false;
	}

	@Override
	public User findUserAfterLogin(String username, String password) {
		String sql = "select * from user where username=? and password=?";
		DBHelper dbHelper = new DBHelper();
		ResultSet rst = dbHelper.execQuery(sql, username, password);
		try {
			if(rst.next()){
				// 提取用户信息
				User user = new User();
				user.setUser_id(rst.getInt(1));
				user.setUsername(rst.getString(2));
				user.setPassword(rst.getString(3));
				user.setReal_username(rst.getString(4));
				user.setIdentity_number(rst.getString(5));
				user.setJob(rst.getString(6));
				user.setQuestion(rst.getString(7));
				user.setAnswer(rst.getString(8));
				return user;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			dbHelper.closeAll();
		}
		return null;
	}
	@Override
	public String findCompanynameByUserid(int user_id) {
		String sql = "SELECT company_name FROM user_company NATURAL JOIN company WHERE user_company.user_id=?";
		DBHelper dbHelper = new DBHelper();
		ResultSet rst = dbHelper.execQuery(sql, user_id);
		try {
			if(rst.next()){
				// 提取用户信息
				return rst.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			dbHelper.closeAll();
		}
		return null;
	}
}
