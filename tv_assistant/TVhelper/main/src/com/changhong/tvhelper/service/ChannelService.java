package com.changhong.tvhelper.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.DateUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.tvhelper.domain.OrderProgram;
import com.changhong.tvhelper.domain.Program;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Created by Jack Wang
 */
public class ChannelService {

    private static final String TAG = "ChannelService";

    /*******************************************处理频道相关***********************************************************/

    /**
     * 获得频道播放的URL
     */
    public static String obtainChannlPlayURL(Map<String, Object> map) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("http://" + ClientSendCommandService.serverIP + ":8000/live.ts?freq=" + map.get("freq") + "&pmtPid=" + map.get("pmtPid")
                + "&aPid=" + map.get("aPid") + "&vPid=" + map.get("vPid")
                + "&dmxId=" + map.get("dmxId") + "&service_id=" + map.get("service_id"));

        String channelName = (String)map.get("service_name");
        if (channelName.contains("高清") || channelName.contains("HD")) {
            String vStreamType = (String)map.get("vStreamType");
            String aStreamType = (String)map.get("aStreamType");
            String convertV = obtainVideoType(vStreamType);
            String convertA = obtainAudioType(aStreamType);
            if (StringUtils.hasLength(convertV) && StringUtils.hasLength(convertA)) {
                buffer.append("&encode=1&encSrc=0&aStreamType=" + convertA + "&vStreamType=" + convertV);
            }
        }
        Log.i(TAG, buffer.toString());
        return buffer.toString();
    }

    /**
     *   public final static int CH_VIDEO_CODE_MPEG2 		= 0;
         public final static int CH_VIDEO_CODE_MPEG2_HD		= 1;
         public final static int CH_VIDEO_CODE_MPEG4_ASP    = 2;
         public final static int CH_VIDEO_CODE_MPEG4_ASP_A	= 3;
         public final static int CH_VIDEO_CODE_MPEG4_ASP_B	= 4;
         public final static int CH_VIDEO_CODE_MPEG4_ASP_C	= 5;
         public final static int CH_VIDEO_CODE_DIVX			= 6;
         public final static int CH_VIDEO_CODE_VC1			= 7;
         public final static int CH_VIDEO_CODE_H264			= 8;
         public final static int CH_VIDEO_CODE_AVS			= 9;
     */
    private static String obtainVideoType(String videoType) {
        if (!StringUtils.hasLength(videoType)) {
            return null;
        }

        if ("2".equals(videoType)) {
            return "0x02";
        } else if ("27".equals(videoType)) {
            return "0x1B";
        }
        return null;
    }

    /**
     *   public final static int CH_AUDIO_CODE_MPEG1		= 0;
         public final static int CH_AUDIO_CODE_MPEG2		= 1;
         public final static int CH_AUDIO_CODE_MP3			= 2;
         public final static int CH_AUDIO_CODE_AC3			= 3;
         public final static int CH_AUDIO_CODE_AAC_ADTS		= 4;
         public final static int CH_AUDIO_CODE_AAC_LOAS		= 5;
         public final static int CH_AUDIO_CODE_HEAAC_ADTS	= 6;
         public final static int CH_AUDIO_CODE_HEAAC_LOAS	= 7;
         public final static int CH_AUDIO_CODE_WMA			= 8;
         public final static int CH_AUDIO_CODE_AC3_PLUS		= 9;
         public final static int CH_AUDIO_CODE_LPCM			= 10;
         public final static int CH_AUDIO_CODE_DTS			= 11;
         public final static int CH_AUDIO_CODE_ATRAC		= 12;
     */
    private static String obtainAudioType(String audioType) {
        if (!StringUtils.hasLength(audioType)) {
            return null;
        }

        if ("3".equals(audioType)) {
            return "0x03";
        } else if ("4".equals(audioType)) {
            return "0x04";
        } else if ("6".equals(audioType) || "129".equals(audioType)) {
            return "0x06";
        }
        return null;
    }

    /*******************************************处理节目相关的***********************************************************/

    /**
     * 获得所有节目当前播放的节目
     */
    public Map<String, Program> searchCurrentChannelPlay() {
        Map<String, Program> currentPlaying = new HashMap<String, Program>();

        try {
            SQLiteDatabase database = MyApplication.databaseContainer.openEPGDatabase();
            if (database != null) {
                String weekIndex = String.valueOf(DateUtils.getWeekIndex(0));
                String currentTime = DateUtils.getCurrentTimeStamp();
                Cursor cursor = database.rawQuery("select i_ChannelIndex, str_eventName, str_startTime, str_endTime,str_ChannelName from epg_information where i_weekIndex = ? and str_startTime < ? and str_endTime >= ?",
                        new String[]{weekIndex, currentTime, currentTime});

                while (cursor.moveToNext()) {
                    String channelIndex = cursor.getString(0);
                    String eventName = cursor.getString(1);
                    String startTime = cursor.getString(2);
                    String endTime = cursor.getString(3);
                    String channelName = cursor.getString(4);
                    // 获取该图片的父路径名
                    Program program = new Program(channelIndex, eventName, startTime, endTime,channelName);
                    currentPlaying.put(channelName, program);
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return currentPlaying;
    }


    public List<Program> searchCurrentChannelPlayByName(String channelName) {
        List<Program> programList = new ArrayList<Program>();
        SQLiteDatabase database = MyApplication.databaseContainer.openEPGDatabase();
        if (database != null) {
            String weekIndex = String.valueOf(DateUtils.getWeekIndex(0));
            String currentTime = DateUtils.getCurrentTimeStamp();
            Cursor cursor = database.rawQuery("select  i_ChannelIndex,str_eventName, str_startTime, str_endTime from epg_information  where  i_weekIndex = ? AND str_ChannelName = ? AND str_endTime > ? AND str_eventName !='无节目信息'",
                    new String[]{weekIndex,channelName,currentTime});


            while (cursor.moveToNext()) {
            	String channelIndex = cursor.getString(0);
                String eventName = cursor.getString(1);
                String startTime = cursor.getString(2);
                String endTime = cursor.getString(3);
                Program program = new Program(channelIndex, eventName, startTime, endTime,channelName);
                programList.add(program);
            }
            cursor.close();
        }

        return programList;
    }

    /**
     * 获得频道节目详情
     */
    public Map<String, List<Program>> searchProgramInfosByName (String channelName) {
        Map<String, List<Program>> programs = new HashMap<String, List<Program>>();

        SQLiteDatabase database = MyApplication.databaseContainer.openEPGDatabase();
        if (database != null) {
            Cursor cursor = database.rawQuery("select i_ChannelIndex,i_weekIndex, str_eventName, str_startTime, str_endTime from epg_information where str_ChannelName = ? " +
                            "order by str_startTime asc",
                    new String[]{channelName});

            while (cursor.moveToNext()) {
            	String channelIndex = cursor.getString(0);
                String weekIndex = cursor.getString(1);
                String eventName = cursor.getString(2);
                String startTime = cursor.getString(3);
                String endTime = cursor.getString(4);

                // 获取该图片的父路径名
                Program program = new Program(channelIndex, weekIndex, eventName, startTime, endTime,channelName);
                List<Program> list = programs.get(String.valueOf(weekIndex));
                if (list == null) {
                    list = new ArrayList<Program>();
                }
                list.add(program);
                programs.put(String.valueOf(weekIndex), list);
            }
            cursor.close();
        }

        return programs;
    }
    

    public static synchronized Collection<Map<String, Object>> searchProgramByText(String text)
    {
    	HashMap<String,String> map = new HashMap<String,String>();
    	Set<Map<String, Object>> listMap = new HashSet<Map<String, Object>>();
    	
    	SQLiteDatabase database = MyApplication.databaseContainer.openEPGDatabase();
    	if (database == null) {
			return listMap;
		}
    	
    	Cursor cursor = null;
    	String currentTime = DateUtils.getCurrentTimeStamp();
    	try {
    		cursor = database.rawQuery("SELECT i_ChannelIndex,str_ChannelName FROM epg_information WHERE str_eventName LIKE ? AND str_startTime < ? AND str_endTime > ? COLLATE NOCASE", new String[]{"%" + text + "%",currentTime,currentTime});
	    	while (cursor.moveToNext()) {			
				map.put(cursor.getString(0),cursor.getString(1));			
			}
    	} catch (Exception e) {
    		cursor.close();
    		return listMap;
		}
    	
    	if(cursor != null)
    		cursor.close();
    	
    	for (Entry<String, String> mapTemp : map.entrySet()) {
    		HashMap<String, Object> temp = new HashMap<String, Object>();
    		temp.put("channel_index", mapTemp.getKey());
    		temp.put("service_name", mapTemp.getValue());
    		listMap.add(temp);
		}
    	return listMap;
    }
    

    /**
     * 保存频道收藏信息
     */
    public boolean channelShouCang(String channelServiceId) {
        try {
            String insert = "INSERT INTO channel_shoucang (service_id) VALUES (?)";
            SQLiteDatabase database = MyApplication.databaseContainer.getWritableDatabase();
            database.execSQL(insert, new Object[]{channelServiceId});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 取消频道收藏
     */
    public boolean cancelChannelShouCang(String channelServiceId) {
        try {
            String delete = "DELETE FROM channel_shoucang WHERE service_id = ?";
            SQLiteDatabase database = MyApplication.databaseContainer.getWritableDatabase();
            database.execSQL(delete, new Object[]{channelServiceId});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获得已经收藏的节目信息
     */
    public List<String> getAllChannelShouCangs() {
        List<String> all = new ArrayList<String>();

        SQLiteDatabase database = MyApplication.databaseContainer.getWritableDatabase();
        Cursor cursor = database.rawQuery("select service_id from channel_shoucang", null);

        while (cursor.moveToNext()) {
            String channelServiceId = cursor.getString(0);
            all.add(channelServiceId);
        }
        cursor.close();

        return all;
    }

    //预约节目
    public boolean saveOrderProgram(OrderProgram orderProgram) {
        try {
            String insert = "INSERT INTO order_program (order_date, channel_index, week_index, program_start_time,program_end_time,status,program_name,channel_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            SQLiteDatabase database = MyApplication.databaseContainer.getWritableDatabase();
            database.execSQL(insert, new Object[]{orderProgram.getOrderDate(), orderProgram.getChannelIndex(), orderProgram.getWeekIndex(), orderProgram.getProgramStartTime(), orderProgram.getProgramEndTime(), orderProgram.getStatus(), orderProgram.getProgramName(), orderProgram.getChannelName()});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean deleteOrderProgram(String dateOfToday) {
        try {
            String delete = "DELETE FROM order_program WHERE order_date < '" + dateOfToday + "'";
            SQLiteDatabase database = MyApplication.databaseContainer.getWritableDatabase();
            database.execSQL(delete);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteOrderProgram(String programName, String orderDate) {
        try {
            String delete = "DELETE FROM order_program WHERE order_program.program_name = ? AND order_program.order_date = ?";
            SQLiteDatabase database = MyApplication.databaseContainer.getWritableDatabase();
            database.execSQL(delete, new Object[]{programName, orderDate});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean deleteOrderProgramByWeek(String programName, String weekName) {
        try {
            String delete = "DELETE FROM order_program WHERE order_program.program_name = ? AND order_program.week_index = ?";
            SQLiteDatabase database = MyApplication.databaseContainer.getWritableDatabase();
            database.execSQL(delete, new String[]{programName, weekName});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    public OrderProgram findOrderProgramByStartTime(String startTime, String weekIndex) {
        String find = "SELECT * FROM order_program WHERE order_program.program_start_time = ? AND order_program.week_index = ?";
        SQLiteDatabase database = MyApplication.databaseContainer.getWritableDatabase();
        Cursor cursor = database.rawQuery(find, new String[]{startTime, weekIndex});
        OrderProgram orderProgram = new OrderProgram();
        while (cursor.moveToNext()) {
            orderProgram.setId(cursor.getInt(0));
            orderProgram.setOrderDate(cursor.getString(1));
            orderProgram.setChannelIndex(cursor.getString(2));
            orderProgram.setWeekIndex(cursor.getString(3));
            orderProgram.setProgramStartTime(cursor.getString(4));
            orderProgram.setProgramEndTime(cursor.getString(5));
            orderProgram.setStatus(cursor.getString(6));
            orderProgram.setProgramName(cursor.getString(7));
            orderProgram.setChannelName(cursor.getString(8));
        }
        cursor.close();
        return orderProgram;
    }

    public List<OrderProgram> findOrderProgramsByWeek(String weekIndex) {
        String findAll = "SELECT * FROM order_program where order_program.week_index = ?";
        SQLiteDatabase database = MyApplication.databaseContainer.getWritableDatabase();
        Cursor cursor = database.rawQuery(findAll, new String[]{weekIndex});
        List<OrderProgram> orderPrograms = new ArrayList<OrderProgram>();
        while (cursor.moveToNext()) {
            OrderProgram orderProgram = new OrderProgram();
            orderProgram.setId(cursor.getInt(0));
            orderProgram.setOrderDate(cursor.getString(1));
            orderProgram.setChannelIndex(cursor.getString(2));
            orderProgram.setWeekIndex(cursor.getString(3));
            orderProgram.setProgramStartTime(cursor.getString(4));
            orderProgram.setProgramEndTime(cursor.getString(5));
            orderProgram.setStatus(cursor.getString(6));
            orderProgram.setProgramName(cursor.getString(7));
            orderProgram.setChannelName(cursor.getString(8));
            orderPrograms.add(orderProgram);
        }
        cursor.close();
        return orderPrograms;
    }

    public List<OrderProgram> findAllOrderPrograms() {
        String findAll = "SELECT * FROM order_program ORDER BY order_program.order_date";
        SQLiteDatabase database = MyApplication.databaseContainer.getWritableDatabase();
        Cursor cursor = database.rawQuery(findAll, null);
        List<OrderProgram> orderPrograms = new ArrayList<OrderProgram>();
        while (cursor.moveToNext()) {
            OrderProgram orderProgram = new OrderProgram();
            orderProgram.setId(cursor.getInt(0));
            orderProgram.setOrderDate(cursor.getString(1));
            orderProgram.setChannelIndex(cursor.getString(2));
            orderProgram.setWeekIndex(cursor.getString(3));
            orderProgram.setProgramStartTime(cursor.getString(4));
            orderProgram.setProgramEndTime(cursor.getString(5));
            orderProgram.setStatus(cursor.getString(6));
            orderProgram.setProgramName(cursor.getString(7));
            orderProgram.setChannelName(cursor.getString(8));
            orderPrograms.add(orderProgram);
        }
        cursor.close();
        return orderPrograms;
    }


}
