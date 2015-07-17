package com.changhong.tvhelper.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.DataSetObserver;
import android.widget.Adapter;

import com.baidu.cyberplayer.utils.p;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.tvhelper.domain.Program;
import com.changhong.tvhelper.service.ChannelService;

public class CollectionUtil extends DataSetObserver{
	
	private List<Program> allShouChangChannel = new ArrayList<Program>();
	
	Context context = null;
	ChannelService channelService = null;
	Adapter adapter = null;
	
	CollectionUtil(Context context)
	{
		this.context = context;
	}
	
	public void setNotifyAdapter(Adapter adapter)
	{
		this.adapter = adapter;
	}
	
	void init()
	{
		channelService = new ChannelService(context);				
		LoadCollections();        
	}
	
	public void onChanged()
	{
		super.onChanged();
	}
	
	public void onInvalidated()
	{
		super.onInvalidated();
	}
	
	public List<Program> getList()
	{
		return allShouChangChannel;
	}
	
	public boolean addItem(String channelId)
	{
		
		if(channelId == null
				|| channelId.isEmpty())
			return false;
		
		if(channelService.channelShouCang(channelId))
		{
			AddCollection(channelId);
			return true;
		}
		
		return false;
		
	}
	public boolean removeItem(String channelId)
	{
		boolean isSuccess = true;
		
		if(channelId == null
				|| channelId.isEmpty())
			return false;
		
		isSuccess =  channelService.cancelChannelShouCang(channelId);
		if (isSuccess) {
			for(Program program :allShouChangChannel)
			{
				if (program.getChannelIndex() == channelId) {
					allShouChangChannel.remove(program);
					onChanged();
					break;
				}				
			}			
		}	
		return false;
	}
	
	
	public boolean removeItem(Program program)
	{
		boolean isSuccess = true;
		while(true)
		{
			if(program == null)
			{
				isSuccess = false;
				break;
			}
			
			isSuccess = removeItem(program.getChannelIndex());					
		}	
		return isSuccess;
	}
	
	private void LoadCollections()
	{
		List<String> collectionList = channelService.getAllChannelShouCangs();
		
		for(String collection : collectionList)
		{
			AddCollection(collection);
		}   
		onChanged();
	}
	
	private void AddCollection(String channelId)
	{		
		
		int channelSize = ClientSendCommandService.channelData.size();

        for (int i = 0; i < channelSize; i++) {
            Map<String, Object> map = ClientSendCommandService.channelData.get(i);
            String channelServiceId = (String) map.get("service_id");
            if (channelId.equals(channelServiceId)) {
            	allShouChangChannel.add(CommonUtil.RawDataToProgram(map));
            }
        }        
	}
}
