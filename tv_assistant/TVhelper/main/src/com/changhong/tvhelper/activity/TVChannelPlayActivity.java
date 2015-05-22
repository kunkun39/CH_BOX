/*
 * Copyright (C) 2012 YIXIA.COM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.changhong.tvhelper.activity;

import android.view.*;
import android.widget.*;
import com.changhong.common.db.sqlite.DatabaseContainer;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.tvhelper.R;

import com.changhong.tvhelper.domain.Program;
import com.changhong.tvhelper.service.ChannelService;
import com.changhong.tvhelper.service.ClientGetCommandService;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.widget.VideoView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;

import java.util.*;

public class TVChannelPlayActivity extends Activity {

    /**
     * video play view
     */
    public static VideoView mVideoView;
    //    private MediaController controller;
    private int width, height;

    /**
     * video play source
     */
    public static String path = null;
    public static String name = null;
    public static int mm = 1;

    private String freq = "";
    private int HD_PLAYING_BUFFER = 1024 * 1024;
    private int SD_PLAYING_BUFFER = 256 * 1024;

    public GestureDetector mGestureDetector = null;

    /**
     * 播放和退出时间戳
     */
    private long playTimestamp = 0l;
    private long backTimestamp = 0l;

    private View mVolumeBrightnessLayout;
    private ImageView mOperationBg;
    private ImageView mOperationPercent;
    private AudioManager mAudioManager;
    /**
     * 最大声音
     */
    private int mMaxVolume;
    /**
     * 当前声音
     */
    private int mVolume = -1;
    /**
     * 当前亮度
     */
    private float mBrightness = -1f;

    /**
     * 频道列表
     */
    private ListView channelList;
    private ChannelAdapter channelAdapter;
    private List<String> channelNames;
    private boolean menuKey = false;
    private RelativeLayout relativeLayout;

    /**
     * 节目信息
     */
    List<Program> programList = new ArrayList<Program>();
    private RelativeLayout programInfoLayout;
    private TextView textCurrentProgramInfo;
    private TextView textNextProgramInfo;
    private TextView textChannelName;
    private String channelIndex;
    private ImageView imageViewChannelLogo;
    private final String ILLEGAL_PROGRAM_NAME = "无节目信息";

    /**
     * 再按返回确认退出标志爱
     */
    private int returnConfirm = 1;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
            return;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//ȥ��������

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//ȥ����Ϣ��

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        setContentView(R.layout.activity_channel_play);
        mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
        mOperationBg = (ImageView) findViewById(R.id.operation_bg);
        mOperationPercent = (ImageView) findViewById(R.id.operation_percent);

        //频道列表
        channelNames = new ArrayList<String>();
        initTVChannel();
        channelAdapter = new ChannelAdapter(this);
        relativeLayout = (RelativeLayout) findViewById(R.id.channel_list_layout);
        channelList = (ListView) findViewById(R.id.channel_list);
        channelList.setAdapter(channelAdapter);

        //节目信息
        textCurrentProgramInfo = (TextView) findViewById(R.id.text_current_program_info);
        textNextProgramInfo = (TextView) findViewById(R.id.text_next_program_info);
        textChannelName = (TextView) findViewById(R.id.text_channel_name);
        programInfoLayout = (RelativeLayout) findViewById(R.id.program_info_layout);
        imageViewChannelLogo = (ImageView) findViewById(R.id.play_channel_logo);


        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("com.action.switchchannel");
        registerReceiver(this.SwitchReceiver, intentfilter);
        Intent intent = getIntent();
        if (intent.getStringExtra("channelname") != null && !intent.getStringExtra("channelname").equals("") && !intent.getStringExtra("channelname").equals("null")) {
            name = intent.getStringExtra("channelname");
        }

        final ProgressDialog dd = new ProgressDialog(TVChannelPlayActivity.this);
        dd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dd.setMessage("正在拼命为您加载视频数据...");

        dd.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_BACK == keyCode) {
                    if (dd.isShowing()) {
                        dd.dismiss();
                    }
                    finish();
                }
                return false;
            }
        });
        dd.show();

        mVideoView = (VideoView) findViewById(R.id.surface_view);
        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_MEDIUM);
        mVideoView.setBufferSize(256 * 1024);
        mVideoView.setKeepScreenOn(true);
//        mVideoView.setHardwareDecoder(true);
        mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
        if (path != null) {
            mVideoView.setVideoPath(path);
            initProgramInfo(name);
        }

        mVideoView.setOnPreparedListener(new OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                try {
                    if (dd.isShowing()) {
                        dd.dismiss();
                    }
                    int w = mVideoView.getVideoWidth();
                    int h = mVideoView.getVideoHeight();
                    if (h > 576) {
                        mVideoView.setBufferSize(512 * 1024);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mVideoView.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
                try {
                    Log.e("ysharp", "" + arg1);
                    if (arg0.isBuffering()) {
                        if (!dd.isShowing()) {
                            dd.show();
                        }
                    }
                    if (arg1 >= 25) {
                        if (dd.isShowing()) {
                            dd.dismiss();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//        controller = new MediaController(this);

//        if (name != null) {
//            controller.setFileName(name);
//            controller.show();
//        }
//        mVideoView.setMediaController(controller);
        mVideoView.requestFocus();

        mGestureDetector = new GestureDetector(this, new MyGestureListener());
        playTimestamp = System.currentTimeMillis();
        new PlayerIsPlayingMinitorThread().start();
    }

    /**
     * 定时隐藏
     */
    private Handler mDismissHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mVolumeBrightnessLayout.setVisibility(View.GONE);
                    break;
                case 1:
                    Toast.makeText(TVChannelPlayActivity.this, "播放超时，退出播放！！！", 3000).show();
                    break;
                case 3:
                    if (programList != null && programList.size() > 0) {
                        try {
                            Program currentProgram = programList.get(0);
                            textCurrentProgramInfo.setText("当前节目" + ":" + currentProgram.getProgramName() + "  " + currentProgram.getProgramStartTime() + "-" + currentProgram.getProgramEndTime());
                        } catch (Exception e) {
                            textCurrentProgramInfo.setText("当前节目" + ":" + "无节目信息");
                        }
                        try {
                            Program nextProgram = programList.get(1);
                            textNextProgramInfo.setText("下一节目" + ":" + nextProgram.getProgramName() + "  " + nextProgram.getProgramStartTime() + "-" + nextProgram.getProgramEndTime());
                        } catch (Exception e) {
                            textNextProgramInfo.setText("下一节目" + ":" + "无节目信息");
                        }
                    } else {
                        textCurrentProgramInfo.setText("当前节目" + ":" + "无节目信息");
                        textNextProgramInfo.setText("下一节目" + ":" + "无节目信息");
                    }
                    break;
                case 4:
                    programInfoLayout.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    //根据频道名称获得节目信息
    private void initProgramInfo(String channelPlayName) {
        try {
            if (!ClientSendCommandService.channelData.isEmpty()) {

                for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
                    Map<String, Object> map = ClientSendCommandService.channelData.get(i);

                    if (channelPlayName.equals((String) map.get("service_name"))) {
                        //获得节目信息
                        name = (String) map.get("service_name");
                        channelIndex = (String) map.get("channel_index");
                        programInfoLayout.setVisibility(View.VISIBLE);
                        textChannelName.setText(channelPlayName);

                        //设置台标
                        try {
                            imageViewChannelLogo.setImageResource(ClientGetCommandService.channelLogoMapping.get(channelPlayName));
                        } catch (Exception e) {
                            imageViewChannelLogo.setImageResource(R.drawable.logotv);
                        }
                        break;
                    }
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * 初始化DB
                         */
                        if (MyApplication.databaseContainer == null) {
                            MyApplication.databaseContainer = new DatabaseContainer(TVChannelPlayActivity.this);
                        }

                        try {
                            ChannelService channelService = new ChannelService();
                            programList = channelService.searchCurrentChannelPlayByIndex(channelIndex);
                            //得到节目信息，发送消息更新UI
                            mDismissHandler.sendEmptyMessage(3);
                            //3秒后，节目信息显示框消失
                            Thread.sleep(5000);
                            mDismissHandler.sendEmptyMessage(4);
                        } catch (Exception e) {
                            Log.e("TVChannelPlayActivity", e.toString());
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            Log.e("TVChannelPlayActivity", e.toString());
            e.printStackTrace();
        }
    }

    //换台
    private void setPath(final String channelName) {
        try {
            if (!ClientSendCommandService.channelData.isEmpty()) {
                for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
                    Map<String, Object> map = ClientSendCommandService.channelData.get(i);
                    if (channelName.equals((String) map.get("service_name"))) {
                        name = (String) map.get("service_name");
                        path = ChannelService.obtainChannlPlayURL(map);
                        if (mVideoView != null && name != null && !name.equals(ILLEGAL_PROGRAM_NAME)) {
                            mVideoView.setVideoPath(path);
                        }
                        return;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("TVChannelPlayActivity", e.toString());
            e.printStackTrace();
        }
    }

    private void initTVChannel() {
        try {
            if (!ClientSendCommandService.channelData.isEmpty()) {
                for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
                    Map<String, Object> map = ClientSendCommandService.channelData.get(i);
                    channelNames.add(map.get("service_name").toString());
                }
            }
        } catch (Exception e) {
            Log.e("TVChannelPlayActivity", e.toString());
            e.printStackTrace();
        }
    }

    private BroadcastReceiver SwitchReceiver = new BroadcastReceiver() {

        public void onReceive(Context mContext, Intent mIntent) {
            if (mIntent.getAction().equals("com.action.switchchannel")) {
                String name = mIntent.getStringExtra("channelname");
                String switchfreq = mIntent.getStringExtra("channelfreq");
                Log.e("TVPlayer", "channelname >>> " + name + "channelfreq >>>" + switchfreq);
                //异频点才换台
                if (name != null && !name.equals("") && !switchfreq.equals(freq)) {
                    freq = switchfreq;
                    setPath(name);
                }
            }
        }
    };

    private class PlayerIsPlayingMinitorThread extends Thread {
        public void run() {
            while (true) {
                try {
                    //用户在10秒钟之内连续返回则退出，反则技术器归一
                    if ((System.currentTimeMillis() - backTimestamp) > 10000 && backTimestamp != 0l) {
                        returnConfirm = 1;
                        backTimestamp = 0l;
                    }

                    if (mVideoView != null && mVideoView.isPlaying()) {
                        //播放中更新时间
                        playTimestamp = System.currentTimeMillis();
                    }
                    if ((System.currentTimeMillis() - playTimestamp) > 8000 && playTimestamp != 0l) {
                        //超过8秒一直没有播放，退出播放界面
                        if (mDismissHandler != null) {
                            mDismissHandler.sendEmptyMessage(1);
                            finish();
                        }
                        break;
                    }
                    SystemClock.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ChannelAdapter extends BaseAdapter {
        private LayoutInflater minflater;

        public ChannelAdapter(Context context) {
            this.minflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return channelNames.size();
        }

        public Object getItem(int position) {
            return channelNames.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = minflater.inflate(R.layout.tv_play_channel_item, null);
                vh.channelName = (TextView) convertView.findViewById(R.id.channel_name);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            vh.channelName.setText(StringUtils.getShortString("  " + String.valueOf(position + 1) + "  " + channelNames.get(position), 15));
            vh.channelName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    setPath(channelNames.get(position));
                    initProgramInfo(channelNames.get(position));
                }
            });
            return convertView;
        }

        public final class ViewHolder {
            public TextView channelName;
        }
    }

    /***********************************************屏幕触控部分********************************************************/

    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (!menuKey) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    programInfoLayout.setVisibility(View.VISIBLE);
                    menuKey = true;
                } else {
                    relativeLayout.setVisibility(View.GONE);
                    programInfoLayout.setVisibility(View.GONE);
                    menuKey = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                endGesture();
                break;
        }
        return true;
    }

    private class MyGestureListener extends SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            int y = (int) e2.getRawY();
            Display disp = getWindowManager().getDefaultDisplay();
            int windowWidth = disp.getWidth();
            int windowHeight = disp.getHeight();

            if (mOldX > windowWidth * 4.0 / 5)// 右边滑动
                onVolumeSlide((mOldY - y) / windowHeight);
            else if (mOldX < windowWidth / 5.0)// 左边滑动
                onBrightnessSlide((mOldY - y) / windowHeight);

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

    }

    /**
     * 滑动改变声音大小
     */
    private void onVolumeSlide(float percent) {
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;

            // 显示
            mOperationBg.setImageResource(R.drawable.video_volumn_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }

        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        // 变更进度条
        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = findViewById(R.id.operation_full).getLayoutParams().width
                * index / mMaxVolume;
        mOperationPercent.setLayoutParams(lp);
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (mBrightness < 0) {
            mBrightness = getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;

            // 显示
            mOperationBg.setImageResource(R.drawable.video_brightness_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }
        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        getWindow().setAttributes(lpa);

        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
        mOperationPercent.setLayoutParams(lp);
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;

        // 隐藏
        mDismissHandler.removeMessages(0);
        mDismissHandler.sendEmptyMessageDelayed(0, 500);

    }

    /***********************************************系统方法重载********************************************************/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                if (!menuKey) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    programInfoLayout.setVisibility(View.VISIBLE);
                    menuKey = true;
                } else {
                    relativeLayout.setVisibility(View.GONE);
                    programInfoLayout.setVisibility(View.GONE);
                    menuKey = false;
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                if (returnConfirm == 1) {
                    returnConfirm = 0;
                    backTimestamp = System.currentTimeMillis();
                    Toast.makeText(TVChannelPlayActivity.this, "再按一次退出", 1000).show();
                } else {
                    mDismissHandler = null;
                    finish();
                }
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mVideoView != null) {
            mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TVChannelPlayActivity.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (SwitchReceiver != null) {
            try {
                unregisterReceiver(SwitchReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            SwitchReceiver = null;
        }
    }
}
