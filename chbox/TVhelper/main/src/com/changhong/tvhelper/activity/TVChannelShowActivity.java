package com.changhong.tvhelper.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.AppConfig;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.widgets.BoxSelecter;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.domain.Program;
import com.changhong.tvhelper.fragment.RecycleViewFragment;
import com.changhong.tvhelper.service.ChannelService;
import com.changhong.tvhelper.service.ClientGetCommandService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class TVChannelShowActivity extends AppCompatActivity implements Observer, RecycleViewFragment.OnFragmentInteractionListener {

    private static final String TAG = "TVChannelShowActivity";


    private DrawerLayout mDrawerLayout;
    ViewPager pages;
    private TabLayout mTabLayout;

//    private RecyclerView mRecyclerView;
//    private RecyclerViewAdapter mRecyclerViewAdapter;

    /**
     * ***********************************************IP连接部分******************************************************
     */

    private BoxSelecter ipSelecter = null;

    /**
     * ***********************************************频道部分******************************************************
     */

    //private List<Map<String, Object>> channelShowData = new ArrayList<Map<String, Object>>();

    /**
     * id which indicate which channel tab the user selected
     */
    public static int selectedChanncelTabIndex = 0;
    private int height = 0;

    /**
     * ***********************************************EPG查询*******************************************************
     */

    private Map<String, Program> currentChannelPlayData = new HashMap<String, Program>();
    private List<String> allShouChangChannel = new ArrayList<String>();
    private ChannelService channelService;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_channel_view_new);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        height = metric.heightPixels;     // 屏幕高度（像素）

        //提示用户的网络连接情况，由于川网局域网内，只能通通过WIFI连接，所以用不着显示
        //NetEstimateUtils.noticeEndUserNetworkStatus(this);

        channelService = new ChannelService(this);
        ClientGetCommandService.mDataNotifier.addObserver(this);
        initViewAndEvent();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.touying, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();
        } else if (item.getItemId() == R.id.ipbutton) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        return true;
    }


    private void setupTab() {

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        List<String> titles = new ArrayList<String>();
        titles.add("  全部  ");
        titles.add("  高清  ");
        titles.add("  卫视  ");
        titles.add("  少儿  ");
        titles.add("  央视  ");

        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(2)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(3)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(4)));

        pages.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return RecycleViewFragment.newInstance(new RecyclerViewAdapter(getApplicationContext(), position), null);
            }

            @Override
            public int getCount() {
                return 5;
            }
        });
        mTabLayout.setupWithViewPager(pages);
    }


    private void initViewAndEvent() {

//        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        pages = (ViewPager) findViewById(R.id.content_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.tvhelper_drawer);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

//        mRecyclerView.setLayoutManager(new LinearLayoutManager(TVChannelShowActivity.this));
//        mRecyclerViewAdapter = new RecyclerViewAdapter(this,0);
//        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        setupTab();
//		});

        /**
         * IP part
         */
        ipSelecter = new BoxSelecter(this, (TextView) findViewById(R.id.title), (ListView) findViewById(R.id.clients), new Handler(getMainLooper()));


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initData();
            }
        };
        this.registerReceiver(broadcastReceiver, new IntentFilter(AppConfig.BROADCAST_INTENT_EPGDB_UPDATE));
    }

    List<Map<String, Object>> generateList(int position) {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        switch (position) {
            case 0://全部频道
                if (height >= 1080) {
                    for (int j = 0; j < ClientSendCommandService.channelData.size(); j++) {
                        data.add(ClientSendCommandService.channelData.get(j));
                    }
                } else {
                    //屏蔽高清节目
                    for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
                        if (((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("HD".toLowerCase()) >= 0
                                || ((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("高清".toLowerCase()) >= 0) {
                            //高清节目不添加
                        } else {
                            data.add(ClientSendCommandService.channelData.get(i));
                        }
                    }
                }
                break;
            case 1://高清频道
                if (height >= 1080) {
                    for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
                        if (((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("HD".toLowerCase()) >= 0
                                || ((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("高清".toLowerCase()) >= 0) {
                            data.add(ClientSendCommandService.channelData.get(i));
                        }
                    }
                } else {
                    Toast.makeText(TVChannelShowActivity.this, R.string.phone_resolution_not_satisfied, Toast.LENGTH_LONG).show();
                }
                break;
            case 2://卫视频道
                for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
                    if (((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("卫视".toLowerCase()) >= 0) {
                        data.add(ClientSendCommandService.channelData.get(i));
                    }
                }
                break;
            case 3://少儿频道
                for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
                    if (((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("少儿".toLowerCase()) >= 0
                            || ((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("卡通".toLowerCase()) >= 0
                            || ((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("动漫".toLowerCase()) >= 0
                            || ((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("成长".toLowerCase()) >= 0) {
                        data.add(ClientSendCommandService.channelData.get(i));
                    }
                }
                break;
            case 4://央视频道
                for (int i = 0; i < ClientSendCommandService.channelData.size(); i++) {
                    if (((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("中央".toLowerCase()) >= 0
                            || ((String) ClientSendCommandService.channelData.get(i).get("service_name")).toLowerCase().indexOf("cctv".toLowerCase()) >= 0) {
                        data.add(ClientSendCommandService.channelData.get(i));
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
                        data.add(ClientSendCommandService.playingChannelData.get(j));
                    }
                }
                break;
            default:
                break;
        }
        return data;
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                /**
                 * 初始化DB
                 */

                try {
                    allShouChangChannel = channelService.getAllChannelShouCangs();
                    currentChannelPlayData = channelService.searchCurrentChannelPlay();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 通知Handler扫描图片完成
                if (pages != null)
                    pages.getAdapter().notifyDataSetChanged();
            }
        }).start();

        /**
         * 默认选中全部频道0
         */
        selectedChanncelTabIndex = 0;
    }


    /**
     * ****************************************************系统方法重载部分********************************************
     */

    @Override
    protected void onResume() {
        super.onResume();

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
    public class RecyclerViewAdapter extends
            RecycleViewFragment.RecycleViewAdapter<RecyclerViewAdapter.ViewHolder> {

        private Context mContext;
        private int mPosition;
        List<Map<String, Object>> mData;

        public RecyclerViewAdapter(Context context, int position) {
            this.mContext = context;
            mPosition = position;
            mData = generateList(mPosition);

        }


        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.activity_channel_show_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(
                final RecyclerViewAdapter.ViewHolder holder, final int position) {

            /**
             * 观看直播
             */
            holder.channelLogo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    Map<String, Object> map = mData.get(position);

                    TVChannelPlayActivity.name = (String) map
                            .get("service_name");
                    TVChannelPlayActivity.path = ChannelService
                            .obtainChannlPlayURL(map);

                    Intent intent = new Intent(TVChannelShowActivity.this,
                            TVChannelPlayActivity.class);
                    String name = (String) map.get("service_name");
                    intent.putExtra("channelname", name);
                    startActivity(intent);
                }
            });

            final String channelServiceId = (String) mData.get(
                    position).get("service_id");
            String serviceName = (String) mData.get(position).get(
                    "service_name");
            if (StringUtils.hasLength(serviceName)) {
                serviceName = serviceName.trim();
            }
            final String channelName = serviceName;
            final String channelIndex = (String) mData.get(position)
                    .get("channel_index");

            /**
             * 收藏频道和取消收藏
             */
            if (allShouChangChannel.contains(channelServiceId)) {
                holder.channelShouCang.setText("取消\n收藏");
                holder.channelShouCang.setTextColor(getResources().getColor(
                        R.color.orange));
            } else {
                holder.channelShouCang.setText("收藏\n频道");
                holder.channelShouCang.setTextColor(getResources().getColor(
                        R.color.white));
            }

            holder.channelShouCang.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);

                    if (allShouChangChannel.contains(channelServiceId)) {
                        // 取消收藏操作
                        boolean success = channelService
                                .cancelChannelShouCang(channelServiceId);
                        if (success) {
                            holder.channelShouCang.setText("收藏\n频道");
                            holder.channelShouCang.setTextColor(getResources()
                                    .getColor(R.color.white));
                            allShouChangChannel.remove(channelServiceId);

                            Toast.makeText(TVChannelShowActivity.this,
                                    "取消频道收藏成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TVChannelShowActivity.this,
                                    "取消频道收藏失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // 收藏操作
                        boolean success = channelService
                                .channelShouCang(channelServiceId);
                        if (success) {
                            holder.channelShouCang.setText("取消\n收藏");
                            holder.channelShouCang.setTextColor(getResources()
                                    .getColor(R.color.orange));
                            allShouChangChannel.add(channelServiceId);

                            Toast.makeText(TVChannelShowActivity.this,
                                    "频道收藏成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TVChannelShowActivity.this,
                                    "频道收藏失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            /**
             * 查看频道节目
             */
            holder.channelPlayButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);

                    Intent intent = new Intent(TVChannelShowActivity.this,
                            TVChannelProgramShowActivity.class);
                    intent.putExtra("channelName", channelName);
                    intent.putExtra("channelIndex", channelIndex);
                    startActivity(intent);
                }
            });

            /**
             * 设置数据
             */
            if (ClientGetCommandService.channelLogoMapping.get(channelName) != null
                    && !ClientGetCommandService.channelLogoMapping.get(
                    channelName).equals("null")
                    && !ClientGetCommandService.channelLogoMapping.get(
                    channelName).equals("")) {
                holder.channelLogo
                        .setImageResource(ClientGetCommandService.channelLogoMapping
                                .get(channelName));
            } else {
                holder.channelLogo.setImageResource(R.drawable.logotv);
            }
            Log.e("", "channel Name " + channelName + " \n");
            holder.channelName.setText((position + 1) + "  " + channelName);
            Program program = currentChannelPlayData.get(channelName);
            if (program != null) {
                String time = "正在播放:"
                        + program.getProgramStartTime()
                        + " - "
                        + program.getProgramEndTime()
                        + "\n\n"
                        + StringUtils.getShortString(program.getProgramName(),
                        12);
                holder.channelPlayInfo.setText(time);
            } else {
                holder.channelPlayInfo.setText("无节目信息");
            }

        }

        @Override
        public int getItemCount() {
            return mData.size();
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
                channelPlayInfo = (TextView) view
                        .findViewById(R.id.channel_play_info);
                channelShouCang = (TextView) view
                        .findViewById(R.id.channel_shoucang);
                channelPlayButton = (TextView) view
                        .findViewById(R.id.channel_play_button);

            }
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        if (pages != null)
            pages.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
