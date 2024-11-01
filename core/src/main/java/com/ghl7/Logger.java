package com.ghl7;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Auther WenLong
 * @Date 2024/7/11 16:19
 * @Description 日志
 **/
public class Logger {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS"); // 格式化日期和时间

    public static void log(String tag, String text) {
        // 获取当前日期和时间
        String date = dateFormat.format(new Date());
        String time = timeFormat.format(new Date());
        String logFilePath = Paths.LOG_DIR.getPath() + File.separator + date + ".txt";

        // 打印到控制台
        System.out.println("[" + time + "] [" + tag + "] " + text);

        // 检查日志目录是否存在，如果不存在则创建
        File logDir = new File(Paths.LOG_DIR.getPath());
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        // 检查日志文件是否存在，如果不存在则创建
        File logFile = new File(logFilePath);
        try (PrintWriter out = new PrintWriter(new FileWriter(logFile, true))) {
            // 写入日志信息
            out.println("[" + time + "] [" + tag + "] " + text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void log(String text){
        System.out.println(text);
    }
    public static void logHL7Message(Message message){
        PipeParser pipeParser = new PipeParser();
        String messageString = null;
        try {
            messageString = pipeParser.encode(message);
        } catch (HL7Exception e) {
            Logger.log("Error parsing message!");
            throw new RuntimeException(e);
        }
        String[] split = messageString.split("\r");
        for (String s : split) {
            Logger.log(s);
        }
    }
}
