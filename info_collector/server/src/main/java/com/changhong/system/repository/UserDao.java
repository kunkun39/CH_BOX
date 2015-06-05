package com.changhong.system.repository;

import com.changhong.common.repository.EntityObjectDao;
import com.changhong.system.domain.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * User: Jack Wang
 * Date: 14-4-9
 * Time: 上午9:25
 */
public interface UserDao extends EntityObjectDao {

    UserDetails findUserByName(String username);

    List<User> loadUsers(String name, int startPosition, int pageSize);

    int loadUserSize(String name);

    boolean loadUserExist(int userId, String username);
}
