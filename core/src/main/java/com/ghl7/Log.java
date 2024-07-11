package com.ghl7;

import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.ghl7.component.LogPanel;

/**
 * @Auther WenLong
 * @Date 2024/7/11 16:19
 * @Description 日志
 **/
public class Log {
    private final static LogPanel LOG_PANEL = MainApplication.LOG_PANEL;
    public static void log(String text){
        LOG_PANEL.log(text);
    }
}
