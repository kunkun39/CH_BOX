package com.changhong.tvhelper.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import com.changhong.common.db.sqlite.DatabaseContainer;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.DateUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.widgets.BidirSlidingLayout;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.domain.OrderProgram;
import com.changhong.tvhelper.domain.Program;
import com.changhong.tvhelper.service.ChannelService;
import com.changhong.tvhelper.service.ClientGetCommandService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Jack Wang
 */
public class TVChannelShouCangShowActivity extends Activity {

    private static final String TAG = "TVChannelShouCangShowActivity";
//    private BidirSlidingLayout bidirSlidingLayout;
    /**
     * message handler
     */
    public static Handler mHandler = null;

    /**
     * ***********************************************IP连接部分******************************************************
     */

    private BoxSelectAdapter ipAdapter = null;
    public static TextView title = null;
    private ListView clients = null;
    private Button list = null;
    private Button back = null;

    /**
     * ***********************************************频道收藏部分******************************************************
     */

    private List<Map<String, Object>> channelShowData = new ArrayList<Map<String, Object>>();
    private ListView channelOrProgramList = null;
    private ChannelAdapter channelAdapter = null;
    private TextView channelText;

    /**
     * ***********************************************节目预约部分******************************************************
     */
    public static List<OrderProgram> orderProgramList = new ArrayList<OrderProgram>();
    private OrderProgramAdapter orderProgramAdapter;
    private TextView orderProgramText;
    private List<Map<String, Object>> orderProgramShowData = new ArrayList<Map<String, Object>>();

    /**
     * **********************************************EPG查询*******************************************************
     */

    private Map<String, Program> currentChannelPlayData = new HashMap<String, Program>();
    private List<String> allShouChangChannel = new ArrayList<String>();
    private ChannelService channelService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        channelService = new ChannelService();

        initViewAndEvent();

        initData();
    }

    private void initViewAndEvent() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_channel_shoucang_view);

        channelOrProgramList = (ListView) findViewById(R.id.channel_program_list);
        title = (TextView) findViewById(R.id.title);
        clients = (ListView) findViewById(R.id.clients);
        list = (Button) findViewById(R.id.btn_list);
        back = (Button) findViewById(R.id.btn_back);
        channelText = (TextView) findViewById(R.id.text_channel_shoucang);
        orderProgramText = (TextView) findViewById(R.id.text_channel_program_yuyue);
//        bidirSlidingLayout = (BidirSlidingLayout) findViewById(R.id.bidir_sliding_layout);
//        ImageButton collection_smb = (ImageButton) findViewById(R.id.collection_sidemunubutton);
//
//        collection_smb.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				bidirSlidingLayout.clickSideMenu();
//			}
//		});
//		
//		bidirSlidingLayout.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				bidirSlidingLayout.closeRightMenu();
//			}
//		});
		
        /**
         * IP part
         */
        ipAdapter = new BoxSelectAdapter(TVChannelShouCangShowActivity.this, ClientSendCommandService.serverIpList);
        clients.setAdapter(ipAdapter);
        clients.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                clients.setVisibility(View.GONE);
                return false;
            }
        });
        clients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ipAdapter.notifyDataSetChanged();
                ClientSendCommandService.serverIP = ClientSendCommandService.serverIpList.get(arg2);
                title.setText(ClientSendCommandService.getCurrentConnectBoxName());
                ClientSendCommandService.handler.sendEmptyMessage(2);
                clients.setVisibility(View.GONE);

                while (ClientSendCommandService.searchChannelFinished) {
                    mHandler.sendEmptyMessage(0);
                }
            }
        });
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClientSendCommandService.serverIpList.isEmpty()) {
                    Toast.makeText(TVChannelShouCangShowActivity.this, "没有发现长虹智能机顶盒，请确认盒子和手机连在同一个路由器?", Toast.LENGTH_LONG).show();
                } else {
                    clients.setVisibility(View.VISIBLE);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                finish();
            }
        });

        /**
         * 消息处理部分
         */
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        if (channelAdapter != null) {
                            channelOrProgramList.setAdapter(channelAdapter);
                            channelAdapter.notifyDataSetChanged();
                        }
                        break;
                    case 1:
                        if (orderProgramAdapter != null) {
                            channelOrProgramList.setAdapter(orderProgramAdapter);
                            orderProgramAdapter.notifyDataSetChanged();
                        }
                        break;
                    case 2:
                        if (!StringUtils.hasLength(ClientSendCommandService.serverIP)) {
                            Toast.makeText(TVChannelShouCangShowActivity.this, "没有发现长虹智能机顶盒，请确认盒子和手机连在同一个路由器?", Toast.LENGTH_SHORT).show();
                        }

                        //重新加载预约频道数据并刷新Adapter
                        try {
                            channelAdapter = new ChannelAdapter(TVChannelShouCangShowActivity.this);
                            channelOrProgramList.setAdapter(channelAdapter);
                            channelText.setTextColor(getResources().getColor(R.color.orange));

                            channelText.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MyApplication.vibrator.vibrate(100);

                                    if (allShouChangChannel.isEmpty()) {
                                        Toast.makeText(TVChannelShouCangShowActivity.this, "频道收藏为空", Toast.LENGTH_SHORT).show();
                                    }

                                    channelText.setTextColor(getResources().getColor(R.color.orange));
                                    orderProgramText.setTextColor(getResources().getColor(R.color.white));
                                    channelOrProgramList.setAdapter(channelAdapter);
                                    channelAdapter.notifyDataSetChanged();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        //重新加载预约节目数据并刷新Adapter
                        try {
                            orderProgramAdapter = new OrderProgramAdapter(TVChannelShouCangShowActivity.this);
                            orderProgramAdapter.notifyDataSetChanged();

                            orderProgramText.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MyApplication.vibrator.vibrate(100);

                                    if (orderProgramList.size() <= 0) {
                                        Toast.makeText(TVChannelShouCangShowActivity.this, "预约节目为空", Toast.LENGTH_LONG).show();
                                    } else if (ClientSendCommandService.serverIpList.isEmpty()) {
                                        channelOrProgramList.setVisibility(View.INVISIBLE);
                                    }

                                    orderProgramText.setTextColor(getResources().getColor(R.color.orange));
                                    channelText.setTextColor(getResources().getColor(R.color.white));
                                    channelOrProgramList.setAdapter(orderProgramAdapter);
                                    orderProgramAdapter.notifyDataSetChanged();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }

        };
    }

    private void initData() {
        channelShowData.clear();
        orderProgramShowData.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                /**
                 * 初始化DB
                 */
                if (MyApplication.databaseContainer == null) {
                    MyApplication.databaseContainer = new DatabaseContainer(TVChannelShouCangShowActivity.this);
                }

                try {
                    if (StringUtils.hasLength(ClientSendCommandService.serverIP)) {
                        allShouChangChannel = channelService.getAllChannelShouCangs();
                        currentChannelPlayData = channelService.searchCurrentChannelPlay();
                        int channelSize = ClientSendCommandService.channelData.size();

                        for (int i = 0; i < channelSize; i++) {
                            Map<String, Object> map = ClientSendCommandService.channelData.get(i);
                            String channelServiceId = (String) map.get("service_id");
                            if (allShouChangChannel.contains(channelServiceId)) {
                                channelShowData.add(map);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 通知Handler扫描收藏节目完成
                mHandler.sendEmptyMessage(2);
            }
        }).start();



    }

    class OrderProgramThread extends Thread {
        @Override
        public void run() {

            if (MyApplication.databaseContainer == null) {
                MyApplication.databaseContainer = new DatabaseContainer(TVChannelShouCangShowActivity.this);
            }
            try {
                if (StringUtils.hasLength(ClientSendCommandService.serverIP)) {
                    orderProgramList = channelService.findAllOrderPrograms();
                    int channelSize = ClientSendCommandService.channelData.size();
                    for (OrderProgram orderProgram:orderProgramList) {
                        String channelIndex=orderProgram.getChannelIndex();
                        for (int i=0;i<channelSize;i++) {
                            Map<String, Object> map = ClientSendCommandService.channelData.get(i);
                            if (channelIndex.equals(map.get("channel_index"))) {
                                orderProgramShowData.add(map);
                            }

                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //通知Handler扫描预约节目完成
            mHandler.sendEmptyMessage(3);
        }
    }

    /**
     * 频道名称、频道ICON，频道当前信息
     */
    private class ChannelAdapter extends BaseAdapter {

        private LayoutInflater minflater;

        public ChannelAdapter(Context context) {
            this.minflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return allShouChangChannel.size() == channelShowData.size() ? allShouChangChannel.size() : 0;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            /**
             * VIEW HOLDER的配置
             */
            final ViewHolder vh;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = minflater.inflate(R.layout.activity_channel_shoucang_item, null);
                vh.channelLogo = (ImageView) convertView.findViewById(R.id.channel_logo);
                vh.channelName = (TextView) convertView.findViewById(R.id.channel_name);
                vh.channelPlayInfo = (TextView) convertView.findViewById(R.id.channel_play_info);
                vh.channelShouCang = (TextView) convertView.findViewById(R.id.channel_shoucang);
                vh.channelPlayButton = (TextView) convertView.findViewById(R.id.channel_play_button);

                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            /**
             * 观看直播
             */
            final Map<String, Object> map = channelShowData.get(position);
            vh.channelLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);

                    try {
                        TVChannelPlayActivity.name = (String) map.get("service_name");
                        TVChannelPlayActivity.path = ChannelService.obtainChannlPlayURL(map);

                        Intent intent = new Intent(TVChannelShouCangShowActivity.this, TVChannelPlayActivity.class);
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
                        //取消收藏操作
                        boolean success = channelService.cancelChannelShouCang(channelServiceId);
                        if (success) {
                            //更新数据
                            allShouChangChannel.remove(channelServiceId);
                            Map<String, Object> removeMap = null;
                            for (Map<String, Object> loop : channelShowData) {
                                String loopChannelServiceId = (String) loop.get("service_id");
                                if (channelServiceId.equals(loopChannelServiceId)) {
                                    removeMap = loop;
                                    break;
                                }
                            }
                            channelShowData.remove(removeMap);

                            //通知adapter更新数据
                            mHandler.sendEmptyMessage(0);

                            Toast.makeText(TVChannelShouCangShowActivity.this, "取消频道收藏成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TVChannelShouCangShowActivity.this, "取消频道收藏失败", Toast.LENGTH_SHORT).show();
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

                    Intent intent = new Intent(TVChannelShouCangShowActivity.this, TVChannelProgramShowActivity.class);
                    intent.putExtra("channelName", channelName);
                    intent.putExtra("channelIndex", channelIndex);
                    startActivity(intent);
                }
            });

            /**
             * 设置数据
             */
            try {
                if (ClientGetCommandService.channelLogoMapping.get((String) channelShowData.get(position).get("service_name")) != null && !ClientGetCommandService.channelLogoMapping.get((String) channelShowData.get(position).get("service_name")).equals("null") && !ClientGetCommandService.channelLogoMapping.get((String) channelShowData.get(position).get("service_name")).equals("")) {
                    vh.channelLogo.setImageResource(ClientGetCommandService.channelLogoMapping.get((String) channelShowData.get(position).get("service_name")));
                } else {
                    vh.channelLogo.setImageResource(R.drawable.logotv);
                }
                vh.channelName.setText((String) channelShowData.get(position).get("service_name"));
                String channelIdex = (String) channelShowData.get(position).get("channel_index");
                Program program = currentChannelPlayData.get(channelIdex);
                if (program != null) {
                    String time = "正在播放:" + program.getProgramStartTime() + " - " + program.getProgramEndTime() + "\n" + StringUtils.getShortString(program.getProgramName(), 12);
                    vh.channelPlayInfo.setText(time);
                } else {
                    vh.channelPlayInfo.setText("无节目信息");
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

        public OrderProgramAdapter(Context context) {
            this.minflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return orderProgramList.size() == orderProgramShowData.size() ? orderProgramList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return orderProgramList.get(position);
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
                convertView = minflater.inflate(R.layout.activity_channel_program_yuyue_item, null);
                vh.channelLogo = (ImageView) convertView.findViewById(R.id.channel_logo);
                vh.channelName = (TextView) convertView.findViewById(R.id.channel_name);
                vh.channelPlayInfo = (TextView) convertView.findViewById(R.id.channel_play_info);
                vh.channelYuyue = (TextView) convertView.findViewById(R.id.program_yuyue);
                vh.channelPlayButton = (TextView) convertView.findViewById(R.id.channel_play_button);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            final OrderProgram orderProgram = orderProgramList.get(position);
            vh.channelName.setText(orderProgram.getChannelName());
            vh.channelPlayInfo.setText(orderProgram.getWeekIndex() + "  " + orderProgram.getProgramStartTime() + "-" + orderProgram.getProgramEndTime() + "\n" + StringUtils.getShortString(orderProgram.getProgramName(), 12));
            //捕获异常，代表没有这个频道
            try {
                vh.channelLogo.setImageResource(ClientGetCommandService.channelLogoMapping.get(orderProgram.getChannelName()));
            } catch (Exception e) {
                vh.channelLogo.setImageResource(R.drawable.logotv);
            }

            /**
             * 观看直播
             */
            try {
                final Map<String, Object> map = orderProgramShowData.get(position);
                vh.channelLogo.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyApplication.vibrator.vibrate(100);

                        TVChannelPlayActivity.name = orderProgram.getChannelName();
                        TVChannelPlayActivity.path = ChannelService.obtainChannlPlayURL(map);

                        Intent intent = new Intent(TVChannelShouCangShowActivity.this, TVChannelPlayActivity.class);
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
                        //取消预约操作
                        boolean success = channelService.deleteOrderProgram(orderProgram.getProgramName(), orderProgram.getOrderDate());
                        //更新数据
                        if (success) {
                            for (Map<String, Object> map : orderProgramShowData) {
                                if (map.get("channel_index").equals(orderProgram.getChannelIndex())) {
                                    orderProgramShowData.remove(map);
                                    break;
                                }
                            }
                            orderProgramList.remove(orderProgram);
                            //通知adapter更新数据
                            mHandler.sendEmptyMessage(1);
                            Toast.makeText(TVChannelShouCangShowActivity.this, "取消节目预约成功", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(TVChannelShouCangShowActivity.this, "取消节目预约失败", Toast.LENGTH_SHORT).show();
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

                    Intent intent = new Intent(TVChannelShouCangShowActivity.this, TVChannelProgramShowActivity.class);
                    intent.putExtra("channelName", orderProgram.getChannelName());
                    intent.putExtra("channelIndex", orderProgram.getChannelIndex());
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
     * **********************************************系统方法重载*****************************************************
     */

    @Override
    protected void onResume() {
        super.onResume();
        if (ClientSendCommandService.titletxt != null) {
            title.setText(ClientSendCommandService.titletxt);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        //重新加载预约节目数据以更新预约节目UI
        orderProgramList.clear();
        orderProgramShowData.clear();
        new OrderProgramThread().start();

    }
    
//    @Override
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
