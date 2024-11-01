package com.ghl7.instrument;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.protocol.ReceivingApplicationExceptionHandler;
import com.ghl7.Logger;

import java.util.Map;

/**
 * @Auther WenLong
 * @Date 2024/7/28 14:44
 * @Description
 **/
public class ExceptionHandler implements ReceivingApplicationExceptionHandler {
    @Override
    public String processException(String s, Map<String, Object> map, String s1, Exception e) throws HL7Exception {
        Logger.log("Error:"+s+","+map+","+s1+","+e.getMessage());
        return null;
    }
}
