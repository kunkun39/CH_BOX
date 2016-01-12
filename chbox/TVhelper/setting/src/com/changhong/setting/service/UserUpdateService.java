package com.changhong.setting.service;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.utils.DialogUtil;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.utils.DialogUtil.DialogBtnOnClickListener;
import com.changhong.common.utils.DialogUtil.DialogMessage;
import com.changhong.setting.R;

/**
 * Created by Jack Wang
 */
public class UserUpdateService {

    private static final String LOG_TAG = "UserUpdateService";

    /**
     * 系统上下文
     */
    private Context context;

    /**
     * ACTIVITY传过来的消息处理
     */
    private Handler handler;

    /**
     * handler需要处理的MESSAGE的编号
     */
    private int MESSAGE_CODE_100 = 100;
    private int MESSAGE_CODE_200 = 200;
    private int MESSAGE_CODE_300 = 300;
    private int MESSAGE_CODE_400 = 400;
    private int MESSAGE_CODE_500 = 500;
    private int MESSAGE_CODE_600 = 600;

    /**
     * ACITIVY传过来更新进度条
     */
    private ProgressDialog m_pDialog;

    /**
     * 更新文件
     */
//   public static File updateFile  = new File("/sdcard/tvhelperapk/tvhelper.apk");
   public static File updateFile  = new File("/data/data/com.changhong.tvhelper/tvhelper.apk");

    /**
     * JSON和APK服务器URL
     */
//   public final static String JSON_URL = "http://192.168.0.107:8080/update/download/update.json";
//   public final static String UPDATE_URL = "http://192.168.0.107:8080/update/download/tvhelper.apk";
    public final static String JSON_URL = "http://52.74.48.175/update_tv/update.json";
    public final static String UPDATE_URL = "http://52.74.48.175/update_tv/tvhelper.apk";

    /**
     * 更新信息和版本
     */
    private String updateMsgContent;
    private String updateVersion;

    /**
     * 下载后直接安装
     */
    private boolean directlyInstall = false;

    /**
     * 网络连接是否有问题
     */
    private boolean networkProblem = false;

    /**
     * 当前时候在下载
     */
    public static boolean downloading = false;

    /**
     * 线程下载是否异常
     */
    public static boolean THREAD_DOWNLOAD_EXCEPTION = false;

    /**
     * 记录线程1是否完成下载
     */
    public static boolean THREAD_ONE_FINISHED = false;

    /**
     * 记录线程2是否完成下载
     */
    public static boolean THREAD_TWO_FINISHED = false;
    
    /**
     * 构造方法
     * @param context context information which need activity pass in
     * @param handler handler which need declare in activity and pass in
     *                please check message code which definition above
     *                {@link com.changhong.setting.activity.SettingActivity}
     * @param m_pDialog progress dialog which need declare in activity and pass in
     */
    public UserUpdateService(Context context, Handler handler, ProgressDialog m_pDialog) {
        this.context = context;
        this.handler = handler;
        this.m_pDialog = m_pDialog;
    }

    /***********************************************系统方法部分*******************************************************/

    public void initUpdateThread() {
        if (UserUpdateService.downloading) {
            Toast.makeText(context, context.getResources().getString(R.string.loading_waiting), Toast.LENGTH_SHORT).show();
            return;
        }

        /**
         * 检查上次下载是否有异常
         */
        UpdateLogService preferenceService = new UpdateLogService(context);
        boolean downingException = preferenceService.isDownloadingException();

        /**
         * 根据下载的文件和上次的异常来判断走升级那个流程
         */
        if (updateFile.exists() && !downingException) {
            /**
             * 本地APK已存在流程
             */
            fileExistFlow();
        } else {
            /**
             * 本地文件不存在，从服务器获得更新流程
             */
            fileNotExistFlow();
        }
    }

    private void fileExistFlow() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //先比较本地下载APK和服务端最新的版本
                    PackageManager pm = context.getPackageManager();
                    PackageInfo instalPMInfo = pm.getPackageArchiveInfo(updateFile.getAbsolutePath().toString(), PackageManager.GET_ACTIVITIES);
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
                                    //有更新 弹框提示下载更新
                                    updateFile.delete();
                                    fileNotExistFlow();
                                    return;
                                }
                            }
                        } else {
                            //文件包存在，但是又得不到信息，证明下载的文件又问题，重新下载
                            updateFile.delete();
                            fileNotExistFlow();
                            return;
                        }
                    }

                    //在比较本地程序和安装APK的版本
                    PackageInfo localPMInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
                    if (localPMInfo != null) {
                        int localVersionCode = localPMInfo.versionCode;
                        Log.e(LOG_TAG, "安装的versionCode  " + localVersionCode);

                        //获取本地apk的versionCode
                        if (instalPMInfo != null) {
                            int installVersionCode = instalPMInfo.versionCode;
                            Log.e(LOG_TAG, "未安装的versionCode  " + installVersionCode);

                            if (installVersionCode <= localVersionCode) {
                                updateFile.delete();
                                //告诉用户是最新的
                                Message message = new Message();
                                message.arg1 = MESSAGE_CODE_100;
                                handler.sendMessage(message);
                            } else {
                                //弹框提示安装
                                Intent intent = new Intent("SETTING_UPDATE_INSTALL");
                                context.sendBroadcast(intent);
                            }
                        } else {
                            //如果网络出现异常，先判断本地有没有升级的APK，如果没有才提示用户网络有问题
                            //不应该走到这步
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

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

                    //比较本地的版本和服务器端的版本
                    try {
                        PackageManager pm = context.getPackageManager();
                        PackageInfo localPMInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
                        if (localPMInfo != null) {
                            int versionCode = localPMInfo.versionCode;
                            if (updateVersion != null && !updateVersion.equals("") && !updateVersion.equals("null")) {
                                if (Integer.parseInt(updateVersion) <= versionCode) {
                                    //本地版本大于等于服务器版本，无更新
                                    Message message = new Message();
                                    message.arg1 = MESSAGE_CODE_100;
                                    handler.sendMessage(message);
                                } else {
                                    //本地版本小于等于服务器版本,有更新 弹框提示下载更新
                                    Intent intent = new Intent("SETTING_UPDATE_DOWNLOAD");
                                    context.sendBroadcast(intent);
                                }
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (networkProblem) {
                        Message message = new Message();
                        message.arg1 = MESSAGE_CODE_600;
                        handler.sendMessage(message);
                    }
                }
            }
        }).start();
    }

    /********************************************服务器获得更新信息部分***************************************************/

    private String getUpdateInfo() {
        networkProblem = false;
        String retSrc = null;

        /**
         * 没有连接网络，提示用户，流程结束
         */
        if (!NetworkUtils.isConnectInternet(context.getApplicationContext())) {
            Message message = new Message();
            message.arg1 = MESSAGE_CODE_200;
            handler.sendMessage(message);
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
                Log.i(LOG_TAG, "getJsonData get Json  OK !");
            } else {
                Log.e(LOG_TAG, "getJsonData  get Json  ERROR !");
                return null;
            }

            retSrc = removeBOM(retSrc);
        } catch (Exception e) {
            e.printStackTrace();
            networkProblem = true;
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

    /**********************************************下载和安装部分*******************************************************/

    public BroadcastReceiver updateReceiver = new BroadcastReceiver() {

        Dialog mDialog = null;
        public void onReceive(Context mContext, Intent mIntent) {
            /**
             * 升级文件下载
             */
            if (mIntent.getAction().equals("SETTING_UPDATE_DOWNLOAD")) {
            	String content=updateMsgContent;
            	 //如果用户是连接的移动网络，需要提示用户
                if (NetworkUtils.isWifiConnected(context)) {
                	content=updateMsgContent;
                } else {
                	content=mContext.getResources().getString(R.string.mobile_data_tag) + updateMsgContent;
                }
                if (mDialog != null)
                    mDialog.dismiss();

                mDialog =DialogUtil.showAlertDialog(context,
                 		context.getResources().getString(R.string.stb_version_update),content,new DialogBtnOnClickListener() {
 					
 					@Override
 					public void onSubmit(DialogMessage dialogMessage) {
                        m_pDialog.show();
                        directlyInstall = true;

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (!NetworkUtils.isConnectInternet(context)) {
                                    return;
                                }
                                try {
                                    UserUpdateService.downloading = true;

                                    //先获得文件的大小
                                    int fileTotalSize = 0;
                                    HttpURLConnection connection = null;
                                    try {
                                        URL url = new URL(UserUpdateService.UPDATE_URL);
                                        connection = (HttpURLConnection) url.openConnection();
                                        connection.setUseCaches(false);
                                        connection.setConnectTimeout(20000);
                                        connection.setRequestMethod("GET");
                                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                            connection.connect();
                                            fileTotalSize = connection.getContentLength();
                                            m_pDialog.setMax(fileTotalSize);
                                        }
                                    } catch (Exception e) {
                                        UserUpdateService.downloading=false;
                                        e.printStackTrace();
                                    } finally {
                                        try {
                                            if (connection != null) {
                                                connection.disconnect();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    //获得文件的大小为0，直接返回, 并通知用户
                                    if (fileTotalSize <= 0) {
                                        Message message = new Message();
                                        message.arg1 = MESSAGE_CODE_500;
                                        handler.sendMessage(message);
                                        UserUpdateService.downloading=false;
                                        return;
                                    }

                                    //计算两个线程分别要下载的文件大小
                                    long firstThreadStart = 0;
                                    long firstThreadEnd = fileTotalSize / 2;
                                    long secondThreadStart = fileTotalSize / 2 + 1;
                                    long secondThreadEnd = fileTotalSize;

                                    //第一个线程下载
                                    UserUpdateService.THREAD_ONE_FINISHED = false;
                                    int DOWNLOAD_THREAD_ONE = 1;
                                    UpdateFileDownloadThread firstThread = new UpdateFileDownloadThread(context, DOWNLOAD_THREAD_ONE, firstThreadStart, firstThreadEnd);
                                    firstThread.start();

                                    //第二个线程下载
                                    UserUpdateService.THREAD_TWO_FINISHED = false;
                                    int DOWNLOAD_THREAD_TWO = 2;
                                    UpdateFileDownloadThread secondThread = new UpdateFileDownloadThread(context, DOWNLOAD_THREAD_TWO, secondThreadStart, secondThreadEnd);
                                    secondThread.start();

                                    //不停的更新下载的状态
                                    UpdateLogService preferenceService = new UpdateLogService(context);
                                    while (!UserUpdateService.THREAD_ONE_FINISHED || !UserUpdateService.THREAD_TWO_FINISHED) {
                                        Thread.sleep(100);

                                        /**
                                         * 检查是否有异常，如果有异常，向外抛出异常，用handler通知用户
                                         */
                                        if (UserUpdateService.THREAD_DOWNLOAD_EXCEPTION) {
                                            UserUpdateService.THREAD_DOWNLOAD_EXCEPTION = false;
                                            throw new RuntimeException("thread download fail");
                                        }

                                        /**
                                         * 计算现在更新的进度
                                         */
                                        int alreadyRead = (int)preferenceService.getTotalDownlaodDataSize();
                                        m_pDialog.setProgress(alreadyRead);
                                    }

                                    //下载完成，重置下载的进度
                                    preferenceService.saveThreadDownloadDataSize(DOWNLOAD_THREAD_ONE, 0);
                                    preferenceService.saveThreadDownloadDataSize(DOWNLOAD_THREAD_TWO, 0);

                                    //下载完成 安装
                                    Intent install = new Intent("SETTING_UPDATE_INSTALL");
                                    context.sendBroadcast(install);

                                    //现在完成，设置DONWLOADING标志
                                    UserUpdateService.downloading = false;
                                    Message message = new Message();
                                    message.arg1 = MESSAGE_CODE_400;
                                    handler.sendMessage(message);
                                    m_pDialog.setProgress(0);

                                } catch (Exception e) {
                                    //异常捕获
                                    e.printStackTrace();
                                    UserUpdateService.downloading = false;
                                    directlyInstall = false;
                                    Message message = new Message();
                                    message.arg1 = MESSAGE_CODE_500;
                                    handler.sendMessage(message);
                                }
                            }
                        }).start();
                        if (dialogMessage.dialog != null && dialogMessage.dialog.isShowing()) {
                        	dialogMessage.dialog.cancel();
        				}
 					}
 					
 					@Override
 					public void onCancel(DialogMessage dialogMessage) {
 						if (dialogMessage.dialog != null && dialogMessage.dialog.isShowing()) {
                        	dialogMessage.dialog.cancel();
        				}
 					}
 				});
            	
            } else if (mIntent.getAction().equals("SETTING_UPDATE_INSTALL")) {
                //安装最新的apk文件
                if (!updateFile.exists()) {
                    return;
                }

                try {
                    Runtime.getRuntime().exec("chmod 0777  " + updateFile.getAbsolutePath().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /**
                 * 如果用户点击的是直接下载，下载后直接更新，如果下载文件已经存在，就询问用户时候安装
                 */
                if(directlyInstall) {
                    //安装新的APK， 下载后用户直接安装
                    Uri uri = Uri.fromFile(updateFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    context.startActivity(intent);
                } else {
                    if (mDialog != null)
                        mDialog.dismiss();
                    mDialog =DialogUtil.showAlertDialog(context,
                     		context.getResources().getString(R.string.update_prepared),context.getResources().getString(R.string.setup_prompt),new DialogBtnOnClickListener() {
     					
     					@Override
     					public void onSubmit(DialogMessage dialogMessage) {
     						//休息1秒安装
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            //安装新的APK
                            Uri uri = Uri.fromFile(updateFile);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setDataAndType(uri, "application/vnd.android.package-archive");
                            context.startActivity(intent);
                            if (dialogMessage.dialog != null && dialogMessage.dialog.isShowing()) {
                            	dialogMessage.dialog.cancel();
            				}
     					}
     					
     					@Override
     					public void onCancel(DialogMessage dialogMessage) {
     						if (dialogMessage.dialog != null && dialogMessage.dialog.isShowing()) {
                            	dialogMessage.dialog.cancel();
            				}
     					}
     				});
                }

                /**
                 * 还原直接安装
                 */
                directlyInstall = false;
            }
        }
    };
}
