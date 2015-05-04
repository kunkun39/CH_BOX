package com.changhong.touying.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.touying.vedio.Vedio;
import com.changhong.touying.R;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;

import java.util.List;

/**
 * Created by Jack Wang
 */
public class VedioViewActivity extends Activity {

    /**
     * server ip part
     */
    public static TextView title = null;
    private Button listClients;
    private ListView clients = null;
    private Button back;
    private ArrayAdapter<String> IpAdapter;

    /**
     * 视频浏览部分
     */
    private GridView vedioGridView;

    /**
     * 从上个Activity传过来的veidos
     */
    private List<Vedio> vedios;

    /**
     * 数据适配器
     */
    private PictureAdapter pictureAdapter;


    @Override
    protected void onResume() {
        super.onResume();
        if (ClientSendCommandService.titletxt != null) {
            title.setText(ClientSendCommandService.titletxt);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();

        initViews();

        initEvents();
    }

    private void initData() {
        vedios = (List<Vedio>) getIntent().getSerializableExtra("vedios");
    }

    private void initViews() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_vedio_view);

        title = (TextView) findViewById(R.id.title);
        back = (Button) findViewById(R.id.btn_back);
        clients = (ListView) findViewById(R.id.clients);
        listClients = (Button) findViewById(R.id.btn_list);

        IpAdapter = new ArrayAdapter<String>(VedioViewActivity.this, android.R.layout.simple_list_item_1, ClientSendCommandService.serverIpList);
        clients.setAdapter(IpAdapter);
        clients.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                clients.setVisibility(View.GONE);
                return false;
            }
        });
        clients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ClientSendCommandService.serverIP = ClientSendCommandService.serverIpList.get(arg2);
                title.setText("CHBOX");
                ClientSendCommandService.handler.sendEmptyMessage(2);
                clients.setVisibility(View.GONE);
            }
        });

        listClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                if (ClientSendCommandService.serverIpList.isEmpty()) {
                    Toast.makeText(VedioViewActivity.this, "未获取到服务器IP", Toast.LENGTH_LONG).show();
                } else {
                    clients.setVisibility(View.VISIBLE);
                }
            }
        });

        vedioGridView = (GridView) findViewById(R.id.vedio_grid_view);
        pictureAdapter = new PictureAdapter(this, R.layout.vedio_category_item, vedios);
        vedioGridView.setAdapter(pictureAdapter);
    }

    private void initEvents() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                finish();
            }
        });

        //设置点击item事件
        vedioGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyApplication.vibrator.vibrate(100);
                Intent intent = new Intent();
                intent.setClass(VedioViewActivity.this, VedioDetailsActivity.class);
                Bundle bundle = new Bundle();
                Vedio vedio = vedios.get(position);
                bundle.putSerializable("selectedVedio", vedio);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    /**********************************************数据适配器**********************************************************/

    public class PictureAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        private List<Vedio> vedios;

        private int item_image;

        public PictureAdapter(Context context, int item_image, List<Vedio> vedios) {
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.item_image = item_image;
            this.vedios = vedios;
        }

        public int getCount() {
            return vedios.size();
        }

        public Object getItem(int item) {
            return item;
        }

        public long getItemId(int id) {
            return id;
        }

        //创建View方法
        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView vedioImage = null;

            TextView vedioName = null;

            TextView fullPath = null;

            if (convertView == null) {
                //获得view
                convertView = inflater.inflate(item_image, null);
                vedioImage = (ImageView) convertView.findViewById(R.id.vedio_item_image);
                vedioName = (TextView) convertView.findViewById(R.id.vedio_item_name);
                fullPath = (TextView) convertView.findViewById(R.id.vedio_item_path);

                //组装view
                DataWapper wapper = new DataWapper(vedioImage, vedioName, fullPath);
                convertView.setTag(wapper);
            } else {
                DataWapper wapper = (DataWapper) convertView.getTag();
                vedioImage = wapper.getVedioImage();
                vedioName = wapper.getVedioName();
                fullPath = wapper.getFullPath();
            }

            Vedio vedio = vedios.get(position);
            String displayName = StringUtils.hasLength(vedio.getDisplayName()) ? StringUtils.getShortString(vedio.getDisplayName(), 20) : vedio.getTitle();
            vedioName.setText(displayName);
            fullPath.setText(String.valueOf(position));

            String vedioPath = vedio.getPath();
            String vedioImagePath = DiskCacheFileManager.isSmallImageExist(vedioPath);
            if (!vedioImagePath.equals("")) {
                MyApplication.imageLoader.displayImage("file://" + vedioImagePath, vedioImage, MyApplication.viewOptions);
                vedioImage.setScaleType(ImageView.ScaleType.FIT_XY);
            } else {
                synchronizImageLoad(vedioImage, vedioPath);
            }

            return convertView;
        }

        private void synchronizImageLoad(final ImageView imageView, final String path) {
            ImageAsyncTask task = new ImageAsyncTask(imageView);
            task.execute(path);
        }

        private final class ImageAsyncTask extends AsyncTask<String, Integer, Bitmap> {
            ImageView imageView;

            private ImageAsyncTask(ImageView imageView) {
                this.imageView = imageView;
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap bitmap = null;
                try {
                    String path = params[0];
                    bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
                    DiskCacheFileManager.saveSmallImage(bitmap, path);
                    return bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                    return bitmap;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }
        }

        private final class DataWapper {

            //视频的图标
            private ImageView vedioImage;

            //视频的名字
            private TextView vedioName;

            //视屏的全路径
            private TextView fullPath;


            private DataWapper(ImageView vedioImage, TextView vedioName, TextView fullPath) {
                this.vedioImage = vedioImage;
                this.vedioName = vedioName;
                this.fullPath = fullPath;
            }

            public ImageView getVedioImage() {
                return vedioImage;
            }

            public void setVedioImage(ImageView vedioImage) {
                this.vedioImage = vedioImage;
            }

            public TextView getVedioName() {
                return vedioName;
            }

            public void setVedioName(TextView vedioName) {
                this.vedioName = vedioName;
            }

            public TextView getFullPath() {
                return fullPath;
            }

            public void setFullPath(TextView fullPath) {
                this.fullPath = fullPath;
            }
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
