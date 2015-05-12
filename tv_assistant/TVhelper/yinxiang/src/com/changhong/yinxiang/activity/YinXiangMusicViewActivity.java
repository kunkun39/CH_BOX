package com.changhong.yinxiang.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.music.YinXiangMusic;
import com.changhong.yinxiang.music.YinXiangMusicAdapter;
import com.changhong.yinxiang.nanohttpd.HTTPDService;

public class YinXiangMusicViewActivity extends Activity{

	/**************************************************IP连接部分*******************************************************/

    public static TextView title = null;
    private Button listClients;
    private Button back;
    private ListView clients = null;
    private ArrayAdapter<String> IpAdapter;

    /**************************************************音频部分*******************************************************/
    /**
     * 从上个Activity传过来的musics
     */
    private static List<?> musics;
    
    private static Map<String, List<YinXiangMusic>> model;
    
    /**
     * 演唱者
     */
    private TextView musicSinger;
    private String singerName;
    private static List<String> musicList;
    
    /**
     * Image List adapter
     */
    private YinXiangMusicAdapter musicAdapter;
    /**
     * 音频浏览部分
     */
    private ListView musicListView;

    /**
     * 音频推送按钮
     */
    private Button musicSend;

    /**
     * 音频已经选择INFO
     */
    public static TextView musicSelectedInfo;

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
         * 音频部分
         */
        musicListView = (ListView) findViewById(R.id.yinxiang_vedio_list_view);
        Intent intent =getIntent();
        String keyStr=intent.getStringExtra("KeyWords");
        
        musicAdapter = new YinXiangMusicAdapter(this,keyStr);
        musicListView.setAdapter(musicAdapter);

        musicSend = (Button)findViewById(R.id.yinxing_vedio_tuisong);
        musicSelectedInfo = (TextView)findViewById(R.id.yinxing_vedio_tuisong_info);
    }

    private void initEvent() {

        /**
         * IP连接部分
         */
        IpAdapter = new ArrayAdapter<String>(YinXiangMusicViewActivity.this,
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
                        Toast.makeText(YinXiangMusicViewActivity.this, "未获取到服务器IP", Toast.LENGTH_LONG).show();
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
         * 音频发送部分
         */
        musicSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (NetworkUtils.isWifiConnected(YinXiangMusicViewActivity.this)) {
                        if (!StringUtils.hasLength(ClientSendCommandService.serverIP)) {
                            Toast.makeText(YinXiangMusicViewActivity.this, "手机未连接电音，请确认后再推送", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        MyApplication.vibrator.vibrate(100);

                        /**
                         * 转换音乐的路径
                         */
                        List<String> convertSelectedMusicPaths = new ArrayList<String>();
                        for (String selectMusicPath : YinXiangMusicAdapter.selectMusicPaths) {
                            String tempPath = "";
                            if (selectMusicPath.startsWith(HTTPDService.defaultHttpServerPath)) {
                                tempPath = selectMusicPath.replace(HTTPDService.defaultHttpServerPath, "").replace(" ", "%20");
                            } else {
                                for (String otherHttpServerPath : HTTPDService.otherHttpServerPaths) {
                                    if (selectMusicPath.startsWith(otherHttpServerPath)) {
                                        tempPath = selectMusicPath.replace(otherHttpServerPath, "").replace(" ", "%20");
                                    }
                                }
                            }
                            convertSelectedMusicPaths.add(tempPath);
                        }

                        /**
                         * 准备发送音乐的数据
                         */
                        String ipAddress = NetworkUtils.getLocalHostIp();
                        String httpAddress = "http://" + ipAddress + ":" + HTTPDService.HTTP_PORT;
                        JSONObject o = new JSONObject();
                        JSONArray array = new JSONArray();
                        for (String convertSelectedMusicPath : convertSelectedMusicPaths) {
                            array.put(httpAddress + convertSelectedMusicPath);
                        }
                        o.put("musics", array.toString());

                        //发送播放地址
                        //ClientSendCommandService.msg = o.toString();
                        //ClientSendCommandService.handler.sendEmptyMessage(4);
                        Toast.makeText(YinXiangMusicViewActivity.this, o.toString(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(YinXiangMusicViewActivity.this, "请链接无线网络", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(YinXiangMusicViewActivity.this, "音频获取失败", Toast.LENGTH_SHORT).show();
                }
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
