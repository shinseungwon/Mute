package com.shinsw.seungwon.mute;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class DetailActivity extends AppCompatActivity {//한 곡의 자세한 정보를 보여주는 액티비티
    static int onactive=0;
    static Bitmap b;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onactive=1;
        setContentView(R.layout.activity_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        mAdView = (AdView) findViewById(R.id.ad_view2);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("409BDEF01A79416024CB59A96CB3C21F")
                .build();
        mAdView.loadAd(adRequest);

        ImageView iv1=(ImageView)findViewById(R.id.detailactivity_albumart);
        ImageView iv2=(ImageView)findViewById(R.id.detailactivity_play);
        ImageView iv3=(ImageView)findViewById(R.id.detailactivity_prev);
        ImageView iv4=(ImageView)findViewById(R.id.detailactivity_next);
        ImageView iv5=(ImageView)findViewById(R.id.detailactivity_playtype);
        TextView title=(TextView)findViewById(R.id.detail_title);
        TextView artist=(TextView)findViewById(R.id.detail_artist);
        title.setText(ApplicationClass.nowplaying.getTitle());
        artist.setText(ApplicationClass.nowplaying.getArtist());
        title.setSelected(true);
        artist.setSelected(true);

        b=ApplicationClass.getArtworkQuick(DetailActivity.this, Integer.parseInt
                (ApplicationClass.nowplaying.getAlbumArtId()), 250, 250);
        if(b!=null)
            iv1.setImageBitmap(b);
        else
            iv1.setImageResource(R.mipmap.default_albumart);

        ImageView mode =(ImageView)findViewById(R.id.detailactivity_mode);
        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(DetailActivity.this,ModeSettingActivity.class);
                startActivity(i);
            }
        });

        if(MusicPlayBack.mMediaPlayer.isPlaying())
            iv2.setImageResource(R.mipmap.detailactivity_pause);
        else
            iv2.setImageResource(R.mipmap.detailactivity_play);

        if(ApplicationClass.nowplaying.getGoodbad()==1)
            iv5.setImageResource(R.mipmap.detailactivity_good);
        else if(ApplicationClass.nowplaying.getGoodbad()==-1)
            iv5.setImageResource(R.mipmap.detailactivity_bad);
        else
            iv5.setImageBitmap(null);

        if(MusicPlayBack.loop)
            iv5.setImageResource(R.mipmap.repeatone);
        else
            iv5.setImageResource(R.mipmap.repeatall);

        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                String keyword = ApplicationClass.nowplaying.getArtist() +
                        " " + ApplicationClass.nowplaying.getTitle();
                intent.putExtra(SearchManager.QUERY, keyword);
                startActivity(intent);
            }
        });
        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(DetailActivity.this,MusicPlayBack.class);
                i.setAction(Actions.command.playpause);
                startService(i);
            }
        });

        iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this, MusicPlayBack.class);
                i.setAction(Actions.command.prev);
                startService(i);
            }
        });

        iv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(DetailActivity.this,MusicPlayBack.class);
                i.setAction(Actions.command.next);
                startService(i);
            }
        });

        iv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this, MusicPlayBack.class);
                i.setAction(Actions.command.repeat);
                startService(i);

            }
        });

        SeekBar seekbar;
        seekbar = (SeekBar) findViewById(R.id.detailactivity_seekbar);
        seekbar.setProgress(MusicPlayBack.mMediaPlayer.getCurrentPosition());
        TextView Timer;
        Timer = (TextView) findViewById(R.id.detailactivity_timer);
        Timer.setText(getFormalTime(MusicPlayBack.mMediaPlayer.getCurrentPosition()));
        TextView Playtime;
        Playtime = (TextView) findViewById(R.id.detailactivity_runtime);
        seekbar.setMax(MusicPlayBack.mMediaPlayer.getDuration());
        Playtime.setText(getFormalTime(MusicPlayBack.mMediaPlayer.getDuration()));
        ImageView goodview=(ImageView)findViewById(R.id.detailactivity_good);
        ImageView badview=(ImageView)findViewById(R.id.detailactivity_bad);

        goodview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(DetailActivity.this,MusicPlayBack.class);
                i.setAction(Actions.command.good);
                startService(i);
            }
        });

        badview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(DetailActivity.this,MusicPlayBack.class);
                i.setAction(Actions.command.bad);
                startService(i);
            }
        });

        if(ApplicationClass.nowplaying.getGoodbad()==1){
            goodview.setImageResource(R.mipmap.detailactivity_goodactive);
            badview.setImageResource(R.mipmap.detailactivity_bad);
        }else if(ApplicationClass.nowplaying.getGoodbad()==-1){
            goodview.setImageResource(R.mipmap.detailactivity_good);
            badview.setImageResource(R.mipmap.detailactivity_badactive);
        }else{
            goodview.setImageResource(R.mipmap.detailactivity_good);
            badview.setImageResource(R.mipmap.detailactivity_bad);
        }

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            TextView Timer = (TextView) findViewById(R.id.detailactivity_timer);

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                MusicPlayBack.mMediaPlayer.start();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                MusicPlayBack.mMediaPlayer.pause();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (fromUser) {
                    MusicPlayBack.mMediaPlayer.seekTo(seekBar.getProgress());
                    Timer.setText(getFormalTime(MusicPlayBack.mMediaPlayer.getCurrentPosition()));
                }
            }
        });
    }

    private BroadcastReceiver changestatusbroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent i) {

            if(DetailActivity.onactive==1) {

                TextView title=(TextView)findViewById(R.id.detail_title);
                TextView artist=(TextView)findViewById(R.id.detail_artist);
                title.setText(ApplicationClass.nowplaying.getTitle());
                artist.setText(ApplicationClass.nowplaying.getArtist());

                ImageView iv = (ImageView) findViewById(R.id.detailactivity_albumart);
                SeekBar seekbar = (SeekBar) findViewById(R.id.detailactivity_seekbar);
                TextView Playtime = (TextView) findViewById(R.id.detailactivity_runtime);


                ImageView iv2 = (ImageView) findViewById(R.id.detailactivity_play);

                ImageView iv5 = (ImageView) findViewById(R.id.detailactivity_playtype);

                ImageView goodview = (ImageView) findViewById(R.id.detailactivity_good);
                ImageView badview = (ImageView) findViewById(R.id.detailactivity_bad);

                 b = ApplicationClass.getArtworkQuick(DetailActivity.this, Integer.parseInt
                        (ApplicationClass.nowplaying.getAlbumArtId()), 200, 200);
                if (b != null)
                    iv.setImageBitmap(b);
                else
                    iv.setImageResource(R.mipmap.default_albumart);

                seekbar.setMax(MusicPlayBack.mMediaPlayer.getDuration());
                Playtime.setText(getFormalTime(MusicPlayBack.mMediaPlayer.getDuration()));

                if (ApplicationClass.nowplaying.getGoodbad() == 1) {
                    goodview.setImageResource(R.mipmap.detailactivity_goodactive);
                    badview.setImageResource(R.mipmap.detailactivity_bad);
                } else if (ApplicationClass.nowplaying.getGoodbad() == -1) {
                    goodview.setImageResource(R.mipmap.detailactivity_good);
                    badview.setImageResource(R.mipmap.detailactivity_badactive);
                } else {
                    goodview.setImageResource(R.mipmap.detailactivity_good);
                    badview.setImageResource(R.mipmap.detailactivity_bad);
                }

                if (MusicPlayBack.mMediaPlayer.isPlaying())
                    iv2.setImageResource(R.mipmap.detailactivity_pause);
                else
                    iv2.setImageResource(R.mipmap.detailactivity_play);

                if (ApplicationClass.nowplaying.getGoodbad() == 1)
                    iv5.setImageResource(R.mipmap.detailactivity_good);
                else if (ApplicationClass.nowplaying.getGoodbad() == -1)
                    iv5.setImageResource(R.mipmap.detailactivity_bad);
                else
                    iv5.setImageBitmap(null);

                if (MusicPlayBack.loop)
                    iv5.setImageResource(R.mipmap.repeatone);
                else
                    iv5.setImageResource(R.mipmap.repeatall);

                getSupportActionBar().setTitle(ApplicationClass.nowplaying.getTitle() + " - " + ApplicationClass.nowplaying.getArtist());
            }
        }

    };

    @Override
    public void onPause(){
        onactive=0;
        super.onPause();
        if(b!=null)
        b.recycle();
        if (mAdView != null) {
            mAdView.pause();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        onactive=1;
        new Thread(new Runnable() {
            Handler handler = new Handler();
            public void run() {

                while (true) {
                    if(onactive==0)
                        return;
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {}
                    handler.post(new Runnable() {
                        TextView Timer = (TextView) findViewById(R.id.detailactivity_timer);
                        //SeekBar seekbar = (SeekBar) findViewById(R.id.detailactivity_seekbar);
                        public void run() {
                            Timer.setText(getFormalTime(MusicPlayBack.mMediaPlayer.getCurrentPosition()));
                            //seekbar.setProgress(MusicPlayBack.mMediaPlayer.getCurrentPosition());
                        }
                    });
                }
            }
        }).start();


        new Thread(new Runnable() {
            Handler handler = new Handler();
            public void run() {
                while (true) {
                    if(onactive==0)
                        return;
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                    }
                    handler.post(new Runnable() {
                        //TextView Timer = (TextView) findViewById(R.id.detailactivity_timer);
                        SeekBar seekbar = (SeekBar) findViewById(R.id.detailactivity_seekbar);
                        public void run() {
                            //Timer.setText(getFormalTime(MusicPlayBack.mMediaPlayer.getCurrentPosition()));
                            seekbar.setProgress(MusicPlayBack.mMediaPlayer.getCurrentPosition());
                        }
                    });
                }
            }
        }).start();

        registerReceiver(changestatusbroadcast, new IntentFilter(Actions.command.refreshmain));
        changestatusbroadcast.onReceive(this, new Intent(Actions.command.refreshmain));
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(changestatusbroadcast);
        onactive=0;
        b=null;
        if (mAdView != null) {
            mAdView.destroy();
        }
    }

    private String getFormalTime(int x) {
        int min;
        int sec;
        String temp_sec;
        min = x / 1000 / 60;
        sec = (x / 1000) - (min * 60);

        if (sec < 10)
            temp_sec = "0" + sec;
        else
            temp_sec = sec + "";

        return min + ":" + temp_sec;
    }

}
