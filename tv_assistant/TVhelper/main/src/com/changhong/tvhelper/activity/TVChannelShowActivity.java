package com.changhong.tvhelper.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.db.sqlite.DatabaseContainer;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.setting.utils.NetEstimateUtils;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.domain.Program;
import com.changhong.tvhelper.service.ChannelService;
import com.changhong.tvhelper.service.ClientGetCommandService;

public class TVChannelShowActivity extends Activity {

    private static final String TAG = "TVChannelShowActivity";

    /**
     * message handler
     */
    public static Handler mHandler = null;

    /**************************************************IP连接部分*******************************************************/

    private ArrayAdapter<String> adapterString = null;
    public static TextView title = null;
    private ListView clients = null;
    private Button list = null;
    private Button back = null;

    /**************************************************频道部分*******************************************************/

    private List<Map<String, Object>> channelShowData = new ArrayList<Map<String, Object>>();
    private ListView channels = null;
    private ChannelAdapter adapter = null;

    /**
     * id which indicate which channel tab the user selected
     */
    public static int selectedChanncelTabIndex = 0;
    private TextView ALL;
    private TextView HD;
    private TextView WS;
    private TextView SE;
    private TextView YS;
    private TextView TS;
    private int height = 0;

    /**
     * ***********************************************EPG查询*******************************************************
     */

    private Map<String, Program> currentChannelPlayData = new HashMap<String, Program>();
    private List<String> allShouChangChannel = new ArrayList<String>();
    private ChannelService channelService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_channel_view);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        height = metric.heightPixels;     // 屏幕高度（像素）

        NetEstimateUtils.noticeEndUserNetworkStatus(this);

        channelService = new ChannelService();

        initViewAndEvent();

        initData();
    }

    private void initViewAndEvent() {
        channels = (ListView) findViewById(R.id.channellist);
        title = (TextView) findViewById(R.id.title);
        clients = (ListView) findViewById(R.id.clients);
        list = (Button) findViewById(R.id.btn_list);
        back = (Button) findViewById(R.id.btn_back);

        ALL = (TextView) findViewById(R.id.all);
        HD = (TextView) findViewById(R.id.hd);
        WS = (TextView) findViewById(R.id.ws);
        SE = (TextView) findViewById(R.id.se);
        YS = (TextView) findViewById(R.id.ys);
        TS = (TextView) findViewById(R.id.ts);

        ALL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedChanncelTabIndex = 0;
                MyApplication.vibrator.vibrate(100);
                mHandler.sendEmptyMessage(0);
            }
        });
        HD.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedChanncelTabIndex = 1;
                MyApplication.vibrator.vibrate(100);
                mHandler.sendEmptyMessage(0);
            }
        });
        WS.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedChanncelTabIndex = 2;
                MyApplication.vibrator.vibrate(100);
                mHandler.sendEmptyMessage(0);
            }
        });
        SE.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedChanncelTabIndex = 3;
                MyApplication.vibrator.vibrate(100);
                mHandler.sendEmptyMessage(0);
            }
        });
        YS.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedChanncelTabIndex = 4;
                MyApplication.vibrator.vibrate(100);
                mHandler.sendEmptyMessage(0);
            }
        });
        TS.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedChanncelTabIndex = 5;
                MyApplication.vibrator.vibrate(100);
                mHandler.sendEmptyMessage(0);
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        channelShowData.clear();
                        switch (selectedChanncelTabIndex) {
                            case 0://全部频道
                                if (height >= 1080) {
                                    for (int j = 0; j < ClientSendCommandService.channelData.size(); j++) {
                                        channelShowData.add(ClientSendCommandService.channelData.get(j));
                                    }
                                } else {
                                    //屏蔽高清节目
                                    for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
                                        if (((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("HD".toLowerCase()) >= 0
                                                || ((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("高清".toLowerCase()) >= 0) {
                                            //高清节目不添加
                                        } else {
                                            channelShowData.add(ClientSendCommandService.channelData.get(i));
                                        }
                                    }
                                }
                                break;
                            case 1://高清频道
                                if (height >= 1080) {
                                    for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
                                        if (((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("HD".toLowerCase()) >= 0
                                                || ((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("高清".toLowerCase()) >= 0) {
                                            channelShowData.add(ClientSendCommandService.channelData.get(i));
                                        }
                                    }
                                } else {
                                    Toast.makeText(TVChannelShowActivity.this, "由于您手机分辨率小于1080p,为您屏蔽了高清节目！", Toast.LENGTH_LONG).show();
                                }
                                break;
                            case 2://卫视频道
                                for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
                                    if (((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("卫视".toLowerCase()) >= 0) {
                                        channelShowData.add(ClientSendCommandService.channelData.get(i));
                                    }
                                }
                                break;
                            case 3://少儿频道
                                for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
                                    if (((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("少儿".toLowerCase()) >= 0
                                            || ((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("卡通".toLowerCase()) >= 0
                                            || ((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("动漫".toLowerCase()) >= 0
                                            || ((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("成长".toLowerCase()) >= 0) {
                                        channelShowData.add(ClientSendCommandService.channelData.get(i));
                                    }
                                }
                                break;
                            case 4://央视频道
                                for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
                                    if (((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("中央".toLowerCase()) >= 0
                                            || ((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("cctv".toLowerCase()) >= 0) {
                                        channelShowData.add(ClientSendCommandService.channelData.get(i));
                                    }
                                }
                                break;
                            case 5://同时看
                                if (ClientSendCommandService.playingChannelData.isEmpty()) {
                                    //让DTV补发当前节目信息，刷新同时看
                                    ClientSendCommandService.msgSwitchChannel = "GetCurrentChannelInfo";
                                    ClientSendCommandService.handler.sendEmptyMessage(3);
                                } else {
                                    for (int j = 0; j < ClientSendCommandService.playingChannelData.size(); j++) {
                                        channelShowData.add(ClientSendCommandService.playingChannelData.get(j));
                                    }
                                }
                                break;
                            default:
                                break;
                        }

                        /**
                         * 触发频道TAB变化
                         */
                        switchChannelTab();

                        /**
                         * 触发频道列表变换
                         */
                        adapter.notifyDataSetChanged();
                        break;
                    case 1:
                        adapter = new ChannelAdapter(TVChannelShowActivity.this);
                        channels.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }

        };

        /**
         * IP part
         */
        adapterString = new ArrayAdapter<String>(TVChannelShowActivity.this, android.R.layout.simple_list_item_1, ClientSendCommandService.serverIpList);
        clients.setAdapter(adapterString);
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
                adapterString.notifyDataSetChanged();
                ClientSendCommandService.serverIP = ClientSendCommandService.serverIpList.get(arg2);
                title.setText("CHBOX");
                ClientSendCommandService.handler.sendEmptyMessage(2);
                clients.setVisibility(View.GONE);

                while (ClientSendCommandService.searchChannelFinished) {
                    mHandler.sendEmptyMessage(0);
                }
            }
        });
        list.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClientSendCommandService.serverIpList.isEmpty()) {
                    Toast.makeText(TVChannelShowActivity.this, "没有发现长虹智能机顶盒，请确认盒子和手机连在同一个路由器?", Toast.LENGTH_LONG).show();
                } else {
                    clients.setVisibility(View.VISIBLE);
                }
            }
        });
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                finish();
            }
        });
    }

    private void initData() {
        channelShowData.clear();
        channelShowData.addAll(ClientSendCommandService.channelData);

        new Thread(new Runnable() {
            @Override
            public void run() {
                /**
                 * 初始化DB
                 */
                if (MyApplication.databaseContainer == null) {
                    MyApplication.databaseContainer = new DatabaseContainer(TVChannelShowActivity.this);
                }

                try {
                    allShouChangChannel = channelService.getAllChannelShouCangs();
                    currentChannelPlayData = channelService.searchCurrentChannelPlay();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(1);
            }
        }).start();

        /**
         * 默认选中全部频道0
         */
        selectedChanncelTabIndex = 0;
        switchChannelTab();
    }

    private void switchChannelTab() {
        ALL.setTextColor(this.getResources().getColor(R.color.white));
        HD.setTextColor(this.getResources().getColor(R.color.white));
        WS.setTextColor(this.getResources().getColor(R.color.white));
        SE.setTextColor(this.getResources().getColor(R.color.white));
        YS.setTextColor(this.getResources().getColor(R.color.white));
        TS.setTextColor(this.getResources().getColor(R.color.white));

        switch (selectedChanncelTabIndex) {
            case 0:
                ALL.setTextColor(this.getResources().getColor(R.color.orange));
                break;
            case 1:
                HD.setTextColor(this.getResources().getColor(R.color.orange));
                break;
            case 2:
                WS.setTextColor(this.getResources().getColor(R.color.orange));
                break;
            case 3:
                SE.setTextColor(this.getResources().getColor(R.color.orange));
                break;
            case 4:
                YS.setTextColor(this.getResources().getColor(R.color.orange));
                break;
            case 5:
                TS.setTextColor(this.getResources().getColor(R.color.orange));
            break;
            default:
                break;
        }
    }

    /**
     * ****************************************************系统方法重载部分********************************************
     */

    @Override
    protected void onResume() {
        super.onResume();
        if (ClientSendCommandService.titletxt != null) {
            title.setText(ClientSendCommandService.titletxt);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
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
            return channelShowData.size();
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
                convertView = minflater.inflate(R.layout.activity_channel_show_item, null);
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
            vh.channelLogo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    Map<String, Object> map = channelShowData.get(position);
                    TVChannelPlayActivity.name = (String) map.get("service_name");
                    TVChannelPlayActivity.path = ChannelService.obtainChannlPlayURL(map);

                    Intent intent = new Intent(TVChannelShowActivity.this, TVChannelPlayActivity.class);
                    String name = (String) map.get("service_name");
                    intent.putExtra("channelname", name);
                    startActivity(intent);
                }
            });

            final String channelServiceId = (String) channelShowData.get(position).get("service_id");
            final String channelName = (String) channelShowData.get(position).get("service_name");
            final String channelIndex = (String) channelShowData.get(position).get("channel_index");

            /**
             * 收藏频道和取消收藏
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
                        //取消收藏操作
                        boolean success = channelService.cancelChannelShouCang(channelServiceId);
                        if (success) {
                            vh.channelShouCang.setText("收藏\n频道");
                            vh.channelShouCang.setTextColor(getResources().getColor(R.color.white));
                            allShouChangChannel.remove(channelServiceId);

                            Toast.makeText(TVChannelShowActivity.this, "取消频道收藏成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TVChannelShowActivity.this, "取消频道收藏失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //收藏操作
                        boolean success = channelService.channelShouCang(channelServiceId);
                        if (success) {
                            vh.channelShouCang.setText("取消\n收藏");
                            vh.channelShouCang.setTextColor(getResources().getColor(R.color.orange));
                            allShouChangChannel.add(channelServiceId);

                            Toast.makeText(TVChannelShowActivity.this, "频道收藏成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TVChannelShowActivity.this, "频道收藏失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            /**
             * 查看频道节目
             */
            vh.channelPlayButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);

                    Intent intent = new Intent(TVChannelShowActivity.this, TVChannelProgramShowActivity.class);
                    intent.putExtra("channelName", channelName);
                    intent.putExtra("channelIndex", channelIndex);
                    startActivity(intent);
                }
            });

            /**
             * 设置数据
             */
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
