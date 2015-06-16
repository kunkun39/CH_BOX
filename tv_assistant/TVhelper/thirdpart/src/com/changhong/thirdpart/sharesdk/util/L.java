package com.changhong.thirdpart.sharesdk.util;

import android.util.Log;

public class L {
	private static final String TAG = "thirdpart";
	private static final boolean ISLOG = true;

	public static void d(String msg) {
		if (ISLOG) {
			Log.d(TAG, msg);
		}
	}

	public static void e(String msg) {
		if (ISLOG) {
			Log.e(TAG, msg);
		}
	}
}
