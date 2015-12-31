package com.changhong.tvhelper.utils;

import java.util.HashMap;
import java.util.Map;


import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baidu.cyberplayer.utils.bu;

import com.changhong.common.utils.DialogUtil;
import com.changhong.common.utils.DialogUtil.DialogBtnOnClickListener;
import com.changhong.common.utils.DialogUtil.DialogMessage;
import com.changhong.thirdpart.common.IDataListener;
import com.changhong.thirdpart.push.PushUtils;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.activity.TVChannelSearchActivity;
import com.changhong.tvhelper.activity.TVHelperWelcomeActivity;

public class PushSimpleNotifyUtil implements IDataListener {

	Context mContext;

	public PushSimpleNotifyUtil(Context context) {
		mContext = context;
		PushUtils.getInstance().init(context).start();
		PushUtils.getInstance().registerListener(this);
	}

	public void finish() {
		PushUtils.getInstance().unregisterListener(this);
	}

	@Override
	public void OnDataChanged(Message message) {
		if (message == null || message.getData() == null
				|| !message.getData().containsKey("String")) {
			return;
		}
		String data = message.getData().getString("String");
		
		ActivityManager activityManager = (ActivityManager)mContext.getSystemService(Service.ACTIVITY_SERVICE);
		if(activityManager.getRunningTasks(1).get(0).topActivity.getPackageName().contains("com.changhong"))
		{
			String mesgData = data;
			String ObjClass = null;
			try {
				JSONObject o = JSON.parseObject(data);
				if(o.get("C") != null)
				{
					mesgData = o.get("C").toString();
				}
				if (o.get("TO") != null) {
						ObjClass = "com.changhong." + o.get("TO").toString();					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			final String tempString = ObjClass;
			DialogUtil.showPassInformationDialog(mContext, null, mesgData,
					new DialogBtnOnClickListener() {

						@Override
						public void onCancel(DialogMessage dialogMessage) {
							if (dialogMessage.dialog != null
									&& dialogMessage.dialog.isShowing()) {
								dialogMessage.dialog.cancel();
							}
						}

						@Override
						public void onSubmit(DialogMessage dialogMessage) {	
							Class<?> tempClass = null;
							if (tempString != null 
									&& tempString.length() > 0 ) {
								try {
									tempClass = Class.forName(tempString);
								} catch (ClassNotFoundException e) {
									e.printStackTrace();
								}
							}
							Intent intent = new Intent(mContext, tempClass);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							mContext.startActivity(intent);							
						}
					});
		}
		else {
			NotificationManager nm = (NotificationManager) mContext
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification.Builder builder = new Notification.Builder(mContext);

			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(mContext, TVHelperWelcomeActivity.class);
			
			builder.setContentTitle(mContext.getString(R.string.app_name));
			builder.setContentText(data);

			try {
				JSONObject o = JSON.parseObject(data);

				builder.setContentTitle(mContext.getString(R.string.app_name));
				builder.setContentText(data);
				if (o.get("T") != null) {
					builder.setContentTitle(o.get("T").toString());					
				}
				if (o.get("C") != null) {
					builder.setContentText(o.get("C").toString());
				}
				//Class.forName("com.changhong.tvhelper.activity.TVChannelSearchActivity")
				if (o.get("TO") != null) {					
					intent.setClass(mContext, Class.forName("com.changhong." + o.get("TO").toString()));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			builder.setContentIntent(PendingIntent.getActivity(mContext, 0, intent,
					PendingIntent.FLAG_CANCEL_CURRENT));
			builder.setSmallIcon(R.drawable.applogo);
			builder.setTicker(mContext.getString(R.string.notify_mesg));
			
			builder.setDefaults(Notification.DEFAULT_ALL);
			builder.setWhen(System.currentTimeMillis());
			Notification notification = builder.getNotification();
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			nm.notify(mContext.getApplicationInfo().uid, notification);
		}


	}

}
