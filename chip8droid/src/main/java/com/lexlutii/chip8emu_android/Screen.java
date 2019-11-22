package com.lexlutii.chip8emu_android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ismael Rodríguez, ismaro3
 * JPanel that manages screen.
 */

public class Screen extends View {
    private int width;
    private int height;
    private Memory memory;
    private List<VirtualKeyboard> keyboards;
    private TouchHolder touchHolder;
    public static final int[] PALETTE_1 = new int[]{0xFF334422, 0xFFDDFFBB};
    public static final int[] PALETTE_2 = new int[]{0xFF557744, 0xFFFFFF00};


    public Screen(Context context) {
        super(context);
        touchHolder=new TouchHolder();
        keyboards = new ArrayList<>();
    }

    public void setChip8Screen(Memory memory) {
        this.memory = memory;
        this.setOnTouchListener(touchHolder);
    }

    public void addKeyboard(VirtualKeyboard keyboard){
        touchHolder.addListener(keyboard);
        keyboards.add(keyboard);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(0xFF559955);
        width = this.getWidth();
        height = this.getHeight();
        if (memory == null)
            return;

        float chip8ScreenHeight = DrawChip8Screen(canvas, PALETTE_1);
        RectF[]keyboardRects = getKeyboardRects(height-chip8ScreenHeight);

        DrawKeyboards(canvas, keyboardRects, PALETTE_1, PALETTE_2);
    }


    private float DrawChip8Screen(Canvas canvas, int[] palette) {
        int scale = width / 64;
        int padding = (width - scale * 64) / 2;
        Paint paint = new Paint();
        paint.setColor(palette[0]);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(scale / 4);
        Rect field = new Rect(0, 0, 64 * scale, 32 * scale);
        field.offset(padding, padding);
        canvas.drawRect(field, paint);

        paint.setColor(palette[1]);

        Rect gamePoint = new Rect(0, 0, scale, scale);
        gamePoint.offset(padding, padding);
        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 64; x++) {
                boolean value = memory.pixels[x][y];
                if (value) {
                    canvas.drawRect(gamePoint, paint);
                }
                gamePoint.offset(scale, 0);
            }
            gamePoint.offset(-64 * scale, scale);
        }
        return scale*32+2*padding;
    }

    private void DrawKeyboards(Canvas canvas, RectF[] keyboardRects, int[] paletteUp, int[] paletteDown) {

        for (int i=0;i<keyboards.size(); i++) {
            VirtualKeyboard keyboard = keyboards.get(i);
            keyboard.setKeyboardRect(keyboardRects[i]);
            Paint paint = new Paint();
            paint.setStrokeWidth(8);
            paint.setTextAlign(Paint.Align.CENTER);
            float textSize = 90;
            paint.setTextSize(textSize);

            List<String> keyNames = keyboard.getKeyNames();
            for (int j = 0; j < keyboard.keyNames.size(); j++) {
                int[] palette = keyboard.getKeyPressed(keyNames.get(j)) ? paletteDown : paletteUp;
                RectF buttonRect = keyboard.getKeyRect(keyNames.get(j));
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(palette[1]);
                canvas.drawRoundRect(buttonRect, 20, 20, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(palette[0]);
                canvas.drawRoundRect(buttonRect, 20, 20, paint);
                canvas.drawText(keyNames.get(j), buttonRect.centerX(), buttonRect.centerY()+textSize/3, paint);
            }
        }
    }

    private void DrawButton(Canvas canvas, RectF buttonRect, int[] palette, Paint paint){

    }

    RectF[] getKeyboardRects(float verticalSpace){
        // TODO delete repits
        RectF[] result = new RectF[keyboards.size()];

        float prevBottom = height-verticalSpace;

        float emuControlHeight = verticalSpace/3;
        float chip8KeyboardHeight = verticalSpace-emuControlHeight;

        result[0]=new RectF();
        result[0].top=prevBottom;
        result[0].bottom=result[0].top+emuControlHeight;
        result[0].left=0;
        result[0].right=width;

        prevBottom = result[0].bottom;
        result[1]=new RectF();
        result[1].top=prevBottom;
        result[1].bottom=result[1].top+chip8KeyboardHeight;
        result[1].left=0;
        result[1].right=width;

        return result;
    }
}