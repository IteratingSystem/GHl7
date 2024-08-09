package com.ghl7.dao;

import com.ghl7.Log;
import com.ghl7.pojo.Patient;
import com.ghl7.pojo.Result;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Auther WenLong
 * @Date 2024/7/12 10:18
 * @Description
 **/
public class SQLMapper {
    private static final String URL = "jdbc:sqlserver://192.168.0.6:1433;databaseName=clabsdbc";
    private static final String USER_NAME = "lis";
    private static final String PASS_WORLD = "slis";
    private static Connection connection;


    private static Statement getStatement() {
        if (connection == null){
            try {
                Log.log("Connecting sql server...");
                connection = DriverManager.getConnection(URL, USER_NAME, PASS_WORLD);
            } catch (SQLException e) {
                Log.log("Failed to init connection!url:"+URL+",userName:"+USER_NAME);
                throw new RuntimeException(e);
            }
        }
        Statement statement = null;
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException e) {
            Log.log("Create statement failed:"+e.getMessage());
            e.printStackTrace();
        }
        return statement;
    }

    public static ResultSet query(String sql) {
        ResultSet resultSet = null;
        try {
            resultSet = getStatement().executeQuery(sql);
        } catch (SQLException e) {
            Log.log("Query failed,sql:"+sql);
            e.printStackTrace();
        }
        return resultSet;
    }
    public static int update(String sql){
        int num = 0;
        try {
            num = getStatement().executeUpdate(sql);
        } catch (SQLException e) {
            Log.log("Update failed,num:"+num+",sql:"+sql);
            e.printStackTrace();
        }
        return num;
    }

    public static int insert(String sql){
        int num = 0;
        try {
            num = getStatement().executeUpdate(sql);
        } catch (SQLException throwables) {
            Log.log("Insert failed,num:"+num+",sql:"+sql);
            throwables.printStackTrace();
        }
        return num;
    }
    public static void saveResult(Patient patient){
        Log.log("Save patient begin:"+patient);
        String results = "";
        for (Result result : patient.results) {
            String itemName = result.itemName;
            results += itemName+"','";
        }
        results = "('"+results+"')";


        String sql = "delete resulto \n" +
            "where 1=1\n" +
            "and res_id = '"+patient.id+"'\n" +
            "and res_sid = "+patient.sid+"\n" +
            "and res_it_ecd in "+results+"\n" +
            "and res_mid = '"+patient.mid+"';";

        update(sql);

        for (Result result : patient.results) {
            sql = "INSERT INTO " +
                "[resulto] ([res_mid], [res_sid], [res_it_ecd], [res_chr], [res_date], [res_id], [res_sor_flag], [res_user],[res_bar_code]) " +
                "VALUES " +
                "('" + patient.mid + "'," + patient.sid + ",'" + result.itemName + "','" + result.result + "','" + result.resDate + "','" + patient.id + "',1,'" + patient.iName + "','"+patient.barcode+"');";
            insert(sql);
        }
        Log.log("Finished to save patient!");
    }
    public static Patient getPatient(String sid,String mid,String date){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        Log.log("Data:"+date);
        LocalDateTime dateTime = LocalDateTime.parse(date,dateTimeFormatter);
        LocalDate localDate = dateTime.toLocalDate();
        String sDate = localDate.format(dateTimeFormatter);
        LocalDate eLocalDate = localDate.plusDays(1);
        String eDate = eLocalDate.format(dateTimeFormatter);

        String sql = "select pat_i_name,pat_id,pat_bar_code,pat_sid,pat_name,pat_d_name,pat_s_name,pat_sex,pat_performed_status,pat_age,pat_mid,pat_doct,pat_phonenum,pat_identity_card " +
            "from patients " +
            "where 1=1 " +
//            "and pat_mid = '"+mid+"'" +
            "and pat_date < '"+eDate+"'" +
            "and pat_date >= '"+sDate+"'" +
            "and pat_sid = "+sid+"; ";
        ResultSet query = query(sql);
        Patient patient = new Patient();
        try {
            if (!query.next()){
                Log.log("Failed to query patient message!Haven't line");
                return patient;
            }

            patient.id = query.getString("pat_id");
            patient.sid = query.getString("pat_sid");
            patient.barcode = query.getString("pat_bar_code");
            patient.name = query.getString("pat_name");
            patient.age = query.getString("pat_age");
            patient.sex = query.getString("pat_sex");
            patient.sName = query.getString("pat_s_name");
            patient.dct = query.getString("pat_doct");
            patient.depart = query.getString("pat_d_name");
            patient.status = query.getString("pat_performed_status");
            patient.mid = query.getString("pat_mid");
            patient.iName = query.getString("pat_i_name");
            patient.phone = query.getString("pat_phonenum");
            patient.identityCard = query.getString("pat_identity_card");
            Log.log("Get patient message:"+patient);
        } catch (SQLException e) {
            Log.log("Failed to get patient message by sid,sid:"+sid+",mid:"+mid+",error:"+e.getMessage());
            throw new RuntimeException(e);
        }
        return patient;
    }
    public static Patient getPatient(String barcode,String mid){
        String sql = "select pat_i_name,pat_id,pat_bar_code,pat_sid,pat_name,pat_d_name,pat_s_name,pat_sex,pat_performed_status,pat_age,pat_mid,pat_doct,pat_phonenum,pat_identity_card \n" +
            "from patients \n" +
            "where 1=1 \n" +
            "and pat_bar_code = '"+barcode+"' \n" +
//            "and pat_mid = '"+mid+"' \n" +
            "order by pat_date desc;";

        ResultSet query = query(sql);

        Patient patient = new Patient();
        try {
            if (!query.next()){
                Log.log("Failed to query patient message!Haven't line");
                return patient;
            }

            patient.id = query.getString("pat_id");
            patient.sid = query.getString("pat_sid");
            patient.barcode = query.getString("pat_bar_code");
            patient.name = query.getString("pat_name");
            patient.age = query.getString("pat_age");
            patient.sex = query.getString("pat_sex");
            patient.sName = query.getString("pat_s_name");
            patient.dct = query.getString("pat_doct");
            patient.depart = query.getString("pat_d_name");
            patient.status = query.getString("pat_performed_status");
            patient.mid = query.getString("pat_mid");
            patient.iName = query.getString("pat_i_name");
            patient.phone = query.getString("pat_phonenum");
            patient.identityCard = query.getString("pat_identity_card");
            Log.log("Get patient message:"+patient);
        } catch (SQLException e) {
            Log.log("Failed to get patient message by barcode,barcode:"+barcode+",mid:"+mid+",error:"+e.getMessage());
            throw new RuntimeException(e);
        }
        return patient;
    }
}
