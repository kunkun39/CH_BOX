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
	private OnFeedBackListener mVerifyListener = null;
	
	/**
	 * 单例模式
	 */
	static CaVerifyUtil mCaVerifyUtil;
	private CaVerifyUtil(){}
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
		doAction(MESG_ACTION_ENABLEVERVIFY + ":" + (isVerify ? "1" : "0"));
	}
	
	/**
	 * 进行验证
	 */
	public void requestVerify()
	{
		doAction(MESG_ACTION_VERVIFY);
	}		
	
	/**
	 * 设置监听
	 * @param listener 用于执行完操作返回结果。
	 */
	public void setFeedbackListener(OnFeedBackListener listener)
	{
		mVerifyListener = listener;
	}
	
	/**
	 *  Socket接收端调用
	 * @param result 从服务器端返回的消息
	 */
	public void feedback(String result)
	{
		String tempResult = new String(result.trim());
		if(tempResult.contains(MESG_ACTION_VERVIFY)){
			boolean isSuccess = tempResult.charAt(tempResult.length()- 1) == '1' ? true : false;
			if (mVerifyListener != null) {
				mVerifyListener.onVerifyFinish(this,isSuccess);
			}
		}
		
	}	
	
	/**
	 * 打包消息
	 * @param action 消息主体
	 * @return String 打包好的消息
	 */
	private String packetMessage(String action)
	{
		return TAG + ":" +  action;
	}
	
	/**
	 * 发送消息
	 * @param action 消息主体
	 */
	private void doAction(String action)
	{
		if (action == null
				|| action.length() <= 0) {
			return;
		}
		
		ClientSendCommandService.sendMessage(packetMessage(action));
	}
	
	/**
	 *  外部回调监听类
	 */
	public interface OnFeedBackListener
	{
		void onVerifyFinish(CaVerifyUtil vervify,boolean isSuccess);
	}
}
