package com.showsoft.bean;

import java.io.Serializable;

public class NoticeBean implements Serializable{
	public String id;
	public String title;
	public String keyword;
	public String desc;//需要解码
	public String author;
	public String thumbnail;
	public String summary;
	public String add_time;
}
