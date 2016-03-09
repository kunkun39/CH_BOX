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
import com.changhong.touying.adapter.DividerItemDecoration;
import com.changhong.touying.adapter.RecyclerViewAdapter;
import com.changhong.touying.dialog.MusicPlayer;
import com.changhong.touying.dialog.MusicPlayer.OnPlayListener;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicProvider;
import com.changhong.touying.music.SetDefaultImage;
import com.changhong.touying.service.MusicService;
import com.changhong.touying.service.MusicServiceImpl;

import java.util.List;

public class MusicCategoryAllTab extends Fragment {

    public static final String TAG = "MusicCategoryAllTab";
    /************************************************** 歌曲部分 *******************************************************/

    /**
     * 所有的音乐信息
     */
    private List<Music> musics = null;

    private RecyclerView mRecyclerView;

    View view;

    /**
     * 视频浏览部分
     */
    private MusicPlayer player = null;


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

        MusicProvider provider = new MusicProvider(getActivity());
        musics = (List<Music>) provider.getList();

        view = (CoordinatorLayout) inflater.inflate(R.layout.touying_list_fragment,
                container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        initView();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView
                .getContext()));
        mRecyclerView.setAdapter(new RecyclerViewAdapter(getActivity(), musics,
                player));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

    }

    private void initView() {

        initPlayer();

        /**
         * 歌曲部分
         */
        SetDefaultImage.getInstance().setContext(getActivity());

    }

    /**
     * 播放控制栏
     */
    private void initPlayer() {
        player = new MusicPlayer();

        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.music_seek_layout, player, MusicPlayer.TAG)
                .show(player).commitAllowingStateLoss();


        player.setOnPlayListener(new OnPlayListener() {
            boolean isLastSong = false;

            @Override
            public void OnPlayFinished() {
                if (isLastSong) {
                    player.stopTVPlayer();
                    isLastSong = false;
                } else {
                    player.nextMusic();
                }
            }

            @Override
            public void OnPlayBegin(String path, String name, String artist) {
                if (musics.get(musics.size() - 1).getPath().equals(path)) {
                    isLastSong = true;
                }

            }
        });
        player.attachMusics(musics).autoPlaying(true);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        player.attachMusics(musics).autoPlaying(true);
    }

}