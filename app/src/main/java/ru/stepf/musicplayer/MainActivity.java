package ru.stepf.musicplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Searcher mSearcher;
    private ArrayList<String> playList;
    private ArrayList<File> fileList;
    private MediaPlayer mMediaPlayer;
    private int track = -1;

    private ArrayAdapter<String> musicAdapter;
    private ListView lstMusic;
    private Button btnPlay;
    private Button btnNext;
    private Button btnPrev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lstMusic = (ListView) findViewById(R.id.lstMusic);
        btnPlay = (Button)findViewById(R.id.btnPlay);
        btnNext = (Button)findViewById(R.id.btnNext);
        btnPrev = (Button)findViewById(R.id.btnPrev);


        mSearcher = new Searcher(Environment.getExternalStorageDirectory()+"/Музыка");
        mMediaPlayer = MediaPlayer.create(this, R.raw.music);
        playList = new ArrayList<>();
        fileList = mSearcher.search();

        btnPlay.setOnClickListener(btnPlayListener);
        btnNext.setOnClickListener(nextClickListener);
        btnPrev.setOnClickListener(prevClickListener);
        mMediaPlayer.setOnCompletionListener(mMediaPlayerOnCompletionListener);
        lstMusic.setOnItemClickListener(musicListListener);



        for (int i = 0; i < fileList.size(); i++){
            playList.add(fileList.get(i).getName());
        }

        musicAdapter = new ArrayAdapter(this,R.layout.list_item, playList);
        lstMusic.setAdapter(musicAdapter);
    }





    ListView.OnItemClickListener musicListListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            play(position);
        }
    };

    private void play(int index){
        mMediaPlayer.release();
        mMediaPlayer = MediaPlayer.create(this, Uri.fromFile(fileList.get(index)));
        mMediaPlayer.start();
        track = index;
        mMediaPlayer.setOnCompletionListener(mMediaPlayerOnCompletionListener);
    }

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
            if(track == playList.size() - 1){
                play(0);
            }else {
                play(track + 1);
            }
        }
    };

    Button.OnClickListener nextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(track == playList.size() - 1){
                play(0);
            }else {
                play(track + 1);
            }
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
