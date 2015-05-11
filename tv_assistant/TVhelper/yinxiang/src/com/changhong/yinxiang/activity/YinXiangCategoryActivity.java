package com.changhong.yinxiang.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.changhong.common.system.MyApplication;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.nanohttpd.HTTPDService;

/**
 * Created by Jack Wang
 */
public class YinXiangCategoryActivity extends Activity {

	/************************************************** IP连接部分 *******************************************************/

	private Button back;

	/************************************************** 菜单部分 *******************************************************/
	private ImageView imageTouYing;
	private ImageView vedioTouYing;
	private ImageView musicTouYing;
	private ImageView otherTouYing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/**
		 * 启动Http服务
         */
        Intent http = new Intent(YinXiangCategoryActivity.this, HTTPDService.class);
        startService(http);

        initMedia();

		initView();

		initEvent();
	}

    private void initMedia() {
        /**
         * 通知系统媒体去更新媒体库
         */
        String[] types = {"video/3gpp", "video/x-msvideo", "video/mp4", "video/mpeg", "video/quicktime",
                "audio/x-wav", "audio/x-pn-realaudio", "audio/x-ms-wma", "audio/x-ms-wmv", "audio/x-mpeg", "image/jpeg", "image/png"};
        MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().getAbsolutePath()}, types, null);
    }

	private void initView() {
		setContentView(R.layout.activity_yinxiang_category);
		/**
		 * IP连接部分
		 */
		back = (Button) findViewById(R.id.btn_back);

		/**
		 * 菜单部分
		 */
		imageTouYing = (ImageView) findViewById(R.id.button_image_touying);
		vedioTouYing = (ImageView) findViewById(R.id.button_vedio_touying);
		musicTouYing = (ImageView) findViewById(R.id.button_music_touying);
		otherTouYing = (ImageView) findViewById(R.id.button_other_touying);
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
		 * 菜单部分
		 */
//		imageTouYing.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MyApplication.vibrator.vibrate(100);
//                Intent intent = new Intent(TouYingCategoryActivity.this, PictureCategoryActivity.class);
//                startActivity(intent);
//            }
//        });
        vedioTouYing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                Intent intent = new Intent(YinXiangCategoryActivity.this, YinXiangVedioViewActivity.class);
                startActivity(intent);
            }
        });
//		musicTouYing.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//                MyApplication.vibrator.vibrate(100);
//                Intent intent = new Intent(TouYingCategoryActivity.this, MusicCategoryActivity.class);
//                startActivity(intent);
//            }
//        });
        otherTouYing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(YinXiangCategoryActivity.this, "敬请期待中...", Toast.LENGTH_LONG).show();
            }
        });

	}

	/********************************************** 系统发发重载 *********************************************************/

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
