package com.changhong.tvhelper.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

//import com.changhong.faq.activity.QuestionListActivity;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.AppConfig;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.DateUtils;
import com.changhong.common.utils.DialogUtil;
import com.changhong.common.utils.DialogUtil.DialogBtnOnClickListener;
import com.changhong.common.utils.DialogUtil.DialogMessage;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.widgets.BoxSelecter;
import com.changhong.common.widgets.IpSelectorDataServer;
import com.changhong.setting.activity.SettingActivity;
import com.changhong.setting.service.UpdateLogService;
import com.changhong.setting.service.UserUpdateService;
import com.changhong.setting.view.AppHelpDialog;
import com.changhong.touying.activity.MusicCategoryActivity;
import com.changhong.touying.activity.OtherDetailsActivity;
import com.changhong.touying.activity.PictureCategoryActivity;
import com.changhong.touying.activity.VedioCategoryActivity;
import com.changhong.touying.nanohttpd.NanoHTTPDService;
import com.changhong.touying.service.M3UListProviderService;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.service.AppLogService;

public class TVHelperMainActivity extends Activity {

    private static final String TAG = "TVHelperMainActivity";
    /**************************************************IP连接部分*******************************************************/

    private BoxSelecter ipSelecter = null;
    boolean isReadyExit = false;
    Runnable exitRunnable;
    /**
     * message handler
     */
    public static Handler mhandler = null;    

    ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_helper_main);
        
        //setContentView(R.layout.activity_maim_new);
        setContentView(R.layout.activity_maim_muil_lang_pan);
        initHelpDialog();
        initViewAndEvent();

        initUpdateThread();

        initMedia();
    }

    private void initViewAndEvent() {
        /**
         * init all views
         */

        Button controller = (Button) findViewById(R.id.main_control);
        Button tvplayer = (Button) findViewById(R.id.main_player);
        Button setting = (Button) findViewById(R.id.main_setting);
        Button shoucang = (Button) findViewById(R.id.main_soucang);
        Button sousuo = (Button) findViewById(R.id.main_sousuo);
        View musicTouYing = findViewById(R.id.main_music_touying);
        View vedioTouYing =  findViewById(R.id.main_vedio_touying);
        View pictureTouYing =  findViewById(R.id.main_picture_touying);
        View otherTouTing =  findViewById(R.id.main_other_touying);
        View screenMirror = findViewById(R.id.screen_mirror);
        if (screenMirror != null){
            screenMirror.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startMirrcast();
                }
            });
        }
//        Button power = (Button) findViewById(R.id.power);
//        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.banner);
        
//        linearLayout.setBackgroundResource(R.drawable.title_banner);
        /**
         * init all event for every view
         */
        if (controller != null) {
            controller.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    Intent intent = new Intent(TVHelperMainActivity.this, TVRemoteControlActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (tvplayer != null)
        {
            tvplayer.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    Intent intent = new Intent(TVHelperMainActivity.this, TVChannelShowActivity.class);
                    startActivity(intent);
                }
            });
        }
        if (setting != null)
        {
            setting.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    Intent intent = new Intent(TVHelperMainActivity.this, SettingActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (shoucang != null)
        {
            shoucang.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    Intent intent = new Intent(TVHelperMainActivity.this, TVChannelShouCangShowActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (sousuo != null)
        {
            sousuo.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    Intent intent = new Intent(TVHelperMainActivity.this, TVChannelSearchActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (musicTouYing != null)
        {
            musicTouYing.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    Intent intent = new Intent(TVHelperMainActivity.this, MusicCategoryActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (vedioTouYing != null)
        {
            vedioTouYing.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    Intent intent = new Intent(TVHelperMainActivity.this, VedioCategoryActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (pictureTouYing != null)
        {
            pictureTouYing.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    Intent intent = new Intent(TVHelperMainActivity.this, PictureCategoryActivity.class);
                    startActivity(intent);
                }
            });
        }
        if (otherTouTing != null)
        {
            otherTouTing.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    Intent intent = new Intent(TVHelperMainActivity.this, OtherDetailsActivity.class);
                    intent.putExtra("forwardIntent", new Intent(TVHelperMainActivity.this, TVRemoteControlActivity.class));
                    startActivity(intent);
                }
            });
        }



        /**关闭电源键**/
       /* if(power != null)
        {
            power.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    Dialog  dialog =DialogUtil.showAlertDialog(TVHelperMainActivity.this,
                            getString(R.string.powerkey),"",new DialogBtnOnClickListener() {

                                @Override
                                public void onSubmit(DialogMessage dialogMessage) {
                                    ClientSendCommandService.msg = "key:power";
                                    ClientSendCommandService.handler.sendEmptyMessage(1);
                                    if (dialogMessage.dialog!=null && dialogMessage.dialog.isShowing()) {
                                        dialogMessage.dialog.cancel();
                                    }
                                }

                                @Override
                                public void onCancel(DialogMessage dialogMessage) {
                                    if (dialogMessage.dialog!=null && dialogMessage.dialog.isShowing()) {
                                        dialogMessage.dialog.cancel();
                                    }
                                }
                            });
                }
            });
        }*/


        /**
         * Ip部分
         */
        ipSelecter = new BoxSelecter(this, (TextView) findViewById(R.id.title), (ListView) findViewById(R.id.clients),  findViewById(R.id.banner),(LinearLayout)findViewById(R.id.banner),(ImageView)findViewById(R.id.ip_list_item_bg),new Handler(getMainLooper()));
        mhandler = new Handler() {
            @Override
            public void handleMessage(Message msg1) {
                switch (msg1.what) {
                    case 1:
                        break;
                    case 2:
                        finish();
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg1);
            }
        };
        
        
    }

    private void initMedia() {
        /**
         * 启动Http服务
         */
        Intent http = new Intent(TVHelperMainActivity.this, NanoHTTPDService.class);
        startService(http);

        /**
         * 通知系统媒体去更新媒体库
         */
        String[] types = {"video/x-msvideo","video/3gpp", "video/x-msvideo", "video/mp4", "video/mpeg", "video/quicktime",
                "audio/x-wav", "audio/x-pn-realaudio", "audio/x-ms-wma", "audio/x-ms-wmv", "audio/x-mpeg", "image/jpeg", "image/png"};
        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{Environment.getExternalStorageDirectory().getAbsolutePath()}, types, null);

        Intent intent = new Intent(getApplicationContext(), M3UListProviderService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    /********************************************系统方法重载部分*******************************************************/

    @Override
    protected void onResume() {
        super.onResume();
//        if (ClientSendCommandService.titletxt != null) {
//            title.setText(ClientSendCommandService.titletxt);
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (ipSelecter != null) {
			ipSelecter.release();
		}
        if (updateReceiver != null) {
            unregisterReceiver(updateReceiver);
            updateReceiver = null;
        } 
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Log.i(TAG, "KEYCODE_BACK");
                if (isReadyExit) {
                	IpSelectorDataServer.getInstance().clear();
                	System.exit(0);
				}
                else {
                	isReadyExit = true;
                	Toast.makeText(TVHelperMainActivity.this, R.string.press_again_to_exit,3000).show();
                	if (exitRunnable == null) {
                		exitRunnable = new Runnable() {
    						
    						@Override
    						public void run() {
    							if (isReadyExit) {
    								isReadyExit = false;
    							}
    							
    						}
    					};
					}
                	
					TVHelperMainActivity.mhandler.removeCallbacks(exitRunnable);
					TVHelperMainActivity.mhandler.postDelayed(exitRunnable, 3000);
					
				}                              
                return true;
            case KeyEvent.KEYCODE_MENU:
    			return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /************************************************升级部分***********************************************************/

    /**
     * 更新信息
     */
    private String updateMsgContent;

    /**
     * 升级信息
     */
    private String updateVersion;

    /**
     * 系统更新的服�?
     */
    public void initUpdateThread() {
        /**
         * 注册广播
         */
        IntentFilter homefilter = new IntentFilter();
        homefilter.addAction("MAIN_UPDATE_DOWNLOAD");
        homefilter.addAction("MAIN_UPDATE_INSTALL");
        registerReceiver(this.updateReceiver, homefilter);

        /**
         * 更新的时间检�? 如果当前更新过了就不用在更新�?
         */
        UpdateLogService preferenceService = new UpdateLogService(this);
        String updateDate = preferenceService.getUpdateDate();
        if (!updateDate.equals("") && updateDate.compareTo(DateUtils.to10String(new Date())) >= 0) {
            return;
        } else {
            preferenceService.saveUpdateDate();
        }

        if (UserUpdateService.updateFile.exists()) {
            /**
             * 本地APK已存在流�?
             */
            fileExistFlow();
        } else {
            /**
             * 本地文件不存在，从服务器获得更新流程
             */
            fileNotExistFlow();
        }
    }

    @SuppressLint("NewApi")
    private void fileExistFlow() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //先比较本地下载APK和服务端最新的版本
                    PackageManager pm = getPackageManager();
                    PackageInfo instalPMInfo = pm.getPackageArchiveInfo(UserUpdateService.updateFile.getAbsolutePath().toString(), PackageManager.GET_ACTIVITIES);
                    final String updateMsg = getUpdateInfo();
                    if (updateMsg != null) {
                        JsonReader reader = new JsonReader(new StringReader(updateMsg));
                        try {
                            reader.beginObject();
                            while (reader.hasNext()) {
                                String name = reader.nextName();
                                if (name.equals("version")) {
                                    updateVersion = reader.nextString();
                                } else if (name.equals("updatecontent")) {
                                    updateMsgContent = reader.nextString();
                                } else {
                                    reader.skipValue();
                                }
                            }
                            reader.endObject();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //先比较本地程序和服务器的版本
                        if (instalPMInfo != null) {
                            int installVersionCode = instalPMInfo.versionCode;
                            if (updateVersion != null && !updateVersion.equals("") && !updateVersion.equals("null")) {
                                if (Integer.parseInt(updateVersion) > installVersionCode) {
                                    //有更�?弹框提示下载更新
                                    UserUpdateService.updateFile.delete();
                                    fileNotExistFlow();
                                    return;
                                }
                            }
                        } else {
                            //文件包存在，但是又得不到信息，证明下载的文件又问题，重新下载
                            UserUpdateService.updateFile.delete();
                            fileNotExistFlow();
                            return;
                        }
                    }

                    //在比较本地程序和安装APK的版�?
                    PackageInfo localPMInfo = pm.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
                    if (localPMInfo != null) {
                        int localVersionCode = localPMInfo.versionCode;
                        Log.e(TAG, "安装的versionCode  " + localVersionCode);

                        //获取本地apk的versionCode
                        if (instalPMInfo != null) {
                            int installVersionCode = instalPMInfo.versionCode;
                            Log.e(TAG, "未安装的versionCode  " + installVersionCode);

                            if (installVersionCode <= localVersionCode) {
                                UserUpdateService.updateFile.delete();
                            } else {
                                //弹框提示安装
                                Intent intent = new Intent("MAIN_UPDATE_INSTALL");
                                TVHelperMainActivity.this.sendBroadcast(intent);
                            }
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @SuppressLint("NewApi")
    private void fileNotExistFlow() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //服务器获得最新的版本信息
                String updateMsg = getUpdateInfo();
                if (updateMsg != null) {
                    JsonReader reader = new JsonReader(new StringReader(updateMsg));
                    try {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String name = reader.nextName();
                            if (name.equals("version")) {
                                updateVersion = reader.nextString();
                            } else if (name.equals("updatecontent")) {
                                updateMsgContent = reader.nextString();
                            } else {
                                reader.skipValue();
                            }
                        }
                        reader.endObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //比较本地的版本和服务器端的版�?
                    try {
                        PackageManager pm = getPackageManager();
                        PackageInfo localPMInfo = pm.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
                        if (localPMInfo != null) {
                            int versionCode = localPMInfo.versionCode;
                            if (updateVersion != null && !updateVersion.equals("") && !updateVersion.equals("null")) {
                                if (Integer.parseInt(updateVersion) <= versionCode) {
                                    //本地版本大于等于服务器版本，无更�?
                                } else {
                                    //本地版本小于等于服务器版�?有更�?弹框提示下载更新
                                    Intent intent = new Intent("MAIN_UPDATE_DOWNLOAD");
                                    TVHelperMainActivity.this.sendBroadcast(intent);
                                }
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    //没得到升级信息，不做处理
                }
            }
        }).start();
    }

    /**
     * *****************************************服务器获得更新信息部�?*************************************************
     */

    private String getUpdateInfo() {
        String retSrc = null;

        /**
         * 没有连接网络，提示用户，流程结束
         */
        if (!NetworkUtils.isWifiConnected(getApplicationContext())) {
            return null;
        }

        /**
         * 下载最新的版本信息
         */
        try {
            URI url = URI.create(UserUpdateService.JSON_URL);
            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 8000);
            HttpClient httpclient = new DefaultHttpClient(params);
            HttpGet httpRequest = new HttpGet(url);
            HttpResponse httpResponse = httpclient.execute(httpRequest);

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                retSrc = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                Log.i(TAG, "getJsonData get Json  OK !");
            } else {
                Log.e(TAG, "getJsonData  get Json  ERROR !");
                return null;
            }

            retSrc = removeBOM(retSrc);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return retSrc;
    }

    public static final String removeBOM(String data) {
        if (TextUtils.isEmpty(data)) {
            return data;
        }
        if (data.startsWith("\ufeff")) {
            return data.substring(1);
        } else {
            return data;
        }
    }

    /**
     * *******************************************下载和安装部�?*****************************************************
     */

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {

        public void onReceive(Context mContext, Intent mIntent) {
            /**
             * 升级文件下载
             */
            if (mIntent.getAction().equals("MAIN_UPDATE_DOWNLOAD")) {

                //如果用户不是连接的WIFI网络，直接返回不处理
                if (!NetworkUtils.isWifiConnected(TVHelperMainActivity.this)) {
                    return;
                }

                //直接开始下载程序不经过用户确认
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        HttpURLConnection connection = null;
                        try {
                            UserUpdateService.downloading = true;

                            /**
                             * 设置网络连接
                             */
                            URL url = new URL(UserUpdateService.UPDATE_URL);
                            connection = (HttpURLConnection) url.openConnection();
                            connection.setUseCaches(false);
                            connection.setRequestMethod("GET");
                            connection.setConnectTimeout(10000);

                            /**
                             * 开始下载文�?
                             */
                            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                connection.connect();
                                InputStream instream = connection.getInputStream();
                                RandomAccessFile rasf = new RandomAccessFile(UserUpdateService.updateFile, "rwd");
                                byte[] b = new byte[1024 * 24];
                                int length = -1;
                                while ((length = instream.read(b)) != -1) {
                                    rasf.write(b, 0, length);
                                    Log.d("update file size", ">>>>>" + rasf.length());
                                }
                                rasf.close();
                                instream.close();

                                //下载完成安装
                                Intent install = new Intent("MAIN_UPDATE_INSTALL");
                                TVHelperMainActivity.this.sendBroadcast(install);
                            }

                            /**
                             * 下载完成处理
                             */
                            UserUpdateService.downloading = false;
                        } catch (Exception e) {
                            //异常处理
                            e.printStackTrace();
                            if (UserUpdateService.updateFile.exists()) {
                                UserUpdateService.updateFile.delete();
                            }
                            UserUpdateService.downloading = false;
                        } finally {
                            connection.disconnect();
                        }
                    }
                }).start();

            } else if (mIntent.getAction().equals("MAIN_UPDATE_INSTALL")) {
                //安装最新的apk文件
                if (!UserUpdateService.updateFile.exists()) {
                    return;
                }

                try {
                    Runtime.getRuntime().exec("chmod 0777  " + UserUpdateService.updateFile.getAbsolutePath().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /**
                 * 如果用户点击的是直接下载，下载后直接更新，如果下载文件已经存在，就询问用户时候安�?
                 */

                Dialog  dialog =DialogUtil.showEditDialog(TVHelperMainActivity.this,
                 		getApplicationContext().getString(R.string.update_prepared),getApplicationContext().getString(R.string.setup_prompt),new DialogBtnOnClickListener() {
 					
 					@Override
 					public void onSubmit(DialogMessage dialogMessage) {
                        //休息1秒安装
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        //安装新的APK
                        Uri uri = Uri.fromFile(UserUpdateService.updateFile);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
                        startActivity(intent);
                        if (dialogMessage.dialog!=null && dialogMessage.dialog.isShowing()) {
							dialogMessage.dialog.cancel();
						}
 					}
 					
 					@Override
 					public void onCancel(DialogMessage dialogMessage) {
 						if (dialogMessage.dialog!=null && dialogMessage.dialog.isShowing()) {
							dialogMessage.dialog.cancel();
						}
 					}
 				});
            }
        }
    };

    /*******************************************第一次进入显示帮助对话框**************************************************/

    /**
     * 如果用户是第一次进入该程序，则显示该帮助对话框，如果已经登录，不做任何操作
     */
    private void initHelpDialog() {
        AppLogService service = new AppLogService(this);
        if (!service.isUserAlreadyEntrance()) {
//            AppHelpDialog dialog = new AppHelpDialog(this);
//            dialog.show();
            Intent intent = new Intent();
            intent.setClass(this, TVGuide.class);
            startActivity(intent);
        }
    }

    private void startMirrcast(){
        try {
            startActivity(new Intent("android.settings.WIFI_DISPLAY_SETTINGS"));
        } catch (Exception e) {
            Toast.makeText(this,"Please open miracast in 'Settings->Wifi Display'",Toast.LENGTH_LONG).show();
        }
    }
}
