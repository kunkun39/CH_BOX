package com.changhong.tvhelper.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.changhong.common.db.sqlite.DatabaseContainer;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.DateUtils;
import com.changhong.common.utils.DialogUtil;
import com.changhong.common.utils.DialogUtil.DialogBtnOnClickListener;
import com.changhong.common.utils.DialogUtil.DialogMessage;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.domain.OrderProgram;
import com.changhong.tvhelper.domain.Program;
import com.changhong.tvhelper.service.ChannelService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jack Wang
 */
public class TVChannelProgramShowActivity extends Activity implements View.OnClickListener {

    private Handler mHandler = null;
    private Button back;
    private TextView title;

    /**
     * **********************************************频道信息*******************************************************
     */

    private String channelName;
    private String channelIndex;

    /**
     * **********************************************星期信息*******************************************************
     */

    private TextView first;
    private TextView second;
    private TextView third;
    private TextView fourth;
    private TextView five;
    private TextView six;
    private TextView seven;

    private int selectedTabIndex = 1;
    private int selectedWeekIndex = 1;
    private String orderDate;
    private String weekIndexName;
    private boolean shouldVibrate = false;


    /**
     * **********************************************EPG信息*******************************************************
     */

    private Map<String, List<Program>> programInfos = new HashMap<String, List<Program>>();
    private List<Program> weekPrograms = new ArrayList<Program>();
    private ListView programs = null;
    private ProgramAdapter adapter = null;

    private ChannelService channelService;
    List<OrderProgram> orderProgramList = new ArrayList<OrderProgram>();
    List<String> orderProgramKeys = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        channelService = new ChannelService();

        initData();

        initView();

        initEvent();
    }

    private void initData() {
        Intent intent = getIntent();
        channelName = intent.getStringExtra("channelName");
        channelIndex = intent.getStringExtra("channelIndex");
        selectedWeekIndex = DateUtils.getWeekIndex(0);
        weekIndexName = DateUtils.getWeekIndexName(0);
        orderDate = DateUtils.getOrderDate(0);
        orderProgramList = channelService.findAllOrderPrograms();
        for (OrderProgram orderProgram : orderProgramList) {
            orderProgramKeys.add(orderProgram.getProgramName() + "-" + orderProgram.getProgramStartTime() + "-" + orderProgram.getWeekIndex());
        }
    }

    private void initView() {
        /**
         * 设置View
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_channel_program);

        back = (Button) findViewById(R.id.btn_back);
        title = (TextView) findViewById(R.id.title);
        title.setText(channelName);

        first = (TextView) findViewById(R.id.first);
        second = (TextView) findViewById(R.id.second);
        third = (TextView) findViewById(R.id.third);
        fourth = (TextView) findViewById(R.id.fourth);
        five = (TextView) findViewById(R.id.five);
        six = (TextView) findViewById(R.id.six);
        seven = (TextView) findViewById(R.id.seven);


        programs = (ListView) findViewById(R.id.channel_program_details);

        /**
         * 设置星期的顺序
         */
        first.setTextColor(getResources().getColor(R.color.orange));
        first.setText(DateUtils.getWeekIndexName(0));
        second.setText(DateUtils.getWeekIndexName(1));
        third.setText(DateUtils.getWeekIndexName(2));
        fourth.setText(DateUtils.getWeekIndexName(3));
        five.setText(DateUtils.getWeekIndexName(4));
        six.setText(DateUtils.getWeekIndexName(5));
        seven.setText(DateUtils.getWeekIndexName(6));
    }

    private void initEvent() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                finish();
            }
        });

        first.setOnClickListener(this);
        second.setOnClickListener(this);
        third.setOnClickListener(this);
        fourth.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                weekPrograms = programInfos.get(String.valueOf(selectedWeekIndex));
                if (weekPrograms == null) {
                    weekPrograms = new ArrayList<Program>();
                }
                adapter = new ProgramAdapter(TVChannelProgramShowActivity.this);
                programs.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                /**
                 * 初始化DB
                 */
                if (MyApplication.databaseContainer == null) {
                    MyApplication.databaseContainer = new DatabaseContainer(TVChannelProgramShowActivity.this);
                }

                try {
                    programInfos = channelService.searchProgramInfosByName(channelName);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.first:
                selectedTabIndex = 1;
                selectedWeekIndex = DateUtils.getWeekIndex(0);
                weekIndexName = DateUtils.getWeekIndexName(0);
                orderDate = DateUtils.getOrderDate(0);
                shouldVibrate = true;

                break;
            case R.id.second:
                selectedTabIndex = 2;
                selectedWeekIndex = DateUtils.getWeekIndex(1);
                weekIndexName = DateUtils.getWeekIndexName(1);
                orderDate = DateUtils.getOrderDate(1);
                shouldVibrate = true;

                break;
            case R.id.third:
                selectedTabIndex = 3;
                selectedWeekIndex = DateUtils.getWeekIndex(2);
                weekIndexName = DateUtils.getWeekIndexName(2);
                orderDate = DateUtils.getOrderDate(2);
                shouldVibrate = true;

                break;
            case R.id.fourth:
                selectedTabIndex = 4;
                selectedWeekIndex = DateUtils.getWeekIndex(3);
                weekIndexName = DateUtils.getWeekIndexName(3);
                orderDate = DateUtils.getOrderDate(3);
                shouldVibrate = true;

                break;
            case R.id.five:
                selectedTabIndex = 5;
                selectedWeekIndex = DateUtils.getWeekIndex(4);
                weekIndexName = DateUtils.getWeekIndexName(4);
                orderDate = DateUtils.getOrderDate(4);
                shouldVibrate = true;

                break;
            case R.id.six:
                selectedTabIndex = 6;
                selectedWeekIndex = DateUtils.getWeekIndex(5);
                weekIndexName = DateUtils.getWeekIndexName(5);
                orderDate = DateUtils.getOrderDate(5);
                shouldVibrate = true;

                break;
            case R.id.seven:
                selectedTabIndex = 7;
                selectedWeekIndex = DateUtils.getWeekIndex(6);
                weekIndexName = DateUtils.getWeekIndexName(6);
                orderDate = DateUtils.getOrderDate(6);
                shouldVibrate = true;

                break;
            default:
                break;
        }

        if (shouldVibrate) {
            MyApplication.vibrator.vibrate(100);
            controlWeekDaySelected();
            mHandler.sendEmptyMessage(0);
        }
    }

    /**
     * 控制TAB选择效果
     */
    private void controlWeekDaySelected() {
        first.setTextColor(getResources().getColor(R.color.white));
        second.setTextColor(getResources().getColor(R.color.white));
        third.setTextColor(getResources().getColor(R.color.white));
        fourth.setTextColor(getResources().getColor(R.color.white));
        five.setTextColor(getResources().getColor(R.color.white));
        six.setTextColor(getResources().getColor(R.color.white));
        seven.setTextColor(getResources().getColor(R.color.white));

        switch (selectedTabIndex) {
            case 1:
                first.setTextColor(getResources().getColor(R.color.orange));
                break;
            case 2:
                second.setTextColor(getResources().getColor(R.color.orange));
                break;
            case 3:
                third.setTextColor(getResources().getColor(R.color.orange));
                break;
            case 4:
                fourth.setTextColor(getResources().getColor(R.color.orange));
                break;
            case 5:
                five.setTextColor(getResources().getColor(R.color.orange));
                break;
            case 6:
                six.setTextColor(getResources().getColor(R.color.orange));
                break;
            case 7:
                seven.setTextColor(getResources().getColor(R.color.orange));
                break;
            default:
                break;
        }
    }

    /**
     * 节目信息
     */
    private class ProgramAdapter extends BaseAdapter {

        private LayoutInflater minflater;

        public ProgramAdapter(Context context) {
            this.minflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return weekPrograms.size();
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
                convertView = minflater.inflate(R.layout.activity_program_item, null);
                vh.programInfo = (TextView) convertView.findViewById(R.id.program_play_info);
                vh.programStatus = (TextView) convertView.findViewById(R.id.program_status);
                vh.iv_programstate=(ImageView)convertView.findViewById(R.id.iv_program_status);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            /**
             * 设置数据
             */
            final Program program = weekPrograms.get(position);
            String programKey = program.getProgramName() + "-" + program.getProgramStartTime() + "-" + weekIndexName;
            if (program != null) {
                //设置节目信息
                final String programStartTime = program.getProgramStartTime();
                final String programEndTime = program.getProgramEndTime();
                String info = "时间：" + programStartTime + " - " + programEndTime + "\n\n节目：" + program.getProgramName();
                vh.programInfo.setText(info);

                //设置节目信息的颜色
                String currentTime = DateUtils.getCurrentTimeStamp();
                final int weekIndex = DateUtils.getWeekIndex(0);
                
                //设置预约信息
                if (programStartTime.compareTo(currentTime) > 0 || selectedWeekIndex > weekIndex) {

                    if (orderProgramKeys.contains(programKey)) {
                        vh.programStatus.setText("已预约");
                        vh.programStatus.setTextColor(getResources().getColor(R.color.orange));
                    	vh.iv_programstate.setImageResource(R.drawable.program_ordered);
                    	vh.iv_programstate.setVisibility(View.VISIBLE);
                    	vh.programStatus.setVisibility(View.INVISIBLE);
                    } else if (program.getProgramName().equals("无节目信息")) {
                        vh.programStatus.setVisibility(View.INVISIBLE);
                        vh.iv_programstate.setVisibility(View.INVISIBLE);
                    } else {
                        vh.programStatus.setText("可预约");
                        vh.programStatus.setTextColor(getResources().getColor(R.color.white));    
                    	vh.iv_programstate.setImageResource(R.drawable.program_order);
                    	vh.iv_programstate.setVisibility(View.VISIBLE);
                    	vh.programStatus.setVisibility(View.INVISIBLE);
                    }

                    vh.iv_programstate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyApplication.vibrator.vibrate(100);

                            if (vh.programStatus.getText().equals("可预约")) {

                                try {
                                    //删除时间冲突的预约节目
                                    final OrderProgram orderProgramConflict = channelService.findOrderProgramByStartTime(programStartTime, weekIndexName);
                                    if (orderProgramConflict != null && orderProgramConflict.getProgramName() != null && orderProgramConflict.getProgramName().length() > 0) {
                                      String dialogtitle="您已预订该时段节目" + orderProgramConflict.getProgramName();
                                      String content="是否替换为：" + program.getProgramName()+"?";
                                    	Dialog dialog=DialogUtil.showAlertDialog(TVChannelProgramShowActivity.this, dialogtitle, content,"是","NO", new DialogBtnOnClickListener() {
										
										@Override
										public void onSubmit(DialogMessage dialogMessage) {

                                            channelService.deleteOrderProgram(orderProgramConflict.getProgramName(), orderProgramConflict.getOrderDate());
                                            OrderProgram orderProgramReplace = new OrderProgram();
                                            orderProgramReplace.setProgramName(program.getProgramName());
                                            orderProgramReplace.setChannelName(channelName);
                                            orderProgramReplace.setOrderDate(DateUtils.getDayOfToday());
                                            orderProgramReplace.setChannelIndex(program.getChannelIndex());
                                            orderProgramReplace.setProgramStartTime(program.getProgramStartTime());
                                            orderProgramReplace.setProgramEndTime(program.getProgramEndTime());
                                            orderProgramReplace.setWeekIndex(weekIndexName);
                                            orderProgramReplace.setStatus("已预约");
                                            if (channelService.saveOrderProgram(orderProgramReplace)) {
                                                orderProgramKeys.add(program.getProgramName() + "-" + program.getProgramStartTime() + "-" + weekIndexName);
                                                vh.programStatus.setText("已预约");
                                                vh.programStatus.setTextColor(getResources().getColor(R.color.orange));
                                                vh.iv_programstate.setImageResource(R.drawable.program_ordered);
                                                Toast.makeText(TVChannelProgramShowActivity.this, "节目预约成功", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(TVChannelProgramShowActivity.this, "节目预约失败", Toast.LENGTH_SHORT).show();
                                            }
                                        
										}
										
										@Override
										public void onCancel(DialogMessage dialogMessage) {
                                             vh.programStatus.setText("可预约");
                                             vh.programStatus.setTextColor(getResources().getColor(R.color.white));
                                             vh.iv_programstate.setImageResource(R.drawable.program_order);
										}
									});
                                    	
                                    } else {
                                        //保存预约信息
                                        try {
                                            OrderProgram orderProgram = new OrderProgram();
                                            orderProgram.setChannelIndex(program.getChannelIndex());
                                            orderProgram.setStatus("已预约");
                                            orderProgram.setChannelName(channelName);
                                            orderProgram.setOrderDate(orderDate + program.getProgramStartTime());
                                            orderProgram.setProgramName(program.getProgramName());
                                            orderProgram.setProgramStartTime(program.getProgramStartTime());
                                            orderProgram.setProgramEndTime(program.getProgramEndTime());
                                            orderProgram.setWeekIndex(weekIndexName);

                                            if (channelService.saveOrderProgram(orderProgram)) {
                                                orderProgramKeys.add(program.getProgramName() + "-" + program.getProgramStartTime() + "-" + weekIndexName);
                                                vh.programStatus.setText("已预约");
                                                vh.programStatus.setTextColor(getResources().getColor(R.color.orange));
                                                vh.iv_programstate.setImageResource(R.drawable.program_ordered);
                                                Toast.makeText(TVChannelProgramShowActivity.this, "节目预约成功", Toast.LENGTH_SHORT).show();
                                            } else {
                                                vh.programStatus.setText("可预约");
                                                vh.programStatus.setTextColor(getResources().getColor(R.color.white));
                                                vh.iv_programstate.setImageResource(R.drawable.program_order);
                                                Toast.makeText(TVChannelProgramShowActivity.this, "节目预约失败", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else if (channelService.deleteOrderProgramByWeek(program.getProgramName(), weekIndexName)) {
                                orderProgramKeys.remove(program.getProgramName() + "-" + program.getProgramStartTime() + "-" + weekIndexName);
                                vh.programStatus.setText("可预约"); 
                                    vh.programStatus.setTextColor(getResources().getColor(R.color.white));
                                    vh.iv_programstate.setImageResource(R.drawable.program_order);
                            } else {
                                Toast.makeText(TVChannelProgramShowActivity.this, "取消预约失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    vh.programStatus.setText("");
                    vh.iv_programstate.setVisibility(View.INVISIBLE);
                }
                
                if (programStartTime.compareTo(currentTime) < 0 && programEndTime.compareTo(currentTime) >= 0 && selectedWeekIndex == weekIndex) {
                    vh.programInfo.setTextColor(getResources().getColor(R.color.orange));
                    vh.programStatus.setTextColor(getResources().getColor(R.color.orange));
                    vh.programStatus.setText("直播中");
                    vh.programStatus.setVisibility(View.VISIBLE);
                    vh.iv_programstate.setVisibility(View.INVISIBLE);
                } else {
                    vh.programInfo.setTextColor(getResources().getColor(R.color.white));
                }
            }

            return convertView;
        }

        public final class ViewHolder {
            public TextView programInfo;
            public TextView programStatus;
            public ImageView iv_programstate;
        }
    }

    /**
     * *******************************************系统发发重载********************************************************
     */

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
}
