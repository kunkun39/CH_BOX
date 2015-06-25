package com.changhong.thirdpart.sharesdk;

import android.content.Context;
import cn.sharesdk.framework.PlatformActionListener;

import com.changhong.thirdpart.sharesdk.util.ShareCenter;
import com.changhong.thirdpart.sharesdk.util.ShareQQ;
import com.changhong.thirdpart.sharesdk.util.ShareQZone;
import com.changhong.thirdpart.sharesdk.util.ShareSinaWeiBo;
import com.changhong.thirdpart.sharesdk.util.ShareTencentWeiBo;
import com.changhong.thirdpart.sharesdk.util.ShareWeiXin;

public class ShareFactory {

	/**
	 * 获取分享基类对象，包含一键分享和分享到指定平台功能
	 * 
	 * @param context
	 * @param paListener
	 * @return
	 */
	public static ShareCenter getShareCenter(Context context,
			PlatformActionListener paListener) {
		return new ShareCenter(context, paListener);
	}

	/*************************** 以下是直接分享到指定平台 ************************************/

	/**
	 * 获取QQ分享对象
	 * 
	 * @param context
	 * @param paListener
	 * @return
	 */
	public static ShareQQ getShareQQ(Context context,
			PlatformActionListener paListener) {
		return new ShareQQ(context, paListener);
	}

	/**
	 * 获取微信分享对象
	 * 
	 * @param context
	 * @param paListener
	 * @return
	 */
	public static ShareWeiXin getShareWeiXin(Context context,
			PlatformActionListener paListener) {
		return new ShareWeiXin(context, paListener);
	}

	/**
	 * 获取QQ空间分享对象
	 * 
	 * @param context
	 * @param paListener
	 * @return
	 */
	public static ShareQZone getShareQZone(Context context,
			PlatformActionListener paListener) {
		return new ShareQZone(context, paListener);
	}

	/**
	 * 获取新浪微博分享对象
	 * 
	 * @param context
	 * @param paListener
	 * @return
	 */
	public static ShareSinaWeiBo getShareSinaWeiBo(Context context,
			PlatformActionListener paListener) {
		return new ShareSinaWeiBo(context, paListener);
	}

	/**
	 * 获取腾讯微博分享对象
	 * 
	 * @param context
	 * @param paListener
	 * @return
	 */
	public static ShareTencentWeiBo getShareTencentWeiBo(Context context,
			PlatformActionListener paListener) {
		return new ShareTencentWeiBo(context, paListener);
	}

}
