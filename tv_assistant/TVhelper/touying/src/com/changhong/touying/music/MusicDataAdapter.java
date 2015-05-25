package com.changhong.touying.music;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.common.system.MyApplication;
import com.changhong.touying.R;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;

import java.util.List;
import java.util.Map;

/**
 * Created by Jack Wang
 */
public class MusicDataAdapter extends BaseAdapter {

	private Context context;

	private LayoutInflater inflater;

	private static List<?> musics;

	private static List<String> musicList;

	private static Map<String, List<Music>> model;

	public MusicDataAdapter(Context context) {
		this.context = context;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		MusicProvider provider = new MusicProvider(context);
		musics = provider.getList();
		model = provider.getMapStructure(musics);
		musicList = provider.getMusicList(model);
		Log.i("mmmm", "MusicDataAdapter=musics=" + musics + "     model=" + model
				+ "    musicList=" + musicList);
	}

	public int getCount() {
		return musicList.size();
	}

	public Object getItem(int item) {
		return item;
	}

	public long getItemId(int id) {
		return id;
	}

	// 创建View方法
	public View getView(int position, View convertView, ViewGroup parent) {

		ImageView musicImage = null;

		TextView musicName = null;

		TextView musicNO = null;

		TextView fullPath = null;

		DataWapper wapper = null;

		if (convertView == null) {
			// 获得view
			convertView = inflater.inflate(R.layout.music_special_childview,
					null);
			musicImage = (ImageView) convertView
					.findViewById(R.id.music_item_image);
			musicName = (TextView) convertView.findViewById(R.id.special_name);
			musicNO = (TextView) convertView.findViewById(R.id.music_NO);
			fullPath = (TextView) convertView
					.findViewById(R.id.music_item_path);

			// 组装view
			wapper = new DataWapper(musicImage, musicName, musicNO, fullPath);
			convertView.setTag(wapper);
		} else {
			wapper = (DataWapper) convertView.getTag();
			musicImage = wapper.getMusicImage();
			musicName = wapper.getMusicName();
			musicNO = wapper.getMusicNO();
			fullPath = wapper.getFullPath();
		}

		String key = musicList.get(position);
		List<Music> list = model.get(key);
		Music music = (Music) list.get(0);

		musicName.setText(music.getArtist());
		musicNO.setText("共" + list.size() + "首");
		fullPath.setText("");

		String musicImagePath = DiskCacheFileManager.isSmallImageExist(music
				.getPath());
		if (!musicImagePath.equals("")) {
			MyApplication.imageLoader.displayImage("file://" + musicImagePath,
					musicImage, MyApplication.viewOptions);
			musicImage.setScaleType(ImageView.ScaleType.FIT_XY);
		} else {
			SetDefaultImage.getInstance().startExecutor(musicImage, music);
		}


		return convertView;
	}

	

	private final class DataWapper {

		// mp3的图标
		private ImageView musicImage;

		// mp3的名字
		private TextView musicName;

		// mp3数量
		private TextView musicNO;

		// mp3的全路径
		private TextView fullPath;

		private DataWapper(ImageView musicImage, TextView musicName,
				TextView musicNO, TextView fullPath) {
			this.musicImage = musicImage;
			this.musicName = musicName;
			this.fullPath = fullPath;
			this.musicNO = musicNO;
		}

		public ImageView getMusicImage() {
			return musicImage;
		}

		public void setMusicImage(ImageView musicImage) {
			this.musicImage = musicImage;
		}

		public TextView getMusicName() {
			return musicName;
		}

		public void setMusicName(TextView musicName) {
			this.musicName = musicName;
		}

		public TextView getMusicNO() {
			return musicNO;
		}

		public void setMusicNO(TextView musicNO) {
			this.musicNO = musicNO;
		}

		public TextView getFullPath() {
			return fullPath;
		}

		public void setFullPath(TextView fullPath) {
			this.fullPath = fullPath;
		}
	}

	public static Music getPositionMusic(int position) {
		return model.get(musicList.get(position)).get(0);
	}

	public static List<Music> getPositionMusics(int position) {
		return model.get(musicList.get(position));
	}
}
