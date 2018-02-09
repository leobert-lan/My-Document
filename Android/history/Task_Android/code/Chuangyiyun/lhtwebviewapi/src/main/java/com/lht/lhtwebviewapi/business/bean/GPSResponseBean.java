package com.lht.lhtwebviewapi.business.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

import com.lht.lhtwebviewapi.business.API.NativeRet;

/**
 * @ClassName: GPSResponseBean
 * @Description: TODO
 * @date 2016年2月22日 下午2:47:03
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class GPSResponseBean implements NativeRet.NativeGpsRet {

	/**
	 * latitude:纬度
	 */
	private String latitude;

	/**
	 * longitude:经度
	 */
	private String longitude;

	/**
	 * time:生成时间 毫秒级时间戳
	 */
	private String time;

	/**
	 * radius:定位精度
	 */
	private String radius;

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = transferStringDateT2Stamp("yyyy-MM-dd HH:mm:ss", time);
	}

	public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}

	@SuppressLint("SimpleDateFormat")
	private static String transferStringDateT2Stamp(String formatDate,
			String date) {
		SimpleDateFormat sdf = new SimpleDateFormat(formatDate);
		Date dt;
		try {
			dt = sdf.parse(date);
			return String.valueOf(dt.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return date;
		}

	}

}
