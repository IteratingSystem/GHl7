package com.ghl7.message;

import ca.uhn.hl7v2.model.v231.segment.*;
import com.ghl7.pojo.Transmit;
import com.ghl7.segment.CORR_O02;

public class MessageMT8000 {
    public static CORR_O02 generateORR_O02(Transmit transmit){
        CORR_O02 orrO02 = new CORR_O02();
        transmit.responseMessage = orrO02;
        transmit.type = "ORR";
        transmit.event = "O02";
        transmit.modelClassFactory = orrO02.getModelClassFactory();
        MSH msh = StructureFactory.getMSH(transmit);
        MSA msa = StructureFactory.getMSA(transmit);
        PID pid = StructureFactory.getPID(transmit);
        PV1 pv1 = StructureMT8000.getPV1(transmit);
        ORC orc = StructureMT8000.getORC(transmit);
        OBR obr = StructureFactory.getOBR(transmit);
        int obxLength = StructureMT8000.createOBX(transmit);
        return orrO02;
    }
}
