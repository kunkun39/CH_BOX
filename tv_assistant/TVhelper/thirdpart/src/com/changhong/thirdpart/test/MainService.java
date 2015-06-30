/**
 * 
 */
package com.changhong.thirdpart.test;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.changhong.thirdpart.common.IDataListener;
import com.changhong.thirdpart.location.LocationAttribute;
import com.changhong.thirdpart.location.LocationUtil;
import com.changhong.thirdpart.push.PushUtils;
import com.changhong.thirdpart.sharesdk.util.L;
import com.changhong.thirdpart.uti.Util;

/**
 * @author yves.yang 开起一个service 用于监听透传消息
 */
public class MainService extends Service implements IDataListener {
	public static final String TAG = "ThirdpartTest";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		L.e(TAG+"mainservice oncreate");
		// push功能初始化
		PushUtils.getInstance().init(this).start();
		PushUtils.getInstance().registerListener(this);
		// 定位功能初始化
		LocationUtil.getInstance().init(this).start();
		PushUtils.getInstance().registerListener(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		PushUtils.getInstance().finish(this);
		LocationUtil.getInstance().finish(this);
	}

	@Override
	public void OnDataChanged(Message message) {
		Bundle bundle = message.getData();		
		if (bundle.containsKey("String")) {
			String dataString = (String) bundle.get("String");
			Util.showToast(this, dataString, 3000);
		} else if (bundle.containsKey("LocationAttribute")) {
			LocationAttribute locationAttribute = ((LocationAttribute) bundle
					.get("LocationAttribute"));
			if (locationAttribute.getAddress() != null) {
				Util.showToast(this, locationAttribute.getAddress(), 3000);
			}
		}

	}

}
