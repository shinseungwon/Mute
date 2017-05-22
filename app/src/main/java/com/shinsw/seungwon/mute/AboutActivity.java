package com.shinsw.seungwon.mute;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle("About Mute");
        TextView v=(TextView)findViewById(R.id.aboutactivity_textview);
        v.setText("Version : 2.0\n\nCreated By : Shin seung-won\n\nBug Report : \n" );
        TextView v2=(TextView)findViewById(R.id.aboutactivity_textview2);
        v2.setText("ssw900528@gmail.com\n");
        v2.setTextColor(Color.BLUE);
        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("mailto:ssw900528@gmail.com");
                Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(it);
            }
        });

        TextView v3=(TextView)findViewById(R.id.aboutactivity_textview3);
        v3.setText("Click here to visit our website!\n");
        v3.setTextColor(Color.BLUE);

        v3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri u = Uri.parse("http://shin510647.dothome.co.kr/main.htm");
                Intent i = new Intent(Intent.ACTION_VIEW,u);
                startActivity(i);
            }
        });
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
