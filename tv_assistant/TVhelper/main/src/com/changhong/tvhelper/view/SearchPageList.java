package com.changhong.tvhelper.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.changhong.common.db.sqlite.DatabaseContainer;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.activity.TVChannelPlayActivity;
import com.changhong.tvhelper.activity.TVChannelProgramShowActivity;
import com.changhong.tvhelper.activity.TVChannelSearchActivity;
import com.changhong.tvhelper.activity.TVChannelShouCangShowActivity;
import com.changhong.tvhelper.activity.TVChannelShowActivity;
import com.changhong.tvhelper.domain.Program;
import com.changhong.tvhelper.service.ChannelService;
import com.changhong.tvhelper.service.ClientGetCommandService;
import com.changhong.tvhelper.utils.YuYingWordsUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SearchPageList extends Fragment{

	/**
	 * *****************************************Channel Part ********************************************************
	 */
	private ListView channelSearchList;

	private ChannelAdapter channelAdapter = null;
	private List<Map<String, Object>> searchChannel = new ArrayList<Map<String, Object>>();
	private Map<String, Program> currentChannelPlayData = new HashMap<String, Program>();
	private ChannelService channelService;
	/**
	 * channel data and its logo
	 */
	//private HashMap<String, Integer> hs = new HashMap<String, Integer>();	
	
	private Activity activity;
	private String searchString = null;
	/**
	 * message handler
	 */
	private static Handler mHandler = null;
	
	View view = null;
	
    private List<String> allShouChangChannel = new ArrayList<String>();
	@Override
	public void onAttach(Activity activity) {
		// TODO 自动生成的方法存根
		
		super.onAttach(activity);
		
		this.activity = activity;
		initData();
		initViewAndEvent();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view == null)
		{
			view = inflater.inflate(R.layout.search_page_list, container,false);
		}
		else {
			ViewGroup v = (ViewGroup)view.getParent();
			if (v != null) 
				v.removeView(view);
		}
									
		return view;//super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mHandler.sendEmptyMessage(0);
	}
	public void setCondition(String condition)
	{
		this.searchString = condition;		
		if(mHandler != null)
			mHandler.sendEmptyMessage(0);
	}
	
	private void initData() {
		searchChannel.clear();	
		
		// channel
		channelService = new ChannelService();
		new Thread(new Runnable() {
			@Override
			public void run() {
				/**
				 *初始化DB
				 */
				if (MyApplication.databaseContainer == null) {
                    MyApplication.databaseContainer = new DatabaseContainer(activity);
				}

				try {
					allShouChangChannel = channelService.getAllChannelShouCangs();
					currentChannelPlayData = channelService.searchCurrentChannelPlay();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	private void initViewAndEvent() {
		
		channelAdapter = new ChannelAdapter(activity);

		/**
		 * Handler
		 */
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					searchChannel.clear();

					if (StringUtils.hasLength(searchString)) {
						try {
							searchString = YuYingWordsUtils.normalChannelSearchWordsConvert(searchString);

							/**
                             */
							for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
                                String serviceName = (String) ClientSendCommandService.channelData.get(i).get("service_name");
                                serviceName = YuYingWordsUtils.getSpecialWordsChannel(serviceName);
                                if (serviceName.toLowerCase().indexOf(searchString.toLowerCase()) >= 0) {
                                    searchChannel.add(ClientSendCommandService.channelData.get(i));
                                }
                            }
							

							Collection<Map<String, Object>> channelList = (Collection<Map<String, Object>>)ChannelService.searchProgramByText(searchString);
							for (Map<String, Object> channel : channelList) {								
								for (Map<String, Object> tempMap : searchChannel) {
									if (tempMap.containsValue(channel.get("service_name"))) {
										continue;
									}									
								}
								searchChannel.add(channel);								
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

                    /**
                     */
					channelSearchList = (ListView)view.findViewById(R.id.channel_program_list);
                    channelSearchList.setVisibility(View.VISIBLE);

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
			final ViewHolder vh;
			if (convertView == null) {
                vh = new ViewHolder();
                convertView = minflater.inflate(R.layout.activity_channel_shoucang_item/*channel_search_item*/, null);
                vh.channelLogo = (ImageView) convertView.findViewById(R.id.channel_logo);
                vh.channelName = (TextView) convertView.findViewById(R.id.channel_name);
                vh.channelPlayInfo= (TextView) convertView.findViewById(R.id.channel_play_info);
                vh.channelShouCang = (TextView) convertView.findViewById(R.id.channel_shoucang);
                vh.channelPlayButton = (TextView) convertView.findViewById(R.id.channel_play_button);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

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

                    Intent intent = new Intent(activity, TVChannelPlayActivity.class);
                    intent.putExtra("channelname", serviceName);
                    startActivity(intent);
				}
			});
			final String channelServiceId = (String) map.get("service_id");
            final String channelName = (String) map.get("service_name");
            final String channelIndex = (String) map.get("channel_index");

            /**
             *收藏频道和取消收藏
             */
            if (allShouChangChannel.contains(channelServiceId)) {
                vh.channelShouCang.setText("取消\n收藏");
                vh.channelShouCang.setTextColor(getResources().getColor(R.color.orange));
            } else {
                vh.channelShouCang.setText("收藏\n频道");
                vh.channelShouCang.setTextColor(getResources().getColor(R.color.white));
            }
            
            vh.channelShouCang.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);

                    if (allShouChangChannel.contains(channelServiceId)) {
                        //通知adapter更新数据
                        boolean success = channelService.cancelChannelShouCang(channelServiceId);
                        if (success) {
                            vh.channelShouCang.setText("收藏\n频道");
                            vh.channelShouCang.setTextColor(getResources().getColor(R.color.white));
                            allShouChangChannel.remove(channelServiceId);

                            Toast.makeText(activity, "取消频道收藏成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "取消频道收藏失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //收藏频道
                        boolean success = channelService.channelShouCang(channelServiceId);
                        if (success) {
                            vh.channelShouCang.setText("取消\n收藏");
                            vh.channelShouCang.setTextColor(getResources().getColor(R.color.orange));
                            allShouChangChannel.add(channelServiceId);

                            Toast.makeText(activity, "收藏频道成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "收藏频道失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            /**
             *查看频道节目
             */
            vh.channelPlayButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);

                    Intent intent = new Intent(activity, TVChannelProgramShowActivity.class);
                    intent.putExtra("channelName", channelName);
                    intent.putExtra("channelIndex", channelIndex);
                    startActivity(intent);
                }
            });

			vh.channelName.setText(serviceName);

            //设置数据
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
			public TextView channelShouCang;
			public TextView channelPlayButton;
		}
	}
}
