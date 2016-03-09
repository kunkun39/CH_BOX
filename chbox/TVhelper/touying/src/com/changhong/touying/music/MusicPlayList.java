/**
 * 
 */
package com.changhong.touying.music;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yves.yang
 *
 */
public class MusicPlayList implements Serializable{

	String name;
	String path;
	String comment;
	List<String> playList = new ArrayList<String>();
	
	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name 要设置的 name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return path
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @param path 要设置的 path
	 */
	public void setPath(String path) {
		this.path = path;
	}
	/**
	 * @return comment
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param comment 要设置的 comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * @return playList
	 */
	public List<String> getPlayList() {
		return playList;
	}
	/**
	 * @param playList 要设置的 playList
	 */
	public void setPlayList(List<String> playList) {
		this.playList = playList;
	}
	
}
