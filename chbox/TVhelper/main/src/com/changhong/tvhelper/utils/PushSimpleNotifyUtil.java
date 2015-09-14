package com.changhong.tvhelper.utils;

import java.util.Map;

import android.content.Context;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.changhong.common.utils.DialogUtil;
import com.changhong.common.utils.DialogUtil.DialogBtnOnClickListener;
import com.changhong.common.utils.DialogUtil.DialogMessage;
import com.changhong.thirdpart.common.IDataListener;
import com.changhong.thirdpart.push.PushUtils;

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
		Map<String, String> listMap = null;
		
		
		/*NotificationManager nm = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification.Builder builder = new Notification.Builder(mContext);

		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		intent.setClass(mContext, TVHelperWelcomeActivity.class);
		builder.setContentIntent(PendingIntent.getActivity(mContext, 0, intent,
				0));

		try {
			listMap = JSON.parseObject(data,
					new TypeReference<Map<String, String>>() {
					});
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
		nm.notify(mContext.getApplicationInfo().uid, notification);*/

		try {
			listMap = JSON.parseObject(data,
					new TypeReference<Map<String, String>>() {
					});
		} catch (Exception e) {

			DialogUtil.showPassInformationDialog(mContext, null, data,
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
							// TODO Auto-generated method stub

						}
					});

		}

		if (listMap != null) {

			DialogUtil.showPassInformationDialog(mContext, listMap.get("T"),
					listMap.get("C"), new DialogBtnOnClickListener() {

						@Override
						public void onCancel(DialogMessage dialogMessage) {
							if (dialogMessage.dialog != null
									&& dialogMessage.dialog.isShowing()) {
								dialogMessage.dialog.cancel();
							}
						}

						@Override
						public void onSubmit(DialogMessage dialogMessage) {
							

						}
					});

		}


	}

}
