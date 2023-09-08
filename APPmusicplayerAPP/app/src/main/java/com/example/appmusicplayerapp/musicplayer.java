package com.example.appmusicplayerapp;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Handler;

import java.util.LinkedList;

public class musicplayer extends AppCompatActivity {
    MediaPlayer musicPlayer;
    SeekBar Duration;
    TextView Current;
    TextView End;
    TextView Title;
    TextView Artist;
    ImageButton PlayButton;
    ImageButton PrevButton;
    ImageButton NextButton;
    ImageView AlbumCover;
    boolean isSeeking;
    Handler handler = new Handler();
    LinkedList<String[]> songList = new LinkedList<>();
    int currsongindex = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicplayer);

        PlayButton = findViewById(R.id.PlayerPlayButton);
        PrevButton = findViewById(R.id.PlayerPrevBut);
        NextButton = findViewById(R.id.PlayerNextBut);
        Title = findViewById(R.id.PlayerSongTitle);
        Artist = findViewById(R.id.PlayerSongArtist);
        AlbumCover = findViewById(R.id.PlayerAlbumCover);

        // Add songs to the linked list
        songList.add(new String[]{"Summer", "Marshmello", String.valueOf(R.drawable.tunewave_logo), String.valueOf(R.raw.marshmello_summer)});
        songList.add(new String[]{"Alone", "Marshmello", String.valueOf(R.drawable.tunewave_logo), String.valueOf(R.raw.marshmello_alone)});
        songList.add(new String[]{"Summer", "Calvin Harris", String.valueOf(R.drawable.tunewave_logo), String.valueOf(R.raw.calvin_harris_summer)});

        try {
            Duration = findViewById(R.id.seekBar);
            Current = findViewById(R.id.PlayerStartTime);
            End = findViewById(R.id.PlayerEndTime);
            isSeeking = false;
            setupmusicplayer();

            setupPlayButtonListener();
            setupPrevButtonListener();
            setupNextButtonListener();
            setupSeekBarListener();
            updateUIComponents();
            handler.post(updateUIRunnable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setupmusicplayer() {
        if (musicPlayer != null) {
            musicPlayer.release();
        }

        musicPlayer = new MediaPlayer();
        musicPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + songList.get(currsongindex)[3]);
        try {
            musicPlayer.setDataSource(this, uri);
            musicPlayer.prepare();
            Title.setText(songList.get(currsongindex)[0]);
            Artist.setText(songList.get(currsongindex)[1]);
            AlbumCover.setImageResource(Integer.parseInt(songList.get(currsongindex)[2]));

        } catch (Exception e) {
            e.printStackTrace();
        }

        Duration.setMax(musicPlayer.getDuration());
        Duration.setProgress(musicPlayer.getCurrentPosition());

        musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                currsongindex++; // Proceed to the next song
                if (currsongindex < songList.size()) {
                    setupmusicplayer(); // Set up the next song
                    musicPlayer.start(); // Start playing the next song
                } else {
                    PlayButton.setImageResource(R.drawable.playerplaybut);
                }
            }
        });

        if (musicPlayer != null) {
            Duration.setMax(musicPlayer.getDuration());
            Duration.setProgress(musicPlayer.getCurrentPosition());
        }
    }

    Runnable updateUIRunnable = new Runnable() {
        @Override
        public void run() {
            updateUIComponents();
            handler.postDelayed(this, 1000);
        }
    };

    void updateUIComponents() {
        if (!isSeeking) {
            Duration.setProgress(musicPlayer.getCurrentPosition());
            Current.setText(formatTime(musicPlayer.getCurrentPosition()));
        }
        End.setText(formatTime(musicPlayer.getDuration()));
    }

    void setupPlayButtonListener() {
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
    }

    void setupPrevButtonListener() {
        PrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayButton.setImageResource(R.drawable.playerpausebut);
                if (currsongindex > 0) {
                    currsongindex--;
                    setupmusicplayer();
                    musicPlayer.start();
                } else {
                    setupmusicplayer();
                    musicPlayer.start();
                }
            }
        });
    }

    void setupNextButtonListener() {
        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayButton.setImageResource(R.drawable.playerpausebut);
                if (currsongindex < (songList.size()-1)) {
                    currsongindex++;
                    setupmusicplayer();
                    musicPlayer.start();
                } else {
                    currsongindex = 0;
                    setupmusicplayer();
                    musicPlayer.start();
                }
            }
        });
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
                isSeeking = false
                ;
            }
        });
    }

    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
