package com.ghl7.receiving;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.model.v231.message.ACK;
import ca.uhn.hl7v2.model.v231.message.ORM_O01;
import ca.uhn.hl7v2.model.v231.message.ORU_R01;
import ca.uhn.hl7v2.model.v231.segment.OBR;
import ca.uhn.hl7v2.model.v231.segment.OBX;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import ca.uhn.hl7v2.util.Terser;
import com.ghl7.Log;
import com.ghl7.dao.SQLMapper;
import com.ghl7.message.MessageHelper;
import com.ghl7.pojo.Patient;
import com.ghl7.pojo.Result;
import com.microsoft.sqlserver.jdbc.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Auther WenLong
 * @Date 2024/7/22 10:49
 * @Description
 **/
public class H50Receiving implements ReceivingApplication {
    private String mid;


    public H50Receiving(String mid) {
        this.mid = mid;
    }

    @Override
    public Message processMessage(Message message, Map map) throws ReceivingApplicationException, HL7Exception {
        //结果接收
        if (message instanceof ORU_R01){
            Log.log("Is result message!");
            return saveResult((ORU_R01) message);
        }
        //双向查询
        if (message instanceof ORM_O01){
            Log.log("Is query message!");
        }

        return null;
    }

    @Override
    public boolean canProcess(Message message) {
        Log.log("Received message:");
        MessageHelper.logMessage(message);
        return true;
    }
    private synchronized ACK saveResult(ORU_R01 oruR01) {

        try {
            ACK ack = (ACK)oruR01.generateACK();



            //读取id
            String originalId = MessageHelper.getData(oruR01,"/.OBR-3");

            //获取为条码或样本号
            String sid = "";
            String barcode = "";
            if (StringUtils.isEmpty(originalId)){
                return ack;
            }else if (originalId.length() <= 6) {
                Log.log("Length <= 6,Is sid.");
                sid = originalId;
            }else {
                Log.log("Length > 6,Is barcode.");
                barcode = originalId;
            }


            List<String[]> obxs = MessageHelper.getSegment(oruR01, "OBX");
            List<Result> results = new ArrayList<>();
            for (String[] obx : obxs) {
                //结果类型
                String valueType = obx[2];
                if (!"NM".equals(valueType)){
                    continue;
                }
                //获取项目及结果
                String itemName = obx[3].split("\\^")[1];
                String resultValue = obx[5];
                String resDate = obx[14];
                resDate = MessageHelper.strToFormatStr(resDate);
                Result result = new Result();
                result.itemName = itemName;
                result.result = resultValue;
                result.resDate = resDate;
                results.add(result);
            }


            //以样本号接收结果



            //以条码号接收结果
            Patient patient = SQLMapper.getPatient(barcode, mid);
            if (patient == null || StringUtils.isEmpty(patient.id)) {
                Log.log("This patient not in lis system;");
                return ack;
            }
            if (!"6".equals(patient.status)){
                Log.log("This barcode is not layout,barcode:"+barcode+",status:"+patient.status);
                return ack;
            }
            patient.results = results;
            SQLMapper.saveResult(patient);
            return ack;
        } catch (HL7Exception e) {
            Log.log("Failed to parse OBR information, Unable to obtain barcode!");
            throw new RuntimeException(e);
        } catch (IOException e) {
            Log.log("Failed to generate ACK!");
            throw new RuntimeException(e);
        }
    }
}
