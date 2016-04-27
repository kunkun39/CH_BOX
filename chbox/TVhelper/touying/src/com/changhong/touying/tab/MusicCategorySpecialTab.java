/**
 * 
 */
package com.changhong.touying.tab;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.changhong.common.system.MyApplication;
import com.changhong.touying.R;
import com.changhong.touying.activity.MusicViewActivity;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicDataAdapter;
import com.changhong.touying.service.MusicService;
import com.changhong.touying.service.MusicServiceImpl;

/**
 * @author yves.yang
 *
 */
public class MusicCategorySpecialTab extends Fragment {

	public static final String TAG = "MusicCategorySpecialTab";
	/************************************************** 歌曲部分 *******************************************************/

	/**
	 * Image List adapter
	 */
	private MusicDataAdapter musicAdapter = null;
	
	private ArrayList<Music> musicsPlay;
	/**
	 * 视频浏览部分
	 */
	private ListView musicGridView = null;

	private View v = null;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		/**
		 * 启动歌词扫描服务
		 */
		MusicService musicService = new MusicServiceImpl(getActivity());
		musicService.findAllMusicLrc();

		// TODO 自动生成的方法存根
		View v = inflater.inflate(R.layout.music_special_listview, container,
				false);
		initView(v);
		initEvent();
		
		return v;
	}


	private void initView(View v) {
		/**
		 * 歌曲部分
		 */
		musicGridView = (ListView) v.findViewById(R.id.music_special_listview);
		musicAdapter = new MusicDataAdapter(getActivity());
		musicGridView.setAdapter(musicAdapter);
	}

	private void initEvent() {

		/**
		 * 歌曲部分
		 */
		musicGridView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						MyApplication.vibrator.vibrate(100);
						Intent intent = new Intent();
						intent.setClass(getActivity(), MusicViewActivity.class);
						Bundle bundle = new Bundle();
						List<Music> musics = MusicDataAdapter
								.getPositionMusics(position);
						
						//musics序列化,便于在activity之间传递数据
			      		musicsPlay = new ArrayList<Music>();  		
			      		for (Music music : musics) {
			      			musicsPlay.add(music);
			      		}
			      		
						bundle.putSerializable("musics", musicsPlay);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});

	}
}
