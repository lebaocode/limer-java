package com.lebaor.dbutils;

import java.sql.Date;

public class ResourceCell {
	public static final int DEFAULT = 0;
    public static final int UNPASS = 1;
    public static final int PASS = 2;
    public static final int PUBLISH = 3;

    private int id = 0;
    private String key = null;
    private String value = null;
    private int state = DEFAULT;
    private String creator = null;
    private Date createTime = null;
    private String assessor = null;
    private Date assessTime = null;
    private String assessNote = null;
    private int level = -1;
    private String reserved1 = null;
    private String reserved2 = null;
    
    public String getAssessNote() {
        return assessNote;
    }
    public void setAssessNote(String assessNote) {
        this.assessNote = assessNote;
    }
    public String getAssessor() {
        return assessor;
    }
    public void setAssessor(String assessor) {
        this.assessor = assessor;
    }
    public Date getAssessTime() {
        return assessTime;
    }
    public void setAssessTime(Date assessTime) {
        this.assessTime = assessTime;
    }
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public String getCreator() {
        return creator;
    }
    public void setCreator(String creator) {
        this.creator = creator;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public String getReserved1() {
        return reserved1;
    }
    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1;
    }
    public String getReserved2() {
        return reserved2;
    }
    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }
    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
