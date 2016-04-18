package com.changhong.tvhelper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.touying.service.M3UListProviderService;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.service.ClientGetCommandService;
import com.changhong.tvhelper.service.ClientLocalThreadRunningService;

/**
 * Created by maren on 2015/5/15.
 */
public class TVHelperWelcomeActivity extends Activity {

    private Handler handler;
    private ImageView imageView;
    private Animation animation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvhelper_welcome);
        imageView = (ImageView) findViewById(R.id.img_welcome_activity);
        animation = AnimationUtils.loadAnimation(this, R.anim.activity_welcome_anim); //加载动画资源,生成对话对象
        imageView.startAnimation(animation);

        handler = new Handler();
        handler.postDelayed(new LoadingApplication(), 4000);

        initService();
    }

    private void initService() {
        /**
         * 启动get command服务
         */
        Intent service1 = new Intent(TVHelperWelcomeActivity.this, ClientGetCommandService.class);
        startService(service1);

        /**
         * 启动send command服务
         */
        Intent service2 = new Intent(TVHelperWelcomeActivity.this, ClientSendCommandService.class);
        startService(service2);

        /**
         * 启动手机端本地线程
         */
        Intent service3 = new Intent(TVHelperWelcomeActivity.this, ClientLocalThreadRunningService.class);
        startService(service3);

        /**
         * 启动播放了列表收索服务
         */
        Intent service4 = new Intent(TVHelperWelcomeActivity.this, M3UListProviderService.class);
        startService(service4);
    }

    class LoadingApplication implements Runnable {
        @Override
        public void run() {
            Intent intent = new Intent();
            intent.setClass(TVHelperWelcomeActivity.this, TVHelperMainActivity.class);
            startActivity(intent);
            TVHelperWelcomeActivity.this.finish();
        }
    }
}