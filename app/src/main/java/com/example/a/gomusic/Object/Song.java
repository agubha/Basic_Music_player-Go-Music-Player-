package com.example.a.gomusic.Object;

import android.net.Uri;

public class Song {
    private Long Id;

    private String Music_Title;

    private String Music_Artist;

    private Uri Music_Art;

    private String data;

    public Song(String thisdata, long thisId, String title, String artist, Uri Art) {
        this.Id = thisId;
        this.Music_Artist = artist;
        this.Music_Title = title;
        this.Music_Art = Art;
        this.data = thisdata;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Uri getMusic_Art() {
        return Music_Art;
    }

    public void setMusic_Art(Uri music_Art) {
        Music_Art = music_Art;
    }

    public String getMusic_Title() {
        return Music_Title;
    }

    public void setMusic_Title(String music_Title) {
        Music_Title = music_Title;
    }

    public String getMusic_Artist() {
        return Music_Artist;
    }

    public void setMusic_Artist(String music_Artist) {
        Music_Artist = music_Artist;
    }
}
