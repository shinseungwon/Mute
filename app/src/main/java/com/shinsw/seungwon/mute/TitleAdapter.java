package com.shinsw.seungwon.mute;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
/**
 * Created by seungwon on 2016-03-22.
 */
public class TitleAdapter extends BaseAdapter{//제목으로 검색했을때 쓰이는 리스트어댑터
    Context mContext;
    String keyWord;
    ArrayList<Song> filter;
    ArrayList<String> index;
    ArrayList<Integer> compareIndex;
    TitleAdapter(Context context){

        mContext=context;
        index=new ArrayList<>();
        for(int i=0;i<ApplicationClass.songs.size();i++)
            index.add(ApplicationClass.songs.get(i).getTitle());
    }
    TitleAdapter(Context context,String keyWord){
        mContext=context;
        this.keyWord="(?i:.*"+keyWord+".*)";
        filter=new ArrayList<>();
        compareIndex=new ArrayList<>();
        for(int i=0;i<ApplicationClass.songs.size();i++){
            if(ApplicationClass.songs.get(i).getTitle().matches(this.keyWord)) {
                filter.add(ApplicationClass.songs.get(i));
                compareIndex.add(i);
            }
        }
        ApplicationClass.filter=filter;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View listViewItem = convertView;

        if (listViewItem == null) {

            LayoutInflater vi = (LayoutInflater) mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            listViewItem = vi.inflate(R.layout.title_item, null);
        }

        final ArrayList<Song> item;

        if(filter!=null)
            item=filter;
        else
            item=ApplicationClass.songs;

        final ImageView iv = (ImageView) listViewItem.findViewById(R.id.titleitem_albumart);


        TextView tv = (TextView) listViewItem.findViewById(R.id.titleitem_title);
        TextView tv1 = (TextView) listViewItem.findViewById(R.id.titleitem_artist);

        if(item.get(position).vibrant==0&&item.get(position).calm==0&&item.get(position).beat==0&&item.get(position).rock==0)
            tv.setTextColor(Color.RED);
        else
            tv.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));

        tv.setText(item.get(position).getTitle());
        tv1.setText(item.get(position).getArtist());


        new Thread(new Runnable() {

            @Override
            public void run() {
                        Bitmap b;
                        item.get(position).v = iv;
                        //if(item.get(positionx).getAlbumArt()==null){
                        b = item.get(position).getAlbumArt();
                        if (b == null)
                            b = ApplicationClass.getArtworkQuick(mContext,
                                    Integer.parseInt(item.get(position).getAlbumArtId()), 60, 60);

                        item.get(position).setAlbumart(b);
                        Intent i = new Intent(Actions.command.bld);
                        i.putExtra("Type", 0);
                        if (filter == null)
                            i.putExtra("Value", position);
                        else
                            i.putExtra("Value", compareIndex.get(position));
                        mContext.sendBroadcast(i);

            }
        }).start();

        return listViewItem;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
        if(filter==null)
            return ApplicationClass.songs.get(position);
        else
            return filter.get(position);
    }

    @Override
    public int getCount(){
        if(filter==null)
            return ApplicationClass.songs.size();
        else
            return filter.size();
    }
    public ArrayList<String> getIndex(){
        return index;
    }


}
