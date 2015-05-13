package com.changhong.yinxiang.music;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.common.system.MyApplication;
import com.changhong.yinxiang.R;
import com.changhong.yinxiang.activity.YinXiangMusicViewActivity;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;

/**
 * Created by Administrator on 15-5-11.
 */
public class YinXiangMusicAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private List<YinXiangMusic> musicsAll;
	private List<YinXiangMusic> musicsAct = new ArrayList<YinXiangMusic>();

	public static List<String> selectMusicPaths = new ArrayList<String>();
	private Context context;
	private String keyStr;

	public YinXiangMusicAdapter(Context context, String keyWords) {
		this.context = context;
		this.keyStr = keyWords;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		YinXiangMusicProvider provider = new YinXiangMusicProvider(context);
		musicsAll = provider.getList();
		musicFilter();
		selectMusicPaths.clear();
	}

	public int getCount() {
		return musicsAct.size();
	}

	public Object getItem(int item) {
		return item;
	}

	public long getItemId(int id) {
		return id;
	}

	// 创建View方法
	public View getView(int position, View convertView, ViewGroup parent) {
		DataWapper wapper = null;

		if (convertView == null) {
			wapper = new DataWapper();
			// 获得view
			convertView = inflater.inflate(R.layout.yinixiang_vedio_list_item,
					null);
			wapper.musicImage = (ImageView) convertView
					.findViewById(R.id.yinxiang_vedio_item_image);
			wapper.musicName = (TextView) convertView
					.findViewById(R.id.yinxiang_vedio_item_name);
			wapper.fullPath = (TextView) convertView
					.findViewById(R.id.yinxiang_vedio_item_path);
			wapper.musicChecked = (CheckBox) convertView
					.findViewById(R.id.yinxiang_vedio_item_checked);

			// 组装view
			convertView.setTag(wapper);
		} else {
			wapper = (DataWapper) convertView.getTag();
		}

		YinXiangMusic yinXiangMusic = (YinXiangMusic) musicsAct.get(position);

		String displayName = yinXiangMusic.getTitle();

		final String musicPath = yinXiangMusic.getPath();

		wapper.musicName.setText(displayName);
		wapper.fullPath.setText(musicPath);

		String musicImagePath = DiskCacheFileManager
				.isSmallImageExist(musicPath);
		if (!musicImagePath.equals("")) {
			MyApplication.imageLoader.displayImage("file://" + musicImagePath,
					wapper.musicImage, MyApplication.viewOptions);
			wapper.musicImage.setScaleType(ImageView.ScaleType.FIT_XY);
		} else {
			synchronizImageLoad(wapper.musicImage, yinXiangMusic);
		}
//		wapper.musicImage.setImageBitmap(YinXiangMediaUtil.getArtwork(context,
//				yinXiangMusic.getId(), yinXiangMusic.getAlbumId(), true, true));
		final boolean isChecked = selectMusicPaths.contains(musicPath);
		wapper.musicChecked.setChecked(isChecked);
		wapper.musicChecked
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							selectMusicPaths.add(musicPath);
						} else {
							selectMusicPaths.remove(musicPath);
						}
						YinXiangMusicViewActivity.musicSelectedInfo
								.setText("你共选择了" + selectMusicPaths.size()
										+ "首歌曲");
					}
				});
		return convertView;
	}

	private void musicFilter() {
		for (int i = 0; i < musicsAll.size(); i++) {
			YinXiangMusic music = musicsAll.get(i);
			if (TextUtils.isEmpty(keyStr)) {
				musicsAct = musicsAll;
				break;
			} else if (music.getTitle().contains(keyStr)) {
				musicsAct.add(music);
			}
		}
	}

	private void synchronizImageLoad(final ImageView imageView,
			final YinXiangMusic yinXiangMusic) {
		ImageAsyncTask task = new ImageAsyncTask(imageView);
		task.execute(yinXiangMusic);
	}

	private final class ImageAsyncTask extends
			AsyncTask<YinXiangMusic, Integer, Bitmap> {
		ImageView imageView;

		private ImageAsyncTask(ImageView imageView) {
			this.imageView = imageView;
		}

		@Override
		protected Bitmap doInBackground(YinXiangMusic... params) {
			Bitmap bitmap = null;
			try {
				YinXiangMusic yinXiangMusic = params[0];
				bitmap = YinXiangMediaUtil.getArtwork(context,
						yinXiangMusic.getId(), yinXiangMusic.getAlbumId(), true, true);
				DiskCacheFileManager.saveSmallImage(bitmap, yinXiangMusic.getPath());
				return bitmap;
			} catch (Exception e) {
				e.printStackTrace();
				return bitmap;
			}
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (bitmap != null && imageView != null) {
				imageView.setImageBitmap(bitmap);
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			}
		}
	}

	private final class DataWapper {

		// 音频的图标
		public ImageView musicImage;

		// 音频的名字
		public TextView musicName;

		// 音频是否被选中
		public CheckBox musicChecked;

		// 音屏的全路径
		public TextView fullPath;

	}
}
