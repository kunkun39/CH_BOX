/**
 * 
 */
package com.changhong.tvserver.touying.music;

import java.io.Serializable;

/**
 * @author yves.yang
 *
 */
public class JsonMusicObject implements Serializable{

	String musicPath;
	String musicName;
	String artist;
	String musicLrcPath;
	
	public JsonMusicObject()
	{
		super();
	}
	
	/**
	 * @param musicPath
	 * @param musicName
	 * @param artist
	 * @param musicLrcPath
	 */
	public JsonMusicObject(JsonMusicObject object) {
		super();
		this.musicPath = object.musicPath;
		this.musicName = object.musicName;
		this.artist = object.artist;
		this.musicLrcPath = object.musicLrcPath;
	}
	
	/**
	 * @param musicPath
	 * @param musicName
	 * @param artist
	 * @param musicLrcPath
	 */
	public JsonMusicObject(String musicPath, String musicName,
			String artist, String musicLrcPath) {
		super();
		this.musicPath = musicPath;
		this.musicName = musicName;
		this.artist = artist;
		this.musicLrcPath = musicLrcPath;
	}
	/**
	 * @return musicPath
	 */
	public String getMusicPath() {
		return musicPath;
	}
	/**
	 * @param musicPath 要设置的 musicPath
	 */
	public void setMusicPath(String musicPath) {
		this.musicPath = musicPath;
	}
	/**
	 * @return musicName
	 */
	public String getMusicName() {
		return musicName;
	}
	/**
	 * @param musicName 要设置的 musicName
	 */
	public void setMusicName(String musicName) {
		this.musicName = musicName;
	}
	/**
	 * @return artist
	 */
	public String getArtist() {
		return artist;
	}
	/**
	 * @param artist 要设置的 artist
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}
	/**
	 * @return musicLrcPath
	 */
	public String getMusicLrcPath() {
		return musicLrcPath;
	}
	/**
	 * @param musicLrcPath 要设置的 musicLrcPath
	 */
	public void setMusicLrcPath(String musicLrcPath) {
		this.musicLrcPath = musicLrcPath;
	}
	

}
