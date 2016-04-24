package com.lobseek.decimated.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.lobseek.decimated.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
                config.width = 960;
                config.height = 540;
                config.resizable = true;
                config.title = "Decimare";
		new LwjglApplication(new Main(), config);
	}
}
