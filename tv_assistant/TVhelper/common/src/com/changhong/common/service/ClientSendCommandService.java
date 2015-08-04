package com.changhong.common.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;
import com.changhong.common.domain.AppInfo;
import com.changhong.common.utils.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Created by Jack Wang
 */
public class ClientSendCommandService extends Service implements ClientSocketInterface {

    /**
     * message handler
     */
    public static Handler handler = null;

    /**
     * server ip list
     */
    public static ArrayList<String> serverIpList = new ArrayList<String>();
    public static Map<String, String> serverIpListMap = new HashMap<String, String>();

    /********************************************channel part********************************************************/

    public static boolean searchChannelFinished = false;
    public static List<Map<String, Object>> channelData = new ArrayList<Map<String, Object>>();
    /**
     * 同频点频道信息
     */
    public static List<Map<String, Object>> playingChannelData = new ArrayList<Map<String, Object>>();

    /************************************************app part********************************************************/

    public static boolean searchApplicationFinished = false;
    /**
     * 服务端应用列表
     */
    public static List<AppInfo> serverAppInfo = new ArrayList<AppInfo>();

    /**
     * box server ip address
     */
    public static String serverIP = null;

    public static String msg = null;

    public static String msgSwitchChannel = null;

    public static String msgXpointYpoint = null;

    public static String titletxt = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        new SendCommend().start();
    }

    public static String getCurrentConnectBoxName() {
        if (!StringUtils.hasLength(ClientSendCommandService.serverIP)) {
            return "未连接";
        }
        String boxName = ClientSendCommandService.serverIpListMap.get(ClientSendCommandService.serverIP);
        if (StringUtils.hasLength(boxName)) {
            return boxName;
        }
        return ClientSendCommandService.serverIP;
    }

    public static String getConnectBoxName(String serverIP) {
        String boxName = ClientSendCommandService.serverIpListMap.get(serverIP);
        if (StringUtils.hasLength(boxName)) {
            return boxName;
        }
        return NetworkUtils.BOX_DEFAULT_NAME;
    }

    private class SendCommend extends Thread {
        public void run() {
            Looper.prepare();

            handler = new Handler() {
                @Override
                public void handleMessage(Message msg1) {
                    switch (msg1.what) {
                        case 1:
                            //TODO:这个消息只能用于遥控器消息发送，注意其他部分不要使用该消息
                            MobilePerformanceUtils.sharingRemoteControlling = true;
                            MobilePerformanceUtils.sharingRemoteControlLastHappen = System.currentTimeMillis();
                            MobilePerformanceUtils.openPerformance(ClientSendCommandService.this);

                            if (serverIP != null && msg != null) {
                                DatagramSocket dgSocket = null;
                                try {
                                    dgSocket = new DatagramSocket();
                                    byte b[] = msg.getBytes();

                                    DatagramPacket dgPacket = new DatagramPacket(b, b.length, InetAddress.getByName(serverIP), KEY_PORT);
                                    dgSocket.send(dgPacket);
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
                            } else {
                                Log.e(TAG, "未获取到服务器IP");
                            }
                            break;
                        case 2:
                            /**
                             * TODO:comment by Jack Wang:
                             * why I will change this part of the code, because for module dependency, just allow main module use common not allowed command
                             * module use main module, so....
                             * <p>
                             *
                             * the old code here will execute mHandler.sendEmptyMessage(1); that means tell TVPlayerActivity refresh the tv channel datas
                             * <p>
                             *
                             * I check all places which will execute this part of code, except one place, all other happens at change the server ip, so
                             * 1 - if this activity is not TVPlayerActivity, it's OK, because when TVPlayerActivity.onstart() will refresh all
                             *     channel again
                             * 2 - if current activity is TVPlayerActivity, when change teh server ip, after search channel finished, refresh again, so
                             *     the parameter "searchChannelFinished" is used for this
                             */
                            try {
                                searchChannelFinished = false;
                                getChannelList("http://" + serverIP + ":8000/DtvProgInfoJson.json");
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                searchChannelFinished = true;
                            }

                            /**
                             * when user reselected the ip, reload all app info
                             */
                            this.sendEmptyMessage(6);
                            break;
                        case 3:
                            //换台UDP广播
                            if (serverIP != null && msgSwitchChannel != null) {
                                DatagramSocket dgSocket = null;
                                try {
                                    dgSocket = new DatagramSocket();
                                    byte b[] = msgSwitchChannel.getBytes();

                                    DatagramPacket dgPacket = new DatagramPacket(b, b.length, InetAddress.getByName(serverIP), SWITCH_KEY_PORT);
                                    dgSocket.send(dgPacket);
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
                            } else {
                                Log.e(TAG, "未获取到服务器IP");
                            }
                            break;
                        case 4:
                            DatagramSocket dgSocket = null;
                            try {
                                dgSocket = new DatagramSocket();
                                byte b[] = msg.getBytes();
                                DatagramPacket dgPacket = new DatagramPacket(b, b.length, InetAddress.getByName(serverIP), KEY_PORT);
                                dgSocket.send(dgPacket);
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
                            break;
                        case 5:
                            //坐标UDP广播
                            if (serverIP != null && msgXpointYpoint != null) {
                                DatagramSocket xydgSocket = null;
                                try {
                                    xydgSocket = new DatagramSocket();
                                    byte b[] = msgXpointYpoint.getBytes();
                                    DatagramPacket dgPacket = new DatagramPacket(b, b.length, InetAddress.getByName(serverIP), 9008);
                                    xydgSocket.send(dgPacket);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if (xydgSocket != null) {
                                            xydgSocket.close();
                                        }
                                    } catch (Exception e) {
                                        xydgSocket.close();
                                    }
                                }
                            } else {
                                Log.e(TAG, "未获取到服务器IP");
                            }
                            break;
                        case 6:
                            /**
                             * get server all applications
                             */
                            try {
                                searchApplicationFinished = false;
                                getProgramList("http://" + serverIP + ":8000/OttAppInfoJson.json");
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                searchApplicationFinished = true;
                            }
                            break;
                       
                        case 7:
                        	/**
                        	 * new socket to send message
                        	 */
                        	DatagramSocket ndgSocket = null;
                            try {
                            	ndgSocket = new DatagramSocket();
                            	byte b[] = new byte[1024], targetBuf[] = (((String)msg1.obj)+ DEVIDE_MEG).getBytes();

                            	int index = 0;
                            	DatagramPacket dgPacket = new DatagramPacket(b, b.length, InetAddress.getByName(serverIP), NEW_KEY_PORT);                            	
                            	
                            	while ((index + b.length) < targetBuf.length) {
                                    dgPacket.setData(targetBuf, index, b.length);
                                    ndgSocket.send(dgPacket);
                                    index += b.length;
                                }
                                dgPacket.setData(targetBuf,index,targetBuf.length -index);
                            	dgPacket.setLength(targetBuf.length -index);
                            	ndgSocket.send(dgPacket);                                                         
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (ndgSocket != null) {
                                    	ndgSocket.close();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        default: {
                            break;
                        }
                    }
                    super.handleMessage(msg1);
                }
            };

            Looper.loop();
        }
    }

    @SuppressLint("NewApi")
    private void getChannelList(String url) {
        if (url == null) {
            return;
        }
        channelData.clear();
        playingChannelData.clear();
        String sss = null;
        URL url_address = null;
        try {
            url_address = new URL(url);
            HttpURLConnection hurlconn = (HttpURLConnection) url_address.openConnection();
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
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (sss != null && !sss.equals("")) {
            JsonReader reader = new JsonReader(new StringReader(sss));
            try {
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    Log.i(TAG, "nextname:" + name);
                    if (name.equals("JSON_PROGINFO")) {
                        reader.beginArray();
                        while (reader.hasNext()) {
                            Map<String, Object> map = null;
                            reader.beginObject();
                            while (reader.hasNext()) {
                                String namesub = reader.nextName();
                                if (namesub.equals("service_id")) {
                                    String value = reader.nextString();
                                    map = new HashMap<String, Object>();
                                    map.put("service_id", value);
                                } else if (namesub.equals("vEcmPid")) {
                                	reader.nextString();
                                } else if (namesub.equals("video_pid")) {
                                    String value = reader.nextString();
                                    map.put("vPid", value);
                                } else if (namesub.equals("pmtPid")) {
                                    String value = reader.nextString();
                                    map.put("pmtPid", value);
                                } else if (namesub.equals("tuner_id")) {
                                	reader.nextString();
                                } else if (namesub.equals("qam")) {
                                	reader.nextString();
                                } else if (namesub.equals("aEcmPid")) {
                                	reader.nextString();
                                } else if (namesub.equals("service_name")) {
                                    String value = reader.nextString();
                                    if (!TextUtils.isEmpty(value)) {
                                    	value=value.trim();
									}
                                    map.put("service_name", value);
                                } else if (namesub.equals("sym")) {
                                	reader.nextString();
                                } else if (namesub.equals("demux_id")) {
                                    String value = reader.nextString();
                                    map.put("dmxId", value);
                                } else if (namesub.equals("audio_pid")) {
                                    String value = reader.nextString();
                                    map.put("aPid", value);
                                } else if (namesub.equals("logic_number")) {
                                    String value = reader.nextString();
                                    map.put("logic_number", value);
                                } else if (namesub.equals("sType")) {
                                	reader.nextString();
                                } else if (namesub.equals("tsId")) {
                                    String value = reader.nextString();
                                    map.put("tsId", value);
                                } else if (namesub.equals("orgNId")) {
                                    String value = reader.nextString();
                                    map.put("orgNId", value);
                                } else if (namesub.equals("pcr_pid")) {
                                    String value = reader.nextString();
                                    map.put("pcr_pid", value);
                                } else if (namesub.equals("freqKHz")) {
                                    String value = reader.nextString();
                                    map.put("freq", value);
                                } else if (namesub.equals("channel_index")) {
                                    String value = reader.nextString();
                                    map.put("channel_index", value);
                                } else if (namesub.equals("aStreamType")) {
                                    String value = reader.nextString();
                                    map.put("aStreamType", value);
                                } else if (namesub.equals("vStreamType")) {
                                    String value = reader.nextString();
                                    map.put("vStreamType", value);
                                } else {
                                    reader.skipValue();
                                }
                            }
                            reader.endObject();
                            if (map != null) {
                                channelData.add(map);
                                map = null;
                            }
                        }
                        reader.endArray();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "未获取到服务器channel Json");
        }
    }

    @SuppressLint("NewApi")
    private void getProgramList(String url) {
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

        //parse the json data
        serverAppInfo.clear();
        try {
            if (StringUtils.hasLength(sss)) {
                JSONArray all = new JSONArray(sss);
                for (int i = 0; i < all.length(); i++) {
                    JSONObject single = all.getJSONObject(i);
                    String packageName = single.getString("packageName");
                    String applicationName = single.getString("applicationName");

                    AppInfo app = new AppInfo(packageName, applicationName);
                    serverAppInfo.add(app);
                }
            } else {
                Log.e(TAG, "未获取到服务器program Json");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(String message) {
        ClientSendCommandService.msg = message;
        if (ClientSendCommandService.handler != null) {
            ClientSendCommandService.handler.sendEmptyMessage(4);
        }
    }

    public static void sendMessageNew(String message) {
        if (ClientSendCommandService.handler != null) {
            ClientSendCommandService.handler.sendMessage(handler.obtainMessage(7, new String(message)));
        }
    }
}
