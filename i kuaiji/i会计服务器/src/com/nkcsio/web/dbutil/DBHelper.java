package com.nkcsio.web.dbutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHelper {
	// 声明相关参数
	private String driver;
	private String url;
	private String dbname;
	private String uid;
	private String pwd;
	
	// 声明连接对象
	private Connection con;

	// 构造方法
	public DBHelper() {
		init();
		try {
			// 载入驱动
			Class.forName(driver);
			// 建立连接
			this.con = DriverManager.getConnection(url + dbname, uid, pwd);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// 初始化方法
	private void init(){
		this.driver = DBConfig.driver;
		this.url = DBConfig.url;
		this.dbname = DBConfig.dbname;
		this.uid = DBConfig.uid;
		this.pwd = DBConfig.pwd;
	}

	// 执行查询的方法
	public ResultSet execQuery(final String sql) {
		try {
			Statement stm = con.createStatement();  // 获取执行对象
			return stm.executeQuery(sql);  // 执行并返回结果集
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 执行增删改的方法
	public int execOthers(final String sql) {
		try {
			Statement stm = con.createStatement();
			return stm.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	// 执行查询方法（预处理的，支持sql中包含问号）
	public ResultSet execQuery(final String sql, final Object... params) {
		//Object[] params  对于1.5一下的JDK版本
		try {
			PreparedStatement pstm = con.prepareStatement(sql);  // 获取预处理对象
			// 通过循环替换SQL命令中的问号
			for (int i = 0; i < params.length; i++) {
				pstm.setObject(i + 1, params[i]);
			}
			return pstm.executeQuery();  // 执行查询并返回结果
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 执行增删改（预处理的，支持sql中包含问号）
	public int execOthers(final String sql, final Object... params) {
		try {
			PreparedStatement pstm = con.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				pstm.setObject(i + 1, params[i]);
			}
			return pstm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	// 关闭方法
	public void closeAll() {
		try {
			if (this.con != null && !this.con.isClosed()) {
				try {
					this.con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
