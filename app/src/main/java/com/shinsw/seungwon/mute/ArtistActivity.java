package com.shinsw.seungwon.mute;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ArtistActivity extends AppCompatActivity {//아티스트정렬로 표시된 리스트에서 아티스트를 누르면 나오는 액티비티
    ArtistTitleAdapter artisttitleadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        int position=getIntent().getExtras().getInt("position");
        getSupportActionBar().setTitle(ApplicationClass.artists.get(position).getName());
        ListView List=(ListView)findViewById(R.id.artistactivity_titlelist);
            artisttitleadapter=new ArtistTitleAdapter(this,position);
            List.setAdapter(artisttitleadapter);

        List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ApplicationClass.nowplaying = (Song) artisttitleadapter.getItem(position);
                Intent i = new Intent(ArtistActivity.this, MusicPlayBack.class);
                i.setAction(Actions.command.clicksong);
                startService(i);
            }
        });

        ImageView iv=(ImageView)findViewById(R.id.artistactivity_albumart);
        iv.setImageBitmap(ApplicationClass.getArtworkQuick(this, ApplicationClass.artists.get(position).getAlbumArtId(), 200, 200));

        ApplicationClass.artists.get(position).getData();

        ImageView v1=(ImageView)findViewById(R.id.avibrant);
        ImageView v2=(ImageView)findViewById(R.id.acalm);
        ImageView v3=(ImageView)findViewById(R.id.abeat);
        ImageView v4=(ImageView)findViewById(R.id.arock);

        v1.getLayoutParams().width = ApplicationClass.artists.get(position).vibrant * 5;
        v2.getLayoutParams().width = ApplicationClass.artists.get(position).calm * 5;
        v3.getLayoutParams().width = ApplicationClass.artists.get(position).beat * 5;
        v4.getLayoutParams().width = ApplicationClass.artists.get(position).rock * 5;
        TextView click=(TextView)findViewById(R.id.aclicktext);
        click.setText(" : "+ApplicationClass.artists.get(position).getClickcount());

        TextView good=(TextView)findViewById(R.id.agoodtext);
        good.setText(" : "+ApplicationClass.artists.get(position).good);

        TextView bad=(TextView)findViewById(R.id.abadtext);
        bad.setText(" : "+ApplicationClass.artists.get(position).bad);

        //TextView t=(TextView)findViewById(R.id.ainfo);
        //t.setText("Good : "+ApplicationClass.artists.get(position).good+"/Bad : "+ApplicationClass.artists.get(position).bad+"\nClickCount : "+ApplicationClass.artists.get(position).getClickcount());

    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
