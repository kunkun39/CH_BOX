package com.changhong.common.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Map.Entry;

import android.text.StaticLayout;
import android.text.format.Time;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.utils.DateUtils;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.utils.StringUtils;

public class IpSelectorDataServer extends Observable
{
	/**
	 * This
	 */
	private static IpSelectorDataServer dataServer = null;
	
	/**
	 * Static Final String
	 */
	private static final String DEFAULT_IP_NAME = NetworkUtils.BOX_DEFAULT_NAME;
	private static final String DEFAULT_LINKLESSNESS = "未连接";
	private static final int  DELAY_TIME = 5000;
	
	/**
	 * IP & Name Container
	 */
	private Map<String, String> mServerIpListMap = new LinkedHashMap<String, String>();
	
	/**
	 * IP & Time Container
	 */
	private Map<String, Long> mServerIPListLiveTime = new HashMap<String, Long>();
	private String mServerIP = null;
	
	/** ==================================================================================================
	 * Singleton
	 */
	private IpSelectorDataServer(){};
	
	public static synchronized IpSelectorDataServer getInstance() {
		if (dataServer == null) {
			dataServer = new IpSelectorDataServer();
		}
		return dataServer;
	}
	/** =================================================================================================
	 * 
	 * Ip List Controller
	 */
	
	public final Collection<String> getIpList(){		
		return mServerIpListMap.keySet();
	}
	
	public synchronized void addIp(String ip) {
		if (ip == null|| ip.length() < 1) {
			return;
		}
		
		addIp(ip, DEFAULT_IP_NAME);
	}
	
	public synchronized void addIp(String ip,String name) {
		if (ip == null|| ip.length() < 1 ) {
			return;
		}
		
		if (name == null || name.length() < 1) {
			name = DEFAULT_IP_NAME;
		}
		
		mServerIpListMap.put(ip, name);
		activateIp(ip);
		setDefaultIp();
		setChanged();
		notifyObservers();
	}
	
	public synchronized void modifyName(String ip,String name) {
		if (ip == null || ip.length() < 1) {
			return;
		}
		
		if(mServerIpListMap.containsKey(ip)) {
			if(!mServerIpListMap.get(ip).equals(name)) {
				mServerIpListMap.put(ip, name);
				setChanged();
				notifyObservers();
			}			
		}			
	}
	
	public synchronized void removeIp(String ip) {
		if (ip == null|| ip.length() < 1) {
			return;
		}
		
		if(mServerIpListMap.containsKey(ip)){			
			mServerIpListMap.remove(ip);
			removeActivateIp(ip);
			if (mServerIP.equals(ip)) {
				mServerIP =  (mServerIpListMap.size() > 0? setDefaultIp() : null);
			}
			setChanged();
			notifyObservers();
		}
	}
	
	/** =================================================================================================
	 * 
	 * Current IP Controller
	 */
	public synchronized void setCurrentIp(String ip) {
		if (ip == null|| ip.length() < 1) {
			return;
		}
		if (ip.equalsIgnoreCase(mServerIP)) {
			return ;
		}
				
		if (mServerIpListMap.containsKey(ip)) {
			mServerIP = ip;
			ClientSendCommandService.handler.sendEmptyMessage(2);
			setChanged();
			notifyObservers();
		}		
	}
	
	public final String getCurrentIp() {
		return mServerIP;
	}
	
	private final String setDefaultIp() {
		if (mServerIpListMap.size() > 0) {
			setCurrentIp((String) mServerIpListMap.keySet().toArray()[0]);			
		}
		else {
			clear();
		}
		return mServerIP;
	}
	
	/** =================================================================================================
	 * 
	 * Current IP Name Controller
	 */
	public final String getName() {			
		return getName(mServerIP);
	}
	
	public final String getName(String ip) {
		if (ip == null|| ip.length() < 1) {
			return DEFAULT_LINKLESSNESS;
		}
		if (mServerIpListMap.size() < 1) {
			return DEFAULT_LINKLESSNESS;
		}
		
		String name = mServerIpListMap.get(ip);
		if (!StringUtils.hasLength(name)) {
			return DEFAULT_IP_NAME;
		}
		
		return name;
		
	}
	
	public void clear() {
		mServerIpListMap.clear();
		mServerIPListLiveTime.clear();
		mServerIP = null;
	}
	
	/** =================================================================================================
	 * 
	 * Current IP & Time 
	 */
	// Refresh Time
	public void activateIp(String ip){
		if (ip == null|| ip.length() < 1) {
			return ;
		}
		
		mServerIPListLiveTime.put(ip, System.currentTimeMillis());
	}
	
	public void removeActivateIp(String ip){
		if (ip == null|| ip.length() < 1) {
			return ;
		}
		mServerIPListLiveTime.remove(ip);
	}
	
	public synchronized void removeIpOutOfTime(){
		List<String> ipList = new ArrayList<String>();
		Long  timeCurrent = System.currentTimeMillis();
		for (Entry<String, Long> item : mServerIPListLiveTime.entrySet()) {
			if(timeCurrent - item.getValue() > DELAY_TIME)
			{
				ipList.add(item.getKey());
			}
		}
		
		for (String item : ipList) {
			removeIp(item);			
		}
	}
	
	public Long getIPTime(String ip){
		if (ip == null|| ip.length() < 1) {
			return 0L;
		}
		
		return mServerIPListLiveTime.get(ip);
	}		

}
