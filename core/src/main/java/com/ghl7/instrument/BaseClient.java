package com.ghl7.instrument;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.*;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.llp.LowerLayerProtocol;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.message.ACK;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.util.StandardSocketFactory;
import com.ghl7.Log;
import com.ghl7.message.MessageFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.*;

/**
 * @Auther WenLong
 * @Date 2024/7/22 10:07
 * @Description
 **/
public class BaseClient extends BaseInstrument{
    private int targetPort;
    private String targetHost;
    private ReceivingApplication receivingApplication;

    private HapiContext context;
    private HL7Service service;
    public BaseClient(String mid, int port, boolean useSTL,String targetHost,int targetPort,ReceivingApplication receivingApplication) {
        super(mid, port, useSTL);
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.receivingApplication = receivingApplication;
    }

    @Override
    public void start() {
        context = new DefaultHapiContext();

        try {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10, 100,
                30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(100));
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            MinLowerLayerProtocol mllp = new MinLowerLayerProtocol();
            mllp.setCharset("UTF-8");
            //关闭数据验证
            context.getParserConfiguration().setValidating(false);
            context.setExecutorService(executor);
//            context.setSocketFactory(new StandardSocketFactory());
            context.setLowerLayerProtocol(mllp);

            service = context.newServer(port,useSTL);
            service.registerApplication(receivingApplication);
            service.startAndWait();

            Parser parser = context.getGenericParser(); // 使用 HapiContext 提供的通用解析器
            LowerLayerProtocol llp = context.getLowerLayerProtocol(); // 使用 HapiContext 设置的 LowerLayerProtocol
            Socket socket = context.getSocketFactory().createSocket();
            SocketAddress socketAddress = new InetSocketAddress(targetHost, targetPort);
            socket.connect(socketAddress, 5000); // 5000 是连接超时时间
            ActiveConnection activeConnection = new ActiveConnection(parser, llp, socket);
            service.newConnection(activeConnection);

            Log.log("Client startup successful,Start port:" + port + ",Linked:" + targetHost + ":" + targetPort+",mid:"+mid);


        } catch (InterruptedException e) {
            System.out.println("Client startup failed!"+e.getMessage());
            e.printStackTrace();
        } catch (LLPException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dispose() {
        service.stop();
        try {
            context.close();
            System.out.println("Close context success!");
        } catch (IOException e) {
            System.out.println("Close context failed!");
            throw new RuntimeException(e);
        }
    }
}
