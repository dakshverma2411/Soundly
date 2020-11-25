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

import daksh.soundly.Model.Song;

public class FavouritesDB extends SQLiteOpenHelper {

    public FavouritesDB(@Nullable Context context) {
        super(context, Util.FAV_DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_QUERY="CREATE TABLE " + Util.FAV_TABLE + " ( " +
                Util.KEY_ID + " INTEGER PRIMARY KEY, " +
                Util.SONG_NAME + " TEXT, " +
                Util.SONG_ARTIST + " TEXT, " +
                Util.SONG_ALBUM + " TEXT, " +
                Util.SONG_PATH + " TEXT, " +
                Util.SONG_DURATION + " INTEGER ); ";
        db.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ArrayList<Song> getAllSongs()
    {
        String table=Util.FAV_TABLE;
        SQLiteDatabase db=this.getReadableDatabase();
        ArrayList<Song> songs=new ArrayList<>();
        String SELECT_QUERY="SELECT * FROM " + table;
        Cursor cursor=db.rawQuery(SELECT_QUERY,null);
        if(cursor!=null)
        {
            if(cursor.moveToFirst())
            {
                int title_index=cursor.getColumnIndex(Util.SONG_NAME);
                int album_index=cursor.getColumnIndex(Util.SONG_ALBUM);
                int artist_index=cursor.getColumnIndex(Util.SONG_ARTIST);
                int duration_index=cursor.getColumnIndex(Util.SONG_DURATION);
                int path_index=cursor.getColumnIndex(Util.SONG_PATH);
                do{
                    String title=cursor.getString(title_index);
                    String artist=cursor.getString(artist_index);
                    String album=cursor.getString(album_index);
                    Uri path= Uri.parse(cursor.getString(path_index));
                    int duration=cursor.getInt(duration_index);
                    Song song=new Song(title,artist,album,duration,path,0);
                    songs.add(song);
                }while(cursor.moveToNext());
            }
        }
        return songs;
    }

    public void addSong(Song song)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        String query="SELECT * FROM " + Util.FAV_TABLE + " WHERE "+ Util.SONG_PATH + " = '" + song.getPathToSong().toString() + "';" ;

        Cursor cursor= db.rawQuery(query,null);
        if(cursor==null || !cursor.moveToFirst())
        {
            ContentValues values=new ContentValues();
            values.put(Util.SONG_NAME,song.getTitle());
            values.put(Util.SONG_ARTIST,song.getArtist());
            values.put(Util.SONG_ALBUM,song.getAlbum());
            values.put(Util.SONG_PATH,song.getPathToSong().toString());
            values.put(Util.SONG_DURATION,song.getDuration());
            db.insert(Util.FAV_TABLE,null,values);
            db.close();
        }
    }

    public boolean ifPresent(Song song)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        String query="SELECT * FROM " + Util.FAV_TABLE + " WHERE "+ Util.SONG_PATH + " = '" + song.getPathToSong().toString() + "';" ;

        Cursor cursor= db.rawQuery(query,null);
        if(cursor==null || !cursor.moveToFirst())
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    public void remove_song(Song song)
    {
        Log.i("database","song removed");
        SQLiteDatabase db=this.getWritableDatabase();
        String query="DELETE FROM " +Util.FAV_TABLE+ " WHERE " +Util.SONG_PATH + " = '"+ song.getPathToSong().toString() + "';";
        db.execSQL(query);
    }

}
