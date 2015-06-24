package com.changhong.thirdpart.sharesdk.util;

import android.content.Context;

import com.changhong.thirdpart.sharesdk.util.ShareCenter;

import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.tencent.qq.QQ;

public class ShareQQ extends ShareCenter{
	// /** 标题 */
	// public String title;
	// /** 跳转地址，不能为空 **/
	// public String titleUrl;
	// /** 文本内容 */
	// public String text;
	// /** 图片本地路径 */
	// public String imagePath;
	// /** 图片网络路径路径 */
	// public String imageUrl;
	// /** 音乐地址（可选参数） */
	// public String musicUrl;

	public ShareQQ(Context context, PlatformActionListener paListener) {
		super(context, paListener);
	}

	/**
	 * 分享图片
	 * 
	 * @param title
	 * @param titleUrl
	 *            标题链接地址（不能为空）
	 * @param text
	 * @param imagePath
	 * 
	 */
	public void shareImgByPath(String title, String titleUrl, String text,
			String imagePath) {
		ShareParams params = new ShareParams();
		params.setTitle(title);
		params.setTitleUrl(titleUrl);
		params.setText(text);
		params.setImagePath(imagePath);
		shareByShareParams(context, params, QQ.NAME, paListener);
	}

	/**
	 * 分享网络图片
	 * 
	 * @param title
	 * @param titleUrl
	 *            标题链接地址（不能为空）
	 * @param text
	 * @param imageUrl
	 */
	public void shareImgByUrl(String title, String titleUrl,
			String text, String imageUrl) {
		ShareParams params = new ShareParams();
		params.setTitle(title);
		params.setTitleUrl(titleUrl);
		params.setText(text);
		params.setImageUrl(imageUrl);
		shareByShareParams(context, params, QQ.NAME, paListener);
	}

	/**
	 * 分享音乐
	 * 
	 * @param title
	 * @param titleUrl
	 *            标题链接地址（不能为空）
	 * @param text
	 * @param imagePath
	 * 
	 * @param musicUrl
	 *            音乐地址
	 */
	public void shareMusicWithImgPath(String title, String titleUrl,
			String text, String imagePath, String musicUrl) {
		ShareParams params = new ShareParams();
		params.setTitle(title);
		params.setTitleUrl(titleUrl);
		params.setText(text);
		params.setImagePath(imagePath);
		params.setMusicUrl(musicUrl);
		shareByShareParams(context, params, QQ.NAME, paListener);
	}

	/**
	 * 分享音乐
	 * 
	 * @param title
	 * @param titleUrl
	 *            标题链接地址（不能为空）
	 * @param text
	 * @param imagePath
	 * 
	 * @param musicUrl
	 *            音乐地址
	 */
	public void shareMusicWithImgurl(String title, String titleUrl,
			String text, String imageUrl, String musicUrl) {
		ShareParams params = new ShareParams();
		params.setTitle(title);
		params.setTitleUrl(titleUrl);
		params.setText(text);
		params.setImageUrl(imageUrl);
		params.setMusicUrl(musicUrl);
		shareByShareParams(context, params, QQ.NAME, paListener);
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public PlatformActionListener getPaListener() {
		return paListener;
	}

	public void setPaListener(PlatformActionListener paListener) {
		this.paListener = paListener;
	}

}
