package com.ghl7.dao;

import com.ghl7.Log;
import com.ghl7.pojo.Patient;

import java.sql.*;

/**
 * @Auther WenLong
 * @Date 2024/7/12 10:18
 * @Description
 **/
public class SQLMapper {
    private static final String URL = "jdbc:sqlserver://192.168.0.6:1433;databaseName=clabsdbc";
    private static final String USER_NAME = "lis";
    private static final String PASS_WORLD = "slis";

    private static SQLMapper instance;
    private Connection connection;
    private SQLMapper(){
        try {
            connection = DriverManager.getConnection(URL, USER_NAME, PASS_WORLD);
        } catch (SQLException e) {
            Log.log("Create connection failed:"+e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static SQLMapper getInstance() {
        if (instance == null){
            instance = new SQLMapper();
        }
        return instance;
    }
    private Statement getStatement() {
        Statement statement = null;
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException e) {
            Log.log("Create statement failed:"+e.getMessage());
            e.printStackTrace();
        }
        return statement;
    }

    public ResultSet query(String sql) {
        ResultSet resultSet = null;
        try {
            resultSet = getStatement().executeQuery(sql);
        } catch (SQLException e) {
            Log.log("Query failed,sql:"+sql);
            e.printStackTrace();
        }
        return resultSet;
    }
    public int update(String sql){
        int num = 0;
        try {
            num = getStatement().executeUpdate(sql);
        } catch (SQLException e) {
            Log.log("Update failed,num:"+num+",sql:"+sql);
            e.printStackTrace();
        }
        return num;
    }

    public int insert(String sql){
        int num = 0;
        try {
            num = getStatement().executeUpdate(sql);
        } catch (SQLException throwables) {
            Log.log("Insert failed,num:"+num+",sql:"+sql);
            throwables.printStackTrace();
        }
        return num;
    }

    public Patient getPatient(String barcode,String mid){
        String sql = "select pat_id,pat_bar_code,pat_sid,pat_name,pat_d_name,pat_s_name,pat_sex,pat_performed_status,pat_age,pat_mid,pat_doct,pat_phonenum,pat_identity_card\n" +
            "from patients \n" +
            "where 1=1\n" +
            "and pat_bar_code = '"+barcode+"'\n" +
            "and pat_mid = '"+mid+"'\n" +
            "order by pat_date desc;";

        ResultSet query = query(sql);
        Patient patient = null;
        try {
            query.next();
            patient = new Patient();
            patient.id = query.getString("pat_id");
            patient.sid = query.getInt("pat_sid");
            patient.barcode = query.getString("pat_bar_code");
            patient.name = query.getString("pat_name");
            patient.age = query.getString("pat_age");
            patient.sex = query.getString("pat_sex");
            patient.sName = query.getString("pat_s_name");
            patient.dct = query.getString("pat_doct");
            patient.depart = query.getString("pat_d_name");
            patient.status = query.getInt("pat_performed_status");
            patient.mid = query.getString("pat_mid");
            patient.phone = query.getString("pat_phonenum");
            patient.identityCard = query.getString("pat_identity_card");
            Log.log("Get patient message:"+patient);
        } catch (SQLException e) {
            Log.log("Failed to get patient message,barcode:"+barcode+",mid:"+mid+",error:"+e.getMessage());
            throw new RuntimeException(e);
        }
        return patient;
    }
}
