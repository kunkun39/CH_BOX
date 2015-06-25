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
 * 
 * @see 百度定位使用方法：可以直接首先获取对象，然后调用init方法，接着使用start方法开始定位。可以通过注册registerListener获取详细信息
 * 也可以调用getLocationAttribute获取信息。
 * 
 */
public class LocationUtil implements IThirdPartUtil,IDataContainer{

    /**
     * 
     * baidu location
     * @return
     */
	private LocationClient mLocationClient = null;//百度定位客户端
    private GeofenceClient mGeofenceClient = null;
    private MyLocationListener mMyLocationListener = new MyLocationListener();//监听器
    private LocationMode tempMode = LocationMode.Hight_Accuracy;//高精度模式
	private String tempcoor="bd09ll";//百度加密经纬度坐标
    private int span=1000;//定位时间间隔
    
    private DataContainer dataContainer = new DataContainer();
    private static LocationUtil  mLocationUtil;
    private Context mContext;
    private LocationAttribute locationAttribute = null;
    
    /**==================================================================================*/	
	/**
	 * 获取初始化了的实例,Init 方法不允许是application中使用
	 * @param context： Application Context
	 */
    private LocationUtil(){};
	
    /**
     * 获取单例，然后记得调用init初始化之后，才用。
     */
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

/**
 * 函数重写
 * ======================================================================================
 */
	@Override
	public void finish(Context context) {}

	/**
	 * 监听数据变动，当从一个地方到另一个地方的时候，会受到一次通知消息
	 * 消息在 Message里的getData().get("LocationAttribute")
	 * 会获取到LocationAttribute对象，然后自行读取所需要的数据，其他时候，自行读取
	 * getLocationAttribute中的数据
	 */
	@Override
	public void registerListener(IDataListener l) {
		dataContainer.registerListener(l);
	}

	@Override
	public void unregisterListener(IDataListener l) {
		dataContainer.unregisterListener(l);
	}

	/**
	 * 数据中心发送消息，所有的监听器都能收到
	 */
	@Override
	public void dispatchMessage(Message message) {
		dataContainer.dispatchMessage(message);
	}

/**
 * 公用方法
 * ======================================================================================
 */
	/**
	 * 初始化，最好保证context是正确的
	 */
	public LocationUtil init(Context context)
	{			
		if (context == null) return null;
		
		mContext = context;			
		mLocationClient = new LocationClient(mContext);		
		mLocationClient.registerLocationListener(mMyLocationListener);
		mGeofenceClient = new GeofenceClient(mContext);
		InitLocation();		
		return mLocationUtil;
	}		
	
	/**
	 * 开始定位
	 */
	public void start()
	{		
		if(mLocationClient != null) 
			mLocationClient.start();		
	}
	
	/**
	 * 停止定位
	 */
	public void stop()
	{
		if(mLocationClient != null)
			mLocationClient.stop();
	}
	
	/**
	 * 设置定位获取到的信息
	 */
	public void setLocationAttrbute(LocationAttribute locationAttribute)
	{
		this.locationAttribute = locationAttribute;
	}
	
	/**
	 * 获取定位获取到的信息
	 */
	public LocationAttribute getLocationAttribute()
	{
		return this.locationAttribute;
	}
	
/**
 * 私有方法
 * ===================================================================
 */
	/**
	 * 配置参数，用于初始化
	 */
	private void InitLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);//设置定位模式
        option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(span);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }
}
