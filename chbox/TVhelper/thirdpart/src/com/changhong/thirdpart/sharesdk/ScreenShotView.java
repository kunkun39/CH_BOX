package com.changhong.thirdpart.sharesdk;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

import com.changhong.thirdpart.R;
import com.changhong.thirdpart.sharesdk.util.L;
import com.changhong.thirdpart.sharesdk.util.ShareCenter;
import com.changhong.thirdpart.sharesdk.util.ShareUtil;

/**
 * 截屏View 截屏按钮可隐藏加在其它页面然后手动调用cutScreenAndShare()截屏。
 * 流程：截屏--显示截屏图片--停留一段时间后--显示动画--保存图片--分享
 * 
 * @author Administrator
 * 
 */
public class ScreenShotView extends RelativeLayout {
	private static final String TAG = "cutscreen";
	private static final int SHOW_ANIMATION = 100;
	private static final int SHOW_TOAST = 101;
	private static final int DO_SHARE = 102;
	private Context context;

	/** 最底层view */
	private RelativeLayout rootLayout;
	/** 半透明背景，截图图片 */
	private ImageView iv_imgalpha, iv_imgcut;
	private ScaleAnimation scaleAnimation;
	/** 截图图片 */
	private Bitmap mScreenBitmap;// 截屏图片
	private String imgPath = "";
	private boolean isSaving = false;// 图片是否正在保存，避免连续狂点

	// 分享参数
	/** 标题 */
	public String title = "电视助手标题";
	/** 链接 */
	public String titleUrl = null;
	/** 文本 */
	public String text = "来自电视助手的截屏分享内容";
	/** 回调 */
	public PlatformActionListener platformActionListener;

	/************************** 参数配置 ****************************************/
	/** 截屏时候是否替换掉上一张图片，true截图时候会删掉上一张截图，false 已时间命名，截图永远保存 **/
	public boolean isReplaceLastImage = true;
	/** 截屏图片显示停留时间 **/
	public int imgShowTime = 1000;
	/** 是否在该页面显示回调toast **/
	public boolean isShowCallBackToast = false;

	// /** 图片地址 */
	// public String outimagepath;

	public ScreenShotView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public ScreenShotView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	private void init() {
		rootLayout = (RelativeLayout) LayoutInflater.from(context).inflate(
				R.layout.view_screenshot, null);
		addView(rootLayout);
		iv_imgalpha = (ImageView) rootLayout.findViewById(R.id.iv_alpha);
		iv_imgcut = (ImageView) rootLayout.findViewById(R.id.iv_imgcut);
		iv_imgalpha.setVisibility(INVISIBLE);
		iv_imgcut.setVisibility(INVISIBLE);
		initAnimation();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_ANIMATION:
				// 缩放动画
				iv_imgcut.startAnimation(scaleAnimation);
				break;
			case SHOW_TOAST:
				// 弹出toast提示
				showToast(msg.obj + "");
				break;
			case DO_SHARE:
				// 分享操作
				doShare();
			default:
				break;
			}

		}
	};

	/**
	 * 传递分享参数截屏并且分享，所有参数都可为空，为空时候采用默认值
	 * 
	 * @param title
	 *            标题
	 * @param titleurl
	 *            链接
	 * @param text
	 *            文本
	 * @param platformActionListener
	 *            回调函数
	 */
	public void cutScreenAndShare(String title, String titleurl, String text) {
		this.title = title;
		this.titleUrl = titleurl;
		this.text = text;
		cutScreenAndShare();
	}

	/**
	 * 截图分享
	 * 
	 * @param title
	 * @param text
	 * @param platformActionListener
	 * @param bitmaps
	 *            传入的图片层，
	 */
	public void cutScreenAndShare(String title, String text, Bitmap... bitmaps) {
		cutScreenAndShare(title, null, text, bitmaps);
	}

	/**
	 * 截图分享
	 * 
	 * @param title
	 * @param titleurl
	 * @param text
	 * @param platformActionListener
	 * @param bitmaps
	 *            传入的图片层，
	 */
	public void cutScreenAndShare(String title, String titleurl, String text,
			Bitmap... bitmaps) {
		this.title = title;
		this.titleUrl = titleurl;
		this.text = text;
		LayerDrawable la = ShareUtil.getLayerDrawable(bitmaps);
		iv_imgcut.setImageDrawable(la);
		mScreenBitmap = ShareUtil.screenshot(iv_imgcut);
		cutScreenAndShare();
	}

	/**
	 * 截图并发送消息显示动画
	 */
	public void cutScreenAndShare() {
		if (!isSaving) {
			isSaving = true;
		}
		if (mScreenBitmap == null) {
			mScreenBitmap = ShareUtil.screenshot(((Activity) context)
					.getWindow().getDecorView());
			iv_imgcut.setImageBitmap(mScreenBitmap);
		}
		if (mScreenBitmap == null) {
			showToast("截屏图片为空");
			return;
		}
		iv_imgalpha.setVisibility(VISIBLE);
		iv_imgcut.setVisibility(VISIBLE);

		handler.sendEmptyMessageDelayed(SHOW_ANIMATION, imgShowTime);// 发送消息显示动画
	}

	/**
	 * 调用分享接口分享
	 */
	private void doShare() {
		// 电视助手调用例子（弹出意见分享对话框）。
		ShareCenter shareCenter = ShareFactory.getShareCenter(context);
		if (platformActionListener != null) {
			shareCenter.setMyplatformActionListener(platformActionListener);
		}
		shareCenter.showShareMenu(title, titleUrl, text, imgPath);
		isSaving = false;
	}

	public void setmScreenBitmap(Bitmap mScreenBitmap) {
		this.mScreenBitmap = mScreenBitmap;
	}

	public boolean isSaving() {
		return isSaving;
	}

	public void setSaving(boolean isSaving) {
		this.isSaving = isSaving;
	}

	public void saveImageFile() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String imageDir = ShareUtil.getCutScreenImgDirectory(
							context).getAbsolutePath();// 图片保存位置
					String picname = ShareUtil.getDayOfToday();
					if (!TextUtils.isEmpty(picname)) {
						picname = picname.replace("-", "").replace(":", "")
								+ ".png";
					}
					if (isReplaceLastImage) {
						imgPath = imageDir + File.separator + "screencut.png";// 替换之前图片
					} else {
						imgPath = imageDir + File.separator + picname;// 永远保存
					}

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
					isSaving = false;
				}
			}
		}).start();
	}

	private void showToast(String content) {
		Toast.makeText(context, content, Toast.LENGTH_LONG).show();
	}

	public PlatformActionListener getPlatformActionListener() {
		return platformActionListener;
	}

	public void setPlatformActionListener(
			PlatformActionListener platformActionListener) {
		this.platformActionListener = platformActionListener;
	}

	private void initAnimation() {
		scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
				Animation.RELATIVE_TO_PARENT, 1.0f,
				Animation.RELATIVE_TO_PARENT, 1.0f);
		scaleAnimation.setFillAfter(false);
		scaleAnimation.setDuration(300);// 设置动画持续时间
		scaleAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// rootLayout.setVisibility(INVISIBLE);
				iv_imgalpha.setVisibility(INVISIBLE);
				iv_imgcut.setVisibility(INVISIBLE);
				saveImageFile();
			}
		});

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		// L.d(TAG + "on draw");
		super.onDraw(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		// L.d(TAG + "on layout l=" + l + " t=" + t + " r=" + r + " b=" + b);
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// L.d(TAG + "on onMeasure widthMeasureSpec=" + widthMeasureSpec
		// + " heightMeasureSpec=" + heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	// private void init() {//代码布局方式
	//
	// // 最底层view
	// RelativeLayout.LayoutParams rootParams = new RelativeLayout.LayoutParams(
	// RelativeLayout.LayoutParams.MATCH_PARENT,
	// RelativeLayout.LayoutParams.MATCH_PARENT);
	// setLayoutParams(rootParams);
	// rootLayout = new RelativeLayout(context);
	// rootLayout.setLayoutParams(rootParams);
	//
	// // 半透明背景
	// iv_imgalpha = new ImageView(context);
	// iv_imgalpha.setLayoutParams(rootParams);
	// iv_imgalpha.setBackgroundColor(0x77000000);
	// rootLayout.addView(iv_imgalpha);
	//
	// WindowManager wm = (WindowManager) context
	// .getSystemService(Context.WINDOW_SERVICE);
	// int width = wm.getDefaultDisplay().getWidth();
	// int height = wm.getDefaultDisplay().getHeight();
	// L.d(TAG + " screenwidth= " + width + " screenheight=  " + height);
	//
	// // 截屏图片
	// iv_imgcut = new ImageView(context);
	// RelativeLayout.LayoutParams imgcutParams = new
	// RelativeLayout.LayoutParams(
	// RelativeLayout.LayoutParams.MATCH_PARENT,
	// RelativeLayout.LayoutParams.MATCH_PARENT);
	// imgcutParams.width = width * 4 / 5;
	// imgcutParams.height = height * 4 / 5;
	// // imgcutParams.setMargins(100, 100, 100, 100);
	// imgcutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
	// iv_imgcut.setLayoutParams(imgcutParams);
	// rootLayout.addView(iv_imgcut);
	//
	// // 按钮
	// bt_screenshot = new Button(context);
	// bt_screenshot.setText("截屏分享");
	// RelativeLayout.LayoutParams bt_params = new RelativeLayout.LayoutParams(
	// RelativeLayout.LayoutParams.WRAP_CONTENT,
	// RelativeLayout.LayoutParams.WRAP_CONTENT);
	// bt_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	// bt_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	// bt_screenshot.setLayoutParams(bt_params);
	// rootLayout.addView(bt_screenshot);
	//
	// //toastView
	// tv_toast=new TextView(context);
	// tv_toast.setText("分享成功");
	// RelativeLayout.LayoutParams tv_toast_params = new
	// RelativeLayout.LayoutParams(
	// RelativeLayout.LayoutParams.WRAP_CONTENT,
	// RelativeLayout.LayoutParams.WRAP_CONTENT);
	// tv_toast_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	// tv_toast_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
	// tv_toast.setLayoutParams(tv_toast_params);
	// tv_toast_params.bottomMargin=20;
	// tv_toast.setVisibility(INVISIBLE);
	// rootLayout.addView(tv_toast);
	//
	// bt_screenshot.setVisibility(isButtonOutside ? GONE : VISIBLE);
	// iv_imgalpha.setVisibility(INVISIBLE);
	// iv_imgcut.setVisibility(INVISIBLE);
	// addView(rootLayout);
	// initAnimation();
	//
	// bt_screenshot.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// cutScreenAndShare();
	// }
	// });
	// }
}
