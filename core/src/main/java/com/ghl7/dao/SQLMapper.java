package com.ghl7.dao;

import com.ghl7.Logger;
import com.ghl7.pojo.Item;
import com.ghl7.pojo.Patient;
import com.ghl7.pojo.Result;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther WenLong
 * @Date 2024/7/12 10:18
 * @Description
 **/
public class SQLMapper {
    private final static String TAG = SQLMapper.class.getSimpleName();
    private static String URL = "jdbc:sqlserver://192.168.0.6:1433;databaseName=clabsdbc";
    private static String USER_NAME = "lis";
    private static String PASS_WORLD = "slis";
    private static Connection connection;

    public static void setData(String sqlUrl,String userName,String passwd){
        URL = "jdbc:sqlserver://"+sqlUrl+":1433;databaseName=clabsdbc";
        USER_NAME = userName;
        PASS_WORLD = passwd;
    }

    private static Statement getStatement() {
        if (connection == null){
            try {
                Logger.log(TAG,"Connecting sql server...");
                connection = DriverManager.getConnection(URL, USER_NAME, PASS_WORLD);
            } catch (SQLException e) {
                Logger.log(TAG,"Failed to init connection!url:"+URL+",userName:"+USER_NAME);
                throw new RuntimeException(e);
            }
        }
        Statement statement = null;
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException e) {
            Logger.log(TAG,"Create statement failed:"+e.getMessage());
            e.printStackTrace();
        }
        return statement;
    }

    public static ResultSet query(String sql) {
        ResultSet resultSet = null;
        try {
            resultSet = getStatement().executeQuery(sql);
        } catch (SQLException e) {
            Logger.log(TAG,"Query failed,sql:"+sql);
            e.printStackTrace();
        }
        return resultSet;
    }
    public static int update(String sql){
        int num = 0;
        try {
            num = getStatement().executeUpdate(sql);
        } catch (SQLException e) {
            Logger.log(TAG,"Update failed,num:"+num+",sql:"+sql);
            e.printStackTrace();
        }
        return num;
    }

    public static int insert(String sql){
        int num = 0;
        try {
            num = getStatement().executeUpdate(sql);
        } catch (SQLException throwables) {
            Logger.log(TAG,"Insert failed,num:"+num+",sql:"+sql);
            throwables.printStackTrace();
        }
        return num;
    }
    public static void saveResult(Patient patient){
        Logger.log(TAG,"Save patient begin:"+patient);
        String itemNames = "";
        for (Result result : patient.results) {
            String itemName = result.itemName;
            itemNames += itemName+"','";
        }
        itemNames = "('"+itemNames+"')";


        String sql = "delete resulto \n" +
            "where 1=1\n" +
            "and res_id = '"+patient.id+"'\n" +
            "and res_sid = "+patient.sid+"\n" +
            "and res_it_ecd in "+itemNames+"\n" +
            "and res_mid = '"+patient.mid+"';";

        update(sql);

        for (Result result : patient.results) {
            sql = "INSERT INTO " +
                "[resulto] ([res_mid], [res_sid], [res_it_ecd], [res_chr], [res_date], [res_id], [res_sor_flag], [res_user],[res_bar_code]) " +
                "VALUES " +
                "('" + patient.mid + "'," + patient.sid + ",'" + result.itemName + "','" + result.result + "','" + result.resDate + "','" + patient.id + "',1,'" + patient.iName + "','"+patient.barcode+"');";
            insert(sql);
        }
        Logger.log(TAG,"Finished to save patient!");
    }
    public static Patient getPatient(String sid,String mid,String date){
        Logger.log(TAG,"Get patients by sid,date:"+date+",sid:"+sid);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime dateTime = LocalDateTime.parse(date,dateTimeFormatter);
        LocalDate localDate = dateTime.toLocalDate();
        DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("yyyyMMdd");
        String sDate = localDate.format(dateTimeFormatter2);
        LocalDate eLocalDate = localDate.plusDays(1);
        String eDate = eLocalDate.format(dateTimeFormatter2);
        Logger.log(TAG,"Success to get date,sDate:"+sDate+",eDate:"+eDate);

        String sql = "select pat_i_name,pat_id,pat_bar_code,pat_sid,pat_name,pat_d_name,pat_s_name,pat_sex,pat_performed_status,pat_age,pat_mid,pat_doct,pat_phonenum,pat_identity_card " +
            "from patients " +
            "where 1=1 " +
            "and pat_mid = '"+mid+"'" +
            "and pat_date < '"+eDate+"'" +
            "and pat_date >= '"+sDate+"'" +
            "and pat_sid = "+sid+"; ";
        ResultSet query = query(sql);
        Patient patient = new Patient();
        try {
            if (!query.next()){
                Logger.log(TAG,"Failed to query patient message!Haven't line");
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
            Logger.log(TAG,"Get patient message:"+patient);
        } catch (SQLException e) {
            Logger.log(TAG,"Failed to get patient message by sid,sid:"+sid+",mid:"+mid+",error:"+e.getMessage());
            throw new RuntimeException(e);
        }
        return patient;
    }
    public static List<Item> getItems(String barcode){
        List<Item> items = new ArrayList<>();

        String sql = "select itm_dtype,itm_ecd,itm_name \n" +
            "from patients\n" +
            "inner join lis_test_items on test_id = pat_id\n" +
            "inner join lis_order_item_vs_item  on lis_order_item_vs_item.order_item_code = lis_test_items.order_item_code\n" +
            "inner join item on itm_ecd = report_item_code \n" +
            "where 1=1\n" +
            "and pat_bar_code = '"+barcode+"'\n" +
            "group by itm_dtype,itm_ecd,itm_name";

        ResultSet query = query(sql);
        Logger.log(TAG,"Get items in lis:");
        try {
            while (query.next()){
                Item item = new Item();
                item.resultType = query.getString("itm_dtype");
                item.itemName = query.getString("itm_name");
                item.itemCode = query.getString("itm_ecd");
                items.add(item);
            }
        } catch (SQLException e) {
            Logger.log(TAG,"Failed to get items,barcode:"+barcode);
            throw new RuntimeException(e);
        }

        return items;
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
                Logger.log(TAG,"Failed to query patient message!Haven't line");
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
            Logger.log(TAG,"Get patient message:"+patient);
        } catch (SQLException e) {
            Logger.log(TAG,"Failed to get patient message by barcode,barcode:"+barcode+",mid:"+mid+",error:"+e.getMessage());
            throw new RuntimeException(e);
        }
        return patient;
    }
    public static Patient getPatient(String barcode){
        String sql = "select pat_i_name,pat_id,pat_bar_code,pat_sid,pat_name,pat_d_name,pat_s_name,pat_sex,pat_performed_status,pat_age,pat_mid,pat_doct,pat_phonenum,pat_identity_card \n" +
            "from patients \n" +
            "where 1=1 \n" +
            "and pat_bar_code = '"+barcode+"' \n" +
            "order by pat_date desc;";

        ResultSet query = query(sql);

        Patient patient = new Patient();
        try {
            if (!query.next()){
                Logger.log(TAG,"Failed to query patient message!Haven't line");
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
            Logger.log(TAG,"Get patient message:\n"+patient);
        } catch (SQLException e) {
            Logger.log(TAG,"Failed to get patient message by barcode,barcode:"+barcode+",error:"+e.getMessage());
            throw new RuntimeException(e);
        }
        return patient;
    }
}
