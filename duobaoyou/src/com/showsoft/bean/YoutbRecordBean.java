package com.showsoft.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/11/1.
 */

public class YoutbRecordBean {
    public String errorCode;
    public String errorMess;
    public RecordsBean results;
    public class RecordsBean{
        public String count;
        public List<YoutbRecord> lists;
    }
}
