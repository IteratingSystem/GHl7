package com.ghl7.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @Auther WenLong
 * @Date 2024/6/13 15:03
 * @Description 数据源工厂
 **/
public class ConnectionFactory {
    public static Connection connection;
    public static Connection getConnection(ConnectionType type){
        String url = "";
        String userName = "";
        String passWd = "";

        switch (type){
            case PING_HU:
                url = "jdbc:sqlserver://192.168.201.14:1433;databaseName=clabsdbc";
                userName = "lis";
                passWd = "slis";
                break;
            case SHE_KANG:
                url = "jdbc:sqlserver://192.168.0.6:1433;databaseName=clabsdbc";
                userName = "lis";
                passWd = "slis";
                break;
        }


        try {
            connection = DriverManager.getConnection(url, userName, passWd);
            System.out.println("连接数据库成功");
        } catch (SQLException throwables) {
            System.out.println("连接数据库失败");
        }
        return connection;
    }
}
