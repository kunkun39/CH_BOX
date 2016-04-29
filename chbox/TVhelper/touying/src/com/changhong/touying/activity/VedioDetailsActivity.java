package com.changhong.touying.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.*;
import com.changhong.common.widgets.IpSelectorDataServer;
import com.changhong.touying.nanohttpd.NanoHTTPDService;
import com.changhong.touying.vedio.Vedio;
import com.changhong.touying.R;

import java.util.concurrent.Executors;


/**
 * Created by Jack Wang
 */
public class VedioDetailsActivity extends Activity implements QuickQuireMessageUtil.OnFeedBackListener{

    /**
     * 消息处理
     */
    public static Handler handler;

    /**
     * 被选中的视频文件
     */
    private Vedio selectedVedio;

    /**
     * 视频图
     */
    private ImageView vedioImage;

    /**
     * 视频名称
     */
    private TextView vedioName;

    /**
     * 视频创建时间
     */
    private TextView vedioTime;

    /**
     * 视频类型
     */
    private TextView vedioType;

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
    private SeekBar seekBar;  //视频播放进度
    private SeekBar volBar;   //音频播放进度

    /**
     * 时间显示信息
     */
    private TextView showTimeGoing;
    private TextView showTimeTotal;

    /**
     * 影片的时间长度
     */
    private String vedioTotalTime;

    /**
     * 影片的时间长度
     */
    private int totalTime;

    /**
     * SeekBar
     */
    private String currentime;

    /**
     * 判断是否正在播放，可以用来防止用户连续点击播放按钮，导致系统创建线程
     */
    private boolean isPlaying = false;

    /**
     * 是否为暂停状态
     */
    private boolean isPausing = false;

    private boolean isWantoPlay = false;

    /**
     * 是否拖动过，因为，拖动后，手机端的进度条刚更新后，服务端还未跟新就已经发送的进度消息，手机端会返回拖动前状态
     * 一旦发生拖动，就手机缓冲2秒再更新进度条
     */
    private int isSeeking = 0;

    /**
     * 播放时显示进度条，停止时隐藏进度条
     */
    //private RelativeLayout seekbarLayout;

    /**
     * 暂停按钮
     */
    private ImageView controlButton;

    /**
     * 音量控制按钮
     */
    private ImageView volUpBtn;
    private ImageView volDownBtn;

    Runnable mRunableDelayPlay = new Runnable() {
        @Override
        public void run() {
            if (isWantoPlay){
                showOnTV();
            }
        }
    };

    int volCurrent = 0;
    int volMax = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        selectedVedio = (Vedio) intent.getSerializableExtra("selectedVedio");
        QuickQuireMessageUtil.getInstance().setFeedbackListener(this,this);
        Utils.requireServerVolume(this);
        initialViews();

        initialEvents();
    }

    private void initialViews() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_vedio_details_das);

        vedioTotalTime = DateUtils.getTimeShow(selectedVedio.getDuration() / 1000);
        totalTime = ((Long) (selectedVedio.getDuration() / 1000)).intValue();

        vedioImage = (ImageView) findViewById(R.id.details_image);
        vedioImage.setImageBitmap(ThumbnailUtils.createVideoThumbnail(selectedVedio.getPath(), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND));

        vedioName = (TextView) findViewById(R.id.vedio_name);
        String displayName = StringUtils.hasLength(selectedVedio.getDisplayName()) ? selectedVedio.getDisplayName() : selectedVedio.getTitle();
        displayName = displayName.substring(0,displayName.lastIndexOf("."));
        vedioName.setText(displayName);

        returnImage = (ImageView) findViewById(R.id.d_btn_return);
        playImage = (ImageView) findViewById(R.id.vedio_control_button);

        seekBar = (SeekBar) findViewById(R.id.play_seekbar);
        seekBar.setMax(totalTime);
        volBar = (SeekBar)findViewById(R.id.vedio_seek_volue);
        showTimeGoing = (TextView) findViewById(R.id.vedio_showtime_going);
        showTimeGoing.setText("00:00");
        showTimeTotal = (TextView) findViewById(R.id.vedio_showtime_total);
        showTimeTotal.setText(vedioTotalTime);

        //seekbarLayout = (RelativeLayout) findViewById(R.id.vedio_seek_layout);
        controlButton = (ImageView) findViewById(R.id.vedio_control_button);
        controlButton.setBackground(getResources().getDrawable(R.drawable.control_play1));
        volDownBtn = (ImageView) findViewById(R.id.control_volume_small);
        volUpBtn= (ImageView) findViewById(R.id.control_volume_bigger);

        handler = new Handler() {
            public void handleMessage(Message msg) {
                handler.removeCallbacks(mRunableDelayPlay);
                //服务端消息的处理消息的处理
                if (msg.what == 0) {
                    //HTTPD的使用状态
                    MobilePerformanceUtils.httpServerUsing = true;

                    String[] content = StringUtils.delimitedListToStringArray(((String) msg.obj), "|");
                    //有可能出现空指向错误，
                    if (content!=null&&content.length>0&&selectedVedio!=null &&
                    		selectedVedio.getDisplayName()!=null && selectedVedio.getDisplayName().equals(WebUtils.convertHttpURLToLocalFile(content[0]))) {
                          /**
                         * set the play ui and play process
                         */
                        int progress = Integer.parseInt(content[1]);
//                        if (seekbarLayout.getVisibility() == View.INVISIBLE && progress > 0) {
//                            seekbarLayout.setVisibility(View.VISIBLE);
//                            Animation animation = AnimationUtils.loadAnimation(VedioDetailsActivity.this, R.anim.music_seekbar_in);
//                            seekbarLayout.startAnimation(animation);
//                        }
                        Log.d("VEDIO", "progress:" + progress + "-seeking:" + isSeeking);
                        if (progress > 0 && isSeeking == 0) {
                            seekBar.setProgress(progress);
                        }
                        if (isSeeking > 0) {
                            isSeeking = isSeeking - 1;
                        }

                        if (isWantoPlay){
                            doPlay();
                        }else {
                            /**
                             * set play status
                             */
                            String status = content[2];
                            if ("true".equals(status)) {
                                isPlaying = true;
                                isPausing = false;
                            } else {
                                isPlaying = false;
                                isPausing = true;
                            }
//                            if (isPlaying){
//                                controlButton.setBackground(getResources().getDrawable(R.drawable.control_pause1));
//                            }else {
//                                controlButton.setBackground(getResources().getDrawable(R.drawable.control_play1));
//                            }
                        }
                    }else if (isWantoPlay){
                        showOnTV();
                    }
                }


                if (msg.what == 1) {
                    Log.i("VedioDetailsActivity", "video stop play");
                    //进度条的更新
                    isPlaying = false;
                    isPausing = false;

                    seekBar.setProgress(0);
//                    seekbarLayout.setVisibility(View.INVISIBLE);
//                    Animation animation = AnimationUtils.loadAnimation(VedioDetailsActivity.this, R.anim.music_seekbar_out);
//                    seekbarLayout.startAnimation(animation);
                }
                isWantoPlay = false;
            }
        };
    }
    public boolean OnKeyPress(int keyCode, KeyEvent event)
    {
    	if (!isPlaying) {
			return false;
		}
    	
    	switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			ClientSendCommandService.sendMessage("key:volumedown");				
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			ClientSendCommandService.sendMessage("key:volumeup");		
			return true;

		default:
			break;
		}
    	
    	return false;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (!isPlaying) {
			return super.onKeyDown(keyCode, event);
		}
    	
    	switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			ClientSendCommandService.sendMessage("key:volumedown");
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			ClientSendCommandService.sendMessage("key:volumeup");
			return true;

		default:
			break;
		}
    	return super.onKeyDown(keyCode, event);
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
                ClientSendCommandService.msg = "vedio:seekto:" + seekBar.getProgress();
                ClientSendCommandService.handler.sendEmptyMessage(4);

                currentime = DateUtils.getTimeShow(seekBar.getProgress());
                isPlaying = true;
                isPausing = false;
                isSeeking = 2;
            }

        });

        playImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }

        });

        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                if (isPlaying == false){
                    isWantoPlay = true;
                    controlButton.setBackground(getResources().getDrawable(R.drawable.control_pause1));
                    handler.removeCallbacks(mRunableDelayPlay);
                    handler.postDelayed(mRunableDelayPlay,1000);
                }else{
                    if (isPausing) {
                        doPlay();
                    } else {
                        doStop();
                    }
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

    @Override
    public void onFinish(QuickQuireMessageUtil vervify, Object result) {
        if (result == null
                || !(result instanceof String)
                || ((String)result).isEmpty()
                || ((String)result).indexOf("/") == -1){
            return;
        }
        String value = (String)result;

       volCurrent = Integer.parseInt(value.substring(0,value.indexOf("/")));
       volMax  = Integer.parseInt(value.substring(value.indexOf("/") + 1,value.length()));
        volBar.setMax(volMax);
        volBar.setProgress(volCurrent);
    }

    private void showOnTV(){
        try {
            if (NetworkUtils.isWifiConnected(VedioDetailsActivity.this)) {
                if ( !StringUtils.hasLength(IpSelectorDataServer.getInstance().getCurrentIp())) {
                    Toast.makeText(VedioDetailsActivity.this, getResources().getString(R.string.phone_disconnect), Toast.LENGTH_SHORT).show();
                    return;
                }

                MyApplication.vibrator.vibrate(100);

                /**
                 * 1 - 如果当前状态为暂停，点击播放，就继续播放
                 * 2 - 如果当前状态没有暂停，开始播放新的
                 */
//                        if (seekbarLayout.getVisibility() == View.INVISIBLE) {
//                            seekbarLayout.setVisibility(View.VISIBLE);
//                            Animation animation = AnimationUtils.loadAnimation(VedioDetailsActivity.this, R.anim.music_seekbar_in);
//                            seekbarLayout.startAnimation(animation);
//                        }
                seekBar.setProgress(0);
                isPlaying = true;
                isPausing = false;

                /**
                 * 开始投影播放
                 */
                String vedioPath = selectedVedio.getPath();
                String vedioSelectedPath = null;

                if (vedioPath.startsWith(NanoHTTPDService.defaultHttpServerPath)) {
                    vedioSelectedPath = vedioPath.replace(NanoHTTPDService.defaultHttpServerPath, "");
                } else {
                    for (String otherHttpServerPath : NanoHTTPDService.otherHttpServerPaths) {
                        if (vedioPath.startsWith(otherHttpServerPath)) {
                            vedioSelectedPath = vedioPath.replace(otherHttpServerPath, "");
                        }
                    }
                }

                String ipAddress = NetworkUtils.getLocalHostIp();
                String httpAddress = "http://" + ipAddress + ":" + NanoHTTPDService.HTTP_PORT;
                httpAddress = httpAddress + WebUtils.convertLocalFileToHttpURL(vedioSelectedPath);

                /**
                 * 有时候用户在进入投影页面，但是确没有投影动作，http服务关闭，但是用户现在点击投影，所以这里需要先检查有没有HTTP服务
                 */
                if (NanoHTTPDService.httpServer == null) {
                    Intent http = new Intent(VedioDetailsActivity.this, NanoHTTPDService.class);
                    startService(http);

                    //Sleep 1s is used for let http service started fully
                    SystemClock.sleep(1000);
                }

                //发送播放地址
                ClientSendCommandService.msg = httpAddress;
                ClientSendCommandService.handler.sendEmptyMessage(4);

                //HTTPD的使用状态
                MobilePerformanceUtils.openPerformance(VedioDetailsActivity.this);
            } else {
                Toast.makeText(VedioDetailsActivity.this, getResources().getString(R.string.connect_wifi), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(VedioDetailsActivity.this, getResources().getString(R.string.get_video_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void doPlay(){
        ClientSendCommandService.msg = "vedio:start";
        ClientSendCommandService.handler.sendEmptyMessage(4);

        isPlaying = true;
        isPausing = false;

        controlButton.setBackground(getResources().getDrawable(R.drawable.control_pause1));
    }

    private void doStop(){
        ClientSendCommandService.msg = "vedio:stop";
        ClientSendCommandService.handler.sendEmptyMessage(4);

        isPlaying = false;
        isPausing = true;

        controlButton.setBackground(getResources().getDrawable(R.drawable.control_play1));
    }
}