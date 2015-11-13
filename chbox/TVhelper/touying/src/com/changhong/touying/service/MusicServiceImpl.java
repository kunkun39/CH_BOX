package com.changhong.touying.service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.db.sqlite.DatabaseContainer;
import com.changhong.touying.music.MusicLrc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack Wang
 */
public class MusicServiceImpl implements MusicService {

    private Context context;

    private static List<MusicLrc> musicLrcs = new ArrayList<MusicLrc>();

    public MusicServiceImpl(Context context) {
        this.context = context;
    }

    @Override
    public void findAllMusicLrc() {
        musicLrcs.clear();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    File root = Environment.getExternalStorageDirectory();
                    if (root != null) {
                        getLrcFiles(Environment.getExternalStorageDirectory());
                        for (MusicLrc musicLrc : musicLrcs) {
                            MusicLrc lrc = findMusicLrc(musicLrc.getSinger(), musicLrc.getName());
                            if (lrc == null) {
                                saveMusicLrc(musicLrc);
                            }
                        }
                    }
                } catch (Exception e) {

                }}
            };
        thread.start();
    }

    private void getLrcFiles(File filePath) {
        File[] files = filePath.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String[] dirDeep = StringUtils.delimitedListToStringArray(file.getAbsolutePath(), File.separator);
                if (file.isDirectory()) {
                    //6层目录，一般外部存储的根目录就展层，类似于KUGOU, BAIDU ,QQ的歌词都在第5层左右
                    if (dirDeep.length <= 6) {
                        getLrcFiles(file);
                    }
                } else {
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".lrc")) {
                        try {
                            String filename = dirDeep[dirDeep.length - 1];
                            String musicName = StringUtils.delimitedListToStringArray(filename, ".")[0];
                            String name = StringUtils.delimitedListToStringArray(musicName, "-")[0];
                            String singer = StringUtils.delimitedListToStringArray(musicName, "-")[1];
                            MusicLrc musicLrc = new MusicLrc();
                            musicLrc.setName(name);
                            musicLrc.setSinger(singer);
                            musicLrc.setPath(file.getAbsolutePath());
                            musicLrcs.add(musicLrc);
                        } catch (Exception e) {
                            //出问题继续扫描和音乐歌词格式不是按照标准方式的都放弃保存 匆匆那年-王菲.lrc
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void saveMusicLrc(MusicLrc musicLrc) {
        String insert = "INSERT INTO music_lrc (singer, name, path) VALUES (?, ?, ?)";
        SQLiteDatabase database = MyApplication.getDatabaseContainer(context).getWritableDatabase();
        database.execSQL(insert, new Object[]{musicLrc.getSinger(), musicLrc.getName(), musicLrc.getPath()});
    }

    @Override
    public MusicLrc findMusicLrc(String singer, String name) {
        SQLiteDatabase database = MyApplication.getDatabaseContainer(context).getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM music_lrc WHERE singer = ? AND name = ? LIMIT 1", new String[]{singer, name});

        MusicLrc musicLrc = null;
        try {
        	if (cursor.moveToFirst()) {
                musicLrc = new MusicLrc();
                musicLrc.setSinger(cursor.getString(1));
                musicLrc.setName(cursor.getString(2));
                musicLrc.setPath(cursor.getString(3));
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        cursor.close();
        return musicLrc;
    }
}
