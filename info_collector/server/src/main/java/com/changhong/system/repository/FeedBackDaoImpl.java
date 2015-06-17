package com.changhong.system.repository;

import com.changhong.common.repository.HibernateEntityObjectDao;
import com.changhong.common.utils.CHDateUtils;
import com.changhong.system.domain.TvChannelInfo;
import org.hibernate.Query;
import com.changhong.system.domain.ClientUser;
import com.changhong.system.domain.FeedBack;
import org.hibernate.SQLQuery;
import org.hibernate.classic.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by kerio on 2015/6/9.
 */
@Repository("feedBackDao")
public class FeedBackDaoImpl extends HibernateEntityObjectDao implements FeedBackDao {

    @Override
    public List<FeedBack> loadUserFeedBacks(int startPosition, int pageSize) {
        StringBuilder builder = new StringBuilder();
        builder.append("from FeedBack fb order by fb.id asc");

        org.hibernate.Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query query = session.createQuery(builder.toString());
        query.setMaxResults(pageSize);
        query.setFirstResult(startPosition);

        List<FeedBack> feedBacks = query.list();
        return feedBacks;
    }

    @Override
    public int loadUserFeedBackSize() {
        StringBuilder builder = new StringBuilder();
        builder.append("select count(fb.id) from FeedBack fb");

        List list =  getHibernateTemplate().find(builder.toString());
        return ((Long)list.get(0)).intValue();
    }

    @Override
    public String obtainUsernameByMac(String mac) {
        List<ClientUser>users=getHibernateTemplate().find("from ClientUser u where u.mac = ?", mac);
        return users.isEmpty() ? null : users.get(0).getName();
    }

    @Override
    public JSONArray obtainFeedBackInfoByMonth(String status, String year, String month) throws JSONException {
        Map<String, Integer> statistic = new HashMap<String, Integer>();
        JSONArray array = new JSONArray();
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        if (Integer.parseInt(month) > 0) {
            String sql = "select fd_day, count(id) as total from feedback_info " +
                    "where status = '" + status  + "' and fd_year = '" + year + "' and fd_month = '" + month + " 'group by fd_day";
            SQLQuery query = session.createSQLQuery(sql);
            List list = query.list();

            int totalDays = CHDateUtils.getTotalDaysForOneMonth(Integer.parseInt(year), Integer.parseInt(month));
            for (int i = 1; i <= totalDays; i++) {
                statistic.put(i+"", 0);
            }

            for (Object o : list) {
                Object[] result = (Object[]) o;
                String day = result[0].toString();
                BigInteger total = (BigInteger) result[1];
                statistic.put(day, total.intValue());
            }

            JSONObject json = new JSONObject();
            StringBuffer buffer = new StringBuffer();
            for (int i=1; i<= totalDays; i++) {
                buffer.append(statistic.get(i+"") + ",");
            }
            json.put("days", CHDateUtils.getTotalDaysForOneMonth(Integer.parseInt(year), Integer.parseInt(month)));
            json.put("total", buffer.toString().substring(0, buffer.toString().length() - 1));
            array.put(json);
        } else {
            String sql = "select fd_month, count(id) as total from feedback_info " +
                    "where status = '" + status  + "' and fd_year ='" + year + "'group by fd_month";
            SQLQuery query = session.createSQLQuery(sql);
            List list = query.list();

            int totalMonths = 12;
            for (int i = 1; i <= 12; i++) {
                statistic.put(i+"", 0);
            }

            for (Object o : list) {
                Object[] result = (Object[]) o;
                String day = result[0].toString();
                BigInteger total = (BigInteger) result[1];
                statistic.put(day, total.intValue());
            }

            JSONObject json = new JSONObject();
            StringBuffer buffer = new StringBuffer();
            for (int i=1; i<= totalMonths; i++) {
                buffer.append(statistic.get(i+"") + ",");
            }
            json.put("days", totalMonths);
            json.put("total", buffer.toString().substring(0, buffer.toString().length() - 1));
            array.put(json);
        }

        return array;
    }

    @Override
    public JSONArray obtainCollectorInfoAmountByProgram(String tvChannelName, String year, String month) throws JSONException {
        Map<Integer, Integer> statistic = new HashMap<Integer, Integer>();
        JSONArray array = new JSONArray();
        StringBuffer buffer = new StringBuffer();
        StringBuffer bufferToal = new StringBuffer();
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        if (Integer.parseInt(month) > 0) {
            String sql = "select  count(id) as total ,tv_program_name from tv_channel_info " +
                    "where tv_channel_name = '" + tvChannelName  + "' and fd_year = " + year + " and fd_month = " + month + " group by tv_program_name";
            SQLQuery query = session.createSQLQuery(sql);
            query.setMaxResults(10);
            query.setFirstResult(0);
            List list = query.list();
            if(list!=null&&list.size()>0){
                for (Object o : list) {
                    Object[] result = (Object[]) o;
                    BigInteger total = (BigInteger) result[0];
                    String programName=result[1].toString();
                    buffer.append(programName+",");
                    bufferToal.append(total+",");
                }
            }else{
                buffer.append("CCTV-1"+",");
                bufferToal.append(0+",");
            }


            JSONObject json = new JSONObject();

            json.put("total",bufferToal.toString().substring(0,bufferToal.toString().length()-1) );
            json.put("series", buffer.toString().substring(0, buffer.toString().length() - 1));
            array.put(json);
        } else {
            String sql = "select  count(id) as total ,tv_program_name from tv_channel_info " +
                    "where tv_channel_name = '" + tvChannelName  + "' and fd_year = " + year + " group by tv_program_name";
            SQLQuery query = session.createSQLQuery(sql);
            List list = query.list();
            if(list!=null&&list.size()>0) {
                for (Object o : list) {
                    Object[] result = (Object[]) o;
                    BigInteger total = (BigInteger) result[0];
                    String programName = result[1].toString();
                    buffer.append(programName + ",");
                    bufferToal.append(total + ",");
                }
            }else{
                buffer.append("CCTV-1"+",");
                bufferToal.append(0+",");
            }

            JSONObject json = new JSONObject();

            json.put("total",bufferToal.toString().substring(0,bufferToal.toString().length()-1) );
            json.put("series", buffer.toString().substring(0, buffer.toString().length() - 1));
            array.put(json);
        }

        return array;
    }

    @Override
    public List<TvChannelInfo> obtainAllTvChannelInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append("from TvChannelInfo u group by u.tvChannelName");
        org.hibernate.Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query query = session.createQuery(builder.toString());
//        String sql="select * from tv_channel_info u where u.id in (select Max(id) from tv_channel_info group by tv_channel_name)";
//        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//        SQLQuery query = session.createSQLQuery(sql);


        List<TvChannelInfo> tvChannelInfos = query.list();
        return tvChannelInfos;
    }

    @Override
    public List<TvChannelInfo> obtainAllTvChannelInfo(int startPosition,int pageSize,String name) {
        StringBuilder builder = new StringBuilder();
        builder.append("from TvChannelInfo u");
        if (StringUtils.hasText(name)) {
            builder.append(" where u.tv_channel_name like '" + name +"%'" );
        }
        org.hibernate.Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query query = session.createQuery(builder.toString());
        query.setMaxResults(pageSize);
        query.setFirstResult(startPosition);

        List<TvChannelInfo> tvChannelInfos = query.list();
        return tvChannelInfos;
    }

    @Override
    public int loadAllTvChannelInfoSize() {
        StringBuilder builder = new StringBuilder();
        builder.append("select count(u.id) from TvChannelInfo u");
        List list =  getHibernateTemplate().find(builder.toString());
        return ((Long)list.get(0)).intValue();
    }
}
