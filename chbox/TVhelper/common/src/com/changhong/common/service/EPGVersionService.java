package com.changhong.common.service;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jack Wang
 */
public class EPGVersionService {

    private static final String TAG = "EPGVersionService";

    private static final String IP_SAVE_NAME = "IP";
    private static final String EPG_SAVE_NAME = "EPG_VERSION";

    private Context context;

    public EPGVersionService(Context context) {
        this.context = context;
    }

    public void saveEPGVersion(String ip,int version) {
        if (ip == null)
            return;

        SharedPreferences preferences = context.getSharedPreferences("changhong_epg", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(ip,version);
        editor.commit();
    }

    public int getEPGVersion(String ip) {
        if (ip == null)
            return 0x7FFFFFFF;

        SharedPreferences preferences = context.getSharedPreferences("changhong_epg", Context.MODE_PRIVATE);
        return preferences.getInt(ip, -1);
    }

}
