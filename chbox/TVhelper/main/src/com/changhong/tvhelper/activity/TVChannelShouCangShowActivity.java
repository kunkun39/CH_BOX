package com.changhong.tvhelper.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.AppConfig;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.widgets.BoxSelecter;
import com.changhong.common.widgets.IpSelectorDataServer;
import com.changhong.touying.adapter.FragmentAdapter;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.domain.OrderProgram;
import com.changhong.tvhelper.domain.Program;
import com.changhong.tvhelper.fragment.ChannelListFragment;
import com.changhong.tvhelper.fragment.ProgramListFragment;
import com.changhong.tvhelper.service.ChannelService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vov.vitamio.utils.Log;

/**
 * Jack Wang
 */
public class TVChannelShouCangShowActivity extends AppCompatActivity {

    private static final String TAG = "TVChannelShouCangShowActivity";


    private DrawerLayout mDrawerLayout;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    ChannelListFragment mChannelListFragment = null;
    ProgramListFragment mProgramListFragment = null;
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


    /**
     * ***********************************************频道收藏部分********************
     * **********************************
     */

    private List<Map<String, Object>> channelShowData = new ArrayList<Map<String, Object>>();


    /**
     * ***********************************************节目预约部分********************
     * **********************************
     */
    public List<OrderProgram> orderProgramList = new ArrayList<OrderProgram>();
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
        Log.e("testhaha", "process is here");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.touying, menu);

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

    private void setupViewPager() {

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        List<String> titles = new ArrayList<String>();
        titles.add("  频道收藏  ");
        titles.add("  节目预约  ");
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));
        List<Fragment> fragments = new ArrayList<Fragment>();

        mChannelListFragment = new ChannelListFragment();
        mProgramListFragment = new ProgramListFragment();

        fragments.add(mChannelListFragment);
        fragments.add(mProgramListFragment);

        FragmentAdapter adapter =
                new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(adapter);
    }


    private void initViewAndEvent() {

        setContentView(R.layout.activity_channel_shoucang_view_new);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.tvhelper_drawer);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager();

        /**
         * 消息处理部分
         */
        mUiHandler = new IHandler(this);

        /**
         * IP part
         */
        ipSelecter = new BoxSelecter(this, (TextView) findViewById(R.id.title),
                (ListView) findViewById(R.id.clients), new Handler(getMainLooper()));

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

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
        Log.e("test", "process gets here or not");
    }

    private static class IHandler extends Handler {

        private TVChannelShouCangShowActivity theActivity = null;

        public IHandler(TVChannelShouCangShowActivity activity) {
            theActivity = activity;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:

                    theActivity.mChannelListFragment.setData(theActivity.channelService, theActivity.channelShowData,
                            theActivity.allShouChangChannel,
                            theActivity.currentChannelPlayData);

                    Log.d("testcase","case 0");

                    break;
                case 1:
                    theActivity.mProgramListFragment.setData(theActivity.channelService, theActivity.orderProgramList,
                            theActivity.orderProgramShowData);
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
                    mUiHandler.sendEmptyMessage(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 通知Handler扫描收藏节目完成
//            mUiHandler.sendEmptyMessage(0);
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
                    mUiHandler.sendEmptyMessage(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * **********************************************系统方法重载*********************
     * ********************************
     */

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "onResume is running");
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

}
