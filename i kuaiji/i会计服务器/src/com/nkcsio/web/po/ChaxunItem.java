package com.nkcsio.web.po;

import java.util.Date;

public class ChaxunItem {

	private Date date;
	private int pz_id;
	private String description;
	private String subject_name;
	private String lend_or_load;
	private float amount;
	private String made_by;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getPz_id() {
		return pz_id;
	}
	public void setPz_id(int pz_id) {
		this.pz_id = pz_id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSubject_name() {
		return subject_name;
	}
	public void setSubject_name(String subject_name) {
		this.subject_name = subject_name;
	}
	public String getLend_or_load() {
		return lend_or_load;
	}
	public void setLend_or_load(String lend_or_load) {
		this.lend_or_load = lend_or_load;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	public String getMade_by() {
		return made_by;
	}
	public void setMade_by(String made_by) {
		this.made_by = made_by;
	}

	
}
