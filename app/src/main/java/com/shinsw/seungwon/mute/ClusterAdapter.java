package com.shinsw.seungwon.mute;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by seungwon on 2016-03-22.
 */
public class ClusterAdapter extends BaseAdapter{//제목으로 검색했을때 쓰이는 리스트어댑터
    Context mContext;

    ClusterAdapter(Context context){
        mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listViewItem = convertView;

        if (listViewItem == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            listViewItem = vi.inflate(R.layout.cluster_item, null);
        }

        final ImageView iv = (ImageView) listViewItem.findViewById(R.id.clusteritem_albumart);
        TextView tv = (TextView) listViewItem.findViewById(R.id.clusteritem_title);
        TextView tv1 = (TextView) listViewItem.findViewById(R.id.clusteritem_artist);
        RelativeLayout l=(RelativeLayout)listViewItem.findViewById(R.id.clusteritem_layout);
        ImageView iv2=(ImageView) listViewItem.findViewById(R.id.cluster_goodbad);

        if(CoreService.N.getFullResult().get(position).getGoodbad()==1)
            iv2.setImageResource(R.mipmap.nw_goodactive);
        else if(CoreService.N.getFullResult().get(position).getGoodbad()==-1)
            iv2.setImageResource(R.mipmap.nw_badactive);
        else
            iv2.setImageBitmap(null);

        final int positionx=position;

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Song> item=CoreService.N.getFullResult();
                Bitmap b;
                item.get(positionx).v=iv;
                b = item.get(positionx).getAlbumArt();
                if (b == null)
                b=ApplicationClass.getArtworkQuick(mContext,
                        Integer.parseInt(item.get(positionx).getAlbumArtId()), 60, 60);

                    item.get(positionx).setAlbumart(b);
                    Intent i=new Intent(Actions.command.clusterlist);
                    i.putExtra("Value",positionx);
                    mContext.sendBroadcast(i);

            }
        }).start();

        tv.setText(CoreService.N.getFullResult().get(position).getTitle());
        tv1.setText(CoreService.N.getFullResult().get(position).getArtist());
        int color;
        switch(CoreService.N.getFullResult().get(position).NumberNeuron){
            case 1:
                color = mContext.getResources().getColor(R.color.colorOne);
                l.setBackgroundColor(color);
                break;
            case 2:
                color = mContext.getResources().getColor(R.color.colorTwo);
                l.setBackgroundColor(color);
                break;
            case 3:
                color = mContext.getResources().getColor(R.color.colorThree);
                l.setBackgroundColor(color);
                break;
            case 4:
                color = mContext.getResources().getColor(R.color.colorFour);
                l.setBackgroundColor(color);
                break;
            default:
                break;
        }
        return listViewItem;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
        return CoreService.N.getFullResult().get(position);
    }

    @Override
    public int getCount(){
        return CoreService.N.getFullResult().size();
    }

}
