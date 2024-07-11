package com.ghl7.component;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * @Auther WenLong
 * @Date 2024/7/11 16:43
 * @Description
 **/
public class LogPanel extends Table {
    public LogPanel(Skin skin){
        super(skin);
        pad(16);
    }

    public void log(String log){
        row();
        TextField textField = new TextField(log, super.getSkin());
        add(textField).width(600);
    }
}
