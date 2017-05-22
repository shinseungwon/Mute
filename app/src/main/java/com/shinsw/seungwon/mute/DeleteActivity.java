package com.shinsw.seungwon.mute;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class DeleteActivity extends AppCompatActivity {
    DeleteAdapter D;
    ListView v;
    ProgressDialog asyncDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle("Delete Duplicated Songs");
        v=(ListView)findViewById(R.id.delete_listview);
        asyncDialog=new ProgressDialog(this);
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        asyncDialog.setMessage("Searching");
        asyncDialog.show();
        asyncDialog.setMax(100);

        v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ApplicationClass.nowplaying=(Song)D.getItem(position);
                Intent i =new Intent(DeleteActivity.this,MusicPlayBack.class);
                i.setAction(Actions.command.clicksong);
                startService(i);
            }
        });
        v.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int positionx=position;
                final Song song=(Song)D.getItem(positionx);
                AlertDialog.Builder alt_bld = new AlertDialog.Builder(DeleteActivity.this);
                alt_bld.setMessage("Do you want to Delete File '"+song.getTitle()+" - "+song.getArtist()+"' ?").setCancelable(
                        false).setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ApplicationClass.SQLiteConnector.deleteSongList(song);
                                if (ApplicationClass.SQLiteConnector.deleteRealFile(song)) {
                                    Toast.makeText(DeleteActivity.this, "File Successfully Deleted", Toast.LENGTH_SHORT).show();
                                    D.Delete(positionx);
                                    v.setAdapter(D);
                                } else
                                    Toast.makeText(DeleteActivity.this, "File Not Found", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = alt_bld.create();
                alert.setTitle("File Delete");
                alert.show();
                return true;
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                D=new DeleteAdapter(DeleteActivity.this);
                sendBroadcast(new Intent(Actions.command.complete));
            }
        }).start();
    }
    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(bitmapbroadcast, new IntentFilter(Actions.command.delete));
        registerReceiver(bitmapprogbroadcast, new IntentFilter(Actions.command.deleteprog));
        registerReceiver(bitmapcompletebroadcast,new IntentFilter(Actions.command.complete));
    }
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(bitmapbroadcast);
        unregisterReceiver(bitmapprogbroadcast);
        unregisterReceiver(bitmapcompletebroadcast);
    }

    private BroadcastReceiver bitmapbroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent i) {
                Song s= D.getList().get((int) i.getExtras().get("Value"));
                s.v.setImageBitmap(s.getAlbumArt());}
    };

    private BroadcastReceiver bitmapprogbroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent i) {
            int k=(int)i.getExtras().get("progress");
            if(k==99)
                k=100;
            asyncDialog.setProgress(k);
        }
    };
    private BroadcastReceiver bitmapcompletebroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent i) {

                v.setAdapter(D);
                asyncDialog.dismiss();
        }
    };
}
