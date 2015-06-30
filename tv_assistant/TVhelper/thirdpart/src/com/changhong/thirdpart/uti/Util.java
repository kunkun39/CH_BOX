/**
 * 
 */
package com.changhong.thirdpart.uti;

import android.R.integer;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.WindowManager;

/**
 * @author yves.yang
 *
 */
public class Util {

	public static void showToast(Context context ,String content,int time)
	{
		if (context == null
				|| content == null
				|| content.isEmpty()
				|| time <= 0) {
			return ;
		}
		
		Util util = new Util();
		Dialog dlg = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);			
		dlg = builder.setMessage(content).create();
		dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dlg.getWindow().setGravity(Gravity.BOTTOM);
		dlg.show();
		Runnable runnable = util.new ToastRunable(dlg);
		new Handler(context.getMainLooper()).postDelayed(runnable, time);
	}
	
	private class ToastRunable implements Runnable
	{
		Dialog dlg = null;
		ToastRunable(Dialog dialog)
		{
			dlg = dialog;
		}
		@Override
		public void run() {
			if (dlg != null) {
				dlg.dismiss();
				dlg = null;
			}
		}
		
	}
	
}
