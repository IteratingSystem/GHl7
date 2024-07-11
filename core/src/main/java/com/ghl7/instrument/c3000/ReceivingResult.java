package com.ghl7.instrument.c3000;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.message.ORU_R01;
import ca.uhn.hl7v2.model.v231.message.QRY_Q02;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;

import java.util.Map;

/**
 * @Auther WenLong
 * @Date 2024/7/11 13:00
 * @Description
 **/
public class ReceivingResult implements ReceivingApplication<ORU_R01> {


    @Override
    public Message processMessage(ORU_R01 oruR01, Map<String, Object> map) throws ReceivingApplicationException, HL7Exception {
        return null;
    }

    @Override
    public boolean canProcess(ORU_R01 oruR01) {
        return true;
    }
}
