/**
 * 
 */
package com.changhong.touying.music;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.changhong.touying.R;
import com.changhong.touying.dialog.MusicPlayer;
import com.changhong.touying.dialog.MusicPlayer.OnPlayListener;

class ListItemHoder
{
	TextView mIndexText;
	TextView mPlayListName; 
	TextView mPlayListComment;	
}

public class PlayListAdatper extends BaseAdapter
{
	
	/**
	 * 播放列表
	 */	
	private List<MusicPlayList> musicPlayLists;
	
	/**
	 * 应用句柄
	 */
	private FragmentActivity activity;
		
	/**
	 * 音乐列表
	 */
	ArrayList<Music> musics;
	
	/**
	 * @param musicCategoryActivity
	 */
	public PlayListAdatper(FragmentActivity activity,List<MusicPlayList> musicPlayLists) {
		super();
		this.musicPlayLists = musicPlayLists;
		this.activity = activity;		
		
		MusicProvider provider = new MusicProvider(activity);
        musics = (ArrayList<Music>) provider.getList(); 
	}

	@Override
	public int getCount() {
		return musicPlayLists.size();
	}

	@Override
	public Object getItem(int position) {
		return musicPlayLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// 获取View
		ListItemHoder hoder;
		if (convertView == null) {
			hoder = new ListItemHoder();
			
			convertView = activity.getLayoutInflater().inflate(R.layout.playlist_list_item_pan, null);
			hoder.mIndexText = (TextView) convertView.findViewById(R.id.playlist_listitem_index);
			hoder.mPlayListName = (TextView) convertView.findViewById(R.id.playlist_listitem_name);
			hoder.mPlayListComment = (TextView) convertView.findViewById(R.id.playlist_listitem_comment);			
			convertView.setTag(hoder);
		}
		
		hoder = (ListItemHoder) convertView.getTag();
		MusicPlayList item = (MusicPlayList)getItem(position);
		
		if (item == null) {
			return null;
		}
		
		hoder.mIndexText.setText(String.valueOf(position + 1));		
		hoder.mPlayListName.setText(item.getName());
		hoder.mPlayListComment.setText(item.getComment());		

		return convertView;
	}
	

	
}