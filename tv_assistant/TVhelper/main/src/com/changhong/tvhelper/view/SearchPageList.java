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
import com.changhong.tvhelper.activity.TVChannelSearchActivity;
import com.changhong.tvhelper.domain.Program;
import com.changhong.tvhelper.service.ChannelService;
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
	private HashMap<String, Integer> hs = new HashMap<String, Integer>();	
	
	private Activity activity;
	private String searchString = null;
	/**
	 * message handler
	 */
	private static Handler mHandler = null;
	
	View view = null;
	
	
	@Override
	public void onAttach(Activity activity) {
		// TODO 自动生成的方法存根
		
		super.onAttach(activity);
		
		this.activity = activity;
		initData();
		initTVchannel();
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
				 * 鍒濆鍖朌B
				 */
				if (MyApplication.databaseContainer == null) {
                    MyApplication.databaseContainer = new DatabaseContainer(activity);
				}

				try {
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
                             * 鍖归厤棰戦亾
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
                     * 鍏堟樉绀洪閬�
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
			ViewHolder vh = null;
			if (convertView == null) {
                vh = new ViewHolder();
                convertView = minflater.inflate(R.layout.activity_channel_shoucang_item/*channel_search_item*/, null);
                vh.channelLogo = (ImageView) convertView.findViewById(R.id.channel_logo);
                vh.channelName = (TextView) convertView.findViewById(R.id.channel_name);
                vh.channelPlayInfo= (TextView) convertView.findViewById(R.id.channel_play_info);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            //璁剧疆LOGO
            final Map<String, Object> map = searchChannel.get(position);
            if (hs.get((String) map.get("service_name")) != null && !hs.get((String) map.get("service_name")).equals("null") && !hs.get((String) map.get("service_name")).equals("")) {
                vh.channelLogo.setImageResource(hs.get((String) map.get("service_name")));
            } else {
                vh.channelLogo.setImageResource(R.drawable.logotv);
            }
			/**
			 * 瑙傜湅鐩存挱
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

			// 璁剧疆棰戦亾鍚�
			vh.channelName.setText(serviceName);

            //璁剧疆PLAYINFO
            String channelName= (String) map.get("service_name");
            Program program=currentChannelPlayData.get(channelName);
            if(program != null){
                String time="姝ｅ湪鎾斁:" + program.getProgramStartTime() + " - " + program.getProgramEndTime() + "\n" + program.getProgramName();
                vh.channelPlayInfo.setText(time);
            }else {
                vh.channelPlayInfo.setText("鏃犺妭鐩俊鎭�");
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
	 * **********************************************绯荤粺鏂规硶閲嶈浇*********************
	 * ********************************
	 */

	/* tangchao */
	private void initTVchannel() {
		hs.clear();

		hs.put(getResources().getString(R.string.cctv1_1), R.drawable.cctv1);
		hs.put(getResources().getString(R.string.cctv1_2), R.drawable.cctv1);
		hs.put(getResources().getString(R.string.cctv1_3), R.drawable.cctv1);
		hs.put(getResources().getString(R.string.cctv1_4), R.drawable.cctv1);
		hs.put(getResources().getString(R.string.cctv1hd_1), R.drawable.cctv1);
		hs.put(getResources().getString(R.string.cctv1hd_2), R.drawable.cctv1);
		// channelLogoMapping.put("CCTV-1锟斤拷锟斤拷", R.drawable.cctv1hd);
		// channelLogoMapping.put("锟矫ＣＴＶｏ拷锟斤拷(锟斤拷锟斤拷)", R.drawable.cctv1hd);
		hs.put(getResources().getString(R.string.cctv2_1), R.drawable.cctv2);
		hs.put(getResources().getString(R.string.cctv2_2), R.drawable.cctv2);
		hs.put(getResources().getString(R.string.cctv2_3), R.drawable.cctv2);
		hs.put(getResources().getString(R.string.cctv2_4), R.drawable.cctv2);
		hs.put(getResources().getString(R.string.cctv3_1), R.drawable.cctv3);
		hs.put(getResources().getString(R.string.cctv3_2), R.drawable.cctv3);
		hs.put(getResources().getString(R.string.cctv3_3), R.drawable.cctv3);
		hs.put(getResources().getString(R.string.cctv3_4), R.drawable.cctv3);
		hs.put(getResources().getString(R.string.cctv3hd), R.drawable.cctv3);
		// channelLogoMapping.put("CCTV-3锟斤拷锟斤拷", R.drawable.cctv3hd);
		hs.put(getResources().getString(R.string.cctv4_1), R.drawable.cctv4);
		hs.put(getResources().getString(R.string.cctv4_2), R.drawable.cctv4);
		hs.put(getResources().getString(R.string.cctv4_3), R.drawable.cctv4);
		hs.put(getResources().getString(R.string.cctv4_4), R.drawable.cctv4);
		hs.put(getResources().getString(R.string.cctv5hd), R.drawable.cctv5);
		hs.put(getResources().getString(R.string.cctv5hd_1), R.drawable.cctv5hd);
		// channelLogoMapping.put("CCTV5-锟斤拷锟斤拷锟斤拷锟铰革拷锟斤拷", R.drawable.cctv5hd1);
		hs.put(getResources().getString(R.string.cctv5_1), R.drawable.cctv5);
		hs.put(getResources().getString(R.string.cctv5_2), R.drawable.cctv5);
		hs.put(getResources().getString(R.string.cctv5_3), R.drawable.cctv5);
		hs.put(getResources().getString(R.string.cctv5_4), R.drawable.cctv5);
		hs.put(getResources().getString(R.string.cctv6_1), R.drawable.cctv6);
		hs.put(getResources().getString(R.string.cctv6_2), R.drawable.cctv6);
		hs.put(getResources().getString(R.string.cctv6_3), R.drawable.cctv6);
		hs.put(getResources().getString(R.string.cctv6_4), R.drawable.cctv6);
		hs.put(getResources().getString(R.string.cctv6hd), R.drawable.cctv6);
		// channelLogoMapping.put("CCTV-6锟斤拷锟斤拷", R.drawable.cctv6hd);
		hs.put(getResources().getString(R.string.cctv7_1), R.drawable.cctv7);
		hs.put(getResources().getString(R.string.cctv7_2), R.drawable.cctv7);
		hs.put(getResources().getString(R.string.cctv7_3), R.drawable.cctv7);
		hs.put(getResources().getString(R.string.cctv7_4), R.drawable.cctv7);
		hs.put(getResources().getString(R.string.cctv8_1), R.drawable.cctv8);
		hs.put(getResources().getString(R.string.cctv8_2), R.drawable.cctv8);
		hs.put(getResources().getString(R.string.cctv8_3), R.drawable.cctv8);
		hs.put(getResources().getString(R.string.cctv8_4), R.drawable.cctv8);
		hs.put(getResources().getString(R.string.cctv8hd), R.drawable.cctv8);
		// channelLogoMapping.put("CCTV-8锟斤拷锟斤拷", R.drawable.cctv8hd);
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
