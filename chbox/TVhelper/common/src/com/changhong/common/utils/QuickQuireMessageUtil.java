package com.changhong.common.utils;

import android.content.Context;

import com.changhong.common.service.ClientSendCommandService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QuickQuireMessageUtil {
	/**
	 * TAG
	 */
	private static final String TAG =  QuickQuireMessageUtil.class.getName();

	/**
	 * 回调
	 */
	private Map<String,OnFeedBackListener> mVerifyListeners = new HashMap<String, OnFeedBackListener>();

	/**
	 * 单例模式
	 */
	static QuickQuireMessageUtil mInstance;
	private QuickQuireMessageUtil(){}
	public static synchronized QuickQuireMessageUtil getInstance()
	{		
		if (mInstance == null) {
			mInstance = new QuickQuireMessageUtil();
		}
		return mInstance;
	}
	
	/**
	 * 设置监听
	 * @param listener 用于执行完操作返回结果。
	 */
	public void setFeedbackListener(Object owner,OnFeedBackListener listener)
	{
		mVerifyListeners.put(owner.getClass().getName(), listener);
	}
	
	/**
	 *  Socket接收端调用
	 * @param result 从服务器端返回的消息
	 */
	public void feedback(String result)
	{
		if (result == null
				|| result.indexOf(":") == -1){
			return;
		}

		String tempResult = new String(result.trim());
		String className = tempResult.substring(0,tempResult.indexOf(":"));
		OnFeedBackListener listener = mVerifyListeners.get(className);
		if (listener != null){
			listener.onFinish(this, tempResult.substring(tempResult.indexOf(";"),tempResult.length()));
		}
	}	
	
	/**
	 * 打包消息
	 * @param action 消息主体
	 * @return String 打包好的消息
	 */
	private String packetMessage(String ClassName,String action)
	{
		return ClassName + ":" +  action;
	}
	
	/**
	 * 发送消息
	 * @param action 消息主体
	 */
	public void doAction(Object context,String action)
	{
		if (action == null
				|| action.length() <= 0) {
			return;
		}
		
		ClientSendCommandService.sendMessage(packetMessage(context.getClass().getName(),action));
	}
	
	/**
	 *  外部回调监听类
	 */
	public interface OnFeedBackListener
	{
		void onFinish(QuickQuireMessageUtil vervify, Object result);
	}
}
