package com.changhong.system.web.facade.assember;

import com.changhong.common.repository.EntityLoadHolder;
import com.changhong.system.domain.User;
import com.changhong.system.web.facade.dto.UserDTO;
import com.changhong.system.web.facade.dto.UserPasswordDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Jack Wang
 * Date: 14-4-9
 * Time: 上午10:58
 */
public class UserWebAssember {

    public static User toUserDomain(UserDTO userDTO) {
        User user = null;
        if(userDTO == null) return null;

        if (userDTO.getId() > 0) {
            user = (User) EntityLoadHolder.getUserDao().findById(userDTO.getId(), User.class);
            user.setId(userDTO.getId());
            user.setName(userDTO.getName());
            user.setUsername(userDTO.getUsername());
            user.setPassword(userDTO.getPassword());
            user.setContactWay(userDTO.getContactWay());

        } else {
            user = new User(userDTO.getName(), userDTO.getContactWay(), userDTO.getUsername(), userDTO.getPassword());
        }
        return user;
    }

    public static UserDTO toUserDTO(User user, boolean addSubInfo) {
        final int id = user.getId();
        final String name = user.getName();
        final String contactWay = user.getContactWay();
        final String username = user.getUsername();
        final String password = user.getPassword();
        final boolean enabled = user.isEnabled();

        UserDTO dto =  new UserDTO(id, name, contactWay, username, password, enabled);
        return dto;
    }

    public static List<UserDTO> toUserDTOList(List<User> users) {
        List<UserDTO> dtos = new ArrayList<UserDTO>();
        if (users != null) {
            for (User user : users) {
                dtos.add(toUserDTO(user, false));
            }
        }
        return dtos;
    }

    public static UserPasswordDTO toPasswordDTO(User user) {
        final int userId = user.getId();
        final String name = user.getName();
        final String userName = user.getName();
        return new UserPasswordDTO(userId, name, userName);
    }
}
