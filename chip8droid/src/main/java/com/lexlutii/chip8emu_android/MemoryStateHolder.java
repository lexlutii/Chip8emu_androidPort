package com.lexlutii.chip8emu_android;

import java.io.Serializable;

class MemoryStateHolder implements Serializable {//do not change
    byte[] memory; //4KB of memory
    short[] stack; //Stack, 16 16-bit values
    //Screen array. False = black. True = white.
    boolean[][] pixels;
    //Set to true when a sprite has been set to be drawn.
    boolean drawFlag;

    void copyTo(MemoryStateHolder copy){
        copy.memory=this.memory.clone();
        copy.drawFlag=this.drawFlag;
        copy.pixels=this.pixels.clone();
        copy.stack=this.stack.clone();
    }
}
