package com.shinsw.seungwon.mute;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

public class ClusterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle("PlayList");
        ListView v=(ListView)findViewById(R.id.ClusterActivity_List);
        ClusterAdapter A=new ClusterAdapter(this);
        v.setAdapter(A);


        v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ApplicationClass.nowplaying = CoreService.N.getFullResult().get(position);

                Intent i = new Intent(ClusterActivity.this, MusicPlayBack.class);
                i.setAction(Actions.command.clicksong);
                startService(i);
            }
        });



    }
    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(bitmapbroadcast, new IntentFilter(Actions.command.clusterlist));
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(bitmapbroadcast);
    }

    private BroadcastReceiver bitmapbroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent i) {

                Song s= CoreService.N.getFullResult().get((int) i.getExtras().get("Value"));
                if(s.getAlbumArt()!=null)
                    s.v.setImageBitmap(s.getAlbumArt());
                else
                    s.v.setImageResource(R.mipmap.default_albumart);

        }
    };
}
