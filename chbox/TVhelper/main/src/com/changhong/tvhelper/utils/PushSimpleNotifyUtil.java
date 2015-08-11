package com.changhong.tvhelper.utils;

import java.net.ContentHandler;
import java.util.List;
import java.util.Map;

import org.json.JSONStringer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.changhong.thirdpart.common.IDataListener;
import com.changhong.thirdpart.push.PushUtils;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.activity.TVHelperWelcomeActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.JsonReader;



public class PushSimpleNotifyUtil implements IDataListener{

	Context mContext;
	
	public PushSimpleNotifyUtil(Context context)
	{
		mContext = context;
		PushUtils.getInstance().init(context).start();
		PushUtils.getInstance().registerListener(this);
	}
	
	public void finish()
	{
		PushUtils.getInstance().unregisterListener(this);
	}
	
	@Override
	public void OnDataChanged(Message message) {
		if (message == null
				|| message.getData() == null
				|| !message.getData().containsKey("String")) {
			return ;
		}	
		String data = message.getData().getString("String");
		NotificationManager nm = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification.Builder builder = new Notification.Builder(mContext);
		
		Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);        
        intent.setClass(mContext, TVHelperWelcomeActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(mContext, 0, intent, 0));
        
		Map<String, String> listMap = null;
		try {
			listMap = JSON.parseObject(data, new TypeReference<Map<String, String>>(){});
		} catch (Exception e) {
			builder.setContentTitle(data);			
		}
		
		if (listMap != null) {
			builder.setContentTitle(listMap.get("T"));
			builder.setContentText(listMap.get("C"));			
		}		
		
		builder.setSmallIcon(R.drawable.applogo);
		builder.setTicker("长虹电视助手提醒您：有新消息，请查收!");
		builder.setDefaults(Notification.DEFAULT_ALL);	
		builder.setWhen(System.currentTimeMillis());
		Notification notification = builder.getNotification();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;		
		
		//builder.setVibrate(new long[]{500,0,200});
		nm.notify(mContext.getApplicationInfo().uid, notification);
		 
	}			
	
}
