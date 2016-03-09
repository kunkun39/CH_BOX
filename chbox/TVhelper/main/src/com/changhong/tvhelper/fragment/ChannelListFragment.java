package com.changhong.tvhelper.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.activity.TVChannelPlayActivity;
import com.changhong.tvhelper.activity.TVChannelProgramShowActivity;
import com.changhong.tvhelper.domain.Program;
import com.changhong.tvhelper.service.ChannelService;
import com.changhong.tvhelper.service.ClientGetCommandService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelListFragment extends Fragment {

    public static final String TAG = "channellistfragment";
    private RecyclerView mRecyclerView;
    ChannelService mchannelService = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        RecyclerView mRecyclerView;

        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.recyclerview,
                container, false);

//        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView
//                .getContext()));
//
//        mRecyclerView.setAdapter(new RecyclerViewAdapter(getActivity(), null, null, null));

        return mRecyclerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void setData(ChannelService channelService, List<Map<String, Object>> channelShowData,
                        List<String> allShouChangChannel, Map<String, Program> currentChannelPlayData) {

        if (channelShowData.size() > 0 && allShouChangChannel.size() > 0 && currentChannelPlayData.size() > 0) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView
                    .getContext()));

            mRecyclerView.setAdapter(new RecyclerViewAdapter(getActivity(), channelShowData, allShouChangChannel, currentChannelPlayData));
            mchannelService = channelService;
        } else {
            return;
        }

    }

    public class RecyclerViewAdapter extends
            RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private Context mContext;
        private List<Map<String, Object>> mShouCangShowData = new ArrayList<Map<String, Object>>();
        private List<String> mallShouCangChannelData = new ArrayList<String>();
        private Map<String, Program> mShouCangCurrentChannelPlayData = new HashMap<String, Program>();

        public RecyclerViewAdapter(Context context, List<Map<String, Object>> ShouCangShowData,
                                   List<String> allShouCangChannelData, Map<String, Program> ShouCangCurrentChannelPlayData) {

            this.mContext = context;
            mShouCangShowData.clear();
            mallShouCangChannelData.clear();
            mShouCangCurrentChannelPlayData.clear();

            mShouCangShowData.addAll(ShouCangShowData);
            mallShouCangChannelData.addAll(allShouCangChannelData);
            mShouCangCurrentChannelPlayData.putAll(ShouCangCurrentChannelPlayData);
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.activity_channel_shoucang_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(
                final RecyclerViewAdapter.ViewHolder holder, final int position) {

            /**
             * 观看直播
             */
            final Map<String, Object> map = mShouCangShowData.get(position);

            holder.channelLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);

                    try {
                        TVChannelPlayActivity.name = (String) map
                                .get("service_name");
                        TVChannelPlayActivity.path = ChannelService
                                .obtainChannlPlayURL(map);
                        Intent intent = new Intent(mContext, TVChannelPlayActivity.class);
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
            holder.channelShouCang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    try {
                        // 取消收藏操作
                        boolean success = mchannelService
                                .cancelChannelShouCang(channelServiceId);
                        if (success) {
                            // 更新数据
                            mallShouCangChannelData.remove(channelServiceId);
                            Map<String, Object> removeMap = null;
                            for (Map<String, Object> loop : mShouCangShowData) {
                                String loopChannelServiceId = (String) loop
                                        .get("service_id");
                                if (channelServiceId
                                        .equals(loopChannelServiceId)) {
                                    removeMap = loop;
                                    break;
                                }
                            }
                            mShouCangShowData.remove(removeMap);

                            notifyDataSetChanged();

                            Toast.makeText(mContext,
                                    "取消频道收藏成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext,
                                    "取消频道收藏失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            /**
             * 查看频道节目
             */
            holder.channelPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    Intent intent = new Intent(mContext, TVChannelProgramShowActivity.class);
                    intent.putExtra("channelName", channelName);
                    intent.putExtra("channelIndex", channelIndex);
                    startActivity(intent);
                }
            });

            /**
             * 设置数据
             */
            try {
                String serviceName = (String) mShouCangShowData.get(position)
                        .get("service_name");
                if (StringUtils.hasLength(serviceName)) {
                    serviceName = serviceName.trim();
                }
                if (ClientGetCommandService.channelLogoMapping.get(serviceName) != null
                        && !ClientGetCommandService.channelLogoMapping.get(
                        serviceName).equals("null")
                        && !ClientGetCommandService.channelLogoMapping.get(
                        serviceName).equals("")) {
                    holder.channelLogo
                            .setImageResource(ClientGetCommandService.channelLogoMapping
                                    .get(serviceName));
                } else {
                    holder.channelLogo.setImageResource(R.drawable.logotv);
                }
                holder.channelName.setText((position + 1) + " " + channelName);

                Program program = mShouCangCurrentChannelPlayData
                        .get(channelName);
                if (program != null) {
                    String time = "正在播放:"
                            + program.getProgramStartTime()
                            + " - "
                            + program.getProgramEndTime()
                            + "\n\n"
                            + StringUtils.getShortString(
                            program.getProgramName(), 12);
                    holder.channelPlayInfo.setText(time);
                } else {
                    holder.channelPlayInfo.setText("无节目信息");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return mallShouCangChannelData.size() == mShouCangShowData.size() ? mallShouCangChannelData.size() : 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView channelLogo;
            public TextView channelName;
            public TextView channelPlayInfo;
            public TextView channelShouCang;
            public TextView channelPlayButton;

            public final View mView;

            public ViewHolder(View view) {

                super(view);

                mView = view;

                channelLogo = (ImageView) view.findViewById(R.id.channel_logo);
                channelName = (TextView) view.findViewById(R.id.channel_name);
                channelPlayInfo = (TextView) view.findViewById(R.id.channel_play_info);
                channelShouCang = (TextView) view.findViewById(R.id.channel_shoucang);
                channelPlayButton = (TextView) view.findViewById(R.id.channel_play_button);

            }
        }

    }

}