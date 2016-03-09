package com.changhong.touying.tab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.touying.R;
import com.changhong.touying.activity.OtherDetailsActivity;
import com.changhong.touying.file.FileItem;

import java.util.ArrayList;
import java.util.Collection;

public class PDFTouyingTab extends Fragment {

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


    }

    public void setdata(Collection<FileItem> list) {
        if (list == null) {
            return;
        }
        mPathList.clear();
        mPathList.addAll(list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new RecyclerViewAdapter(getActivity(), mPathList));
    }

    public class RecyclerViewAdapter extends
            RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private Context mContext;
        ArrayList<FileItem> PathList = new ArrayList<FileItem>();


        public RecyclerViewAdapter(Context context, ArrayList<FileItem> mPathList) {
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

            final FileItem pdf = mPathList.get(position);
            holder.defaultImage.setBackgroundResource(R.drawable.pdf_icon);
            holder.pptName.setText(pdf.getTitle());
            holder.fullPath.setText(pdf.getPath());

            holder.playBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    OtherDetailsActivity.touYing(getActivity(), pdf.getPath());
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OtherDetailsActivity.touYing(getActivity(), mPathList.get(position).getPath());
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
                defaultImage = (ImageView) view.findViewById(R.id.file_item_image);
            }
        }

    }
}
