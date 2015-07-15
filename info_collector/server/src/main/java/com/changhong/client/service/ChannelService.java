package com.changhong.client.service;

import java.util.HashMap;

/**
 * Created by maren on 15-6-26.
 */
public class ChannelService {
    public static final String CHANNEL_SOURCE = "CCTV-1,CCTV-2,CCTV-3,CCTV-4,CCTV-5,CCTV-6,CCTV-7,CCTV-8,CCTV-9,CCTV-10,CCTV-11,CCTV-12,CCTV-新闻,SCTV-1,SCTV-2,SCTV-3,SCTV-4,SCTV-5,SCTV-6,SCTV-7,SCTV-科教,SCTV-公共,SCTV-峨眉,川网导视,CDTV-1 新闻综合,CDTV-2 经济频道,CDTV-3 都市生活,CDTV-4 影视文艺,CDTV-5 公共频道,CDTV-6 少儿频道,CDTV-8 每日购物,好享购物,快乐购物,中视购物,优购物,时尚购物,家有购物,星元●视讯,北京卫视,江苏卫视,天津卫视,深圳卫视,贵州卫视,浙江卫视 ,东方卫视,山东卫视,福建卫视,安徽卫视,家家购物,环球购物,风尚购物,睛彩四川,湖北卫视,重庆卫视,辽宁卫视,云南卫视,江西卫视,广西卫视,广东卫视,河南卫视,黑龙江卫视,吉林卫视,陕西卫视,河北卫视,兵团卫视,宁夏卫视,内蒙古卫视,湖南卫视,青海卫视,山西卫视,旅游卫视,甘肃卫视,SCTV-星空,金鹰卡通,嘉佳卡通,卡酷卡通,CCTV-少儿,上海纪实,家政频道,北京纪实,CDTV-7 美食天府,康巴卫视,新疆卫视,西藏卫视,CCTV-音乐,CCTV-News,CETV-1,CETV-2,中国天气,星元●首映,星元●欧美院线,星元●欧美剧场,星元●亚洲院线,星元●亚洲剧场,星元●新知,星元●成长,DOX-音像世界,第一剧场,东方财经,动漫秀场,发现之旅,法制天地,风云剧场,风云音乐,风云足球,国防军事,怀旧剧场,劲爆体育,老故事,魅力音乐,全纪实,世界地理,茶,英语辅导,游戏风云,新视觉,环球旅游,家庭理财,四海钓鱼,天元围棋,书画,梨园,摄影,女性时尚,靓装,欢笑剧场,文化精品,新科动漫,生活时尚,游戏竞技,财富天下,金色频道,高尔夫●网球,高尔夫,CHC-动作影院,欧洲足球,先锋乒羽,新动漫,中华美食,家庭健康,电子体育,早期教育,优优宝贝,宝贝家,中学生,时代出行,时代风尚,时代家居,时代美食,七彩戏剧,快乐宠物,快乐垂钓,碟市,彩民在线,车迷,职业指南,幸福彩,收藏天下,老年福,百姓健康,都市剧场,文物宝库,新娱乐,留学世界,考试在线,极速汽车,武术世界,人物,国学,汽摩,现代女性,城市建设,DV生活,CCTV5-体育赛事高清,3D频道,CHC-高清,DOX●映画-HD,DOX●剧场-HD,DOX●新知-HD,DOX●新艺-HD,DOX●英伦-HD,DOX●院线-HD,DOX●怡家-HD,星元●韩剧,星元●少儿,星元●旅行,星元●国剧,CCTV-1高清,CCTV-2高清,CCTV-3高清,CCTV-5高清,CCTV-6高清,CCTV-7高清,CCTV-8高清,CCTV-9高清,CCTV-10高清,CCTV-12高清,CCTV-14高清,新视觉高清,四川高清,四川影视高清,江苏高清,北京高清,天津高清,深圳高清,上海高清,山东高清,安徽高清,湖北高清,辽宁高清,广东高清,黑龙江高清,浙江高清,湖南高清,上海纪实高清,本地节目1,本地节目2,本地节目3,本地节目4,I家TV,精品导视";
    public static HashMap<String, Integer> channelMap = new HashMap<String, Integer>();

    public static void initChannels() {
        String channels[] = CHANNEL_SOURCE.split(",");
        for (int i = 0; i < channels.length; i++) {
            channelMap.put(channels[i], i + 1);
        }

    }
}
