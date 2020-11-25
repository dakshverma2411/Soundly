package daksh.soundly.Model;

import android.net.Uri;

public class Song {
    public String title;
    public String artist;
    public String album;
    public int duration;
    public Uri pathToSong;
    public int online;

    public Song(String title, String artist, String album, int duration, Uri pathToSong,int online) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.pathToSong = pathToSong;
        this.online=online;
    }



    public int isOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Uri getPathToSong() {
        return pathToSong;
    }

    public void setPathToSong(Uri pathToSong) {
        this.pathToSong = pathToSong;
    }
}
