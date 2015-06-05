package com.changhong.system.repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.changhong.common.utils.PagingUtils;
import com.changhong.system.domain.column.Column;
import com.changhong.system.domain.movie.MovieInfo;
import com.changhong.system.domain.movie.PlayInfo;
import com.changhong.system.domain.movie.Poster;
import com.changhong.system.domain.movietype.*;
import junit.framework.TestCase;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;

/**
 * User: Jack Wang
 * Date: 14-11-20
 * Time: 上午11:31
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/database.xml", "/applicationContext.xml"})
public class MovieDaoImplTest extends TestCase {

    @Resource
    SessionFactory sessionFactory;

    @Resource(name = "movieDao")
    MovieDao movieDao;

    LiveDao liveChannelDao;

    HibernateTemplate hibernateTemplate;

    @Before
    public void setUp() {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @After
    public void tearDown() {
        hibernateTemplate = null;
    }

    @Test
    public void testSaveTypeEntity() {
        Type type = new Type();
        type.setTypeID("2");
        type.setType("\u7535\u5f71");
        type.setTypeSequence(2);
        type.setDramaType("1,2,3,4,5,6,7,8,9,10,11,12,13");
        hibernateTemplate.save(type);
    }

    @Test
    public void testSaveDramaTypeEntity() {
        DramaType dramaType = new DramaType();
        dramaType.setDramaTypeID("1");
        dramaType.setDramaType("\u7535\u5f71");
        dramaType.setDramaTypeSequence(1);
        hibernateTemplate.save(dramaType);
    }

    @Test
    public void testSaveAreaEntity() {
        Area area = new Area();
        area.setAreaID("1");
        area.setArea("大陆");
        area.setAreaGroupID("1,2,3,4,5,6,7,8,9,10");
        hibernateTemplate.save(area);
    }

    @Test
    public void testSaveAreaGroupEntity() {
        AreaGroup areaGroup = new AreaGroup();
        areaGroup.setAreaGroupID("1");
        areaGroup.setAreaGroup("大陆,港台");
        hibernateTemplate.save(areaGroup);
    }

    @Test
    public void testSaveClientTypeEntity() {
        ClientType clientType = new ClientType();
        clientType.setClientTypeID("1");
        clientType.setClientType("苹果");
        hibernateTemplate.save(clientType);
    }

    @Test
    public void testSaveChannelTypeEntity() {
        ChannelType channelType = new ChannelType();
        channelType.setChannelTypeID("1");
        channelType.setChannelType("CNTV");
        hibernateTemplate.save(channelType);
    }

    @Test
    public void testSaveEventTypeEntity() {
        EventType eventType = new EventType();
        eventType.setEventTypeID("1");
        eventType.setEventType("LUCHEYE");
        hibernateTemplate.save(eventType);
    }

    @Test
    public void testSaveProviderEntity() {
        Provider provider = new Provider();
        provider.setProviderID("1");
        provider.setProviderName("CNTV");
        provider.setProviderType("OK");
        hibernateTemplate.save(provider);
    }

    @Test
    public void testSaveColumnEntity() {
        Column column = new Column();
        column.setColumnID("50046");
        column.setColumnTypeCode("mf");
        column.setType("1");
        column.setParentID("50041");
        column.setColumnName("nice");
        column.setAlias("免费点播");
        column.setDescribe("1");
        column.setDisplay("1");
        column.setRank("5");
        column.setUrl("1");
        column.setUrlType("1");
        column.setUpdateTime("Wed Jul 16 00:00:00 CST 2014");
        column.setRelevantChargeMode("0");
        column.setProductID("1048584");
        column.setResourceType("2");
        hibernateTemplate.save(column);
    }

    @Test
    public void testSaveMovieInfoEntity() {
        MovieInfo movieInfo = new MovieInfo();
        movieInfo.setMovieID("144265");
        movieInfo.setMovieName("\u6e29\u67d4\u6740\u622e");
        movieInfo.setMovieAliasName("\u975e\u6cd5\u4ea4\u6613\\/\u593a\u547d\u65e0\u58f0\\/\u67ef\u6839\u7684\u4ea4\u6613\\/\u6e29\u67d4\u5730\u6740\u6b7b\u4ed6\u4eec\\/\u6740\u622e\u884c\u52a8\\/\u6e29\u67d4\u7684\u6740\u622e\\/Killing Them Softly\\/Cogan\\\\\\'s Trade");
        movieInfo.setType("内陆");
        movieInfo.setTypeID("1");
        movieInfo.setDramaType("战争");
        movieInfo.setDramaTypeID("1,12");
        movieInfo.setArea("\u5185\u5730");
        movieInfo.setAreaID("31");
        movieInfo.setYear("2014");
        movieInfo.setDirector(";\u5b89\u5fb7\u9c81\u00b7\u591a\u7c73\u5c3c\u514b;");
        movieInfo.setActor(";\u5e03\u62c9\u5fb7\u00b7\u76ae\u7279;\u65af\u79d1\u7279\u00b7\u9ea6\u514b\u7eb3\u91cc;\u672c\u00b7\u95e8\u5fb7\u5c14\u68ee;");
        movieInfo.setHost("1");
        movieInfo.setStation("2");
        movieInfo.setAuthor("3");
        movieInfo.setRunTime("97");
        movieInfo.setCount(0);
        movieInfo.setSummaryShort("    \u5e03\u62c9\u5fb7\u00b7\u76ae\u7279\u9970\u6f14\u7684\u6770\u57fa\u00b7\u67ef\u6839\u662f\u4e00\u4f4d\u5f3a\u786c\u7684\u804c\u4e1a\u6267\u6cd5\u4eba\uff0c\u4ed6\u4ecb\u5165\u8c03\u67e5\u4e00\u6869\u62a2\u52ab\u6848\uff0c\u6848\u4ef6\u53d1\u751f\u5728\u4e00\u6b21\u7531\u9ed1\u5e2e\u4fdd\u62a4\u7684\u91cd\u91d1\u6251\u514b\u8d4c\u5c40\u4e2d\u3002");
        movieInfo.setCommentary("\u6781\u9177\u7684\u98ce\u683c\u5316");
        movieInfo.setTag("\u5341\u6708\u9884\u544a");
        movieInfo.setSuggestPrice("6");
        movieInfo.setRecommendClass1(6.1);
        movieInfo.setRecommendClass2(6.1);
        movieInfo.setRecommendClass3(6.1);
        movieInfo.setRecommendClass4(6.1);
        movieInfo.setOtherInfoArray1("4");

        PlayInfo playInfo = new PlayInfo();
        playInfo.setPlayPlatform("WEB");
        playInfo.setAssetID("11111");
        playInfo.setAssetName("111111");
        playInfo.setContentProviderID("youku");
        playInfo.setLocalEntryUID("uid");
        playInfo.setProductOfferingUID("uid");
        playInfo.setPlayUrl("http:\\/\\/v.youku.com\\/v_show\\/id_XNjIxMTU1Mzg4.html");
        playInfo.setPlayUrlID("8873125");
        playInfo.setPlaySwfUrl("http:\\/\\/player.youku.com\\/player.php\\/sid\\/XNjIxMTU1Mzg4\\/v.swf");
        playInfo.setMainCacheUrl("url");
        playInfo.setSeries("2");
        playInfo.setSinglePriceInfo("30.00");
        playInfo.setCopyRightInfo("right");
        playInfo.setVideoCodecInfo("1111");
        playInfo.setAudioCodecInfo("1111");
        playInfo.setMuxInfo("mux");
        playInfo.setRunTimeInfo("120");
        playInfo.setResolutionInfo("jfoejfoe");
        playInfo.setBitRateInfo("niejfief");
        playInfo.setOtherInfoArray2("foejfoe");
        playInfo.setContentProviderIDArray("youku,tudou");
        playInfo.setSeriesArray("jfoef");
        movieInfo.addPlayInfo(playInfo);

        Poster poster = new Poster();
        poster.setPosterID("11");
        poster.setImageUrl("http:\\/\\/v.youku.com\\/v_show\\/id_XNjIxMTU1Mzg4.html");
        poster.setAspectRatio("34");
        poster.setSeries("e");
        movieInfo.addPoster(poster);

        hibernateTemplate.save(movieInfo);
    }

    @Test
    public void testObtainMovieType() {
        Session session = hibernateTemplate.getSessionFactory().openSession();

        SQLQuery query = null;
        query = session.createSQLQuery("SELECT type_id,type_name,type_sequence,drama_type_id FROM movie_type ORDER BY type_sequence ASC");
        List list = query.list();

        JSONObject result = new JSONObject();
        JSONArray items = new JSONArray();
        for (Object loop : list) {
            JSONObject item = new JSONObject();
            Object[] values = (Object[]) loop;
            item.put("ID", values[0]);
            item.put("Name", values[1]);
            item.put("TypeSequence", values[2]);
            item.put("DramaTypeIDs", values[3]);
            items.add(item);
        }
        result.put("Type", items);
        System.out.println(result.toJSONString());
    }

    @Test
    public void testObtainMovieDatamaType() {
        Session session = hibernateTemplate.getSessionFactory().openSession();

        SQLQuery query = null;
        query = session.createSQLQuery("SELECT drama_type_id,drama_type_name,drama_type_sequence FROM movie_drama_type ORDER BY drama_type_sequence ASC");
        List list = query.list();

        JSONObject result = new JSONObject();
        JSONArray items = new JSONArray();
        for (Object loop : list) {
            JSONObject item = new JSONObject();
            Object[] values = (Object[]) loop;
            item.put("ID", values[0]);
            item.put("Name", values[1]);
            item.put("DramaTypeSequence", values[2]);
            items.add(item);
        }
        result.put("DatamaType", items);
        System.out.println(result.toJSONString());
    }

    @Test
    public void testObtainMovieArea() {
        Session session = hibernateTemplate.getSessionFactory().openSession();

        SQLQuery query = null;
        query = session.createSQLQuery("SELECT area_id,area_name,area_group_id FROM movie_area");
        List list = query.list();

        JSONObject result = new JSONObject();
        JSONArray items = new JSONArray();
        for (Object loop : list) {
            JSONObject item = new JSONObject();
            Object[] values = (Object[]) loop;
            item.put("ID", values[0]);
            item.put("Name", values[1]);
            item.put("AreaGroupIDs", values[2]);
            items.add(item);
        }
        result.put("Area", items);
        System.out.println(result.toJSONString());
    }

    @Test
    public void testObtainMovieAreaGroup() {
        Session session = hibernateTemplate.getSessionFactory().openSession();

        SQLQuery query = null;
        query = session.createSQLQuery("SELECT area_group_id,area_group_name FROM movie_area_group");
        List list = query.list();

        JSONObject result = new JSONObject();
        JSONArray items = new JSONArray();
        for (Object loop : list) {
            JSONObject item = new JSONObject();
            Object[] values = (Object[]) loop;
            item.put("ID", values[0]);
            item.put("Name", values[1]);
            items.add(item);
        }
        result.put("AreaGroup", items);
        System.out.println(result.toJSONString());
    }

    @Test
    public void testObtainMovieYear() {
        JSONObject result = new JSONObject();
        JSONArray items = new JSONArray();
        JSONObject year2014 = new JSONObject();
        year2014.put("ID", "2014");
        items.add(year2014);
        JSONObject year2013 = new JSONObject();
        year2013.put("ID", "2013");
        items.add(year2013);
        result.put("Year", items);
        System.out.println(result.toJSONString());
    }

    @Test
    public void testObtainMovieClient() {
        Session session = hibernateTemplate.getSessionFactory().openSession();

        SQLQuery query = null;
        query = session.createSQLQuery("SELECT client_type_id,client_type_name FROM movie_client_type");
        List list = query.list();

        JSONObject result = new JSONObject();
        JSONArray items = new JSONArray();
        for (Object loop : list) {
            JSONObject item = new JSONObject();
            Object[] values = (Object[]) loop;
            item.put("ID", values[0]);
            item.put("Name", values[1]);
            items.add(item);
        }
        result.put("Client", items);
        System.out.println(result.toJSONString());
    }

    @Test
    public void testObtainMovieChannel() {
        Session session = hibernateTemplate.getSessionFactory().openSession();

        SQLQuery query = null;
        query = session.createSQLQuery("SELECT channel_type_id,channel_type_name FROM movie_channel_type");
        List list = query.list();

        JSONObject result = new JSONObject();
        JSONArray items = new JSONArray();
        for (Object loop : list) {
            JSONObject item = new JSONObject();
            Object[] values = (Object[]) loop;
            item.put("ID", values[0]);
            item.put("Name", values[1]);
            items.add(item);
        }
        result.put("Channel", items);
        System.out.println(result.toJSONString());
    }

    @Test
    public void testObtainMovieEvent() {
        Session session = hibernateTemplate.getSessionFactory().openSession();

        SQLQuery query = null;
        query = session.createSQLQuery("SELECT event_type_id,event_type_name FROM movie_event_type");
        List list = query.list();

        JSONObject result = new JSONObject();
        JSONArray items = new JSONArray();
        for (Object loop : list) {
            JSONObject item = new JSONObject();
            Object[] values = (Object[]) loop;
            item.put("ID", values[0]);
            item.put("Name", values[1]);
            items.add(item);
        }
        result.put("Event", items);
        System.out.println(result.toJSONString());
    }

    @Test
    public void testFindColumns() {
        Session session = hibernateTemplate.getSessionFactory().openSession();

        /**
         * 返回的结果
         */
        JSONObject result = new JSONObject();
        JSONArray items = new JSONArray();

        /**
         * 查询并组装结果
         */
        SQLQuery query = session.createSQLQuery("SELECT column_id,column_parent_id,column_name,column_alias,column_product_id FROM movie_column ORDER BY column_rank ASC");
        List list = query.list();
        for (Object loop : list) {
            JSONObject item = new JSONObject();
            Object[] values = (Object[]) loop;
            item.put("ColumnID", values[0]);
            item.put("ParentID", values[1]);
            item.put("ColumnName", values[2]);
            item.put("Alias", values[3]);
            item.put("ProductID", values[4]);
            items.add(item);
        }
        result.put("Column", items);

        System.out.println(result.toJSONString());
    }

    @Test
    public void testFindMovies() {
        String query = "{\n" +
                "\t\"RequestParams\": {\n" +
                "\t\t\"ContentProviderID\": \"\",\n" +
                "\t\t\"ColumnID\":\"\",\n" +
                "\t\t\"TypeID\": \"1\",\n" +
                "\t\t\"DramaTypeID\": \"\",\n" +
                "\t\t\"Year\": \"2014\",\n" +
                "\t\t\"AreaID\": \"31\",\n" +
                "\t\t\"AreaGroupID\": \"\",\n" +
                "\t\t\"Page\": 1,\n" +
                "\t}\n" +
                "}";

        Session session = hibernateTemplate.getSessionFactory().openSession();
        SQLQuery sqlQuery = null;

        /**
         * 解析JSON
         */
        JSONObject queryJSON = JSONObject.parseObject(query);
        JSONObject requestParams = queryJSON.getJSONObject("RequestParams");
        String providerID = requestParams.getString("ContentProviderID");
        String columnID = requestParams.getString("ColumnID");
        String typeID = requestParams.getString("TypeID");
        String dramaTypeID = requestParams.getString("DramaTypeID");
        String year = requestParams.getString("Year");
        String areaID = requestParams.getString("AreaID");
        String areaGroupID = requestParams.getString("AreaGroupID");
        String currentPage = requestParams.getString("Page");

        /**
         * 构造分页
         */
        sqlQuery = session.createSQLQuery("select count(id) FROM movie_info");
        int numItems = ((BigInteger) sqlQuery.list().get(0)).intValue();
        PagingUtils paging = new PagingUtils(numItems);
        paging.setCurrentPage(currentPage);

        /**
         * 查询MOVIE
         */
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT movie_id,movie_name,movie_alias_name,movie_runtime,movie_suggestprice FROM movie_info WHERE");
        boolean addAnd = false;
        if (StringUtils.hasText(providerID)) {
            addAnd = true;
            builder.append(" play_contentproviderid='" + providerID + "'");
        }
        if (StringUtils.hasText(typeID)) {
            if (addAnd) {
                builder.append(" AND ");
            }
            addAnd = true;
            builder.append(" movie_type_id='" + typeID + "'");
        }
        if (StringUtils.hasText(dramaTypeID)) {
            if (addAnd) {
                builder.append(" AND ");
            }
            addAnd = true;
            builder.append(" drama_type_id='" + dramaTypeID + "'");
        }
        if (StringUtils.hasText(year)) {
            if (addAnd) {
                builder.append(" AND ");
            }
            addAnd = true;
            builder.append(" movie_year='" + year + "'");
        }
        if (StringUtils.hasText(areaID)) {
            if (addAnd) {
                builder.append(" AND ");
            }
            addAnd = true;
            builder.append(" movie_area_id='" + areaID + "'");
        }
        builder.append(" LIMIT " + paging.getStartPosition() + "," + paging.getPageItems());
        System.out.println(builder.toString());
        sqlQuery = session.createSQLQuery(builder.toString());
        List list = sqlQuery.list();

        /**
         * 处理返回的结果
         */
        JSONObject result = new JSONObject();
        JSONObject total = new JSONObject();
        total.put("TotalNumber", numItems);
        result.put("ItemNumber", total);

        JSONArray items = new JSONArray();
        for (Object loop : list) {
            JSONObject item = new JSONObject();
            Object[] values = (Object[]) loop;
            item.put("MovieID", values[0]);
            item.put("MovieName", values[1]);
            item.put("MovieAliasName", values[2]);
            item.put("RunTime", values[3]);
            item.put("SuggestPrice", values[4]);
            items.add(item);
        }
        result.put("Items", items);

        System.out.println(result.toJSONString());
    }

    @Test
    public void testFindMovieByID() {
        MovieInfo movie = (MovieInfo)hibernateTemplate.find("FROM MovieInfo m WHERE m.movieID = ?", new Object[]{"144265"}).get(0);
        JSONObject result = new JSONObject();

        /**
         * 添加MOVIEINFO信息
         */
        JSONObject movieInfo = new JSONObject();

        movieInfo.put("MovieID", movie.getMovieID());
        movieInfo.put("MovieName", movie.getMovieName());
        movieInfo.put("MovieAliasName", movie.getMovieAliasName());
        movieInfo.put("Area", movie.getArea());
        movieInfo.put("Type", movie.getType());
        movieInfo.put("DramaType", movie.getDramaType());
        movieInfo.put("Year", movie.getYear());
        movieInfo.put("Director", movie.getDirector());
        movieInfo.put("Actor", movie.getActor());
        movieInfo.put("Host", movie.getHost());
        movieInfo.put("Station", movie.getStation());
        movieInfo.put("Author", movie.getAuthor());
        movieInfo.put("RunTime", movie.getRunTime());
        movieInfo.put("Count", movie.getRunTime());
        movieInfo.put("SummaryShort", movie.getSummaryShort());
        movieInfo.put("Commentary", movie.getCommentary());
        movieInfo.put("Tag", movie.getTag());
        movieInfo.put("SuggestPrice", movie.getSuggestPrice());
        movieInfo.put("RecommendClass1", movie.getRecommendClass1());
        movieInfo.put("RecommendClass2", movie.getRecommendClass2());
        movieInfo.put("RecommendClass3", movie.getRecommendClass3());
        movieInfo.put("RecommendClass4", movie.getRecommendClass4());
        movieInfo.put("OtherInfoArray", movie.getOtherInfoArray1());
        result.put("MovieInfo", movieInfo);
        /**
         * 添加PlayInfo信息
         */
        JSONObject playInfo = new JSONObject();
        PlayInfo tvPlayInfo = movie.getTVPlayInfo();
        playInfo.put("PlayPlatform", tvPlayInfo.getPlayPlatform());
        playInfo.put("AssetID", tvPlayInfo.getAssetID());
        playInfo.put("AssetName", tvPlayInfo.getAssetName());
        playInfo.put("ContentProviderID", tvPlayInfo.getContentProviderID());
        playInfo.put("ProductOfferingUID", tvPlayInfo.getProductOfferingUID());
        playInfo.put("PlayUrl", tvPlayInfo.getPlayUrl());
        playInfo.put("PlayUrlID", tvPlayInfo.getPlayUrlID());
        playInfo.put("PlaySwfUrl", tvPlayInfo.getPlaySwfUrl());
        playInfo.put("MainCacheUrl", tvPlayInfo.getMainCacheUrl());
        playInfo.put("Series", tvPlayInfo.getSeries());
        playInfo.put("SinglePriceInfo", tvPlayInfo.getSinglePriceInfo());
        playInfo.put("CopyRightInfo", tvPlayInfo.getCopyRightInfo());
        playInfo.put("VideoCodecInfo", tvPlayInfo.getVideoCodecInfo());
        playInfo.put("AudioCodecInfo", tvPlayInfo.getAudioCodecInfo());
        playInfo.put("MuxInfo", tvPlayInfo.getMuxInfo());
        playInfo.put("RunTimeInfo", tvPlayInfo.getRunTimeInfo());
        playInfo.put("ResolutionInfo", tvPlayInfo.getResolutionInfo());
        playInfo.put("BitRateInfo", tvPlayInfo.getBitRateInfo());
        playInfo.put("OtherInfoArray", tvPlayInfo.getOtherInfoArray2());
        playInfo.put("ContentProviderIDArray", tvPlayInfo.getContentProviderIDArray());
        playInfo.put("SeriesArray", tvPlayInfo.getSeriesArray());
        result.put("PlayInfo", playInfo);

        /**
         * 添加Poster信息
         */
        JSONObject poster = new JSONObject();
        Poster tvPoster = movie.getTVPlayPoster();
        poster.put("PosterID", tvPoster.getPosterID());
        poster.put("ImageUrl", tvPoster.getImageUrl());
        poster.put("AspectRatio", tvPoster.getAspectRatio());
        poster.put("Series", tvPoster.getSeries());
        result.put("Poster", poster);

        /**
         * 返回结果
         */
        System.out.println(result.toJSONString());
    }
}
