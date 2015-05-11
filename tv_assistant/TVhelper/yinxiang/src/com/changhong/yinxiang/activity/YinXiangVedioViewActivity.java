package com.changhong.yinxiang.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.vedio.YinXiangVedioAdapter;

/**
 * Created by Administrator on 15-5-11.
 */
public class YinXiangVedioViewActivity extends Activity {

    /**************************************************IP连接部分*******************************************************/

    public static TextView title = null;
    private Button listClients;
    private Button back;
    private ListView clients = null;
    private ArrayAdapter<String> IpAdapter;

    /**************************************************视频部分*******************************************************/

    /**
     * Image List adapter
     */
    private YinXiangVedioAdapter vedioAdapter;
    /**
     * 视频浏览部分
     */
    private ListView vedioListView;

    /**
     * 视频推送按钮
     */
    private Button vedioSend;

    /**
     * 视频已经选择INFO
     */
    public static TextView vedioSelectedInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initEvent();
    }

    private void initView() {
        setContentView(R.layout.activity_yinxiang_vedio_view);

        /**
         * IP连接部分
         */
        title = (TextView) findViewById(R.id.title);
        back = (Button) findViewById(R.id.btn_back);
        clients = (ListView) findViewById(R.id.clients);
        listClients = (Button) findViewById(R.id.btn_list);

        /**
         * 视频部分
         */
        vedioListView = (ListView) findViewById(R.id.yinxiang_vedio_list_view);
        vedioAdapter = new YinXiangVedioAdapter(this);
        vedioListView.setAdapter(vedioAdapter);

        vedioSend = (Button)findViewById(R.id.yinxing_vedio_tuisong);
        vedioSelectedInfo = (TextView)findViewById(R.id.yinxing_vedio_tuisong_info);
    }

    private void initEvent() {

        /**
         * IP连接部分
         */
        IpAdapter = new ArrayAdapter<String>(YinXiangVedioViewActivity.this,
                android.R.layout.simple_list_item_1,
                ClientSendCommandService.serverIpList);
        clients.setAdapter(IpAdapter);
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
                ClientSendCommandService.serverIP = ClientSendCommandService.serverIpList.get(arg2);
                title.setText("CHBOX");
                ClientSendCommandService.handler.sendEmptyMessage(2);
                clients.setVisibility(View.GONE);
            }
        });
        listClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MyApplication.vibrator.vibrate(100);
                    if (ClientSendCommandService.serverIpList.isEmpty()) {
                        Toast.makeText(YinXiangVedioViewActivity.this, "未获取到服务器IP", Toast.LENGTH_LONG).show();
                    } else {
                        clients.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
         * 视频部分
         */
        vedioSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**********************************************系统发发重载*********************************************************/

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
}
