package com.ghl7;

public enum Paths {
    LOG_DIR("log"),
    APP_RULE_PATH("appRule.json");
    private String path;
    Paths(String path){
        setPath(path);
    }
    public String getPath(){
        return path;
    }
    public void setPath(String path){
        this.path = path;
    }
}
