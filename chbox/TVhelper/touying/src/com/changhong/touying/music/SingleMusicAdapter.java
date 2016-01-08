package com.changhong.touying.music;

import java.util.List;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.DateUtils;
import com.changhong.touying.R;
import com.changhong.touying.dialog.MusicPlayer;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;

public class SingleMusicAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Music> musics;
    private Context context;
    private MusicPlayer player;

    public SingleMusicAdapter(Context context,List<Music> musics,MusicPlayer player) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.musics=musics;
        this.context=context;
        this.player=player;
    }

    public int getCount() {
        return musics.size();
    }

    public Object getItem(int item) {
        return item;
    }

    public long getItemId(int id) {
        return id;
    }

 // 创建View方法
 		public View getView(int position, View convertView, ViewGroup parent) {

 			TextView musicName = null;

 			TextView fullPath = null;
 			TextView artist = null;
 			ImageView playBtn = null;
 			ImageView defaultImage = null;
 			DataWapper wapper = null;

 			if (convertView == null) {
 				// 获得view
 				convertView = inflater.inflate(R.layout.music_list_item, null);
 				musicName = (TextView) convertView
 						.findViewById(R.id.music_item_name);
 				fullPath = (TextView) convertView
 						.findViewById(R.id.music_item_path);
 				artist = (TextView) convertView
 						.findViewById(R.id.music_item_artist_duration);
 				playBtn = (ImageView) convertView
 						.findViewById(R.id.music_list_play);
 				defaultImage = (ImageView) convertView
 						.findViewById(R.id.music_list_image);

 				// 组装view
 				wapper = new DataWapper(musicName, fullPath, artist, playBtn,
 						defaultImage);
 				convertView.setTag(wapper);
 			} else {
 				wapper = (DataWapper) convertView.getTag();
 				musicName = wapper.musicName;
 				fullPath = wapper.fullPath;
 				playBtn = wapper.playBtn;
 				artist = wapper.artist;
 				defaultImage = wapper.defaultImage;
 			}

 			final Music music = musics.get(position);
// 			Log.i("mmmm", "SingleMusicAdapter=music=" + music);
 			musicName.setText(music.getTitle());
 			artist.setText(music.getArtist() + "  ["
 					+ DateUtils.getTimeShow(music.getDuration() / 1000) + "]");

 			fullPath.setText(music.getPath());

 			String musicImagePath = DiskCacheFileManager
 					.isSmallImageExist(music.getPath());
 			if (!musicImagePath.equals("")) {
 				MyApplication.imageLoader.displayImage("file://"
 						+ musicImagePath, wapper.defaultImage,
 						MyApplication.musicPicOptions);
 				wapper.defaultImage.setScaleType(ImageView.ScaleType.FIT_XY);
 			} else {
 				SetDefaultImage.getInstance()
 						.startExecutor(defaultImage, music);
 			}

 			playBtn.setOnClickListener(new OnClickListener() {

 				@Override
 				public void onClick(View v) {
 					playMusics(musics, music);
 				}
 			});

 			return convertView;
 		}

 		private final class DataWapper {

 			// 音乐的名字
 			public TextView musicName;

 			// 视屏的全路径
 			public TextView fullPath;

 			public ImageView playBtn;
 			public ImageView defaultImage;
 			public TextView artist;

 			private DataWapper(TextView musicName, TextView fullPath,
 					TextView artist, ImageView playBtn, ImageView defaultImage) {
 				this.musicName = musicName;
 				this.fullPath = fullPath;
 				this.playBtn = playBtn;
 				this.artist = artist;
 				this.defaultImage = defaultImage;
 			}
 		}
    
    
    private void playMusics(List<Music> musics, Music music) {
		if (music != null) {
			player.playMusics(music);
		} else {
			player.autoPlaying(true);
		}
	}
}


