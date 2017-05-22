package com.shinsw.seungwon.mute;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by seungwon on 2016-04-21.
 */
public class NeuralNetwork {
    ArrayList<Song> sort1=new ArrayList<>();
    ArrayList<Song> sort2=new ArrayList<>();
    ArrayList<Song> sort3=new ArrayList<>();
    ArrayList<Song> sort4=new ArrayList<>();
    ArrayList<TimeStampObject> T;
    ArrayList<Song> tempsong=new ArrayList<>();
    private final double deadLine=0.3;
    int size;

    double[] chance=new double[4];
    NeuralNetwork(ArrayList<Song> S){

        T=ApplicationClass.SQLiteConnector.getTimestamp();
        size=S.size();
        for(int i=0;i<size;i++)
            tempsong.add(S.get(i));

        insertgoodbad(tempsong);
        insertsort1(tempsong, T);
        insertsort2(tempsong);
        insertsort3(tempsong);
        insertsort4(tempsong);
        setMuteValue(ApplicationClass.user.mode);
    }

    public void setMuteValue(int mode){

        double c1=sort1.size()/(double)size;
        double c2=sort2.size()/(double)size;
        double c3=sort3.size()/(double)size;
        //double c4=sort4.size()/(double)size;
        double c4=1-c1-c2-c3;



        switch(mode) {
            case 1:
                //deadLine=0.05;
/*                chance[0]=0.5;
                chance[1]=0.4;
                chance[2]=0.1;
                chance[3]=0.0;*/
                chance[0]=c1+((16/21.0)*c4);
                chance[1]=c2+((4/21.0)*c4);
                chance[2]=c3+((1/21.0)*c4);
                chance[3]=1-chance[0]-chance[1]-chance[3];
                break;
            case 2:
                //deadLine=0.1;
/*                chance[0]=0.45;
                chance[1]=0.35;
                chance[2]=0.15;
                chance[3]=0.5;*/
                chance[0]=c1+((9/13.0)*(0.75*c4));
                chance[1]=c2+((3/13.0)*(0.75*c4));
                chance[2]=c3+((1/13.0)*(0.75*c4));
                chance[3]=1-chance[0]-chance[1]-chance[3];
                break;
            case 3:
                //deadLine=0.15;
/*                chance[0]=0.4;
                chance[1]=0.3;
                chance[2]=0.2;
                chance[3]=0.1;*/
                chance[0]=c1+((4/7.0)*(0.5*c4));
                chance[1]=c2+((2/7.0)*(0.5*c4));
                chance[2]=c3+((1/7.0)*(0.5*c4));
                chance[3]=1-chance[0]-chance[1]-chance[3];
                break;
            case 4:
                //deadLine=0.2;
/*                chance[0]=0.35;
                chance[1]=0.25;
                chance[2]=0.25;
                chance[3]=0.15;*/
                chance[0]=c1+((1/3.0)*(0.25*c4));
                chance[1]=c2+((1/3.0)*(0.25*c4));
                chance[2]=c3+((1/3.0)*(0.25*c4));
                chance[3]=1-chance[0]-chance[1]-chance[3];
                break;
            case 5:
                //deadLine=0.25;
                chance[0]=c1;
                chance[1]=c2;
                chance[2]=c3;
                chance[3]=c4;
                break;
            default:
                break;
        }
        Log.i("test1",c1+"/"+c2+"/"+c3+"/"+c4+"/");
        Log.i("test2",c1/chance[0]+"/"+c2/chance[1]+"/"+c3/chance[2]+"/"+c4/chance[3]+"/");
    }

    public void insertgoodbad(ArrayList<Song> S){//test complete
        Random r=new Random();
        for(int i=0;i<S.size();i++){
            if(S.get(i).getGoodbad()==-1) {
                S.get(i).NumberNeuron=4;
                sort4.add(S.get(i));
                S.remove(S.get(i));
            }
            else if(S.get(i).getGoodbad()==1&&r.nextInt(5)==1) {
                S.get(i).NumberNeuron=1;
                sort1.add(S.get(i));
                S.remove(S.get(i));
            }
        }
    }

    public void insertsort1(ArrayList<Song> S,ArrayList<TimeStampObject> T){
        ArrayList<TimeStampObject> temp=new ArrayList<>();

        for(int i=0;i<T.size();i++){
            for(int j=0;j<S.size();j++){
                if(T.get(i).same(S.get(j).getTitle(),S.get(j).getArtist())) {
                    temp.add(T.get(i));
                    break;
                }
            }
        }

        for(int i=0;i<temp.size()*deadLine;i++){
            for(int j=0;j<S.size();j++){
                if(temp.get(i).same(S.get(j).getTitle(),S.get(j).getArtist())){
                    S.get(j).NumberNeuron=1;
                    sort1.add(S.get(j));
                    S.remove(S.get(j));
                    break;
                }
            }
        }
    }
    public void insertsort2(ArrayList<Song> S){
        Song[] temp=new Song[S.size()+1];
        Song tempo;
        for(int i=0;i<S.size();i++){
            temp[i]=S.get(i);
        }

        for(int i=0;i<S.size();i++){
            for(int j=i+1;j<S.size();j++){
                if(temp[i].getRuntime()<=temp[j].getRuntime()){
                    tempo=temp[i];
                    temp[i]=temp[j];
                    temp[j]=tempo;
                }
            }
        }
        for(int i=0;i<S.size()*deadLine;i++) {
            temp[i].NumberNeuron=2;
            sort2.add(temp[i]);
            S.remove(temp[i]);
        }
    }

    public void insertsort3(ArrayList<Song> S){
        Song[] temp=new Song[S.size()+1];
        Song tempo;
        for(int i=0;i<S.size();i++){
            temp[i]=S.get(i);
        }

        for(int i=0;i<S.size();i++){
            for(int j=i+1;j<S.size();j++){
                if(temp[i].getAddDate()<=temp[j].getAddDate()){
                    tempo=temp[i];
                    temp[i]=temp[j];
                    temp[j]=tempo;
                }
            }
        }

        for(int i=0;i<S.size()*deadLine;i++) {
            temp[i].NumberNeuron=3;
            sort3.add(temp[i]);
            S.remove(temp[i]);
        }
    }

    public void insertsort4(ArrayList<Song> S){
        for(int i=0;i<S.size();i++) {
            S.get(i).NumberNeuron=4;
            sort4.add(S.get(i));
        }
    }

    public Song getResult(){
        Random r=new Random();
        double dice=r.nextDouble();
        Log.i(dice+"/","testdice");
        if(sort1.size()==0)
            sort1.add(ApplicationClass.songs.get(0));


        if(sort2.size()==0)
            sort2.add(ApplicationClass.songs.get(0));


        if(sort3.size()==0)
            sort3.add(ApplicationClass.songs.get(0));

        if(sort4.size()==0){
            for(int i=0;i<ApplicationClass.songs.size();i++)
                sort4.add(ApplicationClass.songs.get(i));
        }

        if(dice<chance[0])
            return sort1.get(r.nextInt(sort1.size()));
        else if(dice<chance[0]+chance[1])
            return sort2.get(r.nextInt(sort2.size()));
        else if(dice<chance[0]+chance[1]+chance[2])
            return sort3.get(r.nextInt(sort3.size()));
        else
            return sort4.get(r.nextInt(sort4.size()));
    }

    public ArrayList<Song> getFullResult(){
        ArrayList<Song> A=new ArrayList<>();

        for(int i=0;i<sort1.size();i++)
            A.add(sort1.get(i));

        for(int i=0;i<sort2.size();i++)
            A.add(sort2.get(i));

        for(int i=0;i<sort3.size();i++)
            A.add(sort3.get(i));

        for(int i=0;i<sort4.size();i++)
            A.add(sort4.get(i));

            return A;
    }
}
