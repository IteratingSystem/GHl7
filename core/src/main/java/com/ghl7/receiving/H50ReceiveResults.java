package com.ghl7.receiving;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.message.ORU_R01;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import com.ghl7.Log;
import com.ghl7.dao.SQLMapper;
import com.ghl7.message.v231.MessageHelper;
import com.ghl7.pojo.Patient;
import com.ghl7.pojo.Result;
import com.microsoft.sqlserver.jdbc.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Auther WenLong
 * @Date 2024/7/29 14:03
 * @Description
 **/
public class H50ReceiveResults extends BaseReceiving<ORU_R01> {

    public H50ReceiveResults(String mid, String type, String event) {
        super(mid, type, event);
    }

    @Override
    public Message processMessage(ORU_R01 message, Map<String, Object> theMetadata) throws ReceivingApplicationException, HL7Exception {
        Message response = null;
        try {
            response = message.generateACK();
            //读取id
            String originalId = MessageHelper.getData(message,"/.OBR-3");

            //获取为条码或样本号
            String sid = "";
            String barcode = "";
            if (StringUtils.isEmpty(originalId)){
                Log.log("Error:originalId is empty!");
                return response;
            }else if (originalId.length() <= 6) {
                Log.log("Length <= 6,Is sid:"+originalId);
                sid = originalId;
            }else {
                Log.log("Length > 6,Is barcode:"+originalId);
                barcode = originalId;
            }


            List<String[]> obxs = MessageHelper.getSegment(message, "OBX");
            List<Result> results = new ArrayList<>();
            String resDate = "";
            for (String[] obx : obxs) {
                //结果类型
                String valueType = obx[2];
                if (!"NM".equals(valueType)){
                    continue;
                }
                //获取项目及结果
                String itemName = obx[3].split("\\^")[1];
                String resultValue = obx[5];
                resDate = MessageHelper.getData(message,"/.OBR-7");
                resDate = MessageHelper.strToFormatStr(resDate);
                Result result = new Result();
                result.itemName = itemName;
                result.result = resultValue;
                result.resDate = resDate;
                results.add(result);
            }


            //以样本号接收结果
            if (!StringUtils.isEmpty(sid)){

                Patient patient = SQLMapper.getPatient(sid,mid,resDate);
                if (patient == null){
                    Log.log("This patient not in system;sid:"+sid);
                    return response;
                }
                if ("7".equals(patient.status)){
                    patient.sid = "999" + patient.sid;
                }
                SQLMapper.saveResult(patient);
            }else{
                //以条码号接收结果
                Patient patient = SQLMapper.getPatient(barcode, mid);
                if (patient == null || StringUtils.isEmpty(patient.id)) {
                    Log.log("This patient not in system;barcode:"+barcode);
                    return response;
                }
                if (!"6".equals(patient.status)){
                    Log.log("This barcode is not layout,barcode:"+barcode+",status:"+patient.status);
                    return response;
                }
                patient.results = results;
                SQLMapper.saveResult(patient);
            }
            return response;

        } catch (IOException e) {
            Log.log("Failed to generate ACK!");
            throw new RuntimeException(e);
        }
    }
}
