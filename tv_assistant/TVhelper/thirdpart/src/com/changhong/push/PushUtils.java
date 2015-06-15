/**
 * 
 */
package com.changhong.push;

import java.util.HashSet;
import java.util.Set;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

/**
 * @author yves.yang
 * @category 使用方法:
 * 在要使用的activity,service 中先初始化，然后调用，
 * 每个要使用的activity,service尽量都初始化下，但在application中，
 * 就不需要初始化了
 */
public class PushUtils {
	
	private static PushUtils mPushUtils = null;
	private Context mContext;
	private Set<PushUtilsLisner> lisners = new HashSet<PushUtilsLisner>();
		

/**
 * 开放接口用于接收透传消息
 * ==================================================================================*/
	public interface PushUtilsLisner
	{
		public void OnDataChanged(String data);
	}

/**
 * 屏蔽构造，注册观察者
 * ==================================================================================*/
	private PushUtils(){};
	
	public void registerListener(PushUtilsLisner l)
	{
		if (l == null) {
			return ;
		}
		lisners.add(l);
	}
	
	public void unregisterListener(PushUtilsLisner l)
	{
		if (l == null) {
			return ;
		}
		lisners.remove(l);		
	}
	
	public void dispatchMessage(String data)
	{
		for (PushUtilsLisner l : lisners) {
			l.OnDataChanged(data);
		}
	}
	/**==================================================================================*/	
	/**
	 * 获取初始化了的实例,Init 方法不允许是application中使用
	 * @param context： Application Context
	 */
	public static PushUtils init(Context context)
	{			
		if (context == null) {
			return null;
		}
		
		if (mPushUtils == null){			
			synchronized (PushUtils.class) {
				if (mPushUtils == null) {
					mPushUtils = new PushUtils();					
				}
			}
		}
		mPushUtils.mContext = context.getApplicationContext();	
		
		PushManager.getInstance().initialize(context);
		return mPushUtils;
	}
	
	public static PushUtils getInstance()
	{								
		return mPushUtils;		
	}
	
	public boolean isRunning()
	{
		return PushManager.getInstance().isPushTurnedOn(mContext);
	}
	
	public void start()
	{		
		PushManager.getInstance().turnOnPush(mContext);
	}
	
	public void stop()
	{
		PushManager.getInstance().turnOffPush(mContext);
	}
	
	public void close()
	{
		PushManager.getInstance().stopService(mContext);
	}



}
