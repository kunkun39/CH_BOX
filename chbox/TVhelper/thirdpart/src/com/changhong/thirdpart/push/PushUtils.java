/**
 * 
 */
package com.changhong.thirdpart.push;

import java.util.HashSet;
import java.util.Set;

import com.changhong.common.system.AppConfig;
import com.changhong.common.system.MyApplication;
import com.changhong.thirdpart.common.DataContainer;
import com.changhong.thirdpart.common.IDataContainer;
import com.changhong.thirdpart.common.IDataListener;
import com.changhong.thirdpart.common.IThirdPartUtil;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;

/**
 * @author yves.yang
 * @category 使用方法:
 * 在要使用的activity,service 中先初始化，然后调用，
 * 每个要使用的activity,service尽量都初始化下，但在application中，
 * 就不需要初始化了。使用透传消息，需要注册监听者registerListener。
 */
public class PushUtils implements IThirdPartUtil,IDataContainer{
	
	private static final String INTENT_ACTION = "com.igexin.sdk.action.O9uKVt3AuQABhcdHsq2rw1";
	
	private static PushUtils mPushUtils = null;
	private Context mContext;	
	private PushBroadCastReceiver mReceiver = new PushBroadCastReceiver();
	private DataContainer dataContainer = new DataContainer();

/**
 * 屏蔽构造，注册观察者
 * ==================================================================================*/
	private PushUtils(){};
	
	@Override
	public void finish(Context context) {
		context.unregisterReceiver(mReceiver);
	}

	/**
	 * 注册监听者，获取透传消息，消息保存在Message,getData,getString("String")里面。
	 * 所谓透传消息，就是推送的，消息，监听器会直接接收到，然后反应，不会以通知的形式。
	 */
	@Override
	public void registerListener(IDataListener l) {
		dataContainer.registerListener(l);
	}

	@Override
	public void unregisterListener(IDataListener l) {
		dataContainer.unregisterListener(l);
	}

	/**
	 * 发送消息，所有监听器都能收到
	 */
	@Override
	public void dispatchMessage(Message message) {
		dataContainer.dispatchMessage(message);
	}
	
	
	/**==================================================================================*/	
	/**
	 * 获取初始化了的实例,Init 方法不允许是application中使用
	 * @param context： Application Context
	 */
	public PushUtils init(Context context)
	{			
		if (context == null) {
			return null;
		}
		mPushUtils.mContext = context.getApplicationContext();	
		
		PushManager.getInstance().initialize(context);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(INTENT_ACTION);
		context.registerReceiver(mReceiver, filter);
		
		return mPushUtils;
	}
	
	/**
	 * 获取单例
	 */
	public static PushUtils getInstance()
	{							
		if (mPushUtils == null){			
			synchronized (PushUtils.class) {
				if (mPushUtils == null) {
					mPushUtils = new PushUtils();					
				}
			}
		}
		return mPushUtils;		
	}
	
	/**
	 * 确认运行状态
	 */
	public boolean isRunning()
	{
		return PushManager.getInstance().isPushTurnedOn(mContext);
	}
	
	/**
	 * 开始接收推送
	 */
	public void start()
	{	
		if (AppConfig.NOT_USE_MSG_PUSH) {
			return ;
		}
		
		PushManager.getInstance().turnOnPush(mContext);
		
	}
	
	/**
	 * 暂停功能
	 */
	public void stop()
	{
		PushManager.getInstance().turnOffPush(mContext);
	}
	
	/**
	 * 停止服务，强度大于stop（）
	 */
	public void close()
	{
		PushManager.getInstance().stopService(mContext);
	}

/**
 * 私有函数
 * ===========================================================================================	
 */
	class PushBroadCastReceiver extends BroadcastReceiver
	{

		 /**
	     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
	     */	    
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	
	    	if (!intent.getAction().equals(INTENT_ACTION)) {
				return;
			}
	    	
	        Bundle bundle = intent.getExtras();
	        Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));
	
	        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
	            case PushConsts.GET_MSG_DATA:
	            	try {											
		                // 获取透传数据
		                // String appid = bundle.getString("appid");
		                byte[] payload = bundle.getByteArray("payload");
		
		                String taskid = bundle.getString("taskid");
		                String messageid = bundle.getString("messageid");
		
		                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
		                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
		                System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));
		
		                if (payload != null) {
		                    String data = new String(payload);
		
		                    Log.d("GetuiSdkDemo", "receiver payload : " + data);
		                    
		            		Message mesg = new Message();
		            		Bundle bdle = new Bundle();
		            		bdle.putString("String", data);
		            		mesg.setData(bdle);	  
		            		
		            		PushUtils push = getInstance();
		            		if (push != null) 
		            		{
		            			push.dispatchMessage(mesg);
		            			MyApplication.vibrator.vibrate(100);
		            		}
		                }
	            	} catch (Exception e) {
	            		e.printStackTrace();
					}
	                break;
	
	            case PushConsts.GET_CLIENTID:
	                break;
	
	            case PushConsts.THIRDPART_FEEDBACK:
	                break;
	
	            default:
	                break;
	        }	        
    		
	    }		
	    
	}


	
}
