package com.shinsw.seungwon.mute;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

public class ModeSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_setting);
        getSupportActionBar().setTitle(ApplicationClass.nowplaying.getTitle() + " - " + ApplicationClass.nowplaying.getArtist());
        final SeekBar vibrantbar=(SeekBar)findViewById(R.id.vibrantbar);
        final SeekBar calmbar=(SeekBar)findViewById(R.id.calmbar);
        final SeekBar beatbar=(SeekBar)findViewById(R.id.beatbar);
        final SeekBar rockbar=(SeekBar)findViewById(R.id.rockbar);
        vibrantbar.setMax(100);
        calmbar.setMax(100);
        beatbar.setMax(100);
        rockbar.setMax(100);
        vibrantbar.setProgress(ApplicationClass.nowplaying.vibrant);
        calmbar.setProgress(ApplicationClass.nowplaying.calm);
        beatbar.setProgress(ApplicationClass.nowplaying.beat);
        rockbar.setProgress(ApplicationClass.nowplaying.rock);

        ImageView v=(ImageView)findViewById(R.id.ratingok);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int vb, cb, bb, rb, s;
                s = rockbar.getProgress()+beatbar.getProgress()+calmbar.getProgress()+vibrantbar.getProgress();
                vb=vibrantbar.getProgress()*100/s;
                cb=calmbar.getProgress()*100/s;
                bb=beatbar.getProgress()*100/s;
                rb=100-vb-cb-bb;
                ApplicationClass.nowplaying.setAttribute(rb,bb,cb,vb);
                ApplicationClass.SQLiteConnector.updateElements(ApplicationClass.nowplaying);
                Intent i=new Intent(ModeSettingActivity.this,CoreService.class);
                i.setAction(Actions.command.sendnetwork);
                startService(i);
                finish();
            }
        });
    }
}
