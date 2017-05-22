package com.shinsw.seungwon.mute;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import java.util.ArrayList;

/**
 * Created by seungwon on 2016-03-30.
 */
public class Artist {

    static Context c;
    public int vibrant=0;
    public int calm=0;
    public int beat=0;
    public int rock=0;
    public int good=0;
    public int bad=0;
    ImageView v;
    Bitmap albumart;
    private String name;
    private ArrayList<Song> titles=new ArrayList<>();
    private int clickcount=0;
    Artist(String name){
        this.name=name;
    }
    public String getName(){
        return name;
    }
    public void add(Song song){
        titles.add(song);
    }
    public int getCount(){
        return titles.size();
    }
    public int getClickcount(){
        return clickcount;
    }
    public void setAlbumart(Bitmap b){
        albumart=b;
    }
    public Bitmap getAlbumArt(){
       return albumart;
    }
    public int getAlbumArtId(){
        if(titles.size()==0)
            return 0;
        else{
            for(int i=0;i<titles.size();i++) {
                if(ApplicationClass.getArtworkQuick(c,Integer.parseInt(titles.get(i).getAlbumArtId()),10,10)!=null)
                    return Integer.parseInt(titles.get(i).getAlbumArtId());
            }
            return 0;
        }
    }

    public ArrayList<Song> getTitles(){
        return titles;
    }

    public void getData(){
          for(int i=0;i<titles.size();i++){
              clickcount+=titles.get(i).getClickcount();

              vibrant+=titles.get(i).vibrant;
              calm+=titles.get(i).calm;
              beat+=titles.get(i).beat;
              rock+=titles.get(i).rock;

              if(titles.get(i).getGoodbad()==1)
                  good++;
              else if(titles.get(i).getGoodbad()==-1)
                  bad++;
          }
        vibrant=vibrant/titles.size();
        calm=calm/titles.size();
        beat=beat/titles.size();
        rock=rock/titles.size();
    }
}
