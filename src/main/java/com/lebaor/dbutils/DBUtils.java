package com.lebaor.dbutils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.lebaor.utils.LogUtil;

public class DBUtils implements Runnable {
	Connection conn = null;
    String dbUrl;
    String dbUser;
    String dbPassword;
    String dbName;
    String charset;
    
    Thread t;
    boolean isRunning = false;
    
    protected static DBUtils singleton;
    	
	public DBUtils() {
		singleton = this;
		t= new Thread(this, "DBCheckThread");
		isRunning = true;
		t.start();
	}
	
	public static DBUtils getInstance() {
		return singleton;
	}
    
    public void connect() 
    		throws SQLException, ClassNotFoundException{
        Class.forName("com.mysql.jdbc.Driver");
        LogUtil.WEB_LOG.debug("connect_mysql: " + dbUrl + " " + dbUser);
        conn = DriverManager.getConnection(
                dbUrl,
                dbUser,
                dbPassword);
        
        LogUtil.WEB_LOG.debug("db init success!");
    }
    
    public void run() {
    	while (isRunning) {
    		try {
	    		Thread.sleep(1710000L);//<30分钟
	    		this.checkConnectionValid();
    		} catch (Exception e) {
    			break;
    		}
    	}
    }
    
    public void close() throws Exception {
    	if (conn != null) {
    		conn.close();
    	}
    }
    
    public void checkConnectionValid() {
    	
		String sql = "show processlist;";
		if (this.executeSql(sql, null)){
			LogUtil.WEB_LOG.debug("db check valid.");
		} else {
			//连接已经超时，重建连接
    		LogUtil.WEB_LOG.debug("db check invalid, try to reconnect...");
    		try {
    			if (conn != null) conn.close();
    		} catch (Exception ex) {}
    		try {
    			connect();
    			LogUtil.WEB_LOG.debug("db reconnect success.");
    		} catch (Exception ex) {
    			LogUtil.WEB_LOG.warn("db reconnect failure: ", ex);
    		}
		}
    		
    	
    }
    
    public Object executeQuery(String sql, Object[] values, ResultSetHandler handler) {
    	return executeQuery(sql, values, values, handler);
    }
    
    /**
     * 执行一个query sql
     * @param sql
     * @param handler
     * @return 得到的结果
     */
    public Object executeQuery(String sql, Object[] values, Object[] params, ResultSetHandler handler) {
		Connection conn = getConnection();
		if (conn == null) return null;
		
		String s = "";
		if (values != null) {
			for (Object o : values) {
	    		s+= o + " ";
	    	}
		}
		LogUtil.WEB_LOG.debug("executeQuery: " + sql + " values: " + s);
		
		PreparedStatement pQueryStmt = null;
        ResultSet rs = null;
        try{
        	pQueryStmt = conn.prepareStatement(sql);
        	if (values != null) {
	        	int i = 1;
	        	for (Object o : values) {
	        		pQueryStmt.setObject(i, o);
	        		i++;
	        	}
        	}
        	
        	rs = pQueryStmt.executeQuery();
        	if (rs != null) {
        		return handler.handle(rs, params);
        	}
        }catch(Exception e){
        	LogUtil.WEB_LOG.warn("executeQuery error: ", e);
        }finally{
            if (rs != null){
              try {rs.close(); }catch(Exception e){}
            }
            if (pQueryStmt != null){
              try {pQueryStmt.close();}catch(Exception e){}
            }
        }
		return null;
	}
    
    /**
     * 执行一个sql
     * @param sql
     * @param handler
     * @return 是否执行成功
     */
    public boolean executeSql(String sql, Object[] values) {
		Connection conn = getConnection();
		if (conn == null) return false;
		
		String s = "";
		if (values != null) {
			for (Object o : values) {
	    		s+= o + " ";
	    	}
		}
		LogUtil.WEB_LOG.debug("executeSql: " + sql + " values: " + s);
		
		PreparedStatement pQueryStmt = null;
        ResultSet rs = null;
        try{
        	pQueryStmt = conn.prepareStatement(sql);
        	if (values != null) {
	        	int i = 1;
	        	for (Object o : values) {
	        		pQueryStmt.setObject(i, o);
	        		i++;
	        	}
        	}
        	if (pQueryStmt.execute()) {
        		return true;
        	}
        }catch(Exception e){
        	LogUtil.WEB_LOG.warn("executeSql error: ", e);
            return false;
        }finally{
            if (rs != null){
              try {rs.close(); }catch(Exception e){}
            }
            if (pQueryStmt != null){
              try {pQueryStmt.close();}catch(Exception e){}
            }
        }
		return false;
	}
    

    /**
     * 执行一个sql
     * @param sql
     * @param handler
     * @return 是否执行成功
     */
    public boolean executeUpdate(String sql, Object[] values) {
		Connection conn = getConnection();
		if (conn == null) return false;
		
		String s = "";
		if (values != null) {
			for (Object o : values) {
	    		s+= o + " ";
	    	}
		}
		LogUtil.WEB_LOG.debug("executeUpdate: " + sql + " values: " + s);
		
		PreparedStatement pQueryStmt = null;
        try{
        	pQueryStmt = conn.prepareStatement(sql);
        	if (values != null) {
	        	int i = 1;
	        	for (Object o : values) { 
	        		pQueryStmt.setObject(i, o);
	        		i++;
	        	}
        	}
        	pQueryStmt.executeUpdate();
        	return true;
        }catch(Exception e){
        	LogUtil.WEB_LOG.warn("executeUpdate error: ", e);
        }finally{
            if (pQueryStmt != null){
              try {pQueryStmt.close();}catch(Exception e){}
            }
        }
		return false;
	}
    
	/**
	 * 创建table，先判断表是否存在，如果不存在，再创建。
	 * 如果是新创建的，则返回true，否则返回false
	 */
	public boolean createTable(String tableName, String createTableSql) {
		//先看看表是否存在
		String sql = "select TABLE_NAME from INFORMATION_SCHEMA.TABLES "
				+ "where TABLE_SCHEMA=? and TABLE_NAME=?";
		
		Boolean existsTable = (Boolean)executeQuery(sql, new Object[]{dbName, tableName},  
				new ResultSetHandler() {
			public Object handle(ResultSet rs, Object[] values) throws Exception{
				while (rs.next()) {
					return true;
				}
				return false;
			}
		});
		
		if (existsTable) return false;;
		
		//表不存在，创建一个
		executeSql(createTableSql, null);
		return true;
	}
	

	public static String genUpdateTableSql(String tableName, String[] colNames) {
		String sql = " UPDATE " + tableName + " SET ";
		int i = 0;
		for (String colName : colNames) {
			if (i == 0) {
				i++;
				continue;
			}
			if (i > 1) sql += ",";
			sql += " " + colName + "=?";
			i++;
		}		
		sql += " WHERE id=? ";
		return sql;
	}
	
	public static String genAddRowSql(String tableName, String[] colNames) {
		String sql = " INSERT INTO " + tableName + " (";
		int i = 0;
		for (String colName : colNames) {
			if (i == 0) {
				i++;
				continue;
			}
			if (i > 1) sql += ",";
			sql += " " + colName;
			i++;
		}		
		sql += ") VALUES (";
		
		for (int j = 0; j < colNames.length; j++) {
			if (j == 0) continue;
			if (j > 1) sql += ",";
			sql += "?";
		}
		sql += ")";
		return sql;
	}
	
	public static String genRemoveRowByIdSql(String tableName) {
		String sql = " DELETE FROM " + tableName + " WHERE id=? ";
		return sql;
	}
	
	public boolean isConnected() throws SQLException {
		return conn != null && !conn.isClosed();
	}
        
    public void release() throws SQLException{
        if (conn != null && !conn.isClosed()){
            conn.close();
        }
    }

	public Connection getConnection() {
		return conn;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
}
