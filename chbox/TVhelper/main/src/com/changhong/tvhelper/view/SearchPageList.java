package com.changhong.tvhelper.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.AppConfig;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.DateUtils;
import com.changhong.common.utils.DialogUtil;
import com.changhong.common.utils.DialogUtil.DialogBtnOnClickListener;
import com.changhong.common.utils.DialogUtil.DialogMessage;
import com.changhong.common.utils.StringUtils;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.activity.TVChannelPlayActivity;
import com.changhong.tvhelper.activity.TVChannelProgramShowActivity;
import com.changhong.tvhelper.activity.TVChannelSearchActivity;
import com.changhong.tvhelper.domain.OrderProgram;
import com.changhong.tvhelper.domain.Program;
import com.changhong.tvhelper.service.ChannelService;
import com.changhong.tvhelper.service.ClientGetCommandService;
import com.changhong.tvhelper.utils.CommonUtil;
import com.changhong.tvhelper.utils.YuYingWordsUtils;

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
	BroadcastReceiver broadcastReceiver;
	
    private List<String> allShouChangChannel = new ArrayList<String>();
	@Override
	public void onAttach(Activity activity) {
		// TODO 自动生成的方法存根
		
		super.onAttach(activity);
		
		this.activity = activity;
		
		initViewAndEvent();
		initData();
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
		channelService = new ChannelService(activity);
		new Thread(new Runnable() {
			@Override
			public void run() {
				/**
				 *初始化DB
				 */

				try {
					allShouChangChannel = channelService.getAllChannelShouCangs();
					currentChannelPlayData = channelService.searchCurrentChannelPlay();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (mHandler != null) {
					mHandler.sendEmptyMessage(0);
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
							
							Collection<Map<String, Object>> channelList = (Collection<Map<String, Object>>)channelService.searchProgramByText(searchString);
							for (Map<String, Object> channel : channelList) {								
//								for (Map<String, Object> tempMap : searchChannel) {
//									if (tempMap.containsValue(channel.get("service_name"))) {
//										continue;
//									}									
//								}
								searchChannel.add(channel);								
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (searchChannel.size() == 0) {
							Toast.makeText(getActivity(), "没有搜索到任何节目或频道信息！", Toast.LENGTH_SHORT).show();
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
		
		broadcastReceiver = new BroadcastReceiver()
        {
			@Override
			public void onReceive(Context context, Intent intent) {
				initData();
			}        	
        };
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(AppConfig.BROADCAST_INTENT_EPGDB_UPDATE));
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
            List<OrderProgram> orderPrograms = channelService.findAllOrderPrograms();
            final Program program = CommonUtil.RawDataToProgram(map);
            
			final String serviceName = (String) map.get("service_name");
			vh.channelLogo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    TVChannelPlayActivity.name = serviceName;
                    TVChannelPlayActivity.path = ChannelService.obtainChannlPlayURL(map);

					// 保存历史记录
					TVChannelSearchActivity activity = (TVChannelSearchActivity)getActivity();
					activity.saveHistory(searchString);
					((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);

                    Intent intent = new Intent(activity, TVChannelPlayActivity.class);
                    intent.putExtra("channelname", serviceName);
                    startActivity(intent);
				}
			});
			final String channelServiceId = ((String) map.get("service_id") == null)? (String) map.get("channel_index") : (String) map.get("service_id");
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
                    notifyDataSetChanged();
                }
            });

            /**
             *查看频道节目
             */
            final int weekIndexOffset;
            if(map.containsKey("program_name"))
            {		
            	vh.channelShouCang.setVisibility(View.GONE);
            	weekIndexOffset = Integer.valueOf((String) map.get("week_index")) >= DateUtils.getWeekIndex(0)
                		? Integer.valueOf((String) map.get("week_index")) - DateUtils.getWeekIndex(0)
                		: 7 - DateUtils.getWeekIndex(0) + Integer.valueOf((String) map.get("week_index"));
                
                
            	if(weekIndexOffset == 0 )
            	{            		

            		if (0 < DateUtils.getCurrentTimeStamp().compareTo((String)map.get("str_endTime"))) {
            			vh.channelPlayButton.setText("已经结束");	
					}
            		else if (0 < DateUtils.getCurrentTimeStamp().compareTo((String)map.get("str_startTime"))) {
            			vh.channelPlayButton.setText("正在播放");	
					}
            		else {
            			if(findOrderProgram(orderPrograms,program))
            			{
            				vh.channelPlayButton.setText("已经预约");
            				vh.channelPlayButton.setTextColor(getResources().getColor(R.color.orange));
            			}
            			else {
            				if (map.containsKey("program_name")&&map.get("program_name").equals("无节目信息")) {
            					vh.channelPlayButton.setText("不可预约");
                				vh.channelPlayButton.setTextColor(getResources().getColor(R.color.white));
                				vh.channelPlayButton.setVisibility(View.INVISIBLE);
            				}else {
            					vh.channelPlayButton.setText("可以预约");
                				vh.channelPlayButton.setTextColor(getResources().getColor(R.color.white));
    						}
            			}
					}            		
            	}
            	else {
            		if(findOrderProgram(orderPrograms,program))
            		{
            			vh.channelPlayButton.setText("已经预约");
        				vh.channelPlayButton.setTextColor(getResources().getColor(R.color.orange));
        			}
        			else {
        				if (map.containsKey("program_name")&&map.get("program_name").equals("无节目信息")) {
        					vh.channelPlayButton.setText("不可预约");
            				vh.channelPlayButton.setTextColor(getResources().getColor(R.color.white));
            				vh.channelPlayButton.setVisibility(View.INVISIBLE);
        				}else {
        					vh.channelPlayButton.setText("可以预约");
            				vh.channelPlayButton.setTextColor(getResources().getColor(R.color.white));
						}
        			}
				}
            		
            	
            	
            	vh.channelPlayButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyApplication.vibrator.vibrate(100);

                        if (vh.channelPlayButton.getText().equals("可以预约")) {

                            try {
                                //删除时间冲突的预约节目
                                final OrderProgram orderProgramConflict = channelService.findOrderProgramByStartTime((String)map.get("str_startTime"), DateUtils.getWeekIndexName(weekIndexOffset));
                                if (orderProgramConflict != null && orderProgramConflict.getProgramName() != null && orderProgramConflict.getProgramName().length() > 0) {
                                  String dialogtitle="您已预订该时段节目" + orderProgramConflict.getProgramName();
                                  String content="是否替换为：" + (String)map.get("program_name")+"?";
                                	DialogUtil.showAlertDialog(activity, dialogtitle, content,"是","NO", new DialogBtnOnClickListener() {
									
									@Override
									public void onSubmit(DialogMessage dialogMessage) {

                                        channelService.deleteOrderProgram(orderProgramConflict.getProgramName(), orderProgramConflict.getOrderDate());
                                        OrderProgram orderProgramReplace = new OrderProgram();
                                        orderProgramReplace.setProgramName((String)map.get("program_name"));
                                        orderProgramReplace.setChannelName(channelName);
                                        orderProgramReplace.setOrderDate(DateUtils.getDayOfToday());
                                        orderProgramReplace.setChannelIndex((String)map.get("channel_index"));
                                        orderProgramReplace.setProgramStartTime((String)map.get("str_startTime"));
                                        orderProgramReplace.setProgramEndTime((String)map.get("str_endTime"));
                                        orderProgramReplace.setWeekIndex(DateUtils.getWeekIndexName(weekIndexOffset));
                                        orderProgramReplace.setStatus("已预约");
                                        if (channelService.saveOrderProgram(orderProgramReplace)) {
                                            vh.channelPlayButton.setText("已经预约");
                                            vh.channelPlayButton.setTextColor(getResources().getColor(R.color.orange));                                            
                                            Toast.makeText(activity, "节目预约成功", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(activity, "节目预约失败", Toast.LENGTH_SHORT).show();
                                        }
                                        if (dialogMessage.dialog!=null && dialogMessage.dialog.isShowing()) {
											dialogMessage.dialog.cancel();
										}
                                        notifyDataSetChanged();
									}
									
									@Override
									public void onCancel(DialogMessage dialogMessage) {
                                         vh.channelPlayButton.setText("可以预约");
                                         vh.channelPlayButton.setTextColor(getResources().getColor(R.color.white));
                                         if (dialogMessage.dialog!=null && dialogMessage.dialog.isShowing()) {
 											dialogMessage.dialog.cancel();
 										}
									}
								});
                                	
                                } else {
                                    //保存预约信息
                                    try {
                                        OrderProgram orderProgram = new OrderProgram();
                                        orderProgram.setChannelIndex((String)map.get("channel_index"));
                                        orderProgram.setStatus("已预约");
                                        orderProgram.setChannelName(channelName);
                                        
                                        
                                        
                                        String orderDate = DateUtils.getOrderDate(weekIndexOffset);
                                        orderProgram.setOrderDate(orderDate + (String)map.get("str_startTime"));
                                        orderProgram.setProgramName((String)map.get("program_name"));
                                        orderProgram.setProgramStartTime((String)map.get("str_startTime"));
                                        orderProgram.setProgramEndTime((String)map.get("str_endTime"));
                                        orderProgram.setWeekIndex(DateUtils.getWeekIndexName(weekIndexOffset));

                                        if (channelService.saveOrderProgram(orderProgram)) {                                            
                                            vh.channelPlayButton.setText("已经预约");
                                            vh.channelPlayButton.setTextColor(getResources().getColor(R.color.orange));
                                            Toast.makeText(activity, "节目预约成功", Toast.LENGTH_SHORT).show();
                                        } else {
                                            vh.channelPlayButton.setText("可以预约");
                                            vh.channelPlayButton.setTextColor(getResources().getColor(R.color.white));
                                            Toast.makeText(activity, "节目预约失败", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else if (vh.channelPlayButton.getText().equals("已经预约"))
                        {
                        	OrderProgram orderProgram = new OrderProgram(program);
                        	orderProgram.setWeekIndex(DateUtils.getWeekIndexName(weekIndexOffset));
                        	if (channelService.deleteOrderProgram(orderProgram)) {
                            	vh.channelPlayButton.setText("可以预约"); 
                                vh.channelPlayButton.setTextColor(getResources().getColor(R.color.white));
                                Toast.makeText(activity, "取消预约成功", Toast.LENGTH_SHORT).show();
	                        } else {
	                            Toast.makeText(activity, "取消预约失败", Toast.LENGTH_SHORT).show();
	                        }
                        }
                        notifyDataSetChanged();
                    }
                    
                });
            }
            else {
            	weekIndexOffset = 0;
            	vh.channelPlayButton.setText("节目信息"); 
            	vh.channelPlayButton.setTextColor(getResources().getColor(R.color.white));
            	vh.channelShouCang.setVisibility(View.VISIBLE);
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
			}
            

			vh.channelName.setText(serviceName);
			
            
            //设置数据
			if (map.containsKey("program_name")) {				
				String time="播放时间：" + DateUtils.getOrderDate(weekIndexOffset) + "\n" + map.get("str_startTime") + " - " + map.get("str_endTime") + "\n" + map.get("program_name");
				vh.channelPlayInfo.setText(time);
			}
			else {
				Program currentProgram=currentChannelPlayData.get(channelName);
	            if(currentProgram != null){
	                String time="正在播放:" + currentProgram.getProgramStartTime() + " - " + currentProgram.getProgramEndTime() + "\n" + currentProgram.getProgramName();
	                vh.channelPlayInfo.setText(time);
	            }else {
	                vh.channelPlayInfo.setText("无节目信息");
	            }
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
	
	private boolean findOrderProgram(List<OrderProgram> programs,Program program)
	{
		int weekIndexOffset = Integer.valueOf(program.getWeekIndex()) >= DateUtils.getWeekIndex(0)
        		? Integer.valueOf(program.getWeekIndex()) - DateUtils.getWeekIndex(0)
        		: 7 - DateUtils.getWeekIndex(0) + Integer.valueOf(program.getWeekIndex());
		
		for (OrderProgram orderProgram : programs) {
			if (orderProgram.getProgramName().equalsIgnoreCase(program.getProgramName())
					&& orderProgram.getChannelName().equalsIgnoreCase(program.getChannelName())
					&& orderProgram.getProgramEndTime().equalsIgnoreCase(program.getProgramEndTime())
					&& orderProgram.getProgramStartTime().equalsIgnoreCase(program.getProgramStartTime())
					&& orderProgram.getWeekIndex().equalsIgnoreCase(DateUtils.getWeekIndexName(weekIndexOffset))) {
				return true;
			}
		}
		return false;
	}
	@Override
	public void onDetach() {
		super.onDetach();
		if(broadcastReceiver != null)
		{
			getActivity().unregisterReceiver(broadcastReceiver);
		}
	}
}
