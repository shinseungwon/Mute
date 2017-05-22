package com.shinsw.seungwon.mute;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by seungwon on 2016-05-01.
 */
public class DeleteAdapter extends BaseAdapter{
    Context mContext;
    ArrayList<Song> NeedToDelete;
    ArrayList<Integer> CompareIndex;
    DeleteAdapter(Context context){
        mContext=context;
        NeedToDelete=new ArrayList<>();
        CompareIndex=new ArrayList<>();

        int size=(ApplicationClass.songs.size()*(ApplicationClass.songs.size()+1))/2;
        int count=0;
        int progress=0;
        Intent in;
        for(int i=0;i<ApplicationClass.songs.size();i++){
            for(int j=i+1;j<ApplicationClass.songs.size();j++){
                count++;
                if((count*100/size)>progress){
                    progress=progress+1;
                    in=new Intent(Actions.command.deleteprog);
                    in.putExtra("progress",progress);
                    mContext.sendBroadcast(in);
                }

                if(ApplicationClass.songs.get(i).semiEquals(ApplicationClass.songs.get(j))) {

                    if(!NeedToDelete.contains(ApplicationClass.songs.get(i)))
                        NeedToDelete.add(ApplicationClass.songs.get(i));

                    if(!NeedToDelete.contains(ApplicationClass.songs.get(j)))
                        NeedToDelete.add(ApplicationClass.songs.get(j));

                }
            }
        }
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View listViewItem = convertView;

        if (listViewItem == null) {

            LayoutInflater vi = (LayoutInflater) mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            listViewItem = vi.inflate(R.layout.duplicated_item, null);
        }

        final ArrayList<Song> item=NeedToDelete;
        final ImageView iv = (ImageView) listViewItem.findViewById(R.id.dupitem_albumart);
        TextView tv = (TextView) listViewItem.findViewById(R.id.dupitem_title);
        TextView tv1 = (TextView) listViewItem.findViewById(R.id.dupitem_artist);
        tv.setText(item.get(position).getTitle());
        tv1.setText(item.get(position).getArtist());

        new Thread(new Runnable() {
            Handler h=new Handler();
            @Override
            public void run() {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap b;
                        item.get(position).v=iv;
                        b=ApplicationClass.getArtworkQuick(mContext,
                                Integer.parseInt(item.get(position).getAlbumArtId()), 60, 60);
                        if(b!=null) {
                            item.get(position).setAlbumart(b);
                            Intent i=new Intent(Actions.command.delete);
                            i.putExtra("Value", position);
                            mContext.sendBroadcast(i);
                        }
                    }
                });
            }
        }).start();

        return listViewItem;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public Object getItem(int position) {
            return NeedToDelete.get(position);
    }
    public void Delete(int position){
        NeedToDelete.remove(position);
    }

    @Override
    public int getCount(){
            return NeedToDelete.size();
    }
    public ArrayList<Song> getList(){
        return NeedToDelete;
    }
}
