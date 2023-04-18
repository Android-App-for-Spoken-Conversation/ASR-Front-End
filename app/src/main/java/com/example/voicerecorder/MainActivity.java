package com.example.voicerecorder;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
//import android.Manifest.permission;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static int MICROPHONE_PERMISSION_CODE=200; // when the user responds to the permission request,
    // the app can identify which permission request the user is responding to based on the request code.
    MediaRecorder mediaRecorder;   // for taking the instance of the media recorder
    MediaPlayer  mediaPlayer;    // for taking the instance of the media player
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(isMicrophonePresent()){
            getMicrophonePermission();
        }
    }
    public void btnRecordPressed(View v){
       try{
           mediaRecorder=new MediaRecorder();
           mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
           mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
           mediaRecorder.setOutputFile(getRecordingFilePath());
           mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
           mediaRecorder.prepare();
           mediaRecorder.start();
           Toast.makeText(this, "Recording Started", Toast.LENGTH_SHORT).show();
       }
       catch(Exception e){
           e.printStackTrace();
       }
    }
    public void btnStopPressed(View v){
          mediaRecorder.stop();
          mediaRecorder.release();
          mediaRecorder=null;
        Toast.makeText(this, "Recording Stopped", Toast.LENGTH_SHORT).show();
    }
    public void btnPlayPressed(View v){
         try{
             mediaPlayer=new MediaPlayer();
             mediaPlayer.setDataSource(getRecordingFilePath());
             mediaPlayer.prepare();
             mediaPlayer.start();
             Toast.makeText(this, "Recording is Playing", Toast.LENGTH_SHORT).show();
         }
         catch(Exception e){
             e.printStackTrace();
        }
    }
    private boolean isMicrophonePresent(){
        if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){
            return true;
        }  // this function is used to check whether microphone is present or not.
        else{
            Toast.makeText(this, "microphone is not present", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    private void getMicrophonePermission(){    //this function is used to taken the permission from the user
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)
        ==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.RECORD_AUDIO},MICROPHONE_PERMISSION_CODE);
        }
    }
    private String getRecordingFilePath(){   //here we are given the path to the file where it will be stored
        ContextWrapper contextWrapper= new ContextWrapper(getApplicationContext());
        File musicDirectory=contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file=new File(musicDirectory,"testRecordingFile"+".mp3");
//        Toast.makeText(contextWrapper, "file.getPath()", Toast.LENGTH_SHORT).show();
        Log.i("file path",file.getPath());
        return file.getPath();
    }

    //version 2022.1.1.21
}