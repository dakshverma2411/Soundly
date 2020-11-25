package daksh.soundly.Databases;

import android.content.Context;

public class Util {
    public static final String DB_NAME="Playlists";
    public static final int DB_VERSION=1;
    public static final String KEY_ID="id";
    public static final String SONG_NAME="songTitle";

    public static final String SONG_ARTIST="songArtist";
    public static final String SONG_ALBUM="songAlbum";
    public static final String SONG_ONLINE="songOnline";

    public static final String SONG_PATH="songPath";

    public static final String SONG_DURATION="songDuration";

    public static final String PLAYLISTS_DB_NAME="AllPlaylistsDatabase";
    public static final String PLAYLISTS_DB_TABLE_NAME="AllPlaylists";
    public static final int PLAYLISTS_DB_VERSION=1;
    public static final String PLAYLISTS_DB_ID="id";
    public static final String PLAYLISTS_DB_PLAYLIST_NAME="playlist_title";
    public static final String PLAYLISTS_DB_PLAYLIST_NUM_SONGS="playlist_num_songs";

    // favourites
    public static final String FAV_DB_NAME="favourites";
    public static final String FAV_TABLE="fav_table";

    public static Context context;
    public static void setContext(Context ctx)
    {
        context=ctx;
    }
    public static Context getAppContext()
    {
        return context;
    }

}
