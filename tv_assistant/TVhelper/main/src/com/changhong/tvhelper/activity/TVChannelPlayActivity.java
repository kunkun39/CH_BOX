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

    /**
     * 频道列表
     *
     * @param icicle
     */
    private ListView channelList;
    private ChannelAdapter channelAdapter;
    private HashMap<String, Integer> hs = new HashMap<String, Integer>();
    private List<String> channelNames;
    private boolean menuKey = false;
    private RelativeLayout relativeLayout;

    /**
     * 节目信息
     *
     * @param icicle
     */
    List<Program> programList = new ArrayList<Program>();
    private RelativeLayout programInfoLayout;
    private TextView textCurrentProgramInfo;
    private TextView textNextProgramInfo;
    private TextView textChannelName;
    private String channelIndex;


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

        //频道列表
        initTVchannel();
        Set set = hs.keySet();
        channelNames = new ArrayList<String>();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            String name = (String) iterator.next();
            channelNames.add(name);
        }
        channelAdapter = new ChannelAdapter(this);
        relativeLayout = (RelativeLayout) findViewById(R.id.channel_list_layout);
        channelList = (ListView) findViewById(R.id.channel_list);
        channelList.setAdapter(channelAdapter);

        //节目信息
        textCurrentProgramInfo = (TextView) findViewById(R.id.text_current_program_info);
        textNextProgramInfo = (TextView) findViewById(R.id.text_next_program_info);
        textChannelName = (TextView) findViewById(R.id.text_channel_name);
        programInfoLayout= (RelativeLayout) findViewById(R.id.program_info_layout);


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
        if (path != null && name != null) {
            setPath(name);
//          mVideoView.setVideoPath(path);
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
                if (!menuKey) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    menuKey = true;
                } else {
                    relativeLayout.setVisibility(View.GONE);
                    menuKey = false;
                }
                mVideoView.setVideoLayout(mm, 0);
                mm++;
                break;
            case KeyEvent.KEYCODE_BACK:
                mDismissHandler = null;
                finish();
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
            case MotionEvent.ACTION_DOWN:
                relativeLayout.setVisibility(View.GONE);
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
            /**
             Log.e("dddd", "onSingleTapUp>>>" + ((int) (e.getRawX() / (float) width * 10000) / 100f) + "  " + (int) (e.getRawY() / (float) height * 10000) / 100f);
             ClientSendCommandService.msgXpointYpoint = "xPoint=" + ((int) (e.getRawX() / (float) width * 10000) / 100f) + "%|yPoint=" + (int) (e.getRawY() / (float) height * 10000) / 100f + "%";
             Log.e("dddd", "msgXpointYpoint>>>" + ClientSendCommandService.msgXpointYpoint);
             ClientSendCommandService.handler.sendEmptyMessage(5);
             **/
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            /**
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
             **/
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
                case 3:
                    if(programList.size()>0){
                        Program currentProgram = programList.get(0);
                        textCurrentProgramInfo.setText("当前节目" + ":" + currentProgram.getProgramName() + "  " + currentProgram.getProgramStartTime() + "-" + currentProgram.getProgramEndTime());
                        Program nextProgram = programList.get(1);
                        textNextProgramInfo.setText("下一节目" + ":" + nextProgram.getProgramName() + "  " + nextProgram.getProgramStartTime() + "-" + nextProgram.getProgramEndTime());
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

    private void setPath(final String channelName) {
        if (!ClientSendCommandService.channelData.isEmpty()) {
            for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
                Map<String, Object> map = ClientSendCommandService.channelData.get(i);
                if (channelName.equals((String) map.get("service_name"))) {
                    name = (String) map.get("service_name");
                    path = ChannelService.obtainChannlPlayURL(map);
                    //节目信息
                    channelIndex = (String) map.get("channel_index");
                    programInfoLayout.setVisibility(View.VISIBLE);
                    textChannelName.setText(channelName);
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
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    if (mVideoView != null&&name!=null&&name.equals("无节目信息")) {
                        mVideoView.setVideoPath(path);
                    }
//                    if (name != null && controller != null) {
//                        controller.setFileName(name);
//                        controller.show();
//                    }

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
                    if (mDismissHandler != null) {
                        mDismissHandler.sendEmptyMessage(1);
                        finish();
                    }
                    break;
                }
                SystemClock.sleep(1000);
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
                }
            });
            return convertView;
        }

        public final class ViewHolder {
            public TextView channelName;
        }
    }


    private void initTVchannel() {
        hs.clear();

        hs.put(getResources().getString(R.string.cctv1_1), R.drawable.cctv1);
        hs.put(getResources().getString(R.string.cctv1_2), R.drawable.cctv1);
        hs.put(getResources().getString(R.string.cctv1_3), R.drawable.cctv1);
        hs.put(getResources().getString(R.string.cctv1_4), R.drawable.cctv1);
        hs.put(getResources().getString(R.string.cctv1hd_1), R.drawable.cctv1);
        hs.put(getResources().getString(R.string.cctv1hd_2), R.drawable.cctv1);
//		channelLogoMapping.put("CCTV-1����", R.drawable.cctv1hd);
//		channelLogoMapping.put("�ããԣ֣���(����)", R.drawable.cctv1hd);
        hs.put(getResources().getString(R.string.cctv2_1), R.drawable.cctv2);
        hs.put(getResources().getString(R.string.cctv2_2), R.drawable.cctv2);
        hs.put(getResources().getString(R.string.cctv2_3), R.drawable.cctv2);
        hs.put(getResources().getString(R.string.cctv2_4), R.drawable.cctv2);
        hs.put(getResources().getString(R.string.cctv3_1), R.drawable.cctv3);
        hs.put(getResources().getString(R.string.cctv3_2), R.drawable.cctv3);
        hs.put(getResources().getString(R.string.cctv3_3), R.drawable.cctv3);
        hs.put(getResources().getString(R.string.cctv3_4), R.drawable.cctv3);
        hs.put(getResources().getString(R.string.cctv3hd), R.drawable.cctv3);
//		channelLogoMapping.put("CCTV-3����", R.drawable.cctv3hd);
        hs.put(getResources().getString(R.string.cctv4_1), R.drawable.cctv4);
        hs.put(getResources().getString(R.string.cctv4_2), R.drawable.cctv4);
        hs.put(getResources().getString(R.string.cctv4_3), R.drawable.cctv4);
        hs.put(getResources().getString(R.string.cctv4_4), R.drawable.cctv4);
        hs.put(getResources().getString(R.string.cctv5hd), R.drawable.cctv5);
        hs.put(getResources().getString(R.string.cctv5hd_1), R.drawable.cctv5hd);
//		channelLogoMapping.put("CCTV5-�������¸���", R.drawable.cctv5hd1);
        hs.put(getResources().getString(R.string.cctv5_1), R.drawable.cctv5);
        hs.put(getResources().getString(R.string.cctv5_2), R.drawable.cctv5);
        hs.put(getResources().getString(R.string.cctv5_3), R.drawable.cctv5);
        hs.put(getResources().getString(R.string.cctv5_4), R.drawable.cctv5);
        hs.put(getResources().getString(R.string.cctv6_1), R.drawable.cctv6);
        hs.put(getResources().getString(R.string.cctv6_2), R.drawable.cctv6);
        hs.put(getResources().getString(R.string.cctv6_3), R.drawable.cctv6);
        hs.put(getResources().getString(R.string.cctv6_4), R.drawable.cctv6);
        hs.put(getResources().getString(R.string.cctv6hd), R.drawable.cctv6);
//		channelLogoMapping.put("CCTV-6����", R.drawable.cctv6hd);
        hs.put(getResources().getString(R.string.cctv7_1), R.drawable.cctv7);
        hs.put(getResources().getString(R.string.cctv7_2), R.drawable.cctv7);
        hs.put(getResources().getString(R.string.cctv7_3), R.drawable.cctv7);
        hs.put(getResources().getString(R.string.cctv7_4), R.drawable.cctv7);
        hs.put(getResources().getString(R.string.cctv8_1), R.drawable.cctv8);
        hs.put(getResources().getString(R.string.cctv8_2), R.drawable.cctv8);
        hs.put(getResources().getString(R.string.cctv8_3), R.drawable.cctv8);
        hs.put(getResources().getString(R.string.cctv8_4), R.drawable.cctv8);
        hs.put(getResources().getString(R.string.cctv8hd), R.drawable.cctv8);
//		channelLogoMapping.put("CCTV-8����", R.drawable.cctv8hd);
        hs.put(getResources().getString(R.string.cctv9_1), R.drawable.cctv9);
        hs.put(getResources().getString(R.string.cctv9_2), R.drawable.cctv9);
        hs.put(getResources().getString(R.string.cctv9_3), R.drawable.cctv9);
        hs.put(getResources().getString(R.string.cctv10_1), R.drawable.cctv10);
        hs.put(getResources().getString(R.string.cctv10_2), R.drawable.cctv10);
        hs.put(getResources().getString(R.string.cctv10_3), R.drawable.cctv10);
        hs.put(getResources().getString(R.string.cctv10_4), R.drawable.cctv10);
        hs.put(getResources().getString(R.string.cctv11_1), R.drawable.cctv11);
        hs.put(getResources().getString(R.string.cctv11_2), R.drawable.cctv11);
        hs.put(getResources().getString(R.string.cctv11_3), R.drawable.cctv11);
        hs.put(getResources().getString(R.string.cctv11_4), R.drawable.cctv11);
        hs.put(getResources().getString(R.string.cctv12_1), R.drawable.cctv12);
        hs.put(getResources().getString(R.string.cctv12_2), R.drawable.cctv12);
        hs.put(getResources().getString(R.string.cctv12_3), R.drawable.cctv12);
        hs.put(getResources().getString(R.string.cctv12_4), R.drawable.cctv12);
        hs.put(getResources().getString(R.string.cctv13_1), R.drawable.cctvnews);
        hs.put(getResources().getString(R.string.cctv13_2), R.drawable.cctvnews);
        hs.put(getResources().getString(R.string.cctv13_3), R.drawable.cctvnews);
        hs.put(getResources().getString(R.string.cctv13_4), R.drawable.cctvnews);
        hs.put(getResources().getString(R.string.cctv13_5), R.drawable.cctvnews);
        hs.put(getResources().getString(R.string.cctv13_6), R.drawable.cctvnews);
        hs.put(getResources().getString(R.string.cctv13_7), R.drawable.cctvnews);
        hs.put(getResources().getString(R.string.cctv13_8), R.drawable.cctvnews);
        hs.put(getResources().getString(R.string.cctv13_9), R.drawable.cctvnews);
        hs.put(getResources().getString(R.string.cctv14_1), R.drawable.cctv14);
        hs.put(getResources().getString(R.string.cctv14_2), R.drawable.cctv14);
        hs.put(getResources().getString(R.string.cctv14_3), R.drawable.cctv14);
        hs.put(getResources().getString(R.string.cctv14_4), R.drawable.cctv14);
        hs.put(getResources().getString(R.string.cctv14_5), R.drawable.cctv14);
        hs.put(getResources().getString(R.string.cctv14_6), R.drawable.cctv14);
        hs.put(getResources().getString(R.string.cctv14_7), R.drawable.cctv14);
        hs.put(getResources().getString(R.string.cctv15_1), R.drawable.cctv15);
        hs.put(getResources().getString(R.string.cctv15_2), R.drawable.cctv15);
        hs.put(getResources().getString(R.string.cctv15_3), R.drawable.cctv15);
        hs.put(getResources().getString(R.string.cctveyu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.cctvalaboyu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.cctvguide), R.drawable.logotv);
        hs.put(getResources().getString(R.string.anhuiweishi), R.drawable.logoanhui);
        hs.put(getResources().getString(R.string.beijingweishi), R.drawable.logobeijing);
        hs.put(getResources().getString(R.string.chongqingweishi), R.drawable.logochongqing);
        hs.put(getResources().getString(R.string.dongfangweishi), R.drawable.logodongfang);
        hs.put(getResources().getString(R.string.shanghaiweishi), R.drawable.logodongfang);
        hs.put(getResources().getString(R.string.dongnanweishi), R.drawable.logodongnan);
        hs.put(getResources().getString(R.string.fujianweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.guangdongweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.guangxiweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.guizhouweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.hebeiweishi), R.drawable.logohebei);
        hs.put(getResources().getString(R.string.henanweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.heilongjiangtai), R.drawable.logotv);
        hs.put(getResources().getString(R.string.heilongjiangweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.hubeiweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.hunanweishi), R.drawable.logohunan);
        hs.put(getResources().getString(R.string.hunanweishigaoqing), R.drawable.logohunan);
        hs.put(getResources().getString(R.string.jiangsuweishi), R.drawable.logojiangsu);
        hs.put(getResources().getString(R.string.jiangxiweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.jilinweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.liaoningweishi), R.drawable.logoliaoning);
        hs.put(getResources().getString(R.string.neimenggutai), R.drawable.logotv);
        hs.put(getResources().getString(R.string.neimengguweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.ningxiaweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.shandongweishi), R.drawable.logoshandong);
        hs.put(getResources().getString(R.string.shanxiweishi), R.drawable.logoshanxi);
        hs.put(getResources().getString(R.string.shanxi1weishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.sichuanweishi), R.drawable.logosichuan);
        hs.put(getResources().getString(R.string.tianjinweishi), R.drawable.logotianjin);
        hs.put(getResources().getString(R.string.yunnanweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.xizangweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.xinjiangweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.zhejiangweishi), R.drawable.logozhejiang);
        hs.put(getResources().getString(R.string.shenzhengweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.fenghuangweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.gansuweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.qinghaiweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.sichuangaoqing), R.drawable.logotv);
        hs.put(getResources().getString(R.string.sichuanweishigaoqing), R.drawable.logosichuan);
        hs.put(getResources().getString(R.string.sichuanyingshigaoqing), R.drawable.logosichuan);
        hs.put(getResources().getString(R.string.zhejiangweishigaoqing), R.drawable.logozhejiang);
        hs.put(getResources().getString(R.string.zhejianggaoqing), R.drawable.logozhejiang);
        hs.put(getResources().getString(R.string.beijingweishigaoqing), R.drawable.logobeijing);
        hs.put(getResources().getString(R.string.beijinggaoqing), R.drawable.logobeijing);
        hs.put(getResources().getString(R.string.shanghaiweishigaoqing), R.drawable.logodongfang);
        hs.put(getResources().getString(R.string.shanghaigaoqing), R.drawable.logodongfang);
        hs.put(getResources().getString(R.string.guangdongweishigaoqing), R.drawable.logotv);
        hs.put(getResources().getString(R.string.guangdonggaoqing), R.drawable.logotv);
        hs.put(getResources().getString(R.string.shenzhengweishigaoqing), R.drawable.logotv);
        hs.put(getResources().getString(R.string.jiangsuweishigaoqing), R.drawable.logojiangsu);
        hs.put(getResources().getString(R.string.jiangsugaoqing), R.drawable.logojiangsu);
        hs.put(getResources().getString(R.string.heilongjiangweishigaoqing), R.drawable.logotv);
        hs.put(getResources().getString(R.string.heilongjianggaoqing), R.drawable.logotv);
        hs.put(getResources().getString(R.string.hunanweishigaoqing), R.drawable.logohunan);
        hs.put(getResources().getString(R.string.hunangaoqing), R.drawable.logohunan);
        hs.put(getResources().getString(R.string.hubeigaoqing), R.drawable.logotv);
        hs.put(getResources().getString(R.string.shandonggaoqing), R.drawable.logotv);
        hs.put(getResources().getString(R.string.shenzhenggaoqing), R.drawable.logotv);
        hs.put(getResources().getString(R.string.tianjingaoqing), R.drawable.logotv);
        hs.put(getResources().getString(R.string.quanjishi), R.drawable.logoquanjishi);
        hs.put(getResources().getString(R.string.guofangjunshi), R.drawable.logoguofangjunshi);
        hs.put(getResources().getString(R.string.dieshipindao), R.drawable.logotv);
        hs.put(getResources().getString(R.string.dongfangcaijin), R.drawable.logodongfangcaijin);
        hs.put(getResources().getString(R.string.dongmanxiuchang), R.drawable.logotv);
        hs.put(getResources().getString(R.string.dushijuchang), R.drawable.logotv);
        hs.put(getResources().getString(R.string.emeidianying), R.drawable.logoemei);
        hs.put(getResources().getString(R.string.sihaidiaoyu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.fazhitiandi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.fengyunzuqiu), R.drawable.logofengyunzuqiu);
        hs.put(getResources().getString(R.string.huanxiaojuchang), R.drawable.logotv);
        hs.put(getResources().getString(R.string.jiayougouwu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.jiatinglicai), R.drawable.logotv);
        hs.put(getResources().getString(R.string.jinsepindao), R.drawable.logotv);
        hs.put(getResources().getString(R.string.jingbaotiyu), R.drawable.logojinbaotiyu);
        hs.put(getResources().getString(R.string.jinyingkatong), R.drawable.logojinyingkatong);
        hs.put(getResources().getString(R.string.jisuqiche), R.drawable.logotv);
        hs.put(getResources().getString(R.string.kuailechongwu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.laogushi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.liangzhuangpindao), R.drawable.logotv);
        hs.put(getResources().getString(R.string.liuxueshijie), R.drawable.logotv);
        hs.put(getResources().getString(R.string.lvyoupindao), R.drawable.logotv);
        hs.put(getResources().getString(R.string.lvyouweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.meiliyinyue), R.drawable.logotv);
        hs.put(getResources().getString(R.string.shenghuoshishang), R.drawable.logotv);
        hs.put(getResources().getString(R.string.shijiedili), R.drawable.logoshijiedili);
        hs.put(getResources().getString(R.string.tianyuanweiqi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.weishengjiankang), R.drawable.logotv);
        hs.put(getResources().getString(R.string.yingyufudao), R.drawable.logotv);
        hs.put(getResources().getString(R.string.yingyufudao1), R.drawable.logotv);
        hs.put(getResources().getString(R.string.youxifengyun), R.drawable.logotv);
        hs.put(getResources().getString(R.string.youxifengyun1), R.drawable.logotv);
        hs.put(getResources().getString(R.string.youxijingji), R.drawable.logotv);
        hs.put(getResources().getString(R.string.yougouwu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.yunyuzhinan), R.drawable.logotv);
        hs.put(getResources().getString(R.string.zhengquanzixun), R.drawable.logotv);
        hs.put(getResources().getString(R.string.jiaoyupindao), R.drawable.logotv);
        hs.put(getResources().getString(R.string.zhongguojiaoyu1), R.drawable.logotv);
        hs.put(getResources().getString(R.string.zhongguoqixiang), R.drawable.logotv);
        hs.put(getResources().getString(R.string.zhongguoqixiangtai), R.drawable.logotv);
        hs.put(getResources().getString(R.string.zhongguoqixiang1), R.drawable.logotv);
        hs.put(getResources().getString(R.string.jingcaisichuan), R.drawable.logotv);
        hs.put(getResources().getString(R.string.xinyule), R.drawable.logotv);
        hs.put(getResources().getString(R.string.xingyule), R.drawable.logotv);
        hs.put(getResources().getString(R.string.xingyuanyinhua), R.drawable.xingyuanyinghua);
        hs.put(getResources().getString(R.string.xingyuanjuchang), R.drawable.xingyuanjuchang);
        hs.put(getResources().getString(R.string.xingyuanxinzhi), R.drawable.xingyuanxinzhi);
        hs.put(getResources().getString(R.string.xingyuanxinyi), R.drawable.xingyuanxinyi);
        hs.put(getResources().getString(R.string.xingyuanai), R.drawable.logotv);
        hs.put(getResources().getString(R.string.xingyuanchengzhang), R.drawable.logotv);
        hs.put(getResources().getString(R.string.xingyuanoumeijuchang), R.drawable.logotv);
        hs.put(getResources().getString(R.string.xingyuanoumeiyuanxian), R.drawable.logotv);
        hs.put(getResources().getString(R.string.xingyuanshouying), R.drawable.logotv);
        hs.put(getResources().getString(R.string.xingyuanxinzhi1), R.drawable.logotv);
        hs.put(getResources().getString(R.string.xingyuanyazhoujuchang), R.drawable.logotv);
        hs.put(getResources().getString(R.string.xingyuanyazhouyuanxian), R.drawable.logotv);
        hs.put(getResources().getString(R.string.jiatingjuchangdianshiju), R.drawable.logotv);
        hs.put(getResources().getString(R.string.jingdianjuchangdianshiju), R.drawable.logotv);
        hs.put(getResources().getString(R.string.threeDpindao), R.drawable.logotv);
        hs.put(getResources().getString(R.string.HBO), R.drawable.logotv);
        hs.put(getResources().getString(R.string.TVB8), R.drawable.logotv);
        hs.put(getResources().getString(R.string.BBC), R.drawable.logotv);
        hs.put(getResources().getString(R.string.KBS), R.drawable.logotv);
        hs.put(getResources().getString(R.string.NHK), R.drawable.logotv);
        hs.put(getResources().getString(R.string.sichuan2), R.drawable.logotv);
        hs.put(getResources().getString(R.string.sichuanwenhualvyou), R.drawable.logotv);
        hs.put(getResources().getString(R.string.sichuan3), R.drawable.logotv);
        hs.put(getResources().getString(R.string.sichuanjingji), R.drawable.logotv);
        hs.put(getResources().getString(R.string.sichuan4), R.drawable.logotv);
        hs.put(getResources().getString(R.string.sichuanxinwentongxun), R.drawable.logotv);
        hs.put(getResources().getString(R.string.sichuan5), R.drawable.logotv);
        hs.put(getResources().getString(R.string.sichuanyingshiwenhua), R.drawable.logotv);
        hs.put(getResources().getString(R.string.sichuan6), R.drawable.logotv);
        hs.put(getResources().getString(R.string.sichuanxingkonggouwu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.sichuan7), R.drawable.logotv);
        hs.put(getResources().getString(R.string.sichuanfunvertong), R.drawable.logotv);
        hs.put(getResources().getString(R.string.sichuan8), R.drawable.logotv);
        hs.put(getResources().getString(R.string.sichuankejiao), R.drawable.logotv);
        hs.put(getResources().getString(R.string.sctv09), R.drawable.logotv);
        hs.put(getResources().getString(R.string.sichuan9), R.drawable.logotv);
        hs.put(getResources().getString(R.string.mianyang1), R.drawable.logotv);
        hs.put(getResources().getString(R.string.mianyang1_1), R.drawable.logotv);
        hs.put(getResources().getString(R.string.mianyang2), R.drawable.logotv);
        hs.put(getResources().getString(R.string.mianyang2_1), R.drawable.logotv);
        hs.put(getResources().getString(R.string.mianyang3), R.drawable.logotv);
        hs.put(getResources().getString(R.string.mianyang3_1), R.drawable.logotv);
        hs.put(getResources().getString(R.string.mianyang4), R.drawable.logotv);
        hs.put(getResources().getString(R.string.mianyang4_1), R.drawable.logotv);
        hs.put(getResources().getString(R.string.santai1), R.drawable.logotv);
        hs.put(getResources().getString(R.string.santai2), R.drawable.logotv);
        hs.put(getResources().getString(R.string.santai3), R.drawable.logotv);
        hs.put(getResources().getString(R.string.santai4), R.drawable.logotv);
        hs.put(getResources().getString(R.string.beichuan1), R.drawable.logotv);
        hs.put(getResources().getString(R.string.beichuan2), R.drawable.logotv);
        hs.put(getResources().getString(R.string.zitong3), R.drawable.logotv);
        hs.put(getResources().getString(R.string.zitong4), R.drawable.logotv);
        hs.put(getResources().getString(R.string.zitong5), R.drawable.logotv);
        hs.put(getResources().getString(R.string.zitongzibanjiemu1), R.drawable.logotv);
        hs.put(getResources().getString(R.string.zitongzibanjiemu2), R.drawable.logotv);
        hs.put(getResources().getString(R.string.cdtv1), R.drawable.logotv);
        hs.put(getResources().getString(R.string.cdtv2), R.drawable.logotv);
        hs.put(getResources().getString(R.string.cdtv3), R.drawable.logotv);
        hs.put(getResources().getString(R.string.cdtv4), R.drawable.logotv);
        hs.put(getResources().getString(R.string.cdtv5), R.drawable.logotv);
        hs.put(getResources().getString(R.string.cdtv6), R.drawable.logotv);
        hs.put(getResources().getString(R.string.cdtv7), R.drawable.logotv);
        hs.put(getResources().getString(R.string.cdtv8), R.drawable.logotv);
        hs.put(getResources().getString(R.string.cdtvgaoqing), R.drawable.logotv);
        hs.put(getResources().getString(R.string.cetv1), R.drawable.logotv);
        hs.put(getResources().getString(R.string.cetv2), R.drawable.logotv);
        hs.put(getResources().getString(R.string.CHCdongzuoyingyuan), R.drawable.logotv);
        hs.put(getResources().getString(R.string.CHCgaoqing), R.drawable.logotv);
        hs.put(getResources().getString(R.string.DOXyinxiangshijie), R.drawable.logotv);
        hs.put(getResources().getString(R.string.DVshenghuo), R.drawable.logotv);
        hs.put(getResources().getString(R.string.IjiaTV), R.drawable.logotv);
        hs.put(getResources().getString(R.string.SCTV1), R.drawable.logotv);
        hs.put(getResources().getString(R.string.SCTV2), R.drawable.logotv);
        hs.put(getResources().getString(R.string.SCTV3), R.drawable.logotv);
        hs.put(getResources().getString(R.string.SCTV4), R.drawable.logotv);
        hs.put(getResources().getString(R.string.SCTV5), R.drawable.logotv);
        hs.put(getResources().getString(R.string.SCTV6), R.drawable.logotv);
        hs.put(getResources().getString(R.string.SCTV7), R.drawable.logotv);
        hs.put(getResources().getString(R.string.SCTVemei), R.drawable.logotv);
        hs.put(getResources().getString(R.string.SCTVgonggong), R.drawable.logotv);
        hs.put(getResources().getString(R.string.SCTVkejiao), R.drawable.logotv);
        hs.put(getResources().getString(R.string.SCTVxingkong), R.drawable.logotv);
        hs.put(getResources().getString(R.string.baixingjiankang), R.drawable.logotv);
        hs.put(getResources().getString(R.string.baobeijia), R.drawable.logotv);
        hs.put(getResources().getString(R.string.bingtuanweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.caifutianxia), R.drawable.logotv);
        hs.put(getResources().getString(R.string.caiminzaixian), R.drawable.logotv);
        hs.put(getResources().getString(R.string.chemi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.chengdujiaotongguangbo), R.drawable.logotv);
        hs.put(getResources().getString(R.string.chengdujingjiguangbo), R.drawable.logotv);
        hs.put(getResources().getString(R.string.chengduwangluoguangbo), R.drawable.logotv);
        hs.put(getResources().getString(R.string.chengduxinwenguangbo), R.drawable.logotv);
        hs.put(getResources().getString(R.string.chengduxiuxianguangbo), R.drawable.logotv);
        hs.put(getResources().getString(R.string.chengshijianshe), R.drawable.logotv);
        hs.put(getResources().getString(R.string.diyijuchang), R.drawable.logotv);
        hs.put(getResources().getString(R.string.dianzitiyu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.dieshi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.dushizhisheng), R.drawable.logotv);
        hs.put(getResources().getString(R.string.dushu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.faxianzhilv), R.drawable.logotv);
        hs.put(getResources().getString(R.string.fengshanggouwu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.fengyunjuchang), R.drawable.logotv);
        hs.put(getResources().getString(R.string.fengyunyinyue), R.drawable.logotv);
        hs.put(getResources().getString(R.string.gaoerfu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.gaoerfuwangqiu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.haoxianggouwu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.huaxiazhisheng), R.drawable.logotv);
        hs.put(getResources().getString(R.string.huaijiujuchang), R.drawable.logotv);
        hs.put(getResources().getString(R.string.huanqiugouwu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.huanqiulvyou), R.drawable.logohuanqiulvyou);
        hs.put(getResources().getString(R.string.huanqiuzixunguangbo), R.drawable.logotv);
        hs.put(getResources().getString(R.string.jiajiagouwu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.jiatingjiankang), R.drawable.logotv);
        hs.put(getResources().getString(R.string.jiazhengpindao), R.drawable.logotv);
        hs.put(getResources().getString(R.string.jiajiakatong), R.drawable.logotv);
        hs.put(getResources().getString(R.string.jinniuyouxiantai), R.drawable.logotv);
        hs.put(getResources().getString(R.string.jingjizhisheng), R.drawable.logotv);
        hs.put(getResources().getString(R.string.jingpindaoshi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.kakukatong), R.drawable.logotv);
        hs.put(getResources().getString(R.string.kangbaweishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.kaoshizaixian), R.drawable.logotv);
        hs.put(getResources().getString(R.string.kuailegouwu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.laonianfu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.liyuan), R.drawable.logotv);
        hs.put(getResources().getString(R.string.liangzhuang), R.drawable.logotv);
        hs.put(getResources().getString(R.string.minzuzhisheng), R.drawable.logotv);
        hs.put(getResources().getString(R.string.nvxingshishang), R.drawable.logotv);
        hs.put(getResources().getString(R.string.ouzhouzuqiu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.qicaixiju), R.drawable.logotv);
        hs.put(getResources().getString(R.string.qimo), R.drawable.logotv);
        hs.put(getResources().getString(R.string.quanyuchengduguangbo), R.drawable.logotv);
        hs.put(getResources().getString(R.string.renwu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.rongchengxianfeng), R.drawable.logotv);
        hs.put(getResources().getString(R.string.sheying), R.drawable.logotv);
        hs.put(getResources().getString(R.string.shenzhouzhisheng), R.drawable.logotv);
        hs.put(getResources().getString(R.string.shidaichuxing), R.drawable.logotv);
        hs.put(getResources().getString(R.string.shidaifengshang), R.drawable.logotv);
        hs.put(getResources().getString(R.string.shidaijiaju), R.drawable.logotv);
        hs.put(getResources().getString(R.string.shidaimeishi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.shishanggouwu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.shoucangtianxia), R.drawable.logotv);
        hs.put(getResources().getString(R.string.shuhua), R.drawable.logotv);
        hs.put(getResources().getString(R.string.shuowenjiezi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.tianyinweiyiyinyue), R.drawable.logotv);
        hs.put(getResources().getString(R.string.wangluoqipai), R.drawable.logotv);
        hs.put(getResources().getString(R.string.wenhuajingpin), R.drawable.logotv);
        hs.put(getResources().getString(R.string.wenwubaoku), R.drawable.logotv);
        hs.put(getResources().getString(R.string.wenyizhisheng), R.drawable.logotv);
        hs.put(getResources().getString(R.string.wushushijie), R.drawable.logotv);
        hs.put(getResources().getString(R.string.xianfengjilu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.xiandainvxing), R.drawable.logotv);
        hs.put(getResources().getString(R.string.xindongman), R.drawable.logotv);
        hs.put(getResources().getString(R.string.xinkedongman), R.drawable.logotv);
        hs.put(getResources().getString(R.string.xingfucai), R.drawable.logotv);
        hs.put(getResources().getString(R.string.xinyuezhisheng), R.drawable.logotv);
        hs.put(getResources().getString(R.string.youyoubaobei), R.drawable.logotv);
        hs.put(getResources().getString(R.string.zaoqijiaoyu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.zhiyezhinan), R.drawable.logotv);
        hs.put(getResources().getString(R.string.zhongguotianqi), R.drawable.logotv);
        hs.put(getResources().getString(R.string.zhongguozhisheng), R.drawable.logotv);
        hs.put(getResources().getString(R.string.zhonghuazhisheng), R.drawable.logotv);
        hs.put(getResources().getString(R.string.zhongshigouwu), R.drawable.logotv);
        hs.put(getResources().getString(R.string.zhongxuesheng), R.drawable.logotv);
        // channelLogoMapping.put("NHK", R.drawable.logotv);

    }


}
