package com.shinsw.seungwon.mute;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TabHost;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ControlActivity extends AppCompatActivity {//메인액티비티 우하단 버튼을 누르면 나오는
                                                           //곡 모드선택 액티비티

    int vibrant;
    int calm;
    int beat;
    int rock;
    private TabHost tabHost;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle("Mode");


        tabHost = (TabHost) findViewById(R.id.controlTabHost);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("Quick").setContent(R.id.controllayout1).setIndicator("Quick");
        TabHost.TabSpec spec2 = tabHost.newTabSpec("Manually").setContent(R.id.controllayout2).setIndicator("Manually");

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);

        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        mAdView = (AdView) findViewById(R.id.ad_view);

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("409BDEF01A79416024CB59A96CB3C21F")
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);


        ImageView mute=(ImageView)findViewById(R.id.board);


        mute.setOnTouchListener(new View.OnTouchListener() {
            GestureDetectorCompat mDetector = new GestureDetectorCompat(ControlActivity.this, new gesturedector());

            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
                // ... Respond to touch events
                return true;
            }
        });

        ImageView v=(ImageView)findViewById(R.id.c_ratingok);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent(Actions.command.clusteringstart));
                int vb, cb, bb, rb;
                if(tabHost.getCurrentTab()==0) {
                    vb = vibrant;
                    cb = calm;
                    bb = beat;
                    rb = rock;
                }else{
                    SeekBar s1=(SeekBar)findViewById(R.id.c_vibrantbar);
                    SeekBar s2=(SeekBar)findViewById(R.id.c_calmbar);
                    SeekBar s3=(SeekBar)findViewById(R.id.c_beatbar);
                    SeekBar s4=(SeekBar)findViewById(R.id.c_rockbar);
                    int sum=s1.getProgress()+s2.getProgress()+s3.getProgress()+s4.getProgress();
                    vb=(s1.getProgress()*100)/sum;
                    cb=(s2.getProgress()*100)/sum;
                    bb=(s3.getProgress()*100)/sum;
                    rb=100-vb-cb-bb;
                }

                ApplicationClass.user.setAttribute(vb, cb, bb, rb);
                Intent i = new Intent(ControlActivity.this, CoreService.class);
                i.setAction(Actions.command.clustering);
                startService(i);

                    SharedPreferences shared = getSharedPreferences("user", MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putInt("vibrant", vb);
                    editor.putInt("calm", cb);
                    editor.putInt("beat", bb);
                    editor.putInt("rock", rb);
                    editor.commit();

                finish();

            }
        });
    }

    class gesturedector implements GestureDetector.OnGestureListener{
        final ImageView v;
        final int marksize=0;
        final int marksizeend=150;
        int a,b;
        LayoutParams params;
        gesturedector(){
            v=(ImageView)findViewById(R.id.board);

        }
        @Override
        public boolean onDown(MotionEvent event) {
            int sum;

            a=((int)event.getX()*100)/v.getWidth();
            b=((int)event.getY()*100)/v.getHeight();
            if(a<0||a>100||b<0||b>100)
                return false;

            sum=10000;//맞은편 사각형 부피구함
            vibrant=a*(100-b);
            calm=(100-a)*(100-b);
            beat=(100-a)*b;
            rock=a*b;
            vibrant=(vibrant*100)/sum;
            calm=(calm*100)/sum;
            beat=(beat*100)/sum;
            rock=100-vibrant-calm-beat;
            ImageView image=(ImageView)findViewById(R.id.aim);
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);


            if(event.getX()<marksize)
                params.leftMargin=marksize;
            else if(event.getX()>marksize-10&&event.getX()<v.getWidth()-marksize-marksizeend)
                params.leftMargin=(int)event.getX();
            else
                params.leftMargin=v.getWidth()-marksize-marksizeend;

            if(event.getY()<marksize)
                params.topMargin=marksize;
            else if(event.getY()>marksize&&event.getY()<v.getHeight()-marksize-marksizeend)
                params.topMargin=(int)event.getY();
            else
                params.topMargin=v.getHeight()-marksize-marksizeend;
            image.setLayoutParams(params);



            SharedPreferences shared = getSharedPreferences("user", MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putInt("leftmargin",params.leftMargin);
            editor.putInt("topmargin",params.topMargin);
            editor.commit();

            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {

        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                float distanceY) {//스크롤 되게
            int sum;

            a=((int)e2.getX()*100)/v.getWidth();
            b=((int)e2.getY()*100)/v.getHeight();
            if(a<0||a>100||b<0||b>100)
                return false;
            sum=10000;//맞은편 사각형 부피구함
            vibrant=a*(100-b);
            calm=(100-a)*(100-b);
            beat=(100-a)*b;
            rock=a*b;
            vibrant=(vibrant*100)/sum;
            calm=(calm*100)/sum;
            beat=(beat*100)/sum;
            rock=100-vibrant-calm-beat;
            ImageView image=(ImageView)findViewById(R.id.aim);
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            if(e2.getX()<marksize)
                params.leftMargin=marksize;
            else if(e2.getX()>marksize-10&&e2.getX()<v.getWidth()-marksize-marksizeend)
                params.leftMargin=(int)e2.getX();
            else
                params.leftMargin=v.getWidth()-marksize-marksizeend;

            if(e2.getY()<marksize)
                params.topMargin=marksize;
            else if(e2.getY()>marksize&&e2.getY()<v.getHeight()-marksize-marksizeend)
                params.topMargin=(int)e2.getY();
            else
                params.topMargin=v.getHeight()-marksize-marksizeend;
            image.setLayoutParams(params);

            SharedPreferences shared = getSharedPreferences("user", MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putInt("leftmargin",params.leftMargin);
            editor.putInt("topmargin",params.topMargin);
            editor.commit();

            return true;
        }

        @Override
        public void onShowPress(MotionEvent event) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {//한번 딱 찍으면 되게

            return true;
        }
    }



    @Override
    public void onPause(){
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();

        SeekBar s1=(SeekBar)findViewById(R.id.c_vibrantbar);
        SeekBar s2=(SeekBar)findViewById(R.id.c_calmbar);
        SeekBar s3=(SeekBar)findViewById(R.id.c_beatbar);
        SeekBar s4=(SeekBar)findViewById(R.id.c_rockbar);

        s1.setMax(100);
        s2.setMax(100);
        s3.setMax(100);
        s4.setMax(100);
        s1.setProgress(ApplicationClass.user.vibrant);
        s2.setProgress(ApplicationClass.user.calm);
        s3.setProgress(ApplicationClass.user.beat);
        s4.setProgress(ApplicationClass.user.rock);


        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ImageView image=(ImageView)findViewById(R.id.aim);
        SharedPreferences shared = getSharedPreferences("user", MODE_PRIVATE);
        params.leftMargin=shared.getInt("leftmargin",0);
        params.topMargin=shared.getInt("topmargin",0);
        Log.i(params.leftMargin+"/"+params.topMargin,"test");
        image.setLayoutParams(params);
        registerReceiver(br,new IntentFilter(Actions.command.setted));
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy(){
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
        unregisterReceiver(br);
    }
    BroadcastReceiver br=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent i=new Intent(ControlActivity.this,CoreService.class);
            i.setAction(Actions.command.start);
            startService(i);
        }
    };
}
