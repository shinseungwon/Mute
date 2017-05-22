package com.shinsw.seungwon.mute;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;



/**
 * Created by seungwon on 2016-03-22.
 */
public class ApplicationClass extends Application {//프로그램에 하나만 필요한 메서드,변수,클래스를 관리하는 클래스

    public static ArrayList<Song> songs=new ArrayList<>();
    public static ArrayList<Artist> artists=new ArrayList<>();
    public static SQLiteConnector SQLiteConnector;
    public static ArtistConnector artistConnector;
    public static Song nowplaying;
    public static Stack<Song> stack=new Stack<>();
    public static Song next;
    public static Song longclicked;

    public static User user;
    static ArrayList<Song> filter;

    @Override
    public void onCreate(){
        super.onCreate();
        user=new User();
        Artist.c=this;
        Intent a=new Intent(this,CoreService.class);
        //a.setAction(Actions.command.network);
        startService(a);
        SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
        user.mode=pref.getInt("mode",3);
        user.usehand=pref.getInt("usehand",0);
        user.vibrant=pref.getInt("vibrant",25);
        user.calm=pref.getInt("calm",25);
        user.beat=pref.getInt("beat",25);
        user.rock=pref.getInt("rock",25);
    }
    public static Bitmap getArtworkQuick(Context context, int album_id, int w, int h) {
        final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
        final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        w -= 2;
        h -= 2;
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        if (uri != null) {
            ParcelFileDescriptor fd = null;
            try {
                fd = res.openFileDescriptor(uri, "r");
                int sampleSize = 1;

                sBitmapOptionsCache.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(
                        fd.getFileDescriptor(), null, sBitmapOptionsCache);
                int nextWidth = sBitmapOptionsCache.outWidth >> 1;
                int nextHeight = sBitmapOptionsCache.outHeight >> 1;
                while (nextWidth > w && nextHeight > h) {
                    sampleSize <<= 1;
                    nextWidth >>= 1;
                    nextHeight >>= 1;
                }
                sBitmapOptionsCache.inSampleSize = sampleSize;
                sBitmapOptionsCache.inJustDecodeBounds = false;
                Bitmap b = BitmapFactory.decodeFileDescriptor(
                        fd.getFileDescriptor(), null, sBitmapOptionsCache);
                if (b != null) {

                    if (sBitmapOptionsCache.outWidth != w || sBitmapOptionsCache.outHeight != h) {
                        Bitmap tmp = Bitmap.createScaledBitmap(b, w, h, true);
                        b.recycle();
                        b = tmp;
                    }
                }
                return b;
            } catch (FileNotFoundException e) {
            } finally {
                try {
                    if (fd != null)
                        fd.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}