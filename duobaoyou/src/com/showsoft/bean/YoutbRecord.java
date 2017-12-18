package com.showsoft.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/1.
 */

public class YoutbRecord implements Serializable{
    private String username;    //投资人
    private String is_auto;    //投标方式    0-手动 1-自动
    private String add_time;    //添加时间
    private String money;    //投资金额

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIs_auto() {
        return is_auto;
    }

    public void setIs_auto(String is_auto) {
        this.is_auto = is_auto;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}
