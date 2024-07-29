package com.ghl7.pojo;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.ModelClassFactory;

/**
 * @Auther WenLong
 * @Date 2024/7/29 15:10
 * @Description
 **/
public class Transmit {
    public Message message;
    public Patient patient;
    public String type = "";
    public String event = "";
    public String controlId = "1";
    public String ackValue = "AA";
    public ModelClassFactory modelClassFactory;
}
