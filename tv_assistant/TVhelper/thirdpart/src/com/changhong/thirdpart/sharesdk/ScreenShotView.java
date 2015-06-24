package com.changhong.thirdpart.sharesdk;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.weibo.TencentWeibo;

import com.changhong.thirdpart.R;
import com.changhong.thirdpart.sharesdk.util.L;
import com.changhong.thirdpart.sharesdk.util.ShareCenter;
import com.changhong.thirdpart.sharesdk.util.ShareUtil;
import com.changhong.thirdpart.sharesdk.util.ShareWeiXin.WeiXin;

/**
 * 截屏View 截屏按钮可隐藏加在其它页面然后手动调用cutScreenAndShare()截屏。
 * 流程：截屏--显示截屏图片--停留一段时间后--显示动画--保存图片--分享
 * 
 * @author Administrator
 * 
 */
public class ScreenShotView extends RelativeLayout {
	private static final String TAG = "screenshotview  ";
	private static final int SHOW_ANIMATION = 100;
	private static final int SHOW_TOAST = 101;
	private static final int DO_SHARE = 102;
	private Context context;

	/** 最底层view */
	private RelativeLayout rootLayout;
	/** 半透明背景，截图图片 */
	private ImageView iv_imgalpha, iv_imgcut;
	/** 按钮 */
	private Button bt_screenshot;
	private ScaleAnimation scaleAnimation;
	/** 截图图片 */
	private Bitmap mScreenBitmap;// 截屏图片
	private String imgPath = "";
	private boolean isSaving = false;// 图片是否正在保存，避免连续狂点

	// 分享参数
	/** 标题 */
	public String title = "电视助手标题";
	/** 链接 */
	public String titleUrl = "http://www.baidu.com";
	/** 文本 */
	public String text = "来自电视助手的截屏分享";
	/** 回调 */
	public PlatformActionListener platformActionListener;

	/************************** 参数配置 ****************************************/
	/** 截屏时候是否替换掉上一张图片，true截图时候会删掉上一张截图，false 已时间命名，截图永远保存 **/
	public boolean isReplaceLastImage = true;
	/** 截屏图片显示停留时间 **/
	public int imgShowTime = 1000;
	/** 截屏按钮是否在其它页面 **/
	public boolean isButtonOutside = true;
	/** 是否在该页面显示回调toast **/
	public boolean isShowCallBackToast = true;

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
		// 最底层view
		RelativeLayout.LayoutParams rootParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		setLayoutParams(rootParams);
		rootLayout = new RelativeLayout(context);
		rootLayout.setLayoutParams(rootParams);

		// 半透明背景
		iv_imgalpha = new ImageView(context);
		iv_imgalpha.setLayoutParams(rootParams);
		iv_imgalpha.setBackgroundColor(0x77000000);
		rootLayout.addView(iv_imgalpha);

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		L.d(TAG + " screenwidth= " + width + " screenheight=  " + height);

		// 截屏图片
		iv_imgcut = new ImageView(context);
		RelativeLayout.LayoutParams imgcutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		imgcutParams.width = width * 4 / 5;
		imgcutParams.height = height * 4 / 5;
		// imgcutParams.setMargins(100, 100, 100, 100);
		imgcutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		iv_imgcut.setLayoutParams(imgcutParams);
		rootLayout.addView(iv_imgcut);

		// 按钮
		bt_screenshot = new Button(context);
		bt_screenshot.setText("截屏分享");
		RelativeLayout.LayoutParams bt_params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		bt_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		bt_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		bt_screenshot.setLayoutParams(bt_params);
		rootLayout.addView(bt_screenshot);

		bt_screenshot.setVisibility(isButtonOutside ? GONE : VISIBLE);
		iv_imgalpha.setVisibility(INVISIBLE);
		iv_imgcut.setVisibility(INVISIBLE);
		addView(rootLayout);
		initAnimation();

		bt_screenshot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cutScreenAndShare();
			}
		});
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_ANIMATION:
				//缩放动画
				iv_imgcut.startAnimation(scaleAnimation);
				break;
			case SHOW_TOAST:
				//弹出toast提示
				showToast(msg.obj + "");
				break;
			case DO_SHARE:
				//分享操作
				doShare();
				break;
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
	public void cutScreenAndShare(String title, String titleurl, String text,
			PlatformActionListener platformActionListener) {
		this.title = title;
		this.titleUrl = titleurl;
		this.text = text;
		this.platformActionListener = platformActionListener;
		cutScreenAndShare();
	}

	/**
	 * 截图并发送消息显示动画
	 */
	public void cutScreenAndShare() {
		if (isSaving) {
			showToast("图片正在保存中，请稍后");
			return;
		} else {
			isSaving = !isSaving;
		}
		mScreenBitmap = screenshot(((Activity) context).getWindow()
				.getDecorView());
		if (mScreenBitmap == null) {
			showToast("截屏图片为空");
			return;
		}
		mScreenBitmap.setHasAlpha(false);
		mScreenBitmap.prepareToDraw();
		iv_imgalpha.setVisibility(VISIBLE);
		iv_imgcut.setVisibility(VISIBLE);
		
//		Bitmap bgBitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
//		Drawable[] array = new Drawable[3];  
//        array[2] = new BitmapDrawable(bgBitmap);  
//        array[1] = new BitmapDrawable(mScreenBitmap); 
//        array[0] = new BitmapDrawable(bgBitmap);  
//        LayerDrawable la = new LayerDrawable(array);  
//        la.setLayerInset(2, 0, 0, 0, 0);
//        la.setLayerInset(1, 0, 0, 0, 0);
//        la.setLayerInset(0, 80, 80, 0, 0);
//		iv_imgcut.setImageDrawable(la);
//		mScreenBitmap =screenshot(iv_imgcut);
		
		iv_imgcut.setImageBitmap(mScreenBitmap);
		handler.sendEmptyMessageDelayed(SHOW_ANIMATION, imgShowTime);// 发送消息显示动画
	}

	/**
	 * 截屏
	 * 
	 * @param view
	 * @return
	 */
	public Bitmap screenshot(View view) {
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		return bmp;
	}

	String imageUrl="http://ytqmp.qiniudn.com/biaoqing/bairen12_qmp.gif";
	String musicUrl="http://media.ringring.vn/ringtone/realtone/0/0/161/165346.mp3";
	/**
	 * 调用分享接口分享
	 */
	private void doShare() {
		{// TODO 电视助手调用例子（弹出意见分享对话框）。
			ShareFactory.getShareCenter(context, paListener).showShareMenu(title, titleUrl, text, imgPath);
		}
//		ShareFactory.getShareQQ(context, paListener).shareImgByPath(title, titleUrl, text, imgPath);
//		ShareFactory.getShareQQ(context, paListener).shareMusicWithImgurl(title, titleUrl, text, imgPath, musicUrl);
//		ShareFactory.getShareQZone(context, paListener).shareText(title, titleUrl, text, "site", musicUrl);
//		ShareFactory.getShareQZone(context, paListener).shareImageUrl(title, titleUrl, text, imageUrl, "site", musicUrl);
		
//		ShareFactory.getShareSinaWeiBo(context, paListener).shareImagePath(text, imgPath);
//		ShareFactory.getShareTencentWeiBo(context, paListener).shareImageArray(text, new String[]{imgPath,imgPath,imageUrl,imageUrl}, 1, 1);
		
		// {// 直接分享
		// //
		// //http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/
//		 ShareParams shareParams = new ShareParams();
//		 shareParams.setTitle("我的标题");
//		 shareParams.setTitleUrl("http://www.baidu.com");
//		 shareParams.setText("我的分享内容");
//		 shareParams.setImagePath(imgPath);
//		 shareParams.setImageUrl("http://ytqmp.qiniudn.com/biaoqing/bairen12_qmp.gif");
//		 shareParams.setSite("site标题");
//		 // shareParams.setShareType(Wechat.SHARE_IMAGE);
//		 shareParams
//		 .setSiteUrl("http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/");
//		 boolean silent = true;
//		 // shareParams.setFilePath("/sdcard/TVhelper8.apk");
//		 // File file=new File("/storage/emulated/0/TVhelper8.apk");
//		 // L.d(TAG+"filesize=="+file.length());
//		 shareParams.setExtInfo("App信息TVhelper8");
//		
//		 String platform = TencentWeibo.NAME;
//		 ShareCenter.shareByShareParams(context, shareParams, platform,
//		 paListener);
//		 }
	}
	Bitmap imageData=null;
	public void shareWeiXinHaoyou() {
		imageData=BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
//		ShareFactory.getSharWeiXin(context, paListener).shareText(WeiXin.Wechat, title, text);
//		ShareFactory.getSharWeiXin(context, paListener).shareImageByPath(WeiXin.Wechat, title, text, imgPath);
//		ShareFactory.getSharWeiXin(context, paListener).shareImageByUrl(WeiXin.Wechat, title, text, imageUrl);
		ShareFactory.getSharWeiXin(context, paListener).shareImageByData(WeiXin.Wechat, title, text, imageData);
	
	}
	public void shareWeiXinFriends() {
		imageData=BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
//		ShareFactory.getSharWeiXin(context, paListener).shareText(WeiXin.WechatMoments, title, text);
//		ShareFactory.getSharWeiXin(context, paListener).shareImageByPath(WeiXin.WechatMoments, title, text, imgPath);
//		ShareFactory.getSharWeiXin(context, paListener).shareImageByUrl(WeiXin.WechatMoments, title, text, imageUrl);
		ShareFactory.getSharWeiXin(context, paListener).shareImageByData(WeiXin.WechatMoments, title, text, imageData);
			
	}
	public void shareWeiXinConnect() {
		imageData=BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
//		ShareFactory.getSharWeiXin(context, paListener).shareText(WeiXin.WechatFavorite, title, text);
//		ShareFactory.getSharWeiXin(context, paListener).shareImageByPath(WeiXin.WechatFavorite, title, text, imgPath);
//		ShareFactory.getSharWeiXin(context, paListener).shareImageByUrl(WeiXin.WechatFavorite, title, text, imageUrl);
		ShareFactory.getSharWeiXin(context, paListener).shareImageByData(WeiXin.WechatFavorite, title, text, imageData);
		
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
				} finally {
					isSaving = false;
				}
			}
		}).start();
	}

	private void showToast(String content) {
		Toast.makeText(context, content, Toast.LENGTH_LONG).show();
	}

	private PlatformActionListener paListener = new PlatformActionListener() {

		@Override
		public void onError(Platform arg0, int arg1, Throwable arg2) {
			L.e(TAG + " onError " + arg0.toString() + "  arg1= " + arg1
					+ " arg2.msg=" + arg2.getMessage() + " arg2=="
					+ arg2.toString());
			if (isShowCallBackToast) {
				Message msg = handler.obtainMessage(SHOW_TOAST);
				msg.obj = "分享发生异常";
				handler.sendMessage(msg);
			}
			if (platformActionListener != null) {
				platformActionListener.onError(arg0, arg1, arg2);
			}
		}

		@Override
		public void onComplete(Platform arg0, int arg1,
				HashMap<String, Object> arg2) {
			L.e(TAG + " onComplete " + arg0.toString() + "  arg1= " + arg1);
			if (isShowCallBackToast) {
				Message msg = handler.obtainMessage(SHOW_TOAST);
				msg.obj = "分享完成";
				handler.sendMessage(msg);
			}
			if (platformActionListener != null) {
				platformActionListener.onComplete(arg0, arg1, arg2);
			}
		}

		@Override
		public void onCancel(Platform arg0, int arg1) {
			L.e(TAG + " onCancel " + arg0.toString() + "  arg1= " + arg1 + "");
			if (isShowCallBackToast) {
				Message msg = handler.obtainMessage(SHOW_TOAST);
				msg.obj = "分享取消";
				handler.sendMessage(msg);
			}
			if (platformActionListener != null) {
				platformActionListener.onCancel(arg0, arg1);
			}
		}
	};

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
//		L.d(TAG + "on draw");
		super.onDraw(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
//		L.d(TAG + "on layout l=" + l + " t=" + t + " r=" + r + " b=" + b);
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		L.d(TAG + "on onMeasure widthMeasureSpec=" + widthMeasureSpec
//				+ " heightMeasureSpec=" + heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
