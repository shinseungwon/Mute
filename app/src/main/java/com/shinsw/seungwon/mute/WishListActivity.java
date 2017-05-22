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
import android.widget.ListView;

public class WishListActivity extends AppCompatActivity {
    WishListAdapter b;
    ListView viewx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle("Your WishList");
        viewx=(ListView)findViewById(R.id.wishlist);

        b=new WishListAdapter(this);
        viewx.setAdapter(b);

        viewx.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                    String keyword = b.W.get(position).artist +
                            " " + b.W.get(position).title;
                    intent.putExtra(SearchManager.QUERY, keyword);
                    startActivity(intent);
            }
        });
    }
    BroadcastReceiver br=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            b=new WishListAdapter(WishListActivity.this);
            viewx.setAdapter(b);
        }
    };
    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(br,new IntentFilter(Actions.command.fromwish));
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(br);
    }
}
