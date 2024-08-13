package com.ghl7;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.ghl7.component.LogPanel;

import java.util.Vector;

/**
 * @Auther WenLong
 * @Date 2024/7/11 16:19
 * @Description 日志
 **/
public class Log {
    private final static LogPanel LOG_PANEL = MainApplication.LOG_PANEL;
    public static void log(String text){
        if (LOG_PANEL == null){
            System.out.println(text);
            return;
        }
        LOG_PANEL.log(text);
    }
    public static void logHL7Message(Message message){
        PipeParser pipeParser = new PipeParser();
        String messageString = null;
        try {
            messageString = pipeParser.encode(message);
        } catch (HL7Exception e) {
            Log.log("Error parsing message!");
            throw new RuntimeException(e);
        }
        String[] split = messageString.split("\r");
        for (String s : split) {
            Log.log(s);
        }
    }
}
