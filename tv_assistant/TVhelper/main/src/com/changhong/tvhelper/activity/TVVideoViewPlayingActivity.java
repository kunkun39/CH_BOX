package com.changhong.tvhelper.activity;

import com.changhong.common.utils.MobilePerformanceUtils;
import com.changhong.tvhelper.R;

import com.baidu.cyberplayer.core.BMediaController;
import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class TVVideoViewPlayingActivity extends Activity implements OnPreparedListener,
							OnCompletionListener,
							OnErrorListener, 
							OnInfoListener,
							OnPlayingBufferCacheListener
							{
	private final String TAG = "VideoViewPlayingActivity";
	
	/**
	 * 您的ak 
	 */
	private String AK = "GcB3uqcVvjzEtbsV8lxBDQ8d";
	/**
	 * //您的sk的前16位
	 */
	private String SK = "jshPsRMEXDTah1rqYO6qLilGkuFrFYKG";
	
	private String mVideoSource = null;
	
	private BVideoView mVV = null;
	private BMediaController mVVCtl = null;
	private RelativeLayout mViewHolder = null;
	private LinearLayout mControllerHolder = null;
	
	private boolean mIsHwDecode = false;
	
	private EventHandler mEventHandler;
	private HandlerThread mHandlerThread;
	
	private final Object SYNC_Playing = new Object();
		
	private final int EVENT_PLAY = 0;
	
	private static final String POWER_LOCK = "VideoViewPlayingActivity";
	
	/**
	 * 播放状态
	 */
	private  enum PLAYER_STATUS {
		PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
	}
	
	private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
	private int stat=-1;
	
	/**
	 * 记录播放位置
	 */
	private int mLastPos = 0;

	class EventHandler extends Handler {
		public EventHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EVENT_PLAY:
				/**
				 * 如果已经播放了，等待上一次播放结束
				 */
				if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
					synchronized (SYNC_Playing) {
						try {
							SYNC_Playing.wait();
							
							Log.v(TAG, "wait player status to idle");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				/**
				 * 设置播放url
				 */
				Log.v(TAG, mVideoSource);
				mVV.setVideoPath(mVideoSource);
				//mVV.setCacheBufferSize(1*1024*1024);
				/**
				 * 续播，如果需要如此
				 */
				if (mLastPos > 0) {

					mVV.seekTo(mLastPos);
					mLastPos = 0;
				}

				/**
				 * 显示或者隐藏缓冲提示 
				 */
				mVV.showCacheInfo(true);
				
				/**
				 * 开始播放
				 */
				//mVV.seekTo(0);
				mVV.start();

				mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
				break;
			default:
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
			/**
			 * 如果已经开发播放，先停止播放
			 */
			if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
				mVV.stopPlayback();
			}
			
			/**
			 * 发起一次新的播放任务
			 */
			if(mEventHandler.hasMessages(EVENT_PLAY))
				mEventHandler.removeMessages(EVENT_PLAY);
			mEventHandler.sendEmptyMessage(EVENT_PLAY);
		}
	};
	
	private View.OnClickListener mNextListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Log.v(TAG, "next btn clicked");
		}
	};
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
						
		setContentView(R.layout.activity_video_playing);
		
		mIsHwDecode = getIntent().getBooleanExtra("isHW", false);
		Uri uriPath = getIntent().getData();
		if (null != uriPath) {
			String scheme = uriPath.getScheme();
			if (null != scheme) {
				mVideoSource = uriPath.toString();
			} else {
				mVideoSource = uriPath.getPath();
			}
		}
		
		initUI();
		
		/**
		 * 开启后台事件处理线程
		 */
		mHandlerThread = new HandlerThread("event handler thread", Process.THREAD_PRIORITY_BACKGROUND);
		mHandlerThread.start();
		mEventHandler = new EventHandler(mHandlerThread.getLooper());
	}
	
	/**
	 * 初始化界面
	 */
	private void initUI() {		
		mViewHolder = (RelativeLayout)findViewById(R.id.view_holder);
		mControllerHolder = (LinearLayout )findViewById(R.id.controller_holder);
		
		/**
		 * 设置ak及sk的前16位
		 */
		BVideoView.setAKSK(AK, SK);
		
		/**
		 *创建BVideoView和BMediaController
		 */
		mVV = new BVideoView(this);
		mVVCtl = new BMediaController(this);
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
		mVV.setDecodeMode(BVideoView.DECODE_SW);
	}

	@Override
	protected void onPause() {
		super.onPause();
		stat=1;
		Log.v(TAG, "onPause");
		/**
		 * 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
		 */
		if (mPlayerStatus == PLAYER_STATUS.PLAYER_PREPARED) {
			mLastPos = mVV.getCurrentPosition();
			mVV.stopPlayback();
		}
		onDestroy();
	}

	
	@Override
	protected void onResume() {
		super.onResume();

		/**
		 * 发起一次播放任务,当然您不一定要在这发起
		 */
		mEventHandler.sendEmptyMessage(EVENT_PLAY);	
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		
		mHandlerThread.quit();
		Log.v(TAG, "onStop");
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onDestroy(){
		super.onDestroy();
		/**
		 * 结束后台事件处理线程
		 */
		mHandlerThread.quit();
		Log.v(TAG, "onDestroy");
	}

	@Override
	public boolean onInfo(int what, int extra) {
		switch(what){
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
		synchronized (SYNC_Playing) {
			SYNC_Playing.notify();
		}
		mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
		return true;
	}

	/**
	 * 播放完成
	 */
	@Override
	public void onCompletion() {
		Log.v(TAG, "onCompletion");
		
		synchronized (SYNC_Playing) {
			SYNC_Playing.notify();
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
