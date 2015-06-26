package com.changhong.system.service;

import com.changhong.system.web.facade.dto.AppDTO;
import com.changhong.system.web.facade.dto.FeedBackDTO;
import com.changhong.system.web.facade.dto.UserDTO;
import com.changhong.system.web.facade.dto.UserPasswordDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * User: Jack Wang
 * Date: 14-4-9
 * Time: 上午9:20
 */
public interface AppService {
    List<AppDTO> obtainApps(int startPosition, int pageSize);
    int obtainAppSize();
    AppDTO obtainAppById(int appId);

    void changeAppDetails(AppDTO appDTO);

}
