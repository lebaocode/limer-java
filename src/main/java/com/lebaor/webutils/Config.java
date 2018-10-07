package com.lebaor.webutils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import com.lebaor.utils.LogFormatter;

public class Config {

    private static Logger logger = LogFormatter.getLogger(Config.class);
    
    private String driver;
    private String url;
    private String username;
    private String password;
    private String[] tables;
    private long reloadInterval;
    
    public String getDriver() {
        return driver;
    }
    public String getPassword() {
        return password;
    }
    public String[] getTables() {
        return tables;
    }
    public String getUrl() {
        return url;
    }
    public String getUsername() {
        return username;
    }
    public long getReloadInterval() {
        return reloadInterval;
    }
    
    public static Config load(File file) throws IOException {
        XMLConfiguration xmlConfig;
        try {
            xmlConfig = new XMLConfiguration(file);
        } catch (ConfigurationException e) {
            logger.log(Level.SEVERE, "CONFIG_FILE_NOT_FOUND", e);
            e.printStackTrace();
            throw new IOException("FILE NOT FOUND file : " + file);
        }
        Config config = new Config();
        try {
            config.driver = xmlConfig.getString("db.driver");
            config.url = xmlConfig.getString("db.url");
            config.username = xmlConfig.getString("db.username");
            config.password = xmlConfig.getString("db.password");
            ArrayList<String> tables = new ArrayList<String>();
            for(int i = 0; ; i++) {
                String table = xmlConfig.getString("db.table(" + i + ")");
                if(table == null) {
                    break;
                } else {
                    tables.add(table);
                }
            }
            config.tables = tables.toArray(new String[0]);
            config.reloadInterval = xmlConfig.getLong("reload-interval");
            return config;
        } catch (NoSuchElementException e) {
            logger.log(Level.SEVERE, "Load Config Failed, file: " + file, e);
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }
}
