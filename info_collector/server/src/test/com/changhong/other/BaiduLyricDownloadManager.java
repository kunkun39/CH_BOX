package com.changhong.other;

import com.changhong.common.utils.WebUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Jack Wang
 * Date: 14-11-28
 * Time: 上午11:39
 */
public class BaiduLyricDownloadManager {

    public static final String GB2312 = "GB2312";

    public static final String UTF_8 = "utf-8";

    public static void main(String[] args) throws Exception {
//        LyricDownloadManager manager = new LyricDownloadManager();
//        manager.searchLyricFromWeb("生锈游乐场", "蔡健雅");
//        manager.searchLyricFromWeb("如果这是情", "李克勤");

//        String music = "/sdcard/baidumusci/song/nihao.mp3";
//        String[] tokens = StringUtils.delimitedListToStringArray(music, "/");
//        String musicPackageName = tokens[tokens.length - 2];
//
//        int lastIndexOfDot = music.lastIndexOf(".");
//        String last = music.substring(0, lastIndexOfDot);
//        last = last.replace("/" + musicPackageName, "/lyric");
//        last = last + ".lrc";
//        System.out.println(last);

        long start = System.currentTimeMillis();
        Map<Integer, String> model = new HashMap<Integer, String>();
//        Map<Integer, String> model = new HashMap<Integer, String>(15000);
        for (int i = 0; i < 10000; i++) {
            model.put(i, String.valueOf(i));
        }
        long end = System.currentTimeMillis();
        long during = end - start;
        System.out.println("spend " + during + "ms");


        String vedioFilePath = "/1/2/3/f/4/5/ef/imafefoje.mp4";
        int lastIndexOfDot = vedioFilePath.lastIndexOf(".");
        int lastIndexOfSlash = vedioFilePath.lastIndexOf("/");
        String filename = vedioFilePath.substring(lastIndexOfSlash, lastIndexOfDot);
        String vedioImagePath = "nice" + File.separator + filename + ".mk";
        System.err.println(vedioImagePath);
    }

    /*
     * 根据歌曲名和歌手名取得该歌的XML信息文件 返回歌词保存路径
     */
    public String searchLyricFromWeb(String musicName, String singerName) throws Exception {
        System.out.println("下载前，歌曲名:" + musicName + ", 歌手名:" + singerName);

        // 传进来的如果是汉字，那么就要进行编码转化
        try {
            musicName = URLEncoder.encode(musicName, UTF_8);
            singerName = URLEncoder.encode(singerName, UTF_8);
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }

        // 百度音乐盒的API
        String strUrl = "http://box.zhangmen.baidu.com/x?op=12&count=1&title=" + musicName + "$$" + singerName + "$$$$";
        System.out.println(strUrl);

        // 生成URL
        InputStream returnXML = WebUtils.httpGetRequestAsStream(strUrl);
        SAXReader reader = new SAXReader();
        Document document = reader.read(returnXML);
        Element root = document.getRootElement();
        Element count = root.element("count");
        if (count.getStringValue().equals("0")) {
            return null;
        }
        Element url = root.element("url");
        Element lrcid = url.element("lrcid");
        int mDownloadLyricId = Integer.valueOf(lrcid.getStringValue());
        returnXML.close();

        return fetchLyricContent(mDownloadLyricId, musicName, singerName);
    }

    /**
     * 根据歌词下载ID，获取网络上的歌词文本内容
     */
    private String fetchLyricContent(int mDownloadLyricId, String musicName, String singerName) {
        if (mDownloadLyricId == -1) {
            System.out.println("未指定歌词下载ID");
            return null;
        }
        BufferedReader br = null;
        StringBuilder content = null;
        String temp = null;
        String lyricURL = "http://box.zhangmen.baidu.com/bdlrc/" + mDownloadLyricId / 100 + "/" + mDownloadLyricId + ".lrc";
        System.out.println("歌词的真实下载地址:" + lyricURL);

        // 生成URL
        InputStream stream = WebUtils.httpGetRequestAsStream(lyricURL);

        // 获取歌词文本，存在字符串类中
        try {
            // 建立网络连接
            br = new BufferedReader(new InputStreamReader(stream, GB2312));
            if (br != null) {
                content = new StringBuilder();
                // 逐行获取歌词文本
                while ((temp = br.readLine()) != null) {
                    content.append(temp);
                    System.out.println("<Lyric>" + temp);
                }
                br.close();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            musicName = URLDecoder.decode(musicName, UTF_8);
            singerName = URLDecoder.decode(singerName, UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (content != null) {
            // 检查保存的目录是否已经创建

            String folderPath = "D:\\TempFolder\\music\\";

            File savefolder = new File(folderPath);
            if (!savefolder.exists()) {
                savefolder.mkdirs();
            }
            String savePath = folderPath + File.separator + musicName + "-" + singerName + ".lrc";
            System.out.println("歌词保存路径:" + savePath);

            saveLyric(content.toString(), savePath);

            return savePath;
        } else {
            return null;
        }
    }

    /**
     * 将歌词保存到本地，写入外存中
     */
    private void saveLyric(String content, String filePath) {
        // 保存到本地
        File file = new File(filePath);
        try {
            OutputStream outstream = new FileOutputStream(file);
            OutputStreamWriter out = new OutputStreamWriter(outstream);
            out.write(content);
            out.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
             System.out.println("很遗憾，将歌词写入外存时发生了IO错误");
        }
         System.out.println("歌词保存成功");
    }
}
