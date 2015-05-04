package com.changhong.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.changhong.common.domain.NetworkStatus;
import com.changhong.common.service.ClientSendCommandService;
import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Jack Wang
 */
public class NetworkUtils {

    private final static int NET_5G_FREQUENCY = 5120;

    public static String getLocalHostIp() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ip.getHostAddress())) {
                        ipaddress = ip.getHostAddress();
                        {
                            String[] server = ClientSendCommandService.serverIP.split("\\.");
                            String[] client = ipaddress.split("\\.");
                            if (server[0].equals(client[0]) == true && server[1].equals(client[1]) == true && server[2].equals(client[2]) == true) {
                                return ipaddress;
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("NetworkUtils", "获取本地ip地址失败");
            e.printStackTrace();
        }
        Log.i("NetworkUtils", "本机IP地址为" + ipaddress);
        return ipaddress;
    }

    public static boolean isConnectInternet(final Context pContext) {
        final ConnectivityManager conManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = conManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }

        return false;
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }

        return false;
    }

    public static NetworkStatus getMobileNetworkStatus(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo currentConnectWifi = wifiManager.getConnectionInfo();
        List<ScanResult> avaiableWifiList = wifiManager.getScanResults();

        if (currentConnectWifi != null && avaiableWifiList != null) {
            for (ScanResult scanResult : avaiableWifiList) {
                /**
                 * scanResult.BSSID.equals(currentConnectWifi.getBSSID()) is check which wifi is connection
                 */
                if (scanResult.BSSID.equals(currentConnectWifi.getBSSID())) {
                    if (scanResult.frequency < NET_5G_FREQUENCY) {
                        return NetworkStatus.NET_WIRELESS_24G;
                    } else {
                        return NetworkStatus.NET_WIRELESS_5G;
                    }
                }
            }
        }
        return NetworkStatus.NET_NULL;
    }
}
