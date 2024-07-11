package com.ghl7.instrument.c3000;


import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import com.ghl7.Log;
import com.ghl7.dao.ConnectionFactory;
import com.ghl7.dao.ConnectionType;
import com.ghl7.dao.SQLMapper;
import com.ghl7.instrument.BaseInstrument;

import java.sql.Connection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Auther WenLong
 * @Date 2024/7/11 12:01
 * @Description 接口
 **/
public class Instrument implements BaseInstrument {
    private final static String MID = "C3000";
    private static final int PORT = 5001;
    public static final boolean USE_SSL = false;

    private SQLMapper mapper;
    public  HapiContext context;
    public  HL7Service service = null;

    @Override
    public void start() {

//        Connection connection = ConnectionFactory.getConnection(ConnectionType.SHE_KANG);
//        mapper = new SQLMapper(connection);

        context = new DefaultHapiContext();
        try {
            //关闭数据验证
            context.getParserConfiguration().setValidating(false);

            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10, 100,
                30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(500));
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            context.setExecutorService(executor);

            MinLowerLayerProtocol mllp = new MinLowerLayerProtocol();
            mllp.setCharset("UTF-8");
            context.setLowerLayerProtocol(mllp);

            service = context.newServer(PORT,USE_SSL);
            service.registerApplication(new RegisterApplication());
            service.startAndWait();

            Log.log("Service started successfully!Port:"+PORT);
        } catch (InterruptedException e) {
            Log.log("Service started failed!");
            e.printStackTrace();
        }

    }
}
