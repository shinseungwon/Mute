package com.shinsw.seungwon.mute;

/**
 * Created by seungwon on 2016-04-02.
 */
public class IndexItem {
    private int scrollposition;
    private String character;
    IndexItem(int scr,String c){
        this.scrollposition=scr;
        this.character=c;
    }
    public int getScrollposition(){
        return scrollposition;
    }
    public String getCharacter(){
        return character;
    }
}
