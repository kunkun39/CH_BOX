package com.changhong.thirdpart.sharesdk.util;

import android.content.Context;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.tencent.weibo.TencentWeibo;

import com.changhong.thirdpart.sharesdk.util.ShareCenter;
/**
 * 腾讯微博分享。微博分享是直接分享，没有编辑页面，如需编辑页面需要自定义。
 * @author wangxiufeng
 *
 */
public class ShareTencentWeiBo extends ShareCenter {

	public ShareTencentWeiBo(Context context) {
		super(context);
	}
	public ShareTencentWeiBo(Context context, PlatformActionListener paListener) {
		super(context, paListener);
	}

	/**
	 * 分享文本
	 * 
	 * @param text
	 */
	public void shareText(String text) {
		ShareParams params = new ShareParams();
		params.setText(text);
		shareByShareParams(context, params, TencentWeibo.NAME, paListener);
	}

	/**
	 * 分享文本
	 * 
	 * @param text
	 * @param latitude
	 *            分享地纬度
	 * @param longitude
	 *            分享地经度，
	 */
	public void shareText(String text, float latitude, float longitude) {
		ShareParams params = new ShareParams();
		params.setText(text);
		params.setLatitude(latitude);
		params.setLongitude(longitude);
		shareByShareParams(context, params, TencentWeibo.NAME, paListener);
	}

	/**
	 * 分享本地图片
	 * 
	 * @param text
	 * @param imagePath
	 */
	public void shareImagePath(String text, String imagePath) {
		ShareParams params = new ShareParams();
		params.setText(text);
		params.setImagePath(imagePath);
		shareByShareParams(context, params, TencentWeibo.NAME, paListener);
	}

	/**
	 * 分享本地图片
	 * 
	 * @param text
	 * @param imagePath
	 * @param latitude
	 *            分享地纬度
	 * @param longitude
	 *            分享地经度，
	 */
	public void shareImagePath(String text, String imagePath, float latitude,
			float longitude) {
		ShareParams params = new ShareParams();
		params.setText(text);
		params.setImagePath(imagePath);
		params.setLatitude(latitude);
		params.setLongitude(longitude);
		shareByShareParams(context, params, TencentWeibo.NAME, paListener);
	}

	/**
	 * 分享多张图片
	 * 
	 * @param text
	 * @param imageArray
	 *            图片数组
	 */
	public void shareImageArray(String text, String[] imageArray) {
		ShareParams params = new ShareParams();
		params.setText(text);
		params.setImageArray(imageArray);
		shareByShareParams(context, params, TencentWeibo.NAME, paListener);
	}

	/**
	 * 分享多张图片
	 * 
	 * @param text
	 * @param imageArray
	 *            图片数组
	 * @param latitude
	 *            分享地纬度
	 * @param longitude
	 *            分享地经度，
	 */
	public void shareImageArray(String text, String[] imageArray,
			float latitude, float longitude) {
		ShareParams params = new ShareParams();
		params.setText(text);
		params.setImageArray(imageArray);
		params.setLatitude(latitude);
		params.setLongitude(longitude);
		shareByShareParams(context, params, TencentWeibo.NAME, paListener);
	}

	/**
	 * 分享网络图片
	 * 
	 * @param text
	 * @param imageUrl
	 */
	public void shareImageUrl(String text, String imageUrl) {
		ShareParams params = new ShareParams();
		params.setText(text);
		params.setImageUrl(imageUrl);
		shareByShareParams(context, params, TencentWeibo.NAME, paListener);
	}

	/**
	 * 分享网络图片
	 * 
	 * @param text
	 * @param imageUrl
	 * @param latitude
	 *            分享地纬度
	 * @param longitude
	 *            分享地经度，
	 */
	public void shareImageUrl(String text, String imageUrl, float latitude,
			float longitude) {
		ShareParams params = new ShareParams();
		params.setText(text);
		params.setImageUrl(imageUrl);
		params.setLatitude(latitude);
		params.setLongitude(longitude);
		shareByShareParams(context, params, TencentWeibo.NAME, paListener);
	}

}
