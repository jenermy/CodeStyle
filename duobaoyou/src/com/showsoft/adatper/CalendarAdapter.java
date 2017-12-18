package com.showsoft.adatper;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import com.showsoft.duobaoyou.R;
import com.showsoft.utils.FormatUtils;
import com.showsoft.view.LunarCalendar;
import com.showsoft.view.SpecialCalendar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 日历gridview中的每一个item显示的textview
 * @author lmw
 *
 */
public class CalendarAdapter extends BaseAdapter {
	private static final String TAG = "CalendarAdapter";
	private boolean isLeapyear = false;  //是否为闰年
	private int daysOfMonth = 0;      //某月的天数
	private int dayOfWeek = 0;        //具体某一天是星期几
	private int lastDaysOfMonth = 0;  //上一个月的总天数
	private Context context;
	private String[] dayNumber = new String[42];  //一个gridview中的日期存入此数组中
	private SpecialCalendar sc = null;
	private LunarCalendar lc = null; 
	private Resources res = null;
	private FormatUtils formatUtils;
	
	private String currentYear = "";
	private String currentMonth = "";
	private String currentDay = "";
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
	private int currentFlag = -1;     //用于标记当天
	private int[] schDateTagFlag = null;  //存储当月所有的日程日期
	
	private String showYear = "";   //用于在头部显示的年份
	private String showMonth = "";  //用于在头部显示的月份
	private String animalsYear = ""; 
	private String leapMonth = "";   //闰哪一个月
	private String cyclical = "";   //天干地支
	//系统当前时间
	private String sysDate = "";  
	private String sys_year = "";
	private String sys_month = "";
	private String sys_day = "";
	private JSONObject dataObject;

	public void setDataJSONObject(JSONObject dataObject){
		this.dataObject = dataObject;
		Log.d(TAG, dataObject.toString());
	}

	public CalendarAdapter(){
		Date date = new Date();
		sysDate = sdf.format(date);  //当期日期
		sys_year = sysDate.split("-")[0];
		sys_month = sysDate.split("-")[1];
		sys_day = sysDate.split("-")[2];
	}
	
	
	public CalendarAdapter(Context context,Resources rs,int year, int month, int day){
		this();
		this.context= context;
		sc = new SpecialCalendar();
		lc = new LunarCalendar();
		this.res = rs;
		currentYear = String.valueOf(year);;  //得到跳转到的年份
		currentMonth = String.valueOf(month);  //得到跳转到的月份
		currentDay = String.valueOf(day);  //得到跳转到的天
		getCalendar(Integer.parseInt(currentYear),Integer.parseInt(currentMonth));
		sys_day = String.valueOf(day);
		sys_year=String.valueOf(year);
		sys_month=String.valueOf(month);
		sysDate = year+"-"+month+"-"+day;
	}
	
	@Override
	public int getCount() {
		return dayNumber.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(null == convertView){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.calendar_item, null);
			holder.textView1 = (TextView) convertView.findViewById(R.id.tvtext);
			holder.textView2=(TextView) convertView.findViewById(R.id.tvtext2);
			convertView.setTag(holder);
		 }else{
			 holder = (ViewHolder) convertView.getTag();
		 }
		holder.textView1.setText(dayNumber[position]);
		if(null == dataObject){
			return convertView;
		}else{
			//设置数据的背景和金额
			if(!"".equals(dayNumber[position])){//是日期的操作
				try {
					//数据操作
					String dayData = dataObject.getString(dayNumber[position]);
					if(Double.valueOf(dayData)!=0){
						//不为0
						if(Double.valueOf(dayData)>=10000){
							double str = Double.valueOf(dayData)/10000;
							if(formatUtils == null){
								formatUtils = new FormatUtils();
							}
							String m2 = formatUtils.m2(str);
							holder.textView2.setText(m2+"万");
						}else{
							holder.textView2.setText(dayData);
						}
						holder.textView2.setTextColor(Color.parseColor("#ff8d6b"));
						if(currentDay.equals(dayNumber[position])){
							//如果是当天背景为蓝色
							holder.textView1.setTextColor(Color.WHITE);
							holder.textView1.setBackgroundResource(R.drawable.shape_round_blue);
						}else{
							holder.textView1.setBackgroundResource(R.drawable.shape_round_pink);
						}
					}else{
						holder.textView1.setTextColor(Color.parseColor("#292929"));
					}
				} catch (Exception e) {
				}
			}
			return convertView;
		}
	}
	
	private class ViewHolder{
		public TextView textView1;
		public TextView textView2;
	}
	
	//得到某年的某月的天数且这月的第一天是星期几
	public void getCalendar(int year, int month){
		isLeapyear = sc.isLeapYear(year);              //是否为闰年
		daysOfMonth = sc.getDaysOfMonth(isLeapyear, month);  //某月的总天数
		dayOfWeek = sc.getWeekdayOfMonth(year, month);      //某月第一天为星期几
		lastDaysOfMonth = sc.getDaysOfMonth(isLeapyear, month-1);  //上一个月的总天数
		getweek(year,month);
	}
	
	//将一个月中的每一天的值添加入数组dayNuber中
	private void getweek(int year, int month) {
		int j = 1;
		int flag = 0;
		String lunarDay = "";
		
		//得到当前月的所有日程日期(这些日期需要标记)
		for (int i = 0; i < dayNumber.length; i++) {
			// 周一
			 if(i < dayOfWeek){  //前一个月
				int temp = lastDaysOfMonth - dayOfWeek+1;
				lunarDay = lc.getLunarDate(year, month-1, temp+i,false);
//				dayNumber[i] = (temp + i)+"."+lunarDay;
				dayNumber[i] = "";
			}else if(i < daysOfMonth + dayOfWeek){   //本月
				String day = String.valueOf(i-dayOfWeek+1);   //得到的日期
				lunarDay = lc.getLunarDate(year, month, i-dayOfWeek+1,false);
//				dayNumber[i] = i-dayOfWeek+1+"."+lunarDay;
				dayNumber[i] = i-dayOfWeek+1+"";
				//对于当前月才去标记当前日期
				if(sys_year.equals(String.valueOf(year)) && sys_month.equals(String.valueOf(month)) && sys_day.equals(day)){
					//标记当前日期
					currentFlag = i;
				}	
				setShowYear(String.valueOf(year));
				setShowMonth(String.valueOf(month));
				setAnimalsYear(lc.animalsYear(year));
				setLeapMonth(lc.leapMonth == 0?"":String.valueOf(lc.leapMonth));
				setCyclical(lc.cyclical(year));
			}else{   //下一个月
				lunarDay = lc.getLunarDate(year, month+1, j,false);
//				dayNumber[i] = j+"."+lunarDay;
				dayNumber[i] = "";
				j++;
			}
		}
        
        String abc = "";
        for(int i = 0; i < dayNumber.length; i++){
        	 abc = abc+dayNumber[i]+":";
        }
	}
	
	
	public void matchScheduleDate(int year, int month, int day){
		
	}
	/**
	 * 点击每一个item时返回item中的日期
	 * @param position
	 * @return
	 */
	public String getDateByClickItem(int position){
		return dayNumber[position];
	}
	
	/**
	 * 在点击gridView时，得到这个月中第一天的位置
	 * @return
	 */
	public int getStartPositon(){
		return dayOfWeek+7;
	}
	
	/**
	 * 在点击gridView时，得到这个月中最后一天的位置
	 * @return
	 */
	public int getEndPosition(){
		return  (dayOfWeek+daysOfMonth+7)-1;
	}
	
	public String getShowYear() {
		return showYear;
	}

	public void setShowYear(String showYear) {
		this.showYear = showYear;
	}

	public String getShowMonth() {
		return showMonth;
	}

	public void setShowMonth(String showMonth) {
		this.showMonth = showMonth;
	}
	
	public String getAnimalsYear() {
		return animalsYear;
	}

	public void setAnimalsYear(String animalsYear) {
		this.animalsYear = animalsYear;
	}
	
	public String getLeapMonth() {
		return leapMonth;
	}

	public void setLeapMonth(String leapMonth) {
		this.leapMonth = leapMonth;
	}
	
	public String getCyclical() {
		return cyclical;
	}

	public void setCyclical(String cyclical) {
		this.cyclical = cyclical;
	}
}
