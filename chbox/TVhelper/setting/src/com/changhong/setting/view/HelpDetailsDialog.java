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
import android.widget.TextView;

import com.changhong.common.system.MyApplication;
import com.changhong.setting.R;

public class HelpDetailsDialog extends Dialog{

	Boolean barDisplayControl = true;
	View mView;
	View navigationBar;

	private TextView help_name;
	private TextView help_content;
	private String name,content;
	private ImageButton ibCancel;
	
	public void setParameter(String name,String content){
		this.name=name;
		this.content=content;
	}
	
	public HelpDetailsDialog(Context context) {
		super(context,R.style.Translucent_NoTitle);
		setContentView(R.layout.setting_sys_help_dialog_details);
		
		Window window = this.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
		window.setAttributes(wlp);
		window.setGravity(Gravity.BOTTOM);

		navigationBar =  findViewById(R.id.bar);
		mView =  findViewById(R.id.view);
		help_name = (TextView) findViewById(R.id.help_name);
		help_content = (TextView) findViewById(R.id.help_content);
		ibCancel = (ImageButton) findViewById(R.id.cancel_help);
		Log.i("mmmm", "content==" + name + content);

		ibCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				dismiss();
			}
		});
		
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

	@Override
	public void show() {
		super.show();
		help_name.setText(name);
		help_content.setText(content);
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
