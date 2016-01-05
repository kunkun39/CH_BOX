package com.changhong.tvserver.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.baidu.cyberplayer.utils.ca;
import com.changhong.tvserver.TVSocketControllerService.QuickSendBackClass;

import android.R.integer;
import android.content.Context;
import android.os.Environment;

public class CaVerifyUtil implements QuickSendBackClass{

	/**
	 * TAG
	 */
	public static final String TAG =  "CaVerify";
	
	/**
	 * Config
	 */
	private static final String CA_CONFIG_FILE = Environment.getDataDirectory() + "/webserver/assets/tvassit_config.cfg";
	private static final String CA_FLAG_ENABLE = "ca_verify_enable";
	private static final String CA_NUMBER_FILE = "smartcard_number_file";
	private static final String CA_FLAG_MUNBER = "Number";
	
	/**
	 * Message
	 */
	private static final String MESG_ACTION_ENABLEVERVIFY = "enable";
	private static final String MESG_ACTION_VERVIFY = "vervify";
	
	private static final boolean pass = true;
	private static final boolean failed = false;
	
	public boolean verify()
	{
		if(requestIsNeedVerify() == failed 
				&& verifyCANumber() == failed)
		{
			return failed;
		}
		return pass;
	}	
	
	
	/**
	 * 开启验证
	 */
	public void requestEnableVerify(boolean isVerify)
	{							
		replaceALineFromFile(CA_CONFIG_FILE,CA_FLAG_ENABLE,CA_FLAG_ENABLE + ":" + (isVerify ? "1" : "0"));		
	}		
	
	@Override
	public String update(String param) {
		if (param.contains(MESG_ACTION_VERVIFY)) {
			String result = (verify() == pass) ? "1":"0";
			result = packetMessage(MESG_ACTION_VERVIFY + ":" + result);
			return result;
		}else if(param.contains(MESG_ACTION_ENABLEVERVIFY))
		{
			if(param.charAt(param.length() -1) == '1')
			{
				requestEnableVerify(true);
			}else {
				requestEnableVerify(false);
			}
			return null;
		}
		return null;
	}
	
	/**
	 * 确认是否需要验证
	 */	
	private boolean requestIsNeedVerify() 
	{
		String enableString = getALineFromFile(CA_CONFIG_FILE, CA_FLAG_ENABLE);				
		
		if (enableString != null) {
			// Caution! It may be crash
			try {
				int vaule = Integer.parseInt(enableString.substring(CA_FLAG_ENABLE.length() + 1));
				if (vaule != 0) {
					return failed;								
				}	
			} catch (Exception e) {
				e.printStackTrace();
			}					
		}
		
		return pass;		
	}
	
	/**
	 * 进行验证
	 */
	private boolean verifyCANumber()
	{
		String filePath = getALineFromFile(CA_CONFIG_FILE, CA_NUMBER_FILE);	
		if (filePath == null
				|| filePath.length() <= 0) {
			return pass;
		}
		
		String pathString = filePath.substring(CA_NUMBER_FILE.length() + 1);							
		String caNumber = null;
		if((caNumber = getALineFromFile(pathString, CA_FLAG_MUNBER)) != null)
		{
			long ca = Long.parseLong(caNumber.substring(CA_FLAG_MUNBER.length() + 1, caNumber.indexOf("|")));
			if (ca  != 0) {
				return pass;
			}
			return failed;
		}
		
		return pass;
	}		
	
	
	private String packetMessage(String action)
	{
		return TAG + ":" +  action;
	}		
	
	
	private String getFirstLineFromFile(String path)
	{
		return getALineFromFile(path,null);
	}
	
	private String getALineFromFile(String path,String target)
	{
		File file = new File(path);
		String resultString = null;
		if (!file.exists()) {
			return null;
		}
		
		Reader fReader = null;
		BufferedReader bReader = null;
		
		// To find String 
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);
			String tempString;
			while ((tempString = bReader.readLine()) != null) {
				if (target == null
						|| target.length() <= 0) {
					resultString = tempString;
					break;
				}
				
				if (tempString.indexOf(target) != -1) {
					resultString = tempString;	
					break;
				}
			};			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (bReader != null) {				
				try {					
					bReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fReader != null) {				
				try {
					fReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return resultString;
	}
	
	private void replaceALineFromFile(String path,String token,String target)
	{
		File file = new File(path);
		if (!file.exists()) {
			return ;
		}
		
		Reader fReader = null;
		BufferedReader bReader = null;
		List<String> tempList = new ArrayList<String>();
		
		// To find String 
		try {
			fReader = new FileReader(file);
			bReader = new BufferedReader(fReader);
			String tempString;
			while ((tempString = bReader.readLine()) != null) {
				if (tempString.contains(token)) {
					tempString = target;
				}
				tempList.add(tempString);
			};			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (bReader != null) {				
				try {					
					bReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fReader != null) {				
				try {
					fReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		PrintWriter pWriter = null;
		try {
			pWriter = new PrintWriter(file);
			for (String str : tempList) {
				pWriter.println(str);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			if (pWriter != null) {
				pWriter.close();
			}
		}
	}

		

}
