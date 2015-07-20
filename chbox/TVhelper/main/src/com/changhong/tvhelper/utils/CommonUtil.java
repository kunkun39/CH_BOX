package com.changhong.tvhelper.utils;

import java.util.HashMap;
import java.util.Map;

import com.changhong.tvhelper.domain.OrderProgram;
import com.changhong.tvhelper.domain.Program;

public class CommonUtil {

	public static Map<String, Object> ProgramToRawData(Program program)
	{
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("channel_index", program.getChannelIndex());
		dataMap.put("week_index", program.getWeekIndex());
		dataMap.put("program_name", program.getProgramName());
		dataMap.put("str_startTime", program.getProgramStartTime());
		dataMap.put("str_endTime", program.getProgramEndTime());
		dataMap.put("service_name", program.getChannelName());
		
		return dataMap;
	}
	
	public static Program RawDataToProgram(Map<String, Object> dataMap)
	{
		return new Program((String)dataMap.get("channel_index")
        		, (String)dataMap.get("week_index")
        		, (String)dataMap.get("program_name")
        		, (String)dataMap.get("str_startTime")
        		, (String)dataMap.get("str_endTime")
        		, (String)dataMap.get("service_name"));
	}
	
	public static OrderProgram ProgramToOrderProgram(Program program)
	{
		return new OrderProgram(program);
	}
	
	public static Program OrderProgramToProgram(OrderProgram orderProgram)
	{
		return new Program(orderProgram.getChannelIndex()
				, orderProgram.getWeekIndex()
				, orderProgram.getProgramName()
				, orderProgram.getProgramStartTime()
				, orderProgram.getProgramEndTime()
				, orderProgram.getChannelName());
	}
}
