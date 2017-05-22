package com.shinsw.seungwon.mute;

/**
 * Created by seungwon on 2016-04-21.
 */
public class TimeStampObject {
    String title;
    String artist;
    int count=1;
    TimeStampObject(String title,String artist){
        this.title=title;
        this.artist=artist;
    }
    public boolean same(String title,String artist){
            return this.title.equals(title)&&this.artist.equals(artist);
    }
}
