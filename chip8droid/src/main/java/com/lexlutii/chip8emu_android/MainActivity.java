package com.lexlutii.chip8emu_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;



public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "myLogs";

    final String DIR_SD = "roms";
    final String FILENAME = "zero";
    public final static String START_DIR = "/roms";
    String romName="";
    Chip8 chip8;

        final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 157;
        final int REQUEST_CODE_INTERNET = 223;


    class OnSelectedRomListener implements OpenFileDialog.OpenDialogListener{
        @Override
        public void OnSelectedFile(String fileName) {

            TextView textView = (TextView) findViewById(R.id.textView);
            // проверяем доступность SD
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
                return;
            }
            romName=fileName;
            File sdFile = new File(fileName);

            int size = (int) sdFile.length();
            byte[] bytes = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(sdFile));
                buf.read(bytes, 0, bytes.length);
                buf.close();
            }  catch (IOException e) {
                e.printStackTrace();
            }
            chip8.LoadGame(bytes);
            chip8.start();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        Screen chip8_screen = new Screen(this);
        setContentView(chip8_screen);
        EmuControlKeyboard controlKeyboard = new EmuControlKeyboard();
        chip8_screen.addKeyboard(controlKeyboard);

        chip8 = new Chip8(500);
        chip8.setScreen(chip8_screen);
        
        final OpenFileDialog fileDialog = new OpenFileDialog(this);
        fileDialog.setOpenDialogListener(new OnSelectedRomListener());
        
        controlKeyboard.AddKeyHandler("new game", new Runnable() {
            @Override
            public void run() {
                fileDialog.show();
            }
        });
        controlKeyboard.AddKeyHandler("restart", new Runnable() {
            @Override
            public void run() {
                chip8.restart();
            }
        });
        controlKeyboard.AddKeyHandler("save state", new Runnable() {
            @Override
            public void run() {
                SaveStateToFile();
            }
        });
        controlKeyboard.AddKeyHandler("load state", new Runnable() {
            @Override
            public void run() {
                ReadStateFromFile();
            }
        });
    }

    private void ReadStateFromFile() {
        String saveStatePath = romName.concat(".svst");
        try {
            FileInputStream fileStream = new FileInputStream(saveStatePath);
            ObjectInputStream objectStream = new ObjectInputStream(fileStream);
            StateObject stateObject=(StateObject)objectStream.readObject();
            chip8.LoadState(stateObject);
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        catch (FileNotFoundException e){
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void SaveStateToFile() {
        StateObject stateObject = chip8.SaveState();
        String saveStatePath = romName.concat(".svst");
        try {
            FileOutputStream fileStream = new FileOutputStream(saveStatePath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileStream);
            objectOutputStream.writeObject(stateObject);
        }
        catch (FileNotFoundException e){

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private boolean checkPermissions() {
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_READ_EXTERNAL_STORAGE);
        }
        permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},
                    REQUEST_CODE_INTERNET);
        }
        return true;
    }

}
