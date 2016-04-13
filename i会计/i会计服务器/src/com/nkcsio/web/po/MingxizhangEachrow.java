package com.nkcsio.web.po;

import java.util.Date;

public class MingxizhangEachrow {

	private String subject;
	private int subject_number;
	private Date recorddate;
	private int pznumber;
	private String description;
	private String lend_or_load;
	private String direction;
	private float qichu;
	private float amount;
	
	public float getQichu() {
		return qichu;
	}
	public void setQichu(float qichu) {
		this.qichu = qichu;
	}
	public int getSubject_number() {
		return subject_number;
	}
	public void setSubject_number(int subject_number) {
		this.subject_number = subject_number;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Date getRecorddate() {
		return recorddate;
	}
	public void setRecorddate(Date recorddate) {
		this.recorddate = recorddate;
	}
	public int getPznumber() {
		return pznumber;
	}
	public void setPznumber(int pznumber) {
		this.pznumber = pznumber;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLend_or_load() {
		return lend_or_load;
	}
	public void setLend_or_load(String lend_or_load) {
		this.lend_or_load = lend_or_load;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	
}
