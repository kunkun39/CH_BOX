package com.changhong.touying.service;

import com.changhong.touying.music.MusicLrc;

/**
 * Created by Jack Wang
 */
public interface MusicService {

    /**
     * 查找所有的音乐歌词文件
     */
    void findAllMusicLrc();

    /**
     * 保存歌词
     * @param musicLrc
     */
    void saveMusicLrc(MusicLrc musicLrc);

    /**
     * 查找歌词
     */
    MusicLrc findMusicLrc(String singer, String name);
}
