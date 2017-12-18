package com.showsoft.consts;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.showsoft.bean.RechargeBean;

import android.content.Context;
import android.widget.Toast;

public class JsonUtils {
	static Gson gson = new Gson();
	/**
	 * 返回需要解析的数据
	 * @param context
	 * @param result
	 * @param key
	 * @return
	 */
	public static String getResult(Context context , String result,String key){
		try {
			RechargeBean bean = gson.fromJson(result, RechargeBean.class);
			if(bean.getErrorCode() == 0){
				return new JSONObject(result).getString(key);
			}else{
				Toast.makeText(context, bean.getErrorMess(), Toast.LENGTH_SHORT).show();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "数据异常", Toast.LENGTH_SHORT).show();
			return null;
		}
	}
	/**
	 * 返回需要解析的数据
	 * @param context
	 * @param result
	 * @return
	 */
	public static String getResult(Context context , String result){
		return getResult(context, result, "results");
	}
}
