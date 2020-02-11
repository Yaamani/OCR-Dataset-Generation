package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DesktopLauncher {

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = MyGdxGame.RESOLUTION/2;
		config.height = MyGdxGame.RESOLUTION/2;
		config.useHDPI = true;



		new LwjglApplication(new MyGdxGame(), config);




		/*String fileName = "/Users/mac/Desktop/College/Linear Algebra/Project/SegData6_test/GroundTruthTxt/0.txt";
		//File file = new File(fileName);

		try (FileOutputStream fos = new FileOutputStream(fileName, true)) {

			String text = "Today is a beautiful day\nReally Beautiful";
			byte[] mybytes = text.getBytes();

			fos.write(mybytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
}
