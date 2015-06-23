/**
 * 
 */
package com.changhong.thirdpart.common;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author yves.yang
 *
 */
public class MainBroadCastReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, MainService.class));		
		Log.d("PushBroadCastReceiver", "PushUtils.init(context).start()");
	}
}
