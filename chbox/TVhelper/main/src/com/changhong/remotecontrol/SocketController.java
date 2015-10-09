package com.changhong.remotecontrol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.service.ClientSocketInterface;
import com.changhong.common.widgets.IpSelectorDataServer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import android.util.Log;

public abstract class SocketController implements ClientSocketInterface {

    protected static final String TAG = "TVHelperControlService";

    protected boolean mIsExit = false;
    protected Context mContext = null;
    protected Handler mHandle = null;
    protected RemoteInfoContainer mRemoteInfo = null;
    public static boolean mIsBroadCast = false;

    private ThreadHeartBeatGet mThreadHeartBeatGet = null;

    SocketController(Context context, Handler handle) {
        mContext = context;
        mHandle = handle;

        mRemoteInfo = new RemoteInfoContainer();
        mRemoteInfo.setIp(IpSelectorDataServer.getInstance().getCurrentIp());
        mThreadHeartBeatGet = new ThreadHeartBeatGet();
        mThreadHeartBeatGet.start();

        new ThreadLinkChecked().start();
    }

    // Static Function:
    public static void setIsBroadCastMsg(boolean isBroadCast) {
        mIsBroadCast = isBroadCast;
    }

    protected void clear() {
        mIsExit = true;

        if (mThreadHeartBeatGet != null) {
            try {
                mThreadHeartBeatGet.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (mRemoteInfo != null) {
            mRemoteInfo.exit();
        }
        mRemoteInfo = null;


    }

    protected void sendContent(String data) {
        if (mIsBroadCast) {
            mRemoteInfo.setBroadcastPackage(data);
        } else {
            mRemoteInfo.setSinglePackage(data);
        }
    }

    // Threads:
    class ThreadHeartBeatGet extends Thread {
        DatagramSocket clientSocket = null;

        @Override
        public void run() {

            while (!mIsExit) {
                try {
                    clientSocket = new DatagramSocket(INPUT_IP_POST_PORT);
                    clientSocket.setReuseAddress(true);
                    byte[] receiveData = new byte[512];
                    String ip = null;

                    Log.d("RemoteSocketServer", "ThreadHeartBeatGet in");
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    while (true) {

                        try {
                            /**
                             * 接收数据部分
                             */
                            if (mIsExit) {
                                break;
                            }

                            clientSocket.receive(receivePacket);
                            ip = receivePacket.getAddress().getHostAddress();
                            if (mRemoteInfo != null)
                                mRemoteInfo.addIp(ip);
                            Log.d(TAG, "getIP:" + ip);

                            byte[] ipBytes = mRemoteInfo.getIp() == null ? "null".getBytes("ISO-8859-1") : mRemoteInfo.getIp().getBytes("ISO-8859-1");//ip.getBytes();
                            try {
                                clientSocket.send(new DatagramPacket(ipBytes, ipBytes.length, receivePacket.getAddress(), INPUT_IP_GET_PORT));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }


                            /**
                             * 线程停止3000
                             */
                            //Thread.sleep(3000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (clientSocket != null) {
                        try {
                            clientSocket.close();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }

                    }
                }
            }
            Log.d("RemoteSocketServer", "ThreadHeartBeatGet out");
        }

        public void close() {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }

            }
        }

    }

    class ThreadLinkChecked extends Thread {
        public void run() {
            while (!mIsExit) {
                try {
                    mRemoteInfo.update();

                    sleep(ClientSocketInterface.RELAX_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    protected abstract void onIpObtained(String ip);

    protected abstract void onIpRemoved(String ip);

    class RemoteInfoContainer implements ClientSocketInterface,Observer {

        public static final int WAITING_TIME = 3000;
        private Map<String, Long> mServerIP = new HashMap<String, Long>();
        private Queue<DatagramPacket> mDataPackageList = new LinkedList<DatagramPacket>();
        private String mServerIpCur = null;

        RemoteInfoContainer()
        {
        	IpSelectorDataServer.getInstance().addObserver(this);        	        	
        }
        
        public void exit() {
            mServerIpCur = null;
            mServerIP.clear();
            mDataPackageList.clear();
            IpSelectorDataServer.getInstance().deleteObserver(this);
        }

        public final String getIp() {
            return mServerIpCur;
        }

        public final List<String> getIpList() {
            return new ArrayList<String>(mServerIP.keySet());
        }

        public void addIp(String serverIP) {
            Log.d(TAG, "addip in");
            String ip = ipCheck(serverIP) ? serverIP : null;

            if (ip == null)
                return;
            if (mServerIP.isEmpty()) {
                mServerIpCur = IpSelectorDataServer.getInstance().getCurrentIp();
            }

            //refresh map for date changed or get a new ip
            mServerIP.put(ip, System.currentTimeMillis());
            onIpObtained(ip);                                    
            Log.d(TAG, "Add ip" + mServerIpCur);

        }

        public void update() {
            Long timeCur = System.currentTimeMillis();

            Iterator<Entry<String, Long>> itEntry = mServerIP.entrySet().iterator();
            for (; itEntry.hasNext(); ) {
                Entry<String, Long> entry = itEntry.next();
                if (timeCur - entry.getValue() > (WAITING_TIME * 3)) {
                    Log.d(TAG, "remove ip(timeCur:" + timeCur + "timeSaved:" + entry.getValue() + ")");
                    removeIp(entry);
                    itEntry.remove();

                }
            }

        }

        public void setIp(String serverIP) {
            final String ip = ipCheck(serverIP) ? serverIP : null;
            if (ip == null)
                return;
            
            if (ip.equalsIgnoreCase(mServerIpCur)) {
				return ;
			}
            final String ipCurrent = mServerIpCur;
                        
            
            AsyncTask.execute(new Runnable() {				
				@Override
				public void run() {
					if (ipCurrent != null) {  
		            	DatagramSocket dgSocket = null;
		                try {
		                	byte[] ipBytes = ip.getBytes("ISO-8859-1");//ip.getBytes();
		                	dgSocket = new DatagramSocket();
		                	dgSocket.send(new DatagramPacket(ipBytes, ipBytes.length, InetAddress.getByName(ipCurrent), INPUT_IP_GET_PORT));
		                	dgSocket.send(new DatagramPacket(ipBytes, ipBytes.length, InetAddress.getByName(ip), INPUT_IP_GET_PORT));
		                	Log.d("RemoteIME", "Change binder ip:" + ipCurrent + "to ip:" + ip);
		                } catch (NumberFormatException e) {
		                    e.printStackTrace();
		                } catch (SocketException e) {
							e.printStackTrace();
						} catch (UnknownHostException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}   
		                finally{
		                	if (dgSocket != null) {
		                		dgSocket.close();
							}
		                }
					}
				}
			});  

            // add ip or update time with ip
            {
                Log.d(TAG, "come to add ip");
                addIp(ip);
                mServerIpCur = ip;                
                if (serverIP != null
                        && serverIP.length() > 0) {
                    mServerIpCur = serverIP;
                }
            }
            
                    
        }

        public void removeIp(String ip) {
            if (ipCheck(ip)) {
                for (Entry<String, Long> entry : mServerIP.entrySet()) {
                    if (entry.getKey().equals(ip)) {
                        removeIp(entry);
                    }
                }


            }
        }

        private void removeIp(Entry<String, Long> entry) {
            if (entry == null) {
                return;
            }

            onIpRemoved(entry.getKey());
            Log.d(TAG, "remove ip" + entry.getKey());

            if (entry.getKey() == mServerIpCur) {
                if (!mServerIP.isEmpty()) {
                    mServerIpCur = mServerIP.keySet().toArray()[0].toString();
                } else {
                    mServerIpCur = null;
                }
            }
        }


        public void setBroadcastPackage(String content) {
            String cont = content;
            Map<String, Long> ipMap = mServerIP;

            for (String entry : ipMap.keySet()) {
                byte[] mbytes = cont.getBytes();
                try {
                    mDataPackageList.offer(new DatagramPacket(mbytes,
                            mbytes.length,
                            InetAddress.getByName(entry),
                            CONTENT_PORT));
                } catch (UnknownHostException e) {                    
                    e.printStackTrace();
                }
            }

        }

        public void setSinglePackage(String content) {
            if (mServerIpCur == null)
                return;

            byte[] mbytes = content.getBytes();
            try {
                mDataPackageList.offer(new DatagramPacket(mbytes,
                        mbytes.length,
                        InetAddress.getByName(mServerIpCur),
                        CONTENT_PORT));
            } catch (UnknownHostException e) {
                Log.d(TAG, "add Single Package failed");
                e.printStackTrace();
            }
        }

        public DatagramPacket getPackage() {
            return mDataPackageList.poll();
        }

        private boolean ipCheck(String ip) {
            // 255.255.255.255
            if (ip == null || ip.length() == 0)
                return false;

            if (ip.length() > 0 && ip.length() <= 15) {
                //b begin
                int b = 0, count = 0;
                while ((b = ip.indexOf(".", b) + 1) != 0) count++;
                return  (count == 3)? true : false;
            }
            return false;
        }

		@Override
		public void update(Observable observable, Object data) {
			this.setIp(IpSelectorDataServer.getInstance().getCurrentIp()); 			
		}

    }


}
