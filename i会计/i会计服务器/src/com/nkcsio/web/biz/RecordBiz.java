package com.nkcsio.web.biz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nkcsio.web.po.ChaxunItem;
import com.nkcsio.web.po.Content;
import com.nkcsio.web.po.MingxizhangEachrow;
import com.nkcsio.web.po.Subject;

public interface RecordBiz {

	//找出所有科目
    List<Subject> getAllSubject(int company_id);
		
	//插入company_subject表
	boolean addcompanysubject(int company_id);
			
	//明细账每行信息
	List<MingxizhangEachrow> filloutMXZ(String date, int company_id);
	
	//更新company_subject表
	boolean modifyCompanysubject(int company_id,int subject_number,float content);
	
	//查找company_subject表原值
	ArrayList<Content> findCompanysubject(ArrayList<Content> oldtests,int company_id);
	
	//检测凭证号
	boolean checkpzid(int pz_id,int company_id,String date);
		
	//插入pingzheng表
	boolean addpz(int pz_id, String date, String username, float total_amount,int company_id);
		
	//插入pingzheng_row表
	int addpzr(String description,String lend_or_load,float amount);
		
	//插入collection表
	boolean addcollection(int pzr_id,int pz_id,String date,int company_id);
		
	//插入pingzhengrow_subject表
	boolean addpzrsub(int pzr_id,int subject_number);
	
	//查询凭证
	List<ChaxunItem> getChaxun(int company_id,String date);
}
