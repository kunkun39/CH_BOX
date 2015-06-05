package com.changhong.common.utils;

import java.util.Random;

/**
 * User: Jack Wang
 * Date: 14-4-15
 * Time: 下午5:05
 */
public class NumberUtils {

    public static int generateRandomNumber(int range) {
        Random rand = new Random();
        return rand.nextInt(range);
    }
}
