package com.changhong.system.web.facade.assember;

import com.changhong.common.repository.EntityLoadHolder;
import com.changhong.common.utils.CHDateUtils;
import com.changhong.system.domain.FeedBack;
import com.changhong.system.web.facade.dto.FeedBackDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by kerio on 2015/6/12.
 */
public class FeedBackWebAssember {

    public static FeedBack toFeedBackDomain(FeedBackDTO feedBackDTO) {
        FeedBack feedBack = null;
        if(feedBackDTO == null) return null;

        if (feedBackDTO.getId() > 0) {
//            feedBack = (FeedBack) EntityLoadHolder.getFeedBackDao().findById(feedBackDTO.getId(), FeedBack.class);
            feedBack=new FeedBack();
            feedBack.setId(feedBackDTO.getId());
            feedBack.setReply(feedBackDTO.getReply());
            feedBack.setContent(feedBackDTO.getContent());
            feedBack.setUsermac(feedBackDTO.getUsermac());
            feedBack.setStatus("1");
            feedBack.setYear(CHDateUtils.getCurrentYear()+"");
            feedBack.setMonth(CHDateUtils.getCurrentMonth()+"");
            feedBack.setDay(CHDateUtils.getCurrentDate()+"");

        } else {
            feedBack = new FeedBack(feedBackDTO.getContent(),feedBackDTO.getReply(),"1",feedBackDTO.getUsermac());
        }
        return feedBack;
    }
    public static FeedBackDTO toFeedBackDTO(FeedBack feedBack, Map<String,String>map) {

         int id = feedBack.getId();
         String content = feedBack.getContent();
         Date dateTime=feedBack.getTimestamp();

         String usermac=feedBack.getUsermac();
         String username= map.get(usermac);
         String  status=feedBack.getStatus();
         String reply=feedBack.getReply();


         FeedBackDTO dto =  new FeedBackDTO(id,content,reply,username,usermac,status,dateTime);

        return dto;
    }

    public static List<FeedBackDTO> toFeedBackDTOList(List<FeedBack> feedBacks,Map<String,String>map) {
        List<FeedBackDTO> dtos = new ArrayList<FeedBackDTO>();
        if (feedBacks != null) {
            for (FeedBack feedBack : feedBacks) {
                dtos.add(toFeedBackDTO(feedBack, map));
            }
            return dtos;
        }
        return dtos;
    }
}
