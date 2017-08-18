package ru.stepf.musicplayer;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;


//TODO: плейлисты
//TODO: виджет на главной панели

public class MainActivity extends AppCompatActivity {

    private Searcher mSearcher;
    private MediaPlayer mMediaPlayer;
    private Handler mHandler = new Handler();
    private int track = 0;
    private int numOfTraks;
    int newPosition = 0;

    private ListView lstMusic;
    private Button btnPlay;
    private Button btnNext;
    private SeekBar sbProgress;
    private Button btnPrev;
    private TextView lblName;
    private TextView lblCurTime;
    private TextView lblDuration;
    private ListAdapter mAdapter;

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


        mSearcher = new Searcher(this);

        btnPlay.setOnClickListener(btnPlayListener);
        btnNext.setOnClickListener(nextClickListener);
        btnPrev.setOnClickListener(prevClickListener);
        lstMusic.setOnItemClickListener(musicListListener);
        sbProgress.setOnSeekBarChangeListener(progressChangeListener);


        mAdapter = mSearcher.search();

        lstMusic.setAdapter(mAdapter);

        numOfTraks = lstMusic.getCount();

        play(0);//// TODO: ЕСЛИ МУЗЫКИ НЕТ!!!!
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
        String minsMod = "";
        String secMod = "";

        if(mMediaPlayer != null)
            mMediaPlayer.release();
        mMediaPlayer = MediaPlayer.create(this, mSearcher.getPath(index));//TODO ПРОВЕРКА НА СУЩЕСТВОВАНИЕ
        mMediaPlayer.start();
        track = index;

        sec = mMediaPlayer.getDuration() / 1000;
        mins = sec / 60;
        sec -= mins * 60;
        if(sec < 10)
            secMod = "0";
        if(mins < 10)
            minsMod = "0";

        mMediaPlayer.setOnCompletionListener(mMediaPlayerOnCompletionListener);
        lblName.setText(mSearcher.getName(index));
        sbProgress.setMax(mMediaPlayer.getDuration());
        lblDuration.setText(minsMod + mins + ":" + secMod + sec);

        mHandler.removeCallbacks(timeUpdater);
        mHandler.postDelayed(timeUpdater, 100);

    }

    private Runnable timeUpdater = new Runnable() {
        @Override
        public void run() {
            int mins;
            int sec;
            String minsMod = "";
            String secMod = "";

            sec = mMediaPlayer.getCurrentPosition() / 1000;
            mins = sec / 60;
            sec -= mins * 60;
            if(sec < 10)
                secMod = "0";
            if(mins < 10)
                minsMod = "0";

            sbProgress.setProgress(mMediaPlayer.getCurrentPosition());

            lblCurTime.setText(minsMod + mins + ":" + secMod + sec);
            mHandler.postDelayed(this, 100);
        }
    };

    Button.OnClickListener btnPlayListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.pause();
                mHandler.removeCallbacks(timeUpdater);
            }else{
                mMediaPlayer.start();
                mHandler.postDelayed(timeUpdater, 100);
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
        if(track == numOfTraks - 1){
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
                play(numOfTraks - 1);
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
