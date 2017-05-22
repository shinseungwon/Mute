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
import java.util.ArrayList;
/**
 * Created by seungwon on 2016-03-22.
 */
public class WishListAdapter extends BaseAdapter{//제목으로 검색했을때 쓰이는 리스트어댑터
    Context mContext;
    ArrayList<wishitem> W;
    WishListAdapter(Context context){
        W=ApplicationClass.SQLiteConnector.getWishList();
        mContext=context;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View listViewItem = convertView;

        if (listViewItem == null) {

            LayoutInflater vi = (LayoutInflater) mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            listViewItem = vi.inflate(R.layout.wish_list_item, null);
        }

        final ImageView iv = (ImageView) listViewItem.findViewById(R.id.wishitem_albumart);

        ImageView v=(ImageView)listViewItem.findViewById(R.id.wishitem_delete);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationClass.SQLiteConnector.deleteWishList(W.get(position).title,W.get(position).artist);
                Toast.makeText(mContext,"Deleted from Wishlist!",Toast.LENGTH_SHORT).show();
                mContext.sendBroadcast(new Intent(Actions.command.fromwish));

            }
        });

        TextView tv = (TextView) listViewItem.findViewById(R.id.wishitem_title);
        TextView tv1 = (TextView) listViewItem.findViewById(R.id.wishitem_artist);

        tv.setText(W.get(position).title);
        tv1.setText(W.get(position).artist);

        iv.setImageResource(R.mipmap.wishlist);

        return listViewItem;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
            return W.get(position);
    }

    @Override
    public int getCount(){
            return W.size();
    }


}
