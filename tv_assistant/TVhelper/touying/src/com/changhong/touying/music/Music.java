package com.changhong.touying.music;

import java.io.Serializable;

/**
 * Created by Jack Wang
 */
public class Music implements Serializable {

    private int id;

    private String title;

    private String path;

    private int albumId;

    private String artist;

    private int artistId;

    private int duration;

    private long createTime;

    public Music(int id, String title, String path, int albumId, String artist, int artistId, int duration, long createTime) {
        this.id = id;
        this.title = title;
        this.path = path;
        this.albumId = albumId;
        this.artist = artist;
        this.artistId = artistId;
        this.duration = duration;
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
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
