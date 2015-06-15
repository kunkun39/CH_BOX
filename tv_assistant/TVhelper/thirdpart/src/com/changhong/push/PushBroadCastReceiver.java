/**
 * 
 */
package com.changhong.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author yves.yang
 *
 */
public class PushBroadCastReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, PushService.class));		
		Log.d("PushBroadCastReceiver", "PushUtils.init(context).start()");
	}
}
