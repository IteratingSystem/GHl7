package com.ghl7.instrument;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import com.ghl7.Logger;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Auther WenLong
 * @Date 2024/7/22 10:04
 * @Description
 **/
public class BaseService extends BaseInstrument{
    public HapiContext context;
    public HL7Service service;
    private ReceivingApplication receivingApplication;
    public BaseService(String mid, int port, boolean useSSL,ReceivingApplication receivingApplication) {
        super(mid, port, useSSL);
        if (receivingApplication != null){
            this.receivingApplication = receivingApplication;
        }
    }

    @Override
    public void start() {
        context = new DefaultHapiContext();
        try {
            //关闭数据验证
            context.getParserConfiguration().setValidating(false);

            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                20, 200,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(500));
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            context.setExecutorService(executor);

            MinLowerLayerProtocol mllp = new MinLowerLayerProtocol();
            mllp.setCharset("UTF-8");
            context.setLowerLayerProtocol(mllp);

            service = context.newServer(port,useSTL);
            if (receivingApplication != null){
                service.registerApplication(receivingApplication);
            }
            service.startAndWait();

            System.out.println("Service started successfully!Port:"+port);
            Logger.log("Service started successfully!Port:"+port);
        } catch (InterruptedException e) {
            System.out.println("Service started failed!");
            e.printStackTrace();
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
