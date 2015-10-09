package com.changhong.tvhelper.domain;

import java.io.Serializable;

/**
 * Created by maren on 2015/4/20.
 */
public class OrderProgram implements Serializable{
    private  int id;

    private String orderDate;

    private String channelName;

    private String channelIndex;

    private String weekIndex;

    private String programName;

    private String programStartTime;

    private String programEndTime;

    private String status;

    public OrderProgram()
    {
    	
    }
    
    public OrderProgram(Program program)
    {        
        this.channelName = program.getChannelName();

        this.channelIndex = program.getChannelIndex();

        this.weekIndex = program.getWeekIndex();

        this.programName = program.getProgramName();

        this.programStartTime = program.getProgramStartTime();

        this.programEndTime = program.getProgramEndTime();
    }
    
    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelIndex() {
        return channelIndex;
    }

    public void setChannelIndex(String channelIndex) {
        this.channelIndex = channelIndex;
    }

    public String getWeekIndex() {
        return weekIndex;
    }

    public void setWeekIndex(String weekIndex) {
        this.weekIndex = weekIndex;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramStartTime() {
        return programStartTime;
    }

    public void setProgramStartTime(String programStartTime) {
        this.programStartTime = programStartTime;
    }

    public String getProgramEndTime() {
        return programEndTime;
    }

    public void setProgramEndTime(String programEndTime) {
        this.programEndTime = programEndTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
