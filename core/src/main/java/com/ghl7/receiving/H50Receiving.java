package com.ghl7.receiving;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;

import java.util.Map;

/**
 * @Auther WenLong
 * @Date 2024/7/22 10:49
 * @Description
 **/
public class H50Receiving implements ReceivingApplication {
    private String mid;


    public H50Receiving(String mid) {
        this.mid = mid;
    }

    @Override
    public Message processMessage(Message message, Map map) throws ReceivingApplicationException, HL7Exception {
        return null;
    }

    @Override
    public boolean canProcess(Message message) {
        return true;
    }
}
