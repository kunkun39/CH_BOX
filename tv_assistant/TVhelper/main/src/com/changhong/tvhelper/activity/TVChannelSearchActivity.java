package com.changhong.tvhelper.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.db.sqlite.DatabaseContainer;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.DateUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.touying.activity.MusicDetailsActivity;
import com.changhong.touying.activity.VedioDetailsActivity;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicProvider;
import com.changhong.touying.vedio.Vedio;
import com.changhong.touying.vedio.VedioProvider;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.domain.Program;
import com.changhong.tvhelper.service.ChannelService;
import com.changhong.tvhelper.service.ClientGetCommandService;
import com.changhong.tvhelper.utils.YuYingWordsUtils;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class TVChannelSearchActivity extends Activity {

	private static final String TAG = "tvplayer";

	/**
	 * message handler
	 */
	public static Handler mHandler = null;

	/**
	 * *****************************************Server IP Part ******************************************************
	 */
	public static BoxSelectAdapter ipAdapter = null;
	public static TextView title = null;
	private ListView clients = null;
	private Button list = null;
	private Button back = null;

	/**
	 * *****************************************Channel Part ********************************************************
	 */
	private ListView channelSearchList;
	private ListView musicSearchList;
	private ListView vedioSearchList;

	private ChannelAdapter channelAdapter = null;
	private List<Map<String, Object>> searchChannel = new ArrayList<Map<String, Object>>();
	private Map<String, Program> currentChannelPlayData = new HashMap<String, Program>();
	private ChannelService channelService;
	/**
	 * channel data and its logo
	 */
//	private HashMap<String, Integer> hs = new HashMap<String, Integer>();
	private TextView channelText;

	/**
	 * *****************************************Music Part *********************************************************
	 */
	private MusicAdapter musicAdapter;
	private List<Music> searchMusics = new ArrayList<Music>();
	private MusicProvider musicProvider;
	private List<Music> musics;
	private TextView musicText;

	/**
	 * **********************************************Vedio Part******************************************************
	 */
	private VedioProvider vedioProvider;
	private VideoAdapter videoAdapter;
	private List<Vedio> videos;
	private List<Vedio> searchVideos = new ArrayList<Vedio>();
	private TextView videoText;

	/**
	 * ***********************************************搜索框**********************************************************
	 */
	private InputMethodManager imm = null;
	private EditText searchEditText = null;
	private Button searchButton;
	private String searchString = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_channel_search);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		initData();

//		initTVchannel();//hs修改为使用ClientGetCommandService.channelLogoMapping

		initViewAndEvent();
	}

	private void initData() {
		searchChannel.clear();
		searchMusics.clear();
		searchVideos.clear();

		// channel
		channelService = new ChannelService();
		new Thread(new Runnable() {
			@Override
			public void run() {
				/**
				 * 初始化DB
				 */
				if (MyApplication.databaseContainer == null) {
                    MyApplication.databaseContainer = new DatabaseContainer(TVChannelSearchActivity.this);
				}

				try {
					currentChannelPlayData = channelService.searchCurrentChannelPlay();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		// music
		musicProvider = new MusicProvider(TVChannelSearchActivity.this);
		musics = (List<Music>) musicProvider.getList();
		searchMusics = new ArrayList<Music>();

		// video
		vedioProvider = new VedioProvider(TVChannelSearchActivity.this);
		videos = (List<Vedio>) vedioProvider.getList();
		searchVideos = new ArrayList<Vedio>();

	}

	private void initViewAndEvent() {
		title = (TextView) findViewById(R.id.title);
		clients = (ListView) findViewById(R.id.clients);
		back = (Button) findViewById(R.id.btn_back);
		list = (Button) findViewById(R.id.btn_list);

		searchEditText = (EditText) findViewById(R.id.searchstring);
		searchButton = (Button) findViewById(R.id.btn_search);

        channelSearchList = (ListView) findViewById(R.id.list_channels);
        musicSearchList = (ListView) findViewById(R.id.list_musics);
        vedioSearchList = (ListView) findViewById(R.id.list_vedios);

		// channel
		channelText = (TextView) findViewById(R.id.text_channel);
		channelAdapter = new ChannelAdapter(TVChannelSearchActivity.this);
		channelText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);

				channelText.setTextColor(getResources().getColor(R.color.orange));
				musicText.setTextColor(Color.WHITE);
				videoText.setTextColor(Color.WHITE);

                channelSearchList.clearDisappearingChildren();
                channelSearchList.setAdapter(channelAdapter);
				channelAdapter.notifyDataSetChanged();

                channelSearchList.setVisibility(View.VISIBLE);
                musicSearchList.setVisibility(View.GONE);
                vedioSearchList.setVisibility(View.GONE);
			}
		});

        // music
        musicText = (TextView) findViewById(R.id.text_music);
		musicAdapter = new MusicAdapter(TVChannelSearchActivity.this);
		musicText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				musicText.setTextColor(getResources().getColor(R.color.orange));
				videoText.setTextColor(Color.WHITE);
				channelText.setTextColor(Color.WHITE);

                musicSearchList.clearDisappearingChildren();
                musicSearchList.setAdapter(musicAdapter);
				musicAdapter.notifyDataSetChanged();

                channelSearchList.setVisibility(View.GONE);
                musicSearchList.setVisibility(View.VISIBLE);
                vedioSearchList.setVisibility(View.GONE);
            }
		});

		// video
		videoText = (TextView) findViewById(R.id.text_video);
		videoAdapter = new VideoAdapter(TVChannelSearchActivity.this);
		videoText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);

				videoText.setTextColor(getResources().getColor(R.color.orange));
				musicText.setTextColor(Color.WHITE);
				channelText.setTextColor(Color.WHITE);

                vedioSearchList.clearDisappearingChildren();
                vedioSearchList.setAdapter(videoAdapter);
				videoAdapter.notifyDataSetChanged();

                channelSearchList.setVisibility(View.GONE);
                musicSearchList.setVisibility(View.GONE);
                vedioSearchList.setVisibility(View.VISIBLE);
			}
		});

		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				searchString = searchEditText.getText().toString();
				mHandler.sendEmptyMessage(0);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
		});

        musicSearchList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentMusic = new Intent();
                Music music = searchMusics.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("selectedMusic", music);
                intentMusic.putExtras(bundle);
                intentMusic.setClass(TVChannelSearchActivity.this, MusicDetailsActivity.class);
                startActivity(intentMusic);

            }
        });

        vedioSearchList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentVideo = new Intent();
                Vedio vedio = searchVideos.get(position);
                Bundle bundleVideo = new Bundle();
                bundleVideo.putSerializable("selectedVedio", vedio);
                intentVideo.putExtras(bundleVideo);
                intentVideo.setClass(TVChannelSearchActivity.this, VedioDetailsActivity.class);
                startActivity(intentVideo);
            }
        });

		/**
		 * Ip Part
		 */
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				finish();
			}
		});
		ipAdapter = new BoxSelectAdapter(TVChannelSearchActivity.this, ClientSendCommandService.serverIpList);
		clients.setAdapter(ipAdapter);
		clients.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				clients.setVisibility(View.GONE);
				return false;
			}
		});
		clients.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ClientSendCommandService.serverIP = ClientSendCommandService.serverIpList.get(arg2);
                String boxName = ClientSendCommandService.getCurrentConnectBoxName();
                ClientSendCommandService.titletxt = boxName;
                title.setText(boxName);
				ClientSendCommandService.handler.sendEmptyMessage(2);
				clients.setVisibility(View.GONE);
			}
		});
		list.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ClientSendCommandService.serverIpList.isEmpty()) {
					Toast.makeText(TVChannelSearchActivity.this,
							"没有发现长虹智能机顶盒，请确认盒子和手机连在同一个路由器?", Toast.LENGTH_LONG)
							.show();
				} else {
					clients.setVisibility(View.VISIBLE);
				}
			}
		});

		/**
		 * Handler
		 */
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					searchChannel.clear();
					searchMusics.clear();
					searchVideos.clear();

					if (StringUtils.hasLength(searchString)) {
						try {
							searchString = YuYingWordsUtils.normalChannelSearchWordsConvert(searchString);

							/**
                             * 匹配频道
                             */
							for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
                                String serviceName = (String) ClientSendCommandService.channelData.get(i).get("service_name");
                                serviceName = YuYingWordsUtils.getSpecialWordsChannel(serviceName);
                                if (serviceName.toLowerCase().indexOf(searchString.toLowerCase()) >= 0) {
                                    searchChannel.add(ClientSendCommandService.channelData.get(i));
                                }
                            }
							/**
                             * 匹配音乐
                             */
							int musicSum = musics.size();
							for (int i = 0; i < musicSum; i++) {
                                if ((musics.get(i).getTitle().toLowerCase().indexOf(searchString.toLowerCase())) >= 0||(musics.get(i).getArtist().toLowerCase().indexOf(searchString.toLowerCase())) >= 0) {
                                    searchMusics.add(musics.get(i));
                                }
                            }
							/**
                             * 匹配视频
                             */
							int videoSum = videos.size();
							for (int i = 0; i < videoSum; i++) {
                                if ((videos.get(i).getTitle().toLowerCase().indexOf(searchString.toLowerCase())) >= 0) {
                                    searchVideos.add(videos.get(i));
                                }
                            }
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

                    /**
                     * 先显示频道
                     */
                    channelText.setTextColor(getResources().getColor(R.color.orange));
                    musicText.setTextColor(getResources().getColor(R.color.white));
                    videoText.setTextColor(getResources().getColor(R.color.white));
                    channelSearchList.setVisibility(View.VISIBLE);
                    musicSearchList.setVisibility(View.GONE);
                    vedioSearchList.setVisibility(View.GONE);

                    channelSearchList.clearDisappearingChildren();
                    channelSearchList.setAdapter(channelAdapter);
                    channelAdapter.notifyDataSetChanged();
                    break;
				default:
					break;
				}
				super.handleMessage(msg);
			}

		};
	}

	class MusicAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater layoutInflater;

		public MusicAdapter(Context context) {
			this.context = context;
			this.layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return searchMusics.size();
		}

		@Override
		public Object getItem(int position) {
			return searchMusics.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.music_search_item, null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.music_image);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.music_name);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Music music = searchMusics.get(position);
            synchronizImageLoad(viewHolder.imageView, position);
            viewHolder.textView.setText((position+1) + " > " + music.getArtist() + " - " + music.getTitle() + " [" + DateUtils.getTimeShow(music.getDuration() / 1000) + "]");

            return convertView;
        }

        class ViewHolder {
            public TextView textView;
            public ImageView imageView;
        }

        private void synchronizImageLoad(final ImageView imageView, final int position) {
            int musicImageSource = (position + 1) % 12;
            switch (musicImageSource) {
                case 1:
                    imageView.setBackground(context.getResources().getDrawable(com.changhong.touying.R.drawable.music_bg1));
                    break;
                case 2:
                    imageView.setBackground(context.getResources().getDrawable(com.changhong.touying.R.drawable.music_bg2));
                    break;
                case 3:
                    imageView.setBackground(context.getResources().getDrawable(com.changhong.touying.R.drawable.music_bg3));
                    break;
                case 4:
                    imageView.setBackground(context.getResources().getDrawable(com.changhong.touying.R.drawable.music_bg4));
                    break;
                case 5:
                    imageView.setBackground(context.getResources().getDrawable(com.changhong.touying.R.drawable.music_bg5));
                    break;
                case 6:
                    imageView.setBackground(context.getResources().getDrawable(com.changhong.touying.R.drawable.music_bg6));
                    break;
                case 7:
                    imageView.setBackground(context.getResources().getDrawable(com.changhong.touying.R.drawable.music_bg7));
                    break;
                case 8:
                    imageView.setBackground(context.getResources().getDrawable(com.changhong.touying.R.drawable.music_bg8));
                    break;
                case 9:
                    imageView.setBackground(context.getResources().getDrawable(com.changhong.touying.R.drawable.music_bg9));
                    break;
                case 10:
                    imageView.setBackground(context.getResources().getDrawable(com.changhong.touying.R.drawable.music_bg10));
                    break;
                case 11:
                    imageView.setBackground(context.getResources().getDrawable(com.changhong.touying.R.drawable.music_bg11));
                    break;
                case 12:
                    imageView.setBackground(context.getResources().getDrawable(com.changhong.touying.R.drawable.music_bg12));
                    break;
                default:
                    imageView.setBackground(context.getResources().getDrawable(com.changhong.touying.R.drawable.music_bg1));
                    break;
            }
        }
	}

	class VideoAdapter extends BaseAdapter {
		LayoutInflater layoutInflater;

		public VideoAdapter(Context context) {
			this.layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return searchVideos.size();
		}

		@Override
		public Object getItem(int position) {
			return searchVideos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.video_search_item, null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.video_image);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.video_name);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Vedio vedio = searchVideos.get(position);
            String displayName = StringUtils.hasLength(vedio.getDisplayName()) ? StringUtils.getShortString(vedio.getDisplayName(), 20) : vedio.getTitle();
            viewHolder.textView.setText(displayName);
            String vedioPath = vedio.getPath();
            String vedioImagePath = DiskCacheFileManager.isSmallImageExist(vedioPath);
            viewHolder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            if (!vedioImagePath.equals("")) {
                MyApplication.imageLoader.displayImage("file://" + vedioImagePath, viewHolder.imageView, DisplayImageOptions.createSimple());
            } else {
                synchronizImageLoad(viewHolder.imageView, vedioPath);
            }

            return convertView;
        }

        private void synchronizImageLoad(final ImageView imageView, final String path) {
            ImageAsyncTask task = new ImageAsyncTask(imageView);
            task.execute(path);
        }

        class ViewHolder {
            public TextView textView;
            public ImageView imageView;
        }

        private final class ImageAsyncTask extends AsyncTask<String, Integer, Bitmap> {
            ImageView imageView;

            private ImageAsyncTask(ImageView imageView) {
                this.imageView = imageView;
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap bitmap = null;
                try {
                    String path = params[0];
                    bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
                    DiskCacheFileManager.saveSmallImage(bitmap, path);
                    return bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                    return bitmap;
                }
            }

			@Override
			protected void onPostExecute(Bitmap bitmap) {
				if (bitmap != null && imageView != null) {
					imageView.setImageBitmap(bitmap);
					imageView.setScaleType(ImageView.ScaleType.FIT_XY);
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
			return searchChannel.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh = null;
			if (convertView == null) {
                vh = new ViewHolder();
                convertView = minflater.inflate(R.layout.channel_search_item, null);
                vh.channelLogo = (ImageView) convertView.findViewById(R.id.channel_logo);
                vh.channelName = (TextView) convertView.findViewById(R.id.channel_name);
                vh.channelPlayInfo= (TextView) convertView.findViewById(R.id.channel_play_info);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            //设置LOGO
            final Map<String, Object> map = searchChannel.get(position);
            if (ClientGetCommandService.channelLogoMapping.get((String) map.get("service_name")) != null && !ClientGetCommandService.channelLogoMapping.get((String) map.get("service_name")).equals("null") && !ClientGetCommandService.channelLogoMapping.get((String) map.get("service_name")).equals("")) {
                vh.channelLogo.setImageResource(ClientGetCommandService.channelLogoMapping.get((String) map.get("service_name")));
            } else {
                vh.channelLogo.setImageResource(R.drawable.logotv);
            }
			/**
			 * 观看直播
			 */
			final String serviceName = (String) map.get("service_name");
			vh.channelLogo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    TVChannelPlayActivity.name = serviceName;
                    TVChannelPlayActivity.path = ChannelService.obtainChannlPlayURL(map);

                    Intent intent = new Intent(TVChannelSearchActivity.this, TVChannelPlayActivity.class);
                    intent.putExtra("channelname", serviceName);
                    startActivity(intent);
				}
			});

			// 设置频道名
			vh.channelName.setText(serviceName);

            //设置PLAYINFO
            String channelName= (String) map.get("service_name");
            Program program=currentChannelPlayData.get(channelName);
            if(program != null){
                String time="正在播放:" + program.getProgramStartTime() + " - " + program.getProgramEndTime() + "\n" + program.getProgramName();
                vh.channelPlayInfo.setText(time);
            }else {
                vh.channelPlayInfo.setText("无节目信息");
            }

			return convertView;
		}

		public final class ViewHolder {
			public ImageView channelLogo;
			public TextView channelName;
			public TextView channelPlayInfo;
		}
	}

	/**
	 * **********************************************系统方法重载*********************
	 * ********************************
	 */

	@Override
	protected void onResume() {
		super.onResume();
		if (ClientSendCommandService.titletxt != null) {
			title.setText(ClientSendCommandService.titletxt);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
