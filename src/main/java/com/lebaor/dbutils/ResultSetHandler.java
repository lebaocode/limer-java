package com.lebaor.dbutils;

import java.sql.ResultSet;

public interface ResultSetHandler {
	/**
	 * 处理结果集
	 * @param rs
	 * @param params
	 * @return 返回一些结果
	 * @throws Exception
	 */
	public Object handle(ResultSet rs, Object[] params) throws Exception;
	
	
}
