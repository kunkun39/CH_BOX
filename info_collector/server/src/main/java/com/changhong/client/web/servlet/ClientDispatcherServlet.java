package com.changhong.client.web.servlet;


import com.changhong.client.dao.IbatisFeedBackInfoDao;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * User: Jack Wang
 * Date: 14-11-21
 * Time: 下午3:00
 */

public class ClientDispatcherServlet extends HttpServlet {

    private IbatisFeedBackInfoDao ibatisFeedBackInfoDao;



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURL = request.getRequestURI();

        String responseJSON = "";

        if ("/feedBack/client/userInfoCommit".equals(requestURL)) {
            String userInfo=request.getParameter("userObject");
            ibatisFeedBackInfoDao.saveClientInfo(userInfo);
            responseJSON = "success";


        } else if ("/feedBack/client/feedInfoMessageCommit".equals(requestURL)) {
            String feedInfo=request.getParameter("feedInfoObject");
            ibatisFeedBackInfoDao.saveFeedBackInfo(feedInfo);
            responseJSON = "success";

        }   else if ("/feedBack/client/tvChannelFeedCommit".equals(requestURL)) {
            String tvChannelInfo=request.getParameter("tvChannelInfo");
            ibatisFeedBackInfoDao.saveTvChannelInfo(tvChannelInfo);
            responseJSON = "success";

        } else if ("/feedBack/client/channelFeedInfoCommit".equals(requestURL)) {
            /**
            String channelFeedInfo=request.getParameter("channelFeedInfo");
            List<FeedChannelInfo> channelInfos=new ArrayList<FeedChannelInfo>();
            JSONObject jsonObject= JSON.parseObject(channelFeedInfo);
            JSONObject channelInfoJson=jsonObject.getJSONObject("Hash");
            FeedChannelService feedChannelService=new FeedChannelService();
            List<FeedChannelInfo> feedChannelInfoList=new ArrayList<FeedChannelInfo>();
            feedChannelService.initChannels();
            //把点击次数大于0的频道存起来
            for(String feedChannel:feedChannelService.feedChannels){
                if(Integer.parseInt(channelInfoJson.getString(feedChannel))>0){
                    FeedChannelInfo feedChannelInfo=new FeedChannelInfo();
                    feedChannelInfo.setChannelName(feedChannel);
                    feedChannelInfo.setCount(Integer.parseInt(channelInfoJson.getString(feedChannel)));
                    feedChannelInfoList.add(feedChannelInfo);
                }

            }

            String threeDchannel=channelInfoJson.getString("3D频道");

            ibatisFeedBackInfoDao.saveFeedBackInfo(channelFeedInfo);

            responseJSON = "success";
             */

        }



        //返回结果
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(responseJSON);
        writer.flush();
        writer.close();
    }
}
