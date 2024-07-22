package com.ghl7.component;

import com.badlogic.gdx.scenes.scene2d.ui.*;

import javax.xml.soap.Text;

/**
 * @Auther WenLong
 * @Date 2024/7/11 16:43
 * @Description
 **/
public class LogPanel extends Table {
    private TextArea textArea;
    public LogPanel(Skin skin){
        super(skin);
        pad(16);

        textArea = new TextArea("",skin);
        textArea.setPrefRows(150);
        textArea.setDisabled(true);
        add(textArea).width(300);
    }

    public void log(String log){
        textArea.appendText(log+"\n");
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        int lines = textArea.getLines();
        if (lines > 100000){
            textArea.clear();
        }
    }
}
