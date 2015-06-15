package com.changhong.system.repository;

import com.changhong.common.repository.HibernateEntityObjectDao;
import com.changhong.common.utils.CHDateUtils;
import com.changhong.system.domain.ClientUser;
import com.changhong.system.domain.FeedBack;
import com.changhong.system.domain.User;
import org.hibernate.Query;
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
    public JSONArray obtainCollectorInfoByMonth(String status, String year, String month) throws JSONException {
        Map<Integer, Integer> statistic = new HashMap<Integer, Integer>();
        JSONArray array = new JSONArray();
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        if (Integer.parseInt(month) > 0) {
            String sql = "select fd_day, count(id) as total from feedback_info " +
                    "where status = '" + status  + "' and fd_year = " + year + " and fd_month = " + month + " group by fd_day";
            SQLQuery query = session.createSQLQuery(sql);
            List list = query.list();

            int totalDays = CHDateUtils.getTotalDaysForOneMonth(Integer.parseInt(year), Integer.parseInt(month));
            for (int i = 1; i <= totalDays; i++) {
                statistic.put(i, 0);
            }

            for (Object o : list) {
                Object[] result = (Object[]) o;
                Integer day = Integer.parseInt((String) result[0]);
                BigInteger total = (BigInteger) result[1];
                statistic.put(day, total.intValue());
            }

            JSONObject json = new JSONObject();
            StringBuffer buffer = new StringBuffer();
            for (int i=1; i<= totalDays; i++) {
                buffer.append(statistic.get(i) + ",");
            }
            json.put("days", CHDateUtils.getTotalDaysForOneMonth(Integer.parseInt(year), Integer.parseInt(month)));
            json.put("total", buffer.toString().substring(0, buffer.toString().length() - 1));
            array.put(json);
        } else {
            String sql = "select fd_month, count(id) as total from feedback_info " +
                    "where status = '" + status  + "' and fd_year = " + year + " group by fd_month";
            SQLQuery query = session.createSQLQuery(sql);
            List list = query.list();

            int totalMonths = 12;
            for (int i = 1; i <= 12; i++) {
                statistic.put(i, 0);
            }

            for (Object o : list) {
                Object[] result = (Object[]) o;
                Integer day = (Integer) result[0];
                BigInteger total = (BigInteger) result[1];
                statistic.put(day, total.intValue());
            }

            JSONObject json = new JSONObject();
            StringBuffer buffer = new StringBuffer();
            for (int i=1; i<= totalMonths; i++) {
                buffer.append(statistic.get(i) + ",");
            }
            json.put("days", totalMonths);
            json.put("total", buffer.toString().substring(0, buffer.toString().length() - 1));
            array.put(json);
        }

        return array;
    }
}
