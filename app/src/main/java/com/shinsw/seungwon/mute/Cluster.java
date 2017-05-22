package com.shinsw.seungwon.mute;

import java.util.ArrayList;
/**
 * Created by seungwon on 2016-04-12.
 */
public class Cluster {
    public int vibrant;
    public int calm;
    public int beat;
    public int rock;
    ArrayList<Song> member;
    Cluster(int vibrant,int calm,int beat,int rock){
        this.vibrant=vibrant;
        this.calm=calm;
        this.beat=beat;
        this.rock=rock;
        member=new ArrayList<>();
    }
    public void setPosition(int vibrant,int calm,int beat,int rock){
        this.vibrant=vibrant;
        this.calm=calm;
        this.beat=beat;
        this.rock=rock;
    }
    public void Add(Song song){
        member.add(song);
    }
    
    public void resetMember(){
        member=new ArrayList<>();
    }
}
