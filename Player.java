package com.example.mus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player extends AppCompatActivity {
    Button playBtn,nextBtn,prevBtn,repBtn,randBtn;
    SeekBar positionBar;
    SeekBar volumeBar;
    TextView elapsedTimeLabel;
    TextView remainingTimeLabel;
    TextView songTextLabel;
    String songName;
    MediaPlayer mp;
    ArrayList<File> musicList;
    int totalTime;
    int position;
    int RANDOM=0;

    @SuppressLint("Newapi")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

    playBtn = (Button)findViewById(R.id.playBtn);
    nextBtn = (Button)findViewById(R.id.nextBtn);
    prevBtn = (Button)findViewById(R.id.prevBtn);
    randBtn = (Button)findViewById(R.id.randBtn);
    repBtn = (Button)findViewById(R.id.repBtn);
    elapsedTimeLabel = (TextView) findViewById(R.id.elapsedTimeLabel);
    remainingTimeLabel = (TextView)findViewById(R.id.remainingTimeLabel);
    songTextLabel = (TextView)findViewById(R.id.songLabel);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Now Playing");

//
        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        musicList = new ArrayList<File>();
        musicList=(ArrayList) bundle.getParcelableArrayList("songs");
        songName = bundle.getString("songname");
        //String sname = i.getStringExtra("songname");

        songTextLabel.setText(songName);
        songTextLabel.setSelected(true);

        position = bundle.getInt("pos",0);
        Uri u = Uri.parse(musicList.get(position).toString());

        mp = MediaPlayer.create(getApplicationContext(),u);
        mp.start();
        mp.seekTo(0);
        mp.setVolume(0.5f,0.5f);

        positionBar = (SeekBar)findViewById(R.id.positionBar);
        positionBar.setMax(mp.getDuration());

        /*if(positionBar.getProgress()==mp.getDuration()) {
            mp.stop();
            mp.release();
            position=((position+1)==musicList.size())?(0):(position+1);
            //position=((position+1)%musicList.size());
            Uri r = Uri.parse(musicList.get(position).toString());
            mp= MediaPlayer.create(getApplicationContext(),r);
            songName = musicList.get(position).getName().toString();
            songTextLabel.setText(songName);
            mp.start();
            }*/

        positionBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                mp.seekTo(progress);
                positionBar.setProgress(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
        );

    volumeBar = (SeekBar) findViewById(R.id.volumeBar);
        volumeBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            float volumeNum = progress / 100f;
            mp.setVolume(volumeNum, volumeNum);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
        );
    //Update
        new Thread(new Runnable() {
        @Override
        public void run() {
            while(mp!=null){
                try{
                    Message msg=Message.obtain();
                    msg.what= mp.getCurrentPosition();
                    handler.sendMessage(msg);
                    Thread.sleep(1000);
                }catch (InterruptedException e){}
            }
        }
    }).start();

        /*new Thread(){
            @Override
            public void run() {
                int totalDuration = mp.getDuration();
                int currentPosition = 0;
                while(currentPosition < totalDuration){
                    try{
                        Thread.sleep(500);
                        currentPosition=mp.getCurrentPosition();
                        handler
                    }catch (Exception e){
                }
            }
        }*/

};
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            int currentPosition = msg.what;
            positionBar.setProgress(currentPosition);
            String elapsedTime = createTimeLabel(currentPosition);
            elapsedTimeLabel.setText(elapsedTime);
            String remainingTime = createTimeLabel(mp.getDuration()-currentPosition);
            remainingTimeLabel.setText("- "+ remainingTime);
        }
    };

    public String createTimeLabel(int time){
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time/ 1000 % 60;

        timeLabel = min + " :";
        if (sec<10) timeLabel +="0";
        timeLabel += sec;

        return timeLabel;
    }

    public void prevBtnClick(View view){
        mp.stop();
        //mp.release();
        playBtn.setBackgroundResource(R.drawable.stop);
        if(RANDOM ==1){
            int p;
            p=position;
            do{position=(int)(Math.random() * musicList.size());}
            while(position==p);
            swit();
        }
        else {
            position=((position-1)<0)?(musicList.size()-1):(position-1);
            swit();
        }
    }

    public void  playBtnClick(View view){
        if (!mp.isPlaying()){
            mp.start();
            playBtn.setBackgroundResource(R.drawable.stop);
        }
        else{
            mp.pause();
            playBtn.setBackgroundResource(R.drawable.play);
        }
    }

    public void nextBtnClick(View view){
        mp.stop();
        //mp.release();
        playBtn.setBackgroundResource(R.drawable.stop);
        //position=((position+1)==musicList.size())?(0):(position+1);
        if(RANDOM ==1){
            int p;
            p=position;
            do{position=(int)(Math.random() * musicList.size());}
            while(position==p);
            swit();
        }
        else {
            position=((position+1)%musicList.size());
            swit();
        }
    }

    public void randBtnClick(View view){
        RANDOM = 1;
        mp.stop();
        playBtn.setBackgroundResource(R.drawable.stop);
        //mp.release();
        int p;
        p=position;
        do{position=(int)(Math.random() * musicList.size());}
        while(position==p);
        swit();
    }

    public void repBtnClick(View view){
        RANDOM = 0;
    }

    public void swit(){
        Uri u = Uri.parse(musicList.get(position).toString());
        mp = MediaPlayer.create(getApplicationContext(),u);
        songName = musicList.get(position).getName().toString();
        songTextLabel.setText(songName);
        positionBar.setMax(mp.getDuration());
        mp.start();
    }



    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }*/

}
