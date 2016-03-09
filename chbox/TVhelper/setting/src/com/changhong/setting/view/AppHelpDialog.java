package com.changhong.setting.view;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.changhong.common.system.AppConfig;
import com.changhong.common.system.MyApplication;
import com.changhong.setting.R;

/**
 * Ma Ren
 */

public class AppHelpDialog extends Dialog {
	
	
	Boolean barDisplayControl = true;
	LinearLayout mView;
	LinearLayout navigationBar;

	private View remote_control_help;
	private View voice_control_help;
	private View yuyue_control_help;
	private View file_control_help;
	private HelpDetailsDialog hdd;

	public AppHelpDialog(final Context context) {
		super(context, R.style.Translucent_NoTitle);
		setContentView(R.layout.setting_sys_help_dialog);

		Window window = this.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
		window.setAttributes(wlp);
		window.setGravity(Gravity.BOTTOM);

		ImageButton helpButton = (ImageButton) findViewById(R.id.cancel_help);
		remote_control_help = findViewById(R.id.remote_control_help);
		voice_control_help = findViewById(R.id.voice_control_help);
		yuyue_control_help = findViewById(R.id.yuyue_control_help);
		file_control_help = findViewById(R.id.file_control_help);
		mView = (LinearLayout) findViewById(R.id.view);
		navigationBar = (LinearLayout) findViewById(R.id.bar);
        
        mView = (LinearLayout) findViewById(R.id.view);
        navigationBar = (LinearLayout) findViewById(R.id.bar);

		helpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				dismiss();
			}
		});

		if (!AppConfig.USE_TV) {
			remote_control_help.setVisibility(View.GONE);
		} else {
			remote_control_help.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					MyApplication.vibrator.vibrate(100);
					startDetailsDialog(context, context.getString(R.string.rch_name), context.getString(R.string.rch_content));
				}
			});
		}

		if (!AppConfig.USE_VOICE_INPUT) {
			voice_control_help.setVisibility(View.GONE);
		} else {
			voice_control_help.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					MyApplication.vibrator.vibrate(100);
					startDetailsDialog(context, context.getString(R.string.vch_name), context.getString(R.string.vch_content));
				}
			});
		}


		if (!AppConfig.USE_TV) {
			yuyue_control_help.setVisibility(View.GONE);
		} else {
			yuyue_control_help.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MyApplication.vibrator.vibrate(100);
					startDetailsDialog(context, context.getString(R.string.pch_name), context.getString(R.string.pch_content));
				}
			});
		}

		if (!AppConfig.USE_OTHER_AIRDISPLAY) {
			file_control_help.setVisibility(View.GONE);
		} else {
			file_control_help.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MyApplication.vibrator.vibrate(100);
					startDetailsDialog(context, context.getString(R.string.pdf_ppt_hologram), context.getString(R.string.pdf_ppt_content));
				}
			});
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
		}
        
        
      	mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                
               if(barDisplayControl){
            	   navigationBar.setVisibility(View.GONE);
            	   barDisplayControl=false;
               }else{
            	   navigationBar.setVisibility(View.VISIBLE);
            	   barDisplayControl=true;
               }
            }
        });
      	
	}
	
	private void startDetailsDialog(Context context,String name,String content){
		if (null==hdd){
			hdd=new HelpDetailsDialog(context);
		}
		hdd.setParameter(name, content);
		Log.i("mmmm","content==" +name+content);
		hdd.show();
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
