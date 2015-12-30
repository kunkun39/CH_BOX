package com.changhong.touying.music;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.changhong.touying.nanohttpd.NanoHTTPDService;
import com.changhong.touying.vedio.AbstructProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jack Wang
 */
public class MusicProvider implements AbstructProvider {

    public static final int FILTER_SIZE = 1 * 1024 * 1024;// 1MB

    public static final int FILTER_DURATION = 1 * 60 * 1000;// 1分钟

    private Context context;

    public MusicProvider(Context context) {
        this.context = context;
    }

    @Override
    public List<?> getList() {
        List<Music> list = new ArrayList<Music>();
        if (context != null) {
            StringBuffer select = new StringBuffer(" 1=1 ");
            // 查询语句：检索出.mp3为后缀名，时长大于1分钟，文件大小大于1MB的媒体文件
            select.append(" and " + MediaStore.Audio.Media.SIZE + " > " + FILTER_SIZE);
            select.append(" and " + MediaStore.Audio.Media.DURATION + " > " + FILTER_DURATION);
            Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, select.toString(), null, MediaStore.Audio.Media.ARTIST_KEY);
            if (cursor != null) {

                while (cursor.moveToNext()) {
                	long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    long artistId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID));
                    int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    long createTime = cursor .getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED));
                    Music music = new Music(id, title, path, albumId, artist,artistId, duration, createTime);
                    
                    //除开NanoHTTPD映射目录外的其它目录歌曲不再添加
                    boolean otheradded=false;
                    if (NanoHTTPDService.otherHttpServerPaths!=null&&NanoHTTPDService.otherHttpServerPaths.size()>0) {
						for (String otherpath:NanoHTTPDService.otherHttpServerPaths) {
							if (path.startsWith(otherpath)) {
								list.add(music);
								otheradded=true;
								break;
							}
						}
					}
                    if (!otheradded &&!TextUtils.isEmpty(NanoHTTPDService.defaultHttpServerPath)&&path.startsWith(NanoHTTPDService.defaultHttpServerPath)) {
                    	list.add(music);
					}
                }
                cursor.close();
            }
        }
        return list;
    }

    public Map<String, List<Music>> getMapStructure(List<?> list) {
        Map<String, List<Music>> model = new HashMap<String, List<Music>>();
        if (list != null) {
            for (Object o : list) {
                Music music = (Music)o;
                String artist = music.getArtist();

                List<Music> musics = null;
                if (model.containsKey(artist)) {
                    musics = model.get(artist);
                } else {
                    musics = new ArrayList<Music>();
                }
                musics.add(music);
                model.put(artist, musics);
            }
        }
        return model;
    }

    public List<String> getMusicList(Map<String, List<Music>> model) {
        List<String> vedioList = new ArrayList<String>();
        for (String key : model.keySet()) {
            if (model.get(key).size() > 1) {
                vedioList.add(0, key);
            } else {
                vedioList.add(key);
            }
        }
        return vedioList;
    }
    
	public String getAlbumName(long albumId) {
		String album = "";
		if (albumId < 0) {
			return album;
		}
		StringBuffer select = new StringBuffer(" 1=1 ");
		// 查询语句：检索出.mp3为后缀名，时长大于1分钟，文件大小大于1MB的媒体文件
		select.append(" and " + MediaStore.Audio.Media.ALBUM_ID + " = "
				+ albumId);
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
				select.toString(), null, MediaStore.Audio.Media.ARTIST_KEY);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				album = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
				break;
			}
			cursor.close();
		}
		return album;

	}
}
