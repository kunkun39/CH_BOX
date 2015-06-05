package com.changhong.system.repository;

import com.changhong.system.domain.live.LiveProgram;
import com.changhong.system.service.LiveUpdateServiceImpl;
import com.changhong.system.web.facade.assember.LiveJSONAssember;
import junit.framework.TestCase;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * User: maren
 * Date: 14-11-20
 * Time: 下午2:28
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/database.xml", "/applicationContext.xml"})
public class LiveChannelImplTest extends TestCase{
     @Resource
     SessionFactory sessionFactory;



    @Resource
    LiveUpdateServiceImpl liveUpdateService;

    HibernateTemplate hibernateTemplate;

    @Resource(name = "liveDao")
    LiveDao liveDao;

    @Before
    public void setUp() {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @After
    public void tearDown() {
        hibernateTemplate = null;

    }


//     @Test
//    public void testSaveLiveChannel(){
//        JsonChannelTest jsonChannelTest=new JsonChannelTest();
//        jsonChannelTest.jsonToObject();
//         List<LiveChannel> channels=jsonChannelTest.channels;
//         for (LiveChannel channel:channels){
//              hibernateTemplate.save(channel);
//         }
//
//
//    }

//    @Test
//    public void testSaveLiveProgram(){
//        liveUpdateService.updateliveProgram();
//        liveUpdateService.updateliveChannel();
//
//
//    }

//     @Test
//    public void testSaveLiveChannelProgram(){
//         LiveProgramInfo liveProgramInfo=new LiveProgramInfo();
//         liveProgramInfo.setBitRateInfo("sssss");
//         liveProgramInfo.setAssertID("2342344");
//         liveProgramInfo.setChannelName("北京卫视");
//         hibernateTemplate.save(liveProgramInfo);
//
//    }

    /**
     * JsonAssember更新测试
     */
  @Test
    public void  testLiveProgram(){
        String json="{\n" +
                "    \"ResponseHeader\": {\n" +
                "        \"TransactionId\": null,\n" +
                "        \"Status\": 0,\n" +
                "        \"Params\": {\n" +
                "            \"ChannelID\": \"11\",\n" +
                "            \"EventTypeID\": \"\",\n" +
                "            \"StartDate\": \"20131121\",\n" +
                "            \"EndDate\": \"20161121\",\n" +
                "            \"PosterAspectRatio\": \"10240768\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"ProgramList\": {\n" +
                "        \"Program_item\": [\n" +
                "        {\"ProgramInfo\":{\n" +
                "        \"ProgramName\":\"节目名称\",\n" +
                "        \"Playtime\":\"播放时间\",\n" +
                "        \"EndTime\":\"结束时间\",\n" +
                "        \"EventType\":\"节目类型\",\n" +
                "        \"EventDesc\":\"描述\",\n" +
                "        \"VideoType\":\"高清标识\",\n" +
                "        \"ViewLevel\":\"观看等级\",\n" +
                "        \"PlayUrl\":\"播放地址\",\n" +
                "        \"EventImageUrl\":\"节目海报\",\n" +
                "        \"ContentProvider\":\"节目提供商\",\n" +
                "        \"LocalEntryUID\":\"VOD产品\"\n" +
                "\n" +
                "        },\"MovieInfo\":{\n" +
                "        \"MovieName\":\"影片名称\",\n" +
                "        \"MovieAliasName\":\"影片别名\",\n" +
                "        \"Type\":\"主类型\",\n" +
                "        \"DramaType\":\"副类型\",\n" +
                "        \"Area\":\"地区\" ,\n" +
                "        \"Year\":\"年份\" ,\n" +
                "        \"Actor\":\"演员\" ,\n" +
                "        \"Author\":\"作者\" ,\n" +
                "        \"RunTime\":\"影片时长\",\n" +
                "        \"Count\":10 ,\n" +
                "        \"SummaryShort\":\"简评\",\n" +
                "        \"Commentary\":\"影片简介\" ,\n" +
                "        \"Tag\":\"标签\",\n" +
                "        \"SuggestPrice\":\"定价\",\n" +
                "        \"RecommendClass1\":\"评分1\",\n" +
                "        \"RecommendClass2\":\"评分2\" ,\n" +
                "        \"RecommendClass3\":\"评分3\" ,\n" +
                "        \"RecommendClass4\":\"评分4\"\n" +
                "        },\"Poster\":{\n" +
                "        \"ImageUrl\":\"海报地址\",\n" +
                "        \"Series\":\"关联集\"\n" +
                "        }\n" +
                "        }]\n" +
                "    }\n" +
                "}";

      List<LiveProgram> programList= LiveJSONAssember.toLiveProgramList(json);
      liveDao.saveAll(programList);


    }

}
