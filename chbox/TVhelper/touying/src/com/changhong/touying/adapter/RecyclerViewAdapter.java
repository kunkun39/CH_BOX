package com.changhong.touying.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.DateUtils;
import com.changhong.touying.R;
import com.changhong.touying.activity.MusicDetailsActivity;
import com.changhong.touying.dialog.MusicPlayer;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.SetDefaultImage;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;

public class RecyclerViewAdapter extends
		RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

	private Context mContext;
	private List<Music> musics;
	private Context context;
	private MusicPlayer player;


	public RecyclerViewAdapter(Context context, List<Music> musics,
			MusicPlayer player) {
		this.mContext = context;
		this.musics = musics;
		this.context = context;
		this.player = player;
	}



	@Override
	public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {

		View view = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.music_list_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder,
			 final int position) {
		final View view = holder.mView;

		final Music music = musics.get(position);

		holder.musicName.setText(music.getTitle());
		holder.artist.setText(music.getArtist() + "  ["
				+ DateUtils.getTimeShow(music.getDuration() / 1000) + "]");

		holder.fullPath.setText(music.getPath());

		String musicImagePath = DiskCacheFileManager.isSmallImageExist(music
				.getPath());
		if (!musicImagePath.equals("")) {
			MyApplication.imageLoader.displayImage("file://" + musicImagePath,
					holder.defaultImage, MyApplication.musicPicOptions);
			holder.defaultImage.setScaleType(ImageView.ScaleType.FIT_XY);
		} else {
			SetDefaultImage.getInstance().startExecutor(holder.defaultImage,
					music);
		}

		holder.playBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				playMusics(musics, music);
			}
		});
        
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	MyApplication.vibrator.vibrate(100);
        		Intent intent = new Intent();
        		Music music = musics.get(position);
        		Bundle bundle = new Bundle();
        		bundle.putSerializable("selectedMusic", music);
        		intent.putExtras(bundle);
        		intent.setClass(mContext,MusicDetailsActivity.class);
        		mContext.startActivity(intent);
            }
        });

    	
	}

	@Override
	public int getItemCount() {
		return musics.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		TextView musicName = null;

		TextView fullPath = null;
		TextView artist = null;
		ImageView playBtn = null;
		ImageView defaultImage = null;

		public final View mView;

		public ViewHolder(View view) {

			super(view);

			mView = view;

			musicName = (TextView) view.findViewById(R.id.music_item_name);
			fullPath = (TextView) view.findViewById(R.id.music_item_path);
			artist = (TextView) view
					.findViewById(R.id.music_item_artist_duration);
			playBtn = (ImageView) view.findViewById(R.id.music_list_play);
			defaultImage = (ImageView) view.findViewById(R.id.music_list_image);

		}
	}

	private void playMusics(List<Music> musics, Music music) {
		((FragmentActivity) context).getSupportFragmentManager()
				.beginTransaction().show(player).commitAllowingStateLoss();
		if (music != null) {
			player.playMusics(music);
		} else {
			player.autoPlaying(true);
		}
	}
}
