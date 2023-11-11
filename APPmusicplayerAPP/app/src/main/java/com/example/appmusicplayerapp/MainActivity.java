package com.example.appmusicplayerapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;

import java.sql.*;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


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
                try {
                    dbinitialise();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                startmusicplayer();
            }
    });
    }



    private void dbinitialise() throws SQLException {
        // connect to supabase
        String url = "jdbc:postgresql://db.argjkahpbsjcwfwisulc.supabase.co:5432/postgres?user=postgres&password=TuneWaveMusic";
        String user = "postgres";
        String password = "TuneWaveMusic";
        Connection conn = DriverManager.getConnection(url, user, password);
        Statement stmt = conn.createStatement();


        // SELECT all from the table SongList
        String sql = "SELECT * FROM \"SongList\"";
        ResultSet rs = stmt.executeQuery(sql);

        ArrayList<String> song_uri = new ArrayList<>();
        ArrayList<String> image_path = new ArrayList<>();
        ArrayList<String> image_uri = new ArrayList<>();
        ArrayList<String> song_path = new ArrayList<>();
        int i = 0;
        while (rs.next()) {
//            System.out.println(rs.getString("Song_uri").toString());
            song_uri.add(rs.getString("Song_uri").toString());
//            System.out.println(rs.getString("Image_uri").toString());
            image_uri.add(rs.getString("Image_uri").toString());
//            System.out.println(rs.getString("img_url").toString());
            image_path.add(rs.getString("img_url").toString());
//            System.out.println(rs.getString("song_url").toString());
            song_path.add(rs.getString("song_url").toString());
//            System.out.println(rs.getString("Image_uri").toString());
            image_uri.add(rs.getString("Image_uri").toString());
            i++;
        }

        stmt.close();
        conn.close();

        try {

            for (int m = 0; m < 6; m++) {

                //check if the file is downloaded
                if(Files.exists(Paths.get(image_path.get(m)))) {
//                    System.out.println("File exists: " + image_path.get(m));
//                    System.out.println(image_uri.get(m) + " " + image_path.get(m));
                    continue;
                }
                else{
//                    System.out.println("File does not exist");
                    downloadFile(image_uri.get(m), image_path.get(m));
                }
            }

//            System.out.println("Files downloaded successfully.");
        } catch (IOException e) {
//            System.out.println(e.getMessage() + "HELOOO");
            e.printStackTrace();
        }

        try {
            // Download the file using the specified URL
            for (int m = 0; m < 6; m++) {
                if(Files.exists(Paths.get(song_path.get(m)))) {
//                    System.out.println("File exists: " + song_path.get(m));
                    continue;
                }
                else{
//                    System.out.println("File does not exist");
                    downloadFile(song_uri.get(m), song_path.get(m));

                }
            }

//            System.out.println("songs downloaded successfully.");
        } catch (IOException e) {
//            System.out.println(e.getMessage() + "HELOOO");
            e.printStackTrace();
        }
    }

    private static void downloadFile(String fileUrl, String destinationPath) throws IOException {
        URL url = new URL(fileUrl);

        try (InputStream in = url.openStream()) {
            // Using java.nio.file to copy the InputStream to the destination file
            Path destination = Paths.get(destinationPath);
            Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void startmusicplayer() {
        Intent intent = new Intent(this, musicplayer.class);
        startActivity(intent);
    }
}