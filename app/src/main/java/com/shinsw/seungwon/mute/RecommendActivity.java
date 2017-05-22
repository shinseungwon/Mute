package com.shinsw.seungwon.mute;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

public class RecommendActivity extends AppCompatActivity {//외부 차트에서 신곡정보를 가져와 카드 형식으로 보여주는 액티비티
    RecommendAdapter r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle("Recommend");
        GridView g=(GridView)findViewById(R.id.gridView1);
        r=new RecommendAdapter(this);
        g.setAdapter(r);
        g.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(RecommendActivity.this, ArtistActivity.class);
                i.setAction(Actions.command.frommain);
                i.putExtra("position", r.getRealPosition(position));
                startActivity(i);
            }
        });

        g.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                String keyword = r.getIndex().get(position).getName();
                intent.putExtra(SearchManager.QUERY, keyword);
                startActivity(intent);
                return false;
            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(bitmapbroadcast, new IntentFilter(Actions.command.recommend));
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(bitmapbroadcast);
    }

    private BroadcastReceiver bitmapbroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent i) {

                Artist a= r.getIndex().get((int) i.getExtras().get("Value"));
                a.v.setImageBitmap(a.getAlbumArt());
        }
    };
}
