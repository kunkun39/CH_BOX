package com.changhong.system.repository;

import com.changhong.common.repository.HibernateEntityObjectDao;
import com.changhong.system.domain.App;
import com.changhong.system.web.facade.dto.AppDTO;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by kerio on 2015/6/26.
 */
@Repository("appDao")
public class AppDaoImpl extends HibernateEntityObjectDao implements AppDao {

    @Override
    public List<App> obtainApps(int startPosition, int pageSize) {
        StringBuilder builder = new StringBuilder();
        builder.append("from App a order by a.id asc");

        org.hibernate.Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query query = session.createQuery(builder.toString());
        query.setMaxResults(pageSize);
        query.setFirstResult(startPosition);

        List<App> apps = query.list();
        return apps;
    }

    @Override
    public int obtainAppSize() {
        StringBuilder builder = new StringBuilder();
        builder.append("select count(a.id) from App a");

        List list =  getHibernateTemplate().find(builder.toString());
        return ((Long)list.get(0)).intValue();
    }

}
