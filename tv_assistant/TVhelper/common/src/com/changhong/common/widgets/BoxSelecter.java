package com.changhong.common.widgets;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.changhong.common.service.ClientSendCommandService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Administrator on 15-5-15.
 */
public class BoxSelecter implements Observer
{
	Activity mActivity = null;
	TextView mTitle = null;
	ListView mView = null;
	Button mDropDownBtn = null;
	BoxSelectAdapter mAdapter = null;
	Handler mHandler = null;
	
	
	public BoxSelecter(Activity activity,TextView title,ListView view,Button dropDownBtn,Handler handler)
	{
		mActivity = activity;
		mView = view;
		mTitle = title;
		mDropDownBtn = dropDownBtn;
		mHandler = handler;
		
		mTitle.setText(IpSelectorDataServer.getInstance().getName());
		mAdapter = new BoxSelectAdapter(activity);
		mView.setAdapter(mAdapter);
		mView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mView.setVisibility(View.GONE);
				return false;
			}
		});
		mView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {								
                IpSelectorDataServer.getInstance().setCurrentIp(mAdapter.ipList.get(arg2));                
                mView.setVisibility(View.GONE);
			}
		});
		mDropDownBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (IpSelectorDataServer.getInstance().getIpList().isEmpty()) {
					Toast.makeText(mActivity,
							"没有发现长虹智能机顶盒，请确认盒子和手机连在同一个路由器?", Toast.LENGTH_LONG)
							.show();
				} else {
					mView.setVisibility(View.VISIBLE);
				}
			}
		});
		
		IpSelectorDataServer.getInstance().addObserver(this);
	}
	
	public void release()
	{
		IpSelectorDataServer.getInstance().deleteObserver(this);
	}
	
	@Override
	public void update(Observable observable, Object data) {				
		mHandler.post(new Runnable() {			
			@Override
			public void run() {
				IpSelectorDataServer dataServer = IpSelectorDataServer.getInstance();
				mAdapter.updateList(dataServer.getIpList());			
				mTitle.setText(dataServer.getName());
			}
		});
				
	}
	
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
	            convertView = minflater.inflate(android.R.layout.simple_list_item_1, null);
	            vh.boxInfo = (TextView) convertView.findViewById(android.R.id.text1);

	            convertView.setTag(vh);
	        } else {
	            vh = (ViewHolder) convertView.getTag();
	        }

	        String serverIP = ipList.get(position);

	        vh.boxInfo.setText(ClientSendCommandService.getConnectBoxName(serverIP) +  " [" + serverIP + "]");

	        return convertView;
	    }

	    public final class ViewHolder {
	        public TextView boxInfo;
	    }
	}

	
}

