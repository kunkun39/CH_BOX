/**
 * 
 */
package com.changhong.thirdpart.location;

import android.content.Context;
import android.os.Message;

import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.changhong.thirdpart.common.DataContainer;
import com.changhong.thirdpart.common.IDataContainer;
import com.changhong.thirdpart.common.IDataListener;
import com.changhong.thirdpart.common.IThirdPartUtil;
import com.changhong.thirdpart.push.PushUtils;
import com.igexin.sdk.PushManager;

/**
 * @author yumin.chen
 * 百度定位
 */
public class LocationUtil implements IThirdPartUtil,IDataContainer{

    /**
     * 
     * baidu location
     * @return
     */
	private LocationClient mLocationClient;//百度定位客户端
    private GeofenceClient mGeofenceClient;
    private MyLocationListener mMyLocationListener;//监听器
    private LocationMode tempMode = LocationMode.Hight_Accuracy;//高精度模式
	private String tempcoor="bd09ll";//百度加密经纬度坐标
    private int span=1000;//定位时间间隔
    
    private DataContainer dataContainer = new DataContainer();
    private static LocationUtil  mLocationUtil;
    private Context mContext;
    
    /**==================================================================================*/	
	/**
	 * 获取初始化了的实例,Init 方法不允许是application中使用
	 * @param context： Application Context
	 */
    private LocationUtil(){};
	public LocationUtil init(Context context)
	{			
		if (context == null) {
			return null;
		}
				
		mContext = context.getApplicationContext();	
		
		mLocationClient = new LocationClient(context.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		mGeofenceClient = new GeofenceClient(context.getApplicationContext());
		InitLocation();
		
		return mLocationUtil;
	}
	
	public static LocationUtil getInstance()
	{			
		if (mLocationUtil == null){			
			synchronized (LocationUtil.class) {
				if (mLocationUtil == null) {
					mLocationUtil = new LocationUtil();					
				}
			}
		}
		return mLocationUtil;		
	}
    
	public void InitLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);//设置定位模式
        option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(span);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

	@Override
	public void finish(Context context) {
		
	}

	@Override
	public void registerListener(IDataListener l) {
		dataContainer.registerListener(l);
	}

	@Override
	public void unregisterListener(IDataListener l) {
		dataContainer.unregisterListener(l);
	}

	@Override
	public void dispatchMessage(Message message) {
		dataContainer.dispatchMessage(message);
	}
	
	public void start()
	{
		mLocationClient.start();		
	}
	
	public void stop()
	{
		mLocationClient.stop();
	}
}
