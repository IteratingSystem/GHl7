package com.ghl7;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.ghl7.instrument.BaseInstrument;
import com.ghl7.instrument.c3000.Instrument;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainApplication extends ApplicationAdapter {
    private BaseInstrument baseInstrument;

    private Stage stage;

    @Override
    public void create() {
        baseInstrument = new Instrument();
        stage = new Stage();

    }

    @Override
    public void resize(int width, int height) {
        ScreenUtils.clear(0, 0, 0.2f, 1);
        stage.act();
        stage.draw();
    }

    @Override
    public void render() {
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
