package com.ghl7.message;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.datatype.ST;
import ca.uhn.hl7v2.model.v231.segment.OBX;
import ca.uhn.hl7v2.model.v231.segment.ORC;
import ca.uhn.hl7v2.model.v231.segment.PV1;
import com.ghl7.Channel;
import com.ghl7.Logger;
import com.ghl7.pojo.Item;
import com.ghl7.pojo.Patient;
import com.ghl7.pojo.Transmit;
import com.ghl7.segment.CORR_O02;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StructureMT8000 {
    private final static String TAG = StructureMT8000.class.getSimpleName();

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

        List<Item> items = transmit.items;
        if (items == null){
            Logger.log(TAG,"Items is null,barcode:"+transmit.patient.barcode);
            items = new ArrayList<>();
        }

        Channel channel = transmit.channel;

        CORR_O02 corrO02 = (CORR_O02) message;
        try {
            Logger.log(TAG,"Try to create ORR_O02!");
            OBX obx = corrO02.insertOBX(0);
            obx.getSetIDOBX().setValue("1");
            obx.getValueType().setValue("NM");
            obx.getObservationIdentifier().getCe1_Identifier().setValue("30525-0");
            obx.getObservationIdentifier().getCe2_Text().setValue("Age");
            obx.getObservationIdentifier().getCe3_NameOfCodingSystem().setValue("LN");
            ST st = new ST(corrO02);
            st.setValue("12");
            obx.insertObservationValue(0).setData(st);
            obx.getUnits().getCe1_Identifier().setValue("yr");
            obx.getObservationResultStatus().setValue("F");

            OBX obx2 = corrO02.insertOBX(1);
            obx2.getSetIDOBX().setValue("2");
            obx2.getValueType().setValue("NM");
            obx2.getObservationIdentifier().getCe1_Identifier().setValue("01008");
            obx2.getObservationIdentifier().getCe2_Text().setValue("Patient Area");
            obx2.getObservationIdentifier().getCe3_NameOfCodingSystem().setValue("99MRC");
            ST st2 = new ST(corrO02);
            st2.setValue("v44");
            obx2.insertObservationValue(0).setData(st2);
//            obx2.getAbnormalFlags(0).setValue("W");
//            obx2.getUnits().getCe1_Identifier().setValue("yr");
            obx2.getObservationResultStatus().setValue("F");

            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                OBX obxI= corrO02.insertOBX(2+i);
                obxI.getSetIDOBX().setValue((3+i)+"");
                //结果类型
                String resultType = "ST";
                if ("数值".equals(item.resultType) || "".equals(item.resultType)) {
                    resultType = "NM";
                }
                obxI.getValueType().setValue(resultType);

//                obxI.getObservationIdentifier().getCe1_Identifier().setValue("08003");//
                obxI.getObservationIdentifier().getCe2_Text().setValue(item.itemName);//项目名称
                obxI.getObservationIdentifier().getCe3_NameOfCodingSystem().setValue(item.itemCode);//项目代码
                String channelId = item.itemCode;
                Map<String, String> lisToMid = channel.lisToMid;
                if (lisToMid.containsKey(item.itemCode)) {
                    channelId = lisToMid.get(item.itemCode);
                }
                obxI.getObservationIdentifier().getCe4_AlternateIdentifier().setValue(channelId);//通道
                obxI.getObservationResultStatus().setValue("F");
            }
        } catch (HL7Exception e) {
            Logger.log(TAG,"Failed to create OBX:RuntimeException:\n"+e.getMessage());
            throw new RuntimeException(e);
        }

        return 0;
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
            //病人类型：
            //门诊 = outpatient；
            //住院 = inpatient；
            //体检 = healthCheck；
            //其他 = Other
            pv1.getPatientClass().setValue("outpatient");
            pv1.getAssignedPatientLocation().getBed().setValue("Bn4");
            pv1.getAssignedPatientLocation().getPl1_PointOfCare().setValue("内科");
            pv1.insertFinancialClass(0).getFinancialClass().setValue("gongfei");
        } catch (HL7Exception e) {
            Logger.log(TAG,"Failed to get PV1:Not has PV1 in the message;");
            throw new RuntimeException(e);
        }
        return pv1;
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
}
