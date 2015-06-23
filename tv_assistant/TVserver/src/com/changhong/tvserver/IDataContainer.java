/**
 * 
 */
package com.changhong.tvserver;

/**
 * @author yves.yang
 *
 */
public interface IDataContainer {
	public void registListener(IMessageListener listener);
	public void registListener(IMessageListener listener,String type);	
	public void unregistListener(IMessageListener listener);

	void update(String message);
	void update(String message,String type);
}
