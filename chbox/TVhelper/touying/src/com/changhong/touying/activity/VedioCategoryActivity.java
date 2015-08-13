package com.changhong.touying.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
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
        adapter = new VedioDataAdapter(this, R.layout.vedio_category_item);
        vedioGridView.setAdapter(adapter);
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
         * 视频部分
         */
        vedioGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyApplication.vibrator.vibrate(100);
                TextView fullpath = (TextView) view.findViewById(R.id.vedio_item_path);

                if (StringUtils.hasLength(fullpath.getText().toString())) {
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
