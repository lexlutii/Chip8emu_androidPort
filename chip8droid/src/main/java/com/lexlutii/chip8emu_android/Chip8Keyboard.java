package com.lexlutii.chip8emu_android;

import android.graphics.PointF;
import android.graphics.RectF;

import java.util.Arrays;
import java.util.List;

public class Chip8Keyboard extends VirtualKeyboard{

    public boolean[] pressed = new boolean[16];
    public int numberOfPressedKeys = 0;
    byte lastPressed;

    final static String[] KEY_NAMES = {"1","2","3","C","4","5","6","D","7","8","9","E","A","0","B","F"};

    Chip8Keyboard(){
        keyNames = Arrays.asList(KEY_NAMES);
        keyRects = new RectF[16];
    }

    @Override
    protected void generateKeyboard(){
        //TODO refactor => toBaseClass
        float buttonW = keyboardRect.width() / 5;
        float offsetX = (keyboardRect.width() - buttonW * 4) / 5;
        float buttonH = keyboardRect.height() / 5f;
        float offsetH = (keyboardRect.height() - buttonH * 4) / 5;

        RectF buttonRect = new RectF(0, 0, buttonW, buttonH);
        buttonRect.offset(offsetX, keyboardRect.top + offsetX);
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 4; i++) {
                keyRects[i + 4 * j] = new RectF(buttonRect);
                buttonRect.offset(buttonW + offsetX, 0);
            }
            buttonRect.offset(-4 * (buttonW + offsetX), buttonH + offsetH);
        }
    }

    StringBuilder sb = new StringBuilder();
    public String touchState ="";

    protected void ExecuteKeyStatus(String keyName, boolean currentPressed) {
        if(currentPressed!= getKeyPressed(keyName))
        {
            if(currentPressed){
                numberOfPressedKeys++;
                setKey(true, keyName);
            }
            else {
                numberOfPressedKeys--;
                setKey(false, keyName);
            }
        }
    }

    public byte waitForKey(){
        while(numberOfPressedKeys==0){
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return lastPressed;
    }

    private boolean setKey(boolean value, @org.jetbrains.annotations.NotNull String keycode){

        switch(keycode){
            case "1":
                pressed[0x1] = value;
                lastPressed=0x1;
                break;
            case "2":
                pressed[0x2] = value;
                lastPressed=0x2;
                break;
            case "3":
                pressed[0x3] = value;
                lastPressed=0x3;
                break;
            case "C":
                pressed[0xC] = value;
                lastPressed=0xC;
                break;
            case "4":
                pressed[0x4] = value;
                lastPressed=0x4;
                break;
            case "5":
                pressed[0x5] = value;
                lastPressed=0x5;
                break;
            case "6":
                pressed[0x6] = value;
                lastPressed=0x6;
                break;
            case "D":
                pressed[0xD] = value;
                lastPressed=0xD;
                break;
            case "7":
                pressed[0x7] = value;
                lastPressed=0x7;
                break;
            case "8":
                pressed[0x8] = value;
                lastPressed=0x8;
                break;
            case "9":
                pressed[0x9] = value;
                lastPressed=0x9;
                break;
            case "E":
                pressed[0xE] = value;
                lastPressed=0xE;
                break;
            case "A":
                pressed[0xA] = value;
                lastPressed=0xA;
                break;
            case "0":
                pressed[0x0] = value;
                lastPressed=0x0;
                break;
            case "B":
                pressed[0xB] = value;
                lastPressed=0xB;
                break;
            case "F":
                pressed[0xF] = value;
                lastPressed=0xF;
                break;
            default:
                return false;

        }
        return true;

    }

    public boolean getKeyPressed(String keycode){
        switch(keycode){
            case "1":
                return pressed[0x1];
            case "2":
                return pressed[0x2];
            case "3":
                return pressed[0x3];
            case "C":
                return pressed[0xC];
            case "4":
                return pressed[0x4];
            case "5":
                return pressed[0x5];
            case "6":
                return pressed[0x6];
            case "D":
                return pressed[0xD];
            case "7":
                return pressed[0x7];
            case "8":
                return pressed[0x8];
            case "9":
                return pressed[0x9];
            case "E":
                return pressed[0xE];
            case "A":
                return pressed[0xA];
            case "0":
                return pressed[0x0];
            case "B":
                return pressed[0xB];
            case "F":
                return pressed[0xF];
            default:
                return false;
        }
    }

    /*public String getState(){
        int[] key = {0x1, 0x2, 0x3, 0xC, 0x4, 0x5, 0x6, 0xD,0x7, 0x8, 0x9, 0xE,0xA, 0x0, 0xB, 0xF};
        String[] codes =  {"0x1", "0x2", "0x3", "0xC", "0x4", "0x5", "0x6", "0xD","0x7", "0x8", "0x9", "0xE","0xA", "0x0", "0xB", "0xF"};
        StringBuilder result=new StringBuilder();
        for(int i=0; i< key.length; i++){
            result.append(codes[i]+"="+(pressed[key[i]]?"1, ":"0, "));
            if(i==7)
                result.append("\n");
        }
        return result.toString();
    }*/
}

abstract class VirtualKeyboard  implements TouchHolder.OnActiveTouchListener {

    public List<String> keyNames=null;
    RectF[] keyRects;


    final public List<String> getKeyNames(){
        return keyNames;
    }

    final public RectF getKeyRect(String keyName){
        for(int i=0;i<keyNames.size();i++) {
            if (keyNames.get(i) == keyName)
                return keyRects[i];
        }
        return null;
    }

    @Override
    final public void onActiveTouch(List<PointF> fingers) {
        for (int i = 0; i < keyNames.size(); i++) {
            boolean currentPressed = false;
            for (int j = 0; j < fingers.size(); j++) {
                PointF current = fingers.get(j);
                if (keyRects[i].contains(current.x, current.y))
                    currentPressed = true;
            }
            ExecuteKeyStatus(keyNames.get(i), currentPressed);
        }
    }

    protected RectF keyboardRect;

    public void setKeyboardRect(RectF keyboardRect){
        if(!keyboardRect.equals(this.keyboardRect)){
            this.keyboardRect = keyboardRect;
            generateKeyboard();
        }
    }

    abstract protected void generateKeyboard();

    abstract void ExecuteKeyStatus(String keyName, boolean currentPressed);
    abstract boolean getKeyPressed(String keycode);
}