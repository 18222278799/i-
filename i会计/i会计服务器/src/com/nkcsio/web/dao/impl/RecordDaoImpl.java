package com.nkcsio.web.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nkcsio.web.dao.RecordDao;
import com.nkcsio.web.dbutil.DBHelper;
import com.nkcsio.web.po.ChaxunItem;
import com.nkcsio.web.po.Content;
import com.nkcsio.web.po.MingxizhangEachrow;
import com.nkcsio.web.po.Subject;
import com.nkcsio.web.po.User;

public class RecordDaoImpl implements RecordDao {

	@Override
	public List<Subject> findAllSubject(int company_id) {
		String sql = "select * from subject NATURAL join company_subject WHERE company_id = ?";
		DBHelper dbHelper = new DBHelper();
		ResultSet rst = dbHelper.execQuery(sql,company_id);
		List<Subject> allSubject = new ArrayList<Subject>();
		try {
			while(rst.next()) 
			{
				Subject subject = new Subject();
				subject.setSubject_number(rst.getInt(1));
				subject.setSubject_name(rst.getString(2));
				subject.setDirection(rst.getString(3));
				subject.setQichu(rst.getFloat(5));
				allSubject.add(subject);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			dbHelper.closeAll();
		}
		return allSubject;
	}


	@Override
	public boolean insertcompanysubject(int company_id, int subject_number) {
		String sql = "insert into company_subject(company_id,subject_number) values(?,?)";
		DBHelper dbHelper = new DBHelper();
		int n = dbHelper.execOthers(sql, company_id,subject_number);
		dbHelper.closeAll();
		if (n > 0)
			return true;
		return false;
	}
	
	@Override
	public List<MingxizhangEachrow> fillMXZ(int subject_number,String date,int company_id) {
		String sql = "select subject.subject_number,subject_name,collection.date,pingzheng.pz_id,description,lend_or_load,direction,qichu,amount"
                   + " FROM pingzheng NATURAL JOIN collection NATURAL JOIN pingzheng_row NATURAL JOIN pingzhengrow_subject NATURAL JOIN subject NATURAL JOIN company_subject"
				   + " WHERE subject.subject_number=? AND date_format(collection.date,'%Y-%m')=? AND company_id=?";
		DBHelper dbHelper = new DBHelper();
		ResultSet rst = dbHelper.execQuery(sql,subject_number,date,company_id);
		List<MingxizhangEachrow> submxz = new ArrayList<MingxizhangEachrow>();
		try {
			while(rst.next()) 
			{
				MingxizhangEachrow mxzer = new MingxizhangEachrow();
				mxzer.setSubject_number(rst.getInt(1));
				mxzer.setSubject(rst.getString(2));
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date recorddate = null;
				try {
					recorddate = sdf.parse(rst.getString(3));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				mxzer.setRecorddate(recorddate);
				mxzer.setPznumber(rst.getInt(4));
				mxzer.setDescription(rst.getString(5));
				mxzer.setLend_or_load(rst.getString(6));
				mxzer.setDirection(rst.getString(7));
				mxzer.setQichu(rst.getFloat(8));
				mxzer.setAmount(rst.getFloat(9));
				submxz.add(mxzer);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			dbHelper.closeAll();
		}
		return submxz;
	}


	@Override
	public List<MingxizhangEachrow> fillYearMXZ(int subject_number,String date, int company_id) {
		String sql = "select subject.subject_number,subject_name,collection.date,pingzheng.pz_id,description,lend_or_load,direction,qichu,amount"
                + " FROM pingzheng NATURAL JOIN collection NATURAL JOIN pingzheng_row NATURAL JOIN pingzhengrow_subject NATURAL JOIN subject NATURAL JOIN company_subject"
				   + " WHERE subject.subject_number=? AND date_format(collection.date,'%Y')=? AND company_id=?";
		DBHelper dbHelper = new DBHelper();
		ResultSet rst = dbHelper.execQuery(sql,subject_number,date,company_id);
		List<MingxizhangEachrow> submxz = new ArrayList<MingxizhangEachrow>();
		try {
			while(rst.next()) 
			{
				MingxizhangEachrow mxzer = new MingxizhangEachrow();
				mxzer.setSubject_number(rst.getInt(1));
				mxzer.setSubject(rst.getString(2));
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date recorddate = null;
				try {
					recorddate = sdf.parse(rst.getString(3));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				mxzer.setRecorddate(recorddate);
				mxzer.setPznumber(rst.getInt(4));
				mxzer.setDescription(rst.getString(5));
				mxzer.setLend_or_load(rst.getString(6));
				mxzer.setDirection(rst.getString(7));
				mxzer.setQichu(rst.getFloat(8));
				mxzer.setAmount(rst.getFloat(9));
				submxz.add(mxzer);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			dbHelper.closeAll();
		}
		return submxz;
	}


	@Override
	public boolean updateCompanysubject(int company_id, int subject_number,
			float content) {
		String sql = "UPDATE company_subject SET qichu=? WHERE company_id=? AND subject_number=?";
		DBHelper dbHelper = new DBHelper();
		int n = dbHelper.execOthers(sql, content,company_id,subject_number);
		dbHelper.closeAll();
		if (n > 0)
			return true;
		return false;
	}


	@Override
	public String selectCompanysubject(int company_id, int subject_number) {
		String sql = "SELECT qichu FROM company_subject WHERE company_id=? AND subject_number=?";
		DBHelper dbHelper = new DBHelper();
		ResultSet rst = dbHelper.execQuery(sql,company_id,subject_number);
		
		try {
			if(rst.next()) 
			{
				float qichu = rst.getFloat(1);
			    return qichu + "";
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
	public boolean selectpzid(int pz_id, int company_id, String date) {
		String sql = "SELECT * FROM pingzheng WHERE pz_id=? AND date=? AND company_id=? ";
		DBHelper dbHelper = new DBHelper();
		ResultSet rst = dbHelper.execQuery(sql,company_id,date,company_id);
		
		try {
			if(!rst.next()) 
			{
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			dbHelper.closeAll();
		}
		return false;
	}


	@Override
	public boolean insertpz(int pz_id, String date, String username,
			float total_amount, int company_id) {
		String sql = "INSERT INTO pingzheng(pz_id,date,made_by,toal_amount,company_id) VALUES(?,?,?,?,?)";
		DBHelper dbHelper = new DBHelper();
		int n = dbHelper.execOthers(sql, pz_id,date,username,total_amount,company_id);
		dbHelper.closeAll();
		if (n > 0)
			return true;
		return false;
	}

	
	@Override
	public int selectpzr_id(String description, String lend_or_load, float amount) {
		String sql1 = "INSERT INTO pingzheng_row(description,lend_or_load,amount) VALUES(?,?,?)";
		DBHelper dbHelper = new DBHelper();
		int n = dbHelper.execOthers(sql1, description,lend_or_load,amount);
		
		String sql2 = "SELECT LAST_INSERT_ID()";
		ResultSet rst = dbHelper.execQuery(sql2);
		
		try {
			if(rst.next()) 
			{
				return rst.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			dbHelper.closeAll();
		}
		return 0;
	}


	@Override
	public boolean insertcollection(int pzr_id, int pz_id, String date,
			int company_id) {
		String sql = "INSERT INTO collection VALUES(?,?,?,?)";
		DBHelper dbHelper = new DBHelper();
		int n = dbHelper.execOthers(sql, pzr_id,pz_id,date,company_id);
		dbHelper.closeAll();
		if (n > 0)
			return true;
		return false;
	}


	@Override
	public boolean insertpzrsub(int pzr_id, int subject_number) {
		// TODO Auto-generated method stub
		String sql = "INSERT INTO pingzhengrow_subject VALUES(?,?)";
		DBHelper dbHelper = new DBHelper();
		int n = dbHelper.execOthers(sql, pzr_id,subject_number);
		dbHelper.closeAll();
		if (n > 0)
			return true;
		return false;
	}


	@Override
	public List<ChaxunItem> selectChaxun(int company_id, String date) {
		String sql = "SELECT pingzheng.date,pingzheng.pz_id,description,subject_name,lend_or_load,amount,made_by"
                + " FROM pingzheng NATURAL JOIN collection NATURAL JOIN pingzheng_row NATURAL JOIN pingzhengrow_subject NATURAL JOIN subject"
				   + " WHERE pingzheng.company_id=? AND date_format(collection.date,'%Y-%m')=?";
		DBHelper dbHelper = new DBHelper();
		ResultSet rst = dbHelper.execQuery(sql,company_id,date);
		List<ChaxunItem> cxs = new ArrayList<ChaxunItem>();
		try {
			while(rst.next()) 
			{
				ChaxunItem cxi = new ChaxunItem();			
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date recorddate = null;
				try {
					recorddate = sdf.parse(rst.getString(1));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cxi.setDate(recorddate);
				cxi.setPz_id(rst.getInt(2));
				cxi.setDescription(rst.getString(3));
				cxi.setSubject_name(rst.getString(4));
				cxi.setLend_or_load(rst.getString(5));
				cxi.setAmount(Float.parseFloat(rst.getString(6)));
				cxi.setMade_by(rst.getString(7));
				cxs.add(cxi);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			dbHelper.closeAll();
		}
		return cxs;
	}


	
	







	

}
