package com.changhong.tvserver.touying.other;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.changhong.tvserver.R;
import com.changhong.tvserver.utils.MyProgressDialog;
import com.changhong.tvserver.utils.TextImageButton;
import com.chome.virtualkey.virtualkey;


import android.R.integer;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.tvserver.touying.pdf.PDFViewActivity;
import com.changhong.tvserver.utils.MyProgressDialog;

public class OtherShowService extends Service{

	final static String TAG = "PPTShowService";
	final static String PPT_READY = "PPT_READY";
	final static int TYPE_PDF = 1;
	final static int TYPE_PPT = 2;
	final static int TYPE_NONE = 0;
	
	Handler mHandler = null;	
	Runnable mOpenPPTRunnable = null;
	Runnable mOpenPDFRunable = null;
	OtherProgressDialog mAlertDialog = null;	
	Thread mDownloadFileThread = null;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		mHandler = new Handler(getMainLooper());
		mOpenPPTRunnable = new Runnable() {			
			@Override
			public void run() {				
				if(mAlertDialog != null){
					mAlertDialog.dismiss();
					mAlertDialog = null;
            	}

				Intent intent = new Intent("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()+"/tmp.ppt"));
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                startActivity(intent);
                stopSelf();

            	
			}
		};
		mOpenPDFRunable = new Runnable() {
			
			@Override
			public void run() {
				if(mAlertDialog != null){
					mAlertDialog.dismiss();
					mAlertDialog = null;
            	}
            	Intent intent = new Intent(OtherShowService.this, PDFViewActivity.class);                
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);
                
                Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()+"/tmp.pdf"));
                intent.setDataAndType(uri, "application/pdf");
                startActivity(intent);
                stopSelf();
			}
		};				
		
	}	
	
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (intent != null) {
			openFile(intent.getData().toString());
		}
	}
	
	private void openFile(String msg) {	
		int fileType = TYPE_NONE;
		final Runnable runnable;
		final String tempFile;
		if (msg.endsWith(".ppt")
				|| msg.endsWith(".pptx")) {
			fileType = TYPE_PPT;
			tempFile = Environment.getExternalStorageDirectory().getPath()+"/tmp.ppt";
			runnable = mOpenPPTRunnable;
		}else if (msg.endsWith(".pdf")) {
			fileType = TYPE_PDF;
			tempFile = Environment.getExternalStorageDirectory().getPath()+"/tmp.pdf";
			runnable = mOpenPDFRunable;
		}
		else {
			tempFile = null;
			runnable = null;
		}
		if (fileType == TYPE_PPT) {
			if(((ActivityManager)this.getSystemService(Service.ACTIVITY_SERVICE)).getRunningTasks(1).get(0).topActivity.getClassName().contains("cn.wps.moffice"))
			{
				Toast.makeText(this, "请先退出PPT,再进行投影，谢谢！", Toast.LENGTH_SHORT).show();
				return ;
			}
		}
		
		if (mAlertDialog != null) {
			mAlertDialog.cancel();
			mAlertDialog = null;			
		}				
			   			
		mAlertDialog= new OtherProgressDialog(this);
		mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		mAlertDialog.show();		    
	   
    	final String pptUrl = msg.replace("other_open:", "");
		Log.e("ppt", "pptUrl:" + pptUrl);
    	
    	if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
    		Toast.makeText(this, "无SD卡，无法推送PDF文件!", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	mDownloadFileThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Looper.prepare();				
				try {
		    		/**
		    		 * open pdf
		    		 */
		            URL urlAddress = null;
		            Uri uri = Uri.parse(pptUrl);
		            //urlAddress = new URL(URLEncoder.encode(pptUrl, "utf-8"));
		            
		            Log.d("TIME", String.valueOf(System.currentTimeMillis()));
		            urlAddress = new URL(uri.getScheme(), uri.getHost(), uri.getPort(), URLEncoder.encode(uri.getPath(), "utf-8"));
		            //urlAddress = new URL(pptUrl);
		            HttpURLConnection hurlconn = (HttpURLConnection) urlAddress.openConnection();
		            hurlconn.setRequestMethod("GET");
		            hurlconn.setConnectTimeout(2000);
		            hurlconn.setRequestProperty("Connection", "Close");
		            hurlconn.setReadTimeout(10000);
		            try {
		            	if (hurlconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
		            		Log.d("TIME", String.valueOf(System.currentTimeMillis()));
			                hurlconn.connect();
			                mAlertDialog.setMax(hurlconn.getContentLength());
			                mAlertDialog.setProgress(0);
			                
			                InputStream instream = new BufferedInputStream(hurlconn.getInputStream());
			                File file=new File(tempFile);
			                if(file.exists()){
			                	file.delete();
			                	file.createNewFile();
			                }		
			                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			                byte[] b = new byte[8192];
			                int length = -1;
			                long fileProgress = 0l;
			                Log.d("TIME1", String.valueOf(System.currentTimeMillis()));
			                while ((length = instream.read(b)) != -1) {
			                	Log.d("TIME2", String.valueOf(System.currentTimeMillis()));
			                	bos.write(b, 0, length);
			                	synchronized (this) {
			                		wait(1);
								}
			                	fileProgress += length;
			                	mAlertDialog.setProgress(fileProgress);
			                	Log.d("TIME2", String.valueOf(System.currentTimeMillis()));
			                }
			                instream.close();
			                bos.close();
			                mHandler.post(runnable);
			            } else {
			                Log.e(TAG, ">>>>>>>hurlconn.getResponseCode()!= HttpURLConnection.HTTP_OK");
			            }
					} catch (IOException e) {
						e.printStackTrace();
						if ( hurlconn != null) {
							hurlconn.disconnect();
						}
						if (mAlertDialog != null) {
							mAlertDialog.cancel();
							mAlertDialog = null;			
						}
						Toast.makeText(OtherShowService.this.getApplicationContext(), "网络异常，下载退出", Toast.LENGTH_SHORT).show();
					}
		            
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    		Toast.makeText(OtherShowService.this.getApplicationContext(), "网络异常，下载退出", Toast.LENGTH_SHORT).show();
		    	}
				finally
				{
					mDownloadFileThread = null;
					if (mAlertDialog != null) {
						mAlertDialog.cancel();
						mAlertDialog = null;			
					}
				}
				Looper.loop();
			}
			
		});
    	mDownloadFileThread.start();
		if (mAlertDialog != null)
    		mAlertDialog.setThread(mDownloadFileThread);
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO 自动生成的方法存根
		return null;
	}
	
	class OtherProgressDialog extends MyProgressDialog
	{ 
		Thread mThread = null;
		
		public OtherProgressDialog(Context context) {
			super(context);
			setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					if(mThread != null)
					{
						mThread.interrupt();
						mThread = null;
					}	
					
				}
			});
		}
		
		void setThread(Thread thread)
		{
			mThread = thread;
		}				
					
		
	}

}
