package com.changhong.common.utils;

import com.changhong.common.service.ClientSendCommandService;

public class CaVerifyUtil {
	/**
	 * TAG
	 */
	private static final String TAG =  "CaVerify";
	
	/**
	 * Message
	 */
	private static final String MESG_ACTION_ENABLEVERVIFY = "enable";
	private static final String MESG_ACTION_VERVIFY = "vervify";
	
	/**
	 * 回调
	 */
	private QuickQuireMessageUtil messageUtil;
	
	/**
	 * 单例模式
	 */
	static CaVerifyUtil mCaVerifyUtil;
	private CaVerifyUtil(){messageUtil = QuickQuireMessageUtil.getInstance();}
	public static synchronized CaVerifyUtil getInstance()
	{		
		if (mCaVerifyUtil == null) {
			mCaVerifyUtil = new CaVerifyUtil();
		}
		return mCaVerifyUtil;
	}	
	
	/**
	 * 开启验证
	 */
	public void requestEnableVerify(boolean isVerify)
	{
		messageUtil.doAction(this,MESG_ACTION_ENABLEVERVIFY + ":" + (isVerify ? "1" : "0"));
	}
	
	/**
	 * 进行验证
	 */
	public void requestVerify()
	{
		messageUtil.doAction(this, MESG_ACTION_VERVIFY);
	}		
	
	/**
	 * 设置监听
	 * @param listener 用于执行完操作返回结果。
	 */
	public void setFeedbackListener(Object owner,QuickQuireMessageUtil.OnFeedBackListener listener)
	{
		messageUtil.setFeedbackListener(owner,listener);
	}
}
