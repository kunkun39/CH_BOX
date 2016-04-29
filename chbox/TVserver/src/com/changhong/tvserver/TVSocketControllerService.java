package com.changhong.tvserver;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.input.InputManager;
import android.media.AudioManager;
import android.os.*;
import android.os.Process;

import com.changhong.tvserver.utils.CaVerifyUtil;
import com.changhong.tvserver.utils.Config;
import com.changhong.tvserver.utils.NetworkUtils;
import com.changhong.tvserver.utils.StringUtils;
import com.chome.virtualkey.virtualkey;
import com.changhong.tvserver.touying.image.ImageShowPlayingActivity;
import com.changhong.tvserver.touying.music.MusicViewPlayingActivity;
import com.changhong.tvserver.touying.other.OtherShowService;
import com.changhong.tvserver.touying.video.VideoViewPlayingActivity;
import com.changhong.tvserver.tvmall.MallListActivity;

import org.apache.http.conn.util.InetAddressUtils;
import com.ots.deviceinfoprovide.DeviceInfo;


import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.JsonReader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;



public class TVSocketControllerService extends Service {
    private static final String TAG = "TVSocketControlService";

    virtualkey t = new virtualkey();
    Instrumentation instrumentation = new Instrumentation();

    /**
     * heart internal time which stand for server send info to clients for this value
     */
    private static final int TIME = 500;

    /**
     * server ip
     */
    private String ip = null;

    /**
     * handle for this service
     */
    private Handler handler = null;

    private String DeviceModel = null;
    private String serverInfo = null;

    /**
     * message for client send
     */
    private String msg1 = null;

    /**
     * the tag which stand for music or video stop play
     * 1 - for video
     * 2 - for music
     */
    public static int STOP_PLAY_TAG = 0;
    
    private String targetIp = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 系统默认启动是不启动activity, 而是启动Service, 所以Service也需要从新读一下名称
         */
        MainActivity.getInstance().CH_BOX_NAME = MainActivity.getBoxName(TVSocketControllerService.this);

        DeviceInfo.CollectInfo();
        DeviceModel = DeviceInfo.DeviceModel;

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                	String msgCpy = (String)msg.obj;
                    switch (msg.what) {
                        case 1:
                            if (msgCpy.equals("key:up")) {
                                Log.e(TAG, "key:up");
                                t.vkey_input(103, 1);
                            } else if (msgCpy.equals("key:down")) {
                                Log.e(TAG, "key:down");
                                t.vkey_input(108, 1);
                            } else if (msgCpy.equals("key:left")) {
                                Log.e(TAG, "key:left");
                                t.vkey_input(105, 1);
                            } else if (msgCpy.equals("key:right")) {
                                Log.e(TAG, "key:right");
                                t.vkey_input(106, 1);
                            } else if (msgCpy.equals("key:ok")) {
                                Log.e(TAG, "key:ok");
                                t.vkey_input(28, 1);
                            } else if (msgCpy.equals("key:back")) {
                                Log.e(TAG, "key:back");
                                //sendKey(KeyEvent.KEYCODE_BACK);
                                t.vkey_input(158, 1);
                            } else if (msgCpy.equals("key:menu")) {
                                Log.e(TAG, "key:menu");
                                t.vkey_input(139, 1);
                            } else if (msgCpy.equals("key:home")) {
                                Log.e(TAG, "key:home");
                                t.vkey_input(102, 1);
                            } else if (msgCpy.equals("key:volumeup")) {
                                Log.e(TAG, "key:volumeup");
                                t.vkey_input(115, 1);
                                AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
                                sendbackSmallMessage(targetIp, msgCpy, String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + "/" + audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)));
                            } else if (msgCpy.equals("key:volumedown")) {
                                Log.e(TAG, "key:volumedown");
                                t.vkey_input(114, 1);
                                AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
                                sendbackSmallMessage(targetIp, msgCpy, String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + "/" + audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)));
                            } else if (msgCpy.equals("key:volume_mute")) {
                                Log.e(TAG, "key:volume_mute");
                                t.vkey_input(113, 1);
                            } else if (msgCpy.equals("key:power")) {
                                Log.e(TAG, "key:power");
                                if (Config.PLATFORM == Config.PLATFORM_S805) {
                                    Runtime.getRuntime().exec("echo wh > /sys/power/wake_lock");
                                    t.vkey_input(116, 1);
                                } else {
                                    t.vkey_input(0x7f01, 1);
                                }
                                //sendKey(KeyEvent.KEYCODE_POWER);
                            } else if (msgCpy.equals("key:0")) {
                                Log.e(TAG, "key:0");
                                t.vkey_input(11, 1);
                            } else if (msgCpy.equals("key:1")) {
                                Log.e(TAG, "key:");
                                t.vkey_input(2, 1);
                            } else if (msgCpy.equals("key:2")) {
                                Log.e(TAG, "key:2");
                                t.vkey_input(3, 1);
                            } else if (msgCpy.equals("key:3")) {
                                Log.e(TAG, "key:3");
                                t.vkey_input(4, 1);
                            } else if (msgCpy.equals("key:4")) {
                                Log.e(TAG, "key:4");
                                t.vkey_input(5, 1);
                            } else if (msgCpy.equals("key:5")) {
                                Log.e(TAG, "key:5");
                                t.vkey_input(6, 1);
                            } else if (msgCpy.equals("key:6")) {
                                Log.e(TAG, "key:6");
                                t.vkey_input(7, 1);
                            } else if (msgCpy.equals("key:7")) {
                                Log.e(TAG, "key:7");
                                t.vkey_input(8, 1);
                            } else if (msgCpy.equals("key:8")) {
                                Log.e(TAG, "key:8");
                                t.vkey_input(9, 1);
                            } else if (msgCpy.equals("key:9")) {
                                Log.e(TAG, "key:9");
                                t.vkey_input(10, 1);
                            } else if (msgCpy.equals("key:search")) {
                                Log.e(TAG, "key:9");
                                //sendKey(KeyEvent.KEYCODE_SEARCH);
                                t.vkey_input(127, 1);
                            } else if (msgCpy.equals("key:media_forward")) {
                                Log.e(TAG, "key:9");
                                //sendKey(KeyEvent.KEYCODE_MEDIA_FAST_FORWARD);
                                t.vkey_input(120, 1);
                            } else if (msgCpy.equals("key:media_backward")) {
                                Log.e(TAG, "key:9");
                                t.vkey_input(121, 1);
                            }else if (msgCpy.equals("key:media_play_pause")) {
                                Log.e(TAG, "key:9");
                                //sendKey(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                                t.vkey_input(119, 1);
                                //投影歌曲部分
                            } else if(msgCpy.contains("system_vol")){
                                AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
                                sendbackSmallMessage(targetIp,msgCpy,String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + "/" + audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)));
                            } else if (msgCpy.contains("music_play")) {
                                Log.e(TAG, msgCpy);
                                Intent intent = new Intent(TVSocketControllerService.this, MusicViewPlayingActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.setData(Uri.parse(msgCpy));
                                startActivity(intent);
                            }else if (msgCpy.contains(MusicViewPlayingActivity.CMD_TAG)) {
                            	
                            	if (MusicViewPlayingActivity.mEventHandler != null) {
                                    Log.e(TAG, msgCpy);
                                    
                                    Message message = new Message();
                                    message.what = 0;
                                    message.obj = msgCpy;
                                    MusicViewPlayingActivity.mEventHandler.sendMessage(message);
                                }
                                //投影视屏部分
                            } else if (msgCpy.substring(0, 4).equals("http")) {
                                Log.e(TAG, msgCpy);
                                Intent intent = new Intent(TVSocketControllerService.this, VideoViewPlayingActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.setData(Uri.parse(msgCpy));
                                startActivity(intent);
                            } else if (msgCpy.equals("vedio:start")) {
                                if (VideoViewPlayingActivity.mEventHandler != null) {
                                    Log.e(TAG, msgCpy);
                                    VideoViewPlayingActivity.mEventHandler.sendEmptyMessage(1);
                                }
                            } else if (msgCpy.equals("vedio:stop")) {
                                if (VideoViewPlayingActivity.mEventHandler != null) {
                                    Log.e(TAG, msgCpy);
                                    VideoViewPlayingActivity.mEventHandler.sendEmptyMessage(2);
                                }
                            } else if (msgCpy.startsWith("vedio:seekto:")) {
                                if (VideoViewPlayingActivity.mEventHandler != null) {
                                    Log.e(TAG, msgCpy);
                                    Message message = new Message();
                                    message.what = 3;
                                    message.obj = msgCpy;
                                    VideoViewPlayingActivity.mEventHandler.sendMessage(message);
                                }
                                //投影图片部分
                            } else if (msgCpy.contains("urls")) {
                                Log.e(TAG, msgCpy);
                                handleTouYingPicMsg(msgCpy);
                            } else if (msgCpy.equals("rotation:left")) {
                                if (ImageShowPlayingActivity.handler != null) {
                                    Log.e(TAG, msgCpy);
                                    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                                    ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
                                    String shortClassName = info.topActivity.getClassName();    //类名
                                    if ("com.changhong.tvserver.touying.image.ImageShowPlayingActivity".equals(shortClassName)) {
                                        ImageShowPlayingActivity.handler.sendEmptyMessage(2);
                                    }
                                }
                            } else if (msgCpy.equals("rotation:right")) {
                                if (ImageShowPlayingActivity.handler != null) {
                                    Log.e(TAG, msgCpy);
                                    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                                    ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
                                    String shortClassName = info.topActivity.getClassName();    //类名
                                    Log.e(TAG, "rotation:" + shortClassName);
                                    if ("com.changhong.tvserver.touying.image.ImageShowPlayingActivity".equals(shortClassName)) {
                                        ImageShowPlayingActivity.handler.sendEmptyMessage(3);
                                    }
                                }
                            } else if (msgCpy.startsWith("room_pointer_down:")) {
                                if (ImageShowPlayingActivity.handler != null) {
                                    Log.e(TAG, "Location:" + msgCpy);
                                    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                                    ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
                                    String shortClassName = info.topActivity.getClassName();    //类名
                                    if ("com.changhong.tvserver.touying.image.ImageShowPlayingActivity".equals(shortClassName)) {
                                        Message message = new Message();
                                        message.what = 4;
                                        message.obj = msgCpy;
                                        ImageShowPlayingActivity.handler.sendMessage(message);
                                    }
                                }
                            } else if (msgCpy.startsWith("room_action_move:")) {
                                if (ImageShowPlayingActivity.handler != null) {
                                    Log.e(TAG, "Location:" + msgCpy);
                                    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                                    ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
                                    String shortClassName = info.topActivity.getClassName();    //类名
                                    if ("com.changhong.tvserver.touying.image.ImageShowPlayingActivity".equals(shortClassName)) {
                                        Message message = new Message();
                                        message.what = 5;
                                        message.obj = msgCpy;
                                        ImageShowPlayingActivity.handler.sendMessage(message);
                                    }
                                }
                            } else if (msgCpy.startsWith("room_action_up:")) {
                                if (ImageShowPlayingActivity.handler != null) {
                                    Log.e(TAG, "Location:" + msgCpy);
                                    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                                    ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
                                    String shortClassName = info.topActivity.getClassName();    //类名
                                    if ("com.changhong.tvserver.touying.image.ImageShowPlayingActivity".equals(shortClassName)) {
                                        ImageShowPlayingActivity.handler.sendEmptyMessage(6);
                                    }
                                }
                            } else if (msgCpy.startsWith("room_pointer_up:")) {
                                if (ImageShowPlayingActivity.handler != null) {
                                    Log.e(TAG, "Location:" + msgCpy);
                                    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                                    ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
                                    String shortClassName = info.topActivity.getClassName();    //类名
                                    if ("com.changhong.tvserver.touying.image.ImageShowPlayingActivity".equals(shortClassName)) {
                                        ImageShowPlayingActivity.handler.sendEmptyMessage(7);
                                    }
                                }
                            } else if(msgCpy.equals("key:dtv")){
                                try {
                                    Intent mIntent = getPackageManager().getLaunchIntentForPackage("Com.smarttv_doggle_newui");
                                    mIntent.putExtra("forceresume", true);
                                    startActivity(mIntent);
                                } catch (Exception e) {
                                    Log.i(TAG, "startActivity Com.smarttv_doggle_newui  err ! ");
                                }

                                Intent intent=new Intent("com.action.startDTV");
                                sendBroadcast(intent);
                                Log.i(TAG, "startActivity send com.action.startDTV broadcast");
                            }
                            else if(msgCpy.contains(MallListActivity.TAG.toLowerCase() + ":"))
                            {
                            	Intent intent = new Intent(TVSocketControllerService.this, MallListActivity.class);
                                intent.setData(Uri.parse(msgCpy));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);                                
                            }
                            else if(msgCpy.startsWith("app_open:")){
                                Log.e(TAG, "Location:" + msgCpy);
                                openYuYingApplication(msgCpy);
                            } else if (msgCpy.equals("finish")) {
                                Intent intent = new Intent("FinishActivity");
                                sendBroadcast(intent);
                            }else if(msgCpy.startsWith("other_open")){
                            	Log.e(TAG, "ppt_open" + msgCpy);                            	
								Intent intent = new Intent(TVSocketControllerService.this, OtherShowService.class);
                                intent.setData(Uri.parse(msgCpy));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startService(intent);
                            }
                            else if (msgCpy.contains(CaVerifyUtil.TAG)) {
                            	sendbackSmallMessage(targetIp,msgCpy,new CaVerifyUtil());
							}
                            break;
                        case 2:
//                            Toast.makeText(TVSocketControllerService.this, "������߳�����ͻ����޷����룡����", 3000).show();
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                super.handleMessage(msg);
            }
        };

        new send_heart_thread().start();
        new get_command().start();
        new get_command_new().start();
    }

    /*************************************************send heart part **************************************************/

    /**
     * 服务端发送客户端心跳
     * <p>
     * DatagramSocket:一开始就创建好
     * DatagramPacket:接收一个创建一个, 这样免得发生阻塞
     */
    private class send_heart_thread extends Thread {
        public void run() {
            DatagramSocket dgSocket = null;
            try {
                
                DatagramPacket dgPacket = null;

                while (true) {                    
                	List<String> ipList = NetworkUtils.getLocalIpAddresses();
                    for (String ip : ipList) {
                        try {

                        if (StringUtils.hasLength(ip) && !ip.equals("0.0.0.0")) {
                        	dgSocket = new DatagramSocket(9001, InetAddress.getByName(ip));
                            serverInfo = MainActivity.getInstance().CH_BOX_NAME;

                            /**
                             * 添加服务端网络信息到心跳
                             */
                            serverInfo = serverInfo + "|" + MyApplication.networkStatus.name();

                            if (STOP_PLAY_TAG > 0) {
                                /**
                                 * video or music stop play tag
                                 */
                                serverInfo = serverInfo + "|play_stop|" + STOP_PLAY_TAG;
                                STOP_PLAY_TAG = 0;

                            } else {
                                /**
                                 * 发送当前视频播放的操作信息, 错误之后并不影响心跳机制
                                 */
                                try {
                                    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                                    ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
                                    String shortClassName = info.topActivity.getClassName();    //类名
                                    if ("com.changhong.tvserver.touying.video.VideoViewPlayingActivity".equals(shortClassName)) {
                                        serverInfo = serverInfo + "|vedio_play|" +
                                                VideoViewPlayingActivity.playVeidoKey + "|" + VideoViewPlayingActivity.mVV.getCurrentPosition() + "|" +
                                                VideoViewPlayingActivity.mVV.isPlaying();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                /**
                                 * 发送当前音乐播放的操作信息, 错误之后并不影响心跳机制
                                 */
                                try {
                                    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                                    ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
                                    String shortClassName = info.topActivity.getClassName();    //类名
                                    if ("com.changhong.tvserver.touying.music.MusicViewPlayingActivity".equals(shortClassName)) {
                                        serverInfo = serverInfo + "|music_play|" +
                                                MusicViewPlayingActivity.playVeidoKey + "|" + MusicViewPlayingActivity.mVV.getCurrentPosition() + "|" +
                                                MusicViewPlayingActivity.mVV.isPlaying();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            /**
                             * 发送心跳
                             */
//                            Log.i(TAG, ">>>" + serverInfo);
                            byte[] b = serverInfo.getBytes();
                            dgPacket = new DatagramPacket(b, b.length, InetAddress.getByName("255.255.255.255"), 9001);
                            dgSocket.send(dgPacket);
                            dgSocket.close();
                        } else {
                            Log.e(TAG, "ip>>>not get the ip");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                    	if (dgSocket != null
                    			&& !dgSocket.isClosed()) {
                    		dgSocket.close();
						}
                        dgPacket = null;
                    }
                    }

                    /**
                     *
                     */
                    SystemClock.sleep(TIME);
                }
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

    /*************************************************get command part *************************************************/

    /**
     * 服务端接收客户端发来的socket
     * <p>
     * DatagramSocket:一开始就创建好
     * DatagramPacket:接收一个创建一个, 这样免得发生阻塞
     */
    private class get_command extends Thread {
        public void run() {
            DatagramSocket dgSocket = null;

            try {
                dgSocket = new DatagramSocket(9002);
                DatagramPacket dgPacket = null;

                while (true) {
                    try {
                    	byte[] by = new byte[1024];
                        dgPacket =  new DatagramPacket(by, by.length);
                        dgSocket.receive(dgPacket);
                        targetIp = dgPacket.getAddress().getHostAddress();
                        String command = new String(by, 0, dgPacket.getLength());
                        if (!command.equals("")) {
                            Log.w(TAG, command);
                            msg1 = command;
                            handler.sendMessage(handler.obtainMessage(1, msg1));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        dgPacket = null;
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
     * 服务端接收客户端发来的socket
     * <p>
     * DatagramSocket:一开始就创建好
     * DatagramPacket:接收一个创建一个, 这样免得发生阻塞
     */
    private class get_command_new extends Thread {
        public void run() {
            DatagramSocket dgSocket = null;

            try {
                dgSocket = new DatagramSocket(10013);
                DatagramPacket dgPacket = null;

                
                String command = null;
                while (true) {
                    try {
                    	byte[] by = new byte[1024];
                        dgPacket =  new DatagramPacket(by, by.length);
                        dgSocket.receive(dgPacket);
                        if (command == null) {
                        	command = new String(dgPacket.getData(),0,dgPacket.getData().length).trim();
						}
                        else {
                        	command += new String(dgPacket.getData(),0,dgPacket.getData().length).trim();
						}
                        
                        if (command.contains("^")) {
                        	int bindex = 0,eindex = 0;
                        	
                        	 while ((eindex = command.indexOf('^', bindex)) != -1) {
                        		 Log.w(TAG, command);
                                 msg1 = command.substring(bindex, eindex);
                                 handler.sendMessage(handler.obtainMessage(1, msg1));
                                 
                                 if (command.length() == (eindex + 1)) {
                                	 command = null;
                                	 break;
                                 }
                                 else {
                                	 bindex = eindex + 1;
                                 }
                                 
     						}   
                        	 //处理完连续的信息，其余的数据，全部抛弃
                        	 command = null;
						}

                    } catch (IOException e) {
                        e.printStackTrace();
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

    /********************************************handle client message part*******************************************/

    private void handleTouYingPicMsg(String JsonMsg) {
        List<String> Pics = new ArrayList<String>();
        Pics.clear();
        String clientIP = "";

        if (JsonMsg != null && !JsonMsg.equals("")) {
            JsonReader reader = new JsonReader(new StringReader(JsonMsg));
            try {
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    Log.e(TAG, "nextname:" + name);
                    if (name.equals("urls")) {
                        reader.beginArray();
                        while (reader.hasNext()) {
                            String picAddress = reader.nextString();
                            Log.i(TAG, "nextaddress:" + picAddress);
                            Pics.add(picAddress);
                        }
                        reader.endArray();
                    } else if (name.equals("client_ip")) {
                        clientIP = reader.nextString();
                        Log.i(TAG, "clientaddress:" + clientIP);
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
                reader.close();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "error load json message");
        }

        if (!Pics.isEmpty()) {
            String[] urls = new String[Pics.size()];
            for (int i = 0; i < Pics.size(); i++) {
                urls[i] = Pics.get(i);
            }

            try {
                ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
                String shortClassName = info.topActivity.getClassName();    //类名
                if ("com.changhong.tvserver.touying.image.ImageShowPlayingActivity".equals(shortClassName)) {
                    Message msg = new Message();
                    msg.what = 100;
                    msg.obj = urls;
                    ImageShowPlayingActivity.handler.sendMessage(msg);
                } else {
                    Intent intent = new Intent(TVSocketControllerService.this, ImageShowPlayingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(ImageShowPlayingActivity.EXTRA_IMAGE_URLS, urls);
                    intent.putExtra(ImageShowPlayingActivity.CLIENT_IP, clientIP);
                    intent.putExtra(ImageShowPlayingActivity.EXTRA_IMAGE_INDEX, 0);
                    startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "no picture url");
        }
    }

    /**
     * 记录上次打开的应用
     */
    private String lastLunchApp = "";

    private void openYuYingApplication(String msg) {

        try {
            /**
             * open app
             */
            String[] tokens = StringUtils.delimitedListToStringArray(msg, ":");
            String packageName = tokens[1];
            if (StringUtils.hasLength(packageName)) {
                PackageManager packageManager = getPackageManager();
                Intent intent = new Intent();
                intent = packageManager.getLaunchIntentForPackage(packageName);
                if (intent != null) {
                    startActivity(intent);
                }
            }
            /**
             * close last open app, even last lunch app close by use, it's OK
            if (StringUtils.hasLength(lastLunchApp)) {
                ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                Method forceStopPackage = activityManager.getClass().getDeclaredMethod("forceStopPackage", String.class);
                forceStopPackage.setAccessible(true);
                forceStopPackage.invoke(activityManager, lastLunchApp);
            }
             */
            lastLunchApp = packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void sendbackSmallMessage(final String ip,String object){
        sendbackSmallMessage(ip,null,object);
    }
    public static void sendbackSmallMessage(final String ip,final String param,final Object object)
    {
    	final int NEW_BACK_PORT = 10014;
    	
    	AsyncTask.execute(new Runnable() {			
			@Override
			public void run() {
				if (object != null && param.indexOf(":") != -1) {
                    String resultString = null;
                    if (object instanceof QuickSendBackClass){
                        resultString = ((QuickSendBackClass)object).update(param);
                    }else if (object instanceof String){
                        resultString = (String)object;
                    }
                    if (resultString == null
                            || resultString.length() <= 0) {
                        return ;
                    }
                    resultString = param.substring(0, param.indexOf(":")) + ":" + resultString;

					byte message[] = resultString.getBytes();
					DatagramSocket socket = null;
					try {
						socket = new DatagramSocket();						
						socket.send(new DatagramPacket(message, message.length, InetAddress.getByName(ip), NEW_BACK_PORT));
						socket.close();
					} catch (SocketException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}finally
					{
						if(socket != null)
						{
							socket.close();
						}
					}
					
					
				}
			}
		});
    }
    
    public interface QuickSendBackClass
    {
    	public String update(String param); 
    }

    private void sendKey(final int keycode){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                instrumentation.sendKeyDownUpSync(keycode);
            }
        });

    }
}
