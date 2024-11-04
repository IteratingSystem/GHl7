package com.ghl7.instrument;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import com.ghl7.Logger;
import com.ghl7.receiving.BaseReceiving;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Auther WenLong
 * @Date 2024/7/22 10:04
 * @Description
 **/
public class BaseService extends BaseInstrument{
    private final static String TAG = BaseService.class.getSimpleName();
    public HapiContext context;
    public HL7Service service;
    private List<BaseReceiving> receivings;
    public BaseService(String mid, int port, boolean useSSL,List<BaseReceiving> receivings) {
        super(mid, port, useSSL);
        this.receivings = receivings;
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
            for (BaseReceiving receiving : receivings) {
                service.registerApplication(receiving.type,receiving.event,receiving);
            }
            service.startAndWait();


            Logger.log(TAG,"Success to start service,mid:"+mid+",startPort:"+port);
        } catch (InterruptedException e) {
            Logger.log(TAG,"Failed to start service,mid:"+mid+",startPort:"+port);
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
