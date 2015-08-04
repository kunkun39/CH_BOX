package com.changhong.tvhelper.utils;

import com.changhong.common.utils.StringUtils;

import java.util.*;

/**
 * Created by Jack Wang
 */
public class YuYingWordsUtils {

    /**************************************************控制部分*********************************************************/

    /**
     * yu yin control mapping
     */
    private final static Map<String, List<String>> TV_CONTROL_KEYWORDS = new HashMap<String, List<String>>();

    static {
        List<String> shouYe = new ArrayList<String>();
        shouYe.add("主页");shouYe.add("首页");shouYe.add("桌面");shouYe.add("退出");
        TV_CONTROL_KEYWORDS.put("key:home", shouYe);

        List<String> back = new ArrayList<String>();
        back.add("返回");back.add("回退");back.add("后退");back.add("上一级");back.add("上1级");back.add("上一");back.add("上1");
        TV_CONTROL_KEYWORDS.put("key:back", back);

        List<String> ok = new ArrayList<String>();
        ok.add("ｏｋ");ok.add("确定");ok.add("确认");
        TV_CONTROL_KEYWORDS.put("key:ok", ok);

        List<String> volumnUp = new ArrayList<String>();
        volumnUp.add("声音大点");volumnUp.add("大点声");volumnUp.add("大声点");volumnUp.add("加声音");volumnUp.add("开点声音");
        volumnUp.add("声音大点儿");volumnUp.add("大点声儿");volumnUp.add("大声点儿");volumnUp.add("加点儿声音");volumnUp.add("开点儿声音");volumnUp.add("音量加");
        TV_CONTROL_KEYWORDS.put("key:volumeup|key:volumeup", volumnUp);

        List<String> volumnDown = new ArrayList<String>();
        volumnDown.add("声音小点");volumnDown.add("小点声");volumnDown.add("小声点");volumnDown.add("减声音");volumnDown.add("关点声音");volumnDown.add("观点声音");
        volumnDown.add("声音小点儿");volumnDown.add("小点声儿");volumnDown.add("小声点儿");volumnDown.add("减儿声音");volumnDown.add("关点儿声音");volumnDown.add("观点儿声音");volumnDown.add("音量减");
        TV_CONTROL_KEYWORDS.put("key:volumedown|key:volumedown", volumnDown);

        List<String> tv = new ArrayList<String>();
        tv.add("电视");tv.add("频道");
        TV_CONTROL_KEYWORDS.put("key:dtv", tv);

        List<String> tvUp = new ArrayList<String>();
        tvUp.add("上一台");tvUp.add("上1台");tvUp.add("上一个");tvUp.add("上1个");tvUp.add("频道减");
        TV_CONTROL_KEYWORDS.put("key:dtv|key:up", tvUp);

        List<String> tvDown = new ArrayList<String>();
        tvDown.add("下一台");tvDown.add("下1台");tvDown.add("下一个");tvDown.add("下1个");
        tvDown.add("换台");tvDown.add("换频道");tvDown.add("调台");tvDown.add("跳台");tvDown.add("频道加");
        TV_CONTROL_KEYWORDS.put("key:dtv|key:down", tvDown);

        List<String> left = new ArrayList<String>();
        left.add("左");left.add("左面");left.add("左边");left.add("向左");left.add("向左边");left.add("往左");left.add("往左边");
        TV_CONTROL_KEYWORDS.put("key:left", left);

        List<String> right = new ArrayList<String>();
        right.add("右");right.add("右面");right.add("右边");right.add("向右");right.add("向右边");right.add("往右");right.add("往右边");
        TV_CONTROL_KEYWORDS.put("key:right", right);

        List<String> up = new ArrayList<String>();
        up.add("上");up.add("上面");up.add("上边");up.add("向上");up.add("向上边");up.add("往上");up.add("往上边");
        TV_CONTROL_KEYWORDS.put("key:up", up);

        List<String> down = new ArrayList<String>();
        down.add("下");down.add("下面");down.add("下边");down.add("向下");down.add("向下边");down.add("往下");down.add("往下边");
        TV_CONTROL_KEYWORDS.put("key:down", down);
        
        List<String> mute = new ArrayList<String>();
        mute.add("静音");mute.add("取消静音");
        TV_CONTROL_KEYWORDS.put("key:volume_mute", mute);
    }

    /**
     * 根据用户的语音返回相应的指令，处理音量是模糊匹配，其余都是等于匹配, 如果你想特殊处理某个语音指令，HERE
     */
    public static String isSearchContainsControl(String searchWords) {
        Set<String> commands = TV_CONTROL_KEYWORDS.keySet();

        for (String key : commands) {
            List<String> keywords = TV_CONTROL_KEYWORDS.get(key);

            for (String keyword : keywords) {
                if(keyword.equals("key:volumeup|key:volumeup") || keyword.equals("key:volumedown|key:volumedown")) {
                    if (searchWords.startsWith(keyword)) {
                        return key;
                    }
                } else {
                    if (searchWords.equals(keyword)) {
                        return key;
                    }
                }
            }
        }

        return null;
    }

    /**************************************************应用部分*********************************************************/

    public static boolean isSearchContainsAppKeywords(String searchWords) {
        if ((searchWords.indexOf("打开") >= 0) || (searchWords.indexOf("启动") >= 0) || (searchWords.indexOf("开启") >= 0)) {
            return true;
        }
        return false;
    }

    public static String appSearchWordsConvert(String needConvert) {
        needConvert = needConvert.replace("打开", "");
        needConvert = needConvert.replace("启动", "");
        needConvert = needConvert.replace("开启", "");
        return needConvert;
    }

    /************************************************语音换台部分*******************************************************/

    /**
     * yu yin channel switch need replace chars, key is baidu word, value is convert value which used for compare
     * follow test must execute:
     * 1 - 中央1台 -> 中央一台高清（没有的话，中央一台）
     * 2 - 湖南（福建） -> 湖南（福建）高清（没有的话，湖南（福建）卫视）
     * 3 - 湖南卫视 -> 湖南高清（没有的话，湖南卫视）
     * 4 - 湖南高清 -> 湖南高清（没有的话，湖南卫视）
     * 5 - 湖南卫视标清 -> 湖南卫视
     * 6 - 四川1台 -> 四川一台高清（没有的话，四川一台）
     * 7 - 福建 ->福建高清（没有的话，福建卫视）
     * 8 - 福建高清 ->福建高清（没有的话，福建卫视）
     */
    private final static Map<String, String> FIRST_TV_CHANNEL_REPLACE_KEYWORDS = new HashMap<String, String>();

    static {
        FIRST_TV_CHANNEL_REPLACE_KEYWORDS.put("十一", "11");
        FIRST_TV_CHANNEL_REPLACE_KEYWORDS.put("十二", "12");
        FIRST_TV_CHANNEL_REPLACE_KEYWORDS.put("十三", "13");
        FIRST_TV_CHANNEL_REPLACE_KEYWORDS.put("十四", "14");
    }

    private final static Map<String, String> TV_CHANNEL_REPLACE_KEYWORDS = new HashMap<String, String>();

    static {
        //baidu yuyin charaters, which is different from normal char, so replace it to normal one
        TV_CHANNEL_REPLACE_KEYWORDS.put("ｃｃｔｖ", "CCTV");
        TV_CHANNEL_REPLACE_KEYWORDS.put("cctv", "CCTV");
        TV_CHANNEL_REPLACE_KEYWORDS.put("中央", "CCTV");
        TV_CHANNEL_REPLACE_KEYWORDS.put("央视", "CCTV");
        TV_CHANNEL_REPLACE_KEYWORDS.put("四川", "SCTV");
        TV_CHANNEL_REPLACE_KEYWORDS.put("成都", "CDTV");
        TV_CHANNEL_REPLACE_KEYWORDS.put("一", "1");
        TV_CHANNEL_REPLACE_KEYWORDS.put("二", "2");
        TV_CHANNEL_REPLACE_KEYWORDS.put("三", "3");
        TV_CHANNEL_REPLACE_KEYWORDS.put("四", "4");
        TV_CHANNEL_REPLACE_KEYWORDS.put("五", "5");
        TV_CHANNEL_REPLACE_KEYWORDS.put("六", "6");
        TV_CHANNEL_REPLACE_KEYWORDS.put("七", "7");
        TV_CHANNEL_REPLACE_KEYWORDS.put("八", "8");
        TV_CHANNEL_REPLACE_KEYWORDS.put("九", "9");
        TV_CHANNEL_REPLACE_KEYWORDS.put("十", "10");
        TV_CHANNEL_REPLACE_KEYWORDS.put("十一", "11");
        TV_CHANNEL_REPLACE_KEYWORDS.put("十二", "12");
        TV_CHANNEL_REPLACE_KEYWORDS.put("十三", "13");
        TV_CHANNEL_REPLACE_KEYWORDS.put("十四", "14");
        TV_CHANNEL_REPLACE_KEYWORDS.put("卫视", "");
        TV_CHANNEL_REPLACE_KEYWORDS.put("高清", "");
        TV_CHANNEL_REPLACE_KEYWORDS.put("电视台", "");
        TV_CHANNEL_REPLACE_KEYWORDS.put("台", "");
        TV_CHANNEL_REPLACE_KEYWORDS.put("频道", "");
    }

    /**
     * 该方法主要用于语音词语转化
     */
    public static String yuYingChannelSearchWordsConvert(String needConvert) {
        for (String key : FIRST_TV_CHANNEL_REPLACE_KEYWORDS.keySet()) {
            String value = FIRST_TV_CHANNEL_REPLACE_KEYWORDS.get(key);
            needConvert = needConvert.replace(key, value);
        }

        for (String key : TV_CHANNEL_REPLACE_KEYWORDS.keySet()) {
            String value = TV_CHANNEL_REPLACE_KEYWORDS.get(key);
            needConvert = needConvert.replace(key, value);
        }

        /**
         * 保证标清关键字的转化在卫视的后面，所以在这里转化一次，而没有配置在上面的MAP中, 没有CCTV-4高清频道，所以去掉
         */
        needConvert = needConvert.replace("标清", "卫视");
        if (!needConvert.equals("CCTV") && needConvert.contains("CCTV")
                && !needConvert.startsWith("CCTV-4") && !needConvert.startsWith("CCTV4")
                && !needConvert.startsWith("CCTV-11") && !needConvert.startsWith("CCTV11")) {
            needConvert = needConvert + "高清";
        }
        return needConvert;
    }

    /**
     * 保存语音和文字输入的查询和频道的关键词一致
     */
    private final static Map<String, String> TV_CHANNEL_SPECIAL_WORDS = new HashMap<String, String>();

    static {
        //baidu yuyin charaters, which is different from normal char, so replace it to normal one
        TV_CHANNEL_SPECIAL_WORDS.put("四川", "SCTV-");
        TV_CHANNEL_SPECIAL_WORDS.put("成都", "CDTV-");
    }

    /**
     * 判断
     */
    public static String getSpecialWordsChannel(String needConvert) {
        for (String key : TV_CHANNEL_SPECIAL_WORDS.keySet()) {
            String value = TV_CHANNEL_SPECIAL_WORDS.get(key);
            needConvert = needConvert.replace(key, value);
        }
        return needConvert;
    }

    /**
     * 地方台的卫视一般都是一台，这个需要做一下处理
     */
    private final static Map<String, List<String>> TV_CHANNEL_LOCATION_WORDS = new HashMap<String, List<String>>();

    static {
        //baidu yuyin charaters, which is different from normal char, so replace it to normal one
        List<String> otherNames1 = new ArrayList<String>();
        otherNames1.add("SCTV-1");
        otherNames1.add("四川卫视高清");
        otherNames1.add("四川卫视");
        TV_CHANNEL_LOCATION_WORDS.put("四川卫视", otherNames1);

        //baidu yuyin charaters, which is different from normal char, so replace it to normal one
        List<String> otherNames2 = new ArrayList<String>();
        otherNames2.add("CCTV1");
        otherNames2.add("CCTV-1");
        TV_CHANNEL_LOCATION_WORDS.put("中央电视台", otherNames2);
        TV_CHANNEL_LOCATION_WORDS.put("中央台", otherNames2);
        TV_CHANNEL_LOCATION_WORDS.put("CCTV", otherNames2);

        //baidu yuyin charaters, which is different from normal char, so replace it to normal one
        List<String> otherNames3 = new ArrayList<String>();
        otherNames3.add("东方卫视");
        TV_CHANNEL_LOCATION_WORDS.put("东方卫视", otherNames3);

        //baidu yuyin charaters, which is different from normal char, so replace it to normal one
        List<String> otherNames4 = new ArrayList<String>();
        otherNames4.add("CCTV2");
        otherNames4.add("CCTV-2");
        TV_CHANNEL_LOCATION_WORDS.put("中央财经", otherNames4);

        //baidu yuyin charaters, which is different from normal char, so replace it to normal one
        List<String> otherNames5 = new ArrayList<String>();
        otherNames5.add("CCTV-新闻");
        TV_CHANNEL_LOCATION_WORDS.put("中央新闻", otherNames5);
        TV_CHANNEL_LOCATION_WORDS.put("央视新闻", otherNames5);
        TV_CHANNEL_LOCATION_WORDS.put("CCTV新闻", otherNames5);

        List<String> otherNames6 = new ArrayList<String>();
        otherNames6.add("CCTV-少儿");
        TV_CHANNEL_LOCATION_WORDS.put("中央少儿", otherNames6);
        TV_CHANNEL_LOCATION_WORDS.put("央视少儿", otherNames6);
        TV_CHANNEL_LOCATION_WORDS.put("CCTV少儿", otherNames6);

        List<String> otherNames7 = new ArrayList<String>();
        otherNames7.add("CCTV-音乐");
        TV_CHANNEL_LOCATION_WORDS.put("中央音乐", otherNames7);
        TV_CHANNEL_LOCATION_WORDS.put("央视音乐", otherNames7);
        TV_CHANNEL_LOCATION_WORDS.put("CCTV音乐", otherNames7);

        List<String> otherNames8 = new ArrayList<String>();
        otherNames8.add("CCTV-10");
        otherNames8.add("CCTV-10高清");
        TV_CHANNEL_LOCATION_WORDS.put("中央科教", otherNames8);

        List<String> otherNames9 = new ArrayList<String>();
        otherNames9.add("CCTV-11");
        otherNames9.add("CCTV-11高清");
        TV_CHANNEL_LOCATION_WORDS.put("中央十一台", otherNames9);
        TV_CHANNEL_LOCATION_WORDS.put("CCTV11", otherNames9);
    }

    /**
     * 判断
     */
    public static List<String> getLocationWordsChannel(String needCheck) {
        if (StringUtils.hasLength(needCheck)) {
            needCheck = needCheck.replace("ｃｃｔｖ", "CCTV");
            needCheck = needCheck.replace("cctv", "CCTV");
        }

        return TV_CHANNEL_LOCATION_WORDS.get(needCheck);
    }

    /******************************************某些地方太不需要处理数字***********************************************************/

    private final static Map<String, String> TV_CHANNEL_NUMBER_NOCONVERT = new HashMap<String, String>();

    static {
        //baidu yuyin charaters, which is different from normal char, so replace it to normal one
        TV_CHANNEL_NUMBER_NOCONVERT.put("一", "1");
        TV_CHANNEL_NUMBER_NOCONVERT.put("二", "2");
        TV_CHANNEL_NUMBER_NOCONVERT.put("三", "3");
        TV_CHANNEL_NUMBER_NOCONVERT.put("四", "4");
        TV_CHANNEL_NUMBER_NOCONVERT.put("五", "5");
        TV_CHANNEL_NUMBER_NOCONVERT.put("六", "6");
    }

    /**
     * 该方法主要用于手机上搜索频道的词语转化
     */
    public static String numNotNeedConvert(String needConvert) {
        if (needConvert.contains("绵阳")) {
            for (String key : TV_CHANNEL_NUMBER_NOCONVERT.keySet()) {
                String value = TV_CHANNEL_NUMBER_NOCONVERT.get(key);
                needConvert = needConvert.replace(key, value);
            }
        }
        return needConvert;
    }

    /******************************************频道查询部分***********************************************************/

    private final static Map<String, String> TV_CHANNEL_SEARCH_KEYWORDS = new HashMap<String, String>();

    static {
        //baidu yuyin charaters, which is different from normal char, so replace it to normal one
        TV_CHANNEL_SEARCH_KEYWORDS.put("中央", "CCTV-");
        TV_CHANNEL_SEARCH_KEYWORDS.put("四川", "SCTV-");
        TV_CHANNEL_SEARCH_KEYWORDS.put("成都", "CDTV-");
        TV_CHANNEL_SEARCH_KEYWORDS.put("一", "1");
        TV_CHANNEL_SEARCH_KEYWORDS.put("二", "2");
        TV_CHANNEL_SEARCH_KEYWORDS.put("三", "3");
        TV_CHANNEL_SEARCH_KEYWORDS.put("四", "4");
        TV_CHANNEL_SEARCH_KEYWORDS.put("五", "5");
        TV_CHANNEL_SEARCH_KEYWORDS.put("六", "6");
        TV_CHANNEL_SEARCH_KEYWORDS.put("七", "7");
        TV_CHANNEL_SEARCH_KEYWORDS.put("八", "8");
        TV_CHANNEL_SEARCH_KEYWORDS.put("九", "9");
        TV_CHANNEL_SEARCH_KEYWORDS.put("十", "10");
        TV_CHANNEL_SEARCH_KEYWORDS.put("十一", "11");
        TV_CHANNEL_SEARCH_KEYWORDS.put("十二", "12");
        TV_CHANNEL_SEARCH_KEYWORDS.put("十三", "13");
        TV_CHANNEL_SEARCH_KEYWORDS.put("台", "");
        TV_CHANNEL_SEARCH_KEYWORDS.put("套", "");
        TV_CHANNEL_SEARCH_KEYWORDS.put("电视", "");
    }

    /**
     * 该方法主要用于手机上搜索频道的词语转化
     */
    public static String normalChannelSearchWordsConvert(String needConvert) {
        for (String key : TV_CHANNEL_SEARCH_KEYWORDS.keySet()) {
            String value = TV_CHANNEL_SEARCH_KEYWORDS.get(key);
            needConvert = needConvert.replace(key, value);
        }
        return needConvert;
    }


}
