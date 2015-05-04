package com.changhong.setting.domain;

import android.content.Context;
import com.changhong.common.domain.NetworkStatus;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.setting.utils.NetEstimateUtils;

/**
 * Ma Ren
 */
public class NetworkItem implements ScoreItem {

    /**
     * tag for this item
     */
    private final static String TAG = "NET";

    private NetworkStatus serverNetworkStatus;

    private NetworkStatus mobilelNetworkStatus;

    private int rate = 0;

    public NetworkItem(Context context, int rate) {
        this.rate = rate;
        mobilelNetworkStatus = NetworkUtils.getMobileNetworkStatus(context);
        serverNetworkStatus = NetEstimateUtils.serverNetworkStatus;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public int getCurrentItemScore() {
        if (serverNetworkStatus.equals(NetworkStatus.NET_WIRED) && mobilelNetworkStatus.equals(NetworkStatus.NET_WIRELESS_5G)) {
            return 90 * rate;
        } else if(serverNetworkStatus.equals(NetworkStatus.NET_WIRED) && mobilelNetworkStatus.equals(NetworkStatus.NET_WIRELESS_24G)) {
            return 80 * rate;
        } else if(serverNetworkStatus.equals(NetworkStatus.NET_WIRELESS_24G) && mobilelNetworkStatus.equals(NetworkStatus.NET_WIRELESS_5G)) {
            return 70 * rate;
        } else if(serverNetworkStatus.equals(NetworkStatus.NET_WIRELESS_24G) && mobilelNetworkStatus.equals(NetworkStatus.NET_WIRELESS_24G)) {
            return 50 * rate;
        } else {
            //没有网络
            return 0;
        }
    }

    @Override
    public String getCurrentSuggestion() {
        if (serverNetworkStatus.equals(NetworkStatus.NET_WIRED) && mobilelNetworkStatus.equals(NetworkStatus.NET_WIRELESS_5G)) {
            return "网络运行环境非常好";
        } else if(serverNetworkStatus.equals(NetworkStatus.NET_WIRED) && mobilelNetworkStatus.equals(NetworkStatus.NET_WIRELESS_24G)) {
            return "可以选择更好的路由器";
        } else if(serverNetworkStatus.equals(NetworkStatus.NET_WIRELESS_24G) && mobilelNetworkStatus.equals(NetworkStatus.NET_WIRELESS_5G)) {
            return "请用有线连接机顶盒";
        } else if(serverNetworkStatus.equals(NetworkStatus.NET_WIRELESS_24G) && mobilelNetworkStatus.equals(NetworkStatus.NET_WIRELESS_24G)) {
            return "请用有线连接机顶盒和更好的路由器";
        } else {
            return "手机或机顶盒没有连网";
        }
    }
}
