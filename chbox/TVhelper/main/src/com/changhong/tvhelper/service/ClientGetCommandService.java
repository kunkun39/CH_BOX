package com.changhong.tvhelper.service;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.*;

import android.app.ActivityManager;
import android.content.Context;
import com.changhong.common.domain.NetworkStatus;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.service.ClientSocketInterface;
import com.changhong.common.utils.CaVerifyUtil;
import com.changhong.common.utils.MobilePerformanceUtils;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.utils.QuickQuireMessageUtil;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.widgets.IpSelectorDataServer;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import com.changhong.setting.utils.NetEstimateUtils;
import com.changhong.touying.activity.*;
import com.changhong.touying.dialog.MusicPlayer;
import com.changhong.tvhelper.*;
import com.changhong.tvhelper.activity.TVChannelSearchActivity;
import com.changhong.tvhelper.activity.TVChannelShowActivity;
import com.changhong.tvhelper.activity.TVHelperMainActivity;

public class ClientGetCommandService extends Service implements ClientSocketInterface {

    protected static final String TAG = "CHTVhelper";

    private static final int HANDLER_MESSAGE_WHAT_CA = 19;
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
//                        if (TVChannelSearchActivity.mHandler != null) {
//                            Log.e(TAG, "刷新...");
//                            TVChannelSearchActivity.mHandler.sendEmptyMessage(0);
//                        }
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
                    case HANDLER_MESSAGE_WHAT_CA:
                    {
                    	QuickQuireMessageUtil.getInstance().feedback((String)msg.obj);
                    }break;
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
        
        new ThreadQuickFeedBack().start();
    }

    /*************************************************手机端不停的获得盒子广播线程*****************************************/

    private class GetServerIP extends Thread {

        public void run() {
            //ClientSendCommandService.serverIpList.clear();
            //ClientSendCommandService.serverIpListMap.clear();
        	IpSelectorDataServer.getInstance().clear();
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
                        String content = new String(by, 0, dgPacket.getLength());
                        AsyncTask.execute(new CommandRunable().setContent(content).setServerAddress(serverAddress));
                        if (exit) {
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }finally {
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

    private class CommandRunable implements Runnable
    {
        String mContent,mServerAddress;
        public CommandRunable setContent(String content) {
            mContent = new String(content);
            return this;
        }

        public CommandRunable setServerAddress(String address) {
            mServerAddress = address;
            return this;
        }

        @Override
        public void run() {
            String serverIP = IpSelectorDataServer.getInstance().getCurrentIp();
            String[] tokens = StringUtils.delimitedListToStringArray(mContent, "|");
            String boxName = NetworkUtils.convertCHBoxName(tokens[0]);

            if (StringUtils.hasLength(mServerAddress)) {
                Log.w("COMMAND_CLEAN", (serverIP == null ? "" : serverIP) + "-" + mServerAddress + "-" + IpSelectorDataServer.getInstance().getName(mServerAddress));

                if (!IpSelectorDataServer.getInstance().getIpList().contains(mServerAddress)) {
                    IpSelectorDataServer.getInstance().addIp(mServerAddress, boxName);
                } else if (serverIP != null && mServerAddress.equals(serverIP)) {
                    /**
                     * 更新当前server的活动时间
                     */
                    IpSelectorDataServer.getInstance().activateIp(mServerAddress);
                    IpSelectorDataServer.getInstance().modifyName(mServerAddress, boxName);
                    /**
                     * 设置服务端网络状态
                     */
                    try {
                        if (tokens.length > 1) {
                            NetEstimateUtils.serverNetworkStatus = NetworkStatus.valueOf(tokens[1]);
                        }
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
                    //不做处理
                    IpSelectorDataServer.getInstance().activateIp(mServerAddress);
                    IpSelectorDataServer.getInstance().modifyName(mServerAddress, boxName);
                }
            }
        }
    }

    /***********************************************手机端不停的监控盒子是否有广播发出**************************************/

    private class BoxMinitorThread extends Thread {

        public void run() {
            while (true) {
//                long during = System.currentTimeMillis() - time;
//                if (during > 5000 && time != 0l) {
//                    Log.e("COMMAND_CLEAN", String.valueOf(during));
//                    clearIpList();
//                }
            	IpSelectorDataServer.getInstance().removeIpOutOfTime();
                SystemClock.sleep(1000);
            }
        }
    }

    private void clearIpList() {
    	IpSelectorDataServer.getInstance().clear();
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
                    if (StringUtils.hasLength(IpSelectorDataServer.getInstance().getCurrentIp()) && IpSelectorDataServer.getInstance().getCurrentIp().equals(dgPacket.getAddress().getHostAddress())) {
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
    
    class ThreadQuickFeedBack extends Thread
    { 	
    	@Override
    	public void run()
    	{
    		DatagramSocket socket = null;
    		byte[] by = new byte[1024];
            
    		while(true)
    		{
    			try
    			{
    				socket = new DatagramSocket(NEW_BACK_PORT);    			
    				while(true)
    				{
    					DatagramPacket dgPacket = new DatagramPacket(by, by.length);
    					try {    						
    						socket.receive(dgPacket);
    						mHandler.sendMessage(mHandler.obtainMessage(HANDLER_MESSAGE_WHAT_CA, new String(dgPacket.getData(), 0, dgPacket.getData().length)));
    					} catch (IOException e) {
    						e.printStackTrace();
    					}        				
    				}    				
    			}
    			catch(SocketException  e){
    				e.printStackTrace();
    			}finally {
    				if(socket != null){
    					socket.close();
    				}
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

        //中央台LOG设置
        channelLogoMapping.put(getResources().getString(R.string.cctv1_1), R.drawable.cctv1);
        channelLogoMapping.put(getResources().getString(R.string.cctv1_2), R.drawable.cctv1);
        channelLogoMapping.put(getResources().getString(R.string.cctv1_3), R.drawable.cctv1);
        channelLogoMapping.put(getResources().getString(R.string.cctv1_4), R.drawable.cctv1);
        channelLogoMapping.put(getResources().getString(R.string.cctv1hd_1), R.drawable.cctv1);
        channelLogoMapping.put(getResources().getString(R.string.cctv1hd_2), R.drawable.cctv1);
        channelLogoMapping.put(getResources().getString(R.string.cctv1hd_3), R.drawable.cctv1);
        channelLogoMapping.put(getResources().getString(R.string.cctv2_1), R.drawable.cctv2);
        channelLogoMapping.put(getResources().getString(R.string.cctv2_2), R.drawable.cctv2);
        channelLogoMapping.put(getResources().getString(R.string.cctv2_3), R.drawable.cctv2);
        channelLogoMapping.put(getResources().getString(R.string.cctv2_4), R.drawable.cctv2);
        channelLogoMapping.put(getResources().getString(R.string.cctv2hd_1), R.drawable.cctv2);
        channelLogoMapping.put(getResources().getString(R.string.cctv3_1), R.drawable.cctv3);
        channelLogoMapping.put(getResources().getString(R.string.cctv3_2), R.drawable.cctv3);
        channelLogoMapping.put(getResources().getString(R.string.cctv3_3), R.drawable.cctv3);
        channelLogoMapping.put(getResources().getString(R.string.cctv3_4), R.drawable.cctv3);
        channelLogoMapping.put(getResources().getString(R.string.cctv3hd), R.drawable.cctv3);
        channelLogoMapping.put(getResources().getString(R.string.cctv4_1), R.drawable.cctv4);
        channelLogoMapping.put(getResources().getString(R.string.cctv4_2), R.drawable.cctv4);
        channelLogoMapping.put(getResources().getString(R.string.cctv4_3), R.drawable.cctv4);
        channelLogoMapping.put(getResources().getString(R.string.cctv4_4), R.drawable.cctv4);
        channelLogoMapping.put(getResources().getString(R.string.cctv5hd), R.drawable.cctv5);
        channelLogoMapping.put(getResources().getString(R.string.cctv5hd_1), R.drawable.cctv5hd);
        channelLogoMapping.put(getResources().getString(R.string.cctv5hd_2), R.drawable.cctv5hd);
        channelLogoMapping.put(getResources().getString(R.string.cctv5hd_3), R.drawable.cctv5hd);
        channelLogoMapping.put(getResources().getString(R.string.cctv5_1), R.drawable.cctv5);
        channelLogoMapping.put(getResources().getString(R.string.cctv5_2), R.drawable.cctv5);
        channelLogoMapping.put(getResources().getString(R.string.cctv5_3), R.drawable.cctv5);
        channelLogoMapping.put(getResources().getString(R.string.cctv5_4), R.drawable.cctv5);
        channelLogoMapping.put(getResources().getString(R.string.cctv6_1), R.drawable.cctv6);
        channelLogoMapping.put(getResources().getString(R.string.cctv6_2), R.drawable.cctv6);
        channelLogoMapping.put(getResources().getString(R.string.cctv6_3), R.drawable.cctv6);
        channelLogoMapping.put(getResources().getString(R.string.cctv6_4), R.drawable.cctv6);
        channelLogoMapping.put(getResources().getString(R.string.cctv6hd), R.drawable.cctv6);
        channelLogoMapping.put(getResources().getString(R.string.cctv7_1), R.drawable.cctv7);
        channelLogoMapping.put(getResources().getString(R.string.cctv7_2), R.drawable.cctv7);
        channelLogoMapping.put(getResources().getString(R.string.cctv7_3), R.drawable.cctv7);
        channelLogoMapping.put(getResources().getString(R.string.cctv7_4), R.drawable.cctv7);
        channelLogoMapping.put(getResources().getString(R.string.cctv7hd_1), R.drawable.cctv7);
        channelLogoMapping.put(getResources().getString(R.string.cctv8_1), R.drawable.cctv8);
        channelLogoMapping.put(getResources().getString(R.string.cctv8_2), R.drawable.cctv8);
        channelLogoMapping.put(getResources().getString(R.string.cctv8_3), R.drawable.cctv8);
        channelLogoMapping.put(getResources().getString(R.string.cctv8_4), R.drawable.cctv8);
        channelLogoMapping.put(getResources().getString(R.string.cctv8hd), R.drawable.cctv8);
        channelLogoMapping.put(getResources().getString(R.string.cctv8hd_1), R.drawable.cctv8);
        channelLogoMapping.put(getResources().getString(R.string.cctv9_1), R.drawable.cctv9);
        channelLogoMapping.put(getResources().getString(R.string.cctv9_2), R.drawable.cctv9);
        channelLogoMapping.put(getResources().getString(R.string.cctv9_3), R.drawable.cctv9);
        channelLogoMapping.put(getResources().getString(R.string.cctv9hd), R.drawable.cctv9);
        channelLogoMapping.put(getResources().getString(R.string.cctv10_1), R.drawable.cctv10);
        channelLogoMapping.put(getResources().getString(R.string.cctv10_2), R.drawable.cctv10);
        channelLogoMapping.put(getResources().getString(R.string.cctv10_3), R.drawable.cctv10);
        channelLogoMapping.put(getResources().getString(R.string.cctv10_4), R.drawable.cctv10);
        channelLogoMapping.put(getResources().getString(R.string.cctv10hd), R.drawable.cctv10);
        channelLogoMapping.put(getResources().getString(R.string.cctv11_1), R.drawable.cctv11);
        channelLogoMapping.put(getResources().getString(R.string.cctv11_2), R.drawable.cctv11);
        channelLogoMapping.put(getResources().getString(R.string.cctv11_3), R.drawable.cctv11);
        channelLogoMapping.put(getResources().getString(R.string.cctv11_4), R.drawable.cctv11);
        channelLogoMapping.put(getResources().getString(R.string.cctv11hd), R.drawable.cctv11);
        channelLogoMapping.put(getResources().getString(R.string.cctv12_1), R.drawable.cctv12);
        channelLogoMapping.put(getResources().getString(R.string.cctv12_2), R.drawable.cctv12);
        channelLogoMapping.put(getResources().getString(R.string.cctv12_3), R.drawable.cctv12);
        channelLogoMapping.put(getResources().getString(R.string.cctv12_4), R.drawable.cctv12);
        channelLogoMapping.put(getResources().getString(R.string.cctv12hd), R.drawable.cctv12);
        channelLogoMapping.put(getResources().getString(R.string.cctv13_1), R.drawable.cctvnews);
        channelLogoMapping.put(getResources().getString(R.string.cctv13_2), R.drawable.cctvnews);
        channelLogoMapping.put(getResources().getString(R.string.cctv13_3), R.drawable.cctvnews);
        channelLogoMapping.put(getResources().getString(R.string.cctv13_4), R.drawable.cctvnews);
        channelLogoMapping.put(getResources().getString(R.string.cctv13_5), R.drawable.cctvnews);
        channelLogoMapping.put(getResources().getString(R.string.cctv13_6), R.drawable.cctvnews);
        channelLogoMapping.put(getResources().getString(R.string.cctv13_7), R.drawable.cctvnews);
        channelLogoMapping.put(getResources().getString(R.string.cctv13_8), R.drawable.cctvnews);
        channelLogoMapping.put(getResources().getString(R.string.cctv13_9), R.drawable.cctvnews);
        channelLogoMapping.put(getResources().getString(R.string.cctv13hd), R.drawable.cctvnews);
        channelLogoMapping.put(getResources().getString(R.string.cctv14_1), R.drawable.cctv14);
        channelLogoMapping.put(getResources().getString(R.string.cctv14_2), R.drawable.cctv14);
        channelLogoMapping.put(getResources().getString(R.string.cctv14_3), R.drawable.cctv14);
        channelLogoMapping.put(getResources().getString(R.string.cctv14_4), R.drawable.cctv14);
        channelLogoMapping.put(getResources().getString(R.string.cctv14_5), R.drawable.cctv14);
        channelLogoMapping.put(getResources().getString(R.string.cctv14_6), R.drawable.cctv14);
        channelLogoMapping.put(getResources().getString(R.string.cctv14_7), R.drawable.cctv14);
        channelLogoMapping.put(getResources().getString(R.string.cctv14hd), R.drawable.cctv14);
        channelLogoMapping.put(getResources().getString(R.string.cctv14hd_1), R.drawable.cctv14);
        channelLogoMapping.put(getResources().getString(R.string.cctv15_1), R.drawable.cctv15);
        channelLogoMapping.put(getResources().getString(R.string.cctv15_2), R.drawable.cctv15);
        channelLogoMapping.put(getResources().getString(R.string.cctv15_3), R.drawable.cctv15);

        channelLogoMapping.put(getResources().getString(R.string.anhuiweishi), R.drawable.logoanhui);
        channelLogoMapping.put(getResources().getString(R.string.anhuigaoqing), R.drawable.logoanhui);
        channelLogoMapping.put(getResources().getString(R.string.anhuiweishigaoqing), R.drawable.logoanhui);
        channelLogoMapping.put(getResources().getString(R.string.beijingweishi), R.drawable.logobeijing);
        channelLogoMapping.put(getResources().getString(R.string.chongqingweishi), R.drawable.logochongqing);
        channelLogoMapping.put(getResources().getString(R.string.dongfangweishi), R.drawable.logodongfang);
        channelLogoMapping.put(getResources().getString(R.string.dongnanweishi), R.drawable.logodongnan);
        channelLogoMapping.put(getResources().getString(R.string.fujianweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.fenghuangweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.guangdongweishi), R.drawable.logotv);
        channelLogoMapping.put(getResources().getString(R.string.guangxiweishi), R.drawable.logoguangxi);
        channelLogoMapping.put(getResources().getString(R.string.guizhouweishi), R.drawable.logoguizhou);
        channelLogoMapping.put(getResources().getString(R.string.gansuweishi), R.drawable.logogansu);
        channelLogoMapping.put(getResources().getString(R.string.qinghaiweishi), R.drawable.logoqinghai);
        channelLogoMapping.put(getResources().getString(R.string.hebeiweishi), R.drawable.logohebei);
        channelLogoMapping.put(getResources().getString(R.string.henanweishi), R.drawable.logohenan);
        channelLogoMapping.put(getResources().getString(R.string.heilongjiangtai), R.drawable.logoheilongjiang);
        channelLogoMapping.put(getResources().getString(R.string.heilongjiangweishi), R.drawable.logoheilongjiang);
        channelLogoMapping.put(getResources().getString(R.string.heilongjiangweishigaoqing), R.drawable.logoheilongjiang);
        channelLogoMapping.put(getResources().getString(R.string.heilongjiangweishigaoqing_1), R.drawable.logoheilongjiang);
        channelLogoMapping.put(getResources().getString(R.string.heilongjianggaoqing), R.drawable.logoheilongjiang);
        channelLogoMapping.put(getResources().getString(R.string.hubeiweishi), R.drawable.logohubei);
        channelLogoMapping.put(getResources().getString(R.string.hubeigaoqing), R.drawable.logohubei);
        channelLogoMapping.put(getResources().getString(R.string.hubeiweishigaoqing), R.drawable.logohubei);
        channelLogoMapping.put(getResources().getString(R.string.hunanweishi), R.drawable.logohunan);
        channelLogoMapping.put(getResources().getString(R.string.hunanweishigaoqing), R.drawable.logohunan);
        channelLogoMapping.put(getResources().getString(R.string.hunangaoqing), R.drawable.logohunan);
        channelLogoMapping.put(getResources().getString(R.string.fujianweishi), R.drawable.logofujian);
        channelLogoMapping.put(getResources().getString(R.string.jiangsuweishi), R.drawable.logojiangsu);
        channelLogoMapping.put(getResources().getString(R.string.jiangxiweishi), R.drawable.logojiangxi);
        channelLogoMapping.put(getResources().getString(R.string.jilinweishi), R.drawable.logojilin);
        channelLogoMapping.put(getResources().getString(R.string.jiangsuweishigaoqing), R.drawable.logojiangsu);
        channelLogoMapping.put(getResources().getString(R.string.jiangsuweishigaoqing_1), R.drawable.logojiangsu);
        channelLogoMapping.put(getResources().getString(R.string.jiangsugaoqing), R.drawable.logojiangsu);
        channelLogoMapping.put(getResources().getString(R.string.liaoningweishi), R.drawable.logoliaoning);
        channelLogoMapping.put(getResources().getString(R.string.liaoninggaoqing), R.drawable.logoliaoning);
        channelLogoMapping.put(getResources().getString(R.string.liaoningweishigaoqing), R.drawable.logoliaoning);
        channelLogoMapping.put(getResources().getString(R.string.neimenggutai), R.drawable.logoneimenggu);
        channelLogoMapping.put(getResources().getString(R.string.neimengguweishi), R.drawable.logoneimenggu);
        channelLogoMapping.put(getResources().getString(R.string.ningxiaweishi), R.drawable.logoningxia);
        channelLogoMapping.put(getResources().getString(R.string.shandongweishi), R.drawable.logoshandong);
        channelLogoMapping.put(getResources().getString(R.string.shandonggaoqing), R.drawable.logoshandong);
        channelLogoMapping.put(getResources().getString(R.string.shandongweishigaoqing), R.drawable.logoshandong);
        channelLogoMapping.put(getResources().getString(R.string.shanxiweishi), R.drawable.logoshanxi);
        channelLogoMapping.put(getResources().getString(R.string.shanxi1weishi), R.drawable.logoshanxi);
        channelLogoMapping.put(getResources().getString(R.string.shanghaiweishi), R.drawable.logodongfang);
        channelLogoMapping.put(getResources().getString(R.string.shanghaiweishigaoqing), R.drawable.logodongfang);
        channelLogoMapping.put(getResources().getString(R.string.shanghaiweishigaoqing_1), R.drawable.logodongfang);
        channelLogoMapping.put(getResources().getString(R.string.shenzhengweishi), R.drawable.logoshenzheng);
        channelLogoMapping.put(getResources().getString(R.string.shenzhengweishigaoqing), R.drawable.logoshenzheng);
        channelLogoMapping.put(getResources().getString(R.string.shenzhengweishigaoqing_1), R.drawable.logoshenzheng);
        channelLogoMapping.put(getResources().getString(R.string.shenzhenggaoqing), R.drawable.logoshenzheng);
        channelLogoMapping.put(getResources().getString(R.string.shanghaigaoqing), R.drawable.logodongfang);
        channelLogoMapping.put(getResources().getString(R.string.tianjinweishi), R.drawable.logotianjin);
        channelLogoMapping.put(getResources().getString(R.string.tianjingaoqing), R.drawable.logotianjin);
        channelLogoMapping.put(getResources().getString(R.string.tianjingaoqing_1), R.drawable.logotianjin);
        channelLogoMapping.put(getResources().getString(R.string.yunnanweishi), R.drawable.logoyunan);
        channelLogoMapping.put(getResources().getString(R.string.xizangweishi), R.drawable.logoyunan);
        channelLogoMapping.put(getResources().getString(R.string.xinjiangweishi), R.drawable.logoxinjiang);
        channelLogoMapping.put(getResources().getString(R.string.zhejiangweishi), R.drawable.logozhejiang);
        channelLogoMapping.put(getResources().getString(R.string.zhejiangweishigaoqing), R.drawable.logozhejiang);
        channelLogoMapping.put(getResources().getString(R.string.zhejiangweishigaoqing_1), R.drawable.logozhejiang);
        channelLogoMapping.put(getResources().getString(R.string.zhejianggaoqing), R.drawable.logozhejiang);
        channelLogoMapping.put(getResources().getString(R.string.beijingweishigaoqing), R.drawable.logobeijing);
        channelLogoMapping.put(getResources().getString(R.string.beijingweishigaoqing_1), R.drawable.logobeijing);
        channelLogoMapping.put(getResources().getString(R.string.beijinggaoqing), R.drawable.logobeijing);
        channelLogoMapping.put(getResources().getString(R.string.guangdongweishi), R.drawable.logoguangdong);
        channelLogoMapping.put(getResources().getString(R.string.guangdongweishigaoqing), R.drawable.logoguangdong);
        channelLogoMapping.put(getResources().getString(R.string.guangdongweishigaoqing_1), R.drawable.logoguangdong);
        channelLogoMapping.put(getResources().getString(R.string.guangdonggaoqing), R.drawable.logoguangdong);
        channelLogoMapping.put(getResources().getString(R.string.lvyouweishi), R.drawable.logolvyou);
        channelLogoMapping.put(getResources().getString(R.string.bingtuanweishi), R.drawable.logobingtuan);

        //四川和成都
        channelLogoMapping.put(getResources().getString(R.string.sichuanweishi), R.drawable.logosichuan);
        channelLogoMapping.put(getResources().getString(R.string.emeidianying), R.drawable.logoemei);
        channelLogoMapping.put(getResources().getString(R.string.sichuangaoqing), R.drawable.logosichuan);
        channelLogoMapping.put(getResources().getString(R.string.sichuanweishigaoqing), R.drawable.logosichuan);
        channelLogoMapping.put(getResources().getString(R.string.sichuanyingshigaoqing), R.drawable.logosichuan);

        //其他
        channelLogoMapping.put(getResources().getString(R.string.quanjishi), R.drawable.logoquanjishi);
        channelLogoMapping.put(getResources().getString(R.string.guofangjunshi), R.drawable.logoguofangjunshi);
        channelLogoMapping.put(getResources().getString(R.string.dongfangcaijin), R.drawable.logodongfangcaijin);
        channelLogoMapping.put(getResources().getString(R.string.fengyunzuqiu), R.drawable.logofengyunzuqiu);
        channelLogoMapping.put(getResources().getString(R.string.jingbaotiyu), R.drawable.logojinbaotiyu);
        channelLogoMapping.put(getResources().getString(R.string.jinyingkatong), R.drawable.logojinyingkatong);
        channelLogoMapping.put(getResources().getString(R.string.shijiedili), R.drawable.logoshijiedili);
        channelLogoMapping.put(getResources().getString(R.string.xingyuanyinhua), R.drawable.xingyuanyinghua);
        channelLogoMapping.put(getResources().getString(R.string.xingyuanjuchang), R.drawable.xingyuanjuchang);
        channelLogoMapping.put(getResources().getString(R.string.xingyuanxinzhi), R.drawable.xingyuanxinzhi);
        channelLogoMapping.put(getResources().getString(R.string.xingyuanxinyi), R.drawable.xingyuanxinyi);
        channelLogoMapping.put(getResources().getString(R.string.huanqiulvyou), R.drawable.logohuanqiulvyou);

    }
}

