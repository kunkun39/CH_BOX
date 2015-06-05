package com.changhong.other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Jack Wang
 * Date: 15-1-12
 * Time: 上午11:28
 */
public class ListAddAndRemoveTest {

    private static final int DEFUALT_CACHE_IMAGE_SIZE = 10;

    private static Map<String, List<String>> cacheModel = new HashMap<String, List<String>>();

    public static void addBitmapToCache(String clientIP, String imagePath) {
        if (imagePath == null) {
            return;
        }

        //new cache object for every client
        List<String> cacheImageURLs = cacheModel.get(clientIP);
        if (cacheImageURLs == null) {
            cacheImageURLs = new ArrayList<String>();
        }

        //FIFO stategory for image size
        int currentCacheImageSize = cacheImageURLs.size();
        if (currentCacheImageSize < DEFUALT_CACHE_IMAGE_SIZE) {
            cacheImageURLs.add(currentCacheImageSize, imagePath);
        } else {
            String key = cacheImageURLs.get(0);
            cacheImageURLs.remove(0);
            cacheImageURLs.add(DEFUALT_CACHE_IMAGE_SIZE - 1, imagePath);
        }
        cacheModel.put(clientIP, cacheImageURLs);

        for (String key : cacheModel.keySet()) {
            List<String> values = cacheModel.get(key);
            for (String value : values) {
                System.out.println(key + "-" + value);
            }
        }
    }

    public static void main(String[] args) {
//        for (int i = 1; i < 21; i++) {
//            addBitmapToCache("127.0.0.1", "http://localhost:8080/" + i);
//            System.out.println("\n");
//        }
//        for (int i = 1; i < 21; i++) {
//            addBitmapToCache("127.0.0.2", "http://localhost:8080/" + i);
//            System.out.println("\n");
//        }

        List<String> packages = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            packages.add(0, i + "");
        }

        for (String aPackage : packages) {
            System.out.println(aPackage);
        }
    }
}
