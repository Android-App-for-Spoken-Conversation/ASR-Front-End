package com.example.voicerecorder;
import static android.content.ContentValues.TAG;

import android.Manifest;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import android.app.DownloadManager;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.TextView;
import android.widget.*;
import android.widget.Toast;

import com.example.voicerecorder.R;


import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import okhttp3.*;

public class MainActivity extends AppCompatActivity {
    MediaRecorder mediaRecorder;   // for taking the instance of the media recorder
    MediaPlayer  mediaPlayer;    // for taking the instance of the media player


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private static int MICROPHONE_PERMISSION_CODE=200; // when the user responds to the permission request,
    // the app can identify which permission request the user is responding to based on the request code.

    private TextAnimationLooper textAnimationLooper;

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
           TextView status = findViewById(R.id.status);
           Button stopButton = findViewById(R.id.stopButton);
           Button recButton = findViewById(R.id.recButton);
           Button playButton = findViewById(R.id.playButton);
           playButton.setEnabled(false);
           status.setVisibility(View.VISIBLE);
           textAnimationLooper = new TextAnimationLooper(status,"Recording Started");
           textAnimationLooper.startAnimation();
           recButton.setVisibility(View.GONE);
           stopButton.setVisibility(View.VISIBLE);
//           Toast.makeText(this, "Recording Started", Toast.LENGTH_SHORT).show();
       }
       catch(Exception e){
           e.printStackTrace();
       }
    }

    public void sendPostRequest(String url, JSONObject requestBody,Context context) {
        // Initialize OkHttp client
        OkHttpClient client = new OkHttpClient();
        // Create the request body
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, requestBody.toString());
    
        // Create the request
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        // Send the request asynchronously
        client.newCall(request).enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                    TextView output = findViewById(R.id.Output);
                    output.setText(e.getMessage());
                    TextView status = findViewById(R.id.status);
                    status.setVisibility(View.VISIBLE);
                    textAnimationLooper.stopAnimation();
                    status.setText("Please start recording");
                });
            }
    
            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                // Handle success
                Log.d(TAG, response.toString());
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    // Process the response body
                    runOnUiThread(() -> {
                        // Update UI with the response
                        // Toast.makeText(context,responseBody,Toast.LENGTH_LONG).show();
                        TextView Outbox = findViewById(R.id.Output);
                        TextView status = findViewById(R.id.status);
                        status.setVisibility(View.VISIBLE);
                        textAnimationLooper.stopAnimation();
                        status.setText("Please start Recording");
                        Outbox.setText(responseBody);
                    });
                }else{
                    runOnUiThread(() -> {
                        // Update UI with the response
                        Toast.makeText(context,"not successful",Toast.LENGTH_LONG).show();
                    });
                }
            }
        
        });
    }
    
    public void btnStopPressed(View v) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        Button playButton = findViewById(R.id.playButton);
        playButton.setEnabled(true);
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
//        Toast.makeText(this, "Recording Stopped", Toast.LENGTH_SHORT).show();
        Button recButton = findViewById(R.id.recButton);
        Button stopButton = findViewById(R.id.stopButton);
        recButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.GONE);
        TextView status = findViewById(R.id.status);
        status.setVisibility(View.VISIBLE);
        textAnimationLooper.stopAnimation();
        textAnimationLooper.setText("Generating Text");
        textAnimationLooper.startAnimation();

        // Upload the recorded file to Firebase Storage and get its download URL
        String filePath = getRecordingFilePath();
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists()) {
                Uri fileUri = Uri.fromFile(file);
                StorageReference recordingRef = storageRef.child("recordings/" + file.getName());
                recordingRef.putFile(fileUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            recordingRef.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        String downloadUrl = uri.toString();
//                                        Toast.makeText(this, "Download URL: " + downloadUrl, Toast.LENGTH_LONG).show();
                                        String jsonBody = "{\"downloadUrl\": \"" + downloadUrl + "\"}";
                                        JSONObject requestBody = new JSONObject();
                                        try {
                                            requestBody.put("fileUrl", downloadUrl);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        sendPostRequest("http://10.0.8.184:8888/speech",requestBody,this);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to upload recording", Toast.LENGTH_SHORT).show();
                        });
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



    public void btnPlayPressed(View v){
        try{
            mediaPlayer=new MediaPlayer();
            mediaPlayer.setDataSource(getRecordingFilePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            Button pauseButton = findViewById(R.id.pauseButton);
            Button playButton = findViewById(R.id.playButton);
            playButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Recording is Playing", Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void btnPausePressed(View V){
        Button playButton = findViewById(R.id.playButton);
        Button pauseButton = findViewById(R.id.pauseButton);
        playButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.GONE);
        mediaPlayer.pause();
//        Toast.makeText(this,"insie pause",Toast.LENGTH_SHORT).show();
    }


    //version 2022.1.1.21
}