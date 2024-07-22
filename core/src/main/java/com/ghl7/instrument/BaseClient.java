package com.ghl7.instrument;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.ActiveConnection;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.llp.LowerLayerProtocol;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import com.ghl7.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.Connection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    public BaseClient(String mid, int port, boolean useSSL,String targetHost,int targetPort,ReceivingApplication receivingApplication) {
        super(mid, port, useSSL);
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.receivingApplication = receivingApplication;
    }

    @Override
    public void start() {
        context = new DefaultHapiContext();
        try {
            //关闭数据验证
            context.getParserConfiguration().setValidating(false);

            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10, 100,
                30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(100));
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            context.setExecutorService(executor);

            MinLowerLayerProtocol mllp = new MinLowerLayerProtocol();
            mllp.setCharset("UTF-8");
            context.setLowerLayerProtocol(mllp);

            service = context.newServer(port,useSSL);
            service.registerApplication(receivingApplication);
            service.startAndWait();

            Parser parser = new PipeParser();
            LowerLayerProtocol llp = new MinLowerLayerProtocol();
            llp.setCharset("UTF-8");
            Socket socket = context.getSocketFactory().createSocket();
            SocketAddress socketAddress = new InetSocketAddress(targetHost,targetPort);
            socket.connect(socketAddress);
            ActiveConnection activeConnection = new ActiveConnection(parser, llp, socket);
            service.newConnection(activeConnection);
            Log.log("Client startup successful,Start port:" + port + ",Linked:" + targetHost + ":" + targetPort+",mid:"+mid);
        } catch (InterruptedException e) {
            System.out.println("Client startup failed!"+e.getMessage());
            e.printStackTrace();
        } catch (LLPException e) {
            System.out.println("Client startup failed!"+e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("Client startup failed!Please confirm the connection address:"+targetHost+":"+targetPort);
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
