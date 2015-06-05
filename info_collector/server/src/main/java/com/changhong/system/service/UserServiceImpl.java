package com.changhong.system.service;

import com.changhong.system.domain.User;
import com.changhong.system.repository.UserDao;
import com.changhong.system.web.facade.dto.UserDTO;
import com.changhong.system.web.facade.assember.*;
import com.changhong.system.web.facade.dto.UserPasswordDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: Jack Wang
 * Date: 14-4-9
 * Time: 上午9:20
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        return userDao.findUserByName(username);
    }

    public UserDTO obtainUserById(int userId, boolean addSubInfo) {
        User user = (User) userDao.findById(userId, User.class);
        return UserWebAssember.toUserDTO(user, addSubInfo);
    }

    public List<UserDTO> obtainUsers(String name, int startPosition, int pageSize) {
        List<User> users = userDao.loadUsers(name, startPosition, pageSize);
        return UserWebAssember.toUserDTOList(users);
    }

    public int obtainUserSize(String name) {
        return userDao.loadUserSize(name);
    }

    public boolean obtainUserExist(int id, String username) {
        return userDao.loadUserExist(id, username);
    }

    public void changeUserDetails(UserDTO userDTO) {
        User user = UserWebAssember.toUserDomain(userDTO);
        userDao.persist(user);
    }

    public void changeStatusForUser(int userId) {
        User user = (User) userDao.findById(userId, User.class);
        if (user.isEnabled()) {
            user.setEnabled(false);
        } else {
            user.setEnabled(true);
        }
    }

    public UserPasswordDTO obtainUserPassword(int userId) {
        User user = (User) userDao.findById(userId, User.class);
        return UserWebAssember.toPasswordDTO(user);
    }

    public boolean obtainOldPasswordRight(int userId, String oldPassword) {
        User user = (User) userDao.findById(userId, User.class);
        return user.getPassword().equals(oldPassword) ? true : false;
    }

    public void changeUserPassword(int userId, String newPassword) {
        User user = (User) userDao.findById(userId, User.class);
        user.setPassword(newPassword);
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
