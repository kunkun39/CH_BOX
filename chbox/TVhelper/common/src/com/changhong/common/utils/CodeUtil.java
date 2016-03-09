package com.changhong.common.utils;

import java.util.ArrayList;

import android.content.Context;
import android.widget.Toast;

public class CodeUtil {
	
	/**
	 * Keys
	 */
	private static final String KEY_UP = "key:up";
	private static final String KEY_DOWN = "key:down";
	private static final String KEY_LEFT = "key:left";
	private static final String KEY_RIGHT = "key:right";
	private static final String KEY_CENTER = "key:ok";
	
	private static final String KEY_VOL_UP = "key:volumeup";
	private static final String KEY_VOL_LOW = "key:volumedown";		
	
	/**
	 * Codes
	 */
	private static final String[] CODE_OPEN_CA_VERIFY = {
		KEY_UP,
		KEY_UP,
		KEY_DOWN,
		KEY_DOWN,
		KEY_LEFT,
		KEY_RIGHT,
		KEY_LEFT,
		KEY_RIGHT,
		
		KEY_VOL_UP,
		KEY_VOL_LOW,
		KEY_VOL_UP,
		KEY_VOL_LOW,
		KEY_CENTER
		};
	
	private static final String[] CODE_CLOSE_CA_VERIFY = {
		KEY_UP,
		KEY_UP,
		KEY_DOWN,
		KEY_DOWN,
		KEY_LEFT,
		KEY_RIGHT,
		KEY_LEFT,
		KEY_RIGHT,
		
		KEY_VOL_UP,
		KEY_VOL_UP,
		KEY_VOL_LOW,		
		KEY_VOL_LOW,
		KEY_CENTER
		};
	
	/**
	 * Parameters
	 */
	private int mConfirmTimes = 0; // 记录轮询次数
	private ArrayList<String[]> mConfirmCodes = new ArrayList<String[]>(); //保存正确的值 
	private Context mContext = null;
	public CodeUtil()
	{
		// 初始化
		init();
	}
	
	public void setContext(Context context)
	{
		mContext = context;
	}
	
	/**
	 * 解析密码
	 */
	public void parseCode(String code)
	{
		if (code == null
				|| code.length() <= 0) {
			return;
		}		
		
		ArrayList<String[]> tempConfirmCodes = new ArrayList<String[]>();
		for (String[] aCode : mConfirmCodes) {
			
			// 当mConfirmTimes和密码长度相同，就查看密码正确性，如果正确就执行操作
			if (aCode.length == mConfirmTimes + 1) {				
				if (aCode[mConfirmTimes].equalsIgnoreCase(code)) {
					parseSuccess(aCode);
					init();
					return ;
				}
				else {
					tempConfirmCodes.add(aCode);
					continue ;
				}
			}
			
			//当密码不足长度时，对照对应的位，如果相同就继续，不相同就保存到temp中等待删除
			if (!aCode[mConfirmTimes].equalsIgnoreCase(code)) {
				tempConfirmCodes.add(aCode);
				continue;
			}
		}
		// 删除temp中等待删除的对象
		mConfirmCodes.removeAll(tempConfirmCodes);
		mConfirmTimes++;
		
		if (code.equalsIgnoreCase(KEY_CENTER)) {
			init();
		}
	}
	
	/**
	 *  初始化函数
	 */
	private void init()
	{
		mConfirmTimes = 0;
		mConfirmCodes.clear();
		mConfirmCodes.add(CODE_OPEN_CA_VERIFY);
		mConfirmCodes.add(CODE_CLOSE_CA_VERIFY);		
	}
	
	/**
	 * 解析成功
	 */
	private void parseSuccess(Object codes)
	{
		if (codes.equals(CODE_OPEN_CA_VERIFY)) {
			// 打开CA验证
			CaVerifyUtil.getInstance().requestEnableVerify(true);
			showToast("打开CA验证");
		}else if(codes.equals(CODE_CLOSE_CA_VERIFY)){
			// 关闭CA验证
			CaVerifyUtil.getInstance().requestEnableVerify(false);
			showToast("关闭CA验证");
		}
		
	}
	
	/**
	 * 显示提示
	 */
	private void showToast(String tip)
	{
		if (mContext != null) {
			Toast.makeText(mContext, tip, Toast.LENGTH_SHORT).show();
		}
	}

}
