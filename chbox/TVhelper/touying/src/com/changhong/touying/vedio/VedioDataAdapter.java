package com.changhong.touying.vedio;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.touying.R;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;

import java.util.List;
import java.util.Map;

/**
 * Created by Jack Wang
 */
public class VedioDataAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<?> vedios;

    private List<String> vedioList;

    private Map<String, List<Vedio>> model;

    private int item_image;
    
    private Context context;

    public VedioDataAdapter(Context context, int item_image) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.item_image = item_image;
        this.context=context;

        VedioProvider provider = new VedioProvider(context);
        vedios = provider.getList();
        model = provider.getMapStructure(vedios);
        vedioList = provider.getVedioList(model);
    }

    public int getCount() {
        return vedioList.size();
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
        
        TextView vedioNO = null;

        TextView fullPath = null;

        if (convertView == null) {
            //获得view
            convertView = inflater.inflate(item_image, null);
            vedioImage = (ImageView) convertView.findViewById(R.id.vedio_item_image);
            vedioName = (TextView) convertView.findViewById(R.id.vedio_item_name);
            vedioNO = (TextView) convertView.findViewById(R.id.vedio_item_NO);
            fullPath = (TextView) convertView.findViewById(R.id.vedio_item_path);

            //组装view
            DataWapper wapper = new DataWapper(vedioImage, vedioName,vedioNO, fullPath);
            convertView.setTag(wapper);
        } else {
            DataWapper wapper = (DataWapper) convertView.getTag();
            vedioImage = wapper.getVedioImage();
            vedioName = wapper.getVedioName();
            vedioNO = wapper.getVedioNO();
            fullPath = wapper.getFullPath();
        }

        String key = vedioList.get(position);
        List<Vedio> list = model.get(key);
        Vedio vedio = (Vedio) list.get(0);

        if (list.size() > 1) {
            vedioName.setText(key);
            vedioNO.setText(list.size() + context.getResources().getString(R.string.videos_no));
            if (fullPath != null){
                fullPath.setText("");
            }
        } else {
            String displayName = StringUtils.hasLength(vedio.getDisplayName()) ? StringUtils.getShortString(vedio.getDisplayName(), 20) : vedio.getTitle();
            //vedioName.setLines(2);
            vedioName.setText(displayName);
            if (fullPath != null){
                fullPath.setText(String.valueOf(position));
            }else {
                vedioNO.setText(list.size() + context.getResources().getString(R.string.videos_no));
            }
        }

        String vedioPath = vedio.getPath();
        String vedioImagePath = DiskCacheFileManager.isSmallImageExist(vedioPath);
        if (!vedioImagePath.equals("")) {
            MyApplication.imageLoader.displayImage("file://" + vedioImagePath, vedioImage, MyApplication.viewOptions);
            vedioImage.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            synchronizImageLoad(vedioImage, vedio.getPath());
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
        
      //视频的数量
        private TextView vedioNO;

        //视屏的全路径
        private TextView fullPath;


        private DataWapper(ImageView vedioImage, TextView vedioName, TextView vedioNO, TextView fullPath) {
            this.vedioImage = vedioImage;
            this.vedioName = vedioName;
            this.vedioNO = vedioNO;
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
        
        public TextView getVedioNO() {
            return vedioNO;
        }

        public void setVedioNO(TextView vedioNO) {
            this.vedioNO = vedioNO;
        }

        public TextView getFullPath() {
            return fullPath;
        }

        public void setFullPath(TextView fullPath) {
            this.fullPath = fullPath;
        }
    }

    public Vedio getPositionVedio(int position) {
        return model.get(vedioList.get(position)).get(0);
    }

    public List<Vedio> getPositionVedios(int position) {
        return model.get(vedioList.get(position));
    }
}
