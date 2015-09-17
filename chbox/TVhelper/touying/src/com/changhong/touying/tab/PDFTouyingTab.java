package com.changhong.touying.tab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

import com.changhong.touying.R;
import com.changhong.touying.activity.OtherDetailsActivity;
import com.changhong.touying.file.FileItem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PDFTouyingTab extends Fragment{
	ArrayList<FileItem> mPathList = new ArrayList<FileItem>();
	View mView = null;
	PDFTouyingAdapter mAdapter = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.tab_pdf_list, container, false);
		ListView list = (ListView)mView.findViewById(R.id.pdflist);
		mAdapter = new PDFTouyingAdapter();
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				OtherDetailsActivity.touYing(getActivity(),mPathList.get(arg2).getPath());
			}
		});
		list.setAdapter(mAdapter);
		return mView;
	}
	
	public void setList(Collection<FileItem> list)
	{
		if (list == null) {
			return ;
		}
		mPathList.clear();
		mPathList.addAll(list);
		mAdapter.notifyDataSetChanged();
	}		

	class PDFTouyingAdapter extends BaseAdapter
	{

		@Override
		public int getCount() {
			return mPathList.size();
		}

		@Override
		public Object getItem(int position) {
			return mPathList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView pptName = null;
 			TextView fullPath = null;
 			ImageView playBtn = null;
 			ImageView defaultImage = null;
 			DataWapper wapper = null;

 			if (convertView == null) {
 				// 获得view
 				
 				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.ppt_list_item, null);
 				
 				pptName = (TextView) convertView
 						.findViewById(R.id.file_item_name);
 				fullPath = (TextView) convertView
 						.findViewById(R.id.file_item_path);
 				playBtn = (ImageView) convertView
 						.findViewById(R.id.file_list_play);
 				defaultImage = (ImageView) convertView
 						.findViewById(R.id.file_item_image);

 				// 组装view
 				wapper = new DataWapper(pptName, fullPath, playBtn,defaultImage);
 				convertView.setTag(wapper);
 			} else {
 				wapper = (DataWapper) convertView.getTag();
 				pptName = wapper.pptName;
 				fullPath = wapper.fullPath;
 				playBtn = wapper.playBtn;
 				defaultImage = wapper.defaultImage;
 			}

 			final FileItem pdf = mPathList.get(position);
 			defaultImage.setBackgroundResource(R.drawable.pdf_icon);
 			pptName.setText(pdf.getTitle());
 			fullPath.setText(pdf.getPath());

 			playBtn.setOnClickListener(new OnClickListener() {

 				@Override
 				public void onClick(View v) {
 					OtherDetailsActivity.touYing(getActivity(),pdf.getPath());
 				}
 			});
 			
 			return convertView;
		}
		
	}
	
	private final class DataWapper {

		public TextView pptName;
		public TextView fullPath;
		public ImageView playBtn;
		public ImageView defaultImage;

		private DataWapper(TextView pptName, TextView fullPath, ImageView playBtn, ImageView defaultImage) {
			this.pptName = pptName;
			this.fullPath = fullPath;
			this.playBtn = playBtn;
			this.defaultImage = defaultImage;
		}
	}
}
