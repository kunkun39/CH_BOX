package com.changhong.tvhelper.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import com.changhong.common.utils.*;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.service.EPGVersionService;
import com.changhong.common.system.AppConfig;
import com.changhong.common.system.MyApplication;
import com.changhong.common.widgets.IpSelectorDataServer;
import com.changhong.common.utils.DateUtils;
import com.changhong.common.utils.DialogUtil;
import com.changhong.common.utils.MobilePerformanceUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.utils.SystemUtils;
import com.changhong.common.utils.WebUtils;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicProvider;
import com.changhong.touying.music.SetDefaultImage;
import com.changhong.touying.nanohttpd.NanoHTTPDService;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.activity.TVChannelPlayActivity;
import com.changhong.tvhelper.activity.TVChannelShowActivity;
import com.changhong.tvhelper.domain.OrderProgram;
import com.changhong.tvhelper.utils.PushSimpleNotifyUtil;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;

/**
 * Created by Jack Wang
 */
public class ClientLocalThreadRunningService extends Service {

    private static final String TAG = "ClientLocalThreadRunningService";

    private ActivityManager manager;

    private static Handler handler;

    private Notification notification;

    private PendingIntent pendingIntent;

    private NotificationManager notificationManager;

    private PowerManager powerManager;
    
    private PushSimpleNotifyUtil pushNotifyUtil;
    
    /**预约到期未看节目**/
    private static LinkedList<OrderProgram> yuyueprograms=new LinkedList<OrderProgram>();
    /**预约提示对话框*/
    private static Dialog dialog_yuyue=null;
    /**预约提示列表adapter*/
    private static YuYueAdapter adapter_yuyue=null;
    
    
    

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initData();

        initViewEvent();

        initThreads();
    }

    @Override
    public void onDestroy() {    
    	super.onDestroy();
    	if(pushNotifyUtil != null)
    	{
    		pushNotifyUtil.finish();
    	}
    }
    
    private void initThreads() {
        /**
         * 启动HTTPD服务监视器
         */
        new HttpServerMonitorThread(ClientLocalThreadRunningService.this).start();

        /**
         * 性能监控
         */
        new PerformanceMonitorThread(ClientLocalThreadRunningService.this).start();

        /**
         * 启动缓存图片
         */
        new PictureCacheForLocalThread().start();

        /**
         * 启动EPG信息获得列表
         */
        new EPGDownloadThread().start();

        /**
         * 启动预约管理线程
         */
        new OrderProgramThread().start();
    }

    public void initViewEvent() {
        manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (pushNotifyUtil == null) {
        	pushNotifyUtil = new PushSimpleNotifyUtil(this);
		}
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        final OrderProgram program = (OrderProgram) msg.obj;
                        //判断是否锁屏还有锁屏但是还亮着的
                        if (!powerManager.isScreenOn() || SystemUtils.isScreenLocked(ClientLocalThreadRunningService.this)) {
                            try {
                                Intent intent = new Intent();
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("channelname", program.getChannelName());
                                intent.setClass(ClientLocalThreadRunningService.this, TVChannelPlayActivity.class);

                                pendingIntent = PendingIntent.getActivity(ClientLocalThreadRunningService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                notification = new Notification();
                                notification.icon = R.drawable.applogo;
                                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                                notification.tickerText = "电视助手：您有预约节目";
                                notification.defaults = Notification.DEFAULT_SOUND;
                                notification.setLatestEventInfo(ClientLocalThreadRunningService.this, "电视助手：您有预约节目", program.getChannelName() + "-" + program.getProgramName(), pendingIntent);
                                notificationManager.notify(0, notification);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {

                            try {
//                              ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
//                              final String shortClassName = info.topActivity.getClassName();
//                              String content=program.getChannelName() + "\n" + program.getProgramName() + " " + program.getProgramStartTime();
                            	if (yuyueprograms==null) {
                            		yuyueprograms=new LinkedList<OrderProgram>();
								}
                            	yuyueprograms.add(0, program);
                            		showYuyueDialog();
								} catch (Exception e) {
									e.printStackTrace();
                            }
                        }
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
            }
        };
    }
    /**
     * 弹出预约提示对话框
     * 如果对话框已经弹出只更改数据，没有弹出则弹出对话框
     */
	private void showYuyueDialog() {
		Context context = ClientLocalThreadRunningService.this;
		if (adapter_yuyue == null) {
			adapter_yuyue = new YuYueAdapter(context);
		}
		if (dialog_yuyue == null) {
			dialog_yuyue = new Dialog(context, R.style.Dialog_nowindowbg);
			View view = LayoutInflater.from(context).inflate(
					R.layout.dialog_yuyue_tongzhi, null);
			dialog_yuyue.setContentView(view);
			ListView lv_program = (ListView) view
					.findViewById(R.id.lv_yuyuejiemu);
			view.findViewById(R.id.bt_yytzdia_cancel).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (dialog_yuyue != null
									&& dialog_yuyue.isShowing()) {
								dialog_yuyue.dismiss();
							}
							if (yuyueprograms != null) {
								yuyueprograms.clear();
							}
						}
					});
			dialog_yuyue.getWindow().setType(
					WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			lv_program.setAdapter(adapter_yuyue);
		}
		if (!dialog_yuyue.isShowing()) {
			dialog_yuyue.show();
		}
		LayoutParams param = dialog_yuyue.getWindow().getAttributes();
		param.gravity = Gravity.CENTER;
		param.width = (int) context.getResources().getDimension(
				R.dimen.dialog_width);
		int dialogheight = 150;// 根据数据动态更改对话框高度
		if (yuyueprograms == null || yuyueprograms.size() == 1) {
			dialogheight = 148;
		} else if (yuyueprograms.size() == 2) {
			dialogheight = 195;
		} else {
			dialogheight = 240;
		}
		param.height = DialogUtil.dipTopx(context, dialogheight);
		dialog_yuyue.getWindow().setAttributes(param);
		
		adapter_yuyue.notifyDataSetChanged();
		Toast.makeText(this, "预约提示：请点击节目列表切换频道，或点取消关闭列表", 5000).show();
	}
	class YuYueAdapter extends BaseAdapter {
		private LayoutInflater minflater;

		public YuYueAdapter(Context context) {
			this.minflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return yuyueprograms==null?0:yuyueprograms.size();
		}

		public Object getItem(int position) {
			return yuyueprograms==null?null:yuyueprograms.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder vh = null;
			if (convertView == null) {
				vh = new ViewHolder();
				convertView = minflater.inflate(R.layout.yuyuetongzhi_item, null);
				vh.programname = (TextView) convertView.findViewById(R.id.tv_programname);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			if (getItem(position)!=null) {
				final OrderProgram program=yuyueprograms.get(position);
				vh.programname.setText("预约节目"+(position+1)+"：  "+program.getChannelName() + "\n" + program.getProgramName() + " " + program.getProgramStartTime());
				vh.programname.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						 ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
						 final String shortClassName = info.topActivity.getClassName();
						if ("com.changhong.tvhelper.activity.TVChannelPlayActivity"
								.equals(shortClassName)) {
							Message message = new Message();
							message.obj = program.getChannelName();
							TVChannelPlayActivity.handler.sendMessage(message);
						} else {
							Intent intent = new Intent();
							Bundle bundle = new Bundle();
							bundle.putString("channelname",
									program.getChannelName());
							String name = program.getChannelName();
							int channelSize = ClientSendCommandService.channelData
									.size();
							for (int i = 0; i < channelSize; i++) {
								Map<String, Object> map = ClientSendCommandService.channelData
										.get(i);
								String channelName = (String) map
										.get("service_name");
								if (name.equals(channelName)) {
									TVChannelPlayActivity.path = ChannelService
											.obtainChannlPlayURL(map);
								}
							}

							intent.putExtras(bundle);
							intent.setClass(ClientLocalThreadRunningService.this,
									TVChannelPlayActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
						if (dialog_yuyue != null
								&& dialog_yuyue.isShowing()) {
							dialog_yuyue.dismiss();
						}
						if (yuyueprograms != null) {
							yuyueprograms.clear();
						}
					}
				});
			}
			return convertView;
		}

		public final class ViewHolder {
			public TextView programname;
		}
	}

    /**
     * ******************************************http server monitor  thread******************************************
     */

    /**
     * 说明：进入投影界面的时候，打开此服务, 还有就是用户一直处于投影界面，然而这个时候服务已经关闭，但是用户现在点击投影，所以这里需要先检查有没有HTTP服务， 如果没有就需要开启
     * <p/>
     * <p/>
     * 1 - 这个线程是用来检查是否HTTP服务还在使用 1个小时一次
     * 2 - 如果用户处于投影图片，视频播放，音乐播放时NanoHTTPDService.serverUsing = true的时候，不会停止该服务
     * 3 - 当图片，视频，音乐播放播放结束后，NanoHTTPDService.serverUsing = false，就会停止该服务
     */
    private class HttpServerMonitorThread extends Thread {

        private Context context;

        private HttpServerMonitorThread(Context context) {
            this.context = context;
        }

        public void run() {
            while (true) {
                if (NanoHTTPDService.httpServer != null && !MobilePerformanceUtils.httpServerUsing) {
                    /**
                     * 启动Http服务
                     */
                    Intent http = new Intent(context, NanoHTTPDService.class);
                    stopService(http);
                }
                SystemClock.sleep(1000 * 60 * 60);
            }
        }
    }

    /**
     * ******************************************performance manager thread ********************************************
     */

    /**
     * @link {com.changhong.common.utils.MobilePerformanceUtils}
     */
    private class PerformanceMonitorThread extends Thread {

        private Context context;

        private PerformanceMonitorThread(Context context) {
            this.context = context;
        }

        public void run() {
            while (true) {
                /**
                 * 如果点击屏幕已经操作了三分钟
                 */
                if (MobilePerformanceUtils.sharingRemoteControlling) {
                    Long remoteControlDuringTime = System.currentTimeMillis() - MobilePerformanceUtils.sharingRemoteControlLastHappen;
                    if ((remoteControlDuringTime - 1000 * 60 * 3) > 0) {
                        MobilePerformanceUtils.sharingRemoteControlling = false;
                    }
                }

                MobilePerformanceUtils.closePerformance(context);
                SystemClock.sleep(1000 * 60);
            }
        }
    }

    /**
     * ******************************************picture for local cache thread************************************
     */

    class PictureCacheForLocalThread extends Thread {
        @Override
        public void run() {
            Map<String, List<String>> packageList = new HashMap<String, List<String>>();

            try {
                //sleep for 1 seconds for http server started
                Thread.sleep(1000);

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = ClientLocalThreadRunningService.this.getContentResolver();

                // search images
                Cursor cursor = contentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?", new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED + " desc");

                //put image urls into json object
                while (cursor.moveToNext()) {
                    String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    String[] tokens = StringUtils.delimitedListToStringArray(imagePath, File.separator);
                    String packageName = tokens[tokens.length - 2];

                    //组装相同路径下的package
                    List<String> files = packageList.get(packageName);
                    if (files == null) {
                        files = new ArrayList<String>();
                    }
                    if (AppConfig.MOBILE_CARMERS_PACKAGE.contains(packageName.toLowerCase()) || files.size() < 4) {
                        files.add(imagePath);
                    }
                    packageList.put(packageName, files);
                }
                cursor.close();

                //begin to cache
                Set<String> keys = packageList.keySet();
                if (keys != null) {
                    for (String key : keys) {
                        List<String> images = packageList.get(key);
                        if (images != null) {
                            for (String image : images) {
                                if (!DiskCacheFileManager.isSmallImageExist(image).equals("")) {
                                    MyApplication.preImageLoader.loadImage("file://" + image, MyApplication.viewOptions, null);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ******************************************epg info download thread************************************
     */

    class EPGDownloadThread extends Thread implements Observer{
    	Handler mHandler = null;
    	Dialog dialog = null;
    	Runnable runnable;
        @Override
        public void run() {
        	
        	Looper.prepare();
        	if (mHandler == null) {
        		mHandler = new Handler(Looper.myLooper());        		
			}    
        	IpSelectorDataServer.getInstance().addObserver(this);
        	runnable = new Runnable() {
				
				@Override
				public void run() {
					try {
	                    //sleep for 1 seconds for http server started
	                    Thread.sleep(1000);
	 
	                    if (StringUtils.hasLength(IpSelectorDataServer.getInstance().getCurrentIp())) {
	                        getEPGList("http://" + IpSelectorDataServer.getInstance().getCurrentIp() + ":8000/epg_database_ver.json");
	                    }
	                    //every three minus, the client go to server to check                  

	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
					mHandler.postDelayed(this, 20 * 1000);
				}
			};
        	mHandler.post(runnable);
                
            Looper.loop();
            
        }

        private synchronized void getEPGList(String url) throws Exception {
            /**
             * 如果不是WIFI环境，不允许访问
             */
            if (url == null || !NetworkUtils.isWifiConnected(ClientLocalThreadRunningService.this)) {
                return;
            }
            

            //get network json data
            String sss = null;
            URL urlAddress = null;
            try {
                urlAddress = new URL(url);
                HttpURLConnection hurlconn = (HttpURLConnection) urlAddress.openConnection();
                hurlconn.setRequestMethod("GET");
                hurlconn.setConnectTimeout(2000);
                hurlconn.setRequestProperty("Charset", "UTF-8");
                hurlconn.setRequestProperty("Connection", "Close");
                if (hurlconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    hurlconn.connect();
                    InputStream instream = hurlconn.getInputStream();
                    InputStreamReader inreader = new InputStreamReader(instream, "UTF-8");
                    StringBuffer stringappend = new StringBuffer();
                    char[] b = new char[256];
                    int length = -1;
                    while ((length = inreader.read(b)) != -1) {
                        stringappend.append(new String(b, 0, length));
                    }
                    sss = stringappend.toString();
                    Log.i(TAG, sss);
                    inreader.close();
                    instream.close();
                } else {
                    Log.e(TAG, ">>>>>>>hurlconn.getResponseCode()!= HttpURLConnection.HTTP_OK");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (StringUtils.hasLength(sss)) {
                    boolean shouldUpdateDB = false;
                    EPGVersionService service = new EPGVersionService(ClientLocalThreadRunningService.this);
                    /**
                     * 判断版本
                     */

                    JSONObject allVersions = new JSONObject(sss);
                    JSONObject version = (JSONObject) allVersions.getJSONArray("EPG_DATABASE").get(0);
                    int serverVersion = version.getInt("epg_db_version");
                    int mobileVersion = service.getEPGVersion();
                    if (serverVersion == 0 || serverVersion != mobileVersion) {
                        shouldUpdateDB = true;
                    }

                    /**
                     * 更新节目信息
                     */
                    if (shouldUpdateDB) {
                    	ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                    	ComponentName componentName =  activityManager.getRunningTasks(1).get(0).topActivity;
                    	
                    	if(componentName.getPackageName().contains("com.changhong"))
                    	{
                    		dialog = DialogUtil.showPassInformationDialog(ClientLocalThreadRunningService.this, "提示", "正在更新电视节目，请稍候...", null);
                    		dialog.setCanceledOnTouchOutside(false);                    		
                    	}
                    	
                        InputStream in = WebUtils.httpGetRequest("http://" + IpSelectorDataServer.getInstance().getCurrentIp() + ":8000/epg_database.db");
                        File fileTmp = new File(MyApplication.epgDBCachePath, "epg_database.db.tmp");
                        if (fileTmp.exists()) {
                        	fileTmp.delete();
                        }

                        SystemClock.sleep(1000);
                        
                        IOUtils.copy(in, new FileOutputStream(fileTmp));
                        File file = new File(MyApplication.epgDBCachePath, "epg_database.db");
                        if (file.exists()) {
                            file.delete();
                        }
                        SystemClock.sleep(1000);
                        fileTmp.renameTo(file);
                        
                        service.saveEPGVersion(serverVersion);
                        if (dialog != null) {                        	
                        	dialog.cancel();
						}
                        Log.d(TAG, "电视节目更新成功");                        

                        /**
                         * 重新初始化DB
                         */
                        Intent intent = new Intent(AppConfig.BROADCAST_INTENT_EPGDB_UPDATE);
                        ClientLocalThreadRunningService.this.sendBroadcast(intent);
                        Log.e(TAG, "update epg database to version " + serverVersion);
                    }
                } else {
                    Log.e(TAG, "未获取到服务器EPG Json");
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (dialog != null) {
                	Toast.makeText(ClientLocalThreadRunningService.this, "更新失败，稍后更新", Toast.LENGTH_SHORT).show();
                	dialog.cancel();
				}
            }
        }

		@Override
		public void update(Observable observable, Object data) {
			mHandler.removeCallbacks(runnable);
			mHandler.postAtFrontOfQueue(runnable);
		}
    }

    /**
     * ******************************************booked program notification thread************************************
     */

    class OrderProgramThread extends Thread {
        @Override
        public void run() {

            while (true) {
                try {
                    String currentTime = DateUtils.getCurrentTimeStamp();
                    ChannelService channelService = new ChannelService(ClientLocalThreadRunningService.this);
                    String weekIndexName = DateUtils.getWeekIndexName(0);
                    String date = DateUtils.getDayOfToday();

                    //删除已经过期的预约节目
                    channelService.deleteOrderProgram(date);

                    //得到今天的预约列表
                    List<OrderProgram> orderPrograms = channelService.findOrderProgramsByWeek(weekIndexName);
                    if (orderPrograms != null) {
                        for (OrderProgram orderProgram : orderPrograms) {

                            if (orderProgram.getProgramStartTime().compareTo(currentTime) == 0) {
                                channelService.deleteOrderProgram(orderProgram.getProgramName(), date);
                                //更新收藏界面 上面代码直接对数据库进行了修改，让收藏界面自己从数据库去更新，这里就不再进行操作。2015.7.15.debug-by-cym
//                            if (TVChannelShouCangShowActivity.mHandler != null) {
//                                TVChannelShouCangShowActivity.orderProgramList.remove(orderProgram);
//                                TVChannelShouCangShowActivity.mHandler.sendEmptyMessage(1);
//                            }
                                Log.e("OrderProgram", orderProgram.getProgramStartTime());
                                Message msg = new Message();
                                msg.what = 0;
                                msg.obj = orderProgram;
                                handler.sendMessage(msg);
                            }
                        }
                    }

                    //休息30S
                    Thread.sleep(1000 * 30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    
	private void initData() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				MusicProvider provider = new MusicProvider(ClientLocalThreadRunningService.this);
				List<Music> musics = (List<Music>) provider.getList();
				SetDefaultImage.getInstance().setContext(ClientLocalThreadRunningService.this);
				for (int i = 0; i < musics.size(); i++) {
					Music music=musics.get(i);
					
					SetDefaultImage.getInstance().startExecutor(null, music);
//					Bitmap bitmap = MediaUtil.getArtwork(ClientLocalThreadRunningService.this, music.getId(),
//							music.getArtistId(), true, false);
//					DiskCacheFileManager.saveSmallImage(bitmap, music.getPath());
				}
			}
		}).start();
		
		
		
	}
}
