package com.changhong.tvhelper.activity;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.voicerecognition.android.Candidate;
import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.changhong.baidu.BaiDuVoiceChannelControlDialog;
import com.changhong.baidu.BaiDuVoiceConfiguration;
import com.changhong.common.domain.AppInfo;
import com.changhong.common.fragment.RecycleViewFragment;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.AppConfig;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.DialogUtil;
import com.changhong.common.utils.DialogUtil.DialogBtnOnClickListener;
import com.changhong.common.utils.DialogUtil.DialogMessage;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.widgets.BoxSelecter;
import com.changhong.remotecontrol.TVInputDialogActivity;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.utils.YuYingWordsUtils;
import com.changhong.tvhelper.view.TVChannelSwitchDialog;
import com.changhong.tvhelper.view.TVNumInputDialog;

public class TVRemoteControlActivity extends TVInputDialogActivity implements OnClickListener,
        OnTouchListener, OnGestureListener,RecycleViewFragment.OnFragmentInteractionListener {
    private static final String TAG = "TVRemoteControlActivity";
	private DrawerLayout mDrawerLayout;

    /**
     * control part
     */
    View img_d = null;
    View img_v = null;

    private GestureDetector detector;

    /**
     * server ip part
     */
    private BoxSelecter ipSelecter = null;

    private String LongKeyValue = null;
    private PointF startPoint = new PointF();
    private PointF endPoint = new PointF();

    //数字键盘
    TVNumInputDialog numInputDialog = null;
    TVChannelSwitchDialog switchChannelDialog = null;

    //长按键
    Handler mHandler1 = new Handler();
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (StringUtils.hasLength(LongKeyValue)) {
                MyApplication.vibrator.vibrate(30);
                ClientSendCommandService.msg = LongKeyValue;
                ClientSendCommandService.handler.sendEmptyMessage(1);
            }
            mHandler1.postDelayed(mRunnable, 150);
        }
    };

    ImageView smoothBall;
    private PointF centerPoint = new PointF();
    int width, height;

    /**************************************************百度语音换台部分**************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConfig.USE_TV) {
            setContentView(R.layout.activity_remote_control);
        } else {
            setContentView(R.layout.activity_remote_control_multi);
        }

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;     // 屏幕宽度（像素）
        height = metric.heightPixels;   // 屏幕高度（像素）
        float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        detector = new GestureDetector(this);
//      bidirSlidingLayout = (BidirSlidingLayout) findViewById(R.id.bidir_sliding_layout);
        img_d = findViewById(R.id.img_d);
        img_v = findViewById(R.id.img_volume);
        smoothBall = (ImageView) findViewById(R.id.ball);
        Button btn_up = (Button) findViewById(R.id.up);
        Button btn_down = (Button) findViewById(R.id.down);
        Button btn_left = (Button) findViewById(R.id.left);
        Button btn_right = (Button) findViewById(R.id.right);
        Button btn_center = (Button) findViewById(R.id.center);
        Button btn_vup = (Button) findViewById(R.id.volumeup);
        Button btn_vdown = (Button) findViewById(R.id.volumedown);
        Button btn_vtv = (Button) findViewById(R.id.tv);
        Button btn_vchannel = (Button) findViewById(R.id.channel);
        Button btn_vnum = (Button) findViewById(R.id.num);
        Button back = (Button) findViewById(R.id.btn_back);
        Button power = (Button) findViewById(R.id.power);
        Button home = (Button) findViewById(R.id.btn_home);
        Button menu = (Button) findViewById(R.id.btn_menu);
        Button fanhui = (Button) findViewById(R.id.btn_b);
        Button list = (Button) findViewById(R.id.btn_list);
        btn_up.setOnTouchListener(this);
        btn_up.setOnClickListener(this);
        btn_down.setOnTouchListener(this);
        btn_down.setOnClickListener(this);
        btn_left.setOnTouchListener(this);
        btn_left.setOnClickListener(this);
        btn_right.setOnTouchListener(this);
        btn_right.setOnClickListener(this);
        btn_center.setOnTouchListener(this);
        btn_center.setOnClickListener(this);
        btn_vup.setOnTouchListener(this);
        btn_vup.setOnClickListener(this);
        btn_vdown.setOnTouchListener(this);
        btn_vdown.setOnClickListener(this);

        if (AppConfig.USE_TV) {
            btn_vtv.setOnTouchListener(this);
            btn_vtv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    ClientSendCommandService.msg = "key:dtv";
                    ClientSendCommandService.handler.sendEmptyMessage(1);
                }
            });

            btn_vchannel.setOnTouchListener(this);
            btn_vchannel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    if (ClientSendCommandService.channelData.isEmpty()) {
                        Toast.makeText(TVRemoteControlActivity.this, R.string.channel_list_empty, 3000).show();
                    } else {
                        if (switchChannelDialog != null) {
                            if(getSupportFragmentManager().findFragmentByTag("switchChannelDialog") != null){;
                                switchChannelDialog.getDialog().show();
                            } else {
                                switchChannelDialog.show(getSupportFragmentManager(),"switchChannelDialog");
                            }

                        }
                    }
                }
            });

            btn_vnum.setOnTouchListener(this);
            btn_vnum.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    if (numInputDialog != null && !numInputDialog.isShowing()) {
                        numInputDialog.show();
                    }
                }
            });
        }

        if(back != null){
            back.setOnTouchListener(this);
            back.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    finish();
                }
            });
        }

        home.setOnClickListener(this);
        home.setOnTouchListener(this);
        menu.setOnClickListener(this);
        menu.setOnTouchListener(this);
        fanhui.setOnClickListener(this);
        fanhui.setOnTouchListener(this);

        if (power != null){
            power.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    Dialog dialog = DialogUtil.showAlertDialog(
                            TVRemoteControlActivity.this, "", getString(R.string.close_stb_tip) + "?",
                            new DialogBtnOnClickListener() {

                                @Override
                                public void onSubmit(DialogMessage dialogMessage) {
                                    ClientSendCommandService.msg = "key:power";
                                    ClientSendCommandService.handler
                                            .sendEmptyMessage(1);
                                    if (dialogMessage.dialog != null && dialogMessage.dialog.isShowing()) {
                                        dialogMessage.dialog.cancel();
                                    }
                                }

                                @Override
                                public void onCancel(DialogMessage dialogMessage) {
                                    if (dialogMessage.dialog != null && dialogMessage.dialog.isShowing()) {
                                        dialogMessage.dialog.cancel();
                                    }
                                }
                            });
                }
            });
        }

        ipSelecter = new BoxSelecter(this, (TextView) findViewById(R.id.title), (ListView) findViewById(R.id.clients), (Button) findViewById(R.id.btn_list), new Handler(getMainLooper()));

//        bidirSlidingLayout.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				bidirSlidingLayout.closeRightMenu();
//			}
//		});

        if (numInputDialog == null) {
            numInputDialog = new TVNumInputDialog(TVRemoteControlActivity.this);
            numInputDialog.setCanceledOnTouchOutside(false);
            numInputDialog.btn0.setOnClickListener(this);
            numInputDialog.btn1.setOnClickListener(this);
            numInputDialog.btn2.setOnClickListener(this);
            numInputDialog.btn3.setOnClickListener(this);
            numInputDialog.btn4.setOnClickListener(this);
            numInputDialog.btn5.setOnClickListener(this);
            numInputDialog.btn6.setOnClickListener(this);
            numInputDialog.btn7.setOnClickListener(this);
            numInputDialog.btn8.setOnClickListener(this);
            numInputDialog.btn9.setOnClickListener(this);
            numInputDialog.btnOk.setOnClickListener(this);
            numInputDialog.btnCancle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.vibrator.vibrate(100);
                    if (numInputDialog != null && numInputDialog.isShowing()) {
                        numInputDialog.dismiss();
                    }
                }
            });
        }
        if (switchChannelDialog == null) {
            switchChannelDialog = new TVChannelSwitchDialog();
        }
        centerPoint.set((180.25f - 35.5f) * density, (343.25f - 35.5f) * density);

        if (AppConfig.USE_VOICE_INPUT) {
            //长按触发语音换台功能
            btn_center.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    MyApplication.vibrator.vibrate(100);

                    /**
                     * stop first, because last action maybe not finished
                     */
                    recognitionClient.stopVoiceRecognition();
                    /**
                     * 语音的配置
                     */
                    VoiceRecognitionConfig config = BaiDuVoiceConfiguration.getVoiceRecognitionConfig();
                    /**
                     * 下面发起识别
                     */
                    int code = recognitionClient.startVoiceRecognition(recogListener, config);
                    if (code != VoiceRecognitionClient.START_WORK_RESULT_WORKING) {
                        Toast.makeText(TVRemoteControlActivity.this, R.string.check_network, Toast.LENGTH_LONG).show();
                    }

                    return true;
                }
            });

            initBaiduConfiguration();
            //zyt-1
            mDrawerLayout = (DrawerLayout) findViewById(R.id.remote_drawer);
            Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
            toolbar.setTitle(" ");
            setSupportActionBar(toolbar);
            final ActionBar ab = getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    //zyt-1
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return true;


    }


//    //zyt-1
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if (item.getItemId() == android.R.id.home) {
//
//            finish();
//        }
//        return true;
//    }

    /*****************************************************系统方法重载部分***********************************************/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.up:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:up";
                break;
            case R.id.down:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:down";
                break;
            case R.id.left:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:left";
                break;
            case R.id.right:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:right";
                break;
            case R.id.center:
            case R.id.numok:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:ok";
                break;
            case R.id.btn_b:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:back";
                break;
            case R.id.btn_menu:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:menu";
                break;
            case R.id.btn_home:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:home";
                break;
            case R.id.volumeup:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:volumeup";
                break;
            case R.id.volumedown:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:volumedown";
                break;
            case R.id.power:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:power";
                break;
            case R.id.num0:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:0";
                break;
            case R.id.num1:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:1";
                break;
            case R.id.num2:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:2";
                break;
            case R.id.num3:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:3";
                break;
            case R.id.num4:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:4";
                break;
            case R.id.num5:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:5";
                break;
            case R.id.num6:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:6";
                break;
            case R.id.num7:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:7";
                break;
            case R.id.num8:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:8";
                break;
            case R.id.num9:
                MyApplication.vibrator.vibrate(100);
                ClientSendCommandService.msg = "key:9";
                break;
            default:
                ClientSendCommandService.msg = "";
                break;
        }
        ClientSendCommandService.handler.sendEmptyMessage(1);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                startPoint.set(event.getX(), event.getY());
                //1秒后如果不移动不弹起按键 就执行 长按键操作
                mHandler1.postDelayed(mRunnable, 500);
                break;
            case MotionEvent.ACTION_MOVE:
                endPoint.set(event.getX(), event.getY());
                float moveX = endPoint.x - startPoint.x;
                float moveY = endPoint.y - startPoint.y;

                //移动距离过大判定不是长按键取消长按键操作
                if (Math.abs(moveX) > 80 || Math.abs(moveY) > 80) {
                    mHandler1.removeCallbacks(mRunnable);
                }

                if (Math.abs(moveX) >= Math.abs(moveY)) {
                    if (moveX >= 400) {
                        ClientSendCommandService.msg = "key:right";
                        moveFocus(moveX);
                    }
                    if (moveX <= -400) {
                        ClientSendCommandService.msg = "key:left";
                        moveFocus(moveX);
                    }
                } else {
                    if (moveY >= 400) {
                        ClientSendCommandService.msg = "key:down";
                        moveFocus(moveY);
                    }
                    if (moveY <= -400) {
                        ClientSendCommandService.msg = "key:up";
                        moveFocus(moveY);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //移除长按键操作
                mHandler1.removeCallbacks(mRunnable);
                endPoint.set(event.getX(), event.getY());
                moveX = endPoint.x - startPoint.x;
                moveY = endPoint.y - startPoint.y;

                if (Math.abs(moveX) >= Math.abs(moveY)) {
                    if (moveX >= 120) {
                        moveBall(centerPoint.x, width, centerPoint.y, centerPoint.y);
                        ClientSendCommandService.msg = "key:right";
                        moveFocus(moveX);
                        MyApplication.vibrator.vibrate(100);
                    }
                    if (moveX <= -120) {
                        moveBall(centerPoint.x, -100f, centerPoint.y, centerPoint.y);
                        ClientSendCommandService.msg = "key:left";
                        moveFocus(moveX);
                        MyApplication.vibrator.vibrate(100);
                    }
                } else {
                    if (moveY >= 120) {
                        moveBall(centerPoint.x, centerPoint.x, centerPoint.y, height);
                        ClientSendCommandService.msg = "key:down";
                        moveFocus(moveY);
                        MyApplication.vibrator.vibrate(100);
                    }
                    if (moveY <= -120) {
                        moveBall(centerPoint.x, centerPoint.x, centerPoint.y, 0f);
                        ClientSendCommandService.msg = "key:up";
                        moveFocus(moveY);
                        MyApplication.vibrator.vibrate(100);
                    }
                }

                /**
                 * 语音识别对话结束
                 */
                if (AppConfig.USE_VOICE_INPUT)
                {
                    if (v.getId() == R.id.center) {
                        recognitionClient.speakFinish();
                    }
                }
                break;
        }

        int background_bg = 0;
        switch (v.getId()) {
            case R.id.up:
                if (AppConfig.USE_VOICE_INPUT) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        background_bg = R.drawable.tv_control_directory_up;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        background_bg = R.drawable.tv_control_direction;
                    }
                } else {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        background_bg = R.drawable.pad_up;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        background_bg = R.drawable.pad;
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LongKeyValue = "key:up";
                    img_d.setBackgroundResource(background_bg);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    img_d.setBackgroundResource(background_bg);
                }
                break;
            case R.id.down:
                if (AppConfig.USE_VOICE_INPUT) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        background_bg = R.drawable.tv_control_directory_down;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        background_bg = R.drawable.tv_control_direction;
                    }
                } else {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        background_bg = R.drawable.pad_down;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        background_bg = R.drawable.pad;
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LongKeyValue = "key:down";
                    img_d.setBackgroundResource(background_bg);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    img_d.setBackgroundResource(background_bg);
                }
                break;
            case R.id.left:
                if (AppConfig.USE_VOICE_INPUT) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        background_bg = R.drawable.tv_control_directory_left;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        background_bg = R.drawable.tv_control_direction;
                    }
                } else {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        background_bg = R.drawable.pad_left;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        background_bg = R.drawable.pad;
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LongKeyValue = "key:left";
                    img_d.setBackgroundResource(background_bg);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    img_d.setBackgroundResource(background_bg);
                }
                break;
            case R.id.right:
                if (AppConfig.USE_VOICE_INPUT) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        background_bg = R.drawable.tv_control_directory_right;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        background_bg = R.drawable.tv_control_direction;
                    }
                } else {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        background_bg = R.drawable.pad_right;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        background_bg = R.drawable.pad;
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LongKeyValue = "key:right";
                    img_d.setBackgroundResource(background_bg);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    img_d.setBackgroundResource(background_bg);
                }
                break;
            case R.id.center:
                if (AppConfig.USE_VOICE_INPUT) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        background_bg = R.drawable.tv_control_directory_center;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        background_bg = R.drawable.tv_control_direction;
                    }
                } else {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        background_bg = R.drawable.pad_center;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        background_bg = R.drawable.pad;
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LongKeyValue = "";
                    img_d.setBackgroundResource(background_bg);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    img_d.setBackgroundResource(background_bg);
                }
                break;
            case R.id.volumeup:
                if (AppConfig.USE_TV) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        background_bg = R.drawable.tv_control_menu_volumplus;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        background_bg = R.drawable.tv_control_menu;
                    }
                } else {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        background_bg = R.drawable.volumeplus;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        background_bg = R.drawable.volume;
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LongKeyValue = "key:volumeup";
                    img_v.setBackgroundResource(background_bg);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    img_v.setBackgroundResource(background_bg);
                }
                break;
            case R.id.volumedown:
                if (AppConfig.USE_TV) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        background_bg = R.drawable.tv_control_menu_volumminus;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        background_bg = R.drawable.tv_control_menu;
                    }
                } else {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        background_bg = R.drawable.volumeminus;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        background_bg = R.drawable.volume;
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LongKeyValue = "key:volumedown";
                    img_v.setBackgroundResource(background_bg);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    img_v.setBackgroundResource(background_bg);
                }
                break;
            case R.id.tv:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LongKeyValue = "";
                    img_v.setBackgroundResource(R.drawable.tv_control_menu_tv);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    img_v.setBackgroundResource(R.drawable.tv_control_menu);
                }
                break;
            case R.id.channel:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LongKeyValue = "";
                    img_v.setBackgroundResource(R.drawable.tv_control_menu_channel);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    img_v.setBackgroundResource(R.drawable.tv_control_menu);
                }
                break;
            case R.id.num:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LongKeyValue = "";
                    img_v.setBackgroundResource(R.drawable.tv_control_menu_num);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    img_v.setBackgroundResource(R.drawable.tv_control_menu);
                }
                break;
            case R.id.btn_home:
            case R.id.btn_menu:
            case R.id.btn_b:
                LongKeyValue = "";
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.detector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.e("Fling", "Fling Happened!");
        float xMoveDistenace = e2.getX() - e1.getX();
        float yMoveDistenace = e2.getY() - e1.getY();

        if (Math.abs(xMoveDistenace) > Math.abs(yMoveDistenace)) {
            if (xMoveDistenace > 0 && Math.abs(xMoveDistenace) > 120) {
                moveBall(centerPoint.x, width, centerPoint.y, centerPoint.y);
                ClientSendCommandService.msg = "key:right";
                moveFocus(Math.abs(xMoveDistenace));
                MyApplication.vibrator.vibrate(100);

            } else if (xMoveDistenace < 0 && Math.abs(xMoveDistenace) > 120) {
                moveBall(centerPoint.x, -100f, centerPoint.y, centerPoint.y);
                ClientSendCommandService.msg = "key:left";
                moveFocus(Math.abs(xMoveDistenace));
                MyApplication.vibrator.vibrate(100);

            }
        } else {
            if (yMoveDistenace > 0 && Math.abs(yMoveDistenace) > 120) {
                moveBall(centerPoint.x, centerPoint.x, centerPoint.y, height);
                ClientSendCommandService.msg = "key:down";
                moveFocus(Math.abs(yMoveDistenace));
                MyApplication.vibrator.vibrate(100);

            } else if (yMoveDistenace < 0 && Math.abs(yMoveDistenace) > 120) {
                moveBall(centerPoint.x, centerPoint.x, centerPoint.y, 0f);
                ClientSendCommandService.msg = "key:up";
                moveFocus(Math.abs(yMoveDistenace));
                MyApplication.vibrator.vibrate(100);

            }
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float xMoveDistenace = e2.getX() - e1.getX();
        float yMoveDistenace = e2.getY() - e1.getY();

        if (Math.abs(xMoveDistenace) > Math.abs(yMoveDistenace)) {
            if (xMoveDistenace > 0 && Math.abs(xMoveDistenace) > 400) {
                ClientSendCommandService.msg = "key:right";
                moveFocus(Math.abs(xMoveDistenace));

            } else if (xMoveDistenace < 0 && Math.abs(xMoveDistenace) > 400) {
                ClientSendCommandService.msg = "key:left";
                moveFocus(Math.abs(xMoveDistenace));

            }
        } else {
            if (yMoveDistenace > 0 && Math.abs(yMoveDistenace) > 400) {
                ClientSendCommandService.msg = "key:down";
                moveFocus(Math.abs(yMoveDistenace));

            } else if (yMoveDistenace < 0 && Math.abs(yMoveDistenace) > 400) {
                ClientSendCommandService.msg = "key:up";
                moveFocus(Math.abs(yMoveDistenace));

            }
        }
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    private void moveFocus(float length) {
        ClientSendCommandService.handler.sendEmptyMessage(1);
    }

    private void moveBall(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
        TranslateAnimation animation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        animation.setDuration(500);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                smoothBall.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                smoothBall.clearAnimation();
                smoothBall.setVisibility(View.INVISIBLE);
            }
        });
        smoothBall.startAnimation(animation);
    }

    @Override
    protected void onResume() {
//        if (ClientSendCommandService.titletxt != null) {
//            title.setText(ClientSendCommandService.titletxt);
//        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                break;
//            case KeyEvent.KEYCODE_MENU:
//            	bidirSlidingLayout.clickSideMenu();
//    			return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ipSelecter != null) {
			ipSelecter.release();
		}
    }

    /**********************************************语音部分代码*********************************************************/

    /**
     * baidu recognition client, void to init many times, so use static here
     */
    private static VoiceRecognitionClient recognitionClient;

    /**
     * 识别回调接口
     */
    private BaiDuVoiceChannelDialogRecogListener recogListener = new BaiDuVoiceChannelDialogRecogListener();

    /**
     * 初始化百度的配置
     */
    private void initBaiduConfiguration() {
        if (recognitionClient == null) {
            recognitionClient = VoiceRecognitionClient.getInstance(TVRemoteControlActivity.this);
            recognitionClient.setTokenApis(BaiDuVoiceConfiguration.API_KEY, BaiDuVoiceConfiguration.SECRET_KEY);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * 百度语音监听器
     */
    public class BaiDuVoiceChannelDialogRecogListener implements VoiceRecognitionClient.VoiceClientStatusChangeListener {
        /**
         * 正在识别中
         */
        private boolean isRecognitioning = false;

        private int recognitioningFailedTimes = 0;

        /**
         * channel match list, integer value stand for match time, compare han zi one by one
         */
        private Map<String, Integer> matchChannel = new HashMap<String, Integer>();
        
        

        @Override
        public void onClientStatusChange(int status, Object obj) {
            switch (status) {
                // 语音识别实际开始，这是真正开始识别的时间点，需在界面提示用户说话。
                case VoiceRecognitionClient.CLIENT_STATUS_START_RECORDING:
                    isRecognitioning = true;
                    break;
                // 检测到语音起点
                case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_START:
                    break;
                // 已经检测到语音终点，等待网络返回
                case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_END:
                    break;
                // 语音识别完成，显示obj中的结果
                case VoiceRecognitionClient.CLIENT_STATUS_FINISH:
                    isRecognitioning = false;
                    updateRecognitionResult(obj);
                    break;
                // 处理连续上屏
                case VoiceRecognitionClient.CLIENT_STATUS_UPDATE_RESULTS:
                    break;
                // 用户取消
                case VoiceRecognitionClient.CLIENT_STATUS_USER_CANCELED:
                    recognitionClient.stopVoiceRecognition();
                    break;
                default:
                    break;
            }
        }

        
        @Override
        public void onError(int errorType, int errorCode) {
        	if (errorCode == VoiceRecognitionClient.ERROR_NETWORK_CONNECT_ERROR) {
        		Toast.makeText(TVRemoteControlActivity.this, R.string.sorry_bad_network_failed , Toast.LENGTH_LONG).show();
			}
        	else {
        		Toast.makeText(TVRemoteControlActivity.this, R.string.sorry_empty_message , Toast.LENGTH_LONG).show();
			}
            
            isRecognitioning = false;
            recognitionClient.stopVoiceRecognition();
        }

        @Override
        public void onNetworkStatusChange(int status, Object obj) {
            // 这里不做任何操作不影响简单识别
        }


        /**
         * 将识别结果更新到UI上，搜索模式结果类型为List<String>,输入模式结果类型为List<List<Candidate>>
         */
        private void updateRecognitionResult(Object result) {
            String recognitionResult = "";
            if (result != null && result instanceof List) {
                List results = (List) result;
                if (results.size() > 0) {
                    if (results.get(0) instanceof List) {
                        List<List<Candidate>> sentences = (List<List<Candidate>>) result;
                        StringBuffer sb = new StringBuffer();
                        for (List<Candidate> candidates : sentences) {
                            if (candidates != null && candidates.size() > 0) {
                                sb.append(candidates.get(0).getWord());
                            }
                        }
                        recognitionResult = sb.toString().replace("。", "");
                    } else {
                        recognitionResult = results.get(0).toString().replace("。", "");
                    }
                }
            }

            /**
             * used for check yuying laucher is successful or not
             * <p>
             * we have two flows:
             * 1 - if text start with "打开"，"启动"，"开启" go to open box app way
             * 2 - else go to switch channel way
             */
            boolean hasResult = false;
            if(AppConfig.USE_MALL_APP)
			{
            	String SEARCH_STRING[] = {"魔力","魔力影音","电影","电视剧","综艺","体育","少儿","动画","音乐"};
            	if (!hasResult) {
            		for (String tempString : SEARCH_STRING) {
						if (recognitionResult.equals(tempString)) {
							hasResult = true;
							ClientSendCommandService.msg = "mall:" + recognitionResult;
                            ClientSendCommandService.handler.sendEmptyMessage(1);
                            return ;
						}
					}
				}
            	
			}
            if (!hasResult || StringUtils.hasLength(recognitionResult)) {
                /********************************************处理用户说的话********************************************/            	
            	
                String commands = YuYingWordsUtils.isSearchContainsControl(recognitionResult);
                if (StringUtils.hasLength(commands)) {
                    //TODO:流程->主页
                    String[] command = StringUtils.delimitedListToStringArray(commands, "|");
                    if (command.length == 2 && command[0].equals("key:dtv")) {
                        ClientSendCommandService.msg = command[0];
                        ClientSendCommandService.handler.sendEmptyMessage(1);
                        SystemClock.sleep(300);
                        ClientSendCommandService.msg = command[1];
                        ClientSendCommandService.handler.sendEmptyMessage(1);
                    } else {
                        for (String cmd : command) {
                            ClientSendCommandService.msg = cmd;
                            ClientSendCommandService.handler.sendEmptyMessage(1);
                        }
                    }

                    hasResult = true;
                    Toast.makeText(TVRemoteControlActivity.this, getString(R.string.voice_result) + recognitionResult, Toast.LENGTH_LONG).show();

                } else if (YuYingWordsUtils.isSearchContainsAppKeywords(recognitionResult)) {
                    //TODO:流程->搜索应用
                    recognitionResult = YuYingWordsUtils.appSearchWordsConvert(recognitionResult);

                    /**
                     * search server side all applications
                     */
                    if (ClientSendCommandService.serverAppInfo.isEmpty()) {
                        ClientSendCommandService.handler.sendEmptyMessage(6);
                        //wait for search channel finish
                        while (!ClientSendCommandService.searchApplicationFinished) {
                            SystemClock.sleep(500);
                        }
                    }

                    /**
                     * compare the matched app, use char compare one by one
                     */
                    matchChannel.clear();
                    int size = ClientSendCommandService.serverAppInfo.size();
                    for (int i = 0; i < recognitionResult.length(); i++) {
                        for (int j = 0; j < size; j++) {
                            AppInfo info = ClientSendCommandService.serverAppInfo.get(j);
                            String appName = info.appName;
                            if (appName.indexOf(recognitionResult.charAt(i)) >= 0) {
                                Integer count = matchChannel.get(String.valueOf(j));
                                if (count == null) {
                                    matchChannel.put(String.valueOf(j), 1);
                                } else {
                                    matchChannel.put(String.valueOf(j), count + 1);
                                }
                            }
                        }
                    }

                    /**
                     * get best matched result, the value must bigger than 2
                     * 1 - first compare value which is bigger
                     * 2 - if value is equal compare which is shorter
                     * 3 - if length is equal compare which contains the input string
                     */
                    int bestCounter = 0;
                    String bestPostion = "";
                    for (String position : matchChannel.keySet()) {
                        Integer value = matchChannel.get(position);

                        if (value >= 2) {
                            if (value > bestCounter) {
                                bestCounter = value;
                                bestPostion = position;
                            } else if (value == bestCounter) {
                                String bestApp = ClientSendCommandService.serverAppInfo.get(Integer.valueOf(bestPostion)).appName;
                                String newApp = ClientSendCommandService.serverAppInfo.get(Integer.valueOf(position)).appName;

                                if (newApp.length() < bestApp.length()) {
                                    bestPostion = position;
                                } else if (newApp.length() == bestApp.length()) {
                                    int bestIndex = bestApp.indexOf(recognitionResult);
                                    int newIndex = newApp.indexOf(recognitionResult);
                                    if (bestIndex < 0 && newIndex >= 0) {
                                        bestPostion = position;
                                    }
                                }
                            }
                        }
                    }

                    /**
                     * send command to the server to decide which one should open
                     */
                    if (StringUtils.hasLength(bestPostion)) {
                        AppInfo info = ClientSendCommandService.serverAppInfo.get(Integer.valueOf(bestPostion));
                        ClientSendCommandService.msg = "app_open:" + info.packageName;
                        ClientSendCommandService.handler.sendEmptyMessage(1);
                        Log.e(TAG, "message:" + "app_open:" + info.packageName);

                        Toast.makeText(TVRemoteControlActivity.this, getString(R.string.app_result) + info.appName + "\n" + getString(R.string.voice_result) + recognitionResult, Toast.LENGTH_LONG).show();
                        hasResult = true;
                    } else {
                        hasResult = false;
                    }

                } else {
                    //TODO:流程->搜索频道
                    try {
                        /**
                         * search string convert
                         */
                        String beforeConvertRecognitionResult = recognitionResult;
                        recognitionResult = YuYingWordsUtils.yuYingChannelSearchWordsConvert(recognitionResult);
                        Log.e(TAG, recognitionResult);
                        /**
                         * begin to handle result, compare first one match channel and application
                         * if channel data is not empty, search channel and compare else just compare
                         */
                        if (ClientSendCommandService.channelData.isEmpty()) {
                            ClientSendCommandService.handler.sendEmptyMessage(2);
                            //wait for search channel finish
                            while (!ClientSendCommandService.searchChannelFinished) {
                                SystemClock.sleep(500);
                            }
                        }
                        /**
                         * after search it still empty, notice user channel is empty
                         */
                        if (ClientSendCommandService.channelData.isEmpty()) {
                            Toast.makeText(TVRemoteControlActivity.this, R.string.channel_list_empty, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        /**
                         * compare the matched chancel, use char compare one by one
                         */
                        matchChannel.clear();
                        int size = ClientSendCommandService.channelData.size();
                        for (int i = 0; i < recognitionResult.length(); i++) {
                            for (int j = 0; j < size; j++) {
                                //特殊频道对比的地方
                                String channelName = ((String) ClientSendCommandService.channelData.get(j).get("service_name")).replace("卫视高清", "高清");
                                channelName = YuYingWordsUtils.getSpecialWordsChannel(channelName);
                                channelName = YuYingWordsUtils.numNotNeedConvert(channelName);
                                if (channelName.indexOf(recognitionResult.charAt(i)) >= 0) {
                                    Integer count = matchChannel.get(String.valueOf(j));
                                    if (count == null) {
                                        matchChannel.put(String.valueOf(j), 1);
                                    } else {
                                        matchChannel.put(String.valueOf(j), count + 1);
                                    }
                                }
                            }
                        }

                        /**
                         * get best matched result, the value must bigger than 2
                         * 1 - first compare value which is bigger
                         * 2 - if value is equal compare which is shorter
                         * 3 - if length is equal compare which contains the input string
                         */
                        int bestCounter = 0;
                        String bestPostion = "";
                        for (String position : matchChannel.keySet()) {
                            Integer value = matchChannel.get(position);

                            if (value >= 2) {
                                if (value > bestCounter) {
                                    bestCounter = value;
                                    bestPostion = position;
                                } else if (value == bestCounter) {
                                    String bestChannel = ((String) ClientSendCommandService.channelData.get(Integer.valueOf(bestPostion)).get("service_name")).replace("卫视高清", "高清");
                                    String newChannel = ((String) ClientSendCommandService.channelData.get(Integer.valueOf(position)).get("service_name")).replace("卫视高清", "高清");
                                    bestChannel = YuYingWordsUtils.getSpecialWordsChannel(bestChannel);
                                    newChannel = YuYingWordsUtils.getSpecialWordsChannel(newChannel);
                                    bestChannel = YuYingWordsUtils.numNotNeedConvert(bestChannel);
                                    newChannel = YuYingWordsUtils.numNotNeedConvert(newChannel);

                                    if (newChannel.length() < bestChannel.length()) {
                                        bestPostion = position;
                                    } else if (newChannel.length() == bestChannel.length()) {
                                        int bestIndex = bestChannel.indexOf(recognitionResult);
                                        int newIndex = newChannel.indexOf(recognitionResult);
                                        if (bestIndex < 0 && newIndex >= 0) {
                                            bestPostion = position;
                                        } else {
                                            bestIndex = recognitionResult.indexOf(bestChannel);
                                            newIndex = recognitionResult.indexOf(newChannel);
                                            if (bestIndex < 0 && newIndex >= 0) {
                                                bestPostion = position;
                                            } else {
                                                if (newChannel.contains("高清")) {
                                                    bestPostion = position;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        /**
                         * 特殊地方台处理逻辑
                         */
                        boolean findSpecial = false;
                        List<String> locationChannels = YuYingWordsUtils.getLocationWordsChannel(beforeConvertRecognitionResult);
                        if (locationChannels != null) {
                            for (String locationChannel : locationChannels) {

                                for (int j = 0; j < size; j++) {
                                    String channelName = (String) ClientSendCommandService.channelData.get(j).get("service_name");
                                    if (channelName.equals(locationChannel)) {
                                        bestPostion = String.valueOf(j);
                                        findSpecial = true;
                                        break;
                                    }
                                }

                                if(findSpecial) {
                                    break;
                                }
                            }
                        }

                        /**
                         * send command to the server
                         */
                        if (StringUtils.hasLength(bestPostion)) {
                            /**
                             * switch to full tv model first
                             */
                            ClientSendCommandService.msg = "key:dtv";
                            ClientSendCommandService.handler.sendEmptyMessage(1);

                            /**
                             * switch the channel
                             */
                            String serviceId = (String) ClientSendCommandService.channelData.get(Integer.valueOf(bestPostion)).get("service_id");
                            String tsId = (String) ClientSendCommandService.channelData.get(Integer.valueOf(bestPostion)).get("tsId");
                            String orgNId = (String) ClientSendCommandService.channelData.get(Integer.valueOf(bestPostion)).get("orgNId");
                            ClientSendCommandService.msgSwitchChannel = serviceId + "#" + tsId + "#" + orgNId;
                            ClientSendCommandService.handler.sendEmptyMessage(3);
                            String serviceName = (String) ClientSendCommandService.channelData.get(Integer.valueOf(bestPostion)).get("service_name");
                            Toast.makeText(TVRemoteControlActivity.this, getString(R.string.channel_result) + serviceName + "\n" + getString(R.string.voice_result) + beforeConvertRecognitionResult, Toast.LENGTH_LONG).show();

                            hasResult = true;
                        } else {
                            hasResult = false;
                        }

                        //TODO:流程->如果频道没有搜索到，再次搜索应用
                        if(!hasResult) {
                            recognitionResult = beforeConvertRecognitionResult;

                            /**
                             * search server side all applications
                             */
                            if (ClientSendCommandService.serverAppInfo.isEmpty()) {
                                ClientSendCommandService.handler.sendEmptyMessage(6);
                                //wait for search channel finish
                                while (!ClientSendCommandService.searchApplicationFinished) {
                                    SystemClock.sleep(500);
                                }
                            }

                            /**
                             * compare the matched app, use char compare one by one
                             */
                            matchChannel.clear();
                            for (int i = 0; i < recognitionResult.length(); i++) {
                                for (int j = 0; j < ClientSendCommandService.serverAppInfo.size(); j++) {
                                    AppInfo info = ClientSendCommandService.serverAppInfo.get(j);
                                    String appName = info.appName;
                                    if (appName.indexOf(recognitionResult.charAt(i)) >= 0) {
                                        Integer count = matchChannel.get(String.valueOf(j));
                                        if (count == null) {
                                            matchChannel.put(String.valueOf(j), 1);
                                        } else {
                                            matchChannel.put(String.valueOf(j), count + 1);
                                        }
                                    }
                                }
                            }

                            /**
                             * get best matched result, the value must bigger than 2
                             * 1 - first compare value which is bigger
                             * 2 - if value is equal compare which is shorter
                             * 3 - if length is equal compare which contains the input string
                             */
                            bestCounter = 0;
                            bestPostion = "";
                            for (String position : matchChannel.keySet()) {
                                Integer value = matchChannel.get(position);

                                if (value >= 2) {
                                    if (value > bestCounter) {
                                        bestCounter = value;
                                        bestPostion = position;
                                    } else if (value == bestCounter) {
                                        String bestApp = ClientSendCommandService.serverAppInfo.get(Integer.valueOf(bestPostion)).appName;
                                        String newApp = ClientSendCommandService.serverAppInfo.get(Integer.valueOf(position)).appName;

                                        if (newApp.length() < bestApp.length()) {
                                            bestPostion = position;
                                        } else if (newApp.length() == bestApp.length()) {
                                            int bestIndex = bestApp.indexOf(recognitionResult);
                                            int newIndex = newApp.indexOf(recognitionResult);
                                            if (bestIndex < 0 && newIndex >= 0) {
                                                bestPostion = position;
                                            }
                                        }
                                    }
                                }
                            }

                            /**
                             * send command to the server to decide which one should open
                             */
                            if (StringUtils.hasLength(bestPostion)) {
                                AppInfo info = ClientSendCommandService.serverAppInfo.get(Integer.valueOf(bestPostion));
                                ClientSendCommandService.msg = "app_open:" + info.packageName;
                                ClientSendCommandService.handler.sendEmptyMessage(1);
                                Log.e(TAG, "message:" + "app_open:" + info.packageName);

                                Toast.makeText(TVRemoteControlActivity.this, getString(R.string.app_result) + info.appName + "\n" + getString(R.string.voice_result) + recognitionResult, Toast.LENGTH_LONG).show();
                                hasResult = true;
                            } else {
                                hasResult = false;
                            }
                        }

                    } catch (Exception e) {
                        hasResult = false;
                    }
                }
                /********************************************结束处理用户说的话******************************************/
            }

            if (!hasResult) {
                recognitioningFailedTimes = recognitioningFailedTimes + 1;
                if (recognitioningFailedTimes == 3) {
                    recognitioningFailedTimes = 0;
                    BaiDuVoiceChannelControlDialog yuYingHelpDialog = new BaiDuVoiceChannelControlDialog(TVRemoteControlActivity.this);
                    yuYingHelpDialog.show();
                } else {
				if(AppConfig.USE_MALL_APP)
				{
					if (recognitionResult.equals(getString(R.string.help))) {
						BaiDuVoiceChannelControlDialog yuYingHelpDialog = new BaiDuVoiceChannelControlDialog(TVRemoteControlActivity.this);
	                    yuYingHelpDialog.show();
					}else {
						ClientSendCommandService.msg = "mall:" + recognitionResult;
	                    ClientSendCommandService.handler.sendEmptyMessage(1);
					}					
                	
				}
				else
				{
                    Toast.makeText(TVRemoteControlActivity.this, getString(R.string.sorry_message_cannot_support) + recognitionResult, Toast.LENGTH_LONG).show();
				}
                }
            } else {
                recognitioningFailedTimes = 0;
            }
        }
    }
}
