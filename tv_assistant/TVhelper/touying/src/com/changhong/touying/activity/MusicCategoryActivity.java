package com.changhong.touying.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.touying.R;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicDataAdapter;
import com.changhong.touying.service.MusicService;
import com.changhong.touying.service.MusicServiceImpl;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Jack Wang
 */
public class MusicCategoryActivity extends Activity {

    /**************************************************IP连接部分*******************************************************/

    public static TextView title = null;
    private Button listClients;
    private Button back;
    private ListView clients = null;
    private ArrayAdapter<String> IpAdapter;

    /**************************************************歌曲部分*******************************************************/

    /**
     * Image List adapter
     */
    private MusicDataAdapter musicAdapter;
    /**
     * 视频浏览部分
     */
    private GridView musicGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * 启动歌词扫描服务
         */
        MusicService musicService = new MusicServiceImpl(MusicCategoryActivity.this);
        musicService.findAllMusicLrc();


        initView();

        initEvent();
    }

    private void initView() {
        setContentView(R.layout.activity_music_category);

        /**
         * IP连接部分
         */
        title = (TextView) findViewById(R.id.title);
        back = (Button) findViewById(R.id.btn_back);
        clients = (ListView) findViewById(R.id.clients);
        listClients = (Button) findViewById(R.id.btn_list);

        /**
         * 歌曲部分
         */
        musicGridView = (GridView) findViewById(R.id.music_grid_view);
        musicAdapter = new MusicDataAdapter(this);
        musicGridView.setAdapter(musicAdapter);
    }

    private void initEvent() {
    	
        /**
         * IP连接部分
         */
        IpAdapter = new ArrayAdapter<String>(MusicCategoryActivity.this, android.R.layout.simple_list_item_1, ClientSendCommandService.serverIpList);
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
                        Toast.makeText(MusicCategoryActivity.this, "未获取到服务器IP", Toast.LENGTH_LONG).show();
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
         * 歌曲部分
         */
        musicGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyApplication.vibrator.vibrate(100);
                Intent intent = new Intent();
                intent.setClass(MusicCategoryActivity.this, MusicViewActivity.class);
                Bundle bundle = new Bundle();
                List<Music> musics = MusicDataAdapter.getPositionMusics(position);
                bundle.putSerializable("musics", (Serializable) musics);
                intent.putExtras(bundle);
                startActivity(intent);
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
