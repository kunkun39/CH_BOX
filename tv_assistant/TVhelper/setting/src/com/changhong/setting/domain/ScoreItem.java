package com.changhong.setting.domain;

/**
 * Ma Ren
 */
public interface ScoreItem {

    /**
     * tag for very item
     */
    String getTag();

    /**
     * this method is used for get current item score, full score is 100
     */
    int getCurrentItemScore();

    /**
     * get current item suggestion
     */
    String getCurrentSuggestion();
}
