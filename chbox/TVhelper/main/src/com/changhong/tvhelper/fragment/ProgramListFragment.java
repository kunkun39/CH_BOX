package com.changhong.tvhelper.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.activity.TVChannelPlayActivity;
import com.changhong.tvhelper.activity.TVChannelProgramShowActivity;
import com.changhong.tvhelper.domain.OrderProgram;
import com.changhong.tvhelper.service.ChannelService;
import com.changhong.tvhelper.service.ClientGetCommandService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProgramListFragment extends Fragment {

    public static final String TAG = "programlistfragment";
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

        mRecyclerView = (RecyclerView) inflater.inflate(
                R.layout.recyclerview, container, false);

        return mRecyclerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void setData(ChannelService channelService,
                        List<OrderProgram> orderProgramList, List<Map<String, Object>> orderProgramShowData) {

        if (orderProgramList.size() > 0 && orderProgramShowData.size() > 0) {
            mchannelService = channelService;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(new RecyclerViewAdapter(getActivity(),
                    orderProgramList, orderProgramShowData));
        } else {
            return;
        }

    }

    public class RecyclerViewAdapter extends
            RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private Context mContext;
        private List<OrderProgram> mAllOrderData = new ArrayList<OrderProgram>();
        private List<Map<String, Object>> mOrderShowData = new ArrayList<Map<String, Object>>();

        public RecyclerViewAdapter(Context context, List<OrderProgram> orderProgramList,
                                   List<Map<String, Object>> orderProgramShowData) {

            this.mContext = context;
            mAllOrderData.clear();
            mOrderShowData.clear();
            mAllOrderData.addAll(orderProgramList);
            mOrderShowData.addAll(orderProgramShowData);

        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.activity_channel_program_yuyue_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(
                final RecyclerViewAdapter.ViewHolder holder, final int position) {
            final OrderProgram orderProgram = mAllOrderData.get(position);
            holder.channelName.setText((position + 1) + " "
                    + orderProgram.getChannelName());
            holder.channelPlayInfo.setText(orderProgram.getWeekIndex()
                    + "  "
                    + orderProgram.getProgramStartTime()
                    + "-"
                    + orderProgram.getProgramEndTime()
                    + "\n\n"
                    + StringUtils.getShortString(orderProgram.getProgramName(),
                    12));
            // 捕获异常，代表没有这个频道
            try {
                holder.channelLogo
                        .setImageResource(ClientGetCommandService.channelLogoMapping
                                .get(orderProgram.getChannelName()));
            } catch (Exception e) {
                holder.channelLogo.setImageResource(R.drawable.logotv);
            }

            /**
             * 观看直播
             */
            try {
                final Map<String, Object> map = mOrderShowData.get(position);
                holder.channelLogo.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyApplication.vibrator.vibrate(100);

                        TVChannelPlayActivity.name = orderProgram
                                .getChannelName();
                        TVChannelPlayActivity.path = ChannelService
                                .obtainChannlPlayURL(map);

                        Intent intent = new Intent(mContext,
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
            holder.channelYuyue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);

                    try {
                        // 取消预约操作
                        boolean success = mchannelService.deleteOrderProgram(
                                orderProgram.getProgramName(),
                                orderProgram.getOrderDate());

                        // 更新数据
                        if (success) {
                            for (Map<String, Object> map : mOrderShowData) {
                                if (map.get("channel_index").equals(
                                        orderProgram.getChannelIndex())) {
                                    mOrderShowData.remove(map);
                                    break;
                                }
                            }
                            mAllOrderData.remove(orderProgram);
                            notifyDataSetChanged();

                            Toast.makeText(mContext, "取消节目预约成功",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(mContext, "取消节目预约失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            /**
             * 查看频道节目
             */
            holder.channelPlayButton
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyApplication.vibrator.vibrate(100);

                            Intent intent = new Intent(mContext,
                                    TVChannelProgramShowActivity.class);
                            intent.putExtra("channelName",
                                    orderProgram.getChannelName());
                            intent.putExtra("channelIndex",
                                    orderProgram.getChannelIndex());
                            startActivity(intent);
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return mAllOrderData.size() == mOrderShowData.size() ? mAllOrderData
                    .size() : 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView channelLogo;
            public TextView channelName;
            public TextView channelPlayInfo;
            public TextView channelYuyue;
            public TextView channelPlayButton;

            public final View mView;

            public ViewHolder(View view) {

                super(view);
                mView = view;

                channelLogo = (ImageView) view.findViewById(R.id.channel_logo);
                channelName = (TextView) view.findViewById(R.id.channel_name);
                channelPlayInfo = (TextView) view
                        .findViewById(R.id.channel_play_info);
                channelYuyue = (TextView) view.findViewById(R.id.program_yuyue);
                channelPlayButton = (TextView) view
                        .findViewById(R.id.channel_play_button);

            }
        }

    }

}
