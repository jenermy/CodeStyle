package com.showsoft.data;

import org.json.JSONObject;

import java.io.Serializable;

public class BorrowDetailData implements Serializable {
	private String id; // 标
	private String title;// 标题
	private String ftype;// 标种类型
	private String fstatus;// 状态
	private String time_limit;// 标的周期
	private String style;// 还款方式
	private String account;// 总借款金额
	private String account_yes;// 已接到的金额
	private String apr;// 年利率
	private String is_day;// 是否天标，1，是 0 否
	private String time_limit_day;// 标的周期
	private String dxb_pwd;// 定向标密码
	private String content;
	private String lowest_account;// 最少投标
	private String most_account;
	private String start_time;
	private String hukou;
	private String jiehun;
	private String fangchan;
	private String shiming;
	private String hk_status;
	private String repay_from;
	private String risk_manage;
	private String business;
	private String is_new;
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

	public void setIs_new(String is_new) {
		this.is_new = is_new;
	}
	public String getIs_new() {
		return is_new;
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
	// public String getNk_status() {
	// return hk_status;
	// }
	// public void setNk_status(String nk_status) {
	// this.hk_status = nk_status;
	// }
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getLowest_account() {
		return lowest_account;
	}
	public void setLowest_account(String lowest_account) {
		this.lowest_account = lowest_account;
	}
	public String getMost_account() {
		return most_account;
	}
	public void setMost_account(String most_account) {
		this.most_account = most_account;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getHukou() {
		return hukou;
	}
	public void setHukou(String hukou) {
		this.hukou = hukou;
	}
	public String getJiehun() {
		return jiehun;
	}
	public void setJiehun(String jiehun) {
		this.jiehun = jiehun;
	}
	public String getFangchan() {
		return fangchan;
	}
	public void setFangchan(String fangchan) {
		this.fangchan = fangchan;
	}
	public String getShiming() {
		return shiming;
	}
	public void setShiming(String shiming) {
		this.shiming = shiming;
	}
	public String getHk_status() {
		return hk_status;
	}
	public void setHk_status(String hk_status) {
		this.hk_status = hk_status;
	}
	public String getRepay_from() {
		return repay_from;
	}
	public void setRepay_from(String repay_from) {
		this.repay_from = repay_from;
	}
	public String getRisk_manage() {
		return risk_manage;
	}
	public void setRisk_manage(String risk_manage) {
		this.risk_manage = risk_manage;
	}
	public String getBusiness() {
		return business;
	}
	public void setBusiness(String business) {
		this.business = business;
	}
	public String getDxb_pwd() {
		return dxb_pwd;
	}
	public void setDxb_pwd(String dxb_pwd) {
		this.dxb_pwd = dxb_pwd;
	}
	@Override
	public String toString() {
		return "BorrowDetailData [id=" + id + ", title=" + title + ", ftype=" + ftype + ", fstatus=" + fstatus + ", time_limit=" + time_limit + ", style=" + style + ", account=" + account + ", account_yes=" + account_yes + ", apr=" + apr + ", is_day=" + is_day + ", time_limit_day=" + time_limit_day
				+ ", content=" + content + ", lowest_account=" + lowest_account + ", most_account=" + most_account + ", start_time=" + start_time + ", hukou=" + hukou + ", jiehun=" + jiehun + ", fangchan=" + fangchan + ", shiming=" + shiming + ", hk_status=" + hk_status + ", repay_from="
				+ repay_from + ", risk_manage=" + risk_manage+ ", show_activity=" + show_activity + ", business=" + business + "]";
	}
}
