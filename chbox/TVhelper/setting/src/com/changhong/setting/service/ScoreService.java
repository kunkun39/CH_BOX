package com.changhong.setting.service;

import android.content.Context;
import com.changhong.setting.domain.NetworkItem;
import com.changhong.setting.domain.ScoreItem;
import com.changhong.setting.view.ScoreDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Ma Ren
 */
public class ScoreService {

    private Context context;

    /**
     * all the items which will influence total score
     */
    private List<ScoreItem> items = new ArrayList<ScoreItem>();

    public ScoreService(Context context) {
        this.context = context;
    }

    public void init() {
        ScoreItem item1 = new NetworkItem(context, 100);
        items.add(item1);
    }

    public int getTotal() {
        init();

        int sum = 0;
        for (ScoreItem item : items) {
            sum += item.getCurrentItemScore();

        }
        return sum / 100;
    }

    public ScoreItem getItemDetails(String tag) {
        if (items != null) {
            for (ScoreItem item : items) {
                if (item.getTag().equals(tag)) {
                    return item;
                }
            }
        }
        return null;
    }
}
