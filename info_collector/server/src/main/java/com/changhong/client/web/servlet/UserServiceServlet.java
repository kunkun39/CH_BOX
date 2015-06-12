package com.changhong.client.web.servlet;

import com.changhong.client.dao.IbatisFeedBackInfoDaoImpl;
import com.changhong.client.dao.IbatisMovieDao;
import com.changhong.client.dao.IbatisMovieDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by maren on 2015/6/11.
 */
@Component("userServiceServlet")
public class UserServiceServlet extends HttpServlet {

    @Autowired(required = true)
    IbatisMovieDaoImpl ibatisMovieDao;

    @Autowired(required = true)
    IbatisFeedBackInfoDaoImpl ibatisFeedBackInfoDao;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURL=req.getRequestURI();
        String responseJSON = "";
        if ("/feedBack/client/userInfoCommit".equals(requestURL)) {
            String userInfo=req.getParameter("userObject");
            ibatisFeedBackInfoDao.saveClientInfo(userInfo);
            responseJSON = "success";

        } else if ("/feedBack/client/feedInfoMessageCommit".equals(requestURL)) {
            String feedInfo=req.getParameter("feedInfoObject");
            ibatisFeedBackInfoDao.saveFeedBackInfo(feedInfo);
            responseJSON = "success";

        }   else if ("/feedBack/client/tvChannelFeedCommit".equals(requestURL)) {
            String tvChannelInfo=req.getParameter("tvChannelInfo");
            ibatisFeedBackInfoDao.saveTvChannelInfo(tvChannelInfo);
            responseJSON = "success";

        }

        //返回结果
        resp.setContentType("application/json; charset=utf-8");
        PrintWriter writer = resp.getWriter();
        writer.write(responseJSON);
        writer.flush();
        writer.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);

    }
}
