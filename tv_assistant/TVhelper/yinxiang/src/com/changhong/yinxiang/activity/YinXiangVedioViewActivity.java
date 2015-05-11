package com.changhong.yinxiang.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.changhong.common.system.MyApplication;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.vedio.YinXiangVedioAdapter;

/**
 * Created by Administrator on 15-5-11.
 */
public class YinXiangVedioViewActivity extends Activity {

    /**************************************************IP连接部分*******************************************************/

    private Button back;

    /**************************************************视频部分*******************************************************/

    /**
     * Image List adapter
     */
    private YinXiangVedioAdapter vedioAdapter;
    /**
     * 视频浏览部分
     */
    private ListView vedioListView;

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
        back = (Button) findViewById(R.id.btn_back);

        /**
         * 视频部分
         */
        vedioListView = (ListView) findViewById(R.id.yinxiang_vedio_list_view);
        vedioAdapter = new YinXiangVedioAdapter(this);
        vedioListView.setAdapter(vedioAdapter);
    }

    private void initEvent() {

        /**
         * IP连接部分
         */
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
    }

    /**********************************************系统发发重载*********************************************************/

    @Override
    protected void onResume() {
        super.onResume();
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
