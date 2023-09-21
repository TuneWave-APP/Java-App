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
import java.util.Random;

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
    ImageButton RepButton;
    ImageButton LikeButton;
    ImageView AlbumCover;
    boolean isSeeking;
    Handler handler = new Handler();
    LinkedList<String[]> songList = new LinkedList<>();
    Random rand = new Random();
    int currsongindex = 0;
    int songListsize;
    int repeat = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicplayer);

        PlayButton = findViewById(R.id.PlayerPlayButton);
        PrevButton = findViewById(R.id.PlayerPrevBut);
        NextButton = findViewById(R.id.PlayerNextBut);
        RepButton = findViewById(R.id.PlayerRepButton);
        LikeButton = findViewById(R.id.PlayerLikeButton);
        Title = findViewById(R.id.PlayerSongTitle);
        Artist = findViewById(R.id.PlayerSongArtist);
        AlbumCover = findViewById(R.id.PlayerAlbumCover);

        // Add songs to the linked list
        songList.add(new String[]{"Summer", "Marshmello", String.valueOf(R.drawable.tunewave_logo), String.valueOf(R.raw.marshmello_summer), String.valueOf(false)});
        songList.add(new String[]{"Alone", "Marshmello", String.valueOf(R.drawable.tunewave_logo), String.valueOf(R.raw.marshmello_alone), String.valueOf(false)});
        songList.add(new String[]{"Summer", "Calvin Harris", String.valueOf(R.drawable.tunewave_logo), String.valueOf(R.raw.calvin_harris_summer), String.valueOf(false)});
        songListsize = songList.size();

        try {
            Duration = findViewById(R.id.seekBar);
            Current = findViewById(R.id.PlayerStartTime);
            End = findViewById(R.id.PlayerEndTime);
            isSeeking = false;
            setupmusicplayer();

            setupPlayButtonListener();
            setupRepButtonListener();
            setupLikeButtonListener();
            setupPrevButtonListener();
            setupNextButtonListener();
            setupSeekBarListener();
            updateUIComponents();
            handler.post(updateUIRunnable);
            handler.post(updateLikeRunnable);

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
                if(repeat!=2)
                    currsongindex++;
                if (currsongindex < songListsize) {
                    setupmusicplayer();
                    musicPlayer.start();
                } else {
                    if(repeat==1)
                    {
                        currsongindex = 0;
                        setupmusicplayer();
                        musicPlayer.start();
                    }
                    else
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
    Runnable updateLikeRunnable = new Runnable() {
        @Override
        public void run() {
            checkLikeButton(Boolean.parseBoolean(songList.get(currsongindex)[4]));
            handler.postDelayed(this, 50);
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

    void setupRepButtonListener() {
        RepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(repeat<2)
                    repeat++;
                else
                    repeat=0;
                switch(repeat)
                {
                    case 0: {
                        RepButton.setImageResource(R.drawable.loop_inactive);
                    } break;
                    case 1: {
                        RepButton.setImageResource(R.drawable.loop);
                    } break;
                    case 2: {
                        RepButton.setImageResource(R.drawable.single_loop_1);
                    } break;
                }
            }
        });
    }

    void setupLikeButtonListener() {
        LikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Boolean.parseBoolean(songList.get(currsongindex)[4])){
                    LikeButton.setImageResource(R.drawable.heart_fill_1);
                    songList.get(currsongindex)[4] = String.valueOf(true);
                }
                else {
                    LikeButton.setImageResource(R.drawable.heart_outline_1);
                    songList.get(currsongindex)[4] = String.valueOf(false);
                }
            }
        });
    }

    void checkLikeButton(boolean like)
    {
        if(like)
        {
            LikeButton.setImageResource(R.drawable.heart_fill_1);
        }
        else{
            LikeButton.setImageResource(R.drawable.heart_outline_1);
        }
    }

    void setupPrevButtonListener() {
        PrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayButton.setImageResource(R.drawable.playerpausebut);
                if (currsongindex > 0) {
                if(repeat!=2)
                    currsongindex--;
                setupmusicplayer();
                musicPlayer.start();
            }
            else
            {
                if(repeat==1)
                    currsongindex=songListsize-1;
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
                if (currsongindex < (songListsize - 1)) {
                    if(repeat!=2)
                        currsongindex++;
                    setupmusicplayer();
                    musicPlayer.start();
                } else {
                    if(repeat==2) {
                        setupmusicplayer();
                        musicPlayer.start();
                    }
                    else{
                        currsongindex = 0;
                        setupmusicplayer();
                        PlayButton.setImageResource(R.drawable.playerplaybut);
                        if (repeat == 1) {
                            PlayButton.setImageResource(R.drawable.playerpausebut);
                            musicPlayer.start();
                        }
                    }
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
