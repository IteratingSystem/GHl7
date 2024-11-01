package com.ghl7.receiving;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.message.ORM_O01;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import com.ghl7.Logger;
import com.ghl7.message.MessageFactory;
import com.ghl7.message.MessageHelper;
import com.ghl7.message.MessageMT8000;
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
public class MT8000PlaceItem extends BaseReceiving<ORM_O01> {
    public MT8000PlaceItem(String mid, String type, String event) {
        super(mid, type, event);
    }


    @Override
    public Message processMessage(ORM_O01 message, Map<String, Object> theMetadata) {
        Logger.log(getTag(),"Receiving message with place(query item)!");

        MSH msh = message.getMSH();
        String controlID = msh.getMessageControlID().getValue();
        if (StringUtils.isEmpty(controlID)){
            Logger.log(getTag(),"Error:controlID is empty");
        }
        Logger.log(getTag(),"ControlID:"+controlID);
        Transmit transmit = new Transmit();
        transmit.controlId = controlID;
        transmit.requestMessage = message;

        try {
            String barcode = MessageHelper.getData(message,"/.ORC-3");
            Logger.log(getTag(),"Barcode is "+barcode);
            Logger.log(getTag(),"Creating ORR_R02");
            Patient patient = new Patient();
            patient.barcode = barcode;
            transmit.patient = patient;
            CORR_O02 orrO02 = MessageMT8000.generateORR_O02(transmit);
            Logger.log(getTag(),"Created ORR_R02:\n"+MessageHelper.getString(orrO02));
            Logger.log(getTag(),"Next is send");
            return orrO02;
        } catch (HL7Exception e) {
            Logger.log(getTag(),"Failed to get ORC with ORM_O01!");
            throw new RuntimeException(e);
        }

    }
}
