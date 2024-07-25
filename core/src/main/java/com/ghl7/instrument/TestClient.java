package com.ghl7.instrument;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.ActiveConnection;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.app.Receiver;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.util.StandardSocketFactory;
import com.ghl7.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Auther WenLong
 * @Date 2024/7/22 10:07
 * @Description
 **/
public class TestClient extends BaseInstrument{
    private int targetPort;
    private String targetHost;
    private ReceivingApplication receivingApplication;

    private HapiContext context;
    private HL7Service service;
    public TestClient(String mid, int port, boolean useSTL, String targetHost, int targetPort, ReceivingApplication receivingApplication) {
        super(mid, port, useSTL);
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.receivingApplication = receivingApplication;
    }

    @Override
    public void start() {
        context = new DefaultHapiContext();

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


//            Socket socket = new StandardSocketFactory().createSocket();
//            SocketAddress inAddress = new InetSocketAddress(targetHost,targetPort);
//            socket.connect(inAddress);


//            Log.log("Client startup successful,Start port:" + port + ",Linked:" + targetHost + ":" + targetPort+",mid:"+mid);
//            InputStream inputStream = socket.getInputStream();
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                String message = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
//                System.out.println("Received message: " + message);
//            }




        try {
            service = context.newServer(port,useSTL);
            service.registerApplication(receivingApplication);
            service.startAndWait();

            ActiveConnection activeConnection = (ActiveConnection)context.newClient(targetHost,targetPort,useSTL);

            Class<?> clazz = activeConnection.getClass();
            // 步骤2: 访问私有字段
            Field field = clazz.getDeclaredField("receivers");
            // 步骤3: 修改访问控制
            field.setAccessible(true);
            // 步骤4: 获取字段的值
            List<Receiver> receivers = (List<Receiver>) field.get(activeConnection);
            for (Receiver receiver : receivers) {
                receiver.stopAndWait();
            }

            service.newConnection(activeConnection);
            Log.log("Client startup successful,Start port:" + port + ",Linked:" + targetHost + ":" + targetPort+",mid:"+mid);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (HL7Exception e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
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
