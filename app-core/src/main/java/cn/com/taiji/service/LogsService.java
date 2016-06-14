package cn.com.taiji.service;

import java.util.Map;

import cn.com.taiji.dto.LogsDto;

public interface LogsService {
    
	/**
	 * 条件查询日志模块内容
	 * @param dto
	 * @return
	 */
	Map<String, Object> search(LogsDto dto);
	/**
	 * 日志统计图查询
	 */
    Map<String,Object> chartsStatistic(LogsDto dto);
}
