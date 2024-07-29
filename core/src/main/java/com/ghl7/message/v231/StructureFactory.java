package com.ghl7.message.v231;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.model.v231.segment.*;
import com.ghl7.Log;
import com.ghl7.pojo.Patient;
import com.ghl7.pojo.Result;
import com.ghl7.pojo.Transmit;

import java.util.List;

/**
 * @Auther WenLong
 * @Date 2024/7/29 15:05
 * @Description
 **/
public class StructureFactory {
    public static MSH getMSH(Transmit transmit){
        Message message = transmit.message;
        if (message == null){
            Log.log("Failed to get MSH:message is null;");
            return null;
        }
        MSH msh = null;
        try {
            msh = (MSH)message.get("MSH");

            msh.getFieldSeparator().setValue("|");
            msh.getEncodingCharacters().setValue("^~\\&");

            //厂家名称
            msh.getSendingApplication().getNamespaceID().setValue("");
            msh.getSendingFacility().getNamespaceID().setValue("");

            //lis信息
            msh.getReceivingApplication().getNamespaceID().setValue("LIS");
            msh.getReceivingFacility().getNamespaceID().setValue("LIS");

//            msh.getDateTimeOfMessage().getTimeOfAnEvent().setValue(currentDate);
            msh.getMessageType().getMessageType().setValue(transmit.type);
            msh.getMessageType().getTriggerEvent().setValue(transmit.event);
            msh.getMessageControlID().setValue(transmit.controlId+"");
            msh.getProcessingID().getProcessingID().setValue("P");
            msh.getVersionID().getVersionID().setValue("2.3.1");

            //发送的结果类型
            msh.getAcceptAcknowledgmentType().setValue("");
            msh.getApplicationAcknowledgmentType().setValue("");
        } catch (HL7Exception e) {
            Log.log("Failed to get MSH,Not has MSH in the message!");
            throw new RuntimeException(e);
        }
        return msh;
    }

    public static MSA getMSA(Transmit transmit) {
        Message message = transmit.message;
        if (message == null){
            Log.log("Failed to get MSA:message is null;");
            return null;
        }
        MSA msa = null;
        try {
            msa = (MSA)message.get("MSA");
            msa.getMessageControlID().setValue(transmit.controlId+"");
            msa.getAcknowledgementCode().setValue(transmit.ackValue);
        } catch (HL7Exception e) {
            Log.log("Failed to get MSA,Not has MSA in the message!");
            throw new RuntimeException(e);
        }
        return msa;
    }

    public static PID getPID(Transmit transmit) {
        Message message = transmit.message;
        if (message == null){
            Log.log("Failed to get PID:message is null;");
            return null;
        }

        Patient patient = transmit.patient;
        if (patient == null){
            Log.log("Failed to get PID:patient is null;");
            return null;
        }

        PID pid = null;
        try {
            pid = (PID)message.get("PID");
            pid.getPid1_SetIDPID().setValue("1");
            pid.getPatientIdentifierList()[0].getID().setValue("C1");
            pid.getPatientIdentifierList()[4].getID().setValue("MR");
        } catch (HL7Exception e) {
            Log.log("Failed to get PID:Not has PID in the message;");
            throw new RuntimeException(e);
        }
        return pid;
    }

    public static PV1 getPV1(Transmit transmit) {
        Message message = transmit.message;
        if (message == null){
            Log.log("Failed to get PV1:message is null;");
            return null;
        }

        Patient patient = transmit.patient;
        if (patient == null){
            Log.log("Failed to get PV1:patient is null;");
            return null;
        }

        PV1 pv1 = null;
        try {
            pv1 = (PV1)message.get("PV1");
            pv1.getSetIDPV1().setValue("1");
            pv1.getPatientClass().setValue("Outpatient");
            pv1.getAssignedPatientLocation().getLocationDescription().setValue(patient.depart);
            pv1.getAssignedPatientLocation().getBed().setValue("BN1");
            pv1.getFinancialClass()[0].getFinancialClass().setValue("MedicalInsurance");
        } catch (HL7Exception e) {
            Log.log("Failed to get PV1:Not has PV1 in the message;");
            throw new RuntimeException(e);
        }
        return pv1;
    }

    public static OBR getOBR(Transmit transmit) {
        Message message = transmit.message;
        if (message == null){
            Log.log("Failed to get OBR:message is null;");
            return null;
        }

        Patient patient = transmit.patient;
        if (patient == null){
            Log.log("Failed to get OBR:patient is null;");
            return null;
        }

        OBR obr = null;
        try {
            obr = (OBR)message.get("OBR");
            obr.getSetIDOBR().setValue("1");
            obr.getFillerOrderNumber().getUniversalID().setValue(patient.barcode);
            obr.getUniversalServiceID().getText().setValue("00001");
            obr.getRequestedDateTime().getTimeOfAnEvent().setValue(patient.date);
            obr.getObservationDateTime().getTimeOfAnEvent().setValue(patient.iDate);
            obr.getCollectorIdentifier()[0].getGivenName().setValue("LI");
            obr.getPrincipalResultInterpreter().getOPName().getGivenName().setValue("admin");
        } catch (HL7Exception e) {
            Log.log("Failed to get OBR:Not has OBR in the message;");
            throw new RuntimeException(e);
        }
        return obr;
    }

    public static ORC getORC(Transmit transmit) {
        Message message = transmit.message;
        if (message == null){
            Log.log("Failed to get ORC:message is null;");
            return null;
        }

        Patient patient = transmit.patient;
        if (patient == null){
            Log.log("Failed to get ORC:patient is null;");
            return null;
        }

        ORC orc = null;
        try {
            orc = (ORC)message.get("ORC");
            orc.getOrderControl().setValue("RF");
            orc.getFillerOrderNumber().getUniversalID().setValue(patient.barcode);
            orc.getOrderStatus().setValue("IP");
        } catch (HL7Exception e) {
            Log.log("Failed to get ORC:Not has ORC in the message;");
            throw new RuntimeException(e);
        }
        return orc;
    }

//    public static void getOBX(Transmit transmit) {
//        Message message = transmit.message;
//        if (message == null){
//            Log.log("Failed to get OBX:message is null;");
//            return;
//        }
//
//        Patient patient = transmit.patient;
//        if (patient == null){
//            Log.log("Failed to get OBX:patient is null;");
//            return;
//        }
//        List<Result> results = patient.results;
//        if (results == null){
//            Log.log("Failed to get OBX:results is null;");
//            return;
//        }
//    }
}
