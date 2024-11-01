package com.ghl7.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.utils.Json;
import com.ghl7.AppRule;
import com.ghl7.MainApplication;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
//        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
//        createApplication();
        new MainApplication();
    }

    private static Lwjgl3Application createApplication() {
//        return new Lwjgl3Application(new MainApplication(), getDefaultConfiguration());
        return null;
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("AppRule.WIN_TITLE");
        configuration.useVsync(true);
        configuration.setForegroundFPS(60);
        configuration.setResizable(false);
        //// Limits FPS to the refresh rate of the currently active monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.
//        configuration.setWindowedMode(AppRule.WIN_WIDTH, AppRule.WIN_HEIGHT);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}
