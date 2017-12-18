package com.showsoft.adatper;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.showsoft.consts.URLS;
import com.showsoft.data.BannerData;
import com.showsoft.duobaoyou.R;
import com.showsoft.duobaoyou.WebActivity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class AdvertiseBannerAdapter extends PagerAdapter {
	List<BannerData> advertiseDatas;
	LayoutInflater inflater;
	private DisplayImageOptions options;
	Context context;

	public AdvertiseBannerAdapter(Context context, List<BannerData> advertiseDatas) {
		this.context = context;
		this.advertiseDatas = advertiseDatas;
		inflater = LayoutInflater.from(context);
		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)// 图像将被二次采样的整数倍
				.considerExifParams(true).build();
	}

	@Override
	public int getCount() {
		if(advertiseDatas == null){
			return 0;
		}else{
			return advertiseDatas.size() == 0 ? 0:Integer.MAX_VALUE;
		}
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		View view = (View) object;
		container.removeView(view);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		final BannerData advertiseData = advertiseDatas.get(position % advertiseDatas.size());
		View view = inflater.inflate(R.layout.vp_banner, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.bannerImageView);
		ImageLoader.getInstance().displayImage(URLS.picUrl + advertiseData.getImg(), imageView,options);
		container.addView(view);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 1:纯链接；2：分类：3：商品详情,4,标签
				//
				String title = advertiseData.getTitle();
				String url = advertiseData.getUrl();
				if(url==null || TextUtils.isEmpty(url)){
					return;
				}
				Intent intent = new Intent(context,WebActivity.class);	
				intent.putExtra("url", url);
				intent.putExtra("title", title);
				context.startActivity(intent);
			}
		});
		return view;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return super.getPageTitle(position);
	}
}
