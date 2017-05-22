package com.shinsw.seungwon.mute;

/**
 * Created by seungwon on 2016-03-22.
 */
public class User {//사용자 정보를 담는 객체
    int vibrant=0;
    int calm=0;
    int beat=0;
    int rock=0;
    int mode=0;
    int clusternumber=5;
    boolean set=false;
    int usehand=0;
    User(){

    }
    public void setAttribute(int vibrant,int calm,int beat,int rock){
        this.vibrant=vibrant;
        this.calm=calm;
        this.beat=beat;
        this.rock=rock;
        set=true;
    }




}
