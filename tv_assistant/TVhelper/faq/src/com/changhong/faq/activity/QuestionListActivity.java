package com.changhong.faq.activity;


import java.util.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.utils.NetworkUtils;
import com.changhong.faq.assember.FaqAssember;
import com.changhong.faq.domain.AppDescription;
import com.changhong.faq.domain.Category;
import com.changhong.faq.service.HttpClientService;
import com.changhong.faq.R;


public class QuestionListActivity extends Activity {
	 /**author maren
     * 系统欢迎页面数据
     */
    private AppDescription appDescription;
    /*
     * 点击音效
      */
     static public SoundPool sp;//声明一个SoundPool
     static public int music;//定义一个整型用load（）；来设置suondID
     static public int error;

    /**
     * 问题列表的VIEW
     */
    private ListView listView;

    /**
     * 问题列表的DATA
     */
    private List<Map<String, String>> mData;

    /**
     * 问题目录
     */
    private List<Category> categories = new ArrayList<Category>();

    /**
     * data适配器
     */
    private MyAdapter adapter;

    /**
     * 线程处理
     */
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initAudio();

        initView();

        initData();
    }
    
    private void initAudio() {
        sp = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        music = sp.load(this, R.raw.move, 1);
        error = sp.load(this, R.raw.error, 1);
    }

    private void initView() {
        setContentView(R.layout.activity_question_list);

        listView = (ListView) findViewById(R.id.listView);
        listView.setDividerHeight(0);
        listView.setItemsCanFocus(true);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int what = msg.what;
                switch (what) {
				case 18:
					Toast.makeText(QuestionListActivity.this, "您没有连接网络", Toast.LENGTH_LONG).show();
					break;
                case 19:
                    Toast.makeText(QuestionListActivity.this, (CharSequence) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                case 20:
                      String appString = (String) msg.obj;
                        Dialog dialog = new AlertDialog.Builder(QuestionListActivity.this)
                                .setIcon(android.R.drawable.btn_plus)
                                .setTitle("系统提示")
                                .setMessage(appString)
                                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                    }
                                }).create();
                        dialog.show();
                    break;
                case 21:
                     adapter.notifyDataSetChanged();
                    break;
						
				default:
					break;
				}
                
            }
        };
    }

    private void initData() {
        mData = getData();
        adapter = new MyAdapter(QuestionListActivity.this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ListView lView = (ListView) arg0;
                Map<String, String> dataMap = (Map<String, String>) lView.getItemAtPosition(arg2);
                int examinationId = Integer.parseInt(dataMap.get("id"));
                Intent ruleIntent = new Intent();
                ruleIntent.setClass(QuestionListActivity.this, VoteRuleActivity.class);
                ruleIntent.putExtra("examinationId", examinationId);

                if (QuestionListActivity.sp != null && QuestionListActivity.music > 0) {
                    QuestionListActivity.sp.play(QuestionListActivity.music, 1, 1, 0, 0, 1);
                }
                startActivity(ruleIntent);
			}

        });

        Thread thread = new Thread() {
            @Override
            public void run() {
                if (!NetworkUtils.isConnectInternet(QuestionListActivity.this)) {
                   handler.sendEmptyMessage(18);
                }

                try {
                    String welcomeJson = HttpClientService.getWelcomePageResponse();
                    appDescription = FaqAssember.convertToAppDescription(welcomeJson);
                    categories = FaqAssember.convertToAppCategories(welcomeJson);

                    String appString = appDescription.getDescription();
                    if (appString != null && appString.length() > 0) {
                        handler.sendMessage(handler.obtainMessage(20, appDescription.getDescription()));
                    }

                    mData = getData();
                    handler.sendMessage(handler.obtainMessage(21, "更新问题列表"));
                } catch (Exception e) {
                    handler.sendMessage(handler.obtainMessage(19, "问卷列表获取失败, 请检查你的网络"));
                }
            }
        };
        thread.start();
    }

    //获取动态数组数�? 可以由其他地方传�?json�?
    private List<Map<String, String>> getData() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (int i = 0; i < categories.size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            Category category = categories.get(i);
            map.put("id", category.getId() + "");
            map.put("title", "问卷" + (i + 1) + ":" + category.getTitle());
            list.add(map);
        }
        return list;
    }

    public class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();

                //可以理解为从vlist获取view  之后把view返回给ListView
                convertView = mInflater.inflate(R.layout.activity_question_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.evaluatetitle);
                holder.info = (TextView) convertView.findViewById(R.id.evaluateinfo);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Map<String, String> map = mData.get(position);
            holder.info.setText(map.get("title"));
            return convertView;
        }
    }

    //提取出来方便�?
    public final class ViewHolder {

        public TextView title;

        public TextView info;
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
}
