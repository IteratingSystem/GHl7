package com.ghl7.segment;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v231.group.ORR_O02_PIDNTEORCOBRRQDRQ1RXOODSODTNTECTI;
import ca.uhn.hl7v2.model.v231.message.ORR_O02;
import ca.uhn.hl7v2.model.v231.segment.*;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.parser.ModelClassFactory;

/**
 * @Auther WenLong
 * @Date 2024/7/31 15:38
 * @Description
 **/
public class CORR_O02 extends ORR_O02 {
    public CORR_O02() {
        this(new DefaultModelClassFactory());
    }

    public CORR_O02(ModelClassFactory factory) {
        super(factory);
        this.init(factory);
    }

    private void init(ModelClassFactory factory) {
        try {
            this.add(PID.class, false, false);
            this.add(PV1.class, false, false);
            this.add(ORC.class, false, false);
            this.add(OBR.class, false, false);
            this.add(OBX.class, false, false);
        } catch (HL7Exception var3) {
            log.error("Unexpected error creating ORR_O02 - this is probably a bug in the source code generator.", var3);
        }
    }

    public PID getPID() {
        return (PID)this.getTyped("PID", PID.class);
    }
    public PV1 getPV1() {
        return (PV1)this.getTyped("PV1", PV1.class);
    }
    public ORC getORC() {
        return (ORC)this.getTyped("ORC", ORC.class);
    }
    public OBX getOBX() {
        return (OBX)this.getTyped("OBX", OBX.class);
    }
    public OBR getOBR() {
        return (OBR)this.getTyped("OBR", OBR.class);
    }
}
