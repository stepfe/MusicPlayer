package ru.stepf.musicplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

//TODO Вывод времени
//TODO плейлисты
//TODO виджет на главной панели

public class MainActivity extends AppCompatActivity {

    private Searcher mSearcher;
    private ArrayList<String> playList;
    private ArrayList<File> fileList;
    private MediaPlayer mMediaPlayer;
    private Handler mHandler = new Handler();
    private int track = 0;
    int newPosition = 0;

    private ArrayAdapter<String> musicAdapter;
    private ListView lstMusic;
    private Button btnPlay;
    private Button btnNext;
    private SeekBar sbProgress;
    private Button btnPrev;
    private TextView lblName;
    private TextView lblCurTime;
    private TextView lblDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sbProgress = (SeekBar) findViewById(R.id.sbProgress);
        lstMusic = (ListView) findViewById(R.id.lstMusic);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrev = (Button) findViewById(R.id.btnPrev);
        lblName = (TextView) findViewById(R.id.lblName);
        lblCurTime = (TextView) findViewById(R.id.lblCurTime);
        lblDuration = (TextView) findViewById(R.id.lblDuration);


        mSearcher = new Searcher(Environment.getExternalStorageDirectory() + "/Музыка");
        playList = new ArrayList<>();
        fileList = mSearcher.search();

        btnPlay.setOnClickListener(btnPlayListener);
        btnNext.setOnClickListener(nextClickListener);
        btnPrev.setOnClickListener(prevClickListener);
        lstMusic.setOnItemClickListener(musicListListener);
        sbProgress.setOnSeekBarChangeListener(progressChangeListener);

        for (int i = 0; i < fileList.size(); i++) {
            playList.add(fileList.get(i).getName());
        }

        musicAdapter = new ArrayAdapter<>(this, R.layout.list_item, playList);
        lstMusic.setAdapter(musicAdapter);
        play(0);
        mMediaPlayer.pause();
    }

    SeekBar.OnSeekBarChangeListener progressChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                newPosition = progress;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mMediaPlayer.seekTo(newPosition);
        }
    };


    ListView.OnItemClickListener musicListListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            play(position);
        }
    };

    private void play(int index){
        int mins;
        int sec;
        if(mMediaPlayer != null)
            mMediaPlayer.release();
        mMediaPlayer = MediaPlayer.create(this, Uri.fromFile(fileList.get(index)));//TODO ПРОВЕРКА НА СУЩЕСТВОВАНИЕ
        mMediaPlayer.start();
        track = index;
        sec = mMediaPlayer.getDuration() / 1000;
        mins = sec / 60;
        sec -= mins * 60;
        mMediaPlayer.setOnCompletionListener(mMediaPlayerOnCompletionListener);
        lblName.setText(playList.get(index));
        sbProgress.setMax(mMediaPlayer.getDuration());
        lblDuration.setText(mins + ":" + sec);
        mHandler.removeCallbacks(timeUpdater);
        mHandler.postDelayed(timeUpdater, 100);

    }

    private Runnable timeUpdater = new Runnable() {
        @Override
        public void run() {
            sbProgress.setProgress(mMediaPlayer.getCurrentPosition());
            mHandler.postDelayed(this, 100);
        }
    };

    Button.OnClickListener btnPlayListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.pause();
            }else{
                mMediaPlayer.start();
            }
        }
    };

    MediaPlayer.OnCompletionListener mMediaPlayerOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            playNext();
        }
    };

    private void playNext(){
        if(track == playList.size() - 1){
            play(0);
        }else {
            play(track + 1);
        }
    }

    Button.OnClickListener nextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playNext();
        }
    };

    Button.OnClickListener prevClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(track == 0){
                play(playList.size() - 1);
            }else {
                play(track - 1);
            }
        }
    };

    @Override
    protected void onDestroy() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        super.onDestroy();
    }
}
