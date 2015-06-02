package com.changhong.touying.music;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.changhong.common.system.MyApplication;
import com.changhong.touying.R;
import com.changhong.touying.activity.MusicCategoryActivity;
import com.changhong.touying.tab.MusicCategoryAllTab;
import com.changhong.touying.tab.MusicCategoryPlaylistTab;
import com.changhong.touying.tab.MusicCategorySpecialTab;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
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
				final Bitmap bitmap = MediaUtil.getArtwork(context,
						music.getId(), music.getArtistId(),
						true, false);
				
				if (bitmap != null && iv != null) {
//					Activity activity=(Activity)iv.getContext();
//					activity.runOnUiThread(new UpdateUI(iv, bitmap));
					
					iv.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							iv.setImageBitmap(bitmap);
						}
					});
				}
				DiskCacheFileManager.saveSmallImage(bitmap,
						music.getPath());
				
				
				
			}
		});
	}
	
	private class UpdateUI extends Thread{
		private ImageView iv;
		private Bitmap bitmap;
		public UpdateUI(ImageView iv,Bitmap bitmap){
			this.iv=iv;
			this.bitmap=bitmap;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			iv.setImageBitmap(bitmap);
			
		}
		
	}
	
}
