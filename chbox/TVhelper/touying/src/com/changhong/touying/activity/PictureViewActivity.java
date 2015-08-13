package com.changhong.touying.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.widgets.BoxSelecter;
import com.changhong.touying.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack Wang
 */
public class PictureViewActivity extends Activity {

    /**************************************************IP连接部分*******************************************************/
    public Button back;
    private BoxSelecter ipSelecter;

    /************************************************图片加载部分*******************************************************/

    private GridView listViewlocal;

    /**
     * 传过来需要浏览的图片
     */
    private List<String> imagePaths;

    /**
     * 图片加载的适配器
     */
    private ImageAdapterLocal imageAdapter = new ImageAdapterLocal();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();

        initView();

        initEvent();
    }

    private void initData() {
        imagePaths = getIntent().getStringArrayListExtra("imagePaths");
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_picture_view);
        back = (Button) findViewById(R.id.btn_back);

        /**
         * 图片容器
         */
        listViewlocal = (GridView) findViewById(R.id.select_picture);
        listViewlocal.setAdapter(imageAdapter);
        
    }

    private void initEvent() {
        /**
        * IP连接部分
         */
    	ipSelecter = new BoxSelecter(this, (TextView) findViewById(R.id.title), (ListView) findViewById(R.id.clients), (Button) findViewById(R.id.btn_list), new Handler(getMainLooper()));        
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                finish();
            }
        });

        /**
         * 图片部分
         */
        listViewlocal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                MyApplication.vibrator.vibrate(100);
                /**
                 * 显示图片预览效果
                 */
                Intent intent = new Intent();
                intent.setClass(PictureViewActivity.this, PictureDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putStringArrayList("imagePaths", new ArrayList<String>(imagePaths));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public class ImageAdapterLocal extends BaseAdapter {
        @Override
        public int getCount() {
            return imagePaths.size();
        }

        @Override
        public Object getItem(int position) {
            return imagePaths.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;
            if (view == null || ((ViewHolder)(view.getTag())).index != position) {
                view = getLayoutInflater().inflate(R.layout.activity_picture_row, parent, false);
                holder = new ViewHolder();

                holder.imageView = (ImageView) view.findViewById(R.id.grid_picture);
                holder.index = position;

                MyApplication.imageLoader.displayImage("file://" + imagePaths.get(position), holder.imageView, MyApplication.viewOptions);
                Log.i("IMAGE_VIEW", imagePaths.get(position));
                view.setTag(holder);
            }

            return view;
        }
    }

    private class ViewHolder {
        ImageView imageView;
        int index;
    }

    /**********************************************系统发发重载*********************************************************/

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
