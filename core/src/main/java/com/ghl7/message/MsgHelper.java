package com.ghl7.message;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import com.ghl7.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wenlong
 * @Date: 2023/11/17
 * @Name: MessageStringHelper
 * @Description:
 **/
public class MsgHelper {
    private String messageString;
    private PipeParser pipeParser;

    public MsgHelper(Message message){
        try {
            pipeParser = new PipeParser();
            messageString = pipeParser.encode(message);
        } catch (HL7Exception e) {
            e.printStackTrace();
        }
    }

    public int getLength(){
        return messageString.split("\r").length;
    }

    public int getLength(String segment){
        int length = 0;
        String[] split = messageString.split("\r");
        for (String s : split) {
            if (s.startsWith(segment)) {
                length++;
            }
        }
        return length;
    }

    public List<String[]> getSegment(String segment){
        List<String[]> segments = new ArrayList<>();
        String[] split = messageString.split("\r");
        for (String segmentString : split) {
            if (segmentString.startsWith(segment)){
                String[] segmentData = segmentString.split("\\|");
                segments.add(segmentData);
            }
        }
        return segments;
    }

    public List<String[]> getAll(){
        List<String[]> segments = new ArrayList<>();
        String[] split = messageString.split("\r");
        for (String segmentString : split) {
            String[] segmentData = segmentString.split("\\|");
            segments.add(segmentData);
        }
        return segments;
    }

    public void printMessage(){
        for (String[] strings : getAll()) {
            for (String string : strings) {
                System.out.print(string+"|");
            }
            System.out.println();
        }
    }
    public void logMessage(){
        for (String[] strings : getAll()) {
            for (String string : strings) {
                Log.log(string+"|");
            }
        }
    }
}
