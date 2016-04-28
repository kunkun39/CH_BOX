package com.changhong.touying.vedio;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by Yves Yang on 2016/4/27.
 */
public class VolumeSeekBar extends SeekBar{
    public VolumeSeekBar(Context context) {
        super(context);
    }

    public VolumeSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VolumeSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
