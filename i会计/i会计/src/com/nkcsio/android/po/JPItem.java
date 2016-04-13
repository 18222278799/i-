package com.nkcsio.android.po;

public class JPItem {
	public final static boolean JIE=true;
	public final static boolean DAI=false;
	private String strZhaiyao;
	private int idKemu;
	private double moneyCount;
	private boolean jie_dai;
	public String getStrZhaiyao() {
		return strZhaiyao;
	}
	public void setStrZhaiyao(String strZhaiyao) {
		this.strZhaiyao = strZhaiyao;
	}
	public int getIdKemu() {
		return idKemu;
	}
	public void setIdKemu(int idKemu) {
		this.idKemu = idKemu;
	}
	public double getMoneyCount() {
		return moneyCount;
	}
	public void setMoneyCount(double moneyCount) {
		this.moneyCount = moneyCount;
	}
	public boolean isJie_dai() {
		return jie_dai;
	}
	public void setJie_dai(boolean jie_dai) {
		this.jie_dai = jie_dai;
	}
	public boolean isJie(){
		return moneyCount==0?false:jie_dai==JIE;
	}
	public boolean isDai(){
		return moneyCount==0?false:jie_dai==DAI;
	}
}
