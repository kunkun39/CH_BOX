package com.changhong.tvserver;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.changhong.tvserver.utils.StringUtils;

import java.util.Date;

public class MainActivity extends Activity {

    private static final String TAG = "TVServer";

    public static String CH_BOX_NAME = "客    厅";

    private EditText chboxName;

    private Button chboxSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_main);

        chboxName = (EditText) findViewById(R.id.ch_box_name);
        chboxSave = (Button) findViewById(R.id.ch_box_save);

        CH_BOX_NAME = getBoxName(MainActivity.this);
        chboxName.setText(CH_BOX_NAME);

        Intent intent = new Intent(MainActivity.this, TVSocketControllerService.class);
        startService(intent);

        chboxSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = chboxName.getText().toString();
                if (!StringUtils.hasLength(content)) {
                    Toast.makeText(MainActivity.this, "请输入机顶盒的标识!", 3000).show();
                } else {
                    try {
                        if (!CH_BOX_NAME.equals(content)) {
                            content = content.trim();
                            saveBoxName(MainActivity.this, content);
                            CH_BOX_NAME = content;
                        }
                        Toast.makeText(MainActivity.this, "保存成功!", 3000).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "保存失败!", 3000).show();
                    }
                }
            }
        });
    }

    public static void saveBoxName(Context context, String name) {
        SharedPreferences preferences = context.getSharedPreferences("changhong_box_name", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("CH_BOX_NAME", name);
        editor.commit();
    }

    public static String getBoxName(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("changhong_box_name", Context.MODE_PRIVATE);
        return preferences.getString("CH_BOX_NAME", "客    厅");
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
