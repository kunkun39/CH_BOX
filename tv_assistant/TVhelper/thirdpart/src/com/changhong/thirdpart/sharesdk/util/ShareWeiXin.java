package com.changhong.thirdpart.sharesdk.util;

import android.content.Context;
import android.graphics.Bitmap;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.changhong.thirdpart.sharesdk.util.ShareCenter;

public class ShareWeiXin extends ShareCenter {

	/** 微信分享对象：微信好友，微信朋友圈，微信收藏 **/
	public enum WeiXin {
		Wechat, WechatMoments, WechatFavorite
	}

	public ShareWeiXin(Context context, PlatformActionListener paListener) {
		super(context, paListener);
	}

	/**
	 * 分享文本
	 * 
	 * @param weiXin
	 *            微信平台选择：微信好友，微信朋友圈，微信收藏
	 * @param title
	 * @param text
	 */
	public void shareText(WeiXin weiXin, String title, String text) {
		ShareParams params = new ShareParams();
		params.setTitle(title);
		params.setText(text);
		params.setShareType(Wechat.SHARE_TEXT);
		shareByShareParams(context, params, getName(weiXin), paListener);
	}

	/**
	 * 分享本地图片
	 * 
	 * @param weiXin
	 *            微信平台选择：微信好友，微信朋友圈，微信收藏
	 * @param title
	 * @param text
	 *            可选参数
	 * @param imagePath
	 */
	public void shareImageByPath(WeiXin weiXin, String title, String text,
			String imagePath) {
		ShareParams params = new ShareParams();
		params.setTitle(title);
		params.setText(text);
		params.setShareType(Wechat.SHARE_IMAGE);
		params.setImagePath(imagePath);
		shareByShareParams(context, params, getName(weiXin), paListener);
	}

	/**
	 * 分享图片
	 * 
	 * @param weiXin
	 *            微信平台选择：微信好友，微信朋友圈，微信收藏
	 * @param title
	 * @param text
	 *            可选参数
	 * @param imageData
	 */
	public void shareImageByData(WeiXin weiXin, String title, String text,
			Bitmap imageData) {
		ShareParams params = new ShareParams();
		params.setTitle(title);
		params.setText(text);
		params.setImageData(imageData);
		params.setShareType(Wechat.SHARE_IMAGE);
		shareByShareParams(context, params, getName(weiXin), paListener);
	}

	/**
	 * 分享网络图片
	 * 
	 * @param weiXin
	 *            微信平台选择：微信好友，微信朋友圈，微信收藏
	 * @param title
	 * @param text
	 *            可选参数
	 * @param imageUrl
	 */
	public void shareImageByUrl(WeiXin weiXin, String title, String text,
			String imageUrl) {
		ShareParams params = new ShareParams();
		params.setTitle(title);
		params.setText(text);
		params.setImageUrl(imageUrl);
		params.setShareType(Wechat.SHARE_IMAGE);
		shareByShareParams(context, params, getName(weiXin), paListener);
	}

	/**
	 * 分享表情(只支持分享给好友不支持朋友圈和收藏)
	 * 
	 * @param title
	 * @param text
	 *            可选参数
	 * @param imageData
	 */
	public void shareEmoji(String title, String text, Bitmap imageData) {
		ShareParams params = new ShareParams();
		params.setTitle(title);
		params.setText(text);
		params.setImageData(imageData);
		params.setShareType(Wechat.SHARE_EMOJI);
		shareByShareParams(context, params, getName(WeiXin.Wechat), paListener);
	}

	/**
	 * 分享视频或者网页
	 * 
	 * @param weiXin
	 *            微信平台选择：微信好友，微信朋友圈，微信收藏
	 * @param title
	 * @param text
	 * @param imagePath
	 *            本地图片地址
	 * @param url
	 *            URL地址
	 * @param isVideo
	 *            true为分享视频，false为分享网页
	 */
	public void shareVideoOrPageWithImgPath(WeiXin weiXin, String title,
			String text, String imagePath, String url, boolean isVideo) {
		ShareParams params = new ShareParams();
		params.setTitle(title);
		params.setText(text);
		params.setImagePath(imagePath);
		params.setUrl(url);
		if (isVideo) {
			params.setShareType(Wechat.SHARE_VIDEO);
		} else {
			params.setShareType(Wechat.SHARE_WEBPAGE);
		}

		shareByShareParams(context, params, getName(weiXin), paListener);
	}

	/**
	 * 分享视频或者网页
	 * 
	 * @param weiXin
	 *            微信平台选择：微信好友，微信朋友圈，微信收藏
	 * @param title
	 * @param text
	 * @param imageUrl
	 *            网络图片地址
	 * @param url
	 *            URL地址
	 * @param isVideo
	 *            true为分享视频，false为分享网页
	 */
	public void shareVideoOrPageWithImgurl(WeiXin weiXin, String title,
			String text, String imageUrl, String url, boolean isVideo) {
		ShareParams params = new ShareParams();
		params.setTitle(title);
		params.setText(text);
		params.setImageUrl(imageUrl);
		params.setUrl(url);
		if (isVideo) {
			params.setShareType(Wechat.SHARE_VIDEO);
		} else {
			params.setShareType(Wechat.SHARE_WEBPAGE);
		}

		shareByShareParams(context, params, getName(weiXin), paListener);
	}

	/**
	 * 分享视频或者网页
	 * 
	 * @param weiXin
	 *            微信平台选择：微信好友，微信朋友圈，微信收藏
	 * @param title
	 * @param text
	 * @param imageUrl
	 *            网络图片地址
	 * @param url
	 *            URL地址
	 * @param isVideo
	 *            true为分享视频，false为分享网页
	 */
	public void shareVideoOrPageWithImgdata(WeiXin weiXin, String title,
			String text, Bitmap imageData, String url, boolean isVideo) {
		ShareParams params = new ShareParams();
		params.setTitle(title);
		params.setText(text);
		params.setImageData(imageData);
		params.setUrl(url);
		if (isVideo) {
			params.setShareType(Wechat.SHARE_VIDEO);
		} else {
			params.setShareType(Wechat.SHARE_WEBPAGE);
		}

		shareByShareParams(context, params, getName(weiXin), paListener);
	}

	/**
	 * 分享音乐
	 * (目前sharSDK有朋友圈音乐无法直接播放BUG)
	 * @param weiXin
	 *            微信平台选择：微信好友，微信朋友圈，微信收藏
	 * @param title
	 * @param text
	 * @param imagePath
	 *            本地图片地址
	 * @param musicUrl 音乐地址
	 * @param url
	 *            消息点击后打开的页面地址
	 */
	public void shareMusicWithImgPath(WeiXin weiXin, String title, String text,
			String imagePath, String musicUrl, String url) {
		ShareParams params = new ShareParams();
		params.setTitle(title);
		params.setText(text);
		params.setImagePath(imagePath);
		params.setMusicUrl(musicUrl);
		params.setUrl(url);
		params.setShareType(Wechat.SHARE_MUSIC);

		shareByShareParams(context, params, getName(weiXin), paListener);
	}
	/**
	 * 分享音乐
	 * (目前sharSDK有朋友圈音乐无法直接播放BUG)
	 * @param weiXin
	 *            微信平台选择：微信好友，微信朋友圈，微信收藏
	 * @param title
	 * @param text
	 * @param imageUrl
	 *            网络图片地址
	 * @param musicUrl 音乐地址
	 * @param url
	 *            消息点击后打开的页面地址
	 */
	public void shareMusicWithImgUrl(WeiXin weiXin, String title, String text,
			String imageUrl, String musicUrl, String url) {
		ShareParams params = new ShareParams();
		params.setTitle(title);
		params.setText(text);
		params.setImageUrl(imageUrl);
		params.setMusicUrl(musicUrl);
		params.setUrl(url);
		params.setShareType(Wechat.SHARE_MUSIC);
		
		shareByShareParams(context, params, getName(weiXin), paListener);
	}
	/**
	 * 分享音乐
	 * (目前sharSDK有朋友圈音乐无法直接播放BUG)
	 * @param weiXin
	 *            微信平台选择：微信好友，微信朋友圈，微信收藏
	 * @param title
	 * @param text
	 * @param imageData
	 *            图片对象
	 * @param musicUrl 音乐地址
	 * @param url
	 *            消息点击后打开的页面地址
	 */
	public void shareMusicWithImgData(WeiXin weiXin, String title, String text,
			Bitmap imageData, String musicUrl, String url) {
		ShareParams params = new ShareParams();
		params.setTitle(title);
		params.setText(text);
		params.setImageData(imageData);
		params.setMusicUrl(musicUrl);
		params.setUrl(url);
		params.setShareType(Wechat.SHARE_MUSIC);
		
		shareByShareParams(context, params, getName(weiXin), paListener);
	}

	private String getName(WeiXin weiXin) {
		if (weiXin == WeiXin.Wechat || weiXin.equals(WeiXin.Wechat)) {
			return Wechat.NAME;
		} else if (weiXin == WeiXin.WechatMoments
				|| weiXin.equals(WeiXin.WechatMoments)) {
			return WechatMoments.NAME;
		} else if (weiXin == WeiXin.WechatFavorite
				|| weiXin.equals(WeiXin.WechatFavorite)) {
			return WechatFavorite.NAME;
		} else {
			return Wechat.NAME;
		}
	}
}
