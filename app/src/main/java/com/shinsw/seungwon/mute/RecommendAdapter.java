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
public class RecommendAdapter extends BaseAdapter{//제목으로 검색했을때 쓰이는 리스트어댑터
    Context mContext;
    ArrayList<Artist> artists;
    int[] positions=new int[ApplicationClass.artists.size()];
    RecommendAdapter(Context context){
        mContext=context;
        artists=getRank();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View listViewItem = convertView;

        if (listViewItem == null) {

            LayoutInflater vi = (LayoutInflater) mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            listViewItem = vi.inflate(R.layout.recommend_item, null);
        }

        final ArrayList<Artist> item;
        item=artists;

        final ImageView iv = (ImageView) listViewItem.findViewById(R.id.recommend_albumart);
        TextView tv = (TextView) listViewItem.findViewById(R.id.recommend_artist);
        tv.setText(item.get(position).getName());
        new Thread(new Runnable() {

            @Override
            public void run() {
                Bitmap b;
                item.get(position).v=iv;
                b=ApplicationClass.getArtworkQuick(mContext,
                        item.get(position).getAlbumArtId(), 400, 400);

                if(b!=null) {
                    item.get(position).setAlbumart(b);
                    Intent i=new Intent(Actions.command.recommend);

                        i.putExtra("Value",position);

                    mContext.sendBroadcast(i);
                }

            }
        }).start();

        return listViewItem;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
        return artists.size();
    }

    @Override
    public int getCount(){
        return artists.size();
    }
    public ArrayList<Artist> getIndex(){
        return artists;
    }

    private ArrayList<Artist> getRank(){
        int count=(int)(ApplicationClass.artists.size()*0.1);
        if(count%3==1)
            count=count+2;
        else if(count%3==2)
            count=count+1;

        Artist[] artist = new Artist[ApplicationClass.artists.size()];
        ArrayList<Artist> result=new ArrayList<>();
        for(int i=0;i<ApplicationClass.artists.size();i++){
            artist[i]=ApplicationClass.artists.get(i);
            artist[i].getData();
            positions[i]=i;
        }
        Artist temp;
        int temp2;
        for(int i=0;i<ApplicationClass.artists.size();i++){
            for(int j=i+1;j<ApplicationClass.artists.size();j++){
                if(artist[i].getClickcount()<artist[j].getClickcount()){
                    temp=artist[i];
                    artist[i]=artist[j];
                    artist[j]=temp;
                    temp2=positions[i];
                    positions[i]=positions[j];
                    positions[j]=temp2;
                }
            }
            if(i==count)
                break;

        }
        for(int i=0;i<count;i++)
            result.add(artist[i]);

        return result;
    }

    public int getRealPosition(int position){
        if(positions.length>position)
            return positions[position];
        else
            return 1;
    }
}
