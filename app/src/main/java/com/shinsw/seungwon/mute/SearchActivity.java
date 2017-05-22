package com.shinsw.seungwon.mute;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;

public class SearchActivity extends AppCompatActivity {//곡 검색 액티비티
    static TabHost tabHost;
    static ListView v1;
    static ListView v2;
    private ArtistAdapter artistadapter;
    TitleAdapter titleadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        tabHost = (TabHost) findViewById(R.id.searchactivity_tabhost);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("Title").setContent(R.id.searchactivity_titlelayout).setIndicator("Title");
        TabHost.TabSpec spec2 = tabHost.newTabSpec("Artist").setContent(R.id.searchactivity_artistlayout).setIndicator("Artist");

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

            }
        });

         v1=(ListView)findViewById(R.id.searchactivity_titlesearch);
         v2=(ListView)findViewById(R.id.searchactivity_artistsearch);

        SearchView searchview = (SearchView) findViewById(R.id.searchview);
        searchview.setFocusable(true);
        searchview.setIconified(false);
        searchview.requestFocusFromTouch();
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                artistadapter = new ArtistAdapter(SearchActivity.this, s);
                titleadapter = new TitleAdapter(SearchActivity.this, s);
                v1.setAdapter(titleadapter);
                v2.setAdapter(artistadapter);
                ((TextView) tabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title)).setText("Title (" + v1.getCount() + ")");
                ((TextView) tabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title)).setText("Artist (" + v2.getCount() + ")");
                return false;
            }
        });

        v2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(SearchActivity.this, ArtistActivity.class);
                i.setAction(Actions.command.fromsearch);

                Artist artist = (Artist) artistadapter.getItem(position);
                String name = artist.getName();
                int temp = 0;
                for (int j = 0; j < ApplicationClass.artists.size(); j++) {
                    if (ApplicationClass.artists.get(j).getName().equals(name)) {
                        temp = j;
                        break;
                    }
                }
                i.putExtra("position", temp);
                startActivity(i);
            }
        });

        v1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ApplicationClass.nowplaying=(Song)titleadapter.getItem(position);
                Intent i =new Intent(SearchActivity.this,MusicPlayBack.class);
                i.setAction(Actions.command.clicksong);
                startService(i);
            }
        });

        v1.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                for(int i=firstVisibleItem;i<firstVisibleItem+visibleItemCount;i++){
                    if(ApplicationClass.filter.get(i).getAlbumArt()!=null)
                        ApplicationClass.filter.get(i).v.setImageBitmap(ApplicationClass.filter.get(i).getAlbumArt());
                }
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
