package com.changhong.tvhelper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.changhong.tvhelper.R;

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
        animation = AnimationUtils.loadAnimation(this, R.anim.activity_welcome_anim);
        imageView.startAnimation(animation);

        handler = new Handler();
        handler.postDelayed(new LoadingApplication(), 4000);
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