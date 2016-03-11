package com.changhong.tvhelper.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.changhong.common.adapter.ViewPageAdapter;
import com.changhong.common.fragment.RecycleViewFragment;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.Executor;
import com.changhong.tvhelper.R;
import com.changhong.common.fragment.TabFragment;
import com.changhong.tvhelper.service.ClientGetCommandService;

public class TVChannelSwitchDialog extends DialogFragment {

    public static Handler mHandler = null;

    List<String> dataNumber;
    List<TabFragment> fragments = new ArrayList<TabFragment>();
    /**
     * id which indicate which channel tab the user selected
     */

    private Button cancle;

    private View v;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        initData();
        setRetainInstance(true);
        setStyle(STYLE_NO_TITLE, 0);
        return super.onCreateDialog(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(v == null){
            v = inflater.inflate(R.layout.activity_channel_switch,container,false);
            initViewAndEvent();
        }
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        getDialog().getWindow().setLayout(metrics.widthPixels, getDialog().getWindow().getAttributes().height);
    }

    private void initData() {

        Resources res = getActivity().getResources();
        dataNumber = new ArrayList<String>();
        dataNumber.add(res.getString(R.string.all));
        dataNumber.add(res.getString(R.string.hd));
        dataNumber.add(res.getString(R.string.local_tv));
        dataNumber.add(res.getString(R.string.cartoon));
        dataNumber.add(res.getString(R.string.cctv));
    }

    private void initViewAndEvent() {
        /**
         * init all views
         */
        TabLayout tabs = (TabLayout)v.findViewById(R.id.channel_switch_tabs);
        ViewPager viewPager = (ViewPager)v.findViewById(R.id.channel_switch_viewpager);
        ViewPageAdapter pageAdapter = new ViewPageAdapter(getChildFragmentManager());

;
        Executor.execute(new Runnable() {
            @Override
            public void run() {
                final ViewPager viewPager = (ViewPager) v.findViewById(R.id.channel_switch_viewpager);
                while (viewPager.getAdapter() == null){
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (String name : dataNumber) {
                            TabLayout tabs = (TabLayout) v.findViewById(R.id.channel_switch_tabs);

                            TabFragment fragment = TabFragment.newInstance(new ChannelRecycleViewAdapter(name), name, 2);
                            fragments.add(fragment);
                            tabs.addTab(tabs.newTab().setText(name));
                            ((ViewPageAdapter) viewPager.getAdapter()).addItem(fragment);

                        }
                    }
                });

            }
        });
        viewPager.setAdapter(pageAdapter);
        tabs.setupWithViewPager(viewPager);
        tabs.setTabsFromPagerAdapter(pageAdapter);

        cancle = (Button) v.findViewById(R.id.dialogcancle);

        /**
         * init all events
         */

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().hide();
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(v == null)
                    return;

                List<Map<String, Object>> searchData = new ArrayList<Map<String, Object>>();
                switch (msg.what) {
                    case 0:
                        int size = ClientSendCommandService.channelData.size();

                        switch (((ViewPager) v.findViewById(R.id.viewpager)).getCurrentItem()) {
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
                        ((ChannelRecycleViewAdapter)fragments.get(((ViewPager) v.findViewById(R.id.viewpager)).getCurrentItem()).getmAdapter()).setmData(searchData);
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }

        };
    }



    class ChannelRecycleViewAdapter extends TabFragment.RecycleViewAdapter<ChannelRecycleViewAdapter.ViewHolder>{
        private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
        String kind;
        ChannelRecycleViewAdapter(String kind){
            this.kind = kind;
            init();
        }
        void init(){
            int size = ClientSendCommandService.channelData.size();
            List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();


            for(int i = 0;i < dataNumber.size() ;i ++){
                if (dataNumber.get(i).equalsIgnoreCase(kind)){
                    switch (i){
                        case 0:{
                            for (int j = 0; j < size; j++) {
                                Map<String, Object> channelInfo = ClientSendCommandService.channelData.get(j);
                                String name = ((String) channelInfo.get("service_name")).toLowerCase();
                                data.add(channelInfo);
                            }
                        }break;
                        case 1:{
                            for (int j = 0; j < size; j++) {
                                Map<String, Object> channelInfo = ClientSendCommandService.channelData.get(j);
                                String name = ((String) channelInfo.get("service_name")).toLowerCase();
                                if (name.indexOf("HD".toLowerCase()) >= 0
                                        || name.indexOf("高清".toLowerCase()) >= 0) {
                                    data.add(channelInfo);
                                }
                            }
                        }break;
                        case 2:{
                            for (int j = 0; j < size; j++) {
                                Map<String, Object> channelInfo = ClientSendCommandService.channelData.get(j);
                                String name = ((String) channelInfo.get("service_name")).toLowerCase();
                                if (name.indexOf("卫视".toLowerCase()) >= 0) {
                                    data.add(channelInfo);
                                }
                            }
                        }break;
                        case 3:{
                            for (int j = 0; j < size; j++) {
                                Map<String, Object> channelInfo = ClientSendCommandService.channelData.get(j);
                                String name = ((String) channelInfo.get("service_name")).toLowerCase();
                                if (name.indexOf("少儿".toLowerCase()) >= 0
                                        || name.indexOf("卡通".toLowerCase()) >= 0
                                        || name.indexOf("动漫".toLowerCase()) >= 0
                                        || name.indexOf("成长".toLowerCase()) >= 0){
                                    data.add(channelInfo);
                                }
                            }
                        }break;
                        case 4:{
                            for (int j = 0; j < size; j++) {
                                Map<String, Object> channelInfo = ClientSendCommandService.channelData.get(j);
                                String name = ((String) channelInfo.get("service_name")).toLowerCase();
                                 if (name.indexOf("中央".toLowerCase()) >= 0
                                        || name.indexOf("cctv".toLowerCase()) >= 0){
                                     data.add(channelInfo);
                                }
                            }
                        }break;
                    }
                }
            }
            setmData(data);
        }
        public ChannelRecycleViewAdapter setmData(List<Map<String, Object>> mData) {
            this.mData.clear();
            this.mData.addAll(mData);
            notifyDataSetChanged();
            return this;
        }

        @Override
        public ChannelRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder( LayoutInflater.from(getActivity()).inflate(R.layout.activity_channel_switch_item, null));
        }

        @Override
        public void onBindViewHolder(ChannelRecycleViewAdapter.ViewHolder holder, int position) {

            final Map<String, Object> channelInfo = mData.get(position);
            if (ClientGetCommandService.channelLogoMapping.get((String) channelInfo.get("service_name")) != null && !ClientGetCommandService.channelLogoMapping.get((String) channelInfo.get("service_name")).equals("null") && !ClientGetCommandService.channelLogoMapping.get((String) channelInfo.get("service_name")).equals("")) {
                holder.iv.setImageResource(ClientGetCommandService.channelLogoMapping.get((String) channelInfo.get("service_name")));
            } else {
                holder.iv.setImageResource(R.drawable.logotv);
            }
            holder.tv.setText((position + 1) + "  " + (String) channelInfo.get("service_name"));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);

                    ClientSendCommandService.msg = "key:dtv";
                    ClientSendCommandService.handler.sendEmptyMessage(1);

                    ClientSendCommandService.msgSwitchChannel = channelInfo.get("service_id") + "#" + channelInfo.get("tsId") + "#" + channelInfo.get("orgNId");
                    ClientSendCommandService.handler.sendEmptyMessage(3);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            TextView tv;
            ImageView iv;
            public ViewHolder(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.tvtxt);
                iv = (ImageView) itemView.findViewById(R.id.tvimg);
            }
        }
    }

}
