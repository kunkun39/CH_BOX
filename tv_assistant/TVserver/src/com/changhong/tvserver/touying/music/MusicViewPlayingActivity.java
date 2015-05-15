package com.changhong.tvserver.touying.music;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.cyberplayer.core.BMediaController;
import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;
import com.changhong.tvserver.MyApplication;
import com.changhong.tvserver.R;
import com.changhong.tvserver.TVSocketControllerService;
import com.changhong.tvserver.touying.music.lyc.BiaduLyricDownloadManager;
import com.changhong.tvserver.touying.music.lyc.LycView;
import com.changhong.tvserver.touying.music.lyc.MiniLyricDownloadManager;

public class MusicViewPlayingActivity extends Activity implements OnPreparedListener,
        OnCompletionListener,
        OnErrorListener,
        OnInfoListener,
        OnPlayingBufferCacheListener {

    private final String TAG = "MusicViewPlayingActivity";

    public final static String CMD_TAG = "music:";
	
	private final static String CMD_PAUSE = CMD_TAG + "pause";
	private final static String CMD_STOP = CMD_TAG + "stop";
	private final static String CMD_START = CMD_TAG + "start";
	private final static String CMD_PLAY = CMD_TAG + "play";
	private final static String CMD_SEEK = CMD_TAG + "seekto";
	private final static String CMD_AUTO = CMD_TAG + "auto";
	

		
    /**
     * 百度音乐需要设置的KEY
     */
    private String AK = "GcB3uqcVvjzEtbsV8lxBDQ8d";
    private String SK = "jshPsRMEXDTah1rqYO6qLilGkuFrFYKG";

    /**
     * 播放地址
     */
    private String mVideoSource = null;
    public static String playVeidoKey = null;

    /**
     * 播控控件的设置
     */
    public static BVideoView mVV = null;
    private BMediaController mVVCtl = null;
    private RelativeLayout mViewHolder = null;
    private LinearLayout mControllerHolder = null;
    private boolean mIsHwDecode = false;

    
    /**
     * 播放的信息处理
     */
    public static EventHandler mEventHandler;
    private HandlerThread mHandlerThread;

    /**
     * 播放状态
     */
    private enum PLAYER_STATUS {
        PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
    }

    private final Object SYNC_Playing = new Object();
    private final int EVENT_PLAY = 1;
    private final int EVENT_START = 2;
    private final int EVENT_STOP = 3;
    private final int EVENT_SEEKTO = 4;
    private final int EVENT_PAUSE = 5;
    private final int EVENT_AUTO = 6;

    
    private WakeLock mWakeLock = null;
    private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
    private int stat = 0;////-1; -1自动退出

    private boolean isInital = false;
    /**
     * 记录播放位置
     */
    private int mLastPos = 0;

    /**
     * 歌词显示部分
     */
    private String musicName;
    private String playlist;
    private String artist;
    private String musicLrcPath;
    private TextView textView;

    /**
     * 歌词下载和更新部分
     */
    private boolean isLyricDownloading = false;
    private LycView lyricView;
    private BiaduLyricDownloadManager baiduLyricDownloadManager;
    private MiniLyricDownloadManager miniLyricDownloadManager;

    /**
     * 歌词更新
     */
    private Timer lrcTimer = new Timer();
    private Handler lrcHandler;

    /**
     * 判断是否拖动以同步歌词
     */
    boolean isSeeked = false;       
    
    List<JsonMusicObject> objectsList = new ArrayList<JsonMusicObject>();

    /**
     * *******************************************初始化和包房*******************************************************
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_music_player);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);

        /**
         * 开启后台事件处理线程
         */
        mHandlerThread = new HandlerThread("event handler thread", Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mEventHandler = new EventHandler(mHandlerThread.getLooper());
        
        paserIntent();

        /**
         * 初始化播放器UI
         */
        initPlayUI();

        /**
         * 初始化播放歌词的UI
         */
        initLrcUI();
        
        initPlayer();
        
        updateUI();
        
        /**
         * 计时器更新歌曲
         */
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (mVV != null && mVV.isPlaying()) {
                    lrcHandler.sendEmptyMessage(0);
                }
            }
        };
        lrcTimer.schedule(timerTask, 0, 100);
        lrcHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (lyricView.blLrc) {
                    int curTime = mVV.getCurrentPosition();

                    int lrcIndex = lyricView.selectIndex(curTime * 1000);
                    lyricView.setOffsetY(lyricView.getOffsetY() - lyricView.speedLrc());

                    if (isSeeked) {
                        lyricView.setOffsetY(225 - lrcIndex * (lyricView.getSIZEWORD() + lyricView.INTERVAL));
                        isSeeked = false;
                    }

                    lyricView.invalidate();
                }
            }
        };
        isInital = true;
        synchronized (SYNC_Playing) {
            SYNC_Playing.notify();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

    	super.onNewIntent(intent);
    	
    	setIntent(intent);
    	
    	paserIntent();
    	
    	initPlayer();
    	
    	updateUI();
    }
	    
    /**
     *  解析数据
     */
    private void paserIntent()
    {
    	mIsHwDecode = getIntent().getBooleanExtra("isHW", false);
        Uri uriPath = getIntent().getData();
        objectsList.clear();
        playlist = null;
        
        if (null != uriPath) {
            try {
            	JSONObject  o =  (JSONObject)JSON.parse(uriPath.toString());                
                
            	if (o.containsKey("objects_list")) {  
            		if(o.containsKey("playlistName") )
            		{
            			playlist = o.getString("playlistName");
            		}
            	
            		String objString = o.getString("objects_list");
            		
            		 List<JSONObject> objects =  (List<JSONObject>) JSON.parse(objString);  
            		 for (JSONObject jsonObject : objects) {
            			 String musicPath = jsonObject.getString("musicPath");
            		    	String musicName = jsonObject.getString("musicName");
            		    	String artist = jsonObject.getString("artist");
            		    	String musicLrcPath = jsonObject.getString("musicLrcPath");
            		    	
            			 objectsList.add(new JsonMusicObject(musicPath, musicName, artist, musicLrcPath));
					}
            		 
            		//objectsList.addAll(JSON.parseObject(, new TypeReference<List<JsonMusicObject>>(){}));            		     		            		
                	
                	mVideoSource = objectsList.get(0).musicPath;
                    musicName = objectsList.get(0).musicName;
                    artist = objectsList.get(0).artist;
                    musicLrcPath = objectsList.get(0).musicLrcPath;
				}    
                else
                {
                	mVideoSource = o.getString("path");
                    musicName = o.getString("musicName");
                    artist = o.getString("artist");
                    musicLrcPath = o.getString("musicLrcPath");
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void initPlayer()
    {
    	Message message = mEventHandler.obtainMessage();
        message.what = 0;
        message.obj = CMD_START;
        
        mEventHandler.sendMessageAtFrontOfQueue(message);
    }
    
    private void updateUI()
    {
    	if (playlist != null) {
    		playVeidoKey = playlist + "-" + musicName + "-" + artist;
		}
    	else {
    		playVeidoKey = musicName + "-" + artist;
		}
    	    	
    	textView.setText(playVeidoKey);
    	
    	if (lyricView != null) 
    		lyricView.invalidate();
    	
    	/**
         * 加载歌词
         */
        loadLocalLyric();
    }

    /**
     * 初始化界面
     */
    private void initPlayUI() {
        mViewHolder = (RelativeLayout) findViewById(R.id.music_view_holder);
        mControllerHolder = (LinearLayout) findViewById(R.id.music_controller_holder);

        /**
         * 设置ak及sk的前16位
         */
        BVideoView.setAKSK(AK, SK);

        /**
         *创建BVideoView和BMediaController
         */
        mVV = new BVideoView(this);
        mVV.setVisibility(View.INVISIBLE);
        mVVCtl = new BMediaController(this) {
            @Override
            public void hide() {
            }
        };
        mViewHolder.addView(mVV);
        mControllerHolder.addView(mVVCtl);

        /**
         * 注册listener
         */
        mVV.setOnPreparedListener(this);
        mVV.setOnCompletionListener(this);
        mVV.setOnErrorListener(this);
        mVV.setOnInfoListener(this);
        mVVCtl.setPreNextListener(mPreListener, mNextListener);

        /**
         * 关联BMediaController
         */
        mVV.setMediaController(mVVCtl);
        /**
         * 设置解码模式
         */
        mVV.setDecodeMode(BVideoView.DECODE_HW);
    }

    private void initLrcUI() {
        textView = (TextView) findViewById(R.id.singer_and_name);
        
        /**
         * 播放歌词
         */
        lyricView = (LycView) findViewById(R.id.lrc_show);
        baiduLyricDownloadManager = new BiaduLyricDownloadManager();
        miniLyricDownloadManager = new MiniLyricDownloadManager();
        
    }

    /**
     * *******************************************歌词设置部分*******************************************************
     */

    private void loadLocalLyric() {
        String lyricFilePath = MyApplication.lrcPath + "/" + musicName + "-" + artist + ".lrc";
        File lyricfile = new File(lyricFilePath);
        
        lyricView.initDATAAndUI();
        
        if (lyricfile.exists()) {
            //加载歌词
            lyricView.read(lyricFilePath);
            lyricView.setTextSize();
            lyricView.setOffsetY(350);
        } else {
            loadLyricAuto();
        }
    }

    private void loadLyricAuto() {
        isLyricDownloading = true;
        new LyricDownloadAsyncTask().execute();
    }

    class LyricDownloadAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String lyricFilePath = baiduLyricDownloadManager.searchLyricFromWeb(musicLrcPath, musicName, artist);

            if (lyricFilePath == null || "".equals(lyricFilePath)) {
                lyricFilePath = miniLyricDownloadManager.searchLyricFromWeb(musicName, artist);
            }

            isLyricDownloading = false;
            return lyricFilePath;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                lyricView.read(result);
                lyricView.setTextSize();
                lyricView.setOffsetY(350);
            }
        }
    }

    ;

    /**
     * *******************************************播放器设置部分*******************************************************
     */

    public class EventHandler extends Handler {
        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override
       synchronized public void handleMessage(Message msg) {
            switch (msg.what) {
            case 0:
            {
            	
            	int nState = 0;
            	
            	String cmd = (String) msg.obj;
            	if (cmd.equals(CMD_START)) {
            		nState  = EVENT_START;
				}else if (cmd.equals(CMD_PLAY)) {
            		nState  = EVENT_PLAY;
				}else if (cmd.equals(CMD_PAUSE)) {
					nState  = EVENT_PAUSE;
				}
				else if( cmd.contains(CMD_SEEK)) {
					nState  = EVENT_SEEKTO;
				}
            	else if (cmd.contains(CMD_STOP)) {
            		nState  = EVENT_STOP;
				}
            	else if (cmd.contains(CMD_AUTO)) {
					nState = EVENT_AUTO;
				}

            	
            	/**
                 * 如果已经播放了，等待上一次播放结束
                 */
                /*if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE)*/ 
            	if(!isInital && nState == EVENT_START){
                    synchronized (SYNC_Playing) {
                        try {
                            SYNC_Playing.wait();
                            Log.v(TAG, "wait player status to idle");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            	String message;
            	
            	switch (nState) {
	            	case EVENT_START:
	                    
	                    /**
	                     * 设置播放url
	                     */
	            		if (mVideoSource == null) {
							return ;
						}
	                    Log.v(TAG, mVideoSource);
	                    mVV.setVideoPath(mVideoSource);
	                    //mVV.setCacheBufferSize(1*1024*1024);
	                    /**
	                     * 显示或者隐藏缓冲提示
	                     */
	                    mVV.showCacheInfo(true);
	                    /**
	                     * 开始播放
	                     */
	                    mVV.start();
	
	                    mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
	                    break;
	                case EVENT_PLAY:
	                    mVV.resume();
	                    break;
	                case EVENT_PAUSE:
	                	mVV.pause();
	                    break;
	                case EVENT_AUTO:
	                	message = (String) msg.obj;
	                    int isAuto = Integer.valueOf(message.split(":")[2]);
	                    if (isAuto == 0) {
	                    	stat = 0;
						}
	                    else {
	                    	stat = -1;
						}
	                	
	                	break;
	                case EVENT_STOP:	                	
	                    finish();
	                    break;
	                case EVENT_SEEKTO:
	                    message = (String) msg.obj;
	                    int currentPosition = Integer.valueOf(message.split(":")[2]);
	                    mVV.seekTo(currentPosition);
	
	                    /**
	                     * 标记为歌词不同步
	                     */
	                    isSeeked = true;
	                    break;
	                default:
	                    break;
	            
	            	}
	            }
	            break;
	            default:
	            {
	            	
	            }
	            break;
	        }
	    }
    }

    /**
     * 实现切换示例
     */
    private View.OnClickListener mPreListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.v(TAG, "pre btn clicked");
        }
    };

    private View.OnClickListener mNextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.v(TAG, "next btn clicked");
        }
    };

    /**
     * *******************************************系统方法重载部分*******************************************************
     */

    @Override
    protected void onPause() {
        super.onPause();
        stat = -1;
        Log.v(TAG, "onPause");
        /**
         * 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
         */
        if (mPlayerStatus == PLAYER_STATUS.PLAYER_PREPARED) {
            mLastPos = mVV.getCurrentPosition();
            mVV.stopPlayback();
        }       
    }


    @Override
    protected void onResume() {
        super.onResume();       
        
        Log.v(TAG, "onResume");
        if (null != mWakeLock && (!mWakeLock.isHeld())) {
            mWakeLock.acquire();
        }
        /**
         * 发起一次播放任务,当然您不一定要在这发起
         */
        Message message = mEventHandler.obtainMessage();
        message.what = 0;
        message.obj = CMD_PLAY;
        
        mEventHandler.sendMessage(message);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mHandlerThread.quit();
        Log.v(TAG, "onStop");
        
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 结束后台事件处理线程
         */
                
        mHandlerThread.quit();
        Log.v(TAG, "onDestroy");
    }

    @Override
    public boolean onInfo(int what, int extra) {
        switch (what) {
            /**
             * 开始缓冲
             */
            case BVideoView.MEDIA_INFO_BUFFERING_START:
                break;
            /**
             * 结束缓冲
             */
            case BVideoView.MEDIA_INFO_BUFFERING_END:
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * 当前缓冲的百分比， 可以配合onInfo中的开始缓冲和结束缓冲来显示百分比到界面
     */
    @Override
    public void onPlayingBufferCache(int percent) {

    }

    /**
     * 播放出错
     */
    @Override
    public boolean onError(int what, int extra) {
        Log.v(TAG, "onError");
        
        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        return true;
    }

    /**
     * 播放完成
     */
    @Override
    public void onCompletion() {
        Log.v(TAG, "onCompletion");

        /**
         * play complete, notify boardcast send command to client
         */
        TVSocketControllerService.STOP_PLAY_TAG = 2;

        
        if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
            /**
             * auto play finished stat = -1
             */
            if (stat == -1) {
            	if (objectsList.isEmpty()
            			|| mVideoSource.equals(objectsList.get(objectsList.size() - 1).musicPath)) {
            		finish();
				}
            	
            	boolean isFind = false;
            	for(JsonMusicObject object : objectsList)
            	{
            		if (isFind) {
            			mVideoSource = object.musicPath;
                        musicName = object.musicName;
                        artist = object.artist;
                        musicLrcPath = object.musicLrcPath;
                        initPlayer();
                        updateUI();
                        
                        Message message = mEventHandler.obtainMessage();
                        message.what = 0;
                        message.obj = CMD_PLAY;
                        
                        mEventHandler.sendMessage(message);
                        break;
					}
            		
            		if (object.musicPath.equals(mVideoSource)) {
            			isFind = true;            			                        
                        continue;
					}
            	}
                
            }
        }
        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
    }
    
	
    /**
     * 播放准备就绪
     */
    @Override
    public void onPrepared() {
        Log.v(TAG, "onPrepared");
        mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
    }

}
