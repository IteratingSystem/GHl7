package com.ghl7.instrument;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.message.ACK;
import ca.uhn.hl7v2.model.v231.message.ORU_R01;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import com.ghl7.message.MessageFactory;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * @Auther WenLong
 * @Date 2024/7/24 9:46
 * @Description
 **/
public class TestService {

    public static void main(String[] args){
        TestService testService = new TestService();
    }

    private BaseService baseService;

    public TestService(){
        baseService = new BaseService("H50",5100,false,null);
        baseService.start();
        inputCtrl();
    }

    private void inputCtrl(){

        while (true){
            Scanner scanner = new Scanner(System.in);
            String str = scanner.nextLine();

            switch (str){
                case "size":
                    List<Connection> remoteConnections = baseService.service.getRemoteConnections();
                    if (remoteConnections != null) {
                        System.out.println(remoteConnections.size());
                        break;
                    }
                    System.out.println(0);
                    break;
                case "send":
                    sendACK();
                    break;
            }
        }
    }

    private void sendACK(){
        List<Connection> remoteConnections = baseService.service.getRemoteConnections();
        if (remoteConnections == null) {
            return;
        }

        for (Connection connection : remoteConnections) {
            Initiator initiator = connection.getInitiator();
//
            System.out.println("创建ORU_R01");
            ORU_R01 oruR01 = new ORU_R01();
            MSH msh = oruR01.getMSH();
            try {
                msh.getFieldSeparator().setValue("|");
                msh.getEncodingCharacters().setValue("^~\\&");

                //厂家名称
                msh.getSendingApplication().getNamespaceID().setValue("1");
                msh.getSendingFacility().getNamespaceID().setValue("H50");

                //lis信息
                msh.getReceivingApplication().getNamespaceID().setValue("璟桥LIS");
                msh.getReceivingFacility().getNamespaceID().setValue("LIS");

                msh.getDateTimeOfMessage().getTimeOfAnEvent().setValue("");
                msh.getMessageType().getMessageType().setValue("ORU");
                msh.getMessageType().getTriggerEvent().setValue("R01");
                msh.getMessageControlID().setValue("1");
                msh.getProcessingID().getProcessingID().setValue("1");
                msh.getVersionID().getVersionID().setValue("2.3.1");

                //发送的结果类型
                msh.getApplicationAcknowledgmentType().setValue("String");
                System.out.println("Send ORU_R01");
                Message message = initiator.sendAndReceive(oruR01);
            } catch (DataTypeException e) {
                throw new RuntimeException(e);
            } catch (HL7Exception e) {
                throw new RuntimeException(e);
            } catch (LLPException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
