package com.changhong.thirdpart.test;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.changhong.thirdpart.R;
import com.changhong.thirdpart.location.LocationAttribute;
import com.changhong.thirdpart.location.LocationUtil;
import com.changhong.thirdpart.sharesdk.ScreenShotView;
import com.changhong.thirdpart.sharesdk.util.L;
import com.changhong.thirdpart.sharesdk.util.ShareConfig;
import com.changhong.thirdpart.uti.Util;
import com.igexin.push.config.l;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * 第三方工具测试类
 * 
 * @author Administrator
 * 
 */
public class ThirdpartTestActivity extends Activity implements OnClickListener {

	private ScreenShotView viewShare;
//	private String title, titleurl, text;
	/** 标题 */
	public String title = "长虹电视助手";
	/** 链接 */
	public String titleUrl = null;
	/** 文本 */
	public String text = "我正在观看XXX台，XXX节目，好精彩呀!";
	private EditText edt_title, edt_titleurl, edt_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		initView();
		initPushServer();
	}

	private void initPushServer() {
		startService(new Intent(ThirdpartTestActivity.this, MainService.class));
	}

	private void initView() {
		findViewById(R.id.bt_startshare).setOnClickListener(this);
		findViewById(R.id.bt_startlocation).setOnClickListener(this);
		viewShare = (ScreenShotView) findViewById(R.id.view_sharetest);
		edt_text = (EditText) findViewById(R.id.edt_text);
		edt_title = (EditText) findViewById(R.id.edt_title);
		edt_titleurl = (EditText) findViewById(R.id.edt_titleurl);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.bt_startshare) {
//			title = edt_title.getText().toString();
//			titleUrl = edt_titleurl.getText().toString();
//			text = edt_text.getText().toString();
			L.d(MainService.TAG+"sharemsg==title="+title+" titleurl="+titleUrl+" text=="+text);
			viewShare.cutScreenAndShare(title, titleUrl, text);
		} else if (id == R.id.bt_startlocation) {
			LocationUtil.getInstance().stop();
			LocationUtil.getInstance().start();
			LocationAttribute location = LocationUtil.getInstance()
					.getLocationAttribute();
			
			if (location!=null &&location.getAddress() != null) {
				Util.showToast(this, location.getAddress(), 3000);
			}
		}
	}
	
	public void shareToQQ() {
		Tencent mTencent = Tencent.createInstance(ShareConfig.QQPPID,
				this);
		Bundle params = new Bundle();
		params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,"/storage/sdcard1/DCIM/Camera/IMG_20150703_091824.jpg");
	    params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "");
	    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
//	    params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
		mTencent.shareToQQ(this, params,
				qqShareListener);
	}

	IUiListener qqShareListener = new IUiListener() {
        @Override
        public void onCancel() {
        	L.e("cutscreen QQonCancel");
        }
        @Override
        public void onComplete(Object response) {
        	L.e("cutscreen QQonComplete "+response.toString());
        }
        @Override
        public void onError(UiError e) {
        	L.e("cutscreen QQonerror "+e.toString());
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
//        if (requestCode == Constants.REQUEST_QQ_SHARE) {
//        	if (resultCode == Constants.ACTIVITY_OK) {
        		Tencent.handleResultData(data, qqShareListener);
//        	}
//        }
    }
}
