package com.shinsw.seungwon.mute;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

public class BillboardActivity extends AppCompatActivity {
    BillboardAdapter b;
    ListView viewx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billboard);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle("Billboard Hot 100");
        viewx=(ListView)findViewById(R.id.billboard_listview);
        b=new BillboardAdapter(this);
        viewx.setAdapter(b);

        viewx.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(b.intvec.get(position)==0){

                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                    String keyword = b.artistvec.get(position) +
                            " " + b.titlevec.get(position);
                    intent.putExtra(SearchManager.QUERY, keyword);
                    startActivity(intent);
                }else if(b.intvec.get(position)==1){
                    ImageView v=(ImageView)view.findViewById(R.id.billboarditem_wish);

                    ApplicationClass.nowplaying = (Song) b.songvec.get(position);
                    Intent i = new Intent(BillboardActivity.this, MusicPlayBack.class);
                    i.setAction(Actions.command.clicksong);
                    startService(i);

                    Intent i2=new Intent(BillboardActivity.this,DetailActivity.class);
                    startActivity(i2);
                }
                else{
                    ImageView v=(ImageView)view.findViewById(R.id.billboarditem_wish);

                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                    String keyword = b.artistvec.get(position) +
                            " " + b.titlevec.get(position);
                    intent.putExtra(SearchManager.QUERY, keyword);
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(br,new IntentFilter(Actions.command.frombillboard));
        b=new BillboardAdapter(this);
        viewx.setAdapter(b);

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(br);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_billboard, menu);
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
        /*
        if (id == R.id.action_webpage) {
            Uri u = Uri.parse("http://www.billboard.com/charts/hot-100");
            i = new Intent(Intent.ACTION_VIEW,u);
            startActivity(i);
            return true;
        }
        else */if (id == R.id.action_wishlist) {
            i=new Intent(this,WishListActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    BroadcastReceiver br=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            b=new BillboardAdapter(BillboardActivity.this);
            viewx.setAdapter(b);
        }
    };
}
