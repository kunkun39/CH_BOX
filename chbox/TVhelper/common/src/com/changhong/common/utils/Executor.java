package com.changhong.common.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Yves Yang on 2016/3/1.
 */
public class Executor {
    static Executor executor;
    public static synchronized Executor inst(){
        if (executor == null){
            executor = new Executor();
            executor.executorService = Executors.newCachedThreadPool();
        }
        return executor;
    }

    public static void execute(Runnable runnable){
        if(runnable == null)
            return;
        inst().executorService.execute(runnable);
    }

    ExecutorService executorService;
}
