package com.shinsw.seungwon.mute;

import android.util.Log;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by seungwon on 2016-04-12.
 */
public class Clustering {
    ArrayList<Song> Items;
    ArrayList<Cluster> Clusters;
    int movecount=0;
    Cluster result;
    Clustering(ArrayList<Song> E,int number){
        Items=E;
        Clusters=new ArrayList<>();
        Random random=new Random();
        for(int i=0;i<number;i++)
            Clusters.add(new Cluster(random.nextInt(100), random.nextInt(100), random.nextInt(100), random.nextInt(100)));
    }
    private void getMean(){
        int vibrant=0;
        int calm=0;
        int beat=0;
        int rock=0;
        for(int j=0;j<Clusters.size();j++) {
            for (int i = 0; i < Clusters.get(j).member.size(); i++) {
                vibrant +=Clusters.get(j).member.get(i).vibrant;
                calm +=Clusters.get(j).member.get(i).calm;
                beat +=Clusters.get(j).member.get(i).beat;
                rock +=Clusters.get(j).member.get(i).rock;
            }
            if (Clusters.get(j).member.size() != 0)
                Clusters.get(j).setPosition(vibrant / Clusters.get(j).member.size(), calm / Clusters.get(j).member.size(), beat / Clusters.get(j).member.size(), rock / Clusters.get(j).member.size());

             vibrant=0;
             calm=0;
             beat=0;
             rock=0;
        }
    }

    private double getDist(Song song,Cluster cluster){

        return Math.pow(Math.pow(song.vibrant - cluster.vibrant, 2)
                + Math.pow(song.calm - cluster.calm, 2)
                + Math.pow(song.beat - cluster.beat, 2)
                + Math.pow(song.rock - cluster.rock, 2), 0.5);
    }

    private double getDist(User user,Cluster cluster){

        return Math.pow(Math.pow(cluster.vibrant - user.vibrant, 2)
                + Math.pow(cluster.calm - user.calm, 2)
                + Math.pow(cluster.beat - user.beat, 2)
                + Math.pow(cluster.rock - user.rock, 2), 0.5);
    }

    private void findNearestCluster(Song song){
        double dist=201;
        int cluster=-2;
        double p;
        for(int i=0;i<Clusters.size();i++){
            p=getDist(song,Clusters.get(i));
                if(p<dist) {
                    dist = p;
                    cluster=i;
                }
        }
        if(song.ClusterNumber!=cluster) {
            song.ClusterNumber = cluster;
            movecount++;
        }
        if(cluster>=0)
            Clusters.get(cluster).Add(song);

    }


    public void doClustering(){
        Log.i("broke","dong");
        for(int i=0;i<100;i++){
            Log.i("broke","Cycle"+(i+1));
            if(i!=0) {
                for (int k = 0; k < Clusters.size(); k++)
                    Clusters.get(k).resetMember();
            }
            for(int j=0;j<ApplicationClass.songs.size();j++){
                findNearestCluster(ApplicationClass.songs.get(j));
            }
            if(movecount<5) {
                Log.i(i+"","Broke");
                break;
            }
            movecount=0;
            getMean();
        }
    }

    public void getUserCluster(){

        Cluster[] temp=new Cluster[Clusters.size()];

        for(int i=0;i<Clusters.size();i++)
            temp[i]=Clusters.get(i);

        for(int j=1;j<Clusters.size();j++)
            if(getDist(ApplicationClass.user,temp[0])>getDist(ApplicationClass.user,temp[j]))
                temp[0]=temp[j];

        result=temp[0];

    }

    public void getUserCluster(int n){
        if(Clusters.size()>n)
            result= Clusters.get(n);
        else
            result= Clusters.get(0);
    }
}
