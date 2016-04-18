package com.changhong.common.widgets;

import android.R.color;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.changhong.common.R;
import com.changhong.common.service.ClientSendCommandService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Yves Yang on 15-5-15.
 */
public class BoxSelecter implements Observer
{
	/**
	 * Current Activity
	 */
	Activity mActivity = null;
	
	/**
	 *  Cotrollers
	 */
	TextView mTitle = null;
	ListView mView = null;
	Button mDropDownBtn = null;
	LinearLayout mLinearLayout = null;
	ImageView mImageView = null;
	
	/**
	 * IP List Adapter
	 * 
	 * 自定义类型
	 */
	BoxSelectAdapter mAdapter = null;   
	
	/**
	 * A Handler With MainLooper To Redraw Surface
	 */
	Handler mHandler = null;
	
	
	public BoxSelecter(Activity activity,TextView title,ListView view,Button dropDownBtn,Handler handler)
	{
		if (view == null){
			return;
		}
		mActivity = activity;
		mView = view;
		mTitle = title;
		mDropDownBtn = dropDownBtn;
		
		mHandler = handler;
		if (mTitle != null){
			mTitle.setText(IpSelectorDataServer.getInstance().getName());
		}

		mAdapter = new BoxSelectAdapter(activity);
		mView.setAdapter(mAdapter);    //data and adapter 绑定
		
        //触摸返回		
		mView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mView.setVisibility(View.GONE);
				return false;
			}
		});
		
        //listView Item 选择		
		mView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {								
                IpSelectorDataServer.getInstance().setCurrentIp(mAdapter.ipList.get(arg2));                
                mView.setVisibility(View.GONE);
                mImageView.setVisibility(View.GONE);
			}
		});
		
       //触发IP选择按钮		
		mDropDownBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (IpSelectorDataServer.getInstance().getIpList().isEmpty()) {
					Toast.makeText(mActivity,
							R.string.server_isnt_exist, Toast.LENGTH_LONG)
							.show();
				} else {
					mView.setVisibility(View.VISIBLE);
					mImageView.setVisibility(View.VISIBLE);
				}
			}
		});
		
		IpSelectorDataServer.getInstance().addViewObserver(this);
	}
	
	public BoxSelecter(Activity activity,TextView title,ListView view,Button dropDownBtn,LinearLayout linearLayout,ImageView imageView,Handler handler)
	{
		
		
		this(activity,title,view,dropDownBtn,handler);
		mLinearLayout = linearLayout;
		mImageView = imageView;
		
		if(IpSelectorDataServer.getInstance().getCurrentIp() != null){
			mLinearLayout.setBackgroundResource(R.drawable.ip_connect_title);
		}else {
			mLinearLayout.setBackgroundResource(R.drawable.ip_unconnect_title);
		}
	}
	
	// YVES YANG:!!!! Must Call This Function To Release Its Self
	public void release()
	{
		IpSelectorDataServer.getInstance().deleteViewObserver(this);
	}
	
	@Override
	public void update(Observable observable, Object data) {				
		mHandler.postAtFrontOfQueue(new Runnable() {			
			@Override
			public void run() {
				IpSelectorDataServer dataServer = IpSelectorDataServer.getInstance();
				try {
					List<String> list = new ArrayList<String>(dataServer.getIpList());

					// Update List
					mAdapter.updateList(list);
					
					mLinearLayout.setBackgroundResource(R.drawable.ip_connect_title);
					
					// Update Name
					if(mTitle != null){
						mTitle.setText(dataServer.getName());
					}

					
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
				
	}
	
	/**
	 * 
	 * List Adapter
	 */
	public class BoxSelectAdapter extends BaseAdapter {

	    private LayoutInflater minflater;

	    private List<String> ipList = new ArrayList<String>();

	    public BoxSelectAdapter(Context context) {
	        this.minflater = LayoutInflater.from(context); 
	        ipList.addAll(IpSelectorDataServer.getInstance().getIpList());
	    }
	    /**
	     * 更新数据，更新list后在调用notifyDataSetChanged()
	     * @param ipList
	     */
	    private void updateList(Collection<String> ipList) {
	    	this.ipList.clear();
			this.ipList.addAll(ipList);
			notifyDataSetChanged();
		}

	    @Override
	    public int getCount() {
	        return ipList==null?0:ipList.size();
	    }

	    @Override
	    public Object getItem(int position) {
	        return ipList.get(position);
	    }

	    @Override
	    public long getItemId(int position) {
	        return position;
	    }
	    
	    public int getItemIndex(Object item)
	    {
	    	return ipList.indexOf(item);
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        /**
	         * VIEW HOLDER的配置
	         */
	        final ViewHolder vh;
	        if (convertView == null) {
	            vh = new ViewHolder();
//	            convertView = minflater.inflate(android.R.layout.simple_list_item_1, null);
	            convertView = minflater.inflate(R.layout.list_item1, null);
//	            vh.boxInfo = (TextView) convertView.findViewById(android.R.id.text1);
	            vh.boxInfo = (TextView) convertView.findViewById(R.id.ip_list_item);

	            convertView.setTag(vh);  //将VH存在convertView中
	        } else {
	            vh = (ViewHolder) convertView.getTag();  //重新获取
	        }

	        String serverIP = ipList.get(position);

//            vh.boxInfo.setText(ClientSendCommandService.getConnectBoxName(serverIP) +  " [" + serverIP + "]");
            vh.boxInfo.setText(ClientSendCommandService.getConnectBoxName(serverIP));
            vh.boxInfo.setTextSize(16);
	        return convertView;
	    }
	    
	    /*
	     * 内部类对控件的实例进行缓存
	     * 
	     * */

	    public final class ViewHolder {
	        public TextView boxInfo;
	    }
	}

	
}

