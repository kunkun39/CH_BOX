package com.changhong.thirdpart.sharesdk.util;

import android.content.Context;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;

import com.changhong.thirdpart.sharesdk.util.ShareCenter;
/**
 * 新浪微博分享。
 * 分享是直接分享，微博分享没有编辑页面，如需编辑页面需要自定义。
 * @author wangxiufeng
 *
 */
public class ShareSinaWeiBo extends ShareCenter {

	public ShareSinaWeiBo(Context context) {
		super(context);
	}
	public ShareSinaWeiBo(Context context, PlatformActionListener paListener) {
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
		shareByShareParams(context, params, SinaWeibo.NAME, paListener);
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
		shareByShareParams(context, params, SinaWeibo.NAME, paListener);
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
		shareByShareParams(context, params, SinaWeibo.NAME, paListener);
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
		shareByShareParams(context, params, SinaWeibo.NAME, paListener);
	}

	/**
	 * 分享网络图片(需要在新浪微博应用审核通过并且官网上面申请高级写入接口权限)
	 * 
	 * @param text
	 * @param imageUrl
	 */
	public void shareImageUrl(String text, String imageUrl) {
		ShareParams params = new ShareParams();
		params.setText(text);
		params.setImageUrl(imageUrl);
		shareByShareParams(context, params, SinaWeibo.NAME, paListener);
	}

	/**
	 * 分享网络图片(需要在新浪微博应用审核通过并且官网上面申请高级写入接口权限)
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
		shareByShareParams(context, params, SinaWeibo.NAME, paListener);
	}

}
