package com.changhong.touying.music;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class SetDefaultImage {

	public static SetDefaultImage setImageView;
	private ExecutorService executorService =Executors.newFixedThreadPool(5);
	private Context context;
	
	public static SetDefaultImage getInstance(){
		if(null==setImageView){
			setImageView=new SetDefaultImage();
		}
		return setImageView;
	}
	public void setContext(Context context){
		this.context=context;
	}
	
	public void startExecutor(final ImageView iv,final Music music){
		executorService.submit(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Bitmap bitmap = MediaUtil.getArtwork(context,
						music.getId(), music.getArtistId(),
						true, false);
				if (bitmap != null && iv != null) {
					iv.setImageBitmap(bitmap);
					iv.setScaleType(ImageView.ScaleType.FIT_XY);
				}
				DiskCacheFileManager.saveSmallImage(bitmap,
						music.getPath());
			}
		});
	}
}
