package com.changhong.system.repository;

import com.changhong.common.repository.HibernateEntityObjectDao;
import com.changhong.common.utils.CHDateUtils;
import org.hibernate.SQLQuery;
import org.hibernate.classic.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

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
    public JSONArray obtainFeedBackInfoByMonth(String status, int year, int month) throws JSONException {
        Map<Integer, Integer> statistic = new HashMap<Integer, Integer>();
        JSONArray array = new JSONArray();
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        if (month > 0) {
            String sql = "select fd_day, count(id) as total from feedback_info " +
                    "where status = '" + status  + "' and fd_year = " + year + " and fd_month = " + month + " group by fd_day";
            SQLQuery query = session.createSQLQuery(sql);
            List list = query.list();

            int totalDays = CHDateUtils.getTotalDaysForOneMonth(year, month);
            for (int i = 1; i <= totalDays; i++) {
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
            for (int i=1; i<= totalDays; i++) {
                buffer.append(statistic.get(i) + ",");
            }
            json.put("days", CHDateUtils.getTotalDaysForOneMonth(year, month));
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
