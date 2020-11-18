package daksh.soundly.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import daksh.soundly.Model.Playlist;
import daksh.soundly.Model.Song;

public class AllPlaylists extends SQLiteOpenHelper {

    public AllPlaylists(@Nullable Context context) {
        super(context, Util.PLAYLISTS_DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable="CREATE TABLE "+ Util.PLAYLISTS_DB_TABLE_NAME + " (" +
                Util.PLAYLISTS_DB_ID+ " INTEGER PRIMARY KEY , " +
                Util.PLAYLISTS_DB_PLAYLIST_NAME+" TEXT, "+
                Util.PLAYLISTS_DB_PLAYLIST_NUM_SONGS+" INTEGER );";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addPlaylist(String playlist) {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(Util.PLAYLISTS_DB_PLAYLIST_NAME,playlist);
        values.put(Util.PLAYLISTS_DB_PLAYLIST_NUM_SONGS,0);
        db.insert(Util.PLAYLISTS_DB_TABLE_NAME,null,values);
        db.close();
    }

    public ArrayList<Playlist> getAllPlaylists()
    {
        ArrayList<Playlist> playlists=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();

        String SELECT_QUERY="SELECT * FROM " + Util.PLAYLISTS_DB_TABLE_NAME +";";
        Cursor cursor=db.rawQuery(SELECT_QUERY,null);
        if(cursor!=null)
        {
            if(cursor.moveToFirst())
            {
                int title_index=cursor.getColumnIndex(Util.PLAYLISTS_DB_PLAYLIST_NAME);
                int numSongs_index=cursor.getColumnIndex(Util.PLAYLISTS_DB_PLAYLIST_NUM_SONGS);
                do{
                    String title=cursor.getString(title_index);
                    int numSongs=cursor.getInt(numSongs_index);
                    playlists.add(new Playlist(title,numSongs));
                }while(cursor.moveToNext());
            }
        }
        return playlists;
    }

    public boolean ifAlreadyPresent(String playlist)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        String SELECT_QUERY="SELECT * FROM " + Util.PLAYLISTS_DB_TABLE_NAME +" WHERE "+Util.PLAYLISTS_DB_PLAYLIST_NAME +" = '"+playlist+"' ;";
        Cursor cursor=db.rawQuery(SELECT_QUERY,null);
        if(cursor!=null)
        {
            if(cursor.moveToFirst())
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public void deletePlaylist(String playlist)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        String query="DELETE FROM "+Util.PLAYLISTS_DB_TABLE_NAME+ " WHERE "+Util.PLAYLISTS_DB_PLAYLIST_NAME + " = '" + playlist + "' ;";
        db.execSQL(query);
    }

    public void addSongToPlaylist(Playlist playlist)
    {
        String playlist_name=playlist.getName();
        int songs=playlist.getNumberOfsong();
        Log.i("add_song_to_playlist",String.valueOf(songs));
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(Util.PLAYLISTS_DB_PLAYLIST_NAME,playlist_name);
        values.put(Util.PLAYLISTS_DB_PLAYLIST_NUM_SONGS,songs+1);
        db.update(Util.PLAYLISTS_DB_TABLE_NAME,values,Util.PLAYLISTS_DB_PLAYLIST_NAME+" = ?",new String[]{playlist_name});
    }

    public void remove_song(Playlist playlist)
    {
        String playlist_name=playlist.getName();
        int songs=playlist.getNumberOfsong();
        Log.i("add_song_to_playlist",String.valueOf(songs));
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(Util.PLAYLISTS_DB_PLAYLIST_NAME,playlist_name);
        values.put(Util.PLAYLISTS_DB_PLAYLIST_NUM_SONGS,songs-1);
        db.update(Util.PLAYLISTS_DB_TABLE_NAME,values,Util.PLAYLISTS_DB_PLAYLIST_NAME+" = ?",new String[]{playlist_name});

    }





}
