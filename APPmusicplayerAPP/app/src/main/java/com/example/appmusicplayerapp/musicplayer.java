package com.example.appmusicplayerapp;

import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Handler;
import android.content.Intent;

public class musicplayer extends AppCompatActivity {
    MediaPlayer musicPlayer;
    SeekBar Duration;
    TextView Current;
    TextView End;
    boolean isSeeking;
    Handler handler = new Handler();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicplayer);

        try {
            musicPlayer = new MediaPlayer();
            musicPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            //Change below line once database imported
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.marshmello_summer);
            musicPlayer.setDataSource(this, uri);
            musicPlayer.prepare();

            ImageButton PlayButton = findViewById(R.id.PlayerPlayButton);
            ImageButton PrevButton = findViewById(R.id.PlayerPrevBut);
            ImageButton NextButton = findViewById(R.id.PlayerNextBut);
            Duration = findViewById(R.id.seekBar);
            Current = findViewById(R.id.PlayerStartTime);
            End = findViewById(R.id.PlayerEndTime);
            isSeeking = false;

            Duration.setMax(musicPlayer.getDuration());
            Duration.setProgress(musicPlayer.getCurrentPosition());
            PlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (musicPlayer.isPlaying()) {
                        musicPlayer.pause();
                        PlayButton.setImageResource(R.drawable.playerplaybut);
                    } else {
                        musicPlayer.start();
                        PlayButton.setImageResource(R.drawable.playerpausebut);
                    }
                }
            });
            setupSeekBarListener();
            updateUIComponents();
            handler.post(updateUIRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    Runnable updateUIRunnable = new Runnable() {
        @Override
        public void run() {
            updateUIComponents();
            handler.postDelayed(this, 1000); // Update every second
        }
    };
    void updateUIComponents() {
        if (!isSeeking) {
            Duration.setProgress(musicPlayer.getCurrentPosition());
            Current.setText(formatTime(musicPlayer.getCurrentPosition()));
        }
        End.setText(formatTime(musicPlayer.getDuration()));
    }
    void setupSeekBarListener() {
        Duration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    isSeeking = true;
                    musicPlayer.seekTo(progress); // Update current position of the song
                    Current.setText(formatTime(progress)); // Update current time TextView

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeeking = false;
            }
        });
    }
    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
