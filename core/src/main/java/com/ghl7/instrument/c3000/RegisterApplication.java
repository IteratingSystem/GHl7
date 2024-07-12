package com.ghl7.instrument.c3000;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.datatype.XCN;
import ca.uhn.hl7v2.model.v231.message.ORU_R01;
import ca.uhn.hl7v2.model.v231.message.QCK_Q02;
import ca.uhn.hl7v2.model.v231.message.QRY_Q02;
import ca.uhn.hl7v2.model.v231.segment.MSA;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import ca.uhn.hl7v2.model.v231.segment.QRD;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import com.ghl7.Log;
import com.microsoft.sqlserver.jdbc.StringUtils;
import com.sun.org.apache.xml.internal.security.algorithms.MessageDigestAlgorithm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        //获取controlId
        String controlId = qryQ02.getMSH().getMessageControlID().getValue();
        Log.log("Get controlId:"+controlId);
        if (StringUtils.isEmpty(controlId)){
            Log.log("ControlId is empty!");
            return null;
        }
        //创建QCK_Q02
        String now = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        QCK_Q02 qckQ02 = new QCK_Q02();
        MSH msh = qckQ02.getMSH();
        try {
            msh.getFieldSeparator().setValue("|");
            msh.getEncodingCharacters().setValue("^~\\&");
            //厂家名称
            msh.getSendingApplication().getNamespaceID().setValue("热景");
            msh.getSendingFacility().getNamespaceID().setValue(Instrument.MID);
            //lis信息
            msh.getReceivingApplication().getNamespaceID().setValue("LIS");
            msh.getReceivingFacility().getNamespaceID().setValue("LIS");
            msh.getDateTimeOfMessage().getTimeOfAnEvent().setValue(now);
            msh.getMessageType().getMessageType().setValue("QCK");
            msh.getMessageType().getTriggerEvent().setValue("Q02");
            msh.getMessageControlID().setValue(controlId);
            msh.getProcessingID().getProcessingID().setValue("1");
            msh.getVersionID().getVersionID().setValue("2.3.1");
            //发送的结果类型
            msh.getApplicationAcknowledgmentType().setValue("String");
        } catch (DataTypeException e) {
            Log.log("Create QCK_Q02 MSH failed:"+e.getMessage());
            throw new RuntimeException(e);
        }

        MSA msa = qckQ02.getMSA();
        ResultSet query = mapper.query(sql);

        try {
            query.last();
            int length = query.getRow();
            query.close();
            msa.getMessageControlID().setValue(controlId+"");
            if (length > 0){
                msa.getAcknowledgementCode().setValue("AA");
                msa.getTextMessage().setValue("Message accepted");
                msa.getErrorCondition().getText().setValue("0");
            }else {
                msa.getAcknowledgementCode().setValue("AE");
                msa.getTextMessage().setValue("Table value not found");
                msa.getErrorCondition().getText().setValue("103");
            }

        } catch (DataTypeException e) {
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }





        return null;
    }
    private Message saveResult(ORU_R01 oruR01){
        return null;
    }
}
