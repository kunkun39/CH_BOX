/**
 * 
 */
package com.changhong.touying.tab;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.changhong.touying.R;
import com.changhong.touying.service.MusicService;
import com.changhong.touying.service.MusicServiceImpl;

import com.changhong.touying.adapter.MusicDataRecyclerViewAdapter;

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

	private RecyclerView mRecyclerView;

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView
				.getContext()));
		mRecyclerView.setAdapter(new MusicDataRecyclerViewAdapter(getActivity()));
		
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		/**
		 * 启动歌词扫描服务
		 */
		MusicService musicService = new MusicServiceImpl(getActivity());
		musicService.findAllMusicLrc();
		

		CoordinatorLayout view = (CoordinatorLayout) inflater.inflate(R.layout.touying_list_fragment,
				container, false);
		mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		
		return view;
	}
}
