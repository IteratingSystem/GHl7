package com.ghl7.receiving;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v21.datatype.TM;
import ca.uhn.hl7v2.model.v231.datatype.TX;
import ca.uhn.hl7v2.model.v231.message.ORM_O01;
import ca.uhn.hl7v2.model.v231.message.ORR_O02;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import ca.uhn.hl7v2.model.v231.segment.OBX;
import ca.uhn.hl7v2.model.v231.segment.ORC;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import com.badlogic.gdx.utils.compression.lzma.Base;
import com.ghl7.Log;
import com.ghl7.dao.SQLMapper;
import com.ghl7.message.v231.MessageFactory;
import com.ghl7.message.v231.MessageHelper;
import com.ghl7.pojo.Patient;
import com.ghl7.pojo.Transmit;
import com.ghl7.segment.CORR_O02;
import com.microsoft.sqlserver.jdbc.StringUtils;

import java.util.Map;

/**
 * @Auther WenLong
 * @Date 2024/7/29 14:03
 * @Description
 **/
public class H50PlaceItem extends BaseReceiving<ORM_O01> {
    public H50PlaceItem(String mid, String type, String event) {
        super(mid, type, event);
    }


    @Override
    public Message processMessage(ORM_O01 message, Map<String, Object> theMetadata) {
        MSH msh = message.getMSH();
        String controlID = msh.getMessageControlID().getValue();
        if (StringUtils.isEmpty(controlID)){
            Log.log("Error:controlID is empty");
        }
        Log.log("ControlID:"+controlID);
        Transmit transmit = new Transmit();
        transmit.controlId = controlID;
        transmit.requestMessage = message;



        ORC orc = null;
        try {
            String barcode = MessageHelper.getData(message,"/.ORC-3");
            Log.log("Barcode is "+barcode);

//            Patient patient = SQLMapper.getPatient(barcode, mid);
//            transmit.patient = patient;
//            if (patient == null){
//                Log.log("Not has patient has barcode:"+barcode);
//            }

            Log.log("Creating ORR_R02");
            Patient patient = new Patient();
            patient.barcode = barcode;
            transmit.patient = patient;
            CORR_O02 orrO02 = MessageFactory.generateORR_O02(transmit);
//            OBX obx = orrO02.getOBX();
//            obx.getObx1_SetIDOBX().setValue("1");
//            obx.getValueType().setValue("NM");
//            obx.getObservationIdentifier().getCe1_Identifier().setValue("17856-6");
//            obx.getObservationIdentifier().getCe2_Text().setValue("HbA1c%");
//            obx.getObservationIdentifier().getCe3_NameOfCodingSystem().setValue("LN");
//            TX observationValue = new TX(orrO02);
//            observationValue.setValue("6.7");
//            obx.insertObservationValue(0).setData(observationValue);
//            obx.getUnits().getCe1_Identifier().setValue("%(NGSP)");
//            obx.getReferencesRange().setValue("4.0-6.0");
//            obx.insertAbnormalFlags(0).setValue("H");
//            obx.insertAbnormalFlags(1).setValue("N");
//            obx.getObservationResultStatus().setValue("F");
            MessageHelper.logMessage(orrO02);

            return orrO02;
        } catch (HL7Exception e) {
            Log.log("Failed to get ORC with ORM_O01!");
            throw new RuntimeException(e);
        }

    }
}
