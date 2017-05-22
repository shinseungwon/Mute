package com.shinsw.seungwon.mute;

import android.Manifest;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {//곡 리스트를 보여주는 메인 액티비티

    private ArrayList<IndexItem> titleIndex;
    private ArrayList<IndexItem> artistIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);


        setContentView(R.layout.activity_main);

        findViewById(R.id.mainactivity_title).setSelected(true);
        findViewById(R.id.mainactivity_artist).setSelected(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        findViewById(R.id.relativeLayout).bringToFront();

        RelativeLayout rl=(RelativeLayout)findViewById(R.id.relativeLayout);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ControlActivity.class);
                startActivity(i);
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent i = new Intent(MainActivity.this, CoreService.class);
                i.setAction(Actions.command.clustering);
                startService(i);
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                findViewById(R.id.mainactivity_title).setSelected(true);
                findViewById(R.id.mainactivity_artist).setSelected(true);

                return true;
            }
        });

        TabHost tabHost = (TabHost) findViewById(R.id.mainactivity_tabhost);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("Title").setContent(R.id.mainactivity_titlelayout).setIndicator("Title");
        TabHost.TabSpec spec2 = tabHost.newTabSpec("Artist").setContent(R.id.mainactivity_artistlayout).setIndicator("Artist");

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);

        ImageView iv1=(ImageView)findViewById(R.id.mainactivity_albumart);
        ImageView iv2=(ImageView)findViewById(R.id.mainactivity_play);
        ImageView iv3=(ImageView)findViewById(R.id.mainactivity_prev);
        ImageView iv4=(ImageView)findViewById(R.id.mainactivity_next);
        RelativeLayout r=(RelativeLayout)findViewById(R.id.loadingPanel);
        r.setVisibility(View.INVISIBLE);
        changeStatus();

        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ApplicationClass.nowplaying != null) {
                    Intent i = new Intent(MainActivity.this, DetailActivity.class);
                    startActivity(i);

                }
            }
        });

        iv1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (ApplicationClass.nowplaying == null||CoreService.C == null)
                    return false;

                Intent i = new Intent(MainActivity.this, ClusterActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

                return true;
            }
        });

        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ApplicationClass.nowplaying == null)
                    return;
                Intent i = new Intent(MainActivity.this, MusicPlayBack.class);
                i.setAction(Actions.command.playpause);
                startService(i);
            }
        });

        iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ApplicationClass.nowplaying == null)
                    return;
                Intent i = new Intent(MainActivity.this, MusicPlayBack.class);
                i.setAction(Actions.command.prev);
                startService(i);
            }
        });

        iv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ApplicationClass.nowplaying == null)
                    return;
                Intent i = new Intent(MainActivity.this, MusicPlayBack.class);
                i.setAction(Actions.command.next);
                startService(i);
            }
        });

        View indext = findViewById(R.id.mainactivity_titleindex);
        indext.setOnTouchListener(new View.OnTouchListener() {

            GestureDetectorCompat mDetector = new GestureDetectorCompat(MainActivity.this, new gesturedector(0));
            TextView t1 = (TextView) findViewById(R.id.mainactivity_index);

            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
                // ... Respond to touch events
                if (event.getAction() == MotionEvent.ACTION_UP)
                    t1.setText("  ");
                return true;
            }
        });

        View indexa = findViewById(R.id.mainactivity_artistindex);
        indexa.setOnTouchListener(new View.OnTouchListener() {
            GestureDetectorCompat mDetector = new GestureDetectorCompat(MainActivity.this, new gesturedector(1));
            TextView t1 = (TextView) findViewById(R.id.mainactivity_index);

            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
                // ... Respond to touch events
                if (event.getAction() == MotionEvent.ACTION_UP)
                    t1.setText("  ");
                return true;
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if(CoreService.running==1) {
                        if (checkPermission()) {
                            sendBroadcast(new Intent(Actions.command.permission));
                        } else {
                            requestPermission();
                        }
                        break;
                    }
                }
            }
        }).start();
    }



    private void changeStatus(){
        if(ApplicationClass.nowplaying!=null) {
            ImageView iv1=(ImageView)findViewById(R.id.mainactivity_albumart);
            Bitmap b=ApplicationClass.getArtworkQuick(MainActivity.this, Integer.parseInt
                    (ApplicationClass.nowplaying.getAlbumArtId()), 100, 100);
            if(b!=null)
                iv1.setImageBitmap(b);
            else
                iv1.setImageResource(R.mipmap.default_albumart);
            TextView title = (TextView) findViewById(R.id.mainactivity_title);
            TextView artist = (TextView) findViewById(R.id.mainactivity_artist);
            title.setText(ApplicationClass.nowplaying.getTitle());
            artist.setText(ApplicationClass.nowplaying.getArtist());
        }

        ImageView iv2=(ImageView)findViewById(R.id.mainactivity_play);
        if(MusicPlayBack.mMediaPlayer.isPlaying())
            iv2.setImageResource(R.mipmap.mainactivity_pause);
        else
            iv2.setImageResource(R.mipmap.mainactivity_play);
    }



    class gesturedector implements GestureDetector.OnGestureListener{
        ListView L;
        int mode;
        gesturedector(int k){
            mode=k;
        }
        @Override
        public boolean onDown(MotionEvent event) {
            TextView t1=(TextView)findViewById(R.id.mainactivity_index);
            LinearLayout L1=(LinearLayout)findViewById(R.id.mainactivity_titleindex);
            int size;
            if(mode==0) {
                size=L1.getHeight()/titleIndex.size();
                L = (ListView) findViewById(R.id.mainactivity_titlelist);
                for (int i = 0; i < titleIndex.size(); i++) {
                    if (size * i < event.getY() && size * (i + 1) > event.getY()) {

                        L.setSelection(titleIndex.get(i).getScrollposition());
                        t1.setText(titleIndex.get(i).getCharacter());
                    }
                }
            }else{
                size=L1.getHeight()/artistIndex.size();
                L = (ListView) findViewById(R.id.mainactivity_artistlist);
                for (int i = 0; i < artistIndex.size(); i++) {
                    if (size * i < event.getY() && size * (i + 1) > event.getY()) {

                        L.setSelection(artistIndex.get(i).getScrollposition());
                        t1.setText(artistIndex.get(i).getCharacter());
                    }
                }
            }
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
                                float distanceY) {
            TextView t1=(TextView)findViewById(R.id.mainactivity_index);
            LinearLayout L1=(LinearLayout)findViewById(R.id.mainactivity_titleindex);
            int size;
            if(mode==0) {
                size=L1.getHeight()/titleIndex.size();
                L = (ListView) findViewById(R.id.mainactivity_titlelist);
                for (int i = 0; i < titleIndex.size(); i++) {
                    if (size * i < e2.getY() && size * (i + 1) > e2.getY()) {
                        L.setSelection(titleIndex.get(i).getScrollposition());
                        t1.setText(titleIndex.get(i).getCharacter());
                    }
                }
            }else{
                size=L1.getHeight()/artistIndex.size();
                L = (ListView) findViewById(R.id.mainactivity_artistlist);
                for (int i = 0; i < artistIndex.size(); i++) {
                    if (size * i < e2.getY() && size * (i + 1) > e2.getY()) {

                        L.setSelection(artistIndex.get(i).getScrollposition());
                        t1.setText(artistIndex.get(i).getCharacter());
                    }
                }
            }
            return true;
        }

        @Override
        public void onShowPress(MotionEvent event) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {

            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent i;
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_recommend) {
            i=new Intent(this,RecommendActivity.class);
            startActivity(i);
            return true;
        }
        else if (id == R.id.action_settings) {
            i=new Intent(this,SettingActivity.class);
            startActivity(i);

            return true;
        }
        else if (id == R.id.action_about) {
            i=new Intent(this,AboutActivity.class);
            startActivity(i);
            return true;
        }
        else if (id == R.id.action_search) {
            i = new Intent(this, SearchActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            return true;
        }
        else if (id == R.id.action_billboard) {
            i = new Intent(this, BillboardActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onResume(){
        registerReceiver(loadcompletebroadcast, new IntentFilter(Actions.command.loadcomplete));
        registerReceiver(changestatusbroadcast, new IntentFilter(Actions.command.refreshmain));
        registerReceiver(clusteringcompletebroadcast, new IntentFilter(Actions.command.clusteringcomplete));
        registerReceiver(clusteringstartbroadcast, new IntentFilter(Actions.command.clusteringstart));
        registerReceiver(bitmapbroadcast, new IntentFilter(Actions.command.bld));
        registerReceiver(b, new IntentFilter(Actions.command.networktoast));
        super.onResume();
    }

    @Override
    public void onDestroy(){
        unregisterReceiver(loadcompletebroadcast);
        unregisterReceiver(changestatusbroadcast);
        unregisterReceiver(clusteringcompletebroadcast);
        unregisterReceiver(clusteringstartbroadcast);
        unregisterReceiver(bitmapbroadcast);
        unregisterReceiver(b);
        super.onDestroy();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    sendBroadcast(new Intent(Actions.command.permission));
                    // permission was granted, yay! do the
                    // calendar task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }

    private BroadcastReceiver loadcompletebroadcast = new BroadcastReceiver() {
        TitleAdapter titleadapter;
        ArtistAdapter artistadapter;
        @Override
        public void onReceive(Context ctxt, Intent i) {

            ListView tList=(ListView)findViewById(R.id.mainactivity_titlelist);
            ListView aList=(ListView)findViewById(R.id.mainactivity_artistlist);
            titleadapter=new TitleAdapter(MainActivity.this);
            artistadapter=new ArtistAdapter(MainActivity.this);

            tList.setAdapter(titleadapter);
            aList.setAdapter(artistadapter);

            aList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(MainActivity.this, ArtistActivity.class);
                    i.setAction(Actions.command.frommain);
                    i.putExtra("position", position);
                    startActivity(i);
                }
            });
            tList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ApplicationClass.nowplaying = (Song) titleadapter.getItem(position);
                    Intent i = new Intent(MainActivity.this, MusicPlayBack.class);
                    i.setAction(Actions.command.clicksong);
                    startService(i);
                    findViewById(R.id.mainactivity_title).setSelected(true);
                    findViewById(R.id.mainactivity_artist).setSelected(true);
                }
            });
            tList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    ApplicationClass.longclicked = (Song) titleadapter.getItem(position);
                    ApplicationClass.nowplaying = (Song) titleadapter.getItem(position);
                    Intent i = new Intent(MainActivity.this, CoreService.class);
                    i.setAction(Actions.command.clustering);
                    startService(i);
                    findViewById(R.id.mainactivity_title).setSelected(true);
                    findViewById(R.id.mainactivity_artist).setSelected(true);
                    return true;
                }
            });
            List<String> ALPHABETICAL_LIST_T =titleadapter.getIndex();
            LinearLayout v1=(LinearLayout)findViewById(R.id.mainactivity_titleindex);
            titleIndex=getIndexList(ALPHABETICAL_LIST_T);
            displayIndex(v1, titleIndex);
            List<String> ALPHABETICAL_LIST_A =artistadapter.getIndex();
            LinearLayout v2=(LinearLayout)findViewById(R.id.mainactivity_artistindex);
            artistIndex=getIndexList(ALPHABETICAL_LIST_A);
            displayIndex(v2, artistIndex);

            Intent a=new Intent(MainActivity.this,CoreService.class);
            a.setAction(Actions.command.network);
            startService(a);
        }
    };



    public ArrayList<IndexItem> getIndexList(List<String> lists) {
        LinkedHashMap<String, Integer> mapIndex;
        mapIndex = new LinkedHashMap<>();
        ArrayList<IndexItem> items=new ArrayList<>();
        if(Locale.getDefault().getLanguage().equals("en")) {
            for (int i = 0; i < lists.size(); i++) {
                String list = lists.get(i);
                String index = list.substring(0, 1);
                char x = index.charAt(0);
                if ((int) x < 65 || ((int) x > 90 && (int) x < 97) || ((int) x > 122 && (int) x < 128))
                    index = "#";
                else if ((int) x >= 128)
                    index = "@";
                else if ((int) x > 96 && (int) x < 123) {
                    x = (char) ((int) x - 32);
                    index = String.valueOf(x);
                }
                if (mapIndex.get(index) == null) {
                    mapIndex.put(index, i);
                    items.add(new IndexItem(i, index));
                }
            }
        }else{
            int k=lists.size()/20;
            if(k==0)
                k=1;
            for (int i = 0; i < lists.size(); i++) {
                if(i%k==0) {
                    String list = lists.get(i);
                    String index = list.substring(0, 1);
                    char x = index.charAt(0);
                    if ((int) x < 65 || ((int) x > 90 && (int) x < 97) || ((int) x > 122 && (int) x < 128))
                        index = "#";

                    if (mapIndex.get(index) == null) {
                        mapIndex.put(index, i);
                        items.add(new IndexItem(i, index));
                    }
                }
            }
        }
        return items;
    }

    private void displayIndex(LinearLayout L,ArrayList<IndexItem> mapIndex) {

        TextView textView;
        LinearLayout L2=(LinearLayout)findViewById(R.id.mainactivity_titleindex);
        ArrayList<String> indexList = new ArrayList<>();
        for(int i=0;i<mapIndex.size();i++){
            indexList.add(mapIndex.get(i).getCharacter());
        }

        for (String index : indexList) {
            textView = (TextView) getLayoutInflater().inflate(
                    R.layout.side_index_item, null);
            //textView.setPadding(5,0,0,5);
            textView.setHeight(L2.getHeight() / mapIndex.size());
            textView.setText(index);
            L.addView(textView);
        }
    }

    private BroadcastReceiver changestatusbroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent i) {
            changeStatus();
        }
    };

    private BroadcastReceiver clusteringcompletebroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent i) {
            RelativeLayout r=(RelativeLayout)findViewById(R.id.loadingPanel);
            r.setVisibility(View.INVISIBLE);
        }
    };

    private BroadcastReceiver clusteringstartbroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent i) {
            RelativeLayout r=(RelativeLayout)findViewById(R.id.loadingPanel);
            r.setVisibility(View.VISIBLE);
        }
    };

    private BroadcastReceiver bitmapbroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent i) {

            if((int)i.getExtras().get("Type")==0){
                Song s= ApplicationClass.songs.get((int) i.getExtras().get("Value"));
                if(s.getAlbumArt()!=null)
                    s.v.setImageBitmap(s.getAlbumArt());
                else
                    s.v.setImageResource(R.mipmap.default_albumart);
            }

            else if((int)i.getExtras().get("Type")==1){
                Artist a= ApplicationClass.artists.get((int) i.getExtras().get("Value"));
                if(a.albumart!=null)
                    a.v.setImageBitmap(a.albumart);
                else
                    a.v.setImageResource(R.mipmap.default_artist);
            }
        }
    };

    BroadcastReceiver b=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(SettingActivity.this,CoreService.results+" Songs Updated",Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this,intent.getExtras().getString("tag"),Toast.LENGTH_SHORT).show();
        }
    };
}


