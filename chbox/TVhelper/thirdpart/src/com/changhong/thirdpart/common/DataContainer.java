/**
 * 
 */
package com.changhong.thirdpart.common;

import java.util.HashSet;
import java.util.Set;

import android.R.string;
import android.content.Context;
import android.os.Message;

/**
 * @author yves.yang
 *
 */
public class DataContainer implements IDataContainer{
	private Set<IDataListener> lisners = new HashSet<IDataListener>();
	
	@Override
	public void registerListener(IDataListener l)
	{
		if (l == null) {
			return ;
		}
		lisners.add(l);
	}
	
	@Override
	public void unregisterListener(IDataListener l)
	{
		if (l == null) {
			return ;
		}
		lisners.remove(l);		
	}
		
	
	@Override
	public void dispatchMessage(Message message)
	{        
		if (message == null) {
			return;
		}
		
		for (IDataListener l : lisners) {
			l.OnDataChanged(message);
		}
	}
}
