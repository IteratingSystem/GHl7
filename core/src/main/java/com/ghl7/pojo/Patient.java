package com.ghl7.pojo;

/**
 * @Auther WenLong
 * @Date 2024/7/12 9:34
 * @Description 用户
 **/

public class Patient {
    public String id;
    public int sid;
    public String barcode;

    public int status;
    public String mid;

    public String name;
    public String sex;
    public String age;
    public String phone;

    public String dct;
    public String depart;
    public String sName;
    public String identityCard;

    @Override
    public String toString() {
        return "Patient{" +
            "id='" + id + '\'' +
            ", sid=" + sid +
            ", barcode='" + barcode + '\'' +
            ", status=" + status +
            ", mid='" + mid + '\'' +
            ", name='" + name + '\'' +
            ", sex='" + sex + '\'' +
            ", age='" + age + '\'' +
            ", phone='" + phone + '\'' +
            ", dct='" + dct + '\'' +
            ", depart='" + depart + '\'' +
            ", sName='" + sName + '\'' +
            ", identityCard='" + identityCard + '\'' +
            '}';
    }
}
