package com.changhong.setting.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.changhong.common.system.MyApplication;
import com.changhong.setting.R;
import com.changhong.setting.domain.ScoreItem;
import com.changhong.setting.service.ScoreService;

/**
 * Ma Ren
 * edited by cym
 */
public class ScoreDialog extends Dialog {

    private Context context;

    private TextView textNetSuggestion;

    Boolean barDisplayControl = true;
    View mView;
    View navigationBar;

    private TextView textScoreAll;

    private ImageButton scoreButton;

    private RoundProgressBar roundProgressBar;
    private int i = 0;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            roundProgressBar.setProgress(msg.what);
            if (i <= roundProgressBar.getMax()) {
                handler.sendEmptyMessageDelayed(i++, 1);
            }
            textScoreAll.setText(String.valueOf(msg.what) + getContext().getResources().getString(R.string.points));
        }

        ;
    };

    public ScoreDialog(Context context) {
        super(context, R.style.Translucent_NoTitle);
        this.context = context;
        setContentView(R.layout.setting_sys_score_dialog);

        initView();

        initData();

        initEvent();
    }

    private void initView() {
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        window.setGravity(Gravity.BOTTOM);

        navigationBar = findViewById(R.id.bar);
        mView = findViewById(R.id.view);
        scoreButton = (ImageButton) findViewById(R.id.cancel_system_score);
        textScoreAll = (TextView) findViewById(R.id.realityScore);
        textNetSuggestion = (TextView) findViewById(R.id.text_wireless_suggestion);
        roundProgressBar = (RoundProgressBar) findViewById(R.id.circleProgressBar);

//        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
//        toolbar.setTitle(" 设置 ");
//        setSupportActionBar(toolbar);
//        final ActionBar ab = getSupportActionBar();
//        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void initData() {
        ScoreService scoreService = new ScoreService(context);
        int score = scoreService.getTotal();

        /**
         * Network part
         */
        ScoreItem item = scoreService.getItemDetails("NET");
        String netSuggestion = item.getCurrentSuggestion();
        textNetSuggestion.setText(context.getResources().getString(R.string.network_environment)+ "\n" +String.valueOf(netSuggestion));

        roundProgressBar.setMax(score);
        handler.sendEmptyMessageDelayed(i++, 1);
        textScoreAll.setText(String.valueOf(score) + getContext().getResources().getString(R.string.points));
    }

    private void initEvent() {

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);

                if (barDisplayControl) {
                    navigationBar.setVisibility(View.GONE);
                    barDisplayControl = false;
                } else {
                    navigationBar.setVisibility(View.VISIBLE);
                    barDisplayControl = true;
                }
            }
        });


        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                dismiss();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                dismiss();
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
