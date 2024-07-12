package com.ghl7.dao;

import com.ghl7.pojo.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @Auther WenLong
 * @Date 2024/6/13 15:03
 * @Description sql映射
 **/
public class SQLMapper {
    private Connection connection;
    private boolean log;
    public SQLMapper(Connection connection){
        this(connection,false);
    }
    public SQLMapper(Connection connection,boolean log){
        this.connection = connection;
    }

    public Statement getStatement() {

        Statement statement = null;

        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException throwables) {
            if (log){
                System.out.println("创建statement失败");
            }

            throwables.printStackTrace();
        }

        return statement;
    }

    public User getUser(String barcode,String mid){
        String sql = "";
        User user = new User();
        return user;
    }
    public ResultSet query(String sql) {
        ResultSet resultSet = null;
        try {
            resultSet = getStatement().executeQuery(sql);
            if (log){
                System.out.println("查询成功:"+sql);
                System.out.println("查询结果:"+resultSet);
            }

        } catch (SQLException throwables) {
            if (log){
                System.out.println("查询失败:"+sql);
            }

            throwables.printStackTrace();
        }
        return resultSet;
    }
    public int update(String sql){
        int num = 0;
        try {
            num = getStatement().executeUpdate(sql);
            if (log){
                System.out.println("更新成功:"+sql);
                System.out.println("更新数量:"+num);
            }

        } catch (SQLException throwables) {
            if (log){
                System.out.println("更新失败:"+sql);
            }

            throwables.printStackTrace();
        }
        return num;
    }

    public int insert(String sql){
        int num = 0;
        try {
            num = getStatement().executeUpdate(sql);
            if (log){
                System.out.println("插入成功:"+sql);
                System.out.println("插入数量:"+num);
            }

        } catch (SQLException throwables) {
            if (log){
                System.out.println("插入失败:"+sql);
            }

            throwables.printStackTrace();
        }
        return num;
    }
}
