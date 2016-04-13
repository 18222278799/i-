package com.nkcsio.web.biz.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nkcsio.web.biz.RecordBiz;
import com.nkcsio.web.dao.RecordDao;
import com.nkcsio.web.dao.impl.RecordDaoImpl;
import com.nkcsio.web.po.ChaxunItem;
import com.nkcsio.web.po.Content;
import com.nkcsio.web.po.MingxizhangEachrow;
import com.nkcsio.web.po.Subject;

public class RecordBizImpl implements RecordBiz {

	private RecordDao recordDao = new RecordDaoImpl();
	
	@Override
	public List<Subject> getAllSubject(int company_id) {
		// TODO Auto-generated method stub
		return recordDao.findAllSubject(company_id);
	}

	@Override
	public boolean addcompanysubject(int company_id) {
		List<Subject> subjects = getAllSubject(company_id);
		for(Subject subject : subjects)
		{
			recordDao.insertcompanysubject(company_id, subject.getSubject_number());
		}
		return true;
	}

	@Override
	public List<MingxizhangEachrow> filloutMXZ(String date, int company_id) {
		List<Subject> subjects = getAllSubject(company_id);
		List<MingxizhangEachrow> mingxizhang = new ArrayList<MingxizhangEachrow>();
		for(Subject subject : subjects)
		{
			//期初
			MingxizhangEachrow mxzerqichu = new MingxizhangEachrow();
			mxzerqichu.setSubject_number(subject.getSubject_number());
			mxzerqichu.setSubject(subject.getSubject_name());
			
			SimpleDateFormat sdfqichu = new SimpleDateFormat("yyyy-MM-dd");
			Date recorddateqichu = null;
			try {
				recorddateqichu = sdfqichu.parse(date + "-01");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mxzerqichu.setRecorddate(recorddateqichu);
			mxzerqichu.setDescription("期初余额");
			
			mxzerqichu.setLend_or_load("lendandload");
			mxzerqichu.setQichu(subject.getQichu());
			mxzerqichu.setAmount(0);
			mxzerqichu.setDirection(subject.getDirection());
			mingxizhang.add(mxzerqichu);
			
			List<MingxizhangEachrow> submxz = new ArrayList<MingxizhangEachrow>();
			submxz = recordDao.fillMXZ(subject.getSubject_number(), date, company_id);
			if(submxz != null && submxz.size() > 0)
			{
				MingxizhangEachrow mxzer = new MingxizhangEachrow();
				mxzer.setSubject_number(submxz.get(0).getSubject_number());
				mxzer.setSubject(submxz.get(0).getSubject());
				mxzer.setRecorddate(submxz.get(0).getRecorddate());
				mxzer.setDescription("本期合计");
				float lendamount = 0;
				float loadamount = 0;
				for(MingxizhangEachrow submxzer : submxz)
				{
					if("lend".equals(submxzer.getLend_or_load()))
					{
						lendamount += submxzer.getAmount();
					}
					else if("load".equals(submxzer.getLend_or_load()))
					{
						loadamount += submxzer.getAmount();
					}
				}
				mxzer.setLend_or_load("lendandload");
				mxzer.setQichu(lendamount);
				mxzer.setAmount(loadamount);
				mxzer.setDirection(submxz.get(0).getDirection());
				submxz.add(mxzer);
				mingxizhang.addAll(submxz);
				
				
			}
			else
			{///空的话加上空的累计
				MingxizhangEachrow mxzer = new MingxizhangEachrow();
				mxzer.setSubject_number(subject.getSubject_number());
				mxzer.setSubject(subject.getSubject_name());
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date recorddate = null;
				try {
					recorddate = sdf.parse(date + "-01");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mxzer.setRecorddate(recorddate);
				mxzer.setDescription("本期合计");
				float lendamount = 0;
				float loadamount = 0;
				mxzer.setLend_or_load("lendandload");
				mxzer.setQichu(lendamount);
				mxzer.setAmount(loadamount);
				mxzer.setDirection(subject.getDirection());
				submxz.add(mxzer);
				mingxizhang.addAll(submxz);
			}
			///本年累计
			List<MingxizhangEachrow> subYearmxz = new ArrayList<MingxizhangEachrow>();
			String yeardate = date.substring(0, 4);
			subYearmxz = recordDao.fillYearMXZ(subject.getSubject_number(), yeardate, company_id);
			if(subYearmxz != null && subYearmxz.size() > 0)
			{
				MingxizhangEachrow mxzer = new MingxizhangEachrow();
				mxzer.setSubject_number(subYearmxz.get(0).getSubject_number());
				mxzer.setSubject(subYearmxz.get(0).getSubject());
				mxzer.setRecorddate(subYearmxz.get(subYearmxz.size() - 1).getRecorddate());
				mxzer.setDescription("本年累计");
				float lendamount = 0;
				float loadamount = 0;
				for(MingxizhangEachrow submxzer : subYearmxz)
				{
					if("lend".equals(submxzer.getLend_or_load()))
					{
						lendamount += submxzer.getAmount();
					}
					else if("load".equals(submxzer.getLend_or_load()))
					{
						loadamount += submxzer.getAmount();
					}
				}
				mxzer.setLend_or_load("lendandload");
				mxzer.setQichu(lendamount);
				mxzer.setAmount(loadamount);
				mxzer.setDirection(subYearmxz.get(0).getDirection());
				subYearmxz.clear();
				subYearmxz.add(mxzer);
				mingxizhang.addAll(subYearmxz);				
			}
			else
			{///空的话加上空的累计
				MingxizhangEachrow mxzer = new MingxizhangEachrow();
				mxzer.setSubject_number(subject.getSubject_number());
				mxzer.setSubject(subject.getSubject_name());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date recorddate = null;
				try {
					recorddate = sdf.parse(yeardate + "-12-31");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mxzer.setRecorddate(recorddate);
				mxzer.setDescription("本年累计");
				float lendamount = 0;
				float loadamount = 0;
				mxzer.setLend_or_load("lendandload");
				mxzer.setQichu(lendamount);
				mxzer.setAmount(loadamount);
				mxzer.setDirection(subject.getDirection());
				subYearmxz.clear();
				subYearmxz.add(mxzer);
				mingxizhang.addAll(subYearmxz);	
			}
		}
		return mingxizhang;
	}

	@Override
	public boolean modifyCompanysubject(int company_id, int subject_number,
			float content) {
		return recordDao.updateCompanysubject(company_id, subject_number, content);
	}

	@Override
	public ArrayList<Content> findCompanysubject(ArrayList<Content> oldtests,int company_id) {
		ArrayList<Content> newtests= new ArrayList<Content>();
		for(Content eachcontent : oldtests)
		{
			int subject_number = Integer.parseInt(eachcontent.getContent1());
			eachcontent.setContent4(recordDao.selectCompanysubject(company_id, subject_number));
			newtests.add(eachcontent);
		}
		return newtests;
	}

	@Override
	public boolean checkpzid(int pz_id,int company_id, String date) {
		// TODO Auto-generated method stub
		return recordDao.selectpzid(pz_id, company_id, date);
	}

	@Override
	public boolean addpz(int pz_id, String date, String username,
			float total_amount, int company_id) {
		// TODO Auto-generated method stub
		return recordDao.insertpz(pz_id, date, username, total_amount, company_id);
	}

	@Override
	public int addpzr(String description, String lend_or_load, float amount) {
		// TODO Auto-generated method stub
		//recordDao.insertpzr(description, lend_or_load, amount);
		return recordDao.selectpzr_id(description, lend_or_load, amount);
	}

	@Override
	public boolean addcollection(int pzr_id, int pz_id, String date,
			int company_id) {
		// TODO Auto-generated method stub
		return recordDao.insertcollection(pzr_id, pz_id, date, company_id);
	}

	@Override
	public boolean addpzrsub(int pzr_id, int subject_number) {
		// TODO Auto-generated method stub
		return recordDao.insertpzrsub(pzr_id, subject_number);
	}

	@Override
	public List<ChaxunItem> getChaxun(int company_id, String date) {
		// TODO Auto-generated method stub
		return recordDao.selectChaxun(company_id, date);
	}



}
