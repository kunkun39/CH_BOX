/**
 * 
 */
package com.changhong.touying.tab;

import java.io.Serializable;
import java.util.List;
import java.util.zip.Inflater;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
public class MusicCategorySpecialTab extends Fragment{


	public static final String TAG = "MusicCategorySpecialTab";
    /**************************************************歌曲部分*******************************************************/

    /**
     * Image List adapter
     */
    private MusicDataAdapter musicAdapter;
    /**
     * 视频浏览部分
     */
    private GridView musicGridView;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /* （非 Javadoc）
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
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
    	View v = inflater.inflate(R.layout.tab_music_gridview, container,false);
    	
    	initView(v);
    	
    	initEvent();
    	
    	return v;
    }
    private void initView(View v) {        
        /**
         * 歌曲部分
         */    	
        musicGridView = (GridView) v.findViewById(R.id.music_grid_view);
        musicAdapter = new MusicDataAdapter(getActivity());
        musicGridView.setAdapter(musicAdapter);
    }

    private void initEvent() {        

        /**
         * 歌曲部分
         */
        musicGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyApplication.vibrator.vibrate(100);
                Intent intent = new Intent();
                intent.setClass(getActivity(), MusicViewActivity.class);
                Bundle bundle = new Bundle();
                List<Music> musics = MusicDataAdapter.getPositionMusics(position);
                bundle.putSerializable("musics", (Serializable) musics);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }
}
