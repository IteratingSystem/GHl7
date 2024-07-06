package com.ghl7.component;

import com.badlogic.gdx.scenes.scene2d.ui.*;

/**
 * @Author: WenLong
 * @Date: 2024-07-06-21:24
 * @Description: 设置页面
 */
public class SettingTable extends Table {
    public SettingTable(Skin skin){
        CheckBox debug = new CheckBox("debug",skin);

        TextButton cancel = new TextButton("cancel",skin);
        TextButton save = new TextButton("save",skin);


        pad(32);
        add(debug);
        row();
        add(cancel);
        add(save);
    }
}
