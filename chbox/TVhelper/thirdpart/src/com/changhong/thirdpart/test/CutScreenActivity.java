package com.changhong.thirdpart.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.changhong.thirdpart.R;
import com.changhong.thirdpart.sharesdk.ScreenShotView;

public class CutScreenActivity extends Activity implements OnClickListener {
	private Context context;
	private Button btSubmit;
	private ScreenShotView screenShotView;
	private static final String TAG = "CutScreenActivity  ";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cutscreen);
		context = this.getApplicationContext();
		initView();
	}

	private void initView() {
		screenShotView = (ScreenShotView) findViewById(R.id.view_screenshot);
		btSubmit = (Button) findViewById(R.id.bt_submit);
		btSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				screenShotView.cutScreenAndShare();
			}
		});
		findViewById(R.id.bt_1).setOnClickListener(this);
		findViewById(R.id.bt_2).setOnClickListener(this);
		findViewById(R.id.bt_3).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.bt_1) {
//			screenShotView.shareWeiXinHaoyou();
		} else if (id == R.id.bt_2) {
//			screenShotView.shareWeiXinFriends();
		} else {
//			screenShotView.shareWeiXinConnect();
		}
	}
}
