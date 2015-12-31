package com.changhong.tvhelper.activity;

import io.vov.vitamio.utils.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.AppConfig;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.widgets.BoxSelecter;
import com.changhong.common.widgets.IpSelectorDataServer;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.domain.OrderProgram;
import com.changhong.tvhelper.domain.Program;
import com.changhong.tvhelper.service.ChannelService;
import com.changhong.tvhelper.service.ClientGetCommandService;

/**
 * Jack Wang
 */
public class TVChannelShouCangShowActivity extends Activity {

	private static final String TAG = "TVChannelShouCangShowActivity";
	// private BidirSlidingLayout bidirSlidingLayout;
	/**
	 * message handler
	 */
	// public static Handler mHandler = null;
	private static IHandler mUiHandler = null;

	/**
	 * ***********************************************IP连接部分********************
	 * **********************************
	 */

	private BoxSelecter ipSelecter = null;
	private Button back = null;

	/**
	 * ***********************************************频道收藏部分********************
	 * **********************************
	 */

	private List<Map<String, Object>> channelShowData = new ArrayList<Map<String, Object>>();
	private static ListView channelList = null;
	private static ListView programList = null;
	private ChannelAdapter channelAdapter = null;
	private TextView channelText;

	/**
	 * ***********************************************节目预约部分********************
	 * **********************************
	 */
	public List<OrderProgram> orderProgramList = new ArrayList<OrderProgram>();
	private OrderProgramAdapter orderProgramAdapter;
	private TextView orderProgramText;
	private List<Map<String, Object>> orderProgramShowData = new ArrayList<Map<String, Object>>();

	/**
	 * **********************************************EPG查询**********************
	 * *********************************
	 */

	private Map<String, Program> currentChannelPlayData = new HashMap<String, Program>();
	private List<String> allShouChangChannel = new ArrayList<String>();
	private ChannelService channelService;
	private BroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		channelService = new ChannelService(this);

		initViewAndEvent();

	}

	private void initViewAndEvent() {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_channel_shoucang_view);
		channelList = (ListView) findViewById(R.id.channel_list);
		programList = (ListView) findViewById(R.id.program_list);
		back = (Button) findViewById(R.id.btn_back);
		channelText = (TextView) findViewById(R.id.text_channel_shoucang);
		orderProgramText = (TextView) findViewById(R.id.text_channel_program_yuyue);

		channelAdapter = new ChannelAdapter(this);
		channelList.setAdapter(channelAdapter);
		orderProgramAdapter = new OrderProgramAdapter(this);
		programList.setAdapter(orderProgramAdapter);

		/**
		 * 消息处理部分
		 */
		mUiHandler = new IHandler(this);

		/**
		 * IP part
		 */
		ipSelecter = new BoxSelecter(this, (TextView) findViewById(R.id.title),
				(ListView) findViewById(R.id.clients),
				(Button) findViewById(R.id.btn_list), new Handler(
						getMainLooper()));
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				finish();
			}
		});

		channelText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				if (allShouChangChannel.isEmpty()) {
					Toast.makeText(TVChannelShouCangShowActivity.this,
							R.string.favorite_empty, Toast.LENGTH_SHORT).show();
				}

				channelText.setTextColor(getResources()
						.getColor(R.color.orange));
				orderProgramText.setTextColor(getResources().getColor(
						R.color.white));
				// 通知Handler扫描收藏节目完成
				mUiHandler.sendEmptyMessage(0);
			}
		});

		orderProgramText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);

				Log.i(TAG,
						"orderProgramList.size() =" + orderProgramList.size());
				if (orderProgramList.size() <= 0) {
					Toast.makeText(TVChannelShouCangShowActivity.this,
							R.string.order_empty, Toast.LENGTH_SHORT).show();
				} else if (IpSelectorDataServer.getInstance().getIpList()
						.isEmpty()) {
					
					programList.setVisibility(View.GONE);
			
				}

				orderProgramText.setTextColor(getResources().getColor(
						R.color.orange));
				channelText.setTextColor(getResources().getColor(R.color.white));
				// 通知Handler扫描收藏节目完成
				mUiHandler.sendEmptyMessage(1);

			}
		});
		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				
				programList.setVisibility(View.GONE);
				channelList.setVisibility(View.VISIBLE);
				
				channelText.setTextColor(TVChannelShouCangShowActivity.this
					.getResources().getColor(R.color.orange));
				orderProgramText.setTextColor(TVChannelShouCangShowActivity.this
							.getResources().getColor(R.color.white));
				
				initData();
			}
		};
		this.registerReceiver(broadcastReceiver, new IntentFilter(
				AppConfig.BROADCAST_INTENT_EPGDB_UPDATE));

	}

	private void initData() {

		allShouChangChannel.clear();
		currentChannelPlayData.clear();
		channelShowData.clear();

		orderProgramShowData.clear();
		orderProgramList.clear();

		new collectionProgramThread().start();
		new OrderProgramThread().start();
	}

	private static class IHandler extends Handler {

		private TVChannelShouCangShowActivity theActivity = null;

		public IHandler(TVChannelShouCangShowActivity activity) {
			theActivity = activity;
		}

		@Override
		public void handleMessage(Message msg) {

			// TVChannelShouCangShowActivity theActivity= mActivity.get();

			switch (msg.what) {
			case 0:
				if (theActivity.channelAdapter != null) {
					//theActivity.channelList.setAdapter(theActivity.channelAdapter);
					
					// = (ListView) findViewById(R.id.channel_list);
				//	programList
					programList.setVisibility(View.GONE);
					channelList.setVisibility(View.VISIBLE);
					
				//	theActivity.channelAdapter.notifyDataSetChanged();
					
					theActivity.channelText.setTextColor(theActivity
							.getResources().getColor(R.color.orange));
					theActivity.orderProgramText.setTextColor(theActivity
							.getResources().getColor(R.color.white));
				}
				break;
			case 1:
				if (theActivity.orderProgramAdapter != null) {
					//theActivity.programList.setAdapter(theActivity.orderProgramAdapter);
				
					channelList.setVisibility(View.GONE);
					programList.setVisibility(View.VISIBLE);
				
				//	theActivity.orderProgramAdapter.notifyDataSetChanged();
					
					theActivity.channelText.setTextColor(theActivity
							.getResources().getColor(R.color.white));
					theActivity.orderProgramText.setTextColor(theActivity
							.getResources().getColor(R.color.orange));
					
				}
				break;
			case 2:
				if (!StringUtils.hasLength(IpSelectorDataServer.getInstance()
						.getCurrentIp())) {
					Toast.makeText(theActivity,
							R.string.server_isnt_exist, Toast.LENGTH_SHORT)
							.show();
				}

				// 重新加载预约频道数据并刷新Adapter
				try {

					
				//	programList.setVisibility(View.GONE);
				//	channelList.setVisibility(View.VISIBLE);
					
					theActivity.channelAdapter.setdata(
							theActivity.channelShowData,
							theActivity.allShouChangChannel,
							theActivity.currentChannelPlayData);
			
					//theActivity.channelAdapter.notifyDataSetChanged();

				theActivity.channelText.setTextColor(theActivity
					.getResources().getColor(R.color.orange));
					theActivity.orderProgramText.setTextColor(theActivity
							.getResources().getColor(R.color.white));

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 3:
				// 重新加载预约节目数据并刷新Adapter
				try {
			
				
					theActivity.orderProgramAdapter.setdata(
							theActivity.orderProgramList,
							theActivity.orderProgramShowData);
					
				//	theActivity.channelAdapter.notifyDataSetChanged();
					
		//			theActivity.channelText.setTextColor(theActivity
					//		.getResources().getColor(R.color.white));
				//	theActivity.orderProgramText.setTextColor(theActivity
				//			.getResources().getColor(R.color.orange));

				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	}

	// 收藏节目数据获取
	class collectionProgramThread extends Thread {
		@Override
		public void run() {
			/**
			 * 初始化DB
			 */

			try {
				if (StringUtils.hasLength(IpSelectorDataServer.getInstance()
						.getCurrentIp())) {
					allShouChangChannel = channelService
							.getAllChannelShouCangs();
					currentChannelPlayData = channelService
							.searchCurrentChannelPlay();
					int channelSize = ClientSendCommandService.channelData
							.size();

					for (int i = 0; i < channelSize; i++) {
						Map<String, Object> map = ClientSendCommandService.channelData
								.get(i);
						String channelServiceId = (String) map
								.get("service_id");
						if (allShouChangChannel.contains(channelServiceId)) {
							channelShowData.add(map);
						}
					}
					// mHandler.sendEmptyMessage(0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 通知Handler扫描收藏节目完成
			mUiHandler.sendEmptyMessage(2);
		}
	}

	// 预约节目数据获取
	class OrderProgramThread extends Thread {
		@Override
		public void run() {
			try {
				if (StringUtils.hasLength(IpSelectorDataServer.getInstance()
						.getCurrentIp())) {
					orderProgramList = channelService.findAllOrderPrograms();
					int channelSize = ClientSendCommandService.channelData
							.size();
					for (OrderProgram orderProgram : orderProgramList) {
						String channelName = orderProgram.getChannelName();
						for (int i = 0; i < channelSize; i++) {
							Map<String, Object> map = ClientSendCommandService.channelData
									.get(i);
							if (channelName.equals(map.get("service_name"))) {
								orderProgramShowData.add(map);
							}
						}
					}
					mUiHandler.sendEmptyMessage(3);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 通知Handler扫描预约节目完成
			// mUiHandler.sendEmptyMessage(1);
		}
	}

	/**
	 * 频道名称、频道ICON，频道当前信息
	 */
	private class ChannelAdapter extends BaseAdapter {

		private LayoutInflater minflater;
		private List<Map<String, Object>> ShouCangShowData = new ArrayList<Map<String, Object>>();
		private List<String> allShouCangChannelData = new ArrayList<String>();
		private Map<String, Program> ShouCangCurrentChannelPlayData = new HashMap<String, Program>();

		public ChannelAdapter(Context context) {
			this.minflater = LayoutInflater.from(context);

		}

		public void setdata(List<Map<String, Object>> dataOne,
				List<String> dataTwo, Map<String, Program> dataThree) {

			ShouCangShowData.clear();
			allShouCangChannelData.clear();
			ShouCangCurrentChannelPlayData.clear();

			ShouCangShowData.addAll(dataOne);
			allShouCangChannelData.addAll(dataTwo);
			ShouCangCurrentChannelPlayData.putAll(dataThree);
			notifyDataSetChanged();
		}

		public int getCount() {
			return allShouCangChannelData.size() == ShouCangShowData.size() ? allShouCangChannelData
					.size() : 0;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			/**
			 * VIEW HOLDER的配置
			 */
			final ViewHolder vh;
			if (convertView == null) {
				vh = new ViewHolder();
				convertView = minflater.inflate(
						R.layout.activity_channel_shoucang_item, null);
				vh.channelLogo = (ImageView) convertView
						.findViewById(R.id.channel_logo);
				vh.channelName = (TextView) convertView
						.findViewById(R.id.channel_name);
				vh.channelPlayInfo = (TextView) convertView
						.findViewById(R.id.channel_play_info);
				vh.channelShouCang = (TextView) convertView
						.findViewById(R.id.channel_shoucang);
				vh.channelPlayButton = (TextView) convertView
						.findViewById(R.id.channel_play_button);

				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}

			/**
			 * 观看直播
			 */
			final Map<String, Object> map = ShouCangShowData.get(position);
			vh.channelLogo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MyApplication.vibrator.vibrate(100);

					try {
						TVChannelPlayActivity.name = (String) map
								.get("service_name");
						TVChannelPlayActivity.path = ChannelService
								.obtainChannlPlayURL(map);
						Intent intent = new Intent(
								TVChannelShouCangShowActivity.this,
								TVChannelPlayActivity.class);
						String name = (String) map.get("service_name");
						intent.putExtra("channelname", name);
						startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			final String channelServiceId = (String) map.get("service_id");
			final String channelName = (String) map.get("service_name");
			final String channelIndex = (String) map.get("channel_index");

			/**
			 * 收藏频道和取消收藏
			 */
			vh.channelShouCang.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MyApplication.vibrator.vibrate(100);
					try {
						// 取消收藏操作
						boolean success = channelService
								.cancelChannelShouCang(channelServiceId);
						if (success) {
							// 更新数据
							allShouCangChannelData.remove(channelServiceId);
							Map<String, Object> removeMap = null;
							for (Map<String, Object> loop : ShouCangShowData) {
								String loopChannelServiceId = (String) loop
										.get("service_id");
								if (channelServiceId
										.equals(loopChannelServiceId)) {
									removeMap = loop;
									break;
								}
							}
							ShouCangShowData.remove(removeMap);
							notifyDataSetChanged();
							// 通知adapter更新数据
							mUiHandler.sendEmptyMessage(0);

							Toast.makeText(TVChannelShouCangShowActivity.this,
									R.string.cancel_favorite_success, Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(TVChannelShouCangShowActivity.this,
									R.string.cancel_favorite_failed, Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			/**
			 * 查看频道节目
			 */
			vh.channelPlayButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MyApplication.vibrator.vibrate(100);
					Intent intent = new Intent(
							TVChannelShouCangShowActivity.this,
							TVChannelProgramShowActivity.class);
					intent.putExtra("channelName", channelName);
					intent.putExtra("channelIndex", channelIndex);
					startActivity(intent);
				}
			});

			/**
			 * 设置数据
			 */
			try {
				String serviceName = (String) ShouCangShowData.get(position)
						.get("service_name");
				if (StringUtils.hasLength(serviceName)) {
					serviceName = serviceName.trim();
				}
				if (ClientGetCommandService.channelLogoMapping.get(serviceName) != null
						&& !ClientGetCommandService.channelLogoMapping.get(
								serviceName).equals("null")
						&& !ClientGetCommandService.channelLogoMapping.get(
								serviceName).equals("")) {
					vh.channelLogo
							.setImageResource(ClientGetCommandService.channelLogoMapping
									.get(serviceName));
				} else {
					vh.channelLogo.setImageResource(R.drawable.logotv);
				}
				vh.channelName.setText((position + 1) + " " + channelName);

				Program program = ShouCangCurrentChannelPlayData
						.get(channelName);
				if (program != null) {
					String time = getString(R.string.playing)
							+ program.getProgramStartTime()
							+ " - "
							+ program.getProgramEndTime()
							+ "\n\n"
							+ StringUtils.getShortString(
									program.getProgramName(), 12);
					vh.channelPlayInfo.setText(time);
				} else {
					vh.channelPlayInfo.setText(R.string.no_channel_detail);
				}
			} catch (Exception e) {
				e.printStackTrace();
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

	private class OrderProgramAdapter extends BaseAdapter {

		LayoutInflater minflater;
		private List<OrderProgram> AllOrderData = new ArrayList<OrderProgram>();
		private List<Map<String, Object>> OrderShowData = new ArrayList<Map<String, Object>>();

		public OrderProgramAdapter(Context context) {
			this.minflater = LayoutInflater.from(context);
		}

		public void setdata(List<OrderProgram> dataOne,
				List<Map<String, Object>> dataTwo) {

			AllOrderData.clear();
			OrderShowData.clear();

			AllOrderData.addAll(dataOne);
			OrderShowData.addAll(dataTwo);
			notifyDataSetChanged();

		}

		@Override
		public int getCount() {
			return AllOrderData.size() == OrderShowData.size() ? AllOrderData
					.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return AllOrderData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			if (convertView == null) {
				vh = new ViewHolder();
				convertView = minflater.inflate(
						R.layout.activity_channel_program_yuyue_item, null);
				vh.channelLogo = (ImageView) convertView
						.findViewById(R.id.channel_logo);
				vh.channelName = (TextView) convertView
						.findViewById(R.id.channel_name);
				vh.channelPlayInfo = (TextView) convertView
						.findViewById(R.id.channel_play_info);
				vh.channelYuyue = (TextView) convertView
						.findViewById(R.id.program_yuyue);
				vh.channelPlayButton = (TextView) convertView
						.findViewById(R.id.channel_play_button);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}

			final OrderProgram orderProgram = AllOrderData.get(position);
			vh.channelName.setText((position + 1) + " "
					+ orderProgram.getChannelName());
			vh.channelPlayInfo.setText(orderProgram.getWeekIndex()
					+ "  "
					+ orderProgram.getProgramStartTime()
					+ "-"
					+ orderProgram.getProgramEndTime()
					+ "\n\n"
					+ StringUtils.getShortString(orderProgram.getProgramName(),
							12));
			// 捕获异常，代表没有这个频道
			try {
				vh.channelLogo
						.setImageResource(ClientGetCommandService.channelLogoMapping
								.get(orderProgram.getChannelName()));
			} catch (Exception e) {
				vh.channelLogo.setImageResource(R.drawable.logotv);
			}

			/**
			 * 观看直播
			 */
			try {
				final Map<String, Object> map = OrderShowData.get(position);
				vh.channelLogo.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						MyApplication.vibrator.vibrate(100);

						TVChannelPlayActivity.name = orderProgram
								.getChannelName();
						TVChannelPlayActivity.path = ChannelService
								.obtainChannlPlayURL(map);

						Intent intent = new Intent(
								TVChannelShouCangShowActivity.this,
								TVChannelPlayActivity.class);
						String name = orderProgram.getChannelName();
						intent.putExtra("channelname", name);
						startActivity(intent);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}

			/**
			 * 收藏频道和取消收藏
			 */
			vh.channelYuyue.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MyApplication.vibrator.vibrate(100);

					try {
						// 取消预约操作
						boolean success = channelService.deleteOrderProgram(
								orderProgram.getProgramName(),
								orderProgram.getOrderDate());

						// 更新数据
						if (success) {
							for (Map<String, Object> map : OrderShowData) {
								if (map.get("channel_index").equals(
										orderProgram.getChannelIndex())) {
									OrderShowData.remove(map);
									break;
								}
							}
							AllOrderData.remove(orderProgram);
							notifyDataSetChanged();
							// 通知adapter更新数据
							mUiHandler.sendEmptyMessage(1);
							Toast.makeText(TVChannelShouCangShowActivity.this,
									R.string.cancel_order_success, Toast.LENGTH_SHORT).show();

						} else {
							Toast.makeText(TVChannelShouCangShowActivity.this,
									R.string.cancel_order_failed, Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			/**
			 * 查看频道节目
			 */
			vh.channelPlayButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MyApplication.vibrator.vibrate(100);

					Intent intent = new Intent(
							TVChannelShouCangShowActivity.this,
							TVChannelProgramShowActivity.class);
					intent.putExtra("channelName",
							orderProgram.getChannelName());
					intent.putExtra("channelIndex",
							orderProgram.getChannelIndex());
					startActivity(intent);
				}
			});
			return convertView;
		}

		public final class ViewHolder {
			public ImageView channelLogo;
			public TextView channelName;
			public TextView channelPlayInfo;
			public TextView channelYuyue;
			public TextView channelPlayButton;
		}
	}

	/**
	 * **********************************************系统方法重载*********************
	 * ********************************
	 */

	@Override
	protected void onResume() {
		super.onResume();
		// if (ClientSendCommandService.titletxt != null) {
		// title.setText(ClientSendCommandService.titletxt);
		// }
		Log.i(TAG, "onResume is running");
		
		
		programList.setVisibility(View.GONE);
		channelList.setVisibility(View.VISIBLE);
		
		channelText.setTextColor(TVChannelShouCangShowActivity.this
			.getResources().getColor(R.color.orange));
		orderProgramText.setTextColor(TVChannelShouCangShowActivity.this
					.getResources().getColor(R.color.white));
		
		initData();
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (ipSelecter != null) {
			ipSelecter.release();
		}
		if (broadcastReceiver != null) {
			this.unregisterReceiver(broadcastReceiver);
		}
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// switch (keyCode) {
	// case KeyEvent.KEYCODE_MENU:
	// bidirSlidingLayout.clickSideMenu();
	// return true;
	// default:
	// break;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

}
