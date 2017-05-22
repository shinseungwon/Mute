package com.shinsw.seungwon.mute;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

/**
 * Created by seungwon on 2016-03-22.
 */
public class BillboardAdapter extends BaseAdapter{//제목으로 검색했을때 쓰이는 리스트어댑터
    Context mContext;
    GetRss getRss = new GetRss();
    Vector<String> titlevec;
    Vector<String> artistvec;
    Vector<Integer> intvec;
    Vector<Song> songvec;

    BillboardAdapter(Context context){
        mContext=context;
        getRss.execute(null, null, null);
        while(true) {
            try{
                if(getRss.flag) {
                    titlevec = getRss.titlevec;
                    artistvec = getRss.artistvec;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        intvec=new Vector<>();
        songvec=new Vector<>();
        Song s;
        for(int i=0;i<titlevec.size();i++) {
            s=ApplicationClass.SQLiteConnector.isExist(Parser.billboardTitleParser(titlevec.get(i)), artistvec.get(i));
            if (s!=null){
                songvec.add(s);
                intvec.add(1);
            }
            else {
                songvec.add(null);

                if(ApplicationClass.SQLiteConnector.isWishList(Parser.billboardTitleParser(titlevec.get(i)),artistvec.get(i)))
                    intvec.add(-1);
                else
                    intvec.add(0);
            }
        }


    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View listViewItem = convertView;

        if (listViewItem == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            listViewItem = vi.inflate(R.layout.billboard_item, null);
        }
        ImageView iv=(ImageView)listViewItem.findViewById(R.id.billboarditem_albumart);
        ImageView tv2=(ImageView) listViewItem.findViewById(R.id.billboarditem_wish);


        if(intvec.get(position)==1) {
            iv.setImageResource(R.mipmap.have);
            tv2.setImageBitmap(null);
        }
        else if(intvec.get(position)==0) {
            iv.setImageResource(R.mipmap.donthave);
            tv2.setImageResource(R.mipmap.add);
        }
        else {
            iv.setImageResource(R.mipmap.wishlist);
            tv2.setImageBitmap(null);
        }

        ImageView v=(ImageView)listViewItem.findViewById(R.id.billboarditem_wish);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ApplicationClass.SQLiteConnector.addWishList(Parser.billboardTitleParser(titlevec.get(position)),artistvec.get(position))) {
                    Toast.makeText(mContext, "Added to Wishlist!", Toast.LENGTH_SHORT).show();
                    mContext.sendBroadcast(new Intent(Actions.command.frombillboard));
                }
                else
                    Toast.makeText(mContext,"Already Exist!",Toast.LENGTH_SHORT).show();
            }
        });




        TextView tv = (TextView) listViewItem.findViewById(R.id.billboarditem_title);
        TextView tv1 = (TextView) listViewItem.findViewById(R.id.billboarditem_artist);

        tv.setText(titlevec.get(position));
        tv1.setText(artistvec.get(position));

        return listViewItem;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
        return position;
    }

    @Override
    public int getCount(){
        return artistvec.size();
    }

}
