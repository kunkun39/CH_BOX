package com.changhong.tvhelper.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.service.ClientGetCommandService;

public class TVChannelSwitchDialog extends Dialog {

    public static Handler mHandler = null;
    private Context context;

    /**
     * all the channel data, which will copy from ClientSendCommandService.channelData
     */
    private List<Map<String, Object>> searchData = new ArrayList<Map<String, Object>>();
    private ImageAdapter adapter = null;

    /**
     * all the channel data
     */
    private GridView channels = null;

    /**
     * id which indicate which channel tab the user selected
     */
    public int selectedChanncelTabIndex = 0;
    private TextView ALL;
    private TextView HD;
    private TextView WS;
    private TextView SE;
    private TextView YS;

    private Button cancle;

    public TVChannelSwitchDialog(Context context) {
        super(context, R.style.InputTheme);
        this.context = context;

        initData();

        initViewAndEvent();
    }

    private void initData() {
        searchData.clear();
        for (int j = 0; j < ClientSendCommandService.channelData.size(); j++) {
            searchData.add(ClientSendCommandService.channelData.get(j));
        }
    }

    private void initViewAndEvent() {
        /**
         * init all views
         */
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.alpha = 0.85f;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        window.setGravity(Gravity.BOTTOM);
        setContentView(R.layout.activity_channel_switch);

        channels = (GridView) findViewById(R.id.dialogchannellist);
        ALL = (TextView) findViewById(R.id.dialogall);
        HD = (TextView) findViewById(R.id.dialoghd);
        WS = (TextView) findViewById(R.id.dialogws);
        SE = (TextView) findViewById(R.id.dialogse);
        YS = (TextView) findViewById(R.id.dialogys);
        cancle = (Button) findViewById(R.id.dialogcancle);

        /**
         * init all events
         */
        adapter = new ImageAdapter(context, searchData);
        channels.setAdapter(adapter);
        channels.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                MyApplication.vibrator.vibrate(100);

                ClientSendCommandService.msg = "key:dtv";
                ClientSendCommandService.handler.sendEmptyMessage(1);

                Map<String, Object> channelInfo = searchData.get(arg2);
                ClientSendCommandService.msgSwitchChannel = channelInfo.get("service_id") + "#" + channelInfo.get("tsId") + "#" + channelInfo.get("orgNId");
                ClientSendCommandService.handler.sendEmptyMessage(3);
            }
        });

        ALL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                selectedChanncelTabIndex = 0;
                mHandler.sendEmptyMessage(0);
            }
        });

        HD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                selectedChanncelTabIndex = 1;
                mHandler.sendEmptyMessage(0);
            }
        });

        WS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                selectedChanncelTabIndex = 2;
                mHandler.sendEmptyMessage(0);
            }
        });

        SE.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                selectedChanncelTabIndex = 3;
                mHandler.sendEmptyMessage(0);
            }
        });

        YS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                selectedChanncelTabIndex = 4;
                mHandler.sendEmptyMessage(0);
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                dismiss();
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        searchData.clear();
                        int size = ClientSendCommandService.channelData.size();

                        switch (selectedChanncelTabIndex) {
                            case 0://全部频道
                                for (int j = 0; j < size; j++) {
                                    searchData.add(ClientSendCommandService.channelData.get(j));
                                }
                                break;
                            case 1://高清频道
                                for (int i = 0; i < size; i++) {
                                    Map<String, Object> channelInfo = ClientSendCommandService.channelData.get(i);
                                    if (((String) channelInfo.get("service_name")).toLowerCase().indexOf("HD".toLowerCase()) >= 0
                                            || ((String) channelInfo.get("service_name")).toLowerCase().indexOf("高清".toLowerCase()) >= 0) {
                                        searchData.add(channelInfo);
                                    }
                                }
                                break;
                            case 2://卫视频道
                                for (int i = 0; i < size; i++) {
                                    Map<String, Object> channelInfo = ClientSendCommandService.channelData.get(i);
                                    if (((String) channelInfo.get("service_name")).toLowerCase().indexOf("卫视".toLowerCase()) >= 0) {
                                        searchData.add(channelInfo);
                                    }
                                }
                                break;
                            case 3://少儿频道
                                for (int i = 0; i < size; i++) {
                                    Map<String, Object> channelInfo = ClientSendCommandService.channelData.get(i);
                                    if (((String) channelInfo.get("service_name")).toLowerCase().indexOf("少儿".toLowerCase()) >= 0
                                            || ((String) channelInfo.get("service_name")).toLowerCase().indexOf("卡通".toLowerCase()) >= 0
                                            || ((String) channelInfo.get("service_name")).toLowerCase().indexOf("动漫".toLowerCase()) >= 0
                                            || ((String) channelInfo.get("service_name")).toLowerCase().indexOf("成长".toLowerCase()) >= 0) {
                                        searchData.add(channelInfo);
                                    }
                                }
                                break;
                            case 4://央视频道
                                for (int i = 0; i < size; i++) {
                                    Map<String, Object> channelInfo = ClientSendCommandService.channelData.get(i);
                                    if (((String) channelInfo.get("service_name")).toLowerCase().indexOf("中央".toLowerCase()) >= 0
                                            || ((String) channelInfo.get("service_name")).toLowerCase().indexOf("cctv".toLowerCase()) >= 0) {
                                        searchData.add(channelInfo);
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
                        adapter.initData(searchData);
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }

        };

        /**
         * 默认选中全部频道0
         */
        switchChannelTab();
    }

    private void switchChannelTab() {
        ALL.setTextColor(context.getResources().getColor(R.color.white));
        HD.setTextColor(context.getResources().getColor(R.color.white));
        WS.setTextColor(context.getResources().getColor(R.color.white));
        SE.setTextColor(context.getResources().getColor(R.color.white));
        YS.setTextColor(context.getResources().getColor(R.color.white));

        switch (selectedChanncelTabIndex) {
            case 0:
                ALL.setTextColor(context.getResources().getColor(R.color.orange));
                break;
            case 1:
                HD.setTextColor(context.getResources().getColor(R.color.orange));
                break;
            case 2:
                WS.setTextColor(context.getResources().getColor(R.color.orange));
                break;
            case 3:
                SE.setTextColor(context.getResources().getColor(R.color.orange));
                break;
            case 4:
                YS.setTextColor(context.getResources().getColor(R.color.orange));
                break;
            default:
                break;
        }
    }

    class ImageAdapter extends BaseAdapter {
        private LayoutInflater minflater;
        private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();

        public ImageAdapter(Context context, List<Map<String, Object>> data) {
            this.minflater = LayoutInflater.from(context);
            mData = data;
        }

        public void initData(List<Map<String, Object>> data) {
            mData = data;
        }

        public int getCount() {
            return mData.size();
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
                convertView = minflater.inflate(R.layout.activity_channel_switch_item, null);
                vh.txt1 = (TextView) convertView.findViewById(R.id.tvtxt);
                vh._v1 = (ImageView) convertView.findViewById(R.id.tvimg);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            Map<String, Object> channelInfo = mData.get(position);
            if (ClientGetCommandService.channelLogoMapping.get((String) channelInfo.get("service_name")) != null && !ClientGetCommandService.channelLogoMapping.get((String) channelInfo.get("service_name")).equals("null") && !ClientGetCommandService.channelLogoMapping.get((String) channelInfo.get("service_name")).equals("")) {
                vh._v1.setImageResource(ClientGetCommandService.channelLogoMapping.get((String) channelInfo.get("service_name")));
            } else {
                vh._v1.setImageResource(R.drawable.logotv);
            }
            vh.txt1.setText((position + 1) + "  " + (String)channelInfo.get("service_name"));

            return convertView;
        }

        public final class ViewHolder {
            public TextView txt1;
            public ImageView _v1;
        }
    }

}
