/**
 * 
 */
package com.changhong.thirdpart.common;

import android.content.Context;
import android.os.Message;

/**
 * @author yves.yang
 *
 */

/**
 * 开放接口用于接收透传消息
 * ==================================================================================*/
public interface IDataListener {
	
	void OnDataChanged(Message message);
}
