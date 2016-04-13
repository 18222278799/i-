package com.nkcsio.web.dao;

import java.util.Date;
import java.util.List;

import com.nkcsio.web.po.ChaxunItem;
import com.nkcsio.web.po.Content;
import com.nkcsio.web.po.MingxizhangEachrow;
import com.nkcsio.web.po.Subject;

public interface RecordDao {
    
	//找出所有科目
	List<Subject> findAllSubject(int company_id);
	
	//插入company_subject表
	boolean insertcompanysubject(int company_id,int subject_number);
		
	//明细账每行信息
	List<MingxizhangEachrow> fillMXZ(int subject_number,String date,int company_id);
	
	//明细账本年每行信息
	List<MingxizhangEachrow> fillYearMXZ(int subject_number,String date,int company_id);
	
	//更新company_subject表
	boolean updateCompanysubject(int company_id,int subject_number,float content);
	
	//查找company_subject表原值
	String selectCompanysubject(int company_id,int subject_number);
	
	//检测凭证号
	boolean selectpzid(int pz_id, int company_id,String date);
	
	//插入pingzheng表
	boolean insertpz(int pz_id, String date, String username, float total_amount,int company_id);
	
	//查找pzr_id
	int selectpzr_id(String description, String lend_or_load, float amount);
	
	//插入collection表
	boolean insertcollection(int pzr_id,int pz_id,String date,int company_id);
	
	//插入pingzhengrow_subject表
	boolean insertpzrsub(int pzr_id,int subject_number);
	
	//查询凭证
	List<ChaxunItem> selectChaxun(int company_id,String date);
}
