package com.changhong.touying.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.view.GestureDetector.OnGestureListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.*;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.touying.images.DragImageView;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.widgets.IpSelectorDataServer;
import com.changhong.common.system.AppConfig;
import com.changhong.touying.R;
import com.changhong.touying.nanohttpd.NanoHTTPDService;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.List;

/**
 * Created by Jack Wang
 */
public class PictureDetailsActivity extends Activity implements OnGestureListener {
    /**
     * 传过来所有的图片
     */
    private List<String> imagePaths;

    /**
     * 手势动作
     */
    private GestureDetector detector;
    private RelativeLayout relativeLayout;

    /**
     * 图片预览效果
     */
    private ViewFlipper flipper;


    /**
     * IP地址
     */
    public String ipAddress;

    /**
     * 当前是否投影
     */
    public static boolean currentShow = false;

    /**
     * 图片的总数
     */
    private int totalImageSize = 0;

    /**
     * 取消左右投影
     */
    private boolean isImageContinueShow = true;

    /**
     * 是否在没有连续滑动的操作模式下左右移动过
     */
    private boolean isImageContinueMove = false;

    /**
     * 当前选中图片的位置, 和需要显示的图片，其余的图片资源都需要回收
     */
    private int currentImagePosistion = 0;
    private int previousImagePosistion = 0;
    private int nextImagePosistion = 0;

    /*********************************************按钮部分*************************************************************/

    /**
     * 取消左右投影
     */
    private TextView imageContinueShow;

    /**
     * 向右旋转
     */
    private ImageView rotationLeft;

    /**
     * 向左旋转
     */
    private ImageView rotationRight;

    /**
     * 向左旋转的动画
     */
    private RotateAnimation animationLeft;

    /**
     * 向右旋转的动画
     */
    private RotateAnimation animationRight;

    /**
     * 当前旋转的位置, 默认为0度，分别为90,180,270,360, 注意，左滑、右滑、投影和取消投影都需要还原位置
     */
    private float currentRotationPosition = 0f;

    /*******************************************手势图片**************************************************************/

    private ImageView gestureUp;
    private ImageView gestureLeft;
    private ImageView gestureRight;
    private ImageView gestureScale;
    private TextView textUp;
    private TextView textLeft;
    private TextView textRight;
    private TextView textScale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initData();

        initEvent();
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_picture_details);

        detector = new GestureDetector(this);
        flipper = (ViewFlipper) findViewById(R.id.image_flipper);
        flipper.setDrawingCacheEnabled(false);

        imageContinueShow = (TextView) findViewById(R.id.pic_continue_show);
        imageContinueShow.setVisibility(View.GONE);
        relativeLayout = (RelativeLayout) findViewById(R.id.gesture_layout);

        rotationLeft = (ImageView) findViewById(R.id.rotation_left);
        rotationRight = (ImageView) findViewById(R.id.rotation_right);

        gestureUp = (ImageView) findViewById(R.id.gesture_up);
        gestureLeft = (ImageView) findViewById(R.id.gesture_left);
        gestureRight = (ImageView) findViewById(R.id.gesture_right);
        gestureScale = (ImageView) findViewById(R.id.gesture_scale);
        textUp = (TextView) findViewById(R.id.text_up);
        textLeft = (TextView) findViewById(R.id.text_left);
        textRight = (TextView) findViewById(R.id.text_right);
        textScale = (TextView) findViewById(R.id.text_scale);

        currentShow = false;
    }

    private void initData() {
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        imagePaths = getIntent().getStringArrayListExtra("imagePaths");

        /**
         * 设置图片需要显示的位置
         */
        totalImageSize = imagePaths.size();
        currentImagePosistion = position;
        caculateImageShowPosition();

        /**
         * 显示所有的图片
         */
        for (String imagePath : imagePaths) {
            flipper.addView(getImageView(imagePath));
        }
        flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_show_in));
        flipper.setDisplayedChild(currentImagePosistion);

        showFlipperImages();

        /**
         * 显示手势图片
         */
        AlphaAnimation fadeImage = new AlphaAnimation(0f, 1f);
        fadeImage.setDuration(2000);
        gestureUp.startAnimation(fadeImage);
        gestureLeft.startAnimation(fadeImage);
        gestureRight.startAnimation(fadeImage);
        gestureScale.startAnimation(fadeImage);
        textUp.startAnimation(fadeImage);
        textLeft.startAnimation(fadeImage);
        textRight.startAnimation(fadeImage);
        textScale.startAnimation(fadeImage);
    }

    private void initEvent() {
        rotationLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);

                if (currentShow && !isImageContinueMove) {
                    ClientSendCommandService.msg = "rotation:left";
                    ClientSendCommandService.handler.sendEmptyMessage(4);
                }

                if (currentRotationPosition == 0f) {
                    currentRotationPosition = 360f;
                }
                animationLeft = new RotateAnimation(currentRotationPosition, currentRotationPosition - 90f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animationLeft.setDuration(600);
                animationLeft.setFillAfter(true);
                currentRotationPosition = currentRotationPosition - 90f;

                View view = flipper.getChildAt(currentImagePosistion);
                view.startAnimation(animationLeft);
            }
        });

        rotationRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);

                if (currentShow && !isImageContinueMove) {
                    ClientSendCommandService.msg = "rotation:right";
                    ClientSendCommandService.handler.sendEmptyMessage(4);
                }

                if (currentRotationPosition == 360f) {
                    currentRotationPosition = 0f;
                }
                animationRight = new RotateAnimation(currentRotationPosition, currentRotationPosition + 90f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animationRight.setDuration(600);
                animationRight.setFillAfter(true);
                currentRotationPosition = currentRotationPosition + 90f;

                View view = flipper.getChildAt(currentImagePosistion);
                view.startAnimation(animationRight);
            }
        });

        imageContinueShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                currentRotationPosition = 0f;

                currentShow = false;
                isImageContinueShow = false;
                imageContinueShow.setVisibility(View.GONE);

                flipper.setInAnimation(AnimationUtils.loadAnimation(PictureDetailsActivity.this, R.anim.push_bottom_in));
                flipper.setOutAnimation(AnimationUtils.loadAnimation(PictureDetailsActivity.this, R.anim.push_bottom_out));
                flipper.setDisplayedChild(currentImagePosistion);
            }
        });
    }

    /**
     * **************************************************图片处理部分************************************************
     */

    private View getImageView(String imagePath) {
        DragImageView view = new DragImageView(this, this);
        view.setImageBitmap(null);
        view.setImageDrawable(null);
        view.setDrawingCacheEnabled(false);

        /**
         * 这个是居中显示
         FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(display.getWidth() - 50, 400);
         params.leftMargin = 25;
         params.topMargin = display.getHeight() / 2 - 200;
         view.setLayoutParams(params);
         view.setAdjustViewBounds(true);
         view.setScaleType(ImageView.ScaleType.CENTER_CROP);
         */
        return view;
    }

    private void caculateImageShowPosition() {
        if (currentImagePosistion == 0) {
            previousImagePosistion = totalImageSize - 1;
        } else if (currentImagePosistion == 1) {
            previousImagePosistion = 0;
        } else {
            previousImagePosistion = currentImagePosistion - 1;
        }

        if (currentImagePosistion == totalImageSize - 1) {
            nextImagePosistion = 0;
        } else if (currentImagePosistion == totalImageSize - 2) {
            nextImagePosistion = totalImageSize - 1;
        } else {
            nextImagePosistion = currentImagePosistion + 1;
        }
    }

    /**
     * 回收已经添加的图片资源
     */
    public void showFlipperImages() {
        int childCount = flipper.getChildCount();

        for (int i = 0; i < childCount; i++) {
            DragImageView imageView = (DragImageView) flipper.getChildAt(i);
            Drawable drawable = imageView.getDrawable();

            if (i == currentImagePosistion || i == previousImagePosistion || i == nextImagePosistion) {
                if (drawable == null) {
                    displayImage(imageView, imagePaths.get(i));
                }
            } else {
                if (drawable != null) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                    Bitmap bitmap = bitmapDrawable.getBitmap();

                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                    }

                    imageView.setImageDrawable(null);
                }
            }
        }
        System.gc();
    }

    public void cleanAllFlipperImages() {
        int childCount = flipper.getChildCount();

        for (int i = 0; i < childCount; i++) {
            DragImageView imageView = (DragImageView) flipper.getChildAt(i);
            Drawable drawable = imageView.getDrawable();

            if (drawable != null) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();

                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                    bitmap = null;
                }

                imageView.setImageDrawable(null);
            }
        }
        System.gc();
    }


    /**
     * ***********************************************滑动效果部分***************************************************
     */

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        /**
         * 首先判断是单点还是多点，如果是多点，不考虑
         */
        DragImageView imageView = (DragImageView) flipper.getChildAt(currentImagePosistion);
        if (e1.getPointerCount() > 1 && imageView.mode.equals(DragImageView.MODE.ZOOM)) {
            return true;
        }

        /**
         * 如果执行了横向操作，就没必要再执行纵向操作了
         */
        boolean actionDone = false;

        /**
         * 纵向操作部分
         */
        if (e1.getY() - e2.getY() > 350) {
            /**
             * 竖上滑动, 投影
             */
            if ( StringUtils.hasLength(IpSelectorDataServer.getInstance().getCurrentIp())) {
                isImageContinueShow = true;
                isImageContinueMove = false;
                imageContinueShow.setVisibility(View.VISIBLE);

                /**
                 * 如果是上滑操作已经执行，不必在相应
                 */
                if (!currentShow) {
                    currentRotationPosition = 0f;
                    touYing(imagePaths.get(currentImagePosistion));
                    flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_top_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_top_out));
                    flipper.setDisplayedChild(currentImagePosistion);
                }

                //这里不在客户端自己设置，通过盒子回传信息设置播放是否成功
                currentShow = true;
            } else {
                Toast.makeText(PictureDetailsActivity.this, "手机未连接电视，请确认后再投影", Toast.LENGTH_SHORT).show();
            }
            actionDone = true;
        }
        /**
         * 竖下滑动, 取消连续投影, 为什么向上设置的是350，而向下设置的400，因为用户的习惯是右滑的动作，往往是右斜下
         else if(e2.getY() - e1.getY() > 400) {
         vibrator.vibrate(100);
         currentShow = false;
         isImageContinueShow = false;
         imageContinueShow.setVisibility(View.GONE);

         flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.drawable.push_bottom_in));
         flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.drawable.push_bottom_out));
         flipper.setDisplayedChild(currentImagePosistion);

         actionDone = true;
         }
         */

        /**
         * 如果执行了操作了，后面的操作就不执行了
         */
        if (actionDone) {
            return true;
        }

        /**
         * 横向滑动, 如果是投影状态，需要持续推图片数据到盒子，如果不是投影状态就不管，只滑动
         */
        if (e1.getX() - e2.getX() > 300) {
            /**
             * 向右滑动
             */
            flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
            flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
            flipper.showNext();

            /**
             * 设置当前的图片位置,如果显示的话就显示出来
             */
            currentImagePosistion = flipper.getDisplayedChild();
            if (currentShow && isImageContinueShow) {
                touYing(imagePaths.get(currentImagePosistion));
            }
            if (!isImageContinueShow) {
                isImageContinueMove = true;
            }

            /**
             * 计算需要显示的资源和回收的资源
             */
            caculateImageShowPosition();
            showFlipperImages();

            /**
             * 还原当前的旋转位置
             */
            currentRotationPosition = 0f;
            return true;
        } else if (e2.getX() - e1.getX() > 300) {
            /**
             * 向左滑动
             */
            flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
            flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
            flipper.showPrevious();

            /**
             * 设置当前的图片位置,如果显示的话就显示出来
             */
            currentImagePosistion = flipper.getDisplayedChild();
            if (currentShow && isImageContinueShow) {
                touYing(imagePaths.get(currentImagePosistion));
            }
            if (!isImageContinueShow) {
                isImageContinueMove = true;
            }

            /**
             * 计算需要显示的资源和回收的资源
             */
            caculateImageShowPosition();
            showFlipperImages();

            /**
             * 还原当前的旋转位置
             */
            currentRotationPosition = 0f;
            return true;
        }

        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    private void displayImage(ImageView imageView, String path) {
        MyApplication.imageLoader.displayImage("file://" + path, imageView, MyApplication.detailsOptions,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(final String imageUri, final View view, final Bitmap loadedImage) {
                        ((DragImageView) view).setDragImageBitmap(loadedImage.getWidth(), loadedImage.getHeight());
                    }

                }, null
        );
    }

    /**
     * **********************************************手势部分, 图片缩放************************************************
     */

    public boolean onTouchEvent(MotionEvent event) {
        boolean touch = detector.onTouchEvent(event);

        DragImageView imageView = (DragImageView) flipper.getChildAt(currentImagePosistion);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                relativeLayout.setVisibility(View.INVISIBLE);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (currentShow && !isImageContinueMove && event.getPointerCount() == 2) {
                    float x = event.getX(0) - event.getX(1);
                    float y = event.getY(0) - event.getY(1);
                    ClientSendCommandService.msg = "room_pointer_down:" + event.getPointerCount() + ":" + x + ":" + y;
                    ClientSendCommandService.handler.sendEmptyMessage(4);
                }
                imageView.onPointerDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentShow && !isImageContinueMove && event.getPointerCount() == 2) {
                    float x = event.getX(0) - event.getX(1);
                    float y = event.getY(0) - event.getY(1);
                    ClientSendCommandService.msg = "room_action_move:" + x + ":" + y;
                    ClientSendCommandService.handler.sendEmptyMessage(4);
                }
                imageView.onTouchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                if (currentShow && !isImageContinueMove && event.getPointerCount() == 2) {
                    ClientSendCommandService.msg = "room_action_up:";
                    ClientSendCommandService.handler.sendEmptyMessage(4);
                }
                imageView.mode = DragImageView.MODE.NONE;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (currentShow && !isImageContinueMove && event.getPointerCount() == 2) {
                    ClientSendCommandService.msg = "room_pointer_up:";
                    ClientSendCommandService.handler.sendEmptyMessage(4);
                }
                imageView.mode = DragImageView.MODE.NONE;
                break;
        }
        return true;
    }

    /**
     * **********************************************投影部分*********************************************************
     */

    private void touYing(String imagePath) {
        /**
         * first check the wifi is connected
         */
        if (!NetworkUtils.isWifiConnected(PictureDetailsActivity.this)) {
            Toast.makeText(PictureDetailsActivity.this, "请链接无线网络", Toast.LENGTH_SHORT).show();
            return;
        }

        /**
         * second check the mobile is connect to box
         */
        if (!StringUtils.hasLength(IpSelectorDataServer.getInstance().getCurrentIp())) {
            Toast.makeText(PictureDetailsActivity.this, "手机未连接机顶盒，请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }

        /**
         * fourth begin to tou ying
         */
        try {
            MyApplication.vibrator.vibrate(100);

            //获取IP和外部存储路径
            ipAddress = NetworkUtils.getLocalHostIp();
            String httpAddress = "http://" + ipAddress + ":" + NanoHTTPDService.HTTP_PORT;

            //生成访问图片的HTTP URL
            String newImagePath = null;
            if (imagePath.startsWith(NanoHTTPDService.defaultHttpServerPath)) {
                newImagePath = imagePath.replace(NanoHTTPDService.defaultHttpServerPath, "").replace(" ", "%20");
            } else {
                for (String otherHttpServerPath : NanoHTTPDService.otherHttpServerPaths) {
                    if (imagePath.startsWith(otherHttpServerPath)) {
                        newImagePath = imagePath.replace(otherHttpServerPath, "").replace(" ", "%20");
                    }
                }
            }

            //生成访问缩略图的HTTP URL
            String tmpSmallImagePath = DiskCacheFileManager.isSmallImageExist(imagePath);
            if (StringUtils.hasLength(tmpSmallImagePath)) {
                if (tmpSmallImagePath.startsWith(NanoHTTPDService.defaultHttpServerPath)) {
                    tmpSmallImagePath = tmpSmallImagePath.replace(NanoHTTPDService.defaultHttpServerPath, "").replace(" ", "%20");
                } else {
                    for (String otherHttpServerPath : NanoHTTPDService.otherHttpServerPaths) {
                        if (tmpSmallImagePath.startsWith(otherHttpServerPath)) {
                            tmpSmallImagePath = tmpSmallImagePath.replace(otherHttpServerPath, "").replace(" ", "%20");
                        }
                    }
                }
            }

            String tmpHttpAddress = httpAddress + newImagePath;
            tmpSmallImagePath = httpAddress + tmpSmallImagePath;

            //判断URL是否符合规范，如果不符合规范，就1重命名文件
            try {
                URI.create(tmpHttpAddress);
            } catch (Exception e) {
                try {
                    /**
                     * 创建信的文件
                     */
                    File illegeFile = new File(imagePath);
                    String fullpath = illegeFile.getAbsolutePath();
                    String filename = illegeFile.getName();
                    String filepath = fullpath.replace(File.separator + filename, "");
                    String[] tokens = StringUtils.delimitedListToStringArray(filename, ".");
                    String filenameSuffix = tokens[tokens.length - 1];

                    String newFile = filepath + File.separator + StringUtils.getRandomString(15) + "." + filenameSuffix;
                    Runtime.getRuntime().exec("mv " + fullpath + " " + newFile);

                    /**
                     * 更改内存中的文件
                     */
                    int index = imagePaths.indexOf(fullpath);
                    imagePaths.remove(index);
                    imagePaths.add(index, newFile);
                    String[] directories = StringUtils.delimitedListToStringArray(imagePath, File.separator);
                    String packageName = directories[directories.length - 2];
                    PictureCategoryActivity.packageList.put(packageName, imagePaths);

                    if (imagePath.startsWith(NanoHTTPDService.defaultHttpServerPath)) {
                        newImagePath = imagePath.replace(NanoHTTPDService.defaultHttpServerPath, "").replace(" ", "%20");
                    } else {
                        for (String otherHttpServerPath : NanoHTTPDService.otherHttpServerPaths) {
                            if (imagePath.startsWith(otherHttpServerPath)) {
                                newImagePath = imagePath.replace(otherHttpServerPath, "").replace(" ", "%20");
                            }
                        }
                    }
                    tmpHttpAddress = httpAddress + newImagePath;

                    /**
                     * 更改Content Provider的文件
                     */
                    ContentResolver mContentResolver = PictureDetailsActivity.this.getContentResolver();
                    Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, newFile);
                    mContentResolver.update(mImageUri, values, MediaStore.Images.Media.DATA + " = '" + fullpath + "'", null);
                } catch (Exception e1) {
                    e.printStackTrace();
                    Toast.makeText(PictureDetailsActivity.this, "对不起，图片获取有误，不能正常投影！", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            //获取图片间事件间隔
            JSONObject o = new JSONObject();
            JSONArray array = new JSONArray();
            //image urls
            array.put(0, tmpHttpAddress);
            File imageFile = new File(imagePath);
            if (imageFile.length() >= AppConfig.PICTURE_SMALL_TOUYING_MIN_SIZE) {
                array.put(1, tmpSmallImagePath);
            } else {
                array.put(1, "");
            }
            o.put("urls", array);

            //client ip
            o.put("client_ip", ipAddress);

            /**
             * 有时候用户在进入投影页面，但是确没有投影动作，http服务关闭，但是用户现在点击投影，所以这里需要先检查有没有HTTP服务
             */
            if (NanoHTTPDService.httpServer == null) {
                Intent http = new Intent(PictureDetailsActivity.this, NanoHTTPDService.class);
                startService(http);
                //Sleep 1s is used for let http service started fully
                SystemClock.sleep(1000);
            }

            //发送播放地址
            ClientSendCommandService.msg = o.toString();
            ClientSendCommandService.handler.sendEmptyMessage(4);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PictureDetailsActivity.this, "图片获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * **********************************************系统重载********************************************************
     */

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanAllFlipperImages();
    }
}
