package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.MyText.MyBitmapFont;
import com.mygdx.game.MyText.SimpleText;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {

    public static final String TAG = MyGdxGame.class.getSimpleName();

    public static final int RESOLUTION = 512;

    private SpriteBatch batch;

    private Image bg;
    private TextureRegion badlogic;

    private Pixmap pixmap;
    private Texture _pixTex;

    private FrameBuffer fbo;

    private TextureAtlas atlas;

    private MyBitmapFont arialRegular;
    private MyBitmapFont arialBold;
    private MyBitmapFont timesRegular;
    private MyBitmapFont timesBold;

    /*private SimpleText arialRegularText;
    private SimpleText arialBoldText;
    private SimpleText timesRegularText;
    private SimpleText timesBoldText;*/

    private Array<SimpleText> arialRegularTextArr;
    private Array<SimpleText> arialBoldTextArr;
    private Array<SimpleText> timesRegularTextArr;
    private Array<SimpleText> timesBoldTextArr;

    private Array<SimpleText>[] allAvailableFonts;

    private int startX, startY, endY, lowestLineDiff, highestLineDiff;
    private int numToDraw = 0;

    private ShaderProgram characterSeg;

    @Override
    public void create () {
        batch = new SpriteBatch();

        atlas = new TextureAtlas("All.atlas");

        arialRegular 	= new MyBitmapFont(Gdx.files.internal("Unpacked Assets/Bitmap Fonts/Arial_Regular.fnt"), atlas.findRegion("Arial_Regular"));
        arialBold 		= new MyBitmapFont(Gdx.files.internal("Unpacked Assets/Bitmap Fonts/Arial_Bold.fnt"), atlas.findRegion("Arial_Bold"));
        timesRegular 	= new MyBitmapFont(Gdx.files.internal("Unpacked Assets/Bitmap Fonts/Times_Regular.fnt"), atlas.findRegion("Times_Regular"));
        timesBold 		= new MyBitmapFont(Gdx.files.internal("Unpacked Assets/Bitmap Fonts/Times_Bold.fnt"), atlas.findRegion("Times_Bold"));

        bg = new Image(atlas.findRegion("badlogic"));
        badlogic  = atlas.findRegion("badlogic");

        /*arialRegularText 	= new SimpleText(arialRegular, "jv,wq21SFdsfkdwEFDS"*//*"HI, OCR."*//*);
        arialBoldText 		= new SimpleText(arialBold, "Hello OCR.");
        timesRegularText 	= new SimpleText(timesRegular, "HI OCR.");
        timesBoldText 		= new SimpleText(timesBold, "Hello OCR.");

        int lineDiff = 200;

        arialRegularText.setHeight(lineDiff * 0.65f);
        arialBoldText 	.setHeight(lineDiff * 0.65f);
        timesRegularText.setHeight(lineDiff * 0.65f);
        timesBoldText 	.setHeight(lineDiff * 0.65f);

        arialRegularText.setY(lineDiff * 0);
        arialBoldText 	.setY(lineDiff * 1);
        timesRegularText.setY(lineDiff * 2);
        timesBoldText 	.setY(lineDiff * 3);*/

        startX = -40;
        startY = /*-25*/0;
        endY = Gdx.graphics.getHeight()*2 + 40;
        /*lowestLineDiff = 25;
        highestLineDiff = 75;*/
        lowestLineDiff = /*80*/88;
        highestLineDiff = 90;

        arialRegularTextArr = initializeTextArray(arialRegular, startY, endY, lowestLineDiff);
        arialBoldTextArr    = initializeTextArray(arialBold, startY, endY, lowestLineDiff);
        timesRegularTextArr = initializeTextArray(timesRegular, startY, endY, lowestLineDiff);
        timesBoldTextArr    = initializeTextArray(timesBold, startY, endY, lowestLineDiff);

        allAvailableFonts = new Array[4];
        allAvailableFonts[0] = arialRegularTextArr;
        allAvailableFonts[1] = arialBoldTextArr;
        allAvailableFonts[2] = timesRegularTextArr;
        allAvailableFonts[3] = timesBoldTextArr;

        createCharacterSegShader();
        //batch.setShader(characterSeg);
        ShaderProgram.pedantic = false;

        pixmap = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGBA8888);

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

        generateDataSet("Desktop/College/Linear Algebra/Project/SegData6_test", 0, 500);
    }

    @Override
    public void render () {

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            //numToDraw = generateRandomText(arialRegularTextArr, startX, startY, endY, lowestLineDiff, highestLineDiff);
            generateDataSample(allAvailableFonts, "Desktop/College/Linear Algebra/Project/SegData6_test", 0);
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        batch.draw(badlogic, 0, 0);

        if (_pixTex !=  null)
            batch.draw(_pixTex, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        for (int i = 0; i < numToDraw; i++) {
            arialRegularTextArr.get(i).draw(batch, 1);
        }

        batch.end();
    }

    @Override
    public void dispose () {
        batch.dispose();
        atlas.dispose();
        pixmap.dispose();
    }

    private Array<SimpleText> initializeTextArray(MyBitmapFont font, int startY, int endY, int lowestLineDiff) {
        int yDistance = endY - startY;
        int num = yDistance / lowestLineDiff + 1;

        Array<SimpleText> arr;
        arr = new Array<>(true, num, SimpleText.class);

        for (int i = 0; i < num; i++) {
            arr.add(new SimpleText(font, "", badlogic));
        }

        //Gdx.app.log(TAG, "" + arr.size);
        //System.out.print("" + a);

        return arr;
    }

    private int generateRandomText(Array<SimpleText> arr, int startX, int startY, int endY, int lowestLineDiff, int highestLineDiff) {
        Random random = new Random();

        //startY = startY + random.nextInt(20) - 10;

        int lineDiff = random.nextInt(highestLineDiff - lowestLineDiff + 1) + lowestLineDiff;

        int numOfTexts = (endY - startY) / lineDiff + 1;

        for (int i = 0; i < numOfTexts; i++) {
            SimpleText text = arr.get(i);
            text.setY(i * lineDiff + startY);
            text.setX(startX);
            text.setHeight(lineDiff * 0.75f);
            text.setCharSequence(generateRandomString(130), true);
        }

        return numOfTexts;
    }

    private String generateRandomString(int maxNumOfChars) {
        StringBuilder string = new StringBuilder();
        string.append('|');

        Random random = new Random();
        int charToDraw = random.nextInt(maxNumOfChars+1);
        for (int i = 0; i < charToDraw; i++) {
            //string.append((char) (random.nextInt(127 - 32) + 32));
            if (random.nextInt(5) == 0) string.append(' ');
            string.append((char) (random.nextInt(122 - 97) + 97));
        }
        return string.toString();
        //return "kdfkldWWsl kdfmakld dfdsf kd saad fWWT TYssda asdasdasd sd asdasd";
    }

    private void generateDataSample(Array<SimpleText>[] allAvailableFonts, String path, int i) {
        Random random = new Random();
        int randomFont = random.nextInt(allAvailableFonts.length);

        int drawNum = generateRandomText(allAvailableFonts[randomFont], startX, startY, endY, lowestLineDiff, highestLineDiff);

        fbo.begin();
        batch.setShader(null);
        //batch.setShader(characterSeg);
        //Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        drawText(allAvailableFonts[randomFont], drawNum, false);
        bufferToPng(path + "/Images/" + i + ".png");
        fbo.end();

        fbo.begin();
        batch.setShader(characterSeg);
        //Gdx.gl.glBlendEquation();
        //Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);
        drawText(allAvailableFonts[randomFont], drawNum, true);
        bufferToPng(path + "/Labels/" + i + ".png");
        fbo.end();

        groundTruthTxtSample(randomFont, path, i);
    }

    private void drawText(Array<SimpleText> arr, int drawNum, boolean separateBoundingBoxes) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        for (int i = 0; i < drawNum; i++) {
            arr.get(i).setSeparateBoundingBoxes(separateBoundingBoxes);
            arr.get(i).draw(batch, 1);
        }

        batch.end();
    }

    private void bufferToPng(String path) {
        byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        // this loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
        for(int i = 4; i < pixels.length; i += 4) {
            pixels[i - 1] = (byte) 255;
        }

        BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
        //_pixTex = fbo.getColorBufferTexture();
        PixmapIO.writePNG(Gdx.files.external(path), pixmap);
    }

    private void groundTruthTxtSample(int randomFont, String path, int i) {
        StringBuilder text = new StringBuilder();
        for (SimpleText simpleText : allAvailableFonts[randomFont]) {
            text .append(simpleText.getGroundTruthTxt());
        }

        String fileName = "/Users/mac/" + path + "/GroundTruthTxt/" + i + ".txt";
        //File file = new File(fileName);

        try (FileOutputStream fos = new FileOutputStream(fileName)) {

            byte[] mybytes = text.toString().getBytes();

            fos.write(mybytes);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateDataSet(String path, int counterStart, int totalSamples) {
        for (int i = counterStart; i < totalSamples + counterStart; i++) {
            generateDataSample(allAvailableFonts, path, i);
            System.out.print("\r" + (float)(i-counterStart)/(float)totalSamples * 100 + "%");
        }
        System.out.print("\r" + 100 + "%");
    }

    private void createCharacterSegShader () {
        String vertexShader = "attribute vec4 a_position;    \n" +
                "attribute vec4 a_color;\n" +
                "attribute vec2 a_texCoord0;\n" +
                "uniform mat4 u_projTrans;\n" +
                "varying vec4 v_color;\n" +
                "varying vec2 v_texCoords;\n" +
                "void main()                  \n" +
                "{                            \n" +
                "   v_color = a_color; \n" +
                "   v_texCoords = a_texCoord0; \n" +
                "   gl_Position = u_projTrans * a_position;  \n"      +
                "}                            \n" ;
        String fragmentShader = "#ifdef GL_ES\n" +
                "precision mediump float;\n" +
                "#endif\n" +
                "varying vec4 v_color;\n" +
                "varying vec2 v_texCoords;\n" +
                "uniform sampler2D u_texture;\n" +
                "\n" +
                "void main()                                  \n" +
                "{                                            \n" +
                "   //if (v_color.r == 0.0)\n" +
                "   //    gl_FragColor = v_color /*texture2D(u_texture, v_texCoords)*/;\n" +
                "   //else\n" +
                "   //    gl_FragColor = vec4(1, 1, 1, 1);\n" +
                "   gl_FragColor = v_color;\n" +
                "}";

        characterSeg = new ShaderProgram(vertexShader, fragmentShader);
        if (!characterSeg.isCompiled()) throw new IllegalArgumentException("couldn't compile shader: " + characterSeg.getLog());
    }
}
