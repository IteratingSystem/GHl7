package com.ghl7;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.ghl7.component.PagingPanel;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainApplication extends ApplicationAdapter {
    private Stage stage;


    @Override
    public void create() {
        PagingPanel pagingPanel = new PagingPanel(new Skin());
        pagingPanel.setFillParent(true);

        stage.addActor(pagingPanel);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
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
