package com.ghl7.component;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @Author: WenLong
 * @Date: 2024-07-06-21:06
 * @Description: 分页面板
 */
public class PagingPanel extends Table {
    private Skin skin;
    private ObjectMap<String,Table> panels;
    private ObjectMap<String, TextButton> buttons;
    private HorizontalGroup pages;
    private Table table;
    public PagingPanel(Skin skin){
        this.skin = skin;

        panels = new ObjectMap<>();
        buttons = new ObjectMap<>();
        pages = new HorizontalGroup();
        table = new Table();

        add(pages).expandX();
        row();
        add(table).expand();
    }

    public void addPanel(String name,Table panel){
        if (panels.containsKey(name)) {
            panels.remove(name);
            buttons.remove(name);
        }

        TextButton textButton = new TextButton(name, skin);
        textButton.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                table = panels.get(name);
            }
        });

        panels.put(name,panel);
        buttons.put(name,new TextButton(name,skin));
        updatePages();
    }
    public void updatePages(){
        pages.clear();
        for (ObjectMap.Entry<String, TextButton> button : buttons) {
            pages.addActor(button.value);
        }
    }
}
