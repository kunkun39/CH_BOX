package com.changhong.tvhelper.activity;

import android.app.ProgressDialog;
import android.os.*;
import android.os.Process;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import com.changhong.common.utils.StringUtils;
import com.changhong.tvhelper.R;

import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.widget.RelativeLayout;
import com.changhong.tvhelper.domain.Program;

public class TVVideoViewPlayingActivity extends Activity implements OnPreparedListener,
							OnCompletionListener,
							OnErrorListener, 
							OnInfoListener,
							OnPlayingBufferCacheListener {

	private final String TAG = "VideoViewPlayingActivity";
	
	/**
	 * 您的ak 
	 */
	private String AK = "GcB3uqcVvjzEtbsV8lxBDQ8d";
	private String SK = "jshPsRMEXDTah1rqYO6qLilGkuFrFYKG";
	
	private BVideoView mVV = null;
	private RelativeLayout mViewHolder = null;

    /**
     * 播放状态
     */
	private final Object SYNC_Playing = new Object();
	private  enum PLAYER_STATUS {
		PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
	}
	private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
	private int stat=-1;

    /**
     * 播放的路径
     */
    public static String name = null;
    public static String path = null;

    /**
     * 播放和退出时间戳
     */
    private long playTimestamp = 0l;
    private long backTimestamp = 0l;
    private int returnConfirm = 1;

    /**
     * 定时隐藏
     */
    private Handler mDismissHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    Toast.makeText(TVVideoViewPlayingActivity.this, R.string.play_outoftime, 3000).show();
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_playing);

        /**
         * 获得播放的路径和节目的名称
         */
        if (!StringUtils.hasLength(path)) {
            finish();
            return;
        }

        /**
         * 初始化UI
         */
        initUI();

        /**
         * 开始后台监视线程
         */
        playTimestamp = System.currentTimeMillis();
        new PlayerIsPlayingMinitorThread().start();
	}
	
	/**
	 * 初始化界面
	 */
	private void initUI() {
        mViewHolder = (RelativeLayout)findViewById(R.id.view_holder);

		/**
		 * 设置ak及sk的前16位
		 */
		BVideoView.setAKSK(AK, SK);
		
		/**
		 *创建BVideoView和BMediaController
		 */
		mVV = new BVideoView(this);
        mVV.setVideoScalingMode(BVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        mVV.setDecodeMode(BVideoView.DECODE_HW);
        mVV.setKeepScreenOn(true);
        mVV.showCacheInfo(true);
        mVV.setCacheBufferSize(512 * 1024);
        mVV.setVideoPath(path);
        mViewHolder.addView(mVV);
        Log.v(TAG, path);

		/**
		 * 注册listener
		 */
		mVV.setOnPreparedListener(this);
		mVV.setOnCompletionListener(this);
		mVV.setOnErrorListener(this);
		mVV.setOnInfoListener(this);
        mVV.setOnPlayingBufferCacheListener(this);

        /**
         * 开始播放
         */
        mVV.start();
        mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
	}



    /**********************************************百度和系统方法重载部分*************************************************/

	@Override
	protected void onPause() {
		super.onPause();
		stat=1;
		Log.v(TAG, "onPause");
		/**
		 * 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
		 */
		if (mPlayerStatus == PLAYER_STATUS.PLAYER_PREPARED) {
			mVV.stopPlayback();
		}
		onDestroy();
	}

	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		
		Log.v(TAG, "onStop");

        finish();
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onDestroy(){
		super.onDestroy();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (returnConfirm == 1) {
                    returnConfirm = 0;
                    backTimestamp = System.currentTimeMillis();
                    Toast.makeText(TVVideoViewPlayingActivity.this, R.string.press_again_to_exit, 1000).show();
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

    /**************************************后台播放监视线程*************************************************************/

    private class PlayerIsPlayingMinitorThread extends Thread {
        public void run() {
            while (true) {
                try {
                    Log.v(TAG, mPlayerStatus.name());

                    // 用户在10秒钟之内连续返回则退出，反则计数器归一
                    if ((System.currentTimeMillis() - backTimestamp) > 10000 && backTimestamp != 0l) {
                        returnConfirm = 1;
                        backTimestamp = 0l;
                    }

                    if (mPlayerStatus != null && mPlayerStatus == PLAYER_STATUS.PLAYER_PREPARED) {
                        // 播放中更新时间
                        playTimestamp = System.currentTimeMillis();
                    }

                    if ((System.currentTimeMillis() - playTimestamp) > 10000 && playTimestamp != 0l) {
                        // 超过8秒一直没有播放，退出播放界面
                        if (mDismissHandler != null) {
                            mDismissHandler.sendEmptyMessage(1);
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
}
