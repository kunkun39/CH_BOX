package com.changhong.touying.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.DateUtils;
import com.changhong.common.utils.MobilePerformanceUtils;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.touying.R;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicLrc;
import com.changhong.touying.nanohttpd.NanoHTTPDService;
import com.changhong.touying.service.MusicService;
import com.changhong.touying.service.MusicServiceImpl;
import org.json.JSONObject;

/**
 * Created by maren on 2015/4/9.
 */
public class MusicDetailsActivity extends Activity {

    /**
     * 消息处理
     */
    public static Handler handler;

    /**
     * 被选中的音乐文件
     */
    private Music selectedMusic;

    /**
     * 图片
     */
    private ImageView musicImage;

    /**
     * 歌曲名
     */
    private TextView musicName;


    /**
     * 音乐时长
     */
    private TextView musicDuring;

    /**
     * 音乐类型
     */
    private TextView musicType;

    /**
     * 返回按钮
     */
    private ImageView returnImage;

    /**
     * 播放按钮
     */
    private ImageView playImage;

    /**
     * 播放的进度条
     */
    private SeekBar seekBar;

    /**
     * 时间显示信息
     */
    private TextView showTimeGoing;
    private TextView showTimeTotal;

    /**
     * 音乐的时间长度
     */
    private String musicTotalTime;

    /**
     * 时间长度
     */
    private int totalTime;

    /**
     * SeekBar
     */
    private String currentime;

    /**
     * 判断是否正在播放，可以用来防止用户连续点击播放按钮，导致系统创建信的线程
     */
    private boolean isPlaying = false;

    /**
     * 是否为暂停状态
     */
    private boolean isPausing = false;

    /**
     * 是否拖动过，因为，拖动后，手机端的进度条刚更新后，服务端还未跟新就已经发送的进度消息，手机端会返回拖动前状态
     * 一旦发生拖动，就手机缓冲2秒再更新进度条
     */
    private int isSeeking = 0;

    /**
     * 播放时显示进度条，停止时隐藏进度条
     */
    private RelativeLayout seekbarLayout;

    /**
     * 播放暂停按钮
     */
    private ImageView controlButton;

    /**
     * 音量控制按钮
     */
    private ImageView volUpBtn;
    private ImageView volDownBtn;

    private MusicService musicService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        selectedMusic = (Music) intent.getSerializableExtra("selectedMusic");
        musicService = new MusicServiceImpl(MusicDetailsActivity.this);

        initialViews();

        initialEvents();
    }

    private void initialViews() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_music_details);

        musicTotalTime = DateUtils.getTimeShow(selectedMusic.getDuration() / 1000);
        totalTime = (selectedMusic.getDuration() / 1000);

        musicImage = (ImageView) findViewById(R.id.details_image);
        musicName = (TextView) findViewById(R.id.music_name);
        musicName.setText("歌名 ：" + selectedMusic.getTitle());
        musicDuring = (TextView) findViewById(R.id.music_during);
        musicDuring.setText("时长 ： " + musicTotalTime);

        musicType = (TextView) findViewById(R.id.music_type);
        musicType.setText("歌手 ： " + selectedMusic.getArtist());

        returnImage = (ImageView) findViewById(R.id.d_btn_return);
        playImage = (ImageView) findViewById(R.id.d_btn_play);

        seekBar = (SeekBar) findViewById(R.id.play_seekbar);
        seekBar.setMax(totalTime);
        showTimeGoing = (TextView) findViewById(R.id.music_showtime_going);
        showTimeGoing.setText("00:00");
        showTimeTotal = (TextView) findViewById(R.id.music_showtime_total);
        showTimeTotal.setText(musicTotalTime);

        seekbarLayout = (RelativeLayout) findViewById(R.id.music_seek_layout);
        controlButton = (ImageView) findViewById(R.id.music_control_button);

        volDownBtn = (ImageView) findViewById(R.id.control_volume_small);
        volUpBtn = (ImageView) findViewById(R.id.control_volume_bigger);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                //进度条同步服务端的更新
                if (msg.what == 0) {
                    //HTTPD的使用状态
                    MobilePerformanceUtils.httpServerUsing = true;

                    String[] content = StringUtils.delimitedListToStringArray(((String) msg.obj), "|");
                    String key = selectedMusic.getTitle() + "-" +selectedMusic.getArtist();
                    //判断当前的页面是否为在播放的歌曲
                    if (key.equals(content[0].replace("%20", " "))) {
                        int progress = Integer.parseInt(content[1]);
                        if (seekbarLayout.getVisibility() == View.INVISIBLE && progress > 0) {
                            Animation animation = AnimationUtils.loadAnimation(MusicDetailsActivity.this, R.anim.music_seekbar_in);
                            seekbarLayout.startAnimation(animation);
                            seekbarLayout.setVisibility(View.VISIBLE);

                            totalTime = selectedMusic.getDuration() / 1000;
                            seekBar.setMax(totalTime);
                            musicTotalTime = DateUtils.getTimeShow(totalTime);
                            showTimeGoing.setText("00:00");
                            showTimeTotal.setText( musicTotalTime);
                        }
                        if (progress > 0 && isSeeking == 0) {
                            seekBar.setProgress(progress);
                        }
                        if (isSeeking > 0) {
                            isSeeking = isSeeking - 1;
                        }


                        String status = content[2];
                        if ("true".equals(status)) {
                            isPlaying = true;
                            isPausing = false;
                        } else {
                            isPlaying = false;
                            isPausing = true;
                        }
                    }
                }

                if (msg.what == 1) {
                    Log.i("MusicViewActivity", "music stop play");
                    //音乐停止播放
                    isPlaying = false;
                    isPausing = false;

                    seekBar.setProgress(0);
                    Animation animation = AnimationUtils.loadAnimation(MusicDetailsActivity.this, R.anim.music_seekbar_out);
                    seekbarLayout.startAnimation(animation);
                    seekbarLayout.setVisibility(View.INVISIBLE);
                }
            }
        };
    }

    private void initialEvents() {

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentime = DateUtils.getTimeShow(seekBar.getProgress());
                showTimeGoing.setText(currentime);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ClientSendCommandService.msg = "music:seekto:" + seekBar.getProgress();
                ClientSendCommandService.handler.sendEmptyMessage(4);

                currentime = DateUtils.getTimeShow(seekBar.getProgress() / 1000);
                isPlaying = true;
                isPausing = false;
                isSeeking = 2;
            }
        });

        playImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touYing(selectedMusic.getPath(), selectedMusic.getTitle(), selectedMusic.getArtist());
            }
        });

        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                if (isPausing) {
                    ClientSendCommandService.msg = "music:start";
                    ClientSendCommandService.handler.sendEmptyMessage(4);
                    isPausing = false;
                    isPlaying = true;

                    controlButton.setBackground(getResources().getDrawable(R.drawable.control_pause));
                } else {
                    ClientSendCommandService.msg = "music:stop";
                    ClientSendCommandService.handler.sendEmptyMessage(4);
                    isPausing = true;
                    isPlaying = false;

                    controlButton.setBackground(getResources().getDrawable(R.drawable.control_play));
                }
            }
        });

        returnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                finish();
            }
        });

        /**
         * 视频投影音量控制
         */
        volUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:volumeup";
                ClientSendCommandService.handler.sendEmptyMessage(4);
            }
        });
        volDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:volumedown";
                ClientSendCommandService.handler.sendEmptyMessage(4);
            }
        });
    }

    private void touYing(String musicPath, String musicName, String artist) {
        try {
            if (NetworkUtils.isWifiConnected(MusicDetailsActivity.this)) {
                if (ClientSendCommandService.serverIpList.isEmpty()) {
                    Toast.makeText(MusicDetailsActivity.this, "手机未连接电视，请确认后再投影", Toast.LENGTH_SHORT).show();
                    return;
                }

                MyApplication.vibrator.vibrate(100);

                /**
                 * 设置播放滚动条的状态
                 */
                if (seekbarLayout.getVisibility() == View.INVISIBLE) {
                    seekbarLayout.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(MusicDetailsActivity.this, R.anim.music_seekbar_in);
                    seekbarLayout.startAnimation(animation);
                }

                totalTime = selectedMusic.getDuration() / 1000;
                seekBar.setMax(totalTime);
                musicTotalTime = DateUtils.getTimeShow(totalTime);
                showTimeGoing.setText("00:00");
                showTimeTotal.setText(musicTotalTime);
                seekBar.setProgress(0);

                /**
                 * 开始投影播放
                 */
                String musicSelectedPath = null;
                if (musicPath.startsWith(NanoHTTPDService.defaultHttpServerPath)) {
                    musicSelectedPath = musicPath.replace(NanoHTTPDService.defaultHttpServerPath, "").replace(" ", "%20");
                } else {
                    for (String otherHttpServerPath : NanoHTTPDService.otherHttpServerPaths) {
                        if (musicPath.startsWith(otherHttpServerPath)) {
                            musicSelectedPath = musicPath.replace(otherHttpServerPath, "").replace(" ", "%20");
                        }
                    }
                }

                String ipAddress = NetworkUtils.getLocalHostIp();

                String httpAddress = "http://" + ipAddress + ":" + NanoHTTPDService.HTTP_PORT;
                String musicHttpAddress = httpAddress + musicSelectedPath.replace(" ", "%20");
                JSONObject o = new JSONObject();
                o.put("music_play", "music_play");
                o.put("path", musicHttpAddress);
                o.put("musicName", musicName);
                o.put("artist", artist);

                try {
                    MusicLrc lrc = musicService.findMusicLrc(artist, musicName);
                    if (lrc != null) {
                        String lrcPath = lrc.getPath();
                        String lrcHttpAddress = null;
                        if (lrcPath.startsWith(NanoHTTPDService.defaultHttpServerPath)) {
                            lrcHttpAddress = lrcPath.replace(NanoHTTPDService.defaultHttpServerPath, "").replace(" ", "%20");
                        } else {
                            for (String otherHttpServerPath : NanoHTTPDService.otherHttpServerPaths) {
                                if (lrcPath.startsWith(otherHttpServerPath)) {
                                    lrcHttpAddress = musicPath.replace(otherHttpServerPath, "").replace(" ", "%20");
                                }
                            }
                        }
                        o.put("musicLrcPath", httpAddress + lrcHttpAddress.replace(" ", "%20"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /**
                 * 有时候用户在进入投影页面，但是确没有投影动作，http服务关闭，但是用户现在点击投影，所以这里需要先检查有没有HTTP服务
                 */
                if (NanoHTTPDService.httpServer == null) {
                    Intent http = new Intent(MusicDetailsActivity.this, NanoHTTPDService.class);
                    startService(http);

                    //Sleep 1s is used for let http service started fully
                    SystemClock.sleep(1000);
                }

                //发送播放地址
                ClientSendCommandService.msg = o.toString();
                ClientSendCommandService.handler.sendEmptyMessage(4);

                //HTTPD的使用状态
                MobilePerformanceUtils.openPerformance(MusicDetailsActivity.this);
            } else {
                Toast.makeText(MusicDetailsActivity.this, "请链接无线网络", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MusicDetailsActivity.this, "歌曲获取出错", Toast.LENGTH_SHORT).show();
        }
    }
}