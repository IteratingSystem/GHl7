package com.ghl7.message;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.message.ACK;
import ca.uhn.hl7v2.model.v231.segment.MSA;
import ca.uhn.hl7v2.model.v231.segment.MSH;

/**
 * @Author: WenLong
 * @Date: 2024-07-22-21:48
 * @Description:
 */
public class MessageFactory {
    public static Message getMessage(String sampleName){
        if (ACK.class.getSimpleName().equals(sampleName)) {
            return generateACK();
        }
        return null;
    }

    private static ACK generateACK(){
        ACK ack = new ACK();

        MSH msh = ack.getMSH();
        MSA msa = ack.getMSA();
        try {
            msh.getFieldSeparator().setValue("|");
            msh.getEncodingCharacters().setValue("^~\\&");

            //厂家名称
            msh.getSendingApplication().getNamespaceID().setValue("");
            msh.getSendingFacility().getNamespaceID().setValue("");

            //lis信息
            msh.getReceivingApplication().getNamespaceID().setValue("LIS");
            msh.getReceivingFacility().getNamespaceID().setValue("LIS");

//            msh.getDateTimeOfMessage().getTimeOfAnEvent().setValue(currentDate);
            msh.getMessageType().getMessageType().setValue("DSR");
            msh.getMessageType().getTriggerEvent().setValue("Q03");
            msh.getMessageControlID().setValue("1");
            msh.getProcessingID().getProcessingID().setValue("P");
            msh.getVersionID().getVersionID().setValue("2.3.1");

            //发送的结果类型
            msh.getAcceptAcknowledgmentType().setValue("");
            msh.getApplicationAcknowledgmentType().setValue("0");


            msa.getAcknowledgementCode().setValue("AA");
            msa.getMessageControlID().setValue("1");
            msa.getTextMessage().setValue("Message accepted");
            msa.getErrorCondition().getCe1_Identifier().setValue("0");
        } catch (DataTypeException e) {
            System.out.println("Failed to generate ACK message!"+e.getMessage());
            throw new RuntimeException(e);
        }
        return ack;
    }
}
