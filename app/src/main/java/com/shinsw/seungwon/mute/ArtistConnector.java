package com.shinsw.seungwon.mute;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by seungwon on 2016-03-30.
 */
public class ArtistConnector extends SQLiteOpenHelper{
    Context mContext;
    SQLiteDatabase dbase;
    public ArtistConnector(Context context){
        super(context, "MuteDB", null, 1);
        mContext=context;
        dbase=this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        dbase=db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void getData(){
        if(ApplicationClass.artists.size()==0)
            update();
    }

    private void update(){//mediastore 에서 곡 업데이트

        Cursor c = dbase.rawQuery("SELECT DISTINCT artist FROM songlist ORDER BY artist COLLATE NOCASE ASC;", null);
        c.moveToFirst();

        int artist=c.getColumnIndex("artist");

        do{
            ApplicationClass.artists.add(new Artist(c.getString(artist)));
        }while(c.moveToNext());

        for(int i=0;i<ApplicationClass.songs.size();i++){
            for(int j=0;j<ApplicationClass.artists.size();j++){
                if(ApplicationClass.songs.get(i).getArtist().equals(ApplicationClass.artists.get(j).getName())){
                    ApplicationClass.artists.get(j).add(ApplicationClass.songs.get(i));
                    break;
                }
            }
        }c.close();
    }
}
