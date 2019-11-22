package com.lexlutii.chip8emu_android;

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmuControlKeyboard extends VirtualKeyboard{

    Map<String, Runnable> keyHandlers;
    EmuControlKeyboard(){
        super();
        keyNames = new ArrayList<>();
        keyHandlers =new HashMap<String, Runnable>();
        keyRects=new RectF[4];
    }

    void AddKeyHandler(String key, Runnable runnable){
        keyNames.add(key);
        keyHandlers.put(key,runnable);
    }

    protected void ExecuteKeyStatus(String keyName, boolean status) {
        if (status)
            keyHandlers.get(keyName).run();
        return;
    }

    public boolean getKeyPressed(String key){
        return false;
    }

    @Override
    protected void generateKeyboard() {

        //TODO refactor => toBaseClass
        float buttonW = keyboardRect.width() / 2.5f;
        float offsetX = (keyboardRect.width() - buttonW * 2) / 3;
        float buttonH = keyboardRect.height() / 2.5f;
        float offsetH = (keyboardRect.height() - buttonH * 2) / 3;

        RectF buttonRect = new RectF(0, 0, buttonW, buttonH);

        buttonRect.offset(offsetX, keyboardRect.top + offsetH);
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 2; i++) {
                keyRects[i + 2 * j] = new RectF(buttonRect);
                buttonRect.offset(buttonW + offsetX, 0);
            }
            buttonRect.offset(-2 * (buttonW + offsetX), buttonH + offsetH);
        }
    }
}