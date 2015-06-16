package com.changhong.thirdpart.sharesdk.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
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

/**
 * ShareUtil 分享工具类
 * 
 * @author Administrator
 * 
 */
public class ShareCenter {
	public static final String SHAREBEGIN = "正在启动分享中...";

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

	/**
	 * 在主线程中使用，直接单独分享某个平台另一种实现方式。自定义shareParam参数直接分享，适用于所有分享方式。
	 * 
	 * @param context
	 * @param shareParam
	 *            分享数据model各个参数作用见本类最前面注释，每个平台分享内容不同，需要传入分享内容可参考http://wiki.mob.
	 *            com/%E4%B8%8D%E5%90%8C
	 *            %E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%
	 *            84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/
	 * @param platFrom
	 *            若platform设为空，显示一键分享对话框。
	 *            platFrom是要分享平台对应的NAME，NAME值取可参考assert文件下ShareSDK
	 *            .xml中该平台标签.Name .如分享QQ好友为QQ.NAME，QQ空间是QZONE.NAME
	 * @param paListener
	 *            回调监听器
	 */
	public static void shareByShareParams(Context context,
			ShareParams shareParam, String platFrom,
			PlatformActionListener paListener) {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			Log.e("Not UIThreadError",
					"You are not on Ui Thread,it may be take some error or can't shar success.At least the toast can't show.");
		}
		if (shareParam == null) {
			L.e("shareParam or platFrom can not be null");
			showToast(context, "shareParam分享内容不能为空", false);
			return;
		}
		if (TextUtils.isEmpty(platFrom)) {// platFrom平台NAME为空，显示一键分享对话框
			showOneKeyShare(context, shareParam, paListener, true, "");
			return;
		}
		checkParams(context, shareParam, platFrom);// 检测参数
		{// 执行分享代码
			Platform platform = ShareSDK.getPlatform(platFrom);
			platform.setPlatformActionListener(paListener); // 设置分享事件回调
			platform.share(shareParam);// 执行分享
		}
		showToast(context, SHAREBEGIN, true);
	}

	/**
	 * 调用一键分享实现分享功能(TVHelper适用)。
	 * 
	 * @param context
	 * @param title
	 *            分享内容显示的标题
	 * @param titleUrl
	 *            分享内容点击跳转链接，某些平台是必填参数，如QQ
	 * @param text
	 *            分享内容文本
	 * @param imagePath
	 *            本地图片路径
	 * @param paListener
	 *            回调监听器
	 * @param platform
	 *            若platform设为空，显示一键分享对话框。
	 *            platFrom是要分享平台对应的NAME，NAME值取可参考assert文件下ShareSDK
	 *            .xml中该平台标签.Name .如分享QQ好友为QQ.NAME，QQ空间是QZONE.NAME
	 */
	public static void showOneKeyShare(Context context, String title,
			String titleUrl, String text, String imagePath,
			PlatformActionListener paListener, String platform) {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			Log.e("Not UIThreadError",
					"You are not on Ui Thread,it may be take some error or can't shar success.At least the toast can't show.");
			Looper.prepare();
		}
		OnekeyShare oks = new OnekeyShare();
		if (!TextUtils.isEmpty(platform)) {
			oks.setPlatform(platform);// 设置平台，如果设置了平台就是对于单个平台分享，如果为空表示打开分享对话框
		}
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		oks.setDialogMode();// TODO 编辑采用对话框模式，去掉则为全屏
		oks.setSilent(false);// TODO 是否直接分享
		oks.setCallback(paListener);// 分享回调

		// title标题，在印象笔记、邮箱、信息、微信（包括好友、朋友圈和收藏）、 易信（包括好友、朋友圈）、人人网和QQ空间使用，否则可以不提供
		oks.setTitle(title);
		if (TextUtils.isEmpty(titleUrl)) {
			titleUrl = "http://";
		}
		// titleUrl是标题的网络链接，仅在人人网和QQ/QQ空间使用
		oks.setTitleUrl(titleUrl);// QQ分享时候titleurl不能为空
		// url在微信（包括好友、朋友圈收藏）和易信（包括好友和朋友圈）中使用，否则可以不提供
		oks.setUrl(titleUrl);//
		// text是分享文本，所有平台都需要这个字段
		oks.setText(text == null ? "" : text);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数 确保SDcard下面存在此张图片,没图片可能导致分享失败
		oks.setImagePath(imagePath == null ? "" : imagePath);//
		// site是分享此内容的网站名称，仅在QQ空间使用，否则可以不提供,在本工程默认使用title
		oks.setSite(title);
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用，否则可以不提供 ，在本工程默认使用titleUrl
		oks.setSiteUrl(titleUrl);

		// 启动分享GUI
		oks.show(context);
	}

	/**
	 * 调用一键分享实现分享功能。在主线程中使用
	 * 
	 * @param context
	 * @param shareParam
	 *            分享数据model各个参数作用见本类最前面注释，每个平台分享内容不同，需要传入分享内容可参考http://wiki.mob.
	 *            com/%E4%B8%8D%E5%90%8C
	 *            %E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%
	 *            84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/ 分享数据模型
	 * @param paListener
	 *            分享回调（QQ有部分版本回调时候有问题。）
	 * @param silent
	 *            是否直接分享， true直接分享 ，false打开编辑对话框
	 * @param platFrom
	 *            若platform设为空，显示一键分享对话框。
	 *            platFrom是要分享平台对应的NAME，NAME值取可参考assert文件下ShareSDK
	 *            .xml中该平台标签.Name .如分享QQ好友为QQ.NAME，QQ空间是QZONE.NAME
	 */
	public static void showOneKeyShare(Context context,
			ShareParams shareParams, PlatformActionListener paListener,
			boolean silent, String platform) {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			Log.e("Not UIThreadError",
					"You are not on Ui Thread,it may be take some error or can't shar success.At least the toast can't show.");
			Looper.prepare();
		}
		OnekeyShare oks = new OnekeyShare();
		if (!TextUtils.isEmpty(platform)) {
			oks.setPlatform(platform);// 设置平台，如果设置了平台就是对于单个平台分享，如果为空表示打开分享对话框
		} else {
			checkParams(context, shareParams, platform);
		}

		oks.setDialogMode();// 编辑采用对话框模式，去掉则为全屏
		oks.setSilent(silent);// 是否直接分享
		oks.setCallback(paListener);// 回调监听器

		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// title标题，在印象笔记、邮箱、信息、微信（包括好友、朋友圈和收藏）、 易信（包括好友、朋友圈）、人人网和QQ空间使用，否则可以不提供
		oks.setTitle(shareParams.getTitle());
		// titleUrl是标题的网络链接，仅在人人网和QQ/QQ空间使用
		oks.setTitleUrl(TextUtils.isEmpty(shareParams.getTitleUrl()) ? "http://"
				: shareParams.getTitleUrl());// QQ分享时候titleurl不能为空
		// url在微信（包括好友、朋友圈收藏）和易信（包括好友和朋友圈）中使用，否则可以不提供
		oks.setUrl(TextUtils.isEmpty(shareParams.getUrl()) ? "http://"
				: shareParams.getUrl());//
		// text是分享文本，所有平台都需要这个字段
		oks.setText(shareParams.getText() == null ? "" : shareParams.getText());
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数 确保SDcard下面存在此张图片,没图片可能导致分享失败
		oks.setImagePath(shareParams.getImagePath());//
		oks.setImageUrl(shareParams.getImageUrl());// imageUrl是图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字段
		oks.setImageArray(shareParams.getImageArray());// 腾讯微博分享多张图片
		oks.setComment(shareParams.getComment());// comment是我对这条分享的评论，仅在人人网和QQ空间使用，否则可以不提供
		oks.setAddress(shareParams.getAddress());// address是接收人地址，仅在信息和邮件使用，否则可以不提供
		oks.setExecuteUrl(shareParams.getExecuteUrl());// 设置KakaoTalk的应用打开地址
		oks.setInstallUrl(shareParams.getInstallUrl());// 设置KakaoTalk的应用下载地址
		oks.setLatitude(shareParams.getLatitude());// 分享地纬度，新浪微博、腾讯微博和foursquare支持此字段
		oks.setLongitude(shareParams.getLongitude());// 分享地经度，新浪微博、腾讯微博和foursquare支持此字段
		// oks.setShareContentCustomizeCallback(callback);
		oks.setVenueDescription(shareParams.getVenueDescription());
		// foursquare分享时的地方名
		oks.setVenueName(shareParams.getVenueName());
		// 为此添加检测图片是否存在功能
		oks.setSite(shareParams.getSite());
		// 为此添加检测图片是否存在功能
		oks.setSiteUrl(shareParams.getSiteUrl());
		// 启动分享GUI
		oks.show(context);
	}

	/**
	 * 检测分享内容参数有没有缺省 log或者toast提示错误信息 目前只处理QQ/QZONE、新浪微博、腾讯微博、微信
	 * 
	 * @param shareParam
	 * @param platFrom
	 */
	public static void checkParams(Context context, ShareParams shareParam,
			String platFrom) {
		// 参数需要注释介绍：或代表选一即可，（）代表可有可无
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
						&& TextUtils.isEmpty(shareParam.getImagePath())
						&& shareParam.getImageData() == null) {
					// imagePath/imageUrl/imageData
					Log.e("",
							"Wechat分享除SHARE_TEXT外其它ShareType需要图片资源，您没设置图片资源可能导致分享异常");
				} else if (shareParam.getImageData() != null) {
					Bitmap bitmap = shareParam.getImageData();
					if (bitmap.getByteCount() >= 1024) {// 1KB以内 imageData
						Log.e("", "微信分享的imageData不能超过1KB");
						showToast(context, "微信分享的imageData不能超过1KB", false);
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
	private static boolean checkStringsHasEmpty(String name, String... string) {
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

	/************************************ 以下举例调用shareByShareParams方法直接分享 **************************************/
	/**
	 * 举例QQ直接分享，参数可按照要求再添加。
	 * 
	 * @param context
	 *            context
	 * @param title
	 *            标题QQ分享小于30字符
	 * @param titleUrl
	 *            分享超链接
	 * @param text
	 *            文本QQ分享小于40字符
	 * @param imageurl
	 *            图片网络地址
	 * @param imagepath
	 *            图片本地地址 *
	 * @param bitmap
	 *            图片
	 * @param paListener
	 *            回调监听器
	 */
	public static void shareQQ(Context context, String title, String titleUrl,
			String text, String imagepath, PlatformActionListener paListener) {
		ShareParams sp = new ShareParams();
		sp.setTitle(title);
		sp.setText(text);
		sp.setImagePath(imagepath);
		if (TextUtils.isEmpty(titleUrl)) {
			titleUrl = "http://";
			L.e("shareQQ give a empty titleUrl");
		}
		sp.setTitleUrl(titleUrl); // 标题的超链接不能为空
		shareByShareParams(context, sp, QQ.NAME, paListener);
	}

	/**
	 * 举例QQ空间直接分享，参数可按照要求再添加。
	 * 
	 * @param context
	 * @param title
	 * @param titleUrl
	 * @param text
	 * @param imagepath
	 *            图片地址，可为空
	 * @param site
	 * @param siteUrl
	 * @param paListener
	 */
	public static void shareQZone(Context context, String title,
			String titleUrl, String text, String imagepath, String site,
			String siteUrl, PlatformActionListener paListener) {
		ShareParams sp = new ShareParams();
		sp.setTitle(title);
		sp.setTitleUrl(titleUrl); // 标题的超链接
		sp.setText(text);
		sp.setImagePath(imagepath);
		sp.setSite(TextUtils.isEmpty(site) ? title : site);
		sp.setSiteUrl(TextUtils.isEmpty(titleUrl) ? titleUrl : siteUrl);
		shareByShareParams(context, sp, QZone.NAME, paListener);
	}

	/**
	 * 举例新浪微博直接分享，参数可按照要求再添加。
	 * 
	 * @param context
	 * @param text
	 * @param latitude
	 *            北纬
	 * @param longitude
	 *            东经
	 * @param imagePath
	 *            本地图片地址
	 * @param issilent
	 *            是否直接分享，true为直接分享
	 * @param paListener
	 */
	public static void shareSinaWeiBo(Context context, String text,
			float latitude, float longitude, String imagePath,
			boolean issilent, PlatformActionListener paListener) {
		ShareParams sp = new ShareParams();
		sp.setText(text);
		sp.setLatitude(latitude);// 有效范围:-90.0到+90.0，+表示北纬
		sp.setLongitude(longitude);// 有效范围：-180.0到+180.0，+表示东经
		sp.setImagePath(imagePath);
		if (issilent) {
			// 以下两种方式二选一都可以
			shareByShareParams(context, sp, SinaWeibo.NAME, paListener);
			// showOneKeyShare(context, sp, paListener, issilent,
			// SinaWeibo.NAME);
		} else {
			// 目前没有自定义编辑页面，就只能采用自带的一键分享的编辑页面
			showOneKeyShare(context, sp, paListener, issilent, SinaWeibo.NAME);
		}
	}

	/**
	 * 举例腾讯微博直接分享，参数可按照要求再添加。
	 * 
	 * @param context
	 * @param text
	 * @param latitude
	 *            北纬
	 * @param longitude
	 *            东经
	 * @param imagePath
	 *            本地图片地址
	 * @param imageArray
	 *            图片数组，注意：如果设置了imagePath，那么imageArray无效
	 * @param issilent
	 *            是否直接分享 True为直接分享
	 * @param paListener
	 */
	public static void shareTencentWeiBo(Context context, String text,
			float latitude, float longitude, String imagePath,
			String[] imageArray, boolean issilent,
			PlatformActionListener paListener) {
		ShareParams sp = new ShareParams();
		sp.setText(text);
		sp.setLatitude(latitude);// 有效范围:-90.0到+90.0，+表示北纬
		sp.setLongitude(longitude);// 有效范围：-180.0到+180.0，+表示东经
		sp.setImagePath(imagePath);
		sp.setImageArray(imageArray);// 图片数组
		if (issilent) {
			// 以下两种方式二选一都可以
			shareByShareParams(context, sp, TencentWeibo.NAME, paListener);
			// showOneKeyShare(context, sp, paListener, issilent,
			// TencentWeibo.NAME);
		} else {
			// 目前没有自定义编辑页面，就只能采用自带的一键分享的编辑页面
			showOneKeyShare(context, sp, paListener, issilent,
					TencentWeibo.NAME);
		}
	}

	public static void showToast(Context context, String content, boolean islong) {
		Toast.makeText(context, content,
				islong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
	}

	// public enum MyShareParams {
	// SinaWeibo, TencentWeibo, QQ, QZone, Wechat, WechatMoments,
	// WechatFavorite, Facebook, Email, ShortMessage
	// }
	//
	// public static String getPlatFrom(MyShareParams myParams) {
	// String platFrom = "";
	// if (myParams == MyShareParams.SinaWeibo) {
	// platFrom = SinaWeibo.NAME;
	// } else if (myParams == MyShareParams.QQ) {
	// platFrom = QQ.NAME;
	// } else if (myParams == MyShareParams.QZone) {
	// platFrom = QZone.NAME;
	// } else if (myParams == MyShareParams.Wechat) {
	// platFrom = Wechat.NAME;
	// } else if (myParams == MyShareParams.WechatMoments) {
	// platFrom = WechatMoments.NAME;
	// } else if (myParams == MyShareParams.WechatFavorite) {
	// platFrom = WechatFavorite.NAME;
	// } else if (myParams == MyShareParams.Facebook) {
	// platFrom = Facebook.NAME;
	// } else if (myParams == MyShareParams.Email) {
	// platFrom = Email.NAME;
	// } else if (myParams == MyShareParams.ShortMessage) {
	// platFrom = ShortMessage.NAME;
	// }
	//
	// return platFrom;
	// }
}
