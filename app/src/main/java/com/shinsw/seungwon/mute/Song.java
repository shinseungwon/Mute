package com.shinsw.seungwon.mute;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by seungwon on 2016-03-22.
 */
public class Song {//1곡의 정보를 담는 객체

    public int ClusterNumber=0;
    public int NumberNeuron=0;
    public int rock=0;
    public int beat=0;
    public int calm=0;
    public int vibrant=0;
    private String title;
    private String artist;
    private String musicId;
    private String albumArtId;
    private String filePath;
    private int clickcount=0;
    private long addDate;
    private int runtime;
    private Bitmap albumart=null;
    private int goodbad=0;
    ImageView v;

    Song(String title,String artist,String musicId,String albumArtId,String FilePath,long addDate){
        this.title=title;
        this.artist=artist;
        this.musicId=musicId;
        this.albumArtId=albumArtId;
        this.filePath=FilePath;
        this.addDate=addDate;
    }
    //곡 특성 설정
    public void setAttribute(int rock,int beat,int calm,int vibrant){
        this.rock=rock;
        this.beat=beat;
        this.calm=calm;
        this.vibrant=vibrant;
    }

    public void setExtras(int goodbad,int clickcount,int runtime){
        this.goodbad=goodbad;
        this.clickcount=clickcount;
        this.runtime=runtime;
    }

    public void IncreaseClickcount(){
        clickcount++;
        ApplicationClass.SQLiteConnector.updateClickCount(this);
    }

    public String getTitle(){
        return title;
    }
    public String getArtist(){
        return artist;
    }
    public String getMusicId(){
        return musicId;
    }
    public String getAlbumArtId(){
        return albumArtId;
    }
    public String getFilePath(){
        return filePath;
    }

    public int getClickcount(){
        return clickcount;
    }
    public long getAddDate(){
        return addDate;
    }

    public void setAlbumart(Bitmap albumart){
        if(albumart!=null) {
            this.albumart = albumart;
        }
    }

    public Bitmap getAlbumArt(){return albumart;}
    public boolean equals(Song song){
            return this.getTitle().equals(song.getTitle())&&this.getArtist().equals(song.getArtist());
    }

    public boolean semiEquals(Song song){
        return Parser.compare(this.getTitle(),song.getTitle());
    }

    public void good(){
        if(goodbad==0)
            goodbad=1;
        else if(goodbad==-1)
            goodbad=1;
        else
            goodbad=0;
        ApplicationClass.SQLiteConnector.updateGoodBad(this);
    }
    public void bad(){
        if(goodbad==0)
            goodbad=-1;
        else if(goodbad==-1)
            goodbad=0;
        else
            goodbad=-1;
        ApplicationClass.SQLiteConnector.updateGoodBad(this);
    }

    public int getGoodbad(){
        return goodbad;
    }

    public int getRuntime(){return runtime;}

    public void setRuntime(int runtime){this.runtime=runtime;}

}
