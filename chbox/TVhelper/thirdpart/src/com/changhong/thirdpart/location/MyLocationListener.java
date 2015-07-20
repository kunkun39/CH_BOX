package com.changhong.thirdpart.location;

import java.util.Observable;

import android.os.Bundle;
import android.os.Message;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

public class MyLocationListener implements BDLocationListener {

	

	@Override
	public void onReceiveLocation(BDLocation location) {
		// Receive Location
		LocationAttribute locationAttribute = new LocationAttribute();
		
		locationAttribute.setTime(location.getTime());
		locationAttribute.setLocType(location.getLocType());
		locationAttribute.setLatitude(location.getLatitude());
		locationAttribute.setLongitude(location.getLongitude());
		locationAttribute.setRadius(location.getRadius());
		locationAttribute.setAddress(location.getAddrStr());

		if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
			locationAttribute.setOperators(location
					.getOperators());
		}else if (location.getLocType() == BDLocation.TypeGpsLocation){
			locationAttribute.setSpeed(location.getSpeed());
			locationAttribute.setDirection(location.getDirection());
		}
		
		LocationUtil.getInstance().setLocationAttrbute(locationAttribute);
		
		Message message = new Message();
		Bundle bundle = new Bundle();
		bundle.putSerializable("LocationAttribute", locationAttribute);
		message.setData(bundle);
		LocationUtil.getInstance().dispatchMessage(message);
	}
}
