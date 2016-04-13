package com.nkcsio.android.po;

import java.io.Serializable;

public class Company implements Serializable{

	private static final long serialVersionUID = 1L;
	private int company_id;
	private String company_name;
	private String benweibi;
	
	public int getCompany_id() {
		return company_id;
	}
	public void setCompany_id(int company_id) {
		this.company_id = company_id;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getBenweibi() {
		return benweibi;
	}
	public void setBenweibi(String benweibi) {
		this.benweibi = benweibi;
	}
	
}
