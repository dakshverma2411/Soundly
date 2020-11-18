package daksh.soundly.Databases;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import daksh.soundly.App;
import daksh.soundly.MainActivity;
import daksh.soundly.Model.Playlist;
import daksh.soundly.Model.Song;

public class DataBaseFunctions {

    public static Context context;

    public static void setContext(Context ctx)
    {
        context=ctx;
    }

    public static ArrayList<Playlist> getAllPlaylists()
    {
        AllPlaylists allPlaylists=new AllPlaylists(context);
        return allPlaylists.getAllPlaylists();
    }

    public static void addSongToPlaylist(Playlist playlist, Song song)
    {
        AllPlaylists allPlaylists=new AllPlaylists(context);
        PlaylistDatabase playlistDatabase=new PlaylistDatabase(context);

        if(playlistDatabase.addSong(playlist.getName(),song))
        {
            Log.i("add_song","done");
            allPlaylists.addSongToPlaylist(playlist);
        }
        else
        {
            Log.i("add_song","failed");
        }
    }

    public static void removeSongFromPlaylist(Playlist playlist,Song song)
    {
        AllPlaylists allPlaylists=new AllPlaylists(Util.getAppContext());
        PlaylistDatabase playlistDatabase=new PlaylistDatabase(Util.getAppContext());

        playlistDatabase.remove_song(playlist.getName(),song);
        allPlaylists.remove_song(playlist);
    }

    public static void deletePlaylist(Playlist playlist)
    {
        String playlist_name=playlist.getName();
        PlaylistDatabase playlistDatabase= new PlaylistDatabase(Util.getAppContext());
        AllPlaylists allPlaylists=new AllPlaylists(Util.getAppContext());
        playlistDatabase.deletePlaylist(playlist_name);
        allPlaylists.deletePlaylist(playlist_name);
    }
}
