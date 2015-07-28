package com.changhong.touying.vedio;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.changhong.common.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jack Wang
 */
public class VedioProvider implements AbstructProvider {

    private Context context;

    public VedioProvider(Context context) {
        this.context = context;
    }

    @Override
    public List<?> getList() {
        List<Vedio> list = null;
        if (context != null) {
            StringBuffer select = new StringBuffer(" 1=1 ");
            // 查询语句：检索出.mp3为后缀名，时长大于1分钟，文件大小大于1MB的媒体文件
            select.append(" and " + MediaStore.Audio.Media.DURATION + " > 0 ");
            Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, select.toString(), null, null);
            if (cursor != null) {
                list = new ArrayList<Vedio>();
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                    String title = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                    String displayName = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                    String mimeType = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                    String path = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    long duration = cursor
                            .getInt(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                    long createTime = cursor
                            .getInt(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));
                    if (!TextUtils.isEmpty(displayName)) {
                    	 Vedio video = new Vedio(id, title, displayName, mimeType, path, duration, createTime);
                         list.add(video);
					}
                }
                cursor.close();
            }
        }
        return list;
    }

    public Map<String, List<Vedio>> getMapStructure(List<?> list) {
        Map<String, List<Vedio>> model = new HashMap<String, List<Vedio>>();
        if (list != null) {
            for (Object o : list) {
                Vedio vedio = (Vedio)o;
                String path = vedio.getPath();
                String[] tokens = StringUtils.delimitedListToStringArray(path, File.separator);
                String packageName = tokens[tokens.length - 2];

                List<Vedio> vedios = null;
                if (model.containsKey(packageName)) {
                    vedios = model.get(packageName);
                } else {
                    vedios = new ArrayList<Vedio>();
                }
                vedios.add(vedio);
                model.put(packageName, vedios);
            }
        }
        return model;
    }

    public List<String> getVedioList(Map<String, List<Vedio>> model) {
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

}
