package com.lexlutii.chip8emu_android;

import android.content.Context;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author Ismael Rodríguez, ismaro3
 * This class implements the whole Chip8 System.
 * Usage: Create a new Chip8 object, call loadGame to load a game, and then call startEmulationLoop.
 */
public class Chip8 extends Thread {



    //Execution parameters
    private int cpuFreqHz;
    private long periodNanos;       //Time for each cycle
    private int cyclesForRefreshing; //Cycles to refresh screen (60 times a second)

    //Components
    private Memory memory;
    private RegisterBank registerBank;
    private ControlUnit controlUnit;

    private Screen screen;
    private Chip8Keyboard keyboard;
    private byte[] rom = null;
    //private Sound sound;
    private Context context;

    private boolean stopEmulation = false;

    public void setScreen(Screen screen){
        this.screen=screen;
        screen.setChip8Screen(memory);
        screen.addKeyboard(keyboard);
    }

    /**
     * Constructor. Initializes the system, running at "cpuFreqHz" cycles per second.
     */
    public Chip8(int cpuFreqHz)
    {
        this.cpuFreqHz = cpuFreqHz;
        this.periodNanos = 1000000000/ cpuFreqHz;
        this.cyclesForRefreshing = cpuFreqHz /60;
        initialize();
    }


    /**
     *Creates all the components of the system and prepares the GUI.
     */
    private void initialize(){
        memory = new Memory();
        registerBank = new RegisterBank();
        keyboard = new Chip8Keyboard();
        controlUnit = new ControlUnit(registerBank,memory,keyboard);
        //sound = new Sound(true);
        System.out.println("[INFO] Chip-8 system initialized.");
    }

    private void setStartState(){
        memory.setStartState();
        registerBank.setStartState();
    }

/**
     * Loads a game with name "name" located in roms folder.
     * Puts all its bytes into memory, starting from position 0x200.
     * @throws IOException if an error happens.}
 *
 *
 */
    public StateObject SaveState(){
        boolean wasAlife = this.isAlive();
        StopEmulation();
        StateObject stateObject = new StateObject(memory, registerBank);
        if(wasAlife)
            this.start();
        return stateObject;
    }

    public void LoadState(StateObject state){
        StopEmulation();
        state.memState.copyTo(this.memory);
        ((StateObject)state).regState.copyTo(this.registerBank);
        this.start();
    }

    public void restart(){
        if (rom!=null){
            loadGame();
            this.start();
        }
    }

    void LoadGame(Object bytes) {
        rom=(byte[]) bytes;
        loadGame();
    }

    private void loadGame() {
        StopEmulation();
        this.setStartState();
        short currentAddress = (short) 0x200;
        int loadedBytes = 0;
        for (byte b : rom) {
            memory.set(currentAddress, b);
            loadedBytes++;
            currentAddress = (short) (currentAddress + 0x1);

        }
    }

    /**
     * Main emulation loop. Infinite loop where fetch, incrementPC, decode and execute phases are executed on every
     * iteration. Also, 60 times a second, the screen is refreshed, DT and ST are decremented and sound is activated/deactivated.
     * At the end of one iteration, the system waits the proper time to simulate the real speed of the system.
     */
    @Override
    public void run(){

        int emulatedCycles = 0; //Number of emulated cycles (gets reseted every "cpuFreqHz" cycles).

        //Number of emulated cycles (gets reseted every "cyclesForRefreshing" cycles).
        //Used for controlling refreshing rate of screen, DT and ST (must be 60HZ always).
        int refreshCycles = 0;

        //Variables used to measure time
        long passedTime = 0;
        long initTime;
        long endTime;


        while(true){


            initTime = System.nanoTime();

            //1.- Fetch (Load instruction from memory according to PC)
            controlUnit.fetch();

            //2.- Increment PC before executing, so if a JMP is done, it will be overriden.
            controlUnit.incrementPC();

            //3.- Decode instruction and execute it
            controlUnit.decodeAndExecute();


            //Actions done 60 times per second -> every cpuFreqHz/60 cycles
            if(refreshCycles%(cyclesForRefreshing)==0){

                refreshCycles=0;
                //4.- Update screen only every 1/60 seconds (Screen freq = 60Hz)
                if(memory.drawFlag){
                    screen.invalidate();
                    memory.drawFlag=false;
                }

                //5.- Decrement DT
                if(registerBank.DT > 0){
                    registerBank.DT = (byte)(registerBank.DT - 0x01);
                }

                //6.- Decrement ST. If previously on silence -> new sound. If now is 0 -> stop sound
                if(registerBank.ST > 0){
                    //sound.startSound();
                    registerBank.ST = (byte)(registerBank.ST - 0x01);
                    if(registerBank.ST == 0){
                        //sound.stopSound();
                    }
                }
            }

            endTime = System.nanoTime();
            refreshCycles++;


            waitForCompleteCycle(endTime,initTime); //Wait time to simulate real speed

            /** Print ms rate */
            endTime = System.nanoTime();
            emulatedCycles++;
            passedTime += (endTime - initTime);
            if(emulatedCycles== cpuFreqHz){
                System.out.println("Time to emulate " + cpuFreqHz + " Hz: " + passedTime/1000000.0 + " ms");
                emulatedCycles=0;
                passedTime=0;
            }
            if(this.stopEmulation==true)
                this.stopEmulation=false;
                break;
        }
    }

    /**
     * Given the initTime and endTime of the current cycle, it waits
     * until the "periodNanos" time passed. Calling it at the end of a cycle
     * makes it last like a cycle in the real machine.
     * It is a while loop with sleep(0) to get more accuracy than only sleep, and to prevent
     * using CPU too much time.
     */

    private void StopEmulation(){
        if(this.isAlive())
            stopEmulation=true;
        while (this.isAlive()){
            try {
                sleep(1);
            }
            catch (InterruptedException e){}
        }
    }

    private void waitForCompleteCycle(long endTime, long initTime){

        long nanosToWait= periodNanos - (endTime - initTime);
        long initNanos = System.nanoTime();
        long targetNanos = initNanos + nanosToWait;
        while(System.nanoTime()<targetNanos){
            try {
                sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

final class StateObject implements Serializable {
    StateObject(Memory memory, RegisterBank bank){
        memState = memory.getState();
        regState = bank.getState();
    }
    MemoryStateHolder memState;
    RegisterStateHolder regState;
}
