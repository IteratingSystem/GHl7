package com.ghl7.receiving;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.message.ORM_O01;
import ca.uhn.hl7v2.model.v231.message.ORR_O02;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import ca.uhn.hl7v2.model.v231.segment.ORC;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import com.badlogic.gdx.utils.compression.lzma.Base;
import com.ghl7.Log;
import com.ghl7.dao.SQLMapper;
import com.ghl7.message.v231.MessageFactory;
import com.ghl7.message.v231.MessageHelper;
import com.ghl7.pojo.Patient;
import com.ghl7.pojo.Transmit;
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
        Transmit transmit = new Transmit();
        transmit.controlId = controlID;
        transmit.message = message;

        ORC orc = null;
        try {
            orc = (ORC)message.get("ORC");
            String barcode = orc.getFillerOrderNumber().getUniversalID().getValue();
            Log.log("Barcode is "+barcode);

            Patient patient = SQLMapper.getPatient(barcode, mid);
            if (patient == null){
                Log.log("Not has patient has barcode:"+barcode);
            }

            ORR_O02 orrO02 = MessageFactory.generateORR_O02(transmit);
            Log.log("Create ORR_R02");
            MessageHelper.logMessage(orrO02);
            return orrO02;
        } catch (HL7Exception e) {
            Log.log("Failed to get ORC with ORM_O01!");
            throw new RuntimeException(e);
        }

    }


}
