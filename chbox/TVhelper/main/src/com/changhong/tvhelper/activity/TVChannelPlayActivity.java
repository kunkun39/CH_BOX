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

import io.vov.vitamio.MediaMetadataRetriever;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.widget.VideoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.db.sqlite.DatabaseContainer;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.utils.SystemUtils;
import com.changhong.common.widgets.VerticalSeekBar;
import com.changhong.thirdpart.sharesdk.ScreenShotView;
import com.changhong.thirdpart.sharesdk.util.L;
import com.changhong.thirdpart.sharesdk.util.ShareUtil;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.domain.Program;
import com.changhong.tvhelper.service.ChannelService;
import com.changhong.tvhelper.service.ClientGetCommandService;

public class TVChannelPlayActivity extends Activity {

	/**
	 * video play view
	 */
	public static VideoView mVideoView;
	// private MediaController controller;

    public static Handler handler;
    private int width, height;

	/**
	 * 
	 * 缓冲提示对话框
	 */
	private ProgressDialog dd = null;

	/**
	 * video play source
	 */
	public static String path = null;
	public static String name = null;
	public static int mm = 1;
	private String freq = "";

	/**
	 * 播放和退出时间戳
	 */
	private long playTimestamp = 0l;
	private long backTimestamp = 0l;

	/**
	 * 节目和频道操作部分
	 */
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
	 * 屏幕宽度
	 */
	private int screenHeight = 0;

	/**
	 * 
	 * 亮度条和音量条
	 */
	private VerticalSeekBar sound, bright;
	private RelativeLayout seekbarWidget;

	/**
	 * 收藏
	 */
	private TextView collection;
	private List<String> allShouChangChannel = new ArrayList<String>();
	private ChannelService channelService;
	 private List<Map<String, Object>> channelShowData = new ArrayList<Map<String, Object>>();
	private String channelServiceId="";
	 /**
	 * 
	 * 动画效果
	 */
	private AnimationSet PIInAnimationSet, PIOutAnimationSet,
			SKBInAnimationSet, SKBOutAnimationSet, channelListInAnimationSet,
			channelListOutAnimationSet;

	/**
	 * 频道列表
	 */
	private ListView channelList;
	private ChannelAdapter channelAdapter;
	private List<String> channelNames = new ArrayList<String>();
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
	 * 投到电视
	 */
	private ImageButton mBtn_tv;

	/**
	 * 再按返回确认退出标志爱
	 */
	private int returnConfirm = 1;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		this.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
			return;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// ȥ����Ϣ��

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		screenHeight = metric.heightPixels; // 屏幕高度（像素）

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        /**
         * handle，用于外部切换频道是使用
         */
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String channelname = (String) msg.obj;
                setPath(channelname);
                initProgramInfo(channelname);

                try {
                    if (!dd.isShowing()) {
                        dd.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

		initView();

		initEvent();

		registerMyReceiver();

		Intent intent = getIntent();
		if (intent.getStringExtra("channelname") != null
				&& !intent.getStringExtra("channelname").equals("")
				&& !intent.getStringExtra("channelname").equals("null")) {
			name = intent.getStringExtra("channelname");
		}

		initDialog();

		/**
		 * 设置VEDIO部分
		 */
		mVideoView = (VideoView) findViewById(R.id.surface_view);
        try {
            if (screenHeight >= 1080 && SystemUtils.getMaxCpuFreq() > SystemUtils.MIN_CPU_YINGJIE_FRE) {
                mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_MEDIUM);
                mVideoView.setHardwareDecoder(true);
            } else {
                mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW);
                mVideoView.setHardwareDecoder(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW);
            mVideoView.setHardwareDecoder(false);
        }
        mVideoView.setBufferSize(256 * 1024);
		mVideoView.setKeepScreenOn(true);
		mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
		if (path != null) {
			mVideoView.setVideoPath(path);
			initProgramInfo(name);
		}

		mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				return false;
			}
		});

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

		mVideoView.requestFocus();
		if (intent.getStringExtra("channelname") != null) {
			setPath(name);
		}

		playTimestamp = System.currentTimeMillis();
		new PlayerIsPlayingMinitorThread().start();
	}

	private void initView() {
		setContentView(R.layout.activity_channel_play);

		// 频道列表
		initTVChannel();
		channelAdapter = new ChannelAdapter(this);
		relativeLayout = (RelativeLayout) findViewById(R.id.channel_list_layout);
		channelList = (ListView) findViewById(R.id.channel_list);
		channelList.setAdapter(channelAdapter);

		// 节目信息
		textCurrentProgramInfo = (TextView) findViewById(R.id.text_current_program_info);
		textNextProgramInfo = (TextView) findViewById(R.id.text_next_program_info);
		textChannelName = (TextView) findViewById(R.id.text_channel_name);
		programInfoLayout = (RelativeLayout) findViewById(R.id.program_info_layout);
		imageViewChannelLogo = (ImageView) findViewById(R.id.play_channel_logo);

		/**
		 * 
		 * 音量条和亮度条
		 */
		seekbarWidget = (RelativeLayout) findViewById(R.id.seekbarWidget);
		sound = (VerticalSeekBar) findViewById(R.id.sound);
		collection=(TextView)findViewById(R.id.play_collection);
		mBtn_tv = (ImageButton)findViewById(R.id.bt_tv);
		
		initCollectionData();
		initshareView();//分享view
	}

	private void initCollectionData(){
		channelService = new ChannelService(this);
		channelShowData.clear();
        channelShowData.addAll(ClientSendCommandService.channelData);
		new Thread(new Runnable() {
            @Override
            public void run() {
                /**
                 * 初始化DB
                 */

                try {
                    allShouChangChannel = channelService.getAllChannelShouCangs();
                    updateShouChangView(!allShouChangChannel.contains(channelServiceId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
	}
	
	private void initEvent() {
		/**
		 * 
		 * 音量调节
		 */
		sound.setMax(mMaxVolume);
		mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		sound.setProgress(mVolume);
		sound.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (progress < 1) {
					progress = 1;
				}
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
			}
		});

		//收藏监听器
		collection.setOnClickListener(new OnClickListener() {
			
			@Override
            public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
              try {
            	  if (allShouChangChannel.contains(channelServiceId)) {
                      //取消收藏操作
                      boolean success = channelService.cancelChannelShouCang(channelServiceId);
                      if (success) {
                          allShouChangChannel.remove(channelServiceId);
                          Toast.makeText(TVChannelPlayActivity.this, "取消频道收藏成功", Toast.LENGTH_SHORT).show();
                      } else {
                          Toast.makeText(TVChannelPlayActivity.this, "取消频道收藏失败", Toast.LENGTH_SHORT).show();
                      }
                      updateShouChangView(!allShouChangChannel.contains(channelServiceId));
                  } else {
                      //收藏操作
                      boolean success = channelService.channelShouCang(channelServiceId);
                      if (success) {
                    	  allShouChangChannel.add(channelServiceId);
                          Toast.makeText(TVChannelPlayActivity.this, "频道收藏成功", Toast.LENGTH_SHORT).show();
                      } else {
                          Toast.makeText(TVChannelPlayActivity.this, "频道收藏失败", Toast.LENGTH_SHORT).show();
                      }
                      updateShouChangView(!allShouChangChannel.contains(channelServiceId));
                  }
                  
              } catch (Exception e) {
                  e.printStackTrace();
              }
            }
        });
		mBtn_tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:dtv";
                ClientSendCommandService.handler.sendEmptyMessage(1);
               
                String serviceId = null;
                String tsId = null;
                String orgNId = null;
                
                if (!ClientSendCommandService.channelData.isEmpty()) {

    				for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
    					Map<String, Object> map = ClientSendCommandService.channelData.get(i);
    					if (name.equals((String) map.get("service_name"))) {
    						serviceId = (String) map.get("service_id");
    		                tsId = (String) map.get("tsId");
    		                orgNId = (String) map.get("orgNId");
    		                break;
    					}
    				}

                ClientSendCommandService.msgSwitchChannel = serviceId + "#" + tsId + "#" + orgNId;
                ClientSendCommandService.handler.sendEmptyMessage(3);
			}
		}
		});

	}

	private void initDialog() {
        try {
            dd = new ProgressDialog(TVChannelPlayActivity.this);
            dd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dd.setMessage("正在拼命为您加载视频数据...");
            dd.setCanceledOnTouchOutside(false);
            dd.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode,
                                     KeyEvent event) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private void setWidgetVisible(int i) {
		setMyAnimation(i);
		relativeLayout.setVisibility(i);
		programInfoLayout.setVisibility(i);
		seekbarWidget.setVisibility(i);

	}

	private void setMyAnimation(int i) {
		initAnimation();
		if (i == View.VISIBLE) {
			relativeLayout.startAnimation(channelListInAnimationSet);
			programInfoLayout.startAnimation(PIInAnimationSet);
			seekbarWidget.startAnimation(SKBInAnimationSet);
			menuKey = true;

		} else {
			relativeLayout.startAnimation(channelListOutAnimationSet);
			programInfoLayout.startAnimation(PIOutAnimationSet);
			seekbarWidget.startAnimation(SKBOutAnimationSet);
			menuKey = false;
		}
	}

	private void initAnimation() {

		if (null == channelListInAnimationSet) {
			channelListInAnimationSet = new AnimationSet(true);
			TranslateAnimation CLIAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, -1f,
					Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
					0f, Animation.RELATIVE_TO_SELF, 0f);
			CLIAnimation.setDuration(500);
			channelListInAnimationSet.addAnimation(CLIAnimation);
		}
		if (null == channelListOutAnimationSet) {
			channelListOutAnimationSet = new AnimationSet(true);
			TranslateAnimation CLOAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
					-1f, Animation.RELATIVE_TO_SELF, 0f,
					Animation.RELATIVE_TO_SELF, 0f);
			CLOAnimation.setDuration(500);
			channelListOutAnimationSet.addAnimation(CLOAnimation);
		}
		if (null == PIInAnimationSet) {
			PIInAnimationSet = new AnimationSet(true);
			TranslateAnimation PIIAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
					0f, Animation.RELATIVE_TO_SELF, 1f,
					Animation.RELATIVE_TO_SELF, 0f);
			PIIAnimation.setDuration(500);
			PIInAnimationSet.addAnimation(PIIAnimation);
		}
		if (null == PIOutAnimationSet) {
			PIOutAnimationSet = new AnimationSet(true);
			TranslateAnimation PIOAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
					0f, Animation.RELATIVE_TO_SELF, 0f,
					Animation.RELATIVE_TO_SELF, 1f);
			PIOAnimation.setDuration(500);
			PIOutAnimationSet.addAnimation(PIOAnimation);
		}

		if (null == SKBInAnimationSet) {
			SKBInAnimationSet = new AnimationSet(true);
			TranslateAnimation SKBIAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF,
					0f, Animation.RELATIVE_TO_SELF, 0f,
					Animation.RELATIVE_TO_SELF, 0f);
			SKBIAnimation.setDuration(500);
			SKBInAnimationSet.addAnimation(SKBIAnimation);
		}

		if (null == SKBOutAnimationSet) {
			SKBOutAnimationSet = new AnimationSet(true);
			TranslateAnimation SKBOAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
					1f, Animation.RELATIVE_TO_SELF, 0f,
					Animation.RELATIVE_TO_SELF, 0f);
			SKBOAnimation.setDuration(500);
			SKBOutAnimationSet.addAnimation(SKBOAnimation);
		}

	}

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
                try {
                    if (dd.isShowing()) {
                        dd.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(TVChannelPlayActivity.this, "播放超时，退出播放！！！", 3000).show();
				break;
			case 3:
				if (programList != null && programList.size() > 0) {
					try {
						Program currentProgram = programList.get(0);
						textCurrentProgramInfo.setText("当前节目" + ":"
								+ currentProgram.getProgramName() + "  "
								+ currentProgram.getProgramStartTime() + "-"
								+ currentProgram.getProgramEndTime());
					} catch (Exception e) {
						textCurrentProgramInfo.setText("当前节目" + ":" + "无节目信息");
					}
					try {
						Program nextProgram = programList.get(1);
						textNextProgramInfo.setText("下一节目" + ":"
								+ nextProgram.getProgramName() + "  "
								+ nextProgram.getProgramStartTime() + "-"
								+ nextProgram.getProgramEndTime());
					} catch (Exception e) {
						textNextProgramInfo.setText("下一节目" + ":" + "无节目信息");
					}
				} else {
					textCurrentProgramInfo.setText("当前节目" + ":" + "无节目信息");
					textNextProgramInfo.setText("下一节目" + ":" + "无节目信息");
				}
				break;
			case 4:
				if (menuKey) {
					setWidgetVisible(View.GONE);
				}
				break;
			default:
				break;
			}
		}
	};

	// 根据频道名称获得节目信息
	private void initProgramInfo(String channelPlayName) {
		try {
			if (!ClientSendCommandService.channelData.isEmpty()) {

				for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
					Map<String, Object> map = ClientSendCommandService.channelData.get(i);

					if (channelPlayName.equals((String) map.get("service_name"))) {
						// 获得节目信息
						name = (String) map.get("service_name");
						channelIndex = (String) map.get("name");
						setWidgetVisible(View.VISIBLE);

						textChannelName.setText(channelPlayName);

						// 设置台标
						try {
							imageViewChannelLogo.setImageResource(ClientGetCommandService.channelLogoMapping.get(channelPlayName));
						} catch (Exception e) {
							imageViewChannelLogo.setImageResource(R.drawable.logotv);
						}
						
						channelServiceId=(String)map.get("service_id");
						updateShouChangView(!allShouChangChannel.contains(channelServiceId));
						break;
					}
				}

				new Thread(new Runnable() {
					@Override
					public void run() {
						/**
						 * 初始化DB
						 */

						try {
							ChannelService channelService = new ChannelService(TVChannelPlayActivity.this);
							programList = channelService.searchCurrentChannelPlayByName(name);
							// 得到节目信息，发送消息更新UI
							mDismissHandler.sendEmptyMessage(3);

							// 3秒后，节目信息显示框消失
							Thread.sleep(3000);
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

	// 换台
	private void setPath(final String channelName) {
		try {
			if (!ClientSendCommandService.channelData.isEmpty()) {
				for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
					Map<String, Object> map = ClientSendCommandService.channelData.get(i);
					if (channelName.equals((String) map.get("service_name"))) {
						name = (String) map.get("service_name");
						path = ChannelService.obtainChannlPlayURL(map);
						if (mVideoView != null && name != null && !name.equals(ILLEGAL_PROGRAM_NAME)) {
                            mVideoView.suspend();
                            mVideoView.setVideoPath(path);
                            mVideoView.start();
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

	private void registerMyReceiver() {
		IntentFilter intentfilter = new IntentFilter();
		intentfilter.addAction("com.action.switchchannel");
		registerReceiver(SwitchReceiver, intentfilter);
	}

	private BroadcastReceiver SwitchReceiver = new BroadcastReceiver() {

		public void onReceive(Context mContext, Intent mIntent) {
			if (mIntent.getAction().equals("com.action.switchchannel")) {
				String name = mIntent.getStringExtra("channelname");
				String switchfreq = mIntent.getStringExtra("channelfreq");
				Log.e("TVPlayer", "channelname >>> " + name + "channelfreq >>>"
						+ switchfreq);
				// 异频点才换台
				if (name != null && !name.equals("") && !switchfreq.equals(freq)) {
					freq = switchfreq;
					setPath(name);
				}
			}
		}
	};
	
	/**
	 * 显示是否收藏频道
	 * @param isshouchang true显示收藏频道，false显示取消收藏
	 */
	private void updateShouChangView(boolean isshouchang) {
		if (isshouchang) {
			collection.setText("收藏\n频道");
		} else {
			collection.setText("取消\n收藏");
		}
	}

	private class PlayerIsPlayingMinitorThread extends Thread {
		public void run() {
			while (true) {
				try {
					// 用户在10秒钟之内连续返回则退出，反则计数器归一
					if ((System.currentTimeMillis() - backTimestamp) > 10000 && backTimestamp != 0l) {
						returnConfirm = 1;
						backTimestamp = 0l;
					}					
					if (mVideoView != null && mVideoView.isPlaying()) {
						// 播放中更新时间
						playTimestamp = System.currentTimeMillis();
					}
					if ((System.currentTimeMillis() - playTimestamp) > 12000 && playTimestamp != 0l) {
						// 超过12秒一直没有播放，退出播放界面
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
			vh.channelName.setText(StringUtils.getShortString("  " + String.valueOf(position + 1) + "  " + channelNames.get(position), 18));
			vh.channelName.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MyApplication.vibrator.vibrate(100);
					setPath(channelNames.get(position));
					initProgramInfo(channelNames.get(position));

                    try {
                        if (!dd.isShowing()) {
                            dd.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
			});
			return convertView;
		}

		public final class ViewHolder {
			public TextView channelName;
		}
	}

	/*********************************************** 屏幕触控部分 ********************************************************/

	public boolean onTouchEvent(MotionEvent event) {
		// 处理手势结束
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
            if (!menuKey) {
				setWidgetVisible(View.VISIBLE);
				menuKey = true;
			} else {
				setWidgetVisible(View.GONE);
				menuKey = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			endGesture();
			break;
		}
		return true;
	}

	/**
	 * 手势结束
	 */
	private void endGesture() {
		mVolume = -1;
		// 隐藏
		mDismissHandler.removeMessages(0);
		mDismissHandler.sendEmptyMessageDelayed(0, 500);

	}

	/*********************************************** 系统方法重载 ********************************************************/

	@Override
	protected void onNewIntent(Intent intent) {	
		super.onNewIntent(intent);
		if (intent != null) {
			if (intent.getStringExtra("channelname") != null
					&& !intent.getStringExtra("channelname").equals("")
					&& !intent.getStringExtra("channelname").equals("null")) {
				String channelname = intent.getStringExtra("channelname");
				setPath(channelname);
                initProgramInfo(channelname);
			}
		}
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			if (!menuKey) {
				setWidgetVisible(View.VISIBLE);
				menuKey = true;
			} else {
				setWidgetVisible(View.GONE);
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
//		TVChannelPlayActivity.this.finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(SwitchReceiver);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	
	/*******************************截屏分享代码***********************************/
	
	private Button bt_share;
	private ScreenShotView view_cutscreen;
	private String title="长虹电视助手",text;//我正在观看XXX台，XXX节目，好精彩呀！
	private String TAG="cutscreen";
	private RelativeLayout rl_content;
	private ProgressBar pb_cutscreen;
	private Bitmap bitmapVideo;
	private static final int DO_SHARE=222;
	private void initshareView() {
		bt_share = (Button) findViewById(R.id.bt_cutandshare);
		view_cutscreen = (ScreenShotView) findViewById(R.id.viewshare_video);
		rl_content = (RelativeLayout) findViewById(R.id.rl_cutscreencontent);
		pb_cutscreen = (ProgressBar) findViewById(R.id.pb_cutscreen);
		pb_cutscreen.setVisibility(View.INVISIBLE);
		bt_share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (view_cutscreen.isSaving()) {
					return;
				}
				view_cutscreen.setVisibility(View.VISIBLE);
				view_cutscreen.setSaving(true);
				pb_cutscreen.setVisibility(View.VISIBLE);

				if (programList != null && programList.size() > 0
						&& programList.get(0) != null) {
					text = "我正在观看" + name + "台的节目《"
							+ programList.get(0).getProgramName() + "》，好精彩呀！";
				} else {
					text = "我正在观看" + name + "台，好精彩呀！";
				}
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						 bitmapVideo = createVideoThumbnail(path);
						 shareToastHandler.sendEmptyMessage(DO_SHARE);
					}
				}).start();
				
			}
		});
	}
	
	private Bitmap createVideoThumbnail(String filePath) {
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever(
				TVChannelPlayActivity.this);
		try {
			retriever.setDataSource(filePath);
//			 String timeString =
//			 retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//			 long time = Long.parseLong(timeString) * 1000;
//			 bitmap = retriever.getFrameAtTime(time*31/160); //按视频长度比例选择帧
			bitmap = retriever.getFrameAtTime(mVideoView.getCurrentPosition()*1000); // 按视频长度比例选择帧
			L.d(TAG+" getvideo frame time=="+mVideoView.getCurrentPosition()+" bitmap is null? "+(bitmap==null));
		} catch (Exception ex) {
			ex.printStackTrace();
			L.d(TAG+" getvideo frame error  "+ex);
		} finally {
			try {
				retriever.release();
			} catch (RuntimeException ex) {
				ex.printStackTrace();
			}
		}
		return bitmap;
	} 

	Handler shareToastHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == DO_SHARE) {
				Bitmap bitActivity = ShareUtil.screenshot(rl_content);
				pb_cutscreen.setVisibility(View.GONE);
				view_cutscreen.cutScreenAndShare(title, text,
						bitmapVideo, bitActivity);
			}
		};
	};
}
