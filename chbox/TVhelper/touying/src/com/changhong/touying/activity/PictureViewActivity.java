package com.changhong.touying.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changhong.common.system.MyApplication;
import com.changhong.common.widgets.BoxSelecter;
import com.changhong.touying.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack Wang
 */
public class PictureViewActivity extends AppCompatActivity {

    DrawerLayout mDrawerLayout;
    /**
     * ***********************************************IP连接部分******************************************************
     */
    public Button back;
    private BoxSelecter ipSelecter;

    /**
     * *********************************************图片加载部分******************************************************
     */

    private RecyclerView listPicView;

    /**
     * 传过来需要浏览的图片
     */
    private List<String> imagePaths;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();

        initView();

        initEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.touying, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();
        } else if (item.getItemId() == R.id.ipbutton) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        return true;
    }

    private void initData() {
        imagePaths = getIntent().getStringArrayListExtra("imagePaths");
    }

    private void initView() {

        setContentView(R.layout.activity_picture_category);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.pic_main_drawer);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        /**
         * 图片容器
         */
        listPicView = (RecyclerView) findViewById(R.id.select_data);
        listPicView.setLayoutManager(new GridLayoutManager(
                PictureViewActivity.this, 3));
        listPicView
                .setAdapter(new RecyclerViewAdapter(PictureViewActivity.this));

    }

    private void initEvent() {
        /**
         * IP连接部分
         */
        ipSelecter = new BoxSelecter(this, (TextView) findViewById(R.id.title),
                (ListView) findViewById(R.id.clients), new Handler(
                getMainLooper()));

    }

    public class RecyclerViewAdapter extends
            RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private Context mContext;

        public RecyclerViewAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.activity_picture_row, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(
                final RecyclerViewAdapter.ViewHolder holder, final int position) {

            MyApplication.imageLoader.displayImage(
                    "file://" + imagePaths.get(position), holder.imageView,
                    MyApplication.viewOptions);

            Log.i("IMAGE_VIEW", imagePaths.get(position));

            final View view = holder.mView;
            view.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    MyApplication.vibrator.vibrate(100);
                    /**
                     * 显示图片预览效果
                     */
                    Intent intent = new Intent();
                    intent.setClass(PictureViewActivity.this,
                            PictureDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    bundle.putStringArrayList("imagePaths",
                            new ArrayList<String>(imagePaths));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            });

        }

        @Override
        public int getItemCount() {

            return imagePaths.size();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            int index;
            public final View mView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                imageView = (ImageView) view.findViewById(R.id.grid_picture);
            }
        }

    }

    /**
     * *******************************************系统发发重载********************************************************
     */

    @Override
    protected void onResume() {
        super.onResume();
//        if (ClientSendCommandService.titletxt != null) {
//            title.setText(ClientSendCommandService.titletxt);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ipSelecter != null) {
            ipSelecter.release();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

}
