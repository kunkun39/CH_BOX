package com.changhong.thirdpart.sharesdk.util;

import android.content.Context;
import android.text.TextUtils;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.tencent.qzone.QZone;

public class ShareQZone extends ShareCenter{

	public ShareQZone(Context context, PlatformActionListener paListener) {
		super(context, paListener);
	}

	/**
	 * 分享文本
	 * @param title
	 * @param titleUrl 标题的网络链接
	 * @param text
	 * @param site 分享此内容的网站名称，
	 * @param siteUrl 分享此内容的网站地址，
	 */
	public void shareText(String title, String titleUrl, String text,
			String site, String siteUrl) {
		ShareParams params = new ShareParams();
		params.setTitle(title);
		params.setTitleUrl(titleUrl);
		params.setText(text);
		params.setSite(TextUtils.isEmpty(site)?title:site);
		params.setSiteUrl(TextUtils.isEmpty(siteUrl)?titleUrl:siteUrl);
		shareByShareParams(context, params, QZone.NAME, paListener);
	}
	/**
	 * 分享图片
	 * @param title
	 * @param titleUrl 标题的网络链接
	 * @param text
	 * @param imagePath 本地图片地址
	 * @param site 分享此内容的网站名称，
	 * @param siteUrl 分享此内容的网站地址，
	 */
	public void shareImagePath(String title, String titleUrl, String text,String imagePath,
			String site, String siteUrl) {
		ShareParams params = new ShareParams();
		params.setTitle(title);
		params.setTitleUrl(titleUrl);
		params.setImagePath(imagePath);
		params.setText(text);
		params.setSite(TextUtils.isEmpty(site)?title:site);
		params.setSiteUrl(TextUtils.isEmpty(siteUrl)?titleUrl:siteUrl);
		shareByShareParams(context, params, QZone.NAME, paListener);
	}
	/**
	 * 分享图片
	 * @param title
	 * @param titleUrl 标题的网络链接
	 * @param text
	 * @param imagePath 本地图片地址
	 * @param site 分享此内容的网站名称，
	 * @param siteUrl 分享此内容的网站地址，
	 */
	public void shareImageUrl(String title, String titleUrl, String text,String imageUrl,
			String site, String siteUrl) {
		ShareParams params = new ShareParams();
		params.setTitle(title);
		params.setTitleUrl(titleUrl);
		params.setImageUrl(imageUrl);
		params.setText(text);
		params.setSite(TextUtils.isEmpty(site)?title:site);
		params.setSiteUrl(TextUtils.isEmpty(siteUrl)?titleUrl:siteUrl);
		shareByShareParams(context, params, QZone.NAME, paListener);
	}

}
