package com.showsoft.data;

import java.io.Serializable;

public class BorrwData implements Serializable{
	private String id; //标
	private String title;//标题
	private String ftype;//标种类型
	private String fstatus;//状态
	private String time_limit;//标的周期
	private String style;//还款方式
	private String account;//总借款金额
	private String account_yes;//已接到的金额
	private String apr;//年利率
	private String is_day;//是否天标，1，是 0 否
	private String time_limit_day;//标的周期
	private String hk_status;//筹集状态
	private String is_new;//是否是新手表 0否 1是
	private String rewardApr;
	private Object show_activity;//活动标加息

	public Object getShow_activity() {
		return show_activity;
	}

	public void setShow_activity(Object show_activity) {
		this.show_activity = show_activity;
	}

	public void setRewardApr(String rewardApr) {
		this.rewardApr = rewardApr;
	}
	public String getRewardApr() {
		return rewardApr;
	}
	
	public String getIs_new() {
		return is_new;
	}
	
	public void setIs_new(String is_new) {
		this.is_new = is_new;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFtype() {
		return ftype;
	}
	public void setFtype(String ftype) {
		this.ftype = ftype;
	}
	public String getFstatus() {
		return fstatus;
	}
	public void setFstatus(String fstatus) {
		this.fstatus = fstatus;
	}
	public String getTime_limit() {
		return time_limit;
	}
	public void setTime_limit(String time_limit) {
		this.time_limit = time_limit;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getAccount_yes() {
		return account_yes;
	}
	public void setAccount_yes(String account_yes) {
		this.account_yes = account_yes;
	}
	public String getApr() {
		return apr;
	}
	public void setApr(String apr) {
		this.apr = apr;
	}
	public String getIs_day() {
		return is_day;
	}
	public void setIs_day(String is_day) {
		this.is_day = is_day;
	}
	public String getTime_limit_day() {
		return time_limit_day;
	}
	public void setTime_limit_day(String time_limit_day) {
		this.time_limit_day = time_limit_day;
	}
	public String getNk_status() {
		return hk_status;
	}
	public void setNk_status(String hk_status) {
		this.hk_status = hk_status;
	}
}