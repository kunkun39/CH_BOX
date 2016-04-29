package com.changhong.touying.music;

import java.io.Serializable;

/**
 * Created by Jack Wang
 */
public class Music implements Serializable {

    private long id;// 歌曲ID

    private String title; // 歌曲名称

    private String path;// 歌曲路径

    private long albumId;//专辑ID 

    private String artist;// 歌手名称

    private long artistId;

    private int duration;// 歌曲时长

    private long createTime;

    public Music(long id, String title, String path, long albumId, String artist, long artistId, int duration, long createTime) {    	
        this.id = id;
        this.title = title;
        this.path = path;
        this.albumId = albumId;
        this.artist = artist;
        this.artistId = artistId;
        this.duration = duration;
        this.createTime = createTime;
    }

    public Music(){
    	
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
