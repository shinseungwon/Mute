package com.shinsw.seungwon.mute;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
public class ArtistAdapter extends BaseAdapter{//아티스트정렬 리스트의 어댑터

    Context mContext;
    String keyWord;
    ArrayList<Artist> filter;
    ArrayList<String> index;
    ArrayList<Integer> compareIndex;
    ArtistAdapter(Context context){
        mContext=context;
        index=new ArrayList<>();
        for(int i=0;i<ApplicationClass.artists.size();i++)
            index.add(ApplicationClass.artists.get(i).getName());
    }
    ArtistAdapter(Context context,String keyWord){
        mContext=context;
        compareIndex=new ArrayList<>();
        this.keyWord="(?i:.*"+keyWord+".*)";
        filter=new ArrayList<>();
        for(int i=0;i<ApplicationClass.artists.size();i++){
            if(ApplicationClass.artists.get(i).getName().matches(this.keyWord)) {
                filter.add(ApplicationClass.artists.get(i));
                compareIndex.add(i);
            }
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listViewItem = convertView;

        if (listViewItem == null) {

            LayoutInflater vi = (LayoutInflater) mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            listViewItem = vi.inflate(R.layout.artist_item, null);
        }

        final ArrayList<Artist> item;

        if(filter!=null)
            item=filter;
        else
            item=ApplicationClass.artists;

        final ImageView iv = (ImageView) listViewItem.findViewById(R.id.artistitem_albumart);
        final int positionx=position;

        TextView tv = (TextView) listViewItem.findViewById(R.id.artistitem_artist);
        TextView tv1 = (TextView) listViewItem.findViewById(R.id.artistitem_count);
        tv.setText(item.get(position).getName());
        tv1.setText(item.get(position).getCount() + " Tracks");

        new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap b;
                        item.get(positionx).v = iv;

                        b = item.get(positionx).getAlbumArt();
                        if (b == null)
                        b = ApplicationClass.getArtworkQuick(mContext,
                                item.get(positionx).getAlbumArtId(), 60, 60);
                        item.get(positionx).setAlbumart(b);
                        Intent i = new Intent(Actions.command.bld);
                        i.putExtra("Type", 1);

                        if (filter == null)
                            i.putExtra("Value", positionx);
                        else
                            i.putExtra("Value", compareIndex.get(positionx));

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
            return ApplicationClass.artists.get(position);
        else
            return filter.get(position);
    }

    @Override
    public int getCount(){
        if(filter==null)
            return ApplicationClass.artists.size();
        else
            return filter.size();
    }

    public ArrayList<String> getIndex(){
        return index;
    }
}

