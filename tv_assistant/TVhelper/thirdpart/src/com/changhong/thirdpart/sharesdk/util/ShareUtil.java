package com.changhong.thirdpart.sharesdk.util;

import static android.os.Environment.MEDIA_MOUNTED;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

/**
 * Created by wangxiufeng
 */
public class ShareUtil {

	private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
	public static final String DEFAUT_DATE_FORMAT = "yyyy-MM-dd";
	public static final String DEFAUT_TIME_FORMAT = "HH:mm";
	public static final String TAG = "ShareFileUtil  ";

	public static File getCutScreenImgDirectory(Context context) {
		File appCacheDir = null;
		String externalStorageState;
		try {
			externalStorageState = Environment.getExternalStorageState();
		} catch (NullPointerException e) { // (sh)it happens (Issue #660)
			externalStorageState = "";
			L.e(TAG + e);
		}
		L.d(TAG + "externalStorageState= " + externalStorageState);
		if (MEDIA_MOUNTED.equals(externalStorageState)
				&& hasExternalStoragePermission(context)) {
			appCacheDir = getExternalImageDir(context);
			L.d(TAG + "appcachedir==" + appCacheDir);
		}
		if (appCacheDir == null) {
			appCacheDir = context.getCacheDir();
		}
		if (appCacheDir == null) {
			String cacheDirPath = "/data/data/" + context.getPackageName()
					+ "/screencut/";
			appCacheDir = new File(cacheDirPath);
			L.d(TAG + "Can't define system cache directory! '%s' will be used."
					+ cacheDirPath);
		}
		return appCacheDir;
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	private static File getExternalImageDir(Context context) {
		File appCacheDir = new File(new File(
				Environment.getExternalStorageDirectory(),
				context.getPackageName()), "screencut");
		if (!appCacheDir.exists()) {
			if (!appCacheDir.mkdirs()) {
				L.d(TAG + "Unable to create external cache directory");
				return null;
			}
		}
		return appCacheDir;
	}

	private static boolean hasExternalStoragePermission(Context context) {
		int perm = context
				.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
		L.d(TAG + "ExternalStoragePermission==" + perm);
		return perm == PackageManager.PERMISSION_GRANTED;
	}

	public static boolean isImgFileExists(String path) {
		if (TextUtils.isEmpty(path)) {
			return false;
		}
		File file = new File(path);
		if (file.exists() && !file.isDirectory() && file.length() > 0) {
			return true;
		}
		return false;
	}

	public static String getDayOfToday() {
		Date day = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				DEFAUT_DATE_FORMAT + DEFAUT_TIME_FORMAT);
		return simpleDateFormat.format(day);
	}
}
