package com.lebaor.dbutils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.dbcp.BasicDataSource;

import com.lebaor.utils.LogFormatter;
import com.lebaor.webutils.Config;

public class Resource extends TimerTask {
    private static Logger logger = LogFormatter.getLogger(Resource.class);
    
    private Config config;
    private HashMap<String, ArrayList<String>> map;
    private ResourceCellDao rcd;
    
    public Resource(Config config) {
        this.config = config;
        this.map = null;
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(config.getDriver());
        dataSource.setUrl(config.getUrl());
        dataSource.setUsername(config.getUsername());
        dataSource.setPassword(config.getPassword());
        rcd = new ResourceCellDao();
        rcd.setDataSource(dataSource);
        Timer timer = new Timer("reload_timer");
        timer.schedule(this, 0, config.getReloadInterval());
    }
    
    public String[] get(String key) {
        if(map != null) {
            ArrayList<String> list = map.get(key);
            if(list != null) {
                return list.toArray(new String[0]);
            }
        }
        return null;
    }

    @Override
    public void run() {
        HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
        try {
            for(String table : config.getTables()) {
                ResourceCell[] cells = rcd.queryByState(table, ResourceCell.PUBLISH);
                for(ResourceCell cell : cells) {
                    String key = table + ":" + cell.getKey();
                    String value = cell.getValue();
                    ArrayList<String> list = map.get(key);
                    if(list == null) {
                        list = new ArrayList<String>();
                        list.add(value);
                        map.put(key, list);
                    } else {
                        list.add(value);
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "RELOAD FAILED", e);
            return;
        }
        this.map = map;
    }

}
