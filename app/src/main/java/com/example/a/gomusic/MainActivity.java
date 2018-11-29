package com.example.a.gomusic;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a.gomusic.Adapter.SongListAdapter;
import com.example.a.gomusic.Object.Song;
import com.example.a.gomusic.Services.MusicService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101;//
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;
    private Uri thispath;
    private ArrayList<Song> songList;
    private RecyclerView recyclerView;
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;
    private ImageView imageViewMain, imageViewSlide;
    private TextView textTitleMain, textTitleslide, textArtistMain, textArtistSlide, textDateStart, textDateEnd;
    private ImageButton buttonNext, buttonPrevious, buttonSeekForward, buttonseekReverse;

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.song_list);
        imageViewMain = findViewById(R.id.album_cover_main);
        imageViewSlide = findViewById(R.id.album_cover_slide);
        textTitleMain = findViewById(R.id.song_title_main);
        textTitleslide = findViewById(R.id.song_title_slide);
//        textArtistMain=findViewById(R.id.TODO);
        //Naviagation Button
        buttonNext = findViewById(R.id.nextMain);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        });
        buttonPrevious = findViewById(R.id.previousMain);
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        buttonSeekForward = findViewById(R.id.seekforward);
        buttonSeekForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GET DURATION", "button Seek forward" + getCurrentTime());
                forward();

            }
        });
        buttonseekReverse = findViewById(R.id.seekback);
        buttonseekReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GET DURATION", "button Seek backward" + getCurrentTime());

                reverse();
            }
        });


        songList = new ArrayList<>();
        checkPermission();
        sort();


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SongListAdapter musicListAdapter = new SongListAdapter(this);
        recyclerView.setAdapter(musicListAdapter);
        musicListAdapter.setArraylist(songList, MainActivity.this);
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

        } else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        getSongList();
    }

    public void getSongList() {
        //retrieve song info
        ContentResolver musicResolver = this.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int isMusicColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.IS_MUSIC);
            int albumidColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            do {
                String thisMusic = musicCursor.getString(isMusicColumn);
                if (thisMusic.equals("1")) {
                    String thisTitle = musicCursor.getString(titleColumn);
                    String data = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    long thisId = musicCursor.getLong(idColumn);
                    long thisAlbumId = musicCursor.getLong(albumidColumn);
                    Log.d("ALBUM ID DETAIL", "" + thisAlbumId);
                    String thisArtist = musicCursor.getString(artistColumn);
                    Cursor cursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                            new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                            MediaStore.Audio.Albums._ID + "=?",
                            new String[]{String.valueOf(thisAlbumId)},
                            null);
                    Log.d("CHECK CURSOR ", DatabaseUtils.dumpCursorToString(cursor));
                    if (cursor != null && cursor.moveToFirst()) {

                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                        if (path == null) {
                            Log.d("if condition", "Null pinter?");
                            thispath = Uri.parse("0");
                        } else
                            thispath = Uri.parse(path);
                        cursor.close();
                        // do whatever you need to do
                    }
                    Log.d("info received from sto=", thisId + "---" + thisTitle + "--" + thisArtist + "--" + thispath);
                    Song mSong = new Song(data, thisId, thisTitle, thisArtist, thispath);
                    songList.add(mSong);
                }

            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }
    }

    private void sort() {
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getMusic_Title().compareTo(b.getMusic_Title());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    public void songPicked(int i) {
        musicSrv.setSong(i);
        musicSrv.playSong();
        updateUI(i);

    }

    private void updateUI(int i) {
        imageViewMain.setImageURI(songList.get(i).getMusic_Art());
        imageViewSlide.setImageURI(songList.get(i).getMusic_Art());
        textTitleMain.setText(songList.get(i).getMusic_Title());
        textTitleslide.setText(songList.get(i).getMusic_Title());


    }

    //play next
    public void playNext() {
        musicSrv.playNext();
    }

    //play previous
    private void playPrev() {
        musicSrv.playPrev();
    }

    public int getTotalTime() {
        if (musicSrv != null && musicBound && musicSrv.isPng())
            return musicSrv.getDur();
        else return 0;
    }

    public int getCurrentTime() {
        if (musicSrv != null && musicBound && musicSrv.isPng()) {
            return musicSrv.getCTime();
        }
        return 0;
    }

    public boolean isPlaying() {
        if (musicSrv != null && musicBound)
            return musicSrv.isPng();
        return false;
    }

    public void pause() {
        musicSrv.pausePlayer();
    }

    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    private void reverse() {
    }

    private void forward() {
    }

    public void start() {
        musicSrv.go();
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        unbindService(musicConnection);
        musicSrv = null;
        super.onDestroy();
    }
}
//TODO
//https://code.tutsplus.com/tutorials/create-a-music-player-on-android-user-controls--mobile-22787
/*no 3*/