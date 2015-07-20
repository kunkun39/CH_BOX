package com.changhong.tvhelper.service;

import android.app.*;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;

import com.changhong.common.db.sqlite.DatabaseContainer;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.service.EPGVersionService;
import com.changhong.common.system.AppConfig;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.*;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicProvider;
import com.changhong.touying.music.SetDefaultImage;
import com.changhong.touying.nanohttpd.NanoHTTPDService;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.activity.*;
import com.changhong.tvhelper.domain.OrderProgram;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

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
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                intent.setClass(ClientLocalThreadRunningService.this, TVChannelShowActivity.class);

                                pendingIntent = PendingIntent.getActivity(ClientLocalThreadRunningService.this, 0, intent, 0);
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
                                ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
                                final String shortClassName = info.topActivity.getClassName();

                                Dialog dialog = new AlertDialog.Builder(ClientLocalThreadRunningService.this)
                                        .setTitle("电视助手：预约节目已开始")
                                        .setMessage(program.getChannelName() + "\n" + program.getProgramName() + " " + program.getProgramStartTime())
                                        .setPositiveButton("播放", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                /**
                                                 * 如果是正在播放，就直接切换频道，如果没有播放，就跳转到播放页面
                                                 */
                                                if ("com.changhong.tvhelper.activity.TVChannelPlayActivity".equals(shortClassName)) {
                                                    Message message = new Message();
                                                    message.obj = program.getChannelName();
                                                    TVChannelPlayActivity.handler.sendMessage(message);
                                                } else {
                                                    Intent intent = new Intent();
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("channelname", program.getChannelName());
                                                    String index = program.getChannelIndex();
                                                    int channelSize = ClientSendCommandService.channelData.size();
                                                    for (int i = 0; i < channelSize; i++) {
                                                        Map<String, Object> map = ClientSendCommandService.channelData.get(i);
                                                        String channelIndex = (String) map.get("channel_index");
                                                        if (index.equals(channelIndex)) {
                                                            TVChannelPlayActivity.path = ChannelService.obtainChannlPlayURL(map);
                                                        }
                                                    }

                                                    intent.putExtras(bundle);
                                                    intent.setClass(ClientLocalThreadRunningService.this, TVChannelPlayActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                }
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        }).create();
                                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                dialog.show();
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

    class EPGDownloadThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    //sleep for 1 seconds for http server started
                    Thread.sleep(1000);

                    if (StringUtils.hasLength(ClientSendCommandService.serverIP)) {
                        getEPGList("http://" + ClientSendCommandService.serverIP + ":8000/epg_database_ver.json");
                    }

                    Thread.sleep(1000 * 60);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void getEPGList(String url) throws Exception {
            /**
             * 判断版本
             */
            if (url == null) {
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
                    if (serverVersion == 0 || (serverVersion > mobileVersion)) {
                        shouldUpdateDB = true;
                    }

                    /**
                     * 更新节目信息
                     */
                    if (shouldUpdateDB) {
                        InputStream in = WebUtils.httpGetRequest("http://" + ClientSendCommandService.serverIP + ":8000/epg_database.db");
                        File file = new File(MyApplication.epgDBCachePath, "epg_database.db");
                        if (file.exists()) {
                            file.delete();
                        }
                        IOUtils.copy(in, new FileOutputStream(file));
                        service.saveEPGVersion(serverVersion);

                        /**
                         * 重新初始化DB
                         */
                        if (MyApplication.databaseContainer == null) {
                            MyApplication.databaseContainer = new DatabaseContainer(ClientLocalThreadRunningService.this);
                        }
                        MyApplication.databaseContainer.reopenEPGDatabase();
                        Log.e(TAG, "update epg database to version " + serverVersion);
                    }
                } else {
                    Log.e(TAG, "未获取到服务器EPG Json");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                    ChannelService channelService = new ChannelService();
                    String weekIndexName = DateUtils.getWeekIndexName(0);
                    String date = DateUtils.getDayOfToday();

                    if (MyApplication.databaseContainer == null) {
                        MyApplication.databaseContainer = new DatabaseContainer(ClientLocalThreadRunningService.this);
                    }

                    //删除已经过期的预约节目
                    channelService.deleteOrderProgram(date);

                    //得到今天的预约列表
                    List<OrderProgram> orderPrograms = channelService.findOrderProgramsByWeek(weekIndexName);
                    for (OrderProgram orderProgram : orderPrograms) {

                        if (orderProgram.getProgramStartTime().compareTo(currentTime) == 0) {
                            channelService.deleteOrderProgram(orderProgram.getProgramName(), date);
                            //更新收藏界面
                            if (TVChannelShouCangShowActivity.mHandler != null) {
                                TVChannelShouCangShowActivity.orderProgramList.remove(orderProgram);
                                TVChannelShouCangShowActivity.mHandler.sendEmptyMessage(0);
                            }
                            Log.e("OrderProgram", orderProgram.getProgramStartTime());
                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = orderProgram;
                            handler.sendMessage(msg);
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
				if (musics!=null) {
					
				
				for (int i = 0; i < musics.size(); i++) {
					Music music=musics.get(i);
					
					SetDefaultImage.getInstance().startExecutor(null, music);
//					Bitmap bitmap = MediaUtil.getArtwork(ClientLocalThreadRunningService.this, music.getId(),
//							music.getArtistId(), true, false);
//					DiskCacheFileManager.saveSmallImage(bitmap, music.getPath());
				}
				}
			}
		}).start();
		
	}
}
