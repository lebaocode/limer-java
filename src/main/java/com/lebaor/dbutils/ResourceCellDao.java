package com.lebaor.dbutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.dbcp.BasicDataSource;


public class ResourceCellDao {

    private BasicDataSource dataSource;
    
    private static ResourceCell translate(ResultSet rs) throws SQLException {
        ResourceCell cell = new ResourceCell();
        cell.setId(rs.getInt("id"));
        cell.setKey(rs.getString("key"));
        cell.setValue(rs.getString("value"));
        cell.setState(rs.getInt("state"));
        cell.setCreator(rs.getString("creator"));
        cell.setCreateTime(rs.getDate("createTime"));
        cell.setAssessor(rs.getString("assessor"));
        cell.setAssessTime(rs.getDate("assessTime"));
        cell.setAssessNote(rs.getString("assessNote"));
        cell.setLevel(rs.getInt("level"));
        cell.setReserved1(rs.getString("reserved1"));
        cell.setReserved2(rs.getString("reserved2"));
        return cell;
    }
    
    public void setDataSource(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public ResourceCell[] query(String tablename) throws SQLException {
        if(dataSource == null)
            throw new SQLException("data source need init");
        Connection conn = dataSource.getConnection();
        if(conn == null)
            throw new SQLException("fail to get connection");
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement("select * from " + tablename);
            rs = pst.executeQuery();
            ArrayList<ResourceCell> list = new ArrayList<ResourceCell>();
            while(rs.next()) {
                list.add(translate(rs));
            }
            return list.toArray(new ResourceCell[list.size()]);
        } finally {
            if(rs != null)
                rs.close();
            if(pst != null)
                pst.close();
            conn.close();
        }
    }
    
    public ResourceCell[] queryByState(String tablename, int state) throws SQLException {
        if(dataSource == null)
            throw new SQLException("data source need init");
        Connection conn = dataSource.getConnection();
        if(conn == null)
            throw new SQLException("fail to get connection");
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement("select * from " + tablename + " where state=?");
            pst.setInt(1, state);
            rs = pst.executeQuery();
            ArrayList<ResourceCell> list = new ArrayList<ResourceCell>();
            while(rs.next()) {
                list.add(translate(rs));
            }
            return list.toArray(new ResourceCell[list.size()]);
        } finally {
            if(rs != null)
                rs.close();
            if(pst != null)
                pst.close();
            conn.close();
        }
    }
    
    public ResourceCell query(String tablename, int id) throws SQLException {
        if(dataSource == null)
            throw new SQLException("data source need init");
        Connection conn = dataSource.getConnection();
        if(conn == null)
            throw new SQLException("fail to get connection");
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement("select * from " + tablename + " where id=?");
            pst.setInt(1, id);
            rs = pst.executeQuery();
            if(rs.next()) {
                return translate(rs);
            } else {
                return null;
            }
        } finally {
            if(rs != null)
                rs.close();
            if(pst != null)
                pst.close();
            conn.close();
        }
    }
    
    public int insert (String tablename, ResourceCell cell) throws SQLException {
        if(dataSource == null)
            throw new SQLException("data source need init");
        Connection conn = dataSource.getConnection();
        if(conn == null)
            throw new SQLException("fail to get connection");
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement("insert into " + tablename + " (`id`, `key`, `value`, `state`, `creator`, " +
                        "`assessor`, `createTime`, `assessTime`, `assessNote`, `level`, `reserved1`, `reserved2`) " +
                        "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pst.setInt(1, cell.getId());
            pst.setString(2, cell.getKey());
            pst.setString(3, cell.getValue());
            pst.setInt(4, cell.getState());
            pst.setString(5, cell.getCreator());
            pst.setString(6, cell.getAssessor());
            pst.setDate(7, cell.getCreateTime());
            pst.setDate(8, cell.getAssessTime());
            pst.setString(9, cell.getAssessNote());
            pst.setInt(10, cell.getLevel());
            pst.setString(11, cell.getReserved1());
            pst.setString(12, cell.getReserved2());
            pst.executeUpdate();
            pst.close();
            pst = conn.prepareStatement("select last_insert_id()");
            rs = pst.executeQuery();
            if(rs.next())
                return rs.getInt(1);
            else 
                return -1;
        } finally {
            if(rs != null)
                rs.close();
            if(pst != null)
                pst.close();
            conn.close();
        }
    }
    
    public void update (String tablename, ResourceCell cell) throws SQLException {
        if(dataSource == null)
            throw new SQLException("data source need init");
        Connection conn = dataSource.getConnection();
        if(conn == null)
            throw new SQLException("fail to get connection");
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement("update " + tablename + " set `key`=?, `value`=?, `state`=?, `creator`=?, " +
                        "`assessor`=?, `createTime`=?, `assessTime`=?, `assessNote`=?, `level`=?, `reserved1`=?, `reserved2`=? where `id`=?");
            pst.setString(1, cell.getKey());
            pst.setString(2, cell.getValue());
            pst.setInt(3, cell.getState());
            pst.setString(4, cell.getCreator());
            pst.setString(5, cell.getAssessor());
            pst.setDate(6, cell.getCreateTime());
            pst.setDate(7, cell.getAssessTime());
            pst.setString(8, cell.getAssessNote());
            pst.setInt(9, cell.getLevel());
            pst.setString(10, cell.getReserved1());
            pst.setString(11, cell.getReserved2());
            pst.setInt(12, cell.getId());
            pst.executeUpdate();
            return;
        } finally {
            if(pst != null)
                pst.close();
            conn.close();
        }
    }
    
    public void delete (String tablename, int id) throws SQLException {
        if(dataSource == null)
            throw new SQLException("data source need init");
        Connection conn = dataSource.getConnection();
        if(conn == null)
            throw new SQLException("fail to get connection");
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement("delete from " + tablename + " where id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
            return;
        } finally {
            if(pst != null)
                pst.close();
            conn.close();
        }
    }
    
    public void createTable(String tablename) throws SQLException {
        if(dataSource == null)
            throw new SQLException("data source need init");
        Connection conn = dataSource.getConnection();
        if(conn == null)
            throw new SQLException("fail to get connection");
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement("create table " + tablename + 
                        " (`id` integer auto_increment primary key, " +
                        "`key` varchar(40), `value` text, `state` integer, " +
                        "`creator` varchar(40), `createTime` datetime, " +
                        "`assessor` varchar(40), `assessTime` datetime, " +
                        "`assessNote` text, " +
                        "`level` integer, `reserved1` varchar(255), " +
                        "`reserved2` varchar(255))");
            pst.executeUpdate();
            return;
        } finally {
            if(pst != null)
                pst.close();
            conn.close();
        }
    }
    
    public void dropTable(String tablename) throws SQLException {
        if(dataSource == null)
            throw new SQLException("data source need init");
        Connection conn = dataSource.getConnection();
        if(conn == null)
            throw new SQLException("fail to get connection");
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement("drop table " + tablename);
            pst.executeUpdate();
            return;
        } finally {
            if(pst != null)
                pst.close();
            conn.close();
        }
    }
}
