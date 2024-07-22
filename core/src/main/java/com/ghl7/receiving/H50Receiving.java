package com.ghl7.receiving;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.model.v231.message.ACK;
import ca.uhn.hl7v2.model.v231.message.ORM_O01;
import ca.uhn.hl7v2.model.v231.message.ORU_R01;
import ca.uhn.hl7v2.model.v231.message.QRY_Q02;
import ca.uhn.hl7v2.model.v231.segment.OBR;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import com.ghl7.Log;
import com.ghl7.dao.SQLMapper;
import com.ghl7.message.MsgHelper;
import com.ghl7.pojo.Patient;
import com.microsoft.sqlserver.jdbc.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @Auther WenLong
 * @Date 2024/7/22 10:49
 * @Description
 **/
public class H50Receiving implements ReceivingApplication {
    private String mid;
    private SQLMapper sqlMapper;


    public H50Receiving(String mid) {
        this.mid = mid;
    }

    @Override
    public Message processMessage(Message message, Map map) throws ReceivingApplicationException, HL7Exception {
        //结果接收
        if (message instanceof ORU_R01){
            Log.log("Is result message!");
            return saveResult((ORU_R01) message);
        }
        //双向查询
        if (message instanceof ORM_O01){
            Log.log("Is query message!");
        }

        return null;
    }

    @Override
    public boolean canProcess(Message message) {
        Log.log("Received message:");
        MsgHelper msgHelper = new MsgHelper(message);
        msgHelper.logMessage();
        return true;
    }
    private ACK saveResult(ORU_R01 oruR01) {

        try {
            ACK ack = (ACK)oruR01.generateACK();

            //读取id
            OBR obr = (OBR)oruR01.get("OBR");
            String fillerOrderNumber = obr.getFillerOrderNumber().getName();
            Log.log("Get a fillerOrderNumber:"+fillerOrderNumber);

            //获取为条码或样本号
            String sid = "";
            String barcode = "";
            if (StringUtils.isEmpty(fillerOrderNumber)){
                return ack;
            }else if (fillerOrderNumber.length() <= 6) {
                Log.log("Length <= 6,Is sid.");
                sid = fillerOrderNumber;
            }else {
                Log.log("Length > 6,Is barcode.");
                barcode = fillerOrderNumber;
            }



            //以样本号接收结果

            //以条码号接收结果
            Patient patient = sqlMapper.getPatient(barcode, mid);
            if (patient == null || StringUtils.isEmpty(patient.id)) {
                Log.log("This patient not in lis system;");
                return ack;
            }
            if (patient.status != 6){
                Log.log("This barcode is not layout,barcode:"+barcode+",status:"+patient.status);
                return ack;
            }


        } catch (HL7Exception e) {
            Log.log("Failed to parse OBR information, Unable to obtain barcode!");
            throw new RuntimeException(e);
        } catch (IOException e) {
            Log.log("Failed to generate ACK!");
            throw new RuntimeException(e);
        }
        return null;
    }
}
