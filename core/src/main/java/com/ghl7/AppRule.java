package com.ghl7;

/**
 * @Auther WenLong
 * @Date 2024/7/11 11:34
 * @Description
 **/
public class AppRule {
    public String sqlUrl;
    public String userName;
    public String passwd;
    public String mid;
    public int startPort;
    public int targetPort;
    public String targetHost;
    public boolean useSTL;
    public String logDir;

    @Override
    public String toString() {
        return "AppRule{" +
            "sqlUrl='" + sqlUrl + '\'' +
            ", userName='" + userName + '\'' +
            ", passwd='" + passwd + '\'' +
            ", mid='" + mid + '\'' +
            ", startPort='" + startPort + '\'' +
            ", targetPort='" + targetPort + '\'' +
            ", targetHost='" + targetHost + '\'' +
            ", userSTL=" + useSTL +
            '}';
    }
}
