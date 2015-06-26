package com.changhong.common.repository;

import com.changhong.system.repository.FeedBackDao;
import com.changhong.system.repository.UserDao;

/**
 * User: Jack Wang
 * Date: 14-4-9
 * Time: 下午1:21
 */
public class EntityLoadHolder {

    private static UserDao userDao;
    private static FeedBackDao feedBackDao;

    public static FeedBackDao getFeedBackDao() {
        return feedBackDao;
    }

    public  void setFeedBackDao(FeedBackDao feedBackDao) {
        EntityLoadHolder.feedBackDao = feedBackDao;
    }

    public static UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
    	EntityLoadHolder.userDao = userDao;
    }

}
