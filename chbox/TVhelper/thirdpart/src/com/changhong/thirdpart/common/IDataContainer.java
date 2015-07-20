/**
 * 
 */
package com.changhong.thirdpart.common;

import android.os.Message;

/**
 * @author yves.yang
 *
 */
public interface IDataContainer {
	
	public void registerListener(IDataListener l);
	
	public void unregisterListener(IDataListener l);
	
	public void dispatchMessage(Message message);
}
