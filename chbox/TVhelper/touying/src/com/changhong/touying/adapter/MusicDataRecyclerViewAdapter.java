package com.changhong.touying.adapter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.common.system.MyApplication;
import com.changhong.touying.R;
import com.changhong.touying.activity.MusicViewActivity;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicProvider;
import com.changhong.touying.music.SetDefaultImage;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;

public class MusicDataRecyclerViewAdapter  extends
RecyclerView.Adapter<MusicDataRecyclerViewAdapter.ViewHolder>{

		private Context mContext;
		private static List<?> musics;
		private static List<String> musicList;
		private static Map<String, List<Music>> model;


		public  MusicDataRecyclerViewAdapter(Context context) {
			
			this.mContext = context;
			MusicProvider provider = new MusicProvider(context);
			musics = provider.getList();
			model = provider.getMapStructure(musics);
			musicList = provider.getMusicList(model);
		
		}
	
		@Override
		public MusicDataRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
				int viewType) {

			
			View view = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.music_special_childview, parent, false);
			return new ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(final MusicDataRecyclerViewAdapter.ViewHolder holder,
				 final int position) {
			final View view = holder.mView;
		
			
			String key = musicList.get(position);
			List<Music> list = model.get(key);
			Music music = (Music) list.get(0);

			holder.musicName.setText(music.getArtist());
			holder.musicNO.setText("共" + list.size() + "首");
			holder.fullPath.setText("");

			String musicImagePath = DiskCacheFileManager.isSmallImageExist(music
					.getPath());
			if (!musicImagePath.equals("")) {
				MyApplication.imageLoader.displayImage("file://" + musicImagePath,
						holder.musicImage, MyApplication.viewOptions);
				holder.musicImage.setScaleType(ImageView.ScaleType.FIT_XY);
			} else {
				SetDefaultImage.getInstance().startExecutor(holder.musicImage, music);
			}
			
	        
	        view.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	
						MyApplication.vibrator.vibrate(100);
						Intent intent = new Intent();
						intent.setClass(mContext, MusicViewActivity.class);
						Bundle bundle = new Bundle();
						List<Music> musics = model.get(musicList.get(position));
						bundle.putSerializable("musics", (Serializable) musics);
						intent.putExtras(bundle);
						mContext.startActivity(intent);
	            }
	        });

	    	
		}

		@Override
		public int getItemCount() {
			return musicList.size();
		}

		public static class ViewHolder extends RecyclerView.ViewHolder {

			private ImageView musicImage;
			private TextView musicName;
			private TextView musicNO;
			private TextView fullPath;
			public final View mView;
			

			public ViewHolder(View view) {

				super(view);
				mView = view;
				musicImage = (ImageView) view.findViewById(R.id.music_item_image);
				musicName = (TextView) view.findViewById(R.id.special_name);
				musicNO = (TextView) view.findViewById(R.id.music_NO);
				fullPath = (TextView) view.findViewById(R.id.music_item_path);

			}
		}

	}
