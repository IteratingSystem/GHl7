package com.ghl7.instrument.c3000;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.datatype.XCN;
import ca.uhn.hl7v2.model.v231.message.ORU_R01;
import ca.uhn.hl7v2.model.v231.message.QRY_Q02;
import ca.uhn.hl7v2.model.v231.segment.QRD;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import com.ghl7.Log;
import com.microsoft.sqlserver.jdbc.StringUtils;
import com.sun.org.apache.xml.internal.security.algorithms.MessageDigestAlgorithm;

import java.util.Map;

/**
 * @Auther WenLong
 * @Date 2024/7/11 17:42
 * @Description
 **/
public class RegisterApplication implements ReceivingApplication {
    @Override
    public Message processMessage(Message message, Map map) throws ReceivingApplicationException, HL7Exception {
        if (message instanceof QRY_Q02){
            Log.log("Is place request!");
            place((QRY_Q02)message);
        }else if (message instanceof ORU_R01){
            Log.log("Is save result request!");
            saveResult((ORU_R01)message);
        }
        return null;
    }

    @Override
    public boolean canProcess(Message message) {
        if (message instanceof QRY_Q02 || message instanceof ORU_R01){
            Log.log("Received message:");
            Log.logHL7Message(message);
            return true;
        }
        Log.log("Message types outside of documents!");
        return false;
    }

    private Message place(QRY_Q02 qryQ02){
        //获取条码
        QRD qrd = qryQ02.getQRD();
        String barcode = qrd.getQrd8_WhoSubjectFilter()[0].getXcn1_IDNumber().toString();
        if (StringUtils.isEmpty(barcode)){
            Log.log("Barcode is empty!");
            return null;
        }
        Log.log("Barcode is "+barcode);


        return null;
    }
    private Message saveResult(ORU_R01 oruR01){
        return null;
    }
}
