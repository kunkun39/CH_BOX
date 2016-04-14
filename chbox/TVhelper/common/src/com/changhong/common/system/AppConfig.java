package com.changhong.common.system;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack Wang
 */
public class AppConfig {

	public static final boolean USE_MALL_APP = false;

    public static final boolean USE_SHARE = false;

    public static final boolean USE_LOCATION = false;
	
	public static final boolean NOT_USE_MSG_PUSH = true;

    public static final boolean USE_VOICE_INPUT = false;

    public static final boolean USE_REMOTER = true;

    public static final boolean USE_TV = false;

    public static final boolean USE_OTHER_AIRDISPLAY = false;
    /**
     * the parameter which decide the min size of the file we compress 1M
     */
    public final static int PICTURE_COMPRESS_MIN_SIZE = 2014 * 1024;

    /**
     * the parameter which decide the min size of the small picture touying size 512K
     */
    public final static int PICTURE_SMALL_TOUYING_MIN_SIZE = 512 * 1024;

    /**
     * the camera definition for every mobile company
     */
    public final static List<String> MOBILE_CARMERS_PACKAGE = new ArrayList<String>();

    static {
        MOBILE_CARMERS_PACKAGE.add("camera");
    }
    
    /**
     * Intent
     */
    public final static String BROADCAST_INTENT_EPGDB_UPDATE = "com.changhong.epg.db.update";

    /**
     * Project For
     */
    public final static int PROJECT_DEFAULT = 0;
    public final static int PROJECT_INDIA_DAS = 1;

    public static final int PROJECT_NAME = PROJECT_INDIA_DAS;
}
