/**
 * 
 */
package com.changhong.thirdpart.common;

import com.changhong.thirdpart.location.LocationUtil;
import com.changhong.thirdpart.push.PushUtils;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * @author yves.yang
 * 开起一个service 用于监听透传消息
 */
public class MainService extends Service{
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		PushUtils.getInstance().init(this).start();		
		
		LocationUtil.getInstance().init(this).start();		
	}
		
	@Override
	public void onDestroy() {
		super.onDestroy();		
		PushUtils.getInstance().finish(this);
		LocationUtil.getInstance().finish(this);
	}
	
}
