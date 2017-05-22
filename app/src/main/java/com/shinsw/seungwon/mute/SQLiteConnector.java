package com.shinsw.seungwon.mute;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by seungwon on 2016-03-22.
 */
public class SQLiteConnector extends SQLiteOpenHelper{//song 객체들을 db에 관리함,불러오거나 저장
    Context mContext;
    SQLiteDatabase dbase;
    public SQLiteConnector(Context context){
        super(context, "MuteDB", null, 1);
        dbase=this.getWritableDatabase();
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        dbase=db;
        String sql = "CREATE TABLE IF NOT EXISTS " + "songlist" + " (title text, " + "artist text, " +
                "musicid text, " +"albumartid text, " +"filepath text, " +"adddate int, " +"runtime int, " +
                "rock int, " +"beat int, " +"calm int, " +"vibrant int, "+"goodbad int, " +"clickcount int " +
               "x int, "+"y int"+");";

        String sql2= "CREATE TABLE IF NOT EXISTS " + "timestamp" + " (title text, " + "artist text, " +
                "playdate int, " +"walking int " +
                ");";
        String sql3= "CREATE TABLE IF NOT EXISTS " + "wishlist" + " (title text, " + "artist text" + ");";
        dbase.execSQL(sql);
        dbase.execSQL(sql2);
        dbase.execSQL(sql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean addWishList(String title,String artist){
        String sqlx="SELECT * FROM wishlist WHERE" +
                " title=\""+title+"\" AND artist=\""+artist +"\";";
        Cursor c = dbase.rawQuery(sqlx, null);
        if(c.getCount()>0)
            return false;

        String sql="INSERT INTO wishlist VALUES" +
                " ( \""+title+"\",\"" +
                artist+"\");";
        dbase.execSQL(sql);
        return true;
    }

    public boolean isWishList(String title,String artist){

        String sqlx="SELECT * FROM wishlist WHERE" +
                " title=\""+title+"\" AND artist=\""+artist +"\";";
        Cursor c = dbase.rawQuery(sqlx, null);

            return c.getCount()>0;
    }
    public void deleteWishList(String title,String artist){
        String sql="DELETE FROM wishlist WHERE" +
                " title=\""+title+"\" AND artist=\""+artist +"\";";
        dbase.execSQL(sql);
    }

    public ArrayList<wishitem> getWishList(){
        ArrayList<wishitem> E=new ArrayList<>();
        Cursor c = dbase.rawQuery("SELECT * FROM wishlist;", null);
        if(c.getCount()==0)
            return E;
        c.moveToFirst();
        int title=c.getColumnIndex("title");
        int artist=c.getColumnIndex("artist");
        do{
            E.add(new wishitem(c.getString(title),c.getString(artist)));
        }while(c.moveToNext());

        return E;
    }

    public void timeStamp(Song song){
        Calendar c=Calendar.getInstance();

        String sql="INSERT INTO timestamp VALUES" +
                " ( \""+song.getTitle()+"\",\"" +
                song.getArtist()+"\",\""+
                c.getTimeInMillis()/1000+"\",\"" +
                0+"\"" +
                ");";
        dbase.execSQL(sql);
    }

    public ArrayList<TimeStampObject> getTimestamp(){
        Cursor c = dbase.rawQuery("SELECT * FROM timestamp ORDER BY playdate DESC;", null);
        c.moveToFirst();
        Calendar C=Calendar.getInstance();
        int title=c.getColumnIndex("title");
        int artist=c.getColumnIndex("artist");
        int playdate=c.getColumnIndex("playdate");
        //2주 1209600
        ArrayList<TimeStampObject> T=new ArrayList<>();
        int checker;
        do{
            if(T.size()==0) {
                if(c.getCount()==0)
                    T.add(new TimeStampObject(ApplicationClass.songs.get(0).getTitle(), ApplicationClass.songs.get(0).getArtist()));
                else
                    T.add(new TimeStampObject(c.getString(title), c.getString(artist)));
                continue;
            }
            checker=0;
            for(int i=0;i<T.size();i++){
                if(T.get(i).same(c.getString(title),c.getString(artist))) {
                    T.get(i).count++;
                    checker=1;
                }
            }
            if(checker==0)
                T.add(new TimeStampObject(c.getString(title),c.getString(artist)));

        }while(c.moveToNext()&&C.getTimeInMillis()/1000-c.getInt(playdate)<1209600);

        return T;
    }

    public void getData(){

        Cursor c = dbase.rawQuery("SELECT * FROM songlist;", null);
        if(c.getCount()!=0)
            update();
        else
            writeAll();

    }

    private void update(){//mediastore 에서 곡 업데이트

        if(ApplicationClass.songs.size()==0)
            getMusicInfo(ApplicationClass.songs);

        ArrayList<Song> frommediastore=new ArrayList<>();
        ArrayList<Song> fromsonglist=new ArrayList<>();
        ArrayList<Song> needtoadd=new ArrayList<>();
        ArrayList<Song> needtodelete=new ArrayList<>();

        getMusicInfo(frommediastore);

        Cursor c = dbase.rawQuery("SELECT * FROM songlist;", null);
        c.moveToFirst();

        int title=c.getColumnIndex("title");
        int artist=c.getColumnIndex("artist");

        do{
           fromsonglist.add(new Song(c.getString(title),c.getString(artist),"sample","sample","sample",0));
        }while(c.moveToNext());

        //비교
        int checker=0;

        for(int i=0;i<frommediastore.size();i++){
            for(int j=0;j<fromsonglist.size();j++){
                if(frommediastore.get(i).equals(fromsonglist.get(j))) {
                    checker=1;
                }
            }
            if(checker==0){
                needtoadd.add(frommediastore.get(i));
            }else
                checker=0;
        }
        checker=0;
        for(int i=0;i<fromsonglist.size();i++){
            for(int j=0;j<frommediastore.size();j++){
                if(fromsonglist.get(i).equals(frommediastore.get(j)))
                    checker=1;
            }
            if(checker==0){
                needtodelete.add(fromsonglist.get(i));
            }else{
                checker=0;
            }
        }
        for(int i=0;i<needtodelete.size();i++){
                    deleteSongList(needtodelete.get(i));
        }
        for(int i=0;i<needtoadd.size();i++){
            insertSonglist(needtoadd.get(i));
        }

        ApplicationClass.songs=new ArrayList<>();
        LoadSongList(ApplicationClass.songs);

    }

    private void LoadSongList(ArrayList<Song> E){

        Cursor c = dbase.rawQuery("SELECT * FROM songlist ORDER BY title COLLATE NOCASE ASC;", null);
        c.moveToFirst();

        int title=c.getColumnIndex("title");
        int artist=c.getColumnIndex("artist");
        int musicid=c.getColumnIndex("musicid");
        int albumartid=c.getColumnIndex("albumartid");
        int filepath=c.getColumnIndex("filepath");
        int adddate=c.getColumnIndex("adddate");
        int rock=c.getColumnIndex("rock");
        int beat=c.getColumnIndex("beat");
        int calm=c.getColumnIndex("calm");
        int vibrant=c.getColumnIndex("vibrant");
        int goodbad=c.getColumnIndex("goodbad");
        int clickcount=c.getColumnIndex("clickcount");
        int runtime=c.getColumnIndex("runtime");

        do{
            Song temp=new Song(c.getString(title), c.getString(artist), c.getString(musicid), c.getString(albumartid), c.getString(filepath), c.getLong(adddate));
            temp.setAttribute(c.getInt(rock), c.getInt(beat), c.getInt(calm), c.getInt(vibrant));
            temp.setExtras(c.getInt(goodbad), c.getInt(clickcount), c.getInt(runtime));
            E.add(temp);
        }while(c.moveToNext());

    }

    public Song isExist(String title,String artist){
        for(int i=0;i<ApplicationClass.artists.size();i++){
            if(Parser.compare(artist,ApplicationClass.artists.get(i).getName())){
                for(int j=0;j<ApplicationClass.artists.get(i).getTitles().size();j++){
                    if(Parser.compare(ApplicationClass.artists.get(i).getTitles().get(j).getTitle(), title))
                        return ApplicationClass.artists.get(i).getTitles().get(j);
                }
            }
        }
        return null;
    }


    private void writeAll(){//song db가 비었을 경우 한번에 쭉 쓴다.

        getMusicInfo(ApplicationClass.songs);
        for(int i=0;i<ApplicationClass.songs.size();i++){
            insertSonglist(ApplicationClass.songs.get(i));
        }

    }

    public void updateGoodBad(Song song){

        String sql="UPDATE songlist SET goodbad='"+song.getGoodbad()+"' WHERE title=\""+song.getTitle()+"\"AND artist=\""+song.getArtist() +"\";";
        dbase.execSQL(sql);

    }
    public void updateElements(Song song){

        String sql="UPDATE songlist SET "
                +"rock='"+song.rock+"', "
                +"calm='"+song.calm+"', "
                +"beat='"+song.beat+"', "
                +"vibrant='"+song.vibrant+"' "
                +"WHERE title=\""+song.getTitle()+"\"AND artist=\""+song.getArtist() +"\";";
        dbase.execSQL(sql);

    }
    public void updateClickCount(Song song){

        String sql="UPDATE songlist SET clickcount='"+song.getClickcount()+"' WHERE title=\""+song.getTitle()+"\"AND artist=\""+song.getArtist() +"\";";
        dbase.execSQL(sql);

    }

    private void insertSonglist(Song song){

        String sql="INSERT INTO songlist VALUES" +
                " ( \""+song.getTitle()+"\",\"" +
                song.getArtist()+"\",\""+
                song.getMusicId()+"\",\"" +
                song.getAlbumArtId()+"\",\"" +
                song.getFilePath()+"\",\"" +
                song.getAddDate()+"\"" +
                ",0,0,0,0,0,0,0,0);";
        dbase.execSQL(sql);

    }
    public void deleteSongList(Song song){

        ApplicationClass.songs.remove(song);
        String sql="DELETE FROM songlist WHERE" +
                " title=\""+song.getTitle()+"\" AND artist=\""+song.getArtist() +"\";";
        dbase.execSQL(sql);

    }
    public boolean deleteRealFile(Song song){
        File file = new File(song.getFilePath());
        return file.delete();
    }

    public void InsertRuntime(Song song){

        song.setRuntime(MusicPlayBack.mMediaPlayer.getCurrentPosition());
        String sql="UPDATE songlist SET runtime='"+song.getRuntime()+"' WHERE title=\""+song.getTitle()+"\"AND artist=\""+song.getArtist() +"\";";
        dbase.execSQL(sql);

    }
    private void getMusicInfo(ArrayList<Song> songs) {
            /* 이 예제에서는 단순히 ID (Media의 ID, Album의 ID)만 얻는다.*/
        String[] proj = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DATA,

        };
        Cursor musicCursor;
            musicCursor = mContext.getContentResolver().
                    query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            proj, null, null, MediaStore.Audio.Media.TITLE + " COLLATE NOCASE ASC");

        if (musicCursor != null && musicCursor.moveToFirst()) {
            String musicID;
            String albumID;
            String title;
            String artist;
            String size;
            String path;
            int musicIDCol = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int albumIDCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int musicTitleCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int singerCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int musicSizeCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
            int pathCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                size = musicCursor.getString(musicSizeCol);
                //doesn't show under 1 mb .
                if (Integer.parseInt(size) < 1000000)
                    continue;

                musicID = musicCursor.getString(musicIDCol);
                albumID = musicCursor.getString(albumIDCol);
                title = musicCursor.getString(musicTitleCol);
                artist = musicCursor.getString(singerCol);

                byte[] checkstring=title.getBytes();
                for(int i=0;i<checkstring.length;i++){
                    if((int)checkstring[i]==34)
                        checkstring[i] = (byte)((int) checkstring[i] + 5);
                }
                title=new String(checkstring,0,checkstring.length);

                checkstring=artist.getBytes();
                for(int i=0;i<checkstring.length;i++){
                    if((int)checkstring[i]==34)
                        checkstring[i] = (byte)((int) checkstring[i] + 5);
                }
                artist=new String(checkstring,0,checkstring.length);

                path=musicCursor.getString(pathCol);


                Calendar ca = Calendar.getInstance();
                long seconds = ca.getTimeInMillis();
                Song song=new Song(title,artist,musicID,albumID,path,seconds);
                    songs.add(song);

            } while (musicCursor.moveToNext());
        }
        try {
            musicCursor.close();
        } catch (Exception e) {
        }
    }
}
