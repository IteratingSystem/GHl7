package com.ghl7.message;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.segment.ORC;
import ca.uhn.hl7v2.model.v231.segment.PV1;
import com.ghl7.Logger;
import com.ghl7.pojo.Patient;
import com.ghl7.pojo.Transmit;

public class StructureMT8000 {
    private final static String TAG = StructureMT8000.class.getSimpleName();

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
