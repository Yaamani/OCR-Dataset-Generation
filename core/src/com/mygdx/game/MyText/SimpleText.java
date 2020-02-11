package com.mygdx.game.MyText;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MyGdxGame;

/**
 * <p>
 * A One line text that extends {@link Actor} which means it has all the features of an Actor [x, y, width, height .....].
 * </p>
 * <p>
 * If {@code aspectRatioLocked == true}, the text can't be stretched and you can change one dimension at a time[width or height]
 * and the other will be changed automatically.
 * </p>
 * <p>
 * padding and kerning may not be supported yet.
 * </p>
 */

public class SimpleText extends Actor {

    public static final String TAG = SimpleText.class.getSimpleName();

    private MyBitmapFont myBitmapFont;
    private String charSequence;
    private boolean aspectRatioLocked;

    private TextureRegion _temp;

    private Array<BitmapFont.Glyph> glyphs;

    private int charSequenceWidthPixelUnits;
    private int heightPixelUnits;
    private int yoffsetPlusHeightMax;

    public static final int HOW_MANY_PIXELS_TO_SEPARATE = 4;
    private boolean separateBoundingBoxes;
    private TextureRegion badlogic;

    private StringBuilder groundTruthTxt;

    public SimpleText(MyBitmapFont myBitmapFont, String charSequence, TextureRegion badlogic) {
        this.myBitmapFont = myBitmapFont;
        glyphs = new Array<BitmapFont.Glyph>(true, 50);
        setCharSequence(charSequence, true);
        this.aspectRatioLocked = true;

        _temp = new TextureRegion();

        this.badlogic = badlogic;
    }

    public void setSeparateBoundingBoxes(boolean separateBoundingBoxes) {
        this.separateBoundingBoxes = separateBoundingBoxes;
    }

    public String getGroundTruthTxt() {
        return groundTruthTxt!= null ? groundTruthTxt.toString(): "";
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        groundTruthTxt = new StringBuilder();
        int min_x0_line_gt = MyGdxGame.RESOLUTION,
                min_y0_line_gt = MyGdxGame.RESOLUTION,
                max_x1_line_gt = 0,
                max_y1_line_gt = 0;
        int min_x0_word_gt = MyGdxGame.RESOLUTION,
                min_y0_word_gt = MyGdxGame.RESOLUTION,
                max_x1_word_gt = 0,
                max_y1_word_gt = 0;

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        float prevX = 0, prevY = 0, prevW = 0, prevH = 0;

        float cursorX = 0;
        float w, xoffset = 0, myOffsetPixelUnits, myYOffsetWorldUnits, xadvance, kerning;
        int i = 0;
        for (BitmapFont.Glyph glyph : glyphs) {

            _temp.setRegion(myBitmapFont.pages[glyph.page], glyph.srcX, glyph.srcY, glyph.width, glyph.height);


            w = (float) glyph.width / charSequenceWidthPixelUnits * getWidth();
            if (i > 0) xoffset = (float) glyph.xoffset / charSequenceWidthPixelUnits * getWidth();
            myOffsetPixelUnits = yoffsetPlusHeightMax - ((glyph.yoffset) + glyph.height);
            myYOffsetWorldUnits = myOffsetPixelUnits / heightPixelUnits * getHeight();
            xadvance = (float) glyph.xadvance / charSequenceWidthPixelUnits * getWidth();

            if (i > 0) {
                kerning = (float) glyphs.get(i-1).getKerning((char) glyph.id) / charSequenceWidthPixelUnits * getWidth();
                cursorX += kerning;
                //Gdx.app.log(TAG, "" + glyphs.get(i-1).id + ", " + glyph.id + ", " + glyphs.get(i-1).getKerning((char) glyph.id) + ", " + kerning);
            }

            float x = getX() + cursorX + xoffset;
            float y = getY() + myYOffsetWorldUnits;
            float height = w * glyph.height / glyph.width;

            if (glyph.id != ' ') {
                if (aspectRatioLocked)
                    batch.draw(_temp, x, y, w, height);
                else batch.draw(_temp, x, y, w, getHeight() * glyph.height / heightPixelUnits);
            }

            if (separateBoundingBoxes & glyph.id != ' ') {
                batch.setColor(Color.BLACK);

                if (prevX + prevW + HOW_MANY_PIXELS_TO_SEPARATE > x) {
                    batch.draw(badlogic,
                            x - HOW_MANY_PIXELS_TO_SEPARATE / 2f,
                            y,
                            HOW_MANY_PIXELS_TO_SEPARATE,
                            height);

                    if (/*prevX + prevW > x & */prevY + prevH > y + height)
                        batch.draw(badlogic,
                                x - HOW_MANY_PIXELS_TO_SEPARATE / 2f,
                                y + height - HOW_MANY_PIXELS_TO_SEPARATE / 2f,
                                w + HOW_MANY_PIXELS_TO_SEPARATE / 2f,
                                HOW_MANY_PIXELS_TO_SEPARATE);

                    if (prevY < y)
                        batch.draw(badlogic,
                                x - HOW_MANY_PIXELS_TO_SEPARATE / 2f,
                                y - HOW_MANY_PIXELS_TO_SEPARATE / 2f,
                                w + HOW_MANY_PIXELS_TO_SEPARATE / 2f,
                                HOW_MANY_PIXELS_TO_SEPARATE);
                }

                batch.setColor(Color.WHITE);
            }


            if (glyph.id != ' ') {
                int x0_gt = MathUtils.clamp(MathUtils.round(x), 0, MyGdxGame.RESOLUTION);
                int y0_gt = MathUtils.clamp(MathUtils.round(MyGdxGame.RESOLUTION - y - height), 0, MyGdxGame.RESOLUTION);
                int x1_gt = MathUtils.clamp(MathUtils.round(x + w), 0, MyGdxGame.RESOLUTION);
                int y1_gt = MathUtils.clamp(MyGdxGame.RESOLUTION - MathUtils.round(y), 0, MyGdxGame.RESOLUTION);

                /*Gdx.app.log(TAG, (char) glyph.id + " ->" +
                        " x0 =" + '\t' + x0_gt +
                        ", y0 =" + '\t' + y0_gt +
                        ", x1 =" + '\t' + x1_gt +
                        ", y1 =" + '\t' + y1_gt);*/

                if (x < MyGdxGame.RESOLUTION & x+w > 0 & y < MyGdxGame.RESOLUTION & y+height > 0) {
                    groundTruthTxt.append("char" +
                            /*'\t' + (char) glyph.id +*/
                            '\t' + x0_gt +
                            '\t' + y0_gt +
                            '\t' + x1_gt +
                            '\t' + y1_gt +
                            '\n');

                    min_x0_line_gt = x0_gt < min_x0_line_gt ? x0_gt : min_x0_line_gt;
                    min_y0_line_gt = y0_gt < min_y0_line_gt ? y0_gt : min_y0_line_gt;
                    max_x1_line_gt = x1_gt > max_x1_line_gt ? x1_gt : max_x1_line_gt;
                    max_y1_line_gt = y1_gt > max_y1_line_gt ? y1_gt : max_y1_line_gt;
                }

                min_x0_word_gt = x0_gt < min_x0_word_gt ? x0_gt : min_x0_word_gt;
                min_y0_word_gt = y0_gt < min_y0_word_gt ? y0_gt : min_y0_word_gt;
                max_x1_word_gt = x1_gt > max_x1_word_gt ? x1_gt : max_x1_word_gt;
                max_y1_word_gt = y1_gt > max_y1_word_gt ? y1_gt : max_y1_word_gt;

            }

            if (glyph.id == ' ' | i == glyphs.size-1) {
                if (min_x0_word_gt < MyGdxGame.RESOLUTION & min_y0_word_gt != max_y1_word_gt & min_x0_word_gt != max_x1_word_gt) {
                    groundTruthTxt.append("word" +
                            "\t" + min_x0_word_gt +
                            '\t' + min_y0_word_gt +
                            '\t' + max_x1_word_gt +
                            '\t' + max_y1_word_gt +
                            '\n');
                }

                min_x0_word_gt = MyGdxGame.RESOLUTION;
                min_y0_word_gt = MyGdxGame.RESOLUTION;
                max_x1_word_gt = 0;
                max_y1_word_gt = 0;
            }


            prevX = x;
            prevY = y;
            prevW = w;
            prevH = height;

            cursorX += xadvance;
            i++;
        }

        if (!(min_x0_line_gt == MyGdxGame.RESOLUTION & min_y0_line_gt == MyGdxGame.RESOLUTION & max_x1_line_gt == 0 & max_y1_line_gt == 0))
            groundTruthTxt.append("line" +
                    "\t" + min_x0_line_gt +
                    '\t' + min_y0_line_gt +
                    '\t' + max_x1_line_gt +
                    '\t' + max_y1_line_gt +
                    '\n');

        //Gdx.app.log(TAG, "______________________________________________________________________________________________");
    }

    public String getCharSequence() {
        return charSequence;
    }

    /**
     *
     * @param charSequence
     * @param updateWidth When false, the new {@code charSequence} will fit the current width. when true, the width will be changed according to the new {@code charSequence}.
     */
    public void setCharSequence(String charSequence, boolean updateWidth) {
        if (charSequence.contains("\n"))
            //Gdx.app.log(TAG, "All the characters will be written in one line. (No multiline)");
            throw new UnsupportedOperationException("Multiline isn't supported.");

        //Gdx.app.log(TAG, charSequence);

        float prevHeight = getHeight();

        this.charSequence = charSequence/*.replaceAll("\", ")*/;
        glyphs.clear();

        if (charSequence.isEmpty()) {
            return;
        }

        float prevCharSequenceWidthPixels = charSequenceWidthPixelUnits;
        charSequenceWidthPixelUnits = 0;

        BitmapFont.Glyph firstCharInSeq = myBitmapFont.data.getGlyph(charSequence.charAt(0));

        yoffsetPlusHeightMax = (firstCharInSeq.yoffset) + firstCharInSeq.height;
        int yoffsetPlusHeightMaxID = firstCharInSeq.id;
        int yoffsetMin = (firstCharInSeq.yoffset);

        char c;
        for (int i = 0, length = charSequence.length(); i < length; i++) {
            c = charSequence.charAt(i);

            BitmapFont.Glyph glyph = myBitmapFont.getData().getGlyph(c);
            if (glyph == null) continue;

            glyphs.add(glyph);



            // charSequenceWidthPixelUnits.
            if (i == 0 & length == 1)
                charSequenceWidthPixelUnits -= glyph.xoffset;
            else if (i < length - 1)
                charSequenceWidthPixelUnits += glyph.xadvance;
            if (i == length-1)
                charSequenceWidthPixelUnits += glyph.xoffset + glyph.width;


            //Gdx.app.log(TAG, c + ", id = " + glyph.id + ", yoffset = " + (glyph.yoffset));


            // heightPixelUnits parameters.
            int yoffsetPlusHeight = (glyph.yoffset) + glyph.height;
            if (yoffsetPlusHeight > yoffsetPlusHeightMax) {
                yoffsetPlusHeightMax = yoffsetPlusHeight;
                yoffsetPlusHeightMaxID = glyph.id;
            }
            if ((glyph.yoffset) < yoffsetMin)
                yoffsetMin = (glyph.yoffset);
        }

        // heightPixelUnits.
        int base = myBitmapFont.data.base;
        if (yoffsetPlusHeightMax < base) yoffsetPlusHeightMax = base;
        if (yoffsetMin > base) yoffsetMin = base;
        heightPixelUnits = yoffsetPlusHeightMax - yoffsetMin;

        if (updateWidth) {
            if (prevCharSequenceWidthPixels != 0)
                setWidth((float) charSequenceWidthPixelUnits / prevCharSequenceWidthPixels * getWidth());
            else if (prevHeight != 0)
                setWidth(getWidthAccordingToTheRatio(prevHeight));
        }
    }

    @Override
    public void setHeight(float height) {
        if (aspectRatioLocked)
            setWidth(getWidthAccordingToTheRatio(height));
        super.setHeight(height);
    }

    @Override
    public float getHeight() {
        if (charSequence == null) return 0;
        if (charSequence.isEmpty() | !aspectRatioLocked)
            return super.getHeight();
        return getHeightAccordingToTheRatio(getWidth());
    }

    private float getHeightAccordingToTheRatio(float width) {
        if (charSequenceWidthPixelUnits == 0) return 0;
        return (float) heightPixelUnits / charSequenceWidthPixelUnits * width;
    }

    private float getWidthAccordingToTheRatio(float height) {
        if (heightPixelUnits == 0) return 0;
        return (float) charSequenceWidthPixelUnits / heightPixelUnits * height;
    }

    public boolean isAspectRatioLocked() {
        return aspectRatioLocked;
    }

    public void lockAspectRatio() {
        if (!this.aspectRatioLocked)
            setWidth(getWidthAccordingToTheRatio(getHeight()));

        this.aspectRatioLocked = true;
    }

    public void unlockAspectRatio(float width, float height) {
        this.aspectRatioLocked = false;
        setSize(width, height);
    }

    @Deprecated
    @Override
    public void setRotation(float degrees) {
        throw new UnsupportedOperationException("Rotation isn't supported yet.");
    }

    /**
     * @exception RuntimeException is thrown when trying to use this function while {@code aspectRatioLocked == true}
     * @param width
     * @param height
     */
    @Override
    public void setSize(float width, float height) {
        if (aspectRatioLocked)
            throw new RuntimeException(
                    "You can't change width and height at the same time when aspectRatioLocked is true. " +
                            "You can change one at a time and the other will be change automatically."
            );

        super.setSize(width, height);
        /*_getHeight = height;
        _getWidth = width;*/
    }

    /**
     * @exception RuntimeException is thrown when trying to use this function while {@code aspectRatioLocked == true}
     * @param size
     */
    @Override
    public void sizeBy(float size) {
        if (aspectRatioLocked)
            throw new RuntimeException(
                    "You can't change width and height at the same time. You can change one at a time and the other will be change automatically."
            );

        super.sizeBy(size);
    }

    /**
     * @exception RuntimeException is thrown when trying to use this function while {@code aspectRatioLocked == true}
     * @param width
     * @param height
     */
    @Override
    public void sizeBy(float width, float height) {
        if (aspectRatioLocked)

            throw new RuntimeException(
                    "You can't change width and height at the same time. You can change one at a time and the other will be change automatically."
            );

        super.sizeBy(width, height);
    }

    /**
     * @exception RuntimeException is thrown when trying to use this function while {@code aspectRatioLocked == true}
     * @param x
     * @param y
     * @param width
     * @param height
     */
    @Override
    public void setBounds(float x, float y, float width, float height) {
        if (aspectRatioLocked)
            throw new RuntimeException(
                    "You can't change width and height at the same time when aspectRatioLocked is true." +
                            "You can change one at a time and the other will be change automatically. " +
                            "Use setBoundsWidth(x, y, width) or setBoundsHeight(x, y, height) instead."
            );

        super.setBounds(x, y, width, height);
    }

    public void setBoundsWidth(float x, float y, float width) {
        if (aspectRatioLocked) {
            float height = getHeightAccordingToTheRatio(width);
            super.setBounds(x, y, width, height);
        } else
            super.setBounds(x, y, width, getHeight());
    }

    public void setBoundsHeight(float x, float y, float height) {
        if (aspectRatioLocked) {
            float width = getWidthAccordingToTheRatio(height);
            super.setBounds(x, y, width, height);
        } else
            super.setBounds(x, y, getWidth(), height);
    }
}
