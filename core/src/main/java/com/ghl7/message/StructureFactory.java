package com.ghl7.message;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.datatype.ST;
import ca.uhn.hl7v2.model.v231.segment.*;
import ca.uhn.hl7v2.model.v231.datatype.ID;
import com.ghl7.Logger;
import com.ghl7.pojo.Patient;
import com.ghl7.pojo.Transmit;
import com.ghl7.segment.CORR_O02;
import com.microsoft.sqlserver.jdbc.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Auther WenLong
 * @Date 2024/7/29 15:05
 * @Description
 **/
public class StructureFactory {
    private final static String TAG = StructureFactory.class.getSimpleName();
    public static MSH getMSH(Transmit transmit){
        Message message = transmit.responseMessage;
        if (message == null){
            Logger.log(TAG,"Failed to get MSH:message is null;");
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
            ID id = msh.insertCharacterSet(0);
            id.setValue("UNICODE");
        } catch (HL7Exception e) {
            Logger.log(TAG,"Failed to get MSH,Not has MSH in the message!");
            throw new RuntimeException(e);
        }
        return msh;
    }

    public static MSA getMSA(Transmit transmit) {
        Message message = transmit.responseMessage;
        if (message == null){
            Logger.log(TAG,"Failed to get MSA:message is null;");
            return null;
        }
        MSA msa = null;
        try {
            msa = (MSA)message.get("MSA");
            msa.getMessageControlID().setValue(transmit.controlId+"");
            msa.getAcknowledgementCode().setValue(transmit.ackValue);
        } catch (HL7Exception e) {
            Logger.log(TAG,"Failed to get MSA,Not has MSA in the message!");
            throw new RuntimeException(e);
        }
        return msa;
    }

    public static PID getPID(Transmit transmit) {
        Message message = transmit.responseMessage;
        if (message == null){
            Logger.log(TAG,"Failed to get PID:message is null;");
            return null;
        }

        Patient patient = transmit.patient;
        if (patient == null){
            Logger.log(TAG,"Failed to get PID:patient is null;");
            return null;
        }

        PID pid = null;
        try {
            pid = (PID)message.get("PID");
            pid.getPid1_SetIDPID().setValue("1");
            pid.insertPatientIdentifierList(0).getIdentifierTypeCode().setValue("MR");
            pid.getPatientIdentifierList(0).getID().setValue(StringUtils.isEmpty(patient.sid)?"CharNo":patient.sid);
            pid.getDateTimeOfBirth().getTimeOfAnEvent().setValue("19810506");
            pid.insertPatientName(0).getXpn2_GivenName().setValue(StringUtils.isEmpty(patient.name)?"FName":patient.name);
            String sex = "Female";
            if (!StringUtils.isEmpty(patient.sex)){
                if("男".equals(patient.sex.trim())){
                    sex = "Male";
                }
            }
            pid.getSex().setValue(sex);
        } catch (HL7Exception e) {
            Logger.log(TAG,"Failed to get PID:Not has PID in the message;");
            throw new RuntimeException(e);
        }
        return pid;
    }

    public static PV1 getPV1(Transmit transmit) {
        Message message = transmit.responseMessage;
        if (message == null){
            Logger.log(TAG,"Failed to get PV1:message is null;");
            return null;
        }

        Patient patient = transmit.patient;
        if (patient == null){
            Logger.log(TAG,"Failed to get PV1:patient is null;");
            return null;
        }

        PV1 pv1 = null;
        try {
            pv1 = (PV1)message.get("PV1");
            pv1.getSetIDPV1().setValue("1");
            pv1.getPatientClass().setValue("E");
            pv1.getAssignedPatientLocation().getBed().setValue("Bn4");
            pv1.getAssignedPatientLocation().getPl1_PointOfCare().setValue("内科");
            pv1.insertFinancialClass(0).getFinancialClass().setValue("NewChange");
        } catch (HL7Exception e) {
            Logger.log(TAG,"Failed to get PV1:Not has PV1 in the message;");
            throw new RuntimeException(e);
        }
        return pv1;
    }

    public static OBR getOBR(Transmit transmit) {
        Message message = transmit.responseMessage;
        if (message == null){
            Logger.log(TAG,"Failed to get OBR:message is null;");
            return null;
        }

        Patient patient = transmit.patient;
        if (patient == null){
            Logger.log(TAG,"Failed to get OBR:patient is null;");
            return null;
        }

        OBR obr = null;
        try {
            LocalDateTime date = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
            String formattedDate = date.format(formatter);

            obr = (OBR)message.get("OBR");
            obr.getSetIDOBR().setValue("1");
            obr.getPlacerOrderNumber().getEi1_EntityIdentifier().setValue(patient.barcode);
//            obr.getFillerOrderNumber().getUniversalID().setValue(patient.barcode);
            obr.getUniversalServiceID().getCe1_Identifier().setValue("00001");
            obr.getUniversalServiceID().getCe2_Text().setValue("Automated Count");
            obr.getUniversalServiceID().getCe3_NameOfCodingSystem().setValue("99MRC");
            obr.getRequestedDateTime().getTimeOfAnEvent().setValue(formattedDate);
//            obr.getObservationDateTime().getTimeOfAnEvent().setValue(formattedDate);
            obr.insertCollectorIdentifier(0).getXcn1_IDNumber().setValue("".equals(patient.name)?"无名":patient.name);
//            obr.getPrincipalResultInterpreter().getOPName().getGivenName().setValue("admin");
            obr.getSpecimenReceivedDateTime().getTimeOfAnEvent().setValue(formattedDate);
            obr.getDiagnosticServSectID().setValue("HM");
            obr.getResultCopiesTo(0).getXcn1_IDNumber().setValue("");
            obr.getPrincipalResultInterpreter().getNdl1_OPName().getCn1_IDNumber().setValue("CheckerHerry");
        } catch (HL7Exception e) {
            Logger.log(TAG,"Failed to get OBR:Not has OBR in the message;");
            throw new RuntimeException(e);
        }
        return obr;
    }

    public static ORC getORC(Transmit transmit) {
        Message message = transmit.responseMessage;
        if (message == null){
            Logger.log(TAG,"Failed to get ORC:message is null;");
            return null;
        }

        Patient patient = transmit.patient;
        if (patient == null){
            Logger.log(TAG,"Failed to get ORC:patient is null;");
            return null;
        }

        ORC orc = null;
        try {
            orc = (ORC)message.get("ORC");
            orc.getOrderControl().setValue("AF");
            orc.getPlacerOrderNumber().getEi1_EntityIdentifier().setValue(patient.barcode);
//            orc.getFillerOrderNumber().getUniversalID().setValue(patient.barcode);
            orc.getOrderStatus().setValue("IP");
        } catch (HL7Exception e) {
            Logger.log(TAG,"Failed to get ORC:Not has ORC in the message;");
            throw new RuntimeException(e);
        }
        return orc;
    }

    public static int createOBX(Transmit transmit) {
        Message message = transmit.responseMessage;
        if (message == null){
            Logger.log(TAG,"Failed to get OBX:message is null;");
            return 0;
        }

        Patient patient = transmit.patient;
        if (patient == null){
            Logger.log(TAG,"Failed to get OBX:patient is null;");
            return 0;
        }

        CORR_O02 corrO02 = (CORR_O02) message;
        try {
            Logger.log(TAG,"Try to create ORR_O02!");
            OBX obx = corrO02.insertOBX(0);
            obx.getSetIDOBX().setValue("1");
            obx.getValueType().setValue("IS");
            obx.getObservationIdentifier().getCe1_Identifier().setValue("08001");
            obx.getObservationIdentifier().getCe2_Text().setValue("Take Mode");
            obx.getObservationIdentifier().getCe3_NameOfCodingSystem().setValue("99MRC");
            ST st = new ST(corrO02);
            st.setValue("A");
            obx.insertObservationValue(0).setData(st);
//            obx.getAbnormalFlags(0).setValue("A");
            obx.getObservationResultStatus().setValue("F");

            OBX obx2 = corrO02.insertOBX(1);
            obx2.getSetIDOBX().setValue("2");
            obx2.getValueType().setValue("IS");
            obx2.getObservationIdentifier().getCe1_Identifier().setValue("08002");
            obx2.getObservationIdentifier().getCe2_Text().setValue("Blood Mode");
            obx2.getObservationIdentifier().getCe3_NameOfCodingSystem().setValue("99MRC");
            ST st2 = new ST(corrO02);
            st2.setValue("W");
            obx2.insertObservationValue(0).setData(st2);
//            obx2.getAbnormalFlags(0).setValue("W");
            obx2.getObservationResultStatus().setValue("F");

            OBX obx3 = corrO02.insertOBX(2);
            obx3.getSetIDOBX().setValue("3");
            obx3.getValueType().setValue("IS");
            obx3.getObservationIdentifier().getCe1_Identifier().setValue("08003");
            obx3.getObservationIdentifier().getCe2_Text().setValue("Test Mode");
            obx3.getObservationIdentifier().getCe3_NameOfCodingSystem().setValue("99MRC");
            ST st3 = new ST(corrO02);
            st3.setValue("STANDARD");
            obx3.insertObservationValue(0).setData(st3);
//            obx3.getAbnormalFlags(0).setValue("STANDARD");
            obx3.getObservationResultStatus().setValue("F");

            OBX obx4 = corrO02.insertOBX(3);
            obx4.getSetIDOBX().setValue("4");
            obx4.getValueType().setValue("IS");
            obx4.getObservationIdentifier().getCe1_Identifier().setValue("01002");
            obx4.getObservationIdentifier().getCe2_Text().setValue("Ref Group");
            obx4.getObservationIdentifier().getCe3_NameOfCodingSystem().setValue("99MRC");
            ST st4 = new ST(corrO02);
            st4.setValue("XXXX");
            obx4.insertObservationValue(0).setData(st4);
//            obx4.getAbnormalFlags(0).setValue("XXXX");
            obx4.getObservationResultStatus().setValue("F");

            OBX obx5 = corrO02.insertOBX(4);
            obx5.getSetIDOBX().setValue("5");
            obx5.getValueType().setValue("NM");
            obx5.getObservationIdentifier().getCe1_Identifier().setValue("30525-0");
            obx5.getObservationIdentifier().getCe2_Text().setValue("Age");
            obx5.getObservationIdentifier().getCe3_NameOfCodingSystem().setValue("LN");
            ST st5 = new ST(corrO02);
            st5.setValue("22");
            obx5.insertObservationValue(0).setData(st5);
            obx5.getUnits().getCe1_Identifier().setValue("yr");
            obx5.getObservationResultStatus().setValue("F");

            OBX obx6 = corrO02.insertOBX(5);
            obx6.getSetIDOBX().setValue("6");
            obx6.getValueType().setValue("ST");
            obx6.getObservationIdentifier().getCe1_Identifier().setValue("01001");
            obx6.getObservationIdentifier().getCe2_Text().setValue("Remark");
            obx6.getObservationIdentifier().getCe3_NameOfCodingSystem().setValue("99MRC");
//            obx6.getAbnormalFlags(0).setValue("22");
            obx6.getObservationResultStatus().setValue("F");

        } catch (HL7Exception e) {
            throw new RuntimeException(e);
        }

        return 0;
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
