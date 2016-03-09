package com.changhong.touying.tab;

import java.util.List;
import java.util.Map;

import com.changhong.touying.R;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicPlayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MusicPlayListFragment extends ListFragment {
	private ListView list;
	private SimpleAdapter adapter;
	private List<Map<String, Object>> playList;
	private Activity ma;

	private static final String BTN_SELECTED = "SELECTED";
	private static final String TEXT_NAME = "NAME";
	private static final String TEXT_COMMENT = "COMMENT";

	private MusicPlayList listMusicPlayList;
	private List<Map<String, Object>> mPlayList;
	private List<Music> listMusics;
	private View view;

	public MusicPlayListFragment(List<Map<String, Object>> inPlayList, Activity out, MusicPlayList mMusicPlayList,
								 List<Music> mMuscis) {
		playList = inPlayList;
		ma = out;
		listMusicPlayList = mMusicPlayList;
		listMusics = mMuscis;
	}
	
	/**
	 * @描述 在onCreateView中加载布局
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.music_fragment_item, container, false);
		list = (ListView) view.findViewById(android.R.id.list);

		return view;
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		adapter = new SimpleAdapter(ma, playList, R.layout.playlist_item,
				new String[] { BTN_SELECTED, TEXT_NAME, TEXT_COMMENT },
				new int[] { R.id.playlist_radio_btn, R.id.playlist_item_name, R.id.playlist_item_comment });
		setListAdapter(adapter);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		list.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				// TODO 自动生成的方法存根
				Map<String, Object> checkBox = playList.get(arg2);
				boolean checked = (Boolean) playList.get(arg2).get(BTN_SELECTED);
				checkBox.put(BTN_SELECTED, !checked);
				onSelectedChanged(((TextView) arg1.findViewById(R.id.playlist_item_name)).getText().toString(),
						!checked);
				((SimpleAdapter) (arg0.getAdapter())).notifyDataSetChanged();

			}
		});
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	private void onSelectedChanged(String name, boolean isSelected) {
		Music music = null;

		String nameTrimed = name.trim();

		// 根据名字找歌
		try {
			for (Music m : listMusics) {
				if (m.getTitle().trim().equals(nameTrimed)) {
					music = m;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (music == null)
			return;

		// 在列表里面添加或者删除
		if (isSelected) {

			for (String song : listMusicPlayList.getPlayList()) {
				if (music.getPath().equals(song)) {
					return;
				}
			}
			listMusicPlayList.getPlayList().add(music.getPath());
		} else {
			listMusicPlayList.getPlayList().remove(music.getPath());
		}
	}

}
