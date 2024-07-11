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
 * @Date 2024/7/11 15:34
 * @Description
 **/
public class ReceivingPlace implements ReceivingApplication<QRY_Q02> {

    @Override
    public Message processMessage(QRY_Q02 qryQ02, Map<String, Object> map) throws ReceivingApplicationException, HL7Exception {
        return null;
    }

    @Override
    public boolean canProcess(QRY_Q02 qryQ02) {
        return true;
    }
}
