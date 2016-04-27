package com.changhong.common.utils;

import android.content.Context;

import com.changhong.common.service.ClientSendCommandService;

/**
 * Created by Yves Yang on 2016/4/20.
 */
public class Utils {
    public static final String MESSAGE_SYSTEM_VOL = "system_vol";
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
    */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void requireServerVolume(Context context){
        QuickQuireMessageUtil.getInstance().doAction(context,MESSAGE_SYSTEM_VOL);
    }
}
