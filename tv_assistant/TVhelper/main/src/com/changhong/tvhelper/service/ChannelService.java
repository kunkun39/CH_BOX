package com.changhong.tvhelper.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.DateUtils;
import com.changhong.tvhelper.domain.OrderProgram;
import com.changhong.tvhelper.domain.Program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jack Wang
 */
public class ChannelService {

    private static final String TAG = "ChannelService";

    public static String obtainChannlPlayURL(Map<String, Object> map) {
        return "http://" + ClientSendCommandService.serverIP + ":8000/live.ts?freq=" + map.get("freq") + "&pmtPid=" + map.get("pmtPid")
                + "&aPid=" + map.get("aPid") + "&vPid=" + map.get("vPid")
                + "&dmxId=" + map.get("dmxId") + "&service_id=" + map.get("service_id");
    }

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
                Cursor cursor = database.rawQuery("select i_ChannelIndex, str_eventName, str_startTime, str_endTime from epg_information where i_weekIndex = ? and str_startTime < ? and str_endTime >= ?",
                        new String[]{weekIndex, currentTime, currentTime});

                while (cursor.moveToNext()) {
                    String channelIndex = cursor.getString(0);
                    String eventName = cursor.getString(1);
                    String startTime = cursor.getString(2);
                    String endTime = cursor.getString(3);

                    // 获取该图片的父路径名
                    Program program = new Program(channelIndex, eventName, startTime, endTime);
                    currentPlaying.put(channelIndex, program);
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return currentPlaying;
    }


    public List<Program> searchCurrentChannelPlayByIndex(String channelIndex) {
        List<Program> programList = new ArrayList<Program>();
        SQLiteDatabase database = MyApplication.databaseContainer.openEPGDatabase();
        if (database != null) {
            String weekIndex = String.valueOf(DateUtils.getWeekIndex(0));
            String currentTime = DateUtils.getCurrentTimeStamp();
            Cursor cursor = database.rawQuery("select  str_eventName, str_startTime, str_endTime from epg_information  where  i_weekIndex = ? AND i_ChannelIndex = ? AND str_endTime > ? AND str_eventName !='无节目信息'",
                    new String[]{weekIndex,channelIndex,currentTime});


            while (cursor.moveToNext()) {
                String eventName = cursor.getString(0);
                String startTime = cursor.getString(1);
                String endTime = cursor.getString(2);
                Program program = new Program(channelIndex, eventName, startTime, endTime);
                programList.add(program);
            }
            cursor.close();
        }

        return programList;
    }

    /**
     * 获得频道节目详情
     */
    public Map<String, List<Program>> searchProgramInfos(String channelIndex) {
        Map<String, List<Program>> programs = new HashMap<String, List<Program>>();

        SQLiteDatabase database = MyApplication.databaseContainer.openEPGDatabase();
        if (database != null) {
            Cursor cursor = database.rawQuery("select i_weekIndex, str_eventName, str_startTime, str_endTime from epg_information where i_ChannelIndex = ? " +
                            "order by str_startTime asc",
                    new String[]{channelIndex});

            while (cursor.moveToNext()) {
                String weekIndex = cursor.getString(0);
                String eventName = cursor.getString(1);
                String startTime = cursor.getString(2);
                String endTime = cursor.getString(3);

                // 获取该图片的父路径名
                Program program = new Program(channelIndex, weekIndex, eventName, startTime, endTime);
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
