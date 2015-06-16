package com.changhong.thirdpart.sharesdk;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

import com.changhong.thirdpart.R;
import com.changhong.thirdpart.sharesdk.util.L;
import com.changhong.thirdpart.sharesdk.util.ShareCenter;
import com.changhong.thirdpart.sharesdk.util.ShareUtil;

public class CutScreenActivity extends Activity {
	private Context context;
	private ImageView iv_img, iv_imgcut, iv_imgalpha;
	private Button bt_showanim;
	private ScaleAnimation scalSmallAnimation;
	private Bitmap mScreenBitmap;// 截屏图片
	private String imgPath = "";
	private boolean isSaving = false;// 图片是否正在保存，避免连续狂点
	private static final int SHOW_ANIMATION = 100;
	private static final int SHOW_TOAST = 101;
	private static final int DO_SHARE = 102;
	private static final String TAG = "CutScreenActivity  ";

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_ANIMATION:
				iv_imgcut.startAnimation(scalSmallAnimation);
				break;
			case SHOW_TOAST:
				showToast(msg.obj + "");
				break;
			case DO_SHARE:
				doShare();
				break;
			default:
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cutscreen);
		context = this.getApplicationContext();
		iv_img = (ImageView) findViewById(R.id.iv_img);
		iv_imgcut = (ImageView) findViewById(R.id.iv_imgcut);
		iv_imgalpha = (ImageView) findViewById(R.id.iv_imgalpha);
		bt_showanim = (Button) findViewById(R.id.tv_showanim);
		initAnimation();
		initListener();
		ShareSDK.initSDK(this);
	}

	private void initListener() {
		bt_showanim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isSaving) {
					Toast.makeText(CutScreenActivity.this, "图片正在保存中，请稍后",
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					isSaving = !isSaving;
				}
				cutScreen();
			}
		});
	}

	/**
	 * 截图并发送消息显示动画
	 */
	private void cutScreen() {
		mScreenBitmap = screenshot(getWindow().getDecorView());
		if (mScreenBitmap == null) {
			Toast.makeText(this, "截图为空", Toast.LENGTH_SHORT).show();
			return;
		}
		mScreenBitmap.setHasAlpha(false);
		mScreenBitmap.prepareToDraw();
		iv_imgcut.setImageBitmap(mScreenBitmap);
		iv_imgalpha.setVisibility(View.VISIBLE);
		iv_imgcut.setVisibility(View.VISIBLE);
		handler.sendEmptyMessageDelayed(SHOW_ANIMATION, 1000);// 发送消息显示动画
	}

	private void saveImageFile() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String imageDir = ShareUtil.getCutScreenImgDirectory(
							context).getAbsolutePath();// 图片保存位置
					String picname = ShareUtil.getDayOfToday();
					if (!TextUtils.isEmpty(picname)) {
						picname = picname.replace("-", "").replace(":", "");
					}
					// imgPath = imageDir + File.separator + picname;//TODO
					// 正式使用该方式
					imgPath = imageDir + File.separator + "screencut";// TODO
																		// 避免太多图片临时使用
					L.d(TAG + "imgPath==" + imgPath);
					File file = new File(imgPath);
					if (file != null && file.exists()) {
						file.delete();
					}
					FileOutputStream out = new FileOutputStream(imgPath);
					mScreenBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
					handler.sendEmptyMessage(DO_SHARE);
					// doShare();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					isSaving = false;
				}
			}
		}).start();
	}

	/**
	 * 调用分享接口分享
	 */
	private void doShare() {
		{// TODO 电视助手调用例子（弹出意见分享对话框）。
			String title = "电视助手标题";
			String titleUrl = "http://www.baidu.com";
			String text = "电视助手截屏分享，详情请咨询：110119";
			String imagepath = imgPath;
			ShareCenter.showOneKeyShare(CutScreenActivity.this, title, titleUrl,
					text, imagepath, paListener, null);
		}
//		{// 直接分享
//			// http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/
//			ShareParams shareParams = new ShareParams();
//			shareParams.setTitle("我的标题");
//			shareParams.setTitleUrl("http://www.baidu.com");
//			shareParams.setText("我的分享内容");
//			shareParams.setImagePath(imgPath);
//			shareParams.setImageUrl("http://ytqmp.qiniudn.com/biaoqing/bairen12_qmp.gif");
//			shareParams.setSite("site标题");
//			shareParams.setShareType(Wechat.SHARE_IMAGE);
//			shareParams
//					.setSiteUrl("http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/");
//			boolean silent = true;
////			shareParams.setFilePath("/sdcard/TVhelper8.apk");
////			 File file=new File("/storage/emulated/0/TVhelper8.apk");
////			 L.d(TAG+"filesize=="+file.length());
//			shareParams.setExtInfo("App信息TVhelper8");
//
//			String platform = Wechat.NAME;
//			ShareCenter.shareByShareParams(context, shareParams, platform,
//					paListener);
//		}
	}

	private PlatformActionListener paListener = new PlatformActionListener() {

		@Override
		public void onError(Platform arg0, int arg1, Throwable arg2) {
			L.e(TAG + " onError " + arg0.toString() + "  arg1= " + arg1
					+ " arg2.msg=" + arg2.getMessage() + " arg2=="
					+ arg2.toString());
			Message msg = handler.obtainMessage(SHOW_TOAST);
			msg.obj = "分享发生异常";
			handler.sendMessage(msg);
		}

		@Override
		public void onComplete(Platform arg0, int arg1,
				HashMap<String, Object> arg2) {
			L.e(TAG + " onComplete " + arg0.toString() + "  arg1= " + arg1);
			// showToast("分享完成toast");
			Message msg = handler.obtainMessage(SHOW_TOAST);
			msg.obj = "分享完成";
			handler.sendMessage(msg);
		}

		@Override
		public void onCancel(Platform arg0, int arg1) {
			L.e(TAG + " onCancel " + arg0.toString() + "  arg1= " + arg1 + "");
			// showToast("分享取消toast");
			Message msg = handler.obtainMessage(SHOW_TOAST);
			msg.obj = "分享取消";
			handler.sendMessage(msg);
		}
	};

	private void initAnimation() {
		scalSmallAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
				Animation.RELATIVE_TO_PARENT, 1.0f,
				Animation.RELATIVE_TO_PARENT, 1.0f);
		scalSmallAnimation.setFillAfter(false);
		scalSmallAnimation.setDuration(300);// 设置动画持续时间
		scalSmallAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				iv_imgcut.setVisibility(View.GONE);
				iv_imgalpha.setVisibility(View.GONE);
				saveImageFile();
			}
		});

	}
	
	 public  Bitmap screenshot(View view) {
	        view.setDrawingCacheEnabled(true);
	        view.buildDrawingCache();
	        Bitmap bmp = view.getDrawingCache();
	        return bmp;
	    }

	private void showToast(String content) {
		Toast.makeText(CutScreenActivity.this, content, Toast.LENGTH_LONG)
				.show();
		L.d(TAG + "toastcontent==	" + content);
	}
}
