package com.changhong.system.repository;

import com.changhong.common.repository.EntityObjectDao;
import com.changhong.system.domain.App;
import com.changhong.system.web.facade.dto.AppDTO;

import java.util.List;

/**
 * Created by kerio on 2015/6/26.
 */
public interface AppDao extends EntityObjectDao {
    List<App> obtainApps(int startPosition, int pageSize);
    int obtainAppSize();

}
