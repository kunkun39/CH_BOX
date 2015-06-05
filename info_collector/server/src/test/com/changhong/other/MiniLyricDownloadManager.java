package com.changhong.other;

import com.changhong.common.utils.WebUtils;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * User: Jack Wang
 * Date: 15-1-9
 * Time: 下午1:10
 */
public class MiniLyricDownloadManager {

    public static final String GB2312 = "GB2312";

    public static final String UTF_8 = "utf-8";

    public static void main(String[] args) {
        String musicName = "晨间新闻";
        String singer = "蔡健雅";
        http://ttlrcct.qianqian.com/dll/lyricsvr.dll?sh?Artist=蔡健雅&Title=晨间新闻

        try {
            String musicLrcResponse = searchMusicLrcExist(musicName, singer);

            if (StringUtils.hasText(musicLrcResponse)) {
                JSONObject musicLrcJson = new JSONObject(musicLrcResponse);
                int count = musicLrcJson.getInt("count");
                if (count > 0) {
                    JSONArray array = musicLrcJson.getJSONArray("result");
                    JSONObject o = array.getJSONObject(0);
                    String lrcURL = o.getString("lrc");

                    if (StringUtils.hasText(lrcURL)) {
                        getAndSaveLrc(lrcURL, musicName, singer);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String searchMusicLrcExist(String musicName, String singer) throws Exception {
        try {
            musicName = URLEncoder.encode(musicName, UTF_8);
            singer = URLEncoder.encode(singer, UTF_8);
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }

        String musicSrarchURL = "http://geci.me/api/lyric/" + musicName + "/" + singer;

        GetMethod get = new GetMethod(musicSrarchURL);
        String response = WebUtils.httpGetRequest(get);
        System.out.println(response);
        return response;
    }

    public static String getAndSaveLrc(String lrcURL, String musicName, String singer) throws Exception {
        GetMethod get = new GetMethod(lrcURL);
        InputStream in = WebUtils.httpGetRequestAsStream(lrcURL);
        File file = new File("D://" + musicName + "-" + singer + ".lrc");
        FileCopyUtils.copy(in, new FileOutputStream(file));
        return file.getAbsolutePath();
    }
}
