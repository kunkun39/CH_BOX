package com.changhong.system.service;

import com.changhong.system.web.facade.dto.UserDTO;
import com.changhong.system.web.facade.dto.UserPasswordDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * User: Jack Wang
 * Date: 14-4-9
 * Time: 上午9:20
 */
public interface UserService extends UserDetailsService {

    UserDTO obtainUserById(int userId, boolean addSubInfo);

    List<UserDTO> obtainUsers(String name, int startPosition, int pageSize);

    int obtainUserSize(String name);

    boolean obtainUserExist(int userId, String username);

    void changeUserDetails(UserDTO userDTO);

    void changeStatusForUser(int userId);

    UserPasswordDTO obtainUserPassword(int userId);

    boolean obtainOldPasswordRight(int userId, String oldPassword);

    void changeUserPassword(int userId, String newPassword);
}
