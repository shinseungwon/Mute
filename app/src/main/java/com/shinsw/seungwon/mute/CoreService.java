package com.shinsw.seungwon.mute;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class CoreService extends Service {//곡분류 연산을 수행함.
    static int running=0;
    static Clustering C;
    static NeuralNetwork N;
    static int results=0;
    static Song temp;

    public CoreService() {

    }

    @Override
    public void onCreate() {

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(permissionbroadcast, new IntentFilter(Actions.command.permission));
        running=1;

        if(intent.getAction()!=null) {
            if (intent.getAction().equals(Actions.command.getnext)) {
                Thread t=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Song song;
                        while(true){
                            if((song=NeuralNetwork())!=ApplicationClass.nowplaying) {
                                ApplicationClass.next = song;
                                break;
                            }
                        }
                    }
                });
                t.start();
            } else if (intent.getAction().equals(Actions.command.clustering)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendBroadcast(new Intent(Actions.command.clusteringstart));
                        Clustering();
                        N=new NeuralNetwork(C.result.member);
                        if(ApplicationClass.longclicked!=null)
                            ApplicationClass.nowplaying=ApplicationClass.longclicked;
                        else
                            ApplicationClass.nowplaying=NeuralNetwork();
                        Intent i = new Intent(CoreService.this, MusicPlayBack.class);
                        i.setAction(Actions.command.fromcore);
                        startService(i);
                        sendBroadcast(new Intent(Actions.command.clusteringcomplete));
                    }
                }).start();

            } else if(intent.getAction().equals(Actions.command.network)){
                networkProgress();
            }else if(intent.getAction().equals(Actions.command.sendnetwork)){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendNetwork();
                    }
                }).start();
            }else if(intent.getAction().equals(Actions.command.click)){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendClick(temp);
                    }
                }).start();
            }
        }
        return Service.START_NOT_STICKY;
    }

    public Song NeuralNetwork(){
        if(N==null) {
            Random r = new Random();
            return ApplicationClass.songs.get(r.nextInt(ApplicationClass.songs.size()));
        }else
            return N.getResult();
    }
    private void Clustering(){
        SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
        C = new Clustering(ApplicationClass.songs,pref.getInt("cluster",ApplicationClass.songs.size()/2));
        C.doClustering();
        if(ApplicationClass.longclicked!=null)
            C.getUserCluster(ApplicationClass.longclicked.ClusterNumber);
        else
            C.getUserCluster();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        running=0;
        unregisterReceiver(permissionbroadcast);
    }
/*
    public void generateNoteOnSD(String sFileName, ArrayList<Song> song) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            for(int i=0;i<song.size();i++) {
                writer.append(song.get(i).getTitle()+"/"+song.get(i).getArtist()+"/"+song.get(i).vibrant+"/"+song.get(i).calm+"/"
                        +song.get(i).beat+"/"+song.get(i).rock+"@");

            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
    private BroadcastReceiver permissionbroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent i) {
            Log.i("Permission", "Receive");
            ApplicationClass.SQLiteConnector =new SQLiteConnector(CoreService.this);
            ApplicationClass.artistConnector=new ArtistConnector(CoreService.this);
            ApplicationClass.SQLiteConnector.getData();
            ApplicationClass.artistConnector.getData();
            sendBroadcast(new Intent(Actions.command.loadcomplete));
        }
    };

    private void sendClick(Song song){

        String title="default";
        String artist="default";
        boolean k=true;
        try {
             title = java.net.URLEncoder.encode(new String(song.getTitle().getBytes("UTF-8")));
             artist = java.net.URLEncoder.encode(new String(song.getArtist().getBytes("UTF-8")));
        }catch(Exception e){k=false;}

        if(!k)
            return;

        //String link = "http://shin510647.dothome.co.kr/click.php?title="
        //        + song.getTitle()+"&artist="+song.getArtist();
        String link = "http://shin510647.dothome.co.kr/click.php?title="
               + title+"&artist="+artist;

        //url 로 http request 를 보내고 받은 httpresponse 객체에서 문자열을 추출합니다.
        StringBuilder output = new StringBuilder();
        try {
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                int resCode = conn.getResponseCode();
                if (resCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream())) ;
                    String line;
                    while(true) {
                        line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        output.append(line + "\n");
                    }
                    reader.close();
                    conn.disconnect();
                }
            }
        } catch(Exception ex) {
            Log.e("SampleHTTP", "Exception in processing response.", ex);
            ex.printStackTrace();
        }
    }

    private void sendNetwork(){
        StringBuilder output = new StringBuilder();
        Song song=ApplicationClass.nowplaying;
        String title="default";
        String artist="default";
        boolean k=true;
        try {
            title = java.net.URLEncoder.encode(new String(song.getTitle().getBytes("UTF-8")));
            artist = java.net.URLEncoder.encode(new String(song.getArtist().getBytes("UTF-8")));
        }catch(Exception e){k=false;}

        if(!k)
            return;
        String link = "http://shin510647.dothome.co.kr/senddata.php?title="
                +title+"&artist="+artist+"&vibrant="+song.vibrant+"&calm="+song.calm+"&beat="+song.beat+"&rock="+song.rock;

        //StringBuilder output = new StringBuilder();
        try {
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                int resCode = conn.getResponseCode();
                if (resCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream())) ;
                    String line;
                    while(true) {
                        line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        output.append(line + "\n");
                    }
                    Log.i("check",output.toString());
                    reader.close();
                    conn.disconnect();
                }
            }
        } catch(Exception ex) {
            Log.e("SampleHTTP", "Exception in processing response.", ex);
            ex.printStackTrace();
        }
    }

    private void networkProgress(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count=0;
                for(int i=0;i<ApplicationClass.songs.size();i++){
                    if((ApplicationClass.songs.get(i).vibrant==0)&&(ApplicationClass.songs.get(i).calm==0)
                            &&(ApplicationClass.songs.get(i).beat==0)&&(ApplicationClass.songs.get(i).rock==0)){
                        if(getValueFromNetwrok(ApplicationClass.songs.get(i))){
                            //Intent intent=new Intent(Actions.command.networktoast);
                            //intent.putExtra("tag",ApplicationClass.songs.get(i).getTitle()+"/"+ApplicationClass.songs.get(i).getArtist()+" - Genre updated");
                            //sendBroadcast(intent);
                        }
                        else{
                            //Intent intent=new Intent(Actions.command.networktoast);
                            //intent.putExtra("tag",ApplicationClass.songs.get(i).getTitle()+"/"+ApplicationClass.songs.get(i).getArtist()+" - No data in server");
                            //sendBroadcast(intent);
                        }

                        count++;
                        Log.i("count",ApplicationClass.songs.get(i).getTitle());

                    }
                }
                results=count;
            }
        }).start();
    }



    private boolean getValueFromNetwrok(Song song){
        Log.i("Network","count");
        String title=song.getTitle();
        String artist=song.getArtist();
        String link = "http://shin510647.dothome.co.kr/selectdata.php?title="
                + title+"&artist="+artist;

        StringBuilder output = new StringBuilder();
        try {
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                int resCode = conn.getResponseCode();
                if (resCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream())) ;
                    String line = null;
                    while(true) {
                        line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        output.append(line + "\n");
                    }

                    int[] out= Parser.valueParser(output.toString());
                    if(out!=null){
                        Log.i(out[0]+"/"+out[1]+"/"+out[2]+"/"+out[3],"result : "+title+"/~/"+artist);
                        if(out[0]==0&&out[1]==0&&out[2]==0&&out[3]==0)
                            return false;
                        else
                        song.setAttribute(out[3],out[2],out[1],out[0]);
                        ApplicationClass.SQLiteConnector.updateElements(song);
                    }else
                        return false;
                    reader.close();
                    conn.disconnect();
                }
            }
        } catch(Exception ex) {
            Log.e("SampleHTTP", "Exception in processing response.", ex);
            ex.printStackTrace();
        }return true;
    }
}
