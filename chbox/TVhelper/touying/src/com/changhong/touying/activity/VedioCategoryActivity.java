package com.changhong.touying.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.AppConfig;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.utils.Utils;
import com.changhong.common.widgets.BoxSelecter;
import com.changhong.touying.R;
import com.changhong.touying.vedio.Vedio;
import com.changhong.touying.vedio.VedioDataAdapter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Jack Wang
 */
public class VedioCategoryActivity extends Activity {

    /**************************************************IP连接部分*******************************************************/    
    private Button back;
    private BoxSelecter ipSelecter;

    /**************************************************视频部分*******************************************************/

    /**
     * Image List adapter
     */
    private VedioDataAdapter adapter;
    /**
     * 视频浏览部分
     */
    private GridView vedioGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initEvent();
    }

    private void initView() {
        setContentView(R.layout.activity_vedio_category);

        /**
         * IP连接部分
         */
        back = (Button) findViewById(R.id.btn_back);        

        /**
         * 视频部分
         */
        vedioGridView = (GridView) findViewById(R.id.vedio_grid_view);
        int itemLayoutId = R.layout.vedio_category_item;
        if (AppConfig.PROJECT_NAME == AppConfig.PROJECT_INDIA_DAS){
            itemLayoutId = R.layout.vedio_category_item_das;
            vedioGridView.setNumColumns(2);
        }
        adapter = new VedioDataAdapter(this,itemLayoutId);
        vedioGridView.setAdapter(adapter);
    }

    private void initEvent() {
    	
        /**
         * IP连接部分
         */
        if (AppConfig.PROJECT_NAME == AppConfig.PROJECT_INDIA_DAS){
            findViewById(R.id.vedio_base).setBackgroundResource(R.drawable.bk_das);
            findViewById(R.id.banner).setBackgroundResource(R.drawable.das_pic_title);
            findViewById(R.id.title_expand).setVisibility(View.GONE);
            TextView title = ((TextView) findViewById(R.id.title));
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams)title.getLayoutParams();
            layoutParams.setMargins(16,0,0,0);
            title.setLayoutParams(layoutParams);
            title.setText(R.string.videos);
            title.setTextColor(getResources().getColor(R.color.white));
            Button back = ((Button) findViewById(R.id.btn_back));
            back.setBackgroundResource(R.drawable.das_title_back);
            layoutParams = (ViewGroup.MarginLayoutParams)back.getLayoutParams();
            layoutParams.setMargins(16,0,0,0);
            layoutParams.width = Utils.dip2px(this, 40);
            layoutParams.height = Utils.dip2px(this, 40);
            back.setLayoutParams(layoutParams);
        }else {
            ipSelecter = new BoxSelecter(this, (TextView) findViewById(R.id.title), (ListView) findViewById(R.id.clients), (Button) findViewById(R.id.btn_list), new Handler(getMainLooper()));
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                finish();
            }
        });

        /**
         * 视频部分
         */
        vedioGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyApplication.vibrator.vibrate(100);
                List<Vedio> videos = adapter.getPositionVedios(position);
                if (videos.size() == 1) {
                    Intent intent = new Intent();
                    intent.setClass(VedioCategoryActivity.this, VedioDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    Vedio vedio = adapter.getPositionVedio(position);
                    bundle.putSerializable("selectedVedio", vedio);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(VedioCategoryActivity.this, VedioViewActivity.class);
                    Bundle bundle = new Bundle();
                    List<Vedio> vedios = adapter.getPositionVedios(position);
                    bundle.putSerializable("vedios", (Serializable) vedios);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
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
