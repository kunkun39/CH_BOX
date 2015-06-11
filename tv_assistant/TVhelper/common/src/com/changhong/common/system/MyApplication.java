package com.changhong.common.system;

import java.io.File;

import android.app.Application;
import android.app.Service;
import android.graphics.Bitmap;
import android.os.Vibrator;

import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.changhong.common.R;
import com.changhong.common.db.sqlite.DatabaseContainer;
import com.changhong.common.utils.PathGenerateUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * Created by Jack Wang
 */
public class MyApplication extends Application {

    public static DatabaseContainer databaseContainer;

    /**
     * vibrator for every action button
     */
    public static Vibrator vibrator = null;

    /**
     * pre load image loader, can't just let imageLoader load pre image, because maybe end user try to see picture, but
     * at this moment, the imageLoader hasn't finish pre image load
     */
    public static ImageLoader preImageLoader = ImageLoader.getInstance();

    /**
     * image loader for user action
     */
    public static ImageLoader imageLoader = ImageLoader.getInstance();

    /**
     * image load options for zoom in pictures
     */
    public static DisplayImageOptions viewOptions;

    /**
     * image load options for picture details
     */
    public static DisplayImageOptions detailsOptions;
    
    /**
     * image load options for zoom in pictures
     */
    public static DisplayImageOptions musicPicOptions;

    /**
     * small pictures cache file path
     */
    public static File smallImageCachePath;

    /**
     * epg db cache path
     */
    public static MyApplication instance;
    public static File epgDBCachePath;
    /**
     * 
     * baidu location
     * @return
     */
    public static LocationClient mLocationClient;//百度定位客户端
    public GeofenceClient mGeofenceClient;
    public static MyLocationListener mMyLocationListener;//监听器
    private LocationMode tempMode = LocationMode.Hight_Accuracy;//高精度模式
	private String tempcoor="bd09ll";//百度加密经纬度坐标
    private int span=1000;//定位时间间隔

    public static MyApplication getContext(){  
        return instance;  
    }  
    
    @Override
    public void onCreate() {
        super.onCreate();
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        instance=this;
        viewOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_stub)
                .showImageOnFail(R.drawable.ic_stub)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .smallImageGenerate(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(800))
                .build();

        detailsOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_stub)
                .showImageOnFail(R.drawable.ic_stub)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        
        musicPicOptions = new DisplayImageOptions.Builder()
		        .showImageForEmptyUri(R.drawable.ic_stub)
		        .showImageOnFail(R.drawable.music_bg12)
		        .cacheInMemory(false)
		        .cacheOnDisk(true)
		        .smallImageGenerate(true)
		        .considerExifParams(true)
		        .imageScaleType(ImageScaleType.EXACTLY)
		        .bitmapConfig(Bitmap.Config.RGB_565)
		        .displayer(new FadeInBitmapDisplayer(800))
		        .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .build();

        imageLoader.init(config);

        preImageLoader.init(config);

        /**
         * 设置缩略图路径
         */
        smallImageCachePath = StorageUtils.getCacheDirectory(this);
        epgDBCachePath = PathGenerateUtils.getEPGDirectory(this);
        
        /**
         * 
         * 百度定位
         */
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        mGeofenceClient = new GeofenceClient(getApplicationContext());
        InitLocation();
    }
    
    private void InitLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);//设置定位模式
        option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(span);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }
}
