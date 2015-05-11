package com.changhong.yinxiang.vedio;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.yinxiang.R;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;

import java.util.List;

/**
 * Created by Administrator on 15-5-11.
 */
public class YinXiangVedioAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<?> vedios;

    public YinXiangVedioAdapter(Context context) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        YinXaingVedioProvider provider = new YinXaingVedioProvider(context);
        vedios = provider.getList();
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
        DataWapper  wapper = null;

        if (convertView == null) {
            wapper = new DataWapper();
            //获得view
            convertView = inflater.inflate(R.layout.yinixiang_vedio_list_item, null);
            wapper.vedioImage = (ImageView) convertView.findViewById(R.id.yinxiang_vedio_item_image);
            wapper.vedioName = (TextView) convertView.findViewById(R.id.yinxiang_vedio_item_name);
            wapper.fullPath = (TextView) convertView.findViewById(R.id.yinxiang_vedio_item_path);
            wapper.vedioChecked = (CheckBox) convertView.findViewById(R.id.yinxiang_vedio_item_checked);

            //组装view
            convertView.setTag(wapper);
        } else {
            wapper = (DataWapper) convertView.getTag();
        }

        YinXiangVedio yinXiangVedio = (YinXiangVedio) vedios.get(position);

        String displayName = yinXiangVedio.getDisplayName();
        String vedioPath = yinXiangVedio.getPath();

        wapper.vedioName.setText(displayName);
        wapper.fullPath.setText(vedioPath);

        String vedioImagePath = DiskCacheFileManager.isSmallImageExist(vedioPath);
        if (!vedioImagePath.equals("")) {
            MyApplication.imageLoader.displayImage("file://" + vedioImagePath, wapper.vedioImage, MyApplication.viewOptions);
            wapper.vedioImage.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            synchronizImageLoad(wapper.vedioImage, vedioPath);
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
        public ImageView vedioImage;

        //视频的名字
        public TextView vedioName;

        //视频是否被选中
        public CheckBox vedioChecked;

        //视屏的全路径
        public TextView fullPath;

    }
}
