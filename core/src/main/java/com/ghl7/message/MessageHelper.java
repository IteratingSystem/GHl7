package com.ghl7.message;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import com.ghl7.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther WenLong
 * @Date 2024/7/22 14:52
 * @Description
 **/
public class MessageHelper {
    private final static String TAG = MessageHelper.class.getSimpleName();
    private static PipeParser PARSER = new PipeParser();

    public static String getString(Message message){
        String msgStr = "";
        for (String[] strings : getAll(message)) {
            String str = "";
            for (String string : strings) {
                str += string+"|";
            }
            msgStr += str+"\n";
        }
        return msgStr;
    }
    public static void logMessage(Message message){
        for (String[] strings : getAll(message)) {
            String str = "";
            for (String string : strings) {
                str += string+"|";
            }
            Logger.log(str);
        }
    }
    public static List<String[]> getSegment(Message message,String segment){
        String messageStr = "";
        try {
            messageStr = PARSER.encode(message);
        } catch (HL7Exception e) {
            Logger.log(TAG,"Failed to getSegment in MessageHelper,Segment:"+segment);
            throw new RuntimeException(e);
        }

        List<String[]> segments = new ArrayList<>();
        String[] split = messageStr.split("\r");
        for (String segmentString : split) {
            if (segmentString.startsWith(segment)){
                String[] segmentData = segmentString.split("\\|");
                segments.add(segmentData);
            }
        }
        return segments;
    }
    public static String getData(Message message,String terserExpression) throws HL7Exception {
        Terser terser = new Terser(message);
        if (terserExpression == null || "".equals(terserExpression)){
            throw new IllegalArgumentException(
                "Terser expression must be supplied for data retrieval operation"
            );
        }

        return terser.get(terserExpression);
    }
    private static List<String[]> getAll(Message message){
        String messageStr = "";
        try {
            messageStr = PARSER.encode(message);
        } catch (HL7Exception e) {
            Logger.log("Failed to PARSER encode message;");
            throw new RuntimeException(e);
        }

        List<String[]> segments = new ArrayList<>();
        String[] split = messageStr.split("\r");
        for (String segmentString : split) {
            String[] segmentData = segmentString.split("\\|");
            segments.add(segmentData);
        }
        return segments;
    }

    public static String strToFormatStr(String str){
        if (str.isEmpty()){
            Logger.log(TAG,"Error:Date is empty:"+str);
            return "";
        }
        try {
            Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(str);
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        } catch (ParseException e) {
            Logger.log(TAG,"Failed to format str:"+str);
            e.printStackTrace();
        }
        return "";
    }
}
