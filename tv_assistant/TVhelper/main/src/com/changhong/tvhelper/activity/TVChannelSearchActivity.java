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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.db.sqlite.DatabaseContainer;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.DateUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.widgets.BidirSlidingLayout;
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
	private HashMap<String, Integer> hs = new HashMap<String, Integer>();
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

		initTVchannel();

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

		/**
		 * 转到播放界面
		 */
		channelSearchList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TVChannelPlayActivity.name = (String) searchChannel.get(position).get("service_name");
                Intent intent = new Intent(TVChannelSearchActivity.this, TVChannelPlayActivity.class);
                String name = (String) searchChannel.get(position).get("service_name");
                intent.putExtra("channelname", name);
                startActivity(intent);
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
                ipAdapter.notifyDataSetChanged();
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
                                if (((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf(searchString.toLowerCase()) >= 0) {
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
            if (hs.get((String) map.get("service_name")) != null && !hs.get((String) map.get("service_name")).equals("null") && !hs.get((String) map.get("service_name")).equals("")) {
                vh.channelLogo.setImageResource(hs.get((String) map.get("service_name")));
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
            String channelIndex= (String) map.get("channel_index");
            Program program=currentChannelPlayData.get(channelIndex);
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

	/* tangchao */
	private void initTVchannel() {
		hs.clear();

		hs.put(getResources().getString(R.string.cctv1_1), R.drawable.cctv1);
		hs.put(getResources().getString(R.string.cctv1_2), R.drawable.cctv1);
		hs.put(getResources().getString(R.string.cctv1_3), R.drawable.cctv1);
		hs.put(getResources().getString(R.string.cctv1_4), R.drawable.cctv1);
		hs.put(getResources().getString(R.string.cctv1hd_1), R.drawable.cctv1);
		hs.put(getResources().getString(R.string.cctv1hd_2), R.drawable.cctv1);
		// channelLogoMapping.put("CCTV-1����", R.drawable.cctv1hd);
		// channelLogoMapping.put("�ããԣ֣���(����)", R.drawable.cctv1hd);
		hs.put(getResources().getString(R.string.cctv2_1), R.drawable.cctv2);
		hs.put(getResources().getString(R.string.cctv2_2), R.drawable.cctv2);
		hs.put(getResources().getString(R.string.cctv2_3), R.drawable.cctv2);
		hs.put(getResources().getString(R.string.cctv2_4), R.drawable.cctv2);
		hs.put(getResources().getString(R.string.cctv3_1), R.drawable.cctv3);
		hs.put(getResources().getString(R.string.cctv3_2), R.drawable.cctv3);
		hs.put(getResources().getString(R.string.cctv3_3), R.drawable.cctv3);
		hs.put(getResources().getString(R.string.cctv3_4), R.drawable.cctv3);
		hs.put(getResources().getString(R.string.cctv3hd), R.drawable.cctv3);
		// channelLogoMapping.put("CCTV-3����", R.drawable.cctv3hd);
		hs.put(getResources().getString(R.string.cctv4_1), R.drawable.cctv4);
		hs.put(getResources().getString(R.string.cctv4_2), R.drawable.cctv4);
		hs.put(getResources().getString(R.string.cctv4_3), R.drawable.cctv4);
		hs.put(getResources().getString(R.string.cctv4_4), R.drawable.cctv4);
		hs.put(getResources().getString(R.string.cctv5hd), R.drawable.cctv5);
		hs.put(getResources().getString(R.string.cctv5hd_1), R.drawable.cctv5hd);
		// channelLogoMapping.put("CCTV5-�������¸���", R.drawable.cctv5hd1);
		hs.put(getResources().getString(R.string.cctv5_1), R.drawable.cctv5);
		hs.put(getResources().getString(R.string.cctv5_2), R.drawable.cctv5);
		hs.put(getResources().getString(R.string.cctv5_3), R.drawable.cctv5);
		hs.put(getResources().getString(R.string.cctv5_4), R.drawable.cctv5);
		hs.put(getResources().getString(R.string.cctv6_1), R.drawable.cctv6);
		hs.put(getResources().getString(R.string.cctv6_2), R.drawable.cctv6);
		hs.put(getResources().getString(R.string.cctv6_3), R.drawable.cctv6);
		hs.put(getResources().getString(R.string.cctv6_4), R.drawable.cctv6);
		hs.put(getResources().getString(R.string.cctv6hd), R.drawable.cctv6);
		// channelLogoMapping.put("CCTV-6����", R.drawable.cctv6hd);
		hs.put(getResources().getString(R.string.cctv7_1), R.drawable.cctv7);
		hs.put(getResources().getString(R.string.cctv7_2), R.drawable.cctv7);
		hs.put(getResources().getString(R.string.cctv7_3), R.drawable.cctv7);
		hs.put(getResources().getString(R.string.cctv7_4), R.drawable.cctv7);
		hs.put(getResources().getString(R.string.cctv8_1), R.drawable.cctv8);
		hs.put(getResources().getString(R.string.cctv8_2), R.drawable.cctv8);
		hs.put(getResources().getString(R.string.cctv8_3), R.drawable.cctv8);
		hs.put(getResources().getString(R.string.cctv8_4), R.drawable.cctv8);
		hs.put(getResources().getString(R.string.cctv8hd), R.drawable.cctv8);
		// channelLogoMapping.put("CCTV-8����", R.drawable.cctv8hd);
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

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		switch (keyCode) {
//		case KeyEvent.KEYCODE_MENU:
//			bidirSlidingLayout.clickSideMenu();
//			return true;
//		default:
//			break;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
}
