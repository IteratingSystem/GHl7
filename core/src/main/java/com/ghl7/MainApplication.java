package com.ghl7;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.ghl7.component.LogPanel;
import com.ghl7.instrument.BaseInstrument;
import com.ghl7.instrument.c3000.Instrument;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainApplication extends ApplicationAdapter {
    private static final String SKIN_PATH = "ui/uiskin.json";
    private BaseInstrument baseInstrument;

    private Stage stage;
    public static LogPanel LOG_PANEL;

    @Override
    public void create() {
        AssetManager assetManager = new AssetManager();
        assetManager.load(SKIN_PATH, Skin.class);
        assetManager.finishLoading();

        Skin skin = assetManager.get(SKIN_PATH, Skin.class);


        LOG_PANEL = new LogPanel(skin);
        ScrollPane scrollPane = new ScrollPane(LOG_PANEL);
        scrollPane.setScrollbarsVisible(true);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(false, false);

        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(true);
        table.pad(32);
        table.add(scrollPane);
        stage = new Stage();
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);


        baseInstrument = new Instrument();
        baseInstrument.start();
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
    }
}
