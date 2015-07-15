package com.changhong.system.web.facade.assember;

import com.changhong.system.domain.ClientLocationInfo;
import com.changhong.system.repository.FeedBackDaoImpl;
import com.changhong.system.web.facade.dto.ClientLocationInfoDTO;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by maren on 15-7-14.
 */
public class ClientLocationInfoWebAssember {
    public  static ClientLocationInfoDTO toClientLocationInfoDTO(ClientLocationInfo clientLocationInfo,HashMap<String,String> userNames){
        int id=clientLocationInfo.getId();
        String userMac=clientLocationInfo.getUserMac();
        String lon=clientLocationInfo.getLon();
        String lat=clientLocationInfo.getLat();
        String clientName=userNames.get(userMac);
        return new ClientLocationInfoDTO(clientName,id,lat,lon,userMac);
    }
    public static List<ClientLocationInfoDTO> toClientLocationInfoDTOs(List<ClientLocationInfo> clientLocationInfos,HashMap<String,String> userNames) {
        List<ClientLocationInfoDTO> locationInfoDTOList=new ArrayList<ClientLocationInfoDTO>();
        if(clientLocationInfos!=null){
            for (ClientLocationInfo clientLocationInfo : clientLocationInfos) {
                locationInfoDTOList.add(toClientLocationInfoDTO(clientLocationInfo,userNames));
            }
            return locationInfoDTOList;
        }
        return locationInfoDTOList;
    }

}
