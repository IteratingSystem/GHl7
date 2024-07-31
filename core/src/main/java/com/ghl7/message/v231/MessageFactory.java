package com.ghl7.message.v231;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.group.ORR_O02_PIDNTEORCOBRRQDRQ1RXOODSODTNTECTI;
import ca.uhn.hl7v2.model.v231.message.ACK;
import ca.uhn.hl7v2.model.v231.message.ORR_O02;
import ca.uhn.hl7v2.model.v231.segment.*;
import ca.uhn.hl7v2.parser.PipeParser;
import com.ghl7.Log;
import com.ghl7.pojo.Transmit;
import com.ghl7.segment.CORR_O02;

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

    public static CORR_O02 generateORR_O02(Transmit transmit){
        CORR_O02 orrO02 = new CORR_O02();
        transmit.responseMessage = orrO02;
        transmit.type = "ORR";
        transmit.event = "O02";
        transmit.modelClassFactory = orrO02.getModelClassFactory();
        MSH msh = StructureFactory.getMSH(transmit);
        MSA msa = StructureFactory.getMSA(transmit);
        PID pid = StructureFactory.getPID(transmit);
        PV1 pv1 = StructureFactory.getPV1(transmit);
        ORC orc = StructureFactory.getORC(transmit);
        OBR obr = StructureFactory.getOBR(transmit);
        return orrO02;
    }

}
