package com.changhong.common.system;

import java.io.File;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Vibrator;

import com.changhong.common.R;
import com.changhong.common.db.sqlite.DatabaseContainer;
import com.changhong.common.utils.PathGenerateUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by Jack Wang
 */
public class MyApplication extends Application {

    //private static DatabaseContainer databaseContainer = null;

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

    public static MyApplication getContext(){  
        return instance;  
    }  
    
    @Override
    public void onCreate() {
        super.onCreate();
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        instance = this;
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
    }

    public static DatabaseContainer getDatabaseContainer(Context context) {
        return DatabaseContainer.getInstance(context);
    }
}
