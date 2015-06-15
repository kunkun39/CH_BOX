package com.changhong.system.web.facade.assember;

import com.changhong.common.utils.CHStringUtils;
import com.changhong.system.domain.FeedBack;
import com.changhong.system.domain.User;
import com.changhong.system.web.facade.dto.FeedBackDTO;
import com.changhong.system.web.facade.dto.UserDTO;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by kerio on 2015/6/12.
 */
public class FeedBackWebAssember {
    public static FeedBackDTO toFeedBackDTO(FeedBack feedBack, Map<String,String>map) {

         int id = feedBack.getId();
         String content = feedBack.getContent();
         Date dateTime=feedBack.getTimestamp();

         String usermac=feedBack.getUsermac();
         String username= map.get(usermac);
         String  status=feedBack.getStatus();

         FeedBackDTO dto =  new FeedBackDTO(id,content,username,usermac,status,dateTime);

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
