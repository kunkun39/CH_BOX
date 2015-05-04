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

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.tvhelper.R;

import com.changhong.tvhelper.service.ChannelService;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.widget.MediaController;
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
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Map;

public class TVChannelPlayActivity extends Activity {

    /**
     * video play view
     */
    public static VideoView mVideoView;
    private MediaController controller;
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

    private long time = 0l;

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
        width = metric.widthPixels;     // 屏幕宽度（像素）
        height = metric.heightPixels;   // 屏幕高度（像素）
        float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        Log.e("ysharp", "width>>>" + width + "height>>>" + height + "density>>>" + density + "densityDpi>>>" + densityDpi);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        setContentView(R.layout.activity_channel_play);
        mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
        mOperationBg = (ImageView) findViewById(R.id.operation_bg);
        mOperationPercent = (ImageView) findViewById(R.id.operation_percent);

        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("com.action.switchchannel");
        registerReceiver(this.SwitchReceiver, intentfilter);
        Intent intent = getIntent();
        if (intent.getStringExtra("channelname") != null && !intent.getStringExtra("channelname").equals("") && !intent.getStringExtra("channelname").equals("null")) {
            name = intent.getStringExtra("channelname");
        }
        mVideoView = (VideoView) findViewById(R.id.surface_view);
        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
        mVideoView.setBufferSize(256 * 1024);
        mVideoView.setKeepScreenOn(true);
        mVideoView.setHardwareDecoder(true);
        mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
        if (path != null) {
            mVideoView.setVideoPath(path);
        }
        //mVideoView.prepare();
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
        mVideoView.setOnPreparedListener(new OnPreparedListener() {


            public void onPrepared(MediaPlayer mp) {
                if (dd.isShowing()) {
                    dd.dismiss();
                }
                int w = mVideoView.getVideoWidth();
                int h = mVideoView.getVideoHeight();
                if (h > 576) {
                    mVideoView.setBufferSize(512 * 1024);
                }
                Log.i("ysharp", "w is %d" + w);
                Log.i("ysharp", "h is %d" + h);

            }
        });

        mVideoView.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
                Log.e("ysharp", "" + arg1);
//                if(arg1==0){
//                	//缓冲开始，初始化缓冲开始时间
//                	time = System.currentTimeMillis();
//                }
//                if(!arg0.isBuffering()){
//                	//没有数据，并且超过8S，退出播放页面
//                	if((System.currentTimeMillis() - time) > 8000 && time != 0l){
//                		finish();
//                	}
//                }
                if (arg0.isBuffering()) {
                    if (!dd.isShowing()) {
                        dd.show();
                    }
                    //有数据，但是超过8s，没有播放，退出播放页面
//                    if((System.currentTimeMillis() - time) > 8000 && time != 0l&&!mVideoView.isPlaying()){
//                		finish();
//                	}
                }
                if (arg1 >= 25) {
                    if (dd.isShowing()) {
                        dd.dismiss();
                    }
                }
            }
        });
        controller = new MediaController(this);

        if (name != null) {
            controller.setFileName(name);
            controller.show();
        }
        mVideoView.setMediaController(controller);
        mVideoView.requestFocus();
        mGestureDetector = new GestureDetector(this, new MyGestureListener());
        time = System.currentTimeMillis();
        new PlayerIsPlayingMinitorThread().start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mVideoView != null)
            mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                if (mm == 4) {
                    mm = 0;
                }
                mVideoView.setVideoLayout(mm, 0);
                mm++;
                break;
            case KeyEvent.KEYCODE_BACK:
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                endGesture();
                break;
        }
        return false;
    }

    private class MyGestureListener extends SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.e("dddd", "onSingleTapUp>>>" + ((int) (e.getRawX() / (float) width * 10000) / 100f) + "  " + (int) (e.getRawY() / (float) height * 10000) / 100f);
            ClientSendCommandService.msgXpointYpoint = "xPoint=" + ((int) (e.getRawX() / (float) width * 10000) / 100f) + "%|yPoint=" + (int) (e.getRawY() / (float) height * 10000) / 100f + "%";
            Log.e("dddd", "msgXpointYpoint>>>" + ClientSendCommandService.msgXpointYpoint);
            ClientSendCommandService.handler.sendEmptyMessage(5);
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            int w = mVideoView.getVideoWidth();
            int h = mVideoView.getVideoHeight();
            Log.i("ysharp", "w is %d" + w);
            Log.i("ysharp", "h is %d" + h);
            if (mm == VideoView.VIDEO_LAYOUT_ZOOM) {
                mm = VideoView.VIDEO_LAYOUT_SCALE;
            } else {
                mm++;
            }
            if (mVideoView != null) {
                mVideoView.setVideoLayout(mm, 0);
            }
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
     * 手势结束
     */
    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;

        // 隐藏
        mDismissHandler.removeMessages(0);
        mDismissHandler.sendEmptyMessageDelayed(0, 500);
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
                default:
                    break;
            }
        }
    };

    /**
     * 滑动改变声音大小
     *
     * @param percent
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

    private void setPath(String ChannelName) {
        if (!ClientSendCommandService.channelData.isEmpty()) {
            for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
                Map<String,Object> map = ClientSendCommandService.channelData.get(i);

                if (ChannelName.equals((String) map.get("service_name"))) {
                    name = (String) map.get("service_name");
                    path = ChannelService.obtainChannlPlayURL(map);

                    if (mVideoView != null) {
                        mVideoView.setVideoPath(path);
                    }
                    if (name != null && controller != null) {
                        controller.setFileName(name);
                        controller.show();
                    }

                    return;
                }
            }
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
                if (mVideoView != null && mVideoView.isPlaying()) {
                    //播放中更新时间
                    time = System.currentTimeMillis();
                }
                if ((System.currentTimeMillis() - time) > 8000 && time != 0l) {
                    //超过8秒一直没有播放，退出播放界面
                    mDismissHandler.sendEmptyMessage(1);
                    finish();
                    break;
                }
                SystemClock.sleep(1000);
            }
        }
    }
}