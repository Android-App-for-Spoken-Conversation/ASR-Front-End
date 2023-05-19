package com.example.voicerecorder;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

public class TextAnimationLooper {
    private TextView textView;
    private Handler handler;
    private String text;
    private String animatedText;
    private boolean isAnimating;

    public TextAnimationLooper(TextView textView,String text) {
        this.textView = textView;
        this.text = text;
        this.animatedText = "";
        handler = new Handler(Looper.getMainLooper());
        isAnimating = false;
    }

    public void startAnimation() {
        if (isAnimating) {
            return; // Animation is already running
        }

        isAnimating = true;
        handler.post(animationRunnable);
    }

    public void setText(String text,String animatedText){
        this.text = text;
        this.animatedText = text;
    }

    public void setText(String text){
        this.text = text;
    }

    public void stopAnimation() {
        isAnimating = false;
        handler.removeCallbacks(animationRunnable);
    }

    private Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            if(animatedText.equals("...")) animatedText = "";
            else{
                animatedText+=".";
            }
            textView.setText(text+animatedText);
            if (isAnimating) {
                handler.postDelayed(this, 1000); // Repeat after 1 second
            }
        }
    };
}
