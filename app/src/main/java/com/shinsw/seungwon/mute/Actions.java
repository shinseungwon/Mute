package com.shinsw.seungwon.mute;

/**
 * Created by seungwon on 2016-03-22.
 */
public class Actions {//broadcast action string 을 관리하는 클래스
    public interface command{
        String permission="permission";
        String loadcomplete="loadcomplete";
        String frommain="frommain";
        String fromsearch="fromsearch";
        String clicksong="clicksong";
        String playpause="playpause";
        String next="next";
        String prev="prev";
        String stop="stop";
        String repeat="repeat";
        String bad="bad";
        String good="good";
        String refreshmain="refreshmain";
        String start="start";
        String getnext="getnext";
        String clustering="clustering";
        String fromcore="fromcore";
        String setted="setted";
        String clusteringcomplete="clusteringcomplete";
        String clusteringstart="clusteringstart";
        String bld="bld";
        String network="network";
        String sendnetwork="sendnetwork";
        String clusterlist="clusterlist";
        String networktoast="network2";
        String complete="complete";
        String delete="delete";
        String deleteprog="deleteprog";
        String recommend="recommend";
        String click="click";
        String fromwish="fromwish";
        String frombillboard="frombillboard";
    }
}
