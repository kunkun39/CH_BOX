package com.changhong.touying.tab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

import com.changhong.touying.R;
import com.changhong.touying.activity.OtherDetailsActivity;
import com.changhong.touying.file.FileItem;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class PPTTouyingTab extends Fragment{
	private RecyclerView mRecyclerView;
	ArrayList<FileItem> mPathList = new ArrayList<FileItem>();
	View mView = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mRecyclerView = (RecyclerView) inflater.inflate(R.layout.touying_recyclerview,
				container, false);

		return mRecyclerView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

	}
	public void setdata(Collection<FileItem> list) {
		if (list == null) {
			return ;
		}
		mPathList.clear();
		mPathList.addAll(list);
		mRecyclerView.setAdapter(new RecyclerViewAdapter(getActivity(),
				mPathList));

	}

	class RecyclerViewAdapter extends
			RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

		private Context mContext;
		ArrayList<FileItem> PathList = new ArrayList<FileItem>();

		public RecyclerViewAdapter(Context context,
				ArrayList<FileItem> mPathList) {
			this.mContext = context;
			PathList = mPathList;

		}

		@Override
		public RecyclerViewAdapter.ViewHolder onCreateViewHolder(
				ViewGroup parent, int viewType) {

			View view = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.ppt_list_item, parent, false);
			return new ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(
				final RecyclerViewAdapter.ViewHolder holder, final int position) {
			final View view = holder.mView;

			final FileItem ppt = mPathList.get(position);
			holder.defaultImage.setBackgroundResource(R.drawable.pdf_icon);
			holder.pptName.setText(ppt.getTitle());
			holder.fullPath.setText(ppt.getPath());

			holder.playBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					OtherDetailsActivity.touYing(getActivity(), ppt.getPath());
				}
			});

			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					OtherDetailsActivity.touYing(getActivity(),
							mPathList.get(position).getPath());
				}
			});

		}

		@Override
		public int getItemCount() {
			return PathList.size();
		}

		public class ViewHolder extends RecyclerView.ViewHolder {

			TextView pptName = null;
			TextView fullPath = null;
			ImageView playBtn = null;
			ImageView defaultImage = null;

			public final View mView;

			public ViewHolder(View view) {

				super(view);

				mView = view;

				pptName = (TextView) view.findViewById(R.id.file_item_name);
				fullPath = (TextView) view.findViewById(R.id.file_item_path);
				playBtn = (ImageView) view.findViewById(R.id.file_list_play);
				defaultImage = (ImageView) view
						.findViewById(R.id.file_item_image);

			}
		}

	}

}
