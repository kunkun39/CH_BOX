package com.changhong.tvhelper.domain;

/**
 * Created by Jack Wang
 */
public class Program {

    private String channelIndex;

    private String weekIndex;

    private String programName;

    private String programStartTime;

    private String programEndTime;

    public Program(String channelIndex, String programName, String programStartTime, String programEndTime) {
        this.channelIndex = channelIndex;
        this.programName = programName;
        this.programStartTime = programStartTime;
        this.programEndTime = programEndTime;
    }

    public Program(String channelIndex, String weekIndex, String programName, String programStartTime, String programEndTime) {
        this.channelIndex = channelIndex;
        this.weekIndex = weekIndex;
        this.programName = programName;
        this.programStartTime = programStartTime;
        this.programEndTime = programEndTime;
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
}
