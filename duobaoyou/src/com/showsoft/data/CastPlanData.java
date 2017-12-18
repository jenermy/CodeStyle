package com.showsoft.data;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/31.
 */

public class CastPlanData implements Serializable {
    private String b_id;    //优投计划ID
    private String b_title;    //标题
    private String b_fstatus;     //状态  1-筹标中 2-满标 3-放款 4-流标
    private String b_hk_status;    //还款状态  1-正在还款中 2-已还款
    private String b_ftype;    //类型  1-优投宝
    private String b_time_limit;    //借款天数
    private String b_style;    //还款方式
    private String b_account;    //借款总金额
    private String b_account_yes;    //已借到的金额
    private String b_apr;    //利率
    private String b_proportion;    //借款金额与总金额比例

    public String getB_id() {
        return b_id;
    }

    public void setB_id(String b_id) {
        this.b_id = b_id;
    }

    public String getB_title() {
        return b_title;
    }

    public void setB_title(String b_title) {
        this.b_title = b_title;
    }

    public String getB_fstatus() {
        return b_fstatus;
    }

    public void setB_fstatus(String b_fstatus) {
        this.b_fstatus = b_fstatus;
    }

    public String getB_hk_status() {
        return b_hk_status;
    }

    public void setB_hk_status(String b_hk_status) {
        this.b_hk_status = b_hk_status;
    }

    public String getB_ftype() {
        return b_ftype;
    }

    public void setB_ftype(String b_ftype) {
        this.b_ftype = b_ftype;
    }

    public String getB_time_limit() {
        return b_time_limit;
    }

    public void setB_time_limit(String b_time_limit) {
        this.b_time_limit = b_time_limit;
    }

    public String getB_style() {
        return b_style;
    }

    public void setB_style(String b_style) {
        this.b_style = b_style;
    }

    public String getB_account() {
        return b_account;
    }

    public void setB_account(String b_account) {
        this.b_account = b_account;
    }

    public String getB_account_yes() {
        return b_account_yes;
    }

    public void setB_account_yes(String b_account_yes) {
        this.b_account_yes = b_account_yes;
    }

    public String getB_apr() {
        return b_apr;
    }

    public void setB_apr(String b_apr) {
        this.b_apr = b_apr;
    }

    public String getB_proportion() {
        return b_proportion;
    }

    public void setB_proportion(String b_proportion) {
        this.b_proportion = b_proportion;
    }
}
