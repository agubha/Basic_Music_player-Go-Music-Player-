package com.example.a.gomusic.Services;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.example.a.gomusic.Object.Song;

import java.util.ArrayList;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    private final IBinder musicBind = new MusicBinder();
    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;


    public void onCreate() {
        //create the service
        super.onCreate();
        //initialize position
        songPosn = 0;
        //create player
        player = new MediaPlayer();
        initMusicPlayer();

    }

    public void initMusicPlayer() {
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);

    }


    public void setList(ArrayList<Song> theSongs) {
        songs = theSongs;
    }


    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    public void playSong() {
        //play a song
        player.reset();
        //get song
        Song playSong = songs.get(songPosn);
//get id
        long currSong = playSong.getId();
//set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public void setSong(int songIndex) {
        songPosn = songIndex;
    }

    public int getSongId() {
        return songPosn;
    }

    public int getDur() {
        return player.getDuration();
    }

    public int getCTime() {
        return player.getCurrentPosition();
    }

    public boolean isPng() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();
    }

    public void seek(int posn) {
        player.seekTo(posn);
    }

    public void go() {
        player.start();
    }

    public void playPrev() {
        songPosn--;
        if (songPosn > 0) songPosn = songs.size() - 1;
        playSong();
    }

    //skip to next
    public void playNext() {
        songPosn++;
        if (songPosn >= songs.size()) songPosn = 0;
        playSong();
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
