package com.shinsw.seungwon.mute;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by seungwon on 2016-03-30.
 */
public class ArtistTitleAdapter extends BaseAdapter{
    Context mContext;
    int pos;

    ArtistTitleAdapter(Context context,int pos){
        mContext=context;
        this.pos=pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listViewItem = convertView;

        if (listViewItem == null) {

            LayoutInflater vi = (LayoutInflater) mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            listViewItem = vi.inflate(R.layout.artisttitle_item, null);
        }

        ImageView iv = (ImageView) listViewItem.findViewById(R.id.artisttitleitem_albumart);
        TextView tv = (TextView) listViewItem.findViewById(R.id.artisttitleitem_title);

            tv.setText(ApplicationClass.artists.get(pos).getTitles().get(position).getTitle());

                Bitmap c=ApplicationClass.getArtworkQuick(mContext,
                        Integer.parseInt(ApplicationClass.artists.get(pos).getTitles().get(position).getAlbumArtId()), 60, 60);
                if(c!=null)
                    iv.setImageBitmap(c);
                else
                    iv.setImageResource(R.mipmap.default_albumart);

        return listViewItem;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public Object getItem(int position) {
        return ApplicationClass.artists.get(pos).getTitles().get(position);
    }

    @Override
    public int getCount(){
        return ApplicationClass.artists.get(pos).getTitles().size();
    }
}
