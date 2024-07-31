package com.ghl7.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther WenLong
 * @Date 2024/7/12 9:34
 * @Description 用户
 **/

public class Patient {
    public String id = "";
    public String sid = "";
    public String barcode = "";

    public String status = "";
    public String mid = "";

    public String name = "";
    public String sex = "";
    public String age = "";
    public String phone = "";

    public String dct = "";
    public String depart = "Medicine";
    public String sName = "";
    public String iName = "";
    public String identityCard = "";

    public List<Result> results = new ArrayList<>();
    public String date = "20240101";
    public String iDate = "20240101";

    @Override
    public String toString() {
        return "Patient{" +
            "id='" + id + '\'' +
            ", sid=" + sid +
            ", barcode='" + barcode + '\'' +
            ", status='" + status + '\'' +
            ", mid='" + mid + '\'' +
            ", name='" + name + '\'' +
            ", sex='" + sex + '\'' +
            ", age='" + age + '\'' +
            ", phone='" + phone + '\'' +
            ", dct='" + dct + '\'' +
            ", depart='" + depart + '\'' +
            ", sName='" + sName + '\'' +
            ", iName='" + iName + '\'' +
            ", identityCard='" + identityCard + '\'' +
            ", date=" + date +
            ", iDate=" + iDate +
            '}';
    }
}
