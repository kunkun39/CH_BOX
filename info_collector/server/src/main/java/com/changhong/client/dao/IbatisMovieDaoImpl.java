package com.changhong.client.dao;

import com.changhong.common.utils.WebUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Jack Wang
 * Date: 15-3-1
 * Time: 下午2:54
 */
@Repository("ibatisMovieDao")
public class IbatisMovieDaoImpl extends IbatisEntityObjectDao implements IbatisMovieDao {

    public String obtainColumns() {
        List<HashMap> columns = getSqlMapClientTemplate().queryForList("Movie.selectAllColumns");

        Map<String, String> values = new HashMap<String, String>();
        values.put("typeId", "111111");
        values.put("typeName", "Hello World");
        values.put("typeSequence", "657");
        values.put("dramaTypeId", "56");
        getSqlMapClientTemplate().insert("Movie.insertMovieType", values);

        List<HashMap> types = getSqlMapClientTemplate().queryForList("Movie.selectAllMovieTypes");

        return null;
    }
}
