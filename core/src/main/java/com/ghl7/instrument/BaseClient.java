package com.ghl7.instrument;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.*;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.util.StandardSocketFactory;
import com.ghl7.Logger;
import com.ghl7.receiving.BaseReceiving;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Auther WenLong
 * @Date 2024/7/22 10:07
 * @Description
 **/
public class BaseClient extends BaseInstrument{
    private int targetPort;
    private String targetHost;
//    private ReceivingApplication receivingApplication;
    private List<BaseReceiving> receivings;

    private HapiContext context;
    private HL7Service service;
    public BaseClient(String mid, int port, boolean useSTL,String targetHost,int targetPort,List<BaseReceiving> receivings) {
        super(mid, port, useSTL);
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.receivings = receivings;
    }

    @Override
    public synchronized void start() {
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
            context.setSocketFactory(new StandardSocketFactory());
            context.setLowerLayerProtocol(mllp);

            service = context.newServer(port,useSTL);
            for (BaseReceiving receiving : receivings) {
                service.registerApplication(receiving.type,receiving.event,receiving);
            }
            service.setExceptionHandler(new ExceptionHandler());
            service.startAndWait();

            Socket socket = context.getSocketFactory().createSocket();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(targetHost, targetPort);
            socket.connect(inetSocketAddress,5000);
            ActiveConnection connection = new ActiveConnection(
                context.getGenericParser(),
                context.getLowerLayerProtocol(),
                socket,
                context.getExecutorService());
            service.newConnection(connection);

            Logger.log("Successful to startup client,Start port:" + port + ",Linked:" + targetHost + ":" + targetPort+",mid:"+mid);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (LLPException e) {
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
