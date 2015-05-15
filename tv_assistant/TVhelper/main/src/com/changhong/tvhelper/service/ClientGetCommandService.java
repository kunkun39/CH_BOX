package com.changhong.tvhelper.service;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.*;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import com.changhong.common.domain.NetworkStatus;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.service.ClientSocketInterface;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.MobilePerformanceUtils;
import com.changhong.common.utils.StringUtils;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import com.changhong.common.system.AppConfig;
import com.changhong.setting.utils.NetEstimateUtils;
import com.changhong.touying.activity.*;
import com.changhong.touying.dialog.MusicPlayer;
import com.changhong.touying.nanohttpd.NanoHTTPDService;
import com.changhong.tvhelper.*;
import com.changhong.tvhelper.activity.TVChannelSearchActivity;
import com.changhong.tvhelper.activity.TVChannelShowActivity;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;

public class ClientGetCommandService extends Service implements ClientSocketInterface {

    protected static final String TAG = "CHTVhelper";

    private ActivityManager manager;
    /**
     * message handler
     */
    public static Handler mHandler = null;
    /**
     * change and its logo
     */
    public static HashMap<String, Integer> channelLogoMapping = new HashMap<String, Integer>();

    /**
     * the parameter which used for check the application will exist or not
     */
    private boolean exit = false;

    /**
     * client and heart time internal check
     */
    private long time = 0l;

    @Override
    public void onCreate() {
        super.onCreate();

        initTVchannel();

        initView();

        initThreads();
    }

    private void initView() {
        manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        //set every activity title when client connect to server
                        ClientTitleSettingService.setClientActivityTitle();
                        break;
                    case 1:
                        //use change server refresh the all channel, please also check ClientSendCommandService
                        if (TVChannelShowActivity.mHandler != null) {
                            Log.e(TAG, "刷新...");
                            TVChannelShowActivity.mHandler.sendEmptyMessage(0);
                        }
                        if (TVChannelSearchActivity.mHandler != null) {
                            Log.e(TAG, "刷新...");
                            TVChannelSearchActivity.mHandler.sendEmptyMessage(0);
                        }
                        break;
                    case 2:
                        //异频点换台以后操作
                        if (TVChannelShowActivity.selectedChanncelTabIndex == 5 && TVChannelShowActivity.mHandler != null) {
                            Log.e(TAG, "刷新同时看");
                            TVChannelShowActivity.mHandler.sendEmptyMessage(0);
                        }
                        break;
                    case 3:
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    private void initThreads() {

        new GetServerIP().start();

        new BoxMinitorThread().start();

        new GetChannelName().start();
    }

    /*************************************************手机端不停的获得盒子广播线程*****************************************/

    private class GetServerIP extends Thread {

        public void run() {
            ClientSendCommandService.serverIpList.clear();
            DatagramSocket dgSocket = null;

            try {
                dgSocket = new DatagramSocket(SERVER_IP_POST_PORT);
                DatagramPacket dgPacket = null;

                while (true) {
                    try {
                        /**
                         * 接收Socket
                         */
                        byte[] by = new byte[512];
                        dgPacket = new DatagramPacket(by, by.length);
                        dgSocket.receive(dgPacket);

                        /**
                         * 处理Socket
                         */
                        String serverAddress = dgPacket.getAddress().getHostAddress();
                        if (StringUtils.hasLength(serverAddress)) {
                            Log.w(TAG, serverAddress);

                            if (!ClientSendCommandService.serverIpList.contains(serverAddress)) {
                                ClientSendCommandService.serverIpList.add(serverAddress);
                                /**
                                 * 如果用户已经选择了IP，就不用选择了，如果为空，就按照系统自动分配
                                 */
                                if (!StringUtils.hasLength(ClientSendCommandService.serverIP)) {
                                    ClientSendCommandService.serverIP = ClientSendCommandService.serverIpList.get(0);

                                    /**
                                     * 更新频道和应用列表
                                     */
                                    ClientSendCommandService.handler.sendEmptyMessage(2);
                                }
                                ClientSendCommandService.titletxt = "CHBOX";
                                time = System.currentTimeMillis();
                                ClientSendCommandService.handler.sendEmptyMessage(2);

                                /**
                                 * 更细所有的频道TITLE
                                 */
                                mHandler.sendEmptyMessage(0);
//                                Log.e("COMMAND_CLEAN_1", serverAddress + "-" + ClientSendCommandService.serverIP);

                            } else if (ClientSendCommandService.serverIP != null && serverAddress.equals(ClientSendCommandService.serverIP)) {
//                                Log.e("COMMAND_CLEAN_2", serverAddress + "-" + ClientSendCommandService.serverIP);
                                /**
                                 * 更新当前server的活动时间
                                 */
                                time = System.currentTimeMillis();

                                /**
                                 * 获得服务器的状态
                                 */
                                String content = new String(by, 0, dgPacket.getLength());
                                String[] tokens = StringUtils.delimitedListToStringArray(content, "|");

                                /**
                                 * 设置服务端网络状态
                                 */
                                try {
                                    NetEstimateUtils.serverNetworkStatus = NetworkStatus.valueOf(tokens[1]);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                /**
                                 * 音乐和视频播放
                                 */
                                if (tokens.length == 6) {
                                    ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
                                    String shortClassName = info.topActivity.getClassName();

                                    //视频播放
                                    if (tokens[2].equals("vedio_play")) {
                                        if ("com.changhong.touying.activity.VedioDetailsActivity".equals(shortClassName)) {
                                            try {
                                                if (VedioDetailsActivity.handler != null) {
                                                    Message message = new Message();
                                                    message.what = 0;
                                                    message.obj = tokens[3] + "|" + tokens[4] + "|" + tokens[5];
                                                    VedioDetailsActivity.handler.sendMessage(message);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    //音乐播放
                                    if (tokens[2].equals("music_play")) {
                                    /*if ("com.changhong.touying.dialog".equals(shortClassName)) */{
                                            try {
                                            if (MusicPlayer.handler != null) {
                                                Message message = new Message();
                                                message.what = 0;
                                                message.obj = tokens[3] + "|" + tokens[4] + "|" + tokens[5];
                                                MusicPlayer.handler.sendMessage(message);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    }
                                }

                                /**
                                 * 音乐和是视频播放结束
                                 */
                                if (tokens.length == 4 && tokens[2].equals("play_stop")) {
                                    //视频和音乐播放停止, 视频的停止信号为1，因为的停止信号为2
                                    int stopTag = Integer.valueOf(tokens[3]);

                                    if (stopTag == 1) {
                                        if (VedioDetailsActivity.handler != null) {
                                            VedioDetailsActivity.handler.sendEmptyMessage(1);
                                        }
                                    } else if (stopTag == 2) {
                                        if (MusicPlayer.handler != null) {
                                            MusicPlayer.handler.sendEmptyMessage(1);
                                        }
                                    }
                                }

                                /**
                                 * 没有播放音频和视频的情况, 关闭httpserver
                                 */
                                if(tokens.length == 2) {
                                    //HTTPD的使用状态
                                    MobilePerformanceUtils.httpServerUsing = false;
                                }
                            } else {
//                                Log.e("COMMAND_CLEAN_3", serverAddress + "-" + ClientSendCommandService.serverIP);
                            }
                        }
                        if (exit) {
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        dgPacket = null;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (dgSocket != null) {
                        dgSocket.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /***********************************************手机端不停的监控盒子是否有广播发出**************************************/

    private class BoxMinitorThread extends Thread {

        public void run() {
            while (true) {
                long during = System.currentTimeMillis() - time;
                if (during > 4000 && time != 0l) {
                    Log.e("COMMAND_CLEAN", String.valueOf(during));
                    clearIpList();
                }
                SystemClock.sleep(1000);
            }
        }
    }

    private void clearIpList() {
        ClientSendCommandService.serverIpList.clear();
        ClientSendCommandService.serverIP = null;
        ClientSendCommandService.titletxt = "未连接";
        mHandler.sendEmptyMessage(0);
        time = 0l;
    }

    /***********************************************手机端不停的监控盒子是否有广播发出**************************************/

    private class GetChannelName extends Thread {

        public void run() {
            DatagramSocket dgSocket = null;
            try {
                dgSocket = new DatagramSocket(9003);
                byte[] by = new byte[1024];
                DatagramPacket dgPacket = new DatagramPacket(by, by.length);
                while (true) {
                    try {
                        dgSocket.receive(dgPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mHandler.sendEmptyMessage(3);
                    Log.e(TAG, "UDPIP >>> " + dgPacket.getAddress().getHostAddress());
                    if (ClientSendCommandService.serverIP != null && ClientSendCommandService.serverIP.equals(dgPacket.getAddress().getHostAddress())) {
                        String ChannelInfo = new String(by, 0, dgPacket.getLength());
                        Log.e(TAG, "ChannelInfo >>> " + ChannelInfo);
                        String ChannelName = ChannelInfo.substring(0, ChannelInfo.indexOf("|"));
                        String ChannelFreq = ChannelInfo.substring(ChannelInfo.indexOf("|") + 1, ChannelInfo.length());
                        if (ChannelName != null && !ChannelName.equals("")) {
                            //发送广播通知手机直播换台
                            Intent intent = new Intent("com.action.switchchannel");
                            intent.putExtra("channelname", ChannelName);
                            intent.putExtra("channelfreq", ChannelFreq);
                            ClientGetCommandService.this.sendBroadcast(intent);
                            //更新同时看的同频点频道列表
                            ClientSendCommandService.playingChannelData.clear();
                            Log.e(TAG, "ChannelName >>> " + ChannelName);
                            Log.e(TAG, "ChannelFreq >>> " + ChannelFreq);
                            String freq = ChannelFreq;
                            if (!ClientSendCommandService.channelData.isEmpty() && freq != null && !freq.equals("")) {
                                for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
                                    if (((String) (ClientSendCommandService.channelData.get(i).get("freq"))).equals(freq)) {
                                        ClientSendCommandService.playingChannelData.add(ClientSendCommandService.channelData.get(i));
                                    }
                                }
                            }
                            //刷新同时看
                            mHandler.sendEmptyMessage(2);
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (dgSocket != null) {
                        dgSocket.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * *****************************************************系统重载部分*******************************************
     */

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exit = true;
    }

    /* tangchao */
    void initTVchannel() {
        channelLogoMapping.clear();

        channelLogoMapping.put(getResources().getString(R.string.cctv1_1), R.drawable.cctv1);
        channelLogoMapping.put(getResources().getString(R.string.cctv1_2), R.drawable.cctv1);
        channelLogoMapping.put(getResources().getString(R.string.cctv1_3), R.drawable.cctv1);
        channelLogoMapping.put(getResources().getString(R.string.cctv1_4), R.drawable.cctv1);
        channelLogoMapping.put(getResources().getString(R.string.cctv1hd_1), R.drawable.cctv1);
        channelLogoMapping.put(getResources().getString(R.string.cctv1hd_2), R.drawable.cctv1);
//		channelLogoMapping.put("CCTV-1����", R.drawable.cctv1hd);
//		channelLogoMapping.put("�ããԣ֣���(����)", R.drawable.cctv1hd);
        channelLogoMapping.put(getResources().getString(R.string.cctv2_1), R.drawable.cctv2);
        channelLogoMapping.put(getResources().getString(R.string.cctv2_2), R.drawable.cctv2);
        channelLogoMapping.put(getResources().getString(R.string.cctv2_3), R.drawable.cctv2);
        channelLogoMapping.put(getResources().getString(R.string.cctv2_4), R.drawable.cctv2);
        channelLogoMapping.put(getResources().getString(R.string.cctv3_1), R.drawable.cctv3);
        channelLogoMapping.put(getResources().getString(R.string.cctv3_2), R.drawable.cctv3);
        channelLogoMapping.put(getResources().getString(R.string.cctv3_3), R.drawable.cctv3);
        channelLogoMapping.put(getResources().getString(R.string.cctv3_4), R.drawable.cctv3);
        channelLogoMapping.put(getResources().getString(R.string.cctv3hd), R.drawable.cctv3);
//		channelLogoMapping.put("CCTV-3����", R.drawable.cctv3hd);
        channelLogoMapping.put(getResources().getString(R.string.cctv4_1), R.drawable.cctv4);
        channelLogoMapping.put(getResources().getString(R.string.cctv4_2), R.drawable.cctv4);
        channelLogoMapping.put(getResources().getString(R.string.cctv4_3), R.drawable.cctv4);
        channelLogoMapping.put(getResources().getString(R.string.cctv4_4), R.drawable.cctv4);
        channelLogoMapping.put(getResources().getString(R.string.cctv5hd), R.drawable.cctv5);
        channelLogoMapping.put(getResources().getString(R.string.cctv5hd_1), R.drawable.cctv5hd);
//		channelLogoMapping.put("CCTV5-�������¸���", R.drawable.cctv5hd1);
        channelLogoMapping.put(getResources().getString(R.string.cctv5_1), R.drawable.cctv5);
        channelLogoMapping.put(getResources().getString(R.string.cctv5_2), R.drawable.cctv5);
        channelLogoMapping.put(getResources().getString(R.string.cctv5_3), R.drawable.cctv5);
        channelLogoMapping.put(getResources().getString(R.string.cctv5_4), R.drawable.cctv5);
        channelLogoMapping.put(getResources().getString(R.string.cctv6_1), R.drawable.cctv6);
        channelLogoMapping.put(getResources().getString(R.string.cctv6_2), R.drawable.cctv6);
        channelLogoMapping.put(getResources().getString(R.string.cctv6_3), R.drawable.cctv6);
        channelLogoMapping.put(getResources().getString(R.string.cctv6_4), R.drawable.cctv6);
        channelLogoMapping.put(getResources().getString(R.string.cctv6hd), R.drawable.cctv6);
//		channelLogoMapping.put("CCTV-6����", R.drawable.cctv6hd);
        channelLogoMapping.put(getResources().getString(R.string.cctv7_1), R.drawable.cctv7);
        channelLogoMapping.put(getResources().getString(R.string.cctv7_2), R.drawable.cctv7);
        channelLogoMapping.put(getResources().getString(R.string.cctv7_3), R.drawable.cctv7);
        channelLogoMapping.put(getResources().getString(R.string.cctv7_4), R.drawable.cctv7);
        channelLogoMapping.put(getResources().getString(R.string.cctv8_1), R.drawable.cctv8);
        channelLogoMapping.put(getResources().getString(R.string.cctv8_2), R.drawable.cctv8);
        channelLogoMapping.put(getResources().getString(R.string.cctv8_3), R.drawable.cctv8);
        channelLogoMapping.put(getResources().getString(R.string.cctv8_4), R.drawable.cctv8);
        channelLogoMapping.put(getResources().getString(R.string.cctv8hd), R.drawable.cctv8);
//		channelLogoMapping.put("CCTV-8����", R.drawable.cctv8hd);
        channelLogoMapping.put(getResources().getString(R.string.cctv9_1), R.drawable.cctv9);
        channelLogoMapping.put(getResources().getString(R.string.cctv9_2), R.drawable.cctv9);
        channelLogoMapping.put(getResources().getString(R.string.cctv9_3), R.drawable.cctv9);
        channelLogoMapping.put(getResources().getString(R.string.cctv10_1), R.drawable.cctv10);
        channelLogoMapping.put(getResources().getString(R.string.cctv10_2), R.drawable.cctv10);
        channelLogoMapping.put(getResources().getString(R.string.cctv10_3), R.drawable.cctv10);
        channelLogoMapping.put(getResources().getString(R.string.cctv10_4), R.drawable.cctv10);
        channelLogoMapping.put(getResources().getString(R.string.cctv11_1), R.drawable.cctv11);
        channelLogoMapping.put(getResources().getString(R.string.cctv11_2), R.drawable.cctv11);
        channelLogoMapping.put(getResources().getString(R.string.cctv11_3), R.drawable.cctv11);
        channelLogoMapping.put(getResources().getString(R.string.cctv11_4), R.drawable.cctv11);
        channelLogoMapping.put(getResources().getString(R.string.cctv12_1), R.drawable.cctv12);
        channelLogoMapping.put(getResources().getString(R.string.cctv12_2), R.drawable.cctv12);
        channelLogoMapping.put(getResources().getString(R.string.cctv12_3), R.drawable.cctv12);
        channelLogoMapping.put(getResources().getString(R.string.cctv12_4), R.drawable.cctv12);
        channelLogoMapping.put(getResources().getString(R.string.cctv13_1), R.drawable.cctvnews);
        channelLogoMapping.put(getResources().getString(R.string.cctv13_2), R.drawable.cctvnews);
        channelLogoMapping.put(getResources().getString(R.string.cctv13_3), R.drawable.cctvnews);
        channelLogoMapping.put(getResources().getString(R.string.cctv13_4), R.drawable.cctvnews);
        channelLogoMapping.put(getResources().getString(R.string.cctv13_5), R.drawable.cctvnews);
        channelLogoMapping.put(getResources().getString(R.string.cctv13_6), R.drawable.cctvnews);
        channelLogoMapping.put(getResources().getString(R.string.cctv13_7), R.drawable.cctvnews);
        channelLogoMapping.put(getResources().getString(R.string.cctv13_8), R.drawable.cctvnews);
        channelLogoMapping.put(getResources().getString(R.string.cctv13_9), R.drawable.cctvnews);
        channelLogoMapping.put(getResources().getString(R.string.cctv14_1), R.drawable.cctv14);
        channelLogoMapping.put(getResources().getString(R.string.cctv14_2), R.drawable.cctv14);
        channelLogoMapping.put(getResources().getString(R.string.cctv14_3), R.drawable.cctv14);
        channelLogoMapping.put(getResources().getString(R.string.cctv14_4), R.drawable.cctv14);
        channelLogoMapping.put(getResources().getString(R.string.cctv14_5), R.drawable.cctv14);
        channelLogoMapping.put(getResources().getString(R.string.cctv14_6), R.drawable.cctv14);
        channelLogoMapping.put(getResources().getString(R.string.cctv14_7), R.drawable.cctv14);
        channelLogoMapping.put(getResources().getString(R.string.cctv15_1), R.drawable.cctv15);
        channelLogoMapping.put(getResources().getString(R.string.cctv15_2), R.drawable.cctv15);
        channelLogoMapping.put(getResources().getString(R.string.cctv15_3), R.drawable.cctv15);
        channelLogoMapping.put(getResources().getString(R.string.cctveyu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.cctvalaboyu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.cctvguide), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.anhuiweishi), R.drawable.logoanhui);
        channelLogoMapping.put(getResources().getString(R.string.beijingweishi), R.drawable.logobeijing);
        channelLogoMapping.put(getResources().getString(R.string.chongqingweishi), R.drawable.logochongqing);
        channelLogoMapping.put(getResources().getString(R.string.dongfangweishi), R.drawable.logodongfang);
        channelLogoMapping.put(getResources().getString(R.string.shanghaiweishi), R.drawable.logodongfang);
        channelLogoMapping.put(getResources().getString(R.string.dongnanweishi), R.drawable.logodongnan);
        channelLogoMapping.put(getResources().getString(R.string.fujianweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.guangdongweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.guangxiweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.guizhouweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.hebeiweishi), R.drawable.logohebei);
        channelLogoMapping.put(getResources().getString(R.string.henanweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.heilongjiangtai), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.heilongjiangweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.hubeiweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.hunanweishi), R.drawable.logohunan);
        channelLogoMapping.put(getResources().getString(R.string.hunanweishigaoqing), R.drawable.logohunan);
        channelLogoMapping.put(getResources().getString(R.string.jiangsuweishi), R.drawable.logojiangsu);
        channelLogoMapping.put(getResources().getString(R.string.jiangxiweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.jilinweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.liaoningweishi), R.drawable.logoliaoning);
        channelLogoMapping.put(getResources().getString(R.string.neimenggutai), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.neimengguweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.ningxiaweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.shandongweishi), R.drawable.logoshandong);
        channelLogoMapping.put(getResources().getString(R.string.shanxiweishi), R.drawable.logoshanxi);
        channelLogoMapping.put(getResources().getString(R.string.shanxi1weishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.sichuanweishi), R.drawable.logosichuan);
        channelLogoMapping.put(getResources().getString(R.string.tianjinweishi), R.drawable.logotianjin);
        channelLogoMapping.put(getResources().getString(R.string.yunnanweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.xizangweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.xinjiangweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.zhejiangweishi), R.drawable.logozhejiang);
        channelLogoMapping.put(getResources().getString(R.string.shenzhengweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.fenghuangweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.gansuweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.qinghaiweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.sichuangaoqing), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.sichuanweishigaoqing), R.drawable.logosichuan);
        channelLogoMapping.put(getResources().getString(R.string.sichuanyingshigaoqing), R.drawable.logosichuan);
        channelLogoMapping.put(getResources().getString(R.string.zhejiangweishigaoqing), R.drawable.logozhejiang);
        channelLogoMapping.put(getResources().getString(R.string.zhejianggaoqing), R.drawable.logozhejiang);
        channelLogoMapping.put(getResources().getString(R.string.beijingweishigaoqing), R.drawable.logobeijing);
        channelLogoMapping.put(getResources().getString(R.string.beijinggaoqing), R.drawable.logobeijing);
        channelLogoMapping.put(getResources().getString(R.string.shanghaiweishigaoqing), R.drawable.logodongfang);
        channelLogoMapping.put(getResources().getString(R.string.shanghaigaoqing), R.drawable.logodongfang);
        channelLogoMapping.put(getResources().getString(R.string.guangdongweishigaoqing), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.guangdonggaoqing), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.shenzhengweishigaoqing), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.jiangsuweishigaoqing), R.drawable.logojiangsu);
        channelLogoMapping.put(getResources().getString(R.string.jiangsugaoqing), R.drawable.logojiangsu);
        channelLogoMapping.put(getResources().getString(R.string.heilongjiangweishigaoqing), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.heilongjianggaoqing), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.hunanweishigaoqing), R.drawable.logohunan);
        channelLogoMapping.put(getResources().getString(R.string.hunangaoqing), R.drawable.logohunan);
        channelLogoMapping.put(getResources().getString(R.string.hubeigaoqing), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.shandonggaoqing), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.shenzhenggaoqing), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.tianjingaoqing), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.quanjishi), R.drawable.logoquanjishi);
        channelLogoMapping.put(getResources().getString(R.string.guofangjunshi), R.drawable.logoguofangjunshi);
        channelLogoMapping.put(getResources().getString(R.string.dieshipindao), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.dongfangcaijin), R.drawable.logodongfangcaijin);
        channelLogoMapping.put(getResources().getString(R.string.dongmanxiuchang), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.dushijuchang), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.emeidianying), R.drawable.logoemei);
        channelLogoMapping.put(getResources().getString(R.string.sihaidiaoyu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.fazhitiandi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.fengyunzuqiu), R.drawable.logofengyunzuqiu);
        channelLogoMapping.put(getResources().getString(R.string.huanxiaojuchang), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.jiayougouwu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.jiatinglicai), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.jinsepindao), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.jingbaotiyu), R.drawable.logojinbaotiyu);
        channelLogoMapping.put(getResources().getString(R.string.jinyingkatong), R.drawable.logojinyingkatong);
        channelLogoMapping.put(getResources().getString(R.string.jisuqiche), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.kuailechongwu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.laogushi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.liangzhuangpindao), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.liuxueshijie), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.lvyoupindao), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.lvyouweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.meiliyinyue), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.shenghuoshishang), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.shijiedili), R.drawable.logoshijiedili);
        channelLogoMapping.put(getResources().getString(R.string.tianyuanweiqi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.weishengjiankang), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.yingyufudao), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.yingyufudao1), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.youxifengyun), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.youxifengyun1), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.youxijingji), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.yougouwu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.yunyuzhinan), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.zhengquanzixun), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.jiaoyupindao), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.zhongguojiaoyu1), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.zhongguoqixiang), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.zhongguoqixiangtai), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.zhongguoqixiang1), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.jingcaisichuan), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.xinyule), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.xingyule), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.xingyuanyinhua), R.drawable.xingyuanyinghua);
        channelLogoMapping.put(getResources().getString(R.string.xingyuanjuchang), R.drawable.xingyuanjuchang);
        channelLogoMapping.put(getResources().getString(R.string.xingyuanxinzhi), R.drawable.xingyuanxinzhi);
        channelLogoMapping.put(getResources().getString(R.string.xingyuanxinyi), R.drawable.xingyuanxinyi);
        channelLogoMapping.put(getResources().getString(R.string.xingyuanai), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.xingyuanchengzhang), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.xingyuanoumeijuchang), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.xingyuanoumeiyuanxian), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.xingyuanshouying), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.xingyuanxinzhi1), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.xingyuanyazhoujuchang), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.xingyuanyazhouyuanxian), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.jiatingjuchangdianshiju), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.jingdianjuchangdianshiju), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.threeDpindao), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.HBO), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.TVB8), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.BBC), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.KBS), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.NHK), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.sichuan2), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.sichuanwenhualvyou), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.sichuan3), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.sichuanjingji), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.sichuan4), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.sichuanxinwentongxun), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.sichuan5), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.sichuanyingshiwenhua), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.sichuan6), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.sichuanxingkonggouwu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.sichuan7), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.sichuanfunvertong), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.sichuan8), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.sichuankejiao), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.sctv09), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.sichuan9), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.mianyang1), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.mianyang1_1), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.mianyang2), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.mianyang2_1), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.mianyang3), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.mianyang3_1), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.mianyang4), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.mianyang4_1), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.santai1), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.santai2), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.santai3), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.santai4), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.beichuan1), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.beichuan2), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.zitong3), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.zitong4), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.zitong5), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.zitongzibanjiemu1), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.zitongzibanjiemu2), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.cdtv1), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.cdtv2), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.cdtv3), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.cdtv4), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.cdtv5), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.cdtv6), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.cdtv7), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.cdtv8), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.cdtvgaoqing), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.cetv1), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.cetv2), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.CHCdongzuoyingyuan), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.CHCgaoqing), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.DOXyinxiangshijie), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.DVshenghuo), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.IjiaTV), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.SCTV1), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.SCTV2), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.SCTV3), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.SCTV4), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.SCTV5), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.SCTV6), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.SCTV7), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.SCTVemei), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.SCTVgonggong), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.SCTVkejiao), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.SCTVxingkong), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.baixingjiankang), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.baobeijia), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.bingtuanweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.caifutianxia), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.caiminzaixian), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.chemi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.chengdujiaotongguangbo), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.chengdujingjiguangbo), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.chengduwangluoguangbo), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.chengduxinwenguangbo), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.chengduxiuxianguangbo), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.chengshijianshe), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.diyijuchang), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.dianzitiyu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.dieshi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.dushizhisheng), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.dushu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.faxianzhilv), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.fengshanggouwu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.fengyunjuchang), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.fengyunyinyue), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.gaoerfu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.gaoerfuwangqiu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.haoxianggouwu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.huaxiazhisheng), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.huaijiujuchang), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.huanqiugouwu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.huanqiulvyou), R.drawable.logohuanqiulvyou);
        channelLogoMapping.put(getResources().getString(R.string.huanqiuzixunguangbo), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.jiajiagouwu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.jiatingjiankang), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.jiazhengpindao), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.jiajiakatong), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.jinniuyouxiantai), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.jingjizhisheng), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.jingpindaoshi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.kakukatong), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.kangbaweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.kaoshizaixian), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.kuailegouwu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.laonianfu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.liyuan), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.liangzhuang), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.minzuzhisheng), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.nvxingshishang), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.ouzhouzuqiu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.qicaixiju), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.qimo), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.quanyuchengduguangbo), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.renwu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.rongchengxianfeng), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.sheying), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.shenzhouzhisheng), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.shidaichuxing), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.shidaifengshang), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.shidaijiaju), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.shidaimeishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.shishanggouwu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.shoucangtianxia), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.shuhua), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.shuowenjiezi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.tianyinweiyiyinyue), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.wangluoqipai), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.wenhuajingpin), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.wenwubaoku), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.wenyizhisheng), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.wushushijie), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.xianfengjilu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.xiandainvxing), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.xindongman), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.xinkedongman), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.xingfucai), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.xinyuezhisheng), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.youyoubaobei), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.zaoqijiaoyu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.zhiyezhinan), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.zhongguotianqi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.zhongguozhisheng), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.zhonghuazhisheng), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.zhongshigouwu), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.zhongxuesheng), R.drawable.logotv);
        // channelLogoMapping.put("NHK", R.drawable.logotv);

    }
}

