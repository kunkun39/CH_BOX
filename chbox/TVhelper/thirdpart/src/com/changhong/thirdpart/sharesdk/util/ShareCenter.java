package com.changhong.thirdpart.sharesdk.util;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.changhong.thirdpart.R;
import com.changhong.thirdpart.uti.Util;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * ShareCenter 分享工具类 各个平台分享参数shareParam见官网http
 * ://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%
 * E5%88%86%E4%BA%AB%E5%86%85
 * %E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/
 * 
 * shareByShareParams(context,shareParam,platFrom,paListener) 直接分享到指定平台。
 * showShareMenu 打开分享菜单对话框 shareByOnekeyshare 使用一键分享实现分享功能
 * 
 * @author wangxiufeng
 * 
 */
public class ShareCenter {
	public static final String SHAREBEGIN = "正在启动分享中...";
	private static final int SHOW_TOAST = 223;
	/** context */
	public Context context = null;
	/** share callback */
	public PlatformActionListener myplatformActionListener = null;
	/** 是否只使用外部传来myplatformActionListener回调函数？ */
	public boolean isOnlyMypaListener = false;

	/** Toast提示是否对话框方式？true对话框；false默认toast */
	public boolean isDialogToast = true;

	/** 一键分享微博否采用编辑方式？true编辑；false直接分享 */
	public boolean isEdit = true;
	/**上次显示分享对话框时间，解决连续快速点击显示多个对话框*/
	private static long lastShowTime=0;
	/**
	 * ShareParams分享数据模型类，每个平台有自己对应的ShareParams类，建议使用父类
	 * cn.sharesdk.framework.Platform.ShareParams 各数据类型即作用如下
	 */
	{
		/** 一键分享适用：编辑页面是否采用对话框模式，true为对话框，false为全屏。 */
		// public boolean isDialogMode = false;
		/** 一键分享适用：是否直接分享，true为直接分享，false为打开编辑页面。 */
		// public boolean isSilent = false;
		/** 分享回调（目前QQ有的版本不能正常回调，qq的原因） */
		// public PlatformActionListener paListener;
		/** 标题，在印象笔记、邮箱、信息、微信（包括好友、朋友圈和收藏）、 易信（包括好友、朋友圈）、人人网和QQ空间使用，否则可以不提供 */
		// public String title;
		/** 是标题的网络链接，仅在人人网和QQ空间使用，否则可以不提供 */
		// public String titleurl = "http://";
		/** 是分享文本，所有平台都需要这个字段 */
		// public String text = " ";
		/** 是图片的本地路径，Linked-In以外的平台都支持此参数 确保SDcard下面存在此张图片,没图片可能导致分享失败 */
		// public String imagePath;
		/** 图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字段 */
		// public String imageUrl;
		/** url在微信（包括好友、朋友圈收藏）和易信（包括好友和朋友圈）中使用，否则可以不提供 */
		// public String url
		/** 腾讯微博分享多张图片 */
		// public String[] imageArray;
		/** comment是我对这条分享的评论，仅在人人网和QQ空间使用，否则可以不提供 */
		// public String comment;
		/** site是分享此内容的网站名称，仅在QQ空间使用，否则可以不提供 */
		// public String site;
		/** siteUrl是分享此内容的网站地址，仅在QQ空间使用，否则可以不提供 */
		// public String siteUrl;
		/** 分享地纬度，新浪微博、腾讯微博和foursquare支持此字段 */
		// public String latitude;
		/** 分享地经度，新浪微博、腾讯微博和foursquare支持此字段 */
		// public String longitude;
		/** address是接收人地址，仅在信息和邮件使用，否则可以不提供 */
		// public String address;
		/** 设置KakaoTalk的应用打开地址 */
		// public String executeUrl;
		/** 设置KakaoTalk的应用下载地址 */
		// public String installUrl;
		/** foursquare分享时的地方描述 */
		// public String venueDescription;
		/** foursquare分享时的地方名 */
		// public String venueName;
	}
	/**
	 * platform平台名字,用于区别分享到那个2平台（各个平台分享时候platform值取值来源于ShareSDK.xml中该平台标签.Name.
	 * 如分享QQ好友为QQ. NAME ）
	 */
	{// 这里只列出部分常用的，详细请参考ShareSDK.xml文件
		/** 新浪微博 */
		// SinaWeibo.NAME;
		/** 腾讯微博 */
		// TencentWeibo.NAME;
		/** QQ */
		// QQ.NAME;
		/** QQ空间 */
		// QZone.NAME;
		/** 微信好友 */
		// Wechat.NAME;
		/** 微信朋友圈 */
		// WechatMoments.NAME;
		/** 微信收藏 */
		// WechatFavorite.NAME;
	}

	public ShareCenter(Context context) {
		super();
		isOnlyMypaListener = false;
		this.context = context;
	}

	public ShareCenter(Context context, PlatformActionListener paListener) {
		super();
		this.context = context;
		this.paListener = paListener;
	}

	/**
	 * 直接单独分享到某个指定平台。自定义shareParam参数直接分享，适用于所有平台直接分享。
	 * 
	 * @param shareParam
	 *            分享数据model各个参数作用见本类最前面注释，每个平台分享内容不同，需要传入分享内容可参考http://wiki.mob.
	 *            com/%E4%B8%8D%E5%90%8C
	 *            %E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%
	 *            84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/
	 * @param platFrom
	 *            platform为空，显示一键分享对话框。
	 *            platFrom平台对应的NAME，直接分享到指定品台，NAME值取可参考assert文件下ShareSDK
	 *            .xml文件标签.如分享QQ空间是QZONE.NAME
	 */
	public void shareByShareParams(ShareParams shareParam, String platFrom) {

		if (Looper.myLooper() != Looper.getMainLooper()) {// 非主线程可能会影响某些功能
			Log.e("Not UIThreadError",
					"You are not on Ui Thread,it may be take some error or can't shar success.At least the toast can't show.");
		}

		if (shareParam == null) {// 分享参数为空不能分享
			L.e("shareParam or platFrom can not be null");
			showToast(context, "shareParam分享内容不能为空", false);
			return;
		}

		if (TextUtils.isEmpty(platFrom)) {// 平台NAME为空，显示一键分享列表对话框
			shareByOnekeyshare(shareParam, "");
			return;
		}
		if (context != null) {
			ShareSDK.initSDK(context);
		}
		checkParams(context, shareParam, platFrom);// 检测参数
		{// 执行分享代码
			Platform platform = ShareSDK.getPlatform(platFrom);
			platform.SSOSetting(true);
			platform.setPlatformActionListener(paListener); // 设置分享事件回调
			platform.share(shareParam);// 执行分享
		}
		showToast(context, SHAREBEGIN, true);
	}

	protected void shareByShareParams(Context context, ShareParams shareParam,
			String platFrom, PlatformActionListener paListener) {
		this.context = context;
		this.paListener = paListener;
		shareByShareParams(shareParam, platFrom);
	}

	/**
	 * 弹出一键分享对话框分享本地图片和文本信息
	 * 
	 * @param context
	 * @param title
	 *            分享内容显示的标题
	 * @param text
	 *            分享内容文本
	 * @param imagePath
	 *            本地图片路径
	 * @param paListener
	 *            回调监听器
	 */
	public void showShareMenu(String title, String text, String imagePath) {
		shareByOnekeyshare(title, null, text, imagePath, null, null);
	}

	/**
	 * 弹出一键分享对话框分享本地图片和文本信息
	 * 
	 * @param context
	 * @param title
	 *            分享内容显示的标题
	 * @param titleUrl
	 *            分享内容点击跳转链接，
	 * @param text
	 *            分享内容文本
	 * @param imagePath
	 *            本地图片路径
	 * @param paListener
	 *            回调监听器
	 */
	public void showShareMenu(String title, String titleUrl, String text,
			String imagePath) {
		shareByOnekeyshare(title, titleUrl, text, imagePath, null, null);
	}

	/**
	 * 弹出一键分享对话框分享网络图片和文本信息
	 * 
	 * @param context
	 * @param title
	 *            分享内容显示的标题
	 * @param titleUrl
	 *            分享内容点击跳转链接，
	 * @param text
	 *            分享内容文本
	 * @param imageUrl
	 *            网络图片路径
	 * @param paListener
	 *            回调监听器
	 */
	public void showShareMenuWithImgurl(String title, String titleUrl,
			String text, String imageUrl) {
		shareByOnekeyshare(title, titleUrl, text, null, imageUrl, null);
	}

	/**
	 * 调用一键分享实现分享功能(适用TVHelper)。
	 * 
	 * @param title
	 *            分享内容显示的标题
	 * @param titleUrl
	 *            分享内容点击跳转链接，某些平台是必填参数，如QQ
	 * @param text
	 *            分享内容文本
	 * @param imagePath
	 *            本地图片路径，和imageURL二选一
	 * @param imageUrl
	 *            网络图片 ，和imagePath二选一
	 * @param platform
	 *            platform为空，显示一键分享对话框。
	 *            platFrom平台对应的NAME，直接分享到指定品台，NAME值取可参考assert文件下ShareSDK
	 *            .xml文件标签.如分享QQ空间是QZONE.NAME
	 */
	public void shareByOnekeyshare(String title, String titleUrl, String text,
			final String imagePath, final String imageUrl, String platform) {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			Log.e("Not UIThreadError",
					"You are not on Ui Thread,it may be take some error or can't shar success.At least the toast can't show.");
			Looper.prepare();
		}
		ShareSDK.initSDK(context);
		OnekeyShare oks = new OnekeyShare();
		if (!TextUtils.isEmpty(platform)) {
			oks.setPlatform(platform);// 设置平台Name，如果为空表示打开分享对话框
		}
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		oks.setDialogMode();// 编辑采用对话框模式
		if (!TextUtils.isEmpty(titleUrl)) {
			oks.setTitleUrl(titleUrl);// QQ分享时候titleurl不能为空
			oks.setUrl(titleUrl);
			oks.setSiteUrl(titleUrl);
		}
		if (!TextUtils.isEmpty(imagePath)) {
			oks.setImagePath(imagePath);//
		}
		if (!TextUtils.isEmpty(imageUrl)) {
			oks.setImageUrl(imageUrl);
		}
		oks.setSilent(!isEdit);// 是否直接分享
		oks.setCallback(paListener);// 分享回调
		oks.setTitle(title);
		oks.setSite(title);
		oks.setText(TextUtils.isEmpty(text) ? "  " : text);
//TODO 去掉手动对接QQ分享功能
//		if (!TextUtils.isEmpty(imagePath) || !TextUtils.isEmpty(imageUrl)) {
//			Bitmap enableLogo = BitmapFactory.decodeResource(
//					context.getResources(), R.drawable.logo_qq);
//			Bitmap disableLogo = BitmapFactory.decodeResource(
//					context.getResources(), R.drawable.logo_qq);
//			String label = "QQ好友";
//			OnClickListener listener = new OnClickListener() {
//				public void onClick(View v) {
//					Tencent mTencent = Tencent.createInstance(
//							ShareConfig.QQPPID, context);
//					Bundle params = new Bundle();
//					if (!TextUtils.isEmpty(imagePath)) {
//						params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,
//								imagePath);
//					} else if (!TextUtils.isEmpty(imageUrl)) {
//						params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
//								imageUrl);
//					}
//					params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "");
//					params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
//							QQShare.SHARE_TO_QQ_TYPE_IMAGE);
//					// params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,
//					// QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
//					mTencent.shareToQQ((Activity) context, params,
//							qqShareListener);
//				}
//			};
//			oks.setCustomerLogo(enableLogo, disableLogo, label, listener);
//		}
		long currentShowTime=System.currentTimeMillis();
		if (currentShowTime-lastShowTime>330) {
			oks.show(context);// 启动分享GUI
			lastShowTime=currentShowTime;
		}
		
	}

	/**
	 * 调用一键分享实现分享功能。platform为空弹出分享菜单，不为空直接分享到指定平台，建议在主线程中使用
	 * 
	 * @param shareParam
	 *            分享数据model各个参数作用见本类最前面注释，每个平台分享内容不同，需要传入分享内容可参考http://wiki.mob.
	 *            com/%E4%B8%8D%E5%90%8C
	 *            %E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%
	 *            84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/ 分享数据模型
	 * @param platFrom
	 *            platform为空，显示一键分享对话框。
	 *            platFrom平台对应的NAME，直接分享到指定品台，NAME值取可参考assert文件下ShareSDK
	 *            .xml文件标签.如分享QQ空间是QZONE.NAME
	 */
	public void shareByOnekeyshare(ShareParams shareParams, String platform) {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			Log.e("Not UIThreadError",
					"You are not on Ui Thread,it may be take some error or can't shar success.At least the toast can't show.");
			Looper.prepare();
		}
		ShareSDK.initSDK(context);
		OnekeyShare oks = new OnekeyShare();
		if (!TextUtils.isEmpty(platform)) {
			oks.setPlatform(platform);// 设置平台，如果设置了平台就是对于单个平台分享，如果为空表示打开分享对话框
		} else {
			checkParams(context, shareParams, platform);
		}

		oks.setDialogMode();// 编辑采用对话框模式
		oks.setSilent(!isEdit);// 是否直接分享
		oks.setCallback(paListener);// 回调监听器
		oks.disableSSOWhenAuthorize();// 关闭sso授权
		oks.setTitle(shareParams.getTitle());
		oks.setTitleUrl(shareParams.getTitleUrl());// QQ分享时候titleurl不能为空
		oks.setUrl(shareParams.getUrl());//
		oks.setText(shareParams.getText() == null ? "" : shareParams.getText());
		oks.setImagePath(shareParams.getImagePath());//
		oks.setImageUrl(shareParams.getImageUrl());
		oks.setImageArray(shareParams.getImageArray());
		oks.setComment(shareParams.getComment());
		oks.setAddress(shareParams.getAddress());
		oks.setExecuteUrl(shareParams.getExecuteUrl());
		oks.setInstallUrl(shareParams.getInstallUrl());
		oks.setLatitude(shareParams.getLatitude());
		oks.setLongitude(shareParams.getLongitude());
		oks.setVenueDescription(shareParams.getVenueDescription());
		oks.setVenueName(shareParams.getVenueName());
		oks.setSite(shareParams.getSite());
		oks.setSiteUrl(shareParams.getSiteUrl());
		// 启动分享GUI
		long currentShowTime=System.currentTimeMillis();
		if (currentShowTime-lastShowTime>330) {
			oks.show(context);// 启动分享GUI
			lastShowTime=currentShowTime;
		}
	}

	/**
	 * 检测分享内容参数有没有缺省 log或者toast提示错误信息 目前只处理QQ/QZONE、新浪微博、腾讯微博、微信
	 * 
	 * @param shareParam
	 * @param platFrom
	 */
	private void checkParams(Context context, ShareParams shareParam,
			String platFrom) {
		if (shareParam == null || platFrom == null) {
			return;
		}
		if (platFrom.equals(QQ.NAME)) {
			// QQ 需要参数title，titleUrl，text，imagePath或imageUrl，musicUrl(可选)
			if (checkStringsHasEmpty("QQ", shareParam.getTitle(),
					shareParam.getTitleUrl(), shareParam.getText())) {
				Log.e("",
						"QQ分享内容参数不全，可能导致分享失败，QQ分享内容有title，titleUrl，text，imagePath或imageUrl，musicUrl(可选)");
			} else if (TextUtils.isEmpty(shareParam.getImagePath())
					&& TextUtils.isEmpty(shareParam.getImageUrl())) {
				// QQ分享时候至少选择一张图片
				Log.e("",
						"you shoule be give at least one picture when you are shar QQ friend,it will be make you shar failed");
			}
		} else if (platFrom.equals(QZone.NAME)) {
			// QQ空间参数
			// 需要参数title，titleUrl，text，（imagePath或imageUrl）(可选)，site，siteUrl
			if (checkStringsHasEmpty("QZone", shareParam.getTitle(),
					shareParam.getTitleUrl(), shareParam.getText(),
					shareParam.getSite(), shareParam.getSiteUrl())) {
				Log.e("",
						"QZone分享内容参数不全，可能导致分享失败，QZone分享内容有title，titleUrl，text，（imagePath或imageUrl）(可选)，site，siteUrl。");
			}
		} else if (platFrom.equals(SinaWeibo.NAME)) {
			// 新浪微博空间参数
			// 需要参数text，（imagePath或imageUrl）(可选)，latitude(可选)，longitude(可选)
			if (checkStringsHasEmpty("SinaWeibo", shareParam.getText())) {
				Log.e("",
						"SinaWeibo分享内容参数不全，可能导致分享失败，SinaWeibo分享内容有text，（imagePath或imageUrl）(可选)，latitude(可选)，longitude(可选)");
			}
		} else if (platFrom.equals(TencentWeibo.NAME)) {
			// 腾讯微博空间参数
			// 需要参数text，（imagePath或imageUrl或ImageArray）（可选），latitude(可选)，longitude(可选)，注意：如果设置了imagePath，那么imageArray无效
			if (checkStringsHasEmpty("TencentWeibo", shareParam.getText())) {
				Log.e("",
						"TencentWeibo分享内容参数不全，可能导致分享失败，TencentWeibo分享内容有text，（imagePath或imageUrl或ImageArray）（可选），latitude(可选)，longitude(可选)");
			}
		} else if (platFrom.equals(Wechat.NAME)
				|| platFrom.equals(WechatFavorite.NAME)
				|| platFrom.equals(WechatMoments.NAME)) {
			// 微信好友、微信朋友圈、微信收藏
			if (checkStringsHasEmpty("Wechat", shareParam.getTitle(),
					shareParam.getText())) {
				Log.e("", "Wechat分享内容title或text参数不全，可能导致分享");
			}
			if (shareParam.getShareType() == Wechat.SHARE_TEXT) {

			} else {
				if (TextUtils.isEmpty(shareParam.getImagePath())
						&& TextUtils.isEmpty(shareParam.getImageUrl())
						&& shareParam.getImageData() == null) {
					// imagePath/imageUrl/imageData
					showToast(context, "分享图片资源不存在可能会导致分享失败!", false);
					Log.e("",
							"Wechat分享除SHARE_TEXT外其它ShareType需要图片资源，您没设置图片资源可能导致分享异常");
				} else if (shareParam.getImageData() != null) {
					Bitmap bitmap = shareParam.getImageData();
					if (bitmap.getByteCount() >= 1024) {// 1KB以内 imageData
						Log.e("", "微信分享的imageData不能超过1KB");
						// showToast(context, "微信分享的imageData不能超过1KB", false);
					}
				}
				if (shareParam.getShareType() == Wechat.SHARE_APPS) {
					// 分享应用filePath(apk文件) extInfo(应用信息脚本)
					if (checkStringsHasEmpty("Wechat",
							shareParam.getFilePath(), shareParam.getExtInfo())) {
						Log.e("", "微信分享应用时候filePath或者extInfo参数为空会导致分享异常");
					}
				} else if (shareParam.getShareType() == Wechat.SHARE_FILE) {
					// 分享文件
					if (checkStringsHasEmpty("Wechat", shareParam.getFilePath())) {
						Log.e("", "微信分享文件时候filePath为空可能会导致分享异常");
					}
				} else if (shareParam.getShareType() == Wechat.SHARE_IMAGE
						|| shareParam.getShareType() == Wechat.SHARE_EMOJI) {
					// 分享图片或者表情
				} else if (shareParam.getShareType() == Wechat.SHARE_MUSIC) {
					// 分享音乐musicUrl，url（消息点击后打开的页面）
					if (checkStringsHasEmpty("Wechat",
							shareParam.getMusicUrl(), shareParam.getUrl())) {
						Log.e("", "微信分享音乐时候musicUrl或url为空可能会导致分享异常");
					}
				} else if (shareParam.getShareType() == Wechat.SHARE_VIDEO) {
					// 分享视频 url（视频网页地址）
					if (checkStringsHasEmpty("Wechat", shareParam.getUrl())) {
						Log.e("", "微信分享视频时候url为空可能会导致分享异常");
					}
				} else if (shareParam.getShareType() == Wechat.SHARE_WEBPAGE) {
					// 分享网页 url（视频网页地址）
					if (checkStringsHasEmpty("Wechat", shareParam.getUrl())) {
						Log.e("", "微信分享网页时候url为空可能会导致分享异常");
					}
				} else {
					Log.e("",
							"微信分享 没有指定分享类型会导致分享不成功。请通过shareParam.setShareType()设置分享类型");
					showToast(context, "请通过shareParam.setShareType()设置分享类型",
							true);
				}
			}
		}
	}

	/**
	 * 检测字符串数组里面是否有字符串为空
	 * 
	 * @param string
	 * @return
	 */
	private boolean checkStringsHasEmpty(String name, String... string) {
		boolean hasEmpty = false;
		if (string == null || string.length <= 0) {
			Log.e("", "the shareParams are empty when you shar " + name
					+ ".It may be make you shar failed!");
			return true;
		}
		for (String item : string) {
			if (TextUtils.isEmpty(item)) {
				Log.e("", "there is an empty shareParam item when you shar "
						+ name + ".It may be make you shar failed! ");
				hasEmpty = true;
			}
		}
		return hasEmpty;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public PlatformActionListener getMyplatformActionListener() {
		return myplatformActionListener;
	}

	public void setMyplatformActionListener(
			PlatformActionListener myplatformActionListener) {
		this.myplatformActionListener = myplatformActionListener;
	}

	public boolean isOnlyMypaListener() {
		return isOnlyMypaListener;
	}

	public void setOnlyMypaListener(boolean isOnlyMypaListener) {
		this.isOnlyMypaListener = isOnlyMypaListener;
	}

	public boolean isDialogToast() {
		return isDialogToast;
	}

	public void setDialogToast(boolean isDialogToast) {
		this.isDialogToast = isDialogToast;
	}

	public boolean isEdit() {
		return isEdit;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	protected PlatformActionListener paListener = new PlatformActionListener() {

		@Override
		public void onError(Platform arg0, int arg1, Throwable arg2) {

			if (myplatformActionListener != null) {
				myplatformActionListener.onError(arg0, arg1, arg2);
			}
			if (!isOnlyMypaListener) {
				Message msg = shareToastHandler.obtainMessage(SHOW_TOAST);
				String expName = arg2 == null ? "" : arg2.toString();
				if (!TextUtils.isEmpty(expName)
						&& (expName.contains("WechatClientNotExistException")
								|| expName
										.contains("WechatTimelineNotSupportedException") || expName
									.contains("WechatFavoriteNotSupportedException"))) {
					msg.obj = "分享失败，请安装微信客户端";
				} else {
					msg.obj = "分享发生异常";
				}
				shareToastHandler.sendMessage(msg);
			}
		}

		@Override
		public void onComplete(Platform arg0, int arg1,
				HashMap<String, Object> arg2) {
			if (myplatformActionListener != null) {
				myplatformActionListener.onComplete(arg0, arg1, arg2);
			}
			if (!isOnlyMypaListener) {
				Message msg = shareToastHandler.obtainMessage(SHOW_TOAST);
				msg.obj = "分享成功";
				shareToastHandler.sendMessage(msg);
			}
		}

		@Override
		public void onCancel(Platform arg0, int arg1) {
			if (myplatformActionListener != null) {
				myplatformActionListener.onCancel(arg0, arg1);
			}
			if (!isOnlyMypaListener) {
				Message msg = shareToastHandler.obtainMessage(SHOW_TOAST);
				msg.obj = "分享取消";
				shareToastHandler.sendMessage(msg);
			}
		}
	};
	Handler shareToastHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == SHOW_TOAST) {
				showToast(context, "" + msg.obj, true);
			}
		};
	};
	IUiListener qqShareListener = new IUiListener() {
		@Override
		public void onCancel() {
			L.e("cutscreen QQonCancel");
			paListener.onCancel(null, 0);
		}

		@Override
		public void onComplete(Object response) {
			L.e("cutscreen QQonComplete " + response.toString());
			paListener.onComplete(null, 0, null);
		}

		@Override
		public void onError(UiError e) {
			L.e("cutscreen QQonerror " + e.toString());
			paListener.onError(null, 0, null);
		}
	};

	public void showToast(Context context, String content, boolean islong) {
		if (context != null) {
			if (isDialogToast) {
				Util.showToast(context, "" + content, islong ? 2000 : 3000);
			} else {
				Toast.makeText(context, content,
						islong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
			}
		} else {
			Log.e("", "context is null can not show toast; the content is "
					+ content);
		}
	}
}
