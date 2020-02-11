package com.mygdx.game.desktop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class TexturePacking {
    public static void main (String[] arg) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 4096;
        settings.maxHeight = 4096;
        settings.filterMag = Texture.TextureFilter.Linear;
        settings.filterMin = Texture.TextureFilter.Linear;
        settings.flattenPaths = true;
        settings.combineSubdirectories = true;


        TexturePacker.process(settings,
                "/Users/mac/Documents/PROGRAMMING/JAVA/PROJECTS/OCR-Dataset-Generation/android/assets/Unpacked Assets",
                "/Users/mac/Documents/PROGRAMMING/JAVA/PROJECTS/OCR-Dataset-Generation/android/assets",
                "All");
    }
}
