package com.ghl7;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.ghl7.component.LogPanel;
import com.ghl7.instrument.BaseClient;
import com.ghl7.instrument.BaseInstrument;
import com.ghl7.receiving.H50Receiving;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainApplication extends ApplicationAdapter {
    private static final String SKIN_PATH = "ui/uiskin.json";
    private BaseInstrument baseInstrument;

    private Stage stage;
    public static LogPanel LOG_PANEL;

    private  BaseClient h50Client;

    @Override
    public void create() {
        AssetManager assetManager = new AssetManager();
        assetManager.load(SKIN_PATH, Skin.class);
        assetManager.finishLoading();

        Skin skin = assetManager.get(SKIN_PATH, Skin.class);


        LOG_PANEL = new LogPanel(skin);
        ScrollPane scrollPane = new ScrollPane(LOG_PANEL);
        scrollPane.setScrollbarsVisible(true);
        scrollPane.setFadeScrollBars(true);
        scrollPane.setScrollingDisabled(false, false);

        TextButton clear = new TextButton("clear",skin);
        TextButton save = new TextButton("save",skin);

        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(false);

        table.add(scrollPane).colspan(2);
        table.row();
        table.add(clear);
        table.add(save);


        stage = new Stage();
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);



        runApp();
    }

    private void runApp() {
//        BaseClient h50Client = new BaseClient("H50",5001,false,"10.0.0.11",5100,new H50Receiving("H50"));
        h50Client = new BaseClient("H50",5001,false,"127.0.0.1",5100,new H50Receiving("H50"));
        h50Client.start();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.5f, 0.5f, 0.5f, 1);
        stage.act();
        stage.draw();

    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        h50Client.dispose();
    }
}
