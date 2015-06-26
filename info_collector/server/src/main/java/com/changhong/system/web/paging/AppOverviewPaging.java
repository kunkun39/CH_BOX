package com.changhong.system.web.paging;

import com.changhong.system.service.AppService;
import com.changhong.system.service.UserService;
import com.changhong.system.web.facade.dto.AppDTO;
import com.changhong.system.web.facade.dto.UserDTO;

import java.util.List;

/**
 * User: Jack Wang
 * Date: 14-4-9
 * Time: 上午10:34
 */
public class AppOverviewPaging extends AbstractPaging<AppDTO> {

    private AppService appService;

    public AppOverviewPaging(AppService appService) {
        this.appService = appService;
    }

    public List<AppDTO> getItems() {
        return appService.obtainApps(getStartPosition(), getPageSize());
    }

    public long getTotalItemSize() {
        if (totalItemSize >= 0) {
            return totalItemSize;
        }
        totalItemSize = appService.obtainAppSize();
        return totalItemSize;
    }

    public String getParameterValues() {
        return "";
    }
}

