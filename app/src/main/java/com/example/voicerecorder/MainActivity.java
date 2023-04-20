package com.example.voicerecorder;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



import android.app.DownloadManager;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.TextView;
import android.widget.Toast;
//import android.Manifest.permission;


import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    //enough
    public void btnStopPressed(View v){
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder=null;
        Toast.makeText(this, "Recording Stopped", Toast.LENGTH_SHORT).show();

        // Upload the recorded file to the server
        String filePath = getRecordingFilePath();
        if(filePath != null){
            File file = new File(filePath);
            if(file.exists()){
                OkHttpClient okHttpClient=new OkHttpClient();
                RequestBody formbody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", "recording.wav",
                                RequestBody.create(MediaType.parse("audio/wav"), file))
                        .build();

                try {
                    Request request = new Request.Builder()
                            .url("http://:8888")
                            .post(formbody)
                            .build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            final TextView textView=findViewById(androidx.core.R.id.text); //textview

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        textView.setText(response.body().string());

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
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
    public String getRecordingFilePath(){   //here we are given the path to the file where it will be stored
        ContextWrapper contextWrapper= new ContextWrapper(getApplicationContext());
        File musicDirectory=contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file=new File(musicDirectory,"testRecordingFile"+".wav");
//        Toast.makeText(contextWrapper, "file.getPath()", Toast.LENGTH_SHORT).show();
        Log.i("file path",file.getPath());
        return file.getPath();
    }

//    String filePath = getRecordingFilePath();
//    File file = new File(filePath);
//    if(getRecordingFilePath()!=null{}){
//        OkHttpClient okHttpClient=new OkHttpClient();
//        RequestBody formbody = new FormBody.Builder().add("file",file).build();
//
//        try {
//            Request request = new Request.Builder().url("/").build();
//            okHttpClient.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
//                        }
//                    });
//
//                }
//
//                @Override
//                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                    final TextView textView=findViewById(androidx.core.R.id.text); //textview
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                textView.setText(response.body().string());
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    });
//
//                }
//            });
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//    }

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

    //version 2022.1.1.21
}