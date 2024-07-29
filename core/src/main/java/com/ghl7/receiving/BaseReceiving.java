package com.ghl7.receiving;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import com.ghl7.Log;
import com.ghl7.message.v231.MessageHelper;

import java.util.Map;

/**
 * @Auther WenLong
 * @Date 2024/7/29 14:37
 * @Description
 **/
public class BaseReceiving<T extends Message> implements ReceivingApplication<T> {
    public String mid;
    public String type;
    public String event;

    public BaseReceiving(String mid,String type,String event){
        this.mid = mid;
        this.type = type;
        this.event = event;
    }

    @Override
    public Message processMessage(T message, Map<String, Object> theMetadata) throws ReceivingApplicationException, HL7Exception {
        return null;
    }

    @Override
    public boolean canProcess(T message) {
        Log.log("Received message:");
        MessageHelper.logMessage(message);
        return true;
    }
}
