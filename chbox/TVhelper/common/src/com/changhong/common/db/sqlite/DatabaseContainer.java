package com.changhong.common.db.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.changhong.common.system.MyApplication;
import com.changhong.common.widgets.IpSelectorDataServer;

import java.io.File;

/**
 * Created by Jack Wang
 * <p/>
 * 数据库支持的数据类型，TEXT，VARCHAR, INTEGER, REAL, BLOG,
 */
public class DatabaseContainer extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "tvhelper.db";

    private final static String EPG_DATABASE_NAME = "epg_database.db";

    public final static String TABLE_NAME_SEARCH_HEAT = "search_heat";

    private static int CURRENT_VERSION = 4;

    private SQLiteDatabase epgDatabase;
    
    private static DatabaseContainer databaseContainer = null;

    protected DatabaseContainer(Context context) {
        super(context, DATABASE_NAME, null, CURRENT_VERSION);
    }

    public static synchronized DatabaseContainer getInstance(Context context) {
        if (databaseContainer == null) {
            databaseContainer = new DatabaseContainer(context);
        }

        return databaseContainer;
    }
    
    public SQLiteDatabase openEPGDatabase() {
        try {
            File epgDBFile = new File(MyApplication.epgDBCachePath.getAbsolutePath(), getEpgDatabaseName());
            if (epgDBFile.exists() && epgDatabase == null) {
                epgDatabase = SQLiteDatabase.openDatabase(epgDBFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return epgDatabase;
    }

    /**
     * 当更新了EPG的时候，都需要重新打开一次DB
     * @return 
     */
    public SQLiteDatabase reopenEPGDatabase() {
        try {
            File epgDBFile = new File(MyApplication.epgDBCachePath.getAbsolutePath(), getEpgDatabaseName());
            if (epgDBFile.exists()) {
                epgDatabase = SQLiteDatabase.openDatabase(epgDBFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return epgDatabase;
    }
    /**
     * 获取EPF数据库名
     */
    public static String getEpgDatabaseName(){
        String ip = IpSelectorDataServer.getInstance().getCurrentIp();
        if (ip == null
                || ip.isEmpty())
        {
            return  null;
        }

        return ip + "-" + EPG_DATABASE_NAME;
    }

    /**
     * 数据库第一次创建的时候别调用的
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE music_lrc" +
                "(music_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "singer VARCHAR(30), " +
                "name VARCHAR(100), " +
                "path VARCHAR(200))");

        db.execSQL("CREATE TABLE channel_shoucang" +
                "(shoucang_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "service_id VARCHAR(30))");

        db.execSQL("CREATE TABLE order_program" +
                "(channel_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_date VARCHAR(100), " +
                "channel_index VARCHAR(100), " +
                "week_index VARCHAR(100), " +
                "program_start_time VARCHAR(100)," +
                "program_end_time VARCHAR(100)," +
                "status VARCHAR(100)," +
                "program_name VARCHAR(200)," +
                "channel_name VARCHAR(200))");

        db.execSQL("CREATE TABLE " + TABLE_NAME_SEARCH_HEAT
                + "("
                + "search_name VARCHAR(100) PRIMARY KEY,"
                + "search_time DATE,"
                + "search_count INTEGER"
                + ")");

    }

    /**
     * 数据库版本更新的时候被调用
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	if (newVersion == 1) {
			db.execSQL("CREATE TABLE channel_shoucang" +
	                "(shoucang_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	                "service_id VARCHAR(30))");

	        db.execSQL("CREATE TABLE order_program" +
	                "(channel_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	                "order_date VARCHAR(100), " +
	                "channel_index VARCHAR(100), " +
	                "week_index VARCHAR(100), " +
	                "program_start_time VARCHAR(100)," +
	                "program_end_time VARCHAR(100)," +
	                "status VARCHAR(100)," +
	                "program_name VARCHAR(200)," +
	                "channel_name VARCHAR(200))");
		}
    	else {
			if (oldVersion < 4 && newVersion >= 4) {
				db.execSQL(
		                "CREATE TABLE search_heat" + TABLE_NAME_SEARCH_HEAT
		                        + "("
		                        + "search_name VARCHAR(100) PRIMARY KEY,"
		                        + "search_time DATE,"
		                        + "search_count INTEGER"
		                        + ")"
		        );
			}
		}        
    }
}
