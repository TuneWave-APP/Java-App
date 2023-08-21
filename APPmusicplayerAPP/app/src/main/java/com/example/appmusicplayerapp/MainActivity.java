package com.example.appmusicplayerapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.content.Intent;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView logo = findViewById(R.id.twlogo);
        Animation logofadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        logo.startAnimation(logofadein);

        ImageButton StartButton = findViewById(R.id.start_button);
        Animation butslideinup = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slideinup);
        StartButton.startAnimation(butslideinup);
        StartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startmusicplayer();
            }
    });
    }
    private void startmusicplayer() {
        Intent intent = new Intent(this, musicplayer.class);
        startActivity(intent);
    }
}