package com.shinsw.seungwon.mute;

import android.app.*;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RemoteViews;
import java.io.File;
import java.util.Random;

public class MusicPlayBack extends Service {//음악 재생 서비스
    static MediaPlayer mMediaPlayer = new MediaPlayer();
    public static boolean loop=false;
    android.app.Notification.Builder status;

    private SensorManager mSensorManager;
    private motionListener m1;

    private AudioManager mAudioManager;
    private ComponentName mReceiverComponent;

    private HardButtonReceiver h;
    private BReceiver b;


    public MusicPlayBack() {
    }

    @Override
    public void onCreate() {
        m1=new motionListener();
        Sensor accSensor;
        Sensor magSensor;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(m1, accSensor,SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(m1, magSensor, SensorManager.SENSOR_DELAY_NORMAL);

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                if(!loop) {
                    Intent i = new Intent(MusicPlayBack.this, MusicPlayBack.class);
                    i.setAction(Actions.command.next);
                    startService(i);
                }else{
                    mMediaPlayer.seekTo(0);
                    mMediaPlayer.start();
                }
            }
        });

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mReceiverComponent = new ComponentName(this, MediaButtonReceiver.class);
        mAudioManager.registerMediaButtonEventReceiver(mReceiverComponent);
        h=new HardButtonReceiver();
        b=new BReceiver();
        registerReceiver(h, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        registerReceiver(b, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        NotificationManager manager =(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if(intent.getAction()!=null)
            if(!intent.getAction().equals(Actions.command.stop))
                sendBroadcast(new Intent(Actions.command.refreshmain));

        if(intent.getAction()!=null) {
            if (intent.getAction().equals(Actions.command.clicksong)) {
                CoreService.temp=ApplicationClass.nowplaying;
                ApplicationClass.SQLiteConnector.timeStamp(ApplicationClass.nowplaying);
                ApplicationClass.nowplaying.IncreaseClickcount();
                ApplicationClass.stack.push(ApplicationClass.nowplaying);
                Intent i=new Intent(this,CoreService.class);
                i.setAction(Actions.command.click);
                startService(i);
                if (!onPlay(ApplicationClass.nowplaying))
                    onNext();
            }
            else if (intent.getAction().equals(Actions.command.fromcore)) {
                if (!onPlay(ApplicationClass.nowplaying))
                    onNext();
            }
                else if (intent.getAction().equals(Actions.command.playpause)) {
                if(ApplicationClass.nowplaying!=null) {
                    if (mMediaPlayer.isPlaying())
                        mMediaPlayer.pause();
                    else
                        mMediaPlayer.start();
                }
            } else if (intent.getAction().equals(Actions.command.prev)) {
                if(ApplicationClass.nowplaying!=null)
                onPrev();
            } else if (intent.getAction().equals(Actions.command.next)) {
                if(ApplicationClass.nowplaying!=null)
                onNext();
            } else if (intent.getAction().equals(Actions.command.repeat)) {
                if(ApplicationClass.nowplaying!=null) {
                    loop = !loop;
                    RemoteViews views2 = new RemoteViews(getPackageName(),
                            R.layout.big_notification_provider);
                    if (loop) {

                        views2.setImageViewResource(R.id.big_notification_repeat, R.mipmap.repeatone);
                    } else {

                        views2.setImageViewResource(R.id.big_notification_repeat, R.mipmap.repeatall);
                    }
                }
            } else if (intent.getAction().equals(Actions.command.stop)) {

                if(ApplicationClass.nowplaying!=null) {
                    ApplicationClass.SQLiteConnector.InsertRuntime(ApplicationClass.nowplaying);
                    mMediaPlayer.stop();
                    stopSelf();
                    ApplicationClass.nowplaying = null;
                    manager.cancel(1);
                    mSensorManager.unregisterListener(m1);

                    unregisterReceiver(h);
                    unregisterReceiver(b);
                    return Service.START_STICKY;
                }
            } else if (intent.getAction().equals(Actions.command.good)) {
                if(ApplicationClass.nowplaying!=null)
                ApplicationClass.nowplaying.good();
            } else if (intent.getAction().equals(Actions.command.bad)) {
                if(ApplicationClass.nowplaying!=null)
                ApplicationClass.nowplaying.bad();
            }
        }
        if(ApplicationClass.nowplaying!=null) {
            startForeground(1, showNotification());
            refreshWidget();
        }
        return Service.START_NOT_STICKY;
    }

    private void refreshWidget() {

        RemoteViews views = new RemoteViews(getPackageName(),R.layout.widget_provider);

        if(ApplicationClass.nowplaying!=null){

            views.setTextViewText(R.id.widget_title, ApplicationClass.nowplaying.getTitle());
            views.setTextViewText(R.id.widget_artist, ApplicationClass.nowplaying.getArtist());

            Bitmap b=ApplicationClass.getArtworkQuick(this, Integer.parseInt(ApplicationClass.nowplaying.getAlbumArtId()), 200, 200);
            if(b!=null)
                views.setImageViewBitmap(R.id.widget_albumart,b);
            else
                views.setImageViewResource(R.id.widget_albumart,R.mipmap.default_albumart);

            if(mMediaPlayer.isPlaying())
                views.setImageViewResource(R.id.widget_play,R.mipmap.nw_pause);
            else
                views.setImageViewResource(R.id.widget_play,R.mipmap.nw_play);

            if(loop)
                views.setImageViewResource(R.id.widget_repeat,R.mipmap.repeatone);
            else
                views.setImageViewResource(R.id.widget_repeat,R.mipmap.repeatall);
/*
            if(ApplicationClass.nowplaying.getGoodbad()==1){
                views.setImageViewResource(R.id.widget_bad,R.mipmap.nw_bad);
                views.setImageViewResource(R.id.widget_good,R.mipmap.nw_goodactive);
            }
            else if(ApplicationClass.nowplaying.getGoodbad()==-1){
                views.setImageViewResource(R.id.widget_bad,R.mipmap.nw_badactive);
                views.setImageViewResource(R.id.widget_good,R.mipmap.nw_good);
            }
            else{
                views.setImageViewResource(R.id.widget_bad,R.mipmap.nw_bad);
                views.setImageViewResource(R.id.widget_good,R.mipmap.nw_good);
            }
            */
        }

        ComponentName componentname = new ComponentName(this, WidgetProvider.class);
        AppWidgetManager appwidgetmanager = AppWidgetManager.getInstance(this);
        appwidgetmanager.updateAppWidget(componentname, views);

    }

    public void onPrev(){
        Random random=new Random();
        ApplicationClass.SQLiteConnector.InsertRuntime(ApplicationClass.nowplaying);
        if(ApplicationClass.stack.size()!=0)
            ApplicationClass.nowplaying = ApplicationClass.stack.pop();
        else
            ApplicationClass.nowplaying = ApplicationClass.songs.get(random.nextInt(ApplicationClass.songs.size()));

        if(!onPlay(ApplicationClass.nowplaying))
            onPrev();
    }

    public void onNext(){
        ApplicationClass.SQLiteConnector.InsertRuntime(ApplicationClass.nowplaying);

            Random random = new Random();
            if (ApplicationClass.stack.size() > 100)
                ApplicationClass.stack.clear();

            ApplicationClass.stack.push(ApplicationClass.nowplaying);

            if (ApplicationClass.next != null) {

                ApplicationClass.nowplaying = ApplicationClass.next;

            } else {
                ApplicationClass.nowplaying = ApplicationClass.songs.get(random.nextInt(ApplicationClass.songs.size()));
            }

        if(!onPlay(ApplicationClass.nowplaying))
            onNext();
    }

    public boolean onPlay(Song song) {
        if(ApplicationClass.longclicked!=null)
            ApplicationClass.longclicked=null;
        Uri uri = Uri.withAppendedPath(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + song.getMusicId());
        Intent i=new Intent(this,CoreService.class);
        i.setAction(Actions.command.getnext);
        startService(i);

                try {
                    File file = new File(song.getFilePath());
                    if (file.exists()) {
                        mMediaPlayer.reset();
                        mMediaPlayer.setDataSource(this, uri);
                        mMediaPlayer.prepare();
                        mMediaPlayer.start();
                        return true;
                    }
                    else{
                        ApplicationClass.SQLiteConnector.deleteSongList(song);
                        return false;
                    }
                } catch (Exception e) {
                    return false;
                }
    }

     class motionListener implements SensorEventListener{
         final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
         int degx;
         int degy;
         float[] m_acc_data = null;
         float[] m_mag_data = null;
         int on;
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
        @Override
        public void onSensorChanged(SensorEvent event) {

            if (pm.isScreenOn()&&ApplicationClass.user.usehand==1) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    m_acc_data = event.values.clone();
                } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    m_mag_data = event.values.clone();
                }

                float[] R = new float[9];
                float[] Values = new float[3];
                if (m_acc_data != null && m_mag_data != null) {
                    SensorManager.getRotationMatrix(R, null, m_acc_data, m_mag_data);
                    SensorManager.getOrientation(R, Values);
                    degx = (int) Math.toDegrees(Values[1]);//긴 방향 회전
                    degy = (int) Math.toDegrees(Values[2]);//짧은 방향 회전
                   if (degy < -150 && Math.abs(degx) < 30 && on == 0) {
                        Intent i = new Intent(MusicPlayBack.this, MusicPlayBack.class);
                        i.setAction(Actions.command.next);
                        startService(i);
                        on = 1;
                    } else if (Math.abs(degy) < 30 && Math.abs(degx) < 30 && on == 1) {
                        on = 0;
                    }
                }
            }
        }
    }

    private android.app.Notification showNotification() {

        RemoteViews views1 = new RemoteViews(getPackageName(),
                R.layout.notification_provider);
        RemoteViews views2 = new RemoteViews(getPackageName(),
                R.layout.big_notification_provider);

        Bitmap bitmap = ApplicationClass.getArtworkQuick(this, Integer.parseInt
                (ApplicationClass.nowplaying.getAlbumArtId()), 150, 150);

        if(bitmap!=null)
            views1.setImageViewBitmap(R.id.notification_albumart, bitmap);
        else
            views1.setImageViewResource(R.id.notification_albumart,R.mipmap.default_albumart);
            views1.setTextViewText(R.id.notification_title, ApplicationClass.nowplaying.getTitle());
            views1.setTextViewText(R.id.notification_artist, ApplicationClass.nowplaying.getArtist());

        if(MusicPlayBack.mMediaPlayer.isPlaying())
            views1.setImageViewResource(R.id.notification_play,R.mipmap.nw_pause);
        else
            views1.setImageViewResource(R.id.notification_play,R.mipmap.nw_play);

        if(bitmap!=null)
            views2.setImageViewBitmap(R.id.big_notification_albumart, bitmap);
        else
            views2.setImageViewResource(R.id.big_notification_albumart,R.mipmap.default_albumart);
            views2.setTextViewText(R.id.big_notification_title, ApplicationClass.nowplaying.getTitle());
            views2.setTextViewText(R.id.big_notification_artist, ApplicationClass.nowplaying.getArtist());

        if(MusicPlayBack.mMediaPlayer.isPlaying())
            views2.setImageViewResource(R.id.big_notification_play,R.mipmap.nw_pause);
        else
            views2.setImageViewResource(R.id.big_notification_play,R.mipmap.nw_play);

        if(loop)
            views2.setImageViewResource(R.id.big_notification_repeat,R.mipmap.repeatone);
        else
            views2.setImageViewResource(R.id.big_notification_repeat,R.mipmap.repeatall);
/*
        if(ApplicationClass.nowplaying.getGoodbad()==1){
            views1.setImageViewResource(R.id.notification_good,R.mipmap.nw_goodactive);
            views2.setImageViewResource(R.id.big_notification_good,R.mipmap.nw_goodactive);

            views1.setImageViewResource(R.id.notification_bad,R.mipmap.nw_bad);
            views2.setImageViewResource(R.id.big_notification_bad,R.mipmap.nw_bad);

        }
        else if(ApplicationClass.nowplaying.getGoodbad()==-1){
            views1.setImageViewResource(R.id.notification_bad,R.mipmap.nw_badactive);
            views2.setImageViewResource(R.id.big_notification_bad,R.mipmap.nw_badactive);

            views1.setImageViewResource(R.id.notification_good,R.mipmap.nw_good);
            views2.setImageViewResource(R.id.big_notification_good,R.mipmap.nw_good);
        }
        else{
            views1.setImageViewResource(R.id.notification_bad,R.mipmap.nw_bad);
            views2.setImageViewResource(R.id.big_notification_bad,R.mipmap.nw_bad);

            views1.setImageViewResource(R.id.notification_good,R.mipmap.nw_good);
            views2.setImageViewResource(R.id.big_notification_good,R.mipmap.nw_good);
        }
*/
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
/*
        Intent modeIntent = new Intent(this, ControlActivity.class);
        modeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pmodeIntent = PendingIntent.getActivity(this, 0,
                modeIntent, 0);
*/
        Intent playIntent = new Intent(this,this.getClass());
        playIntent.setAction(Actions.command.playpause);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent prevIntent = new Intent(this, this.getClass());
        prevIntent.setAction(Actions.command.prev);
        PendingIntent pprevIntent = PendingIntent.getService(this, 0,
                prevIntent, 0);

        Intent nextIntent = new Intent(this,this.getClass());
        nextIntent.setAction(Actions.command.next);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent repeatIntent = new Intent(this,this.getClass());
        repeatIntent.setAction(Actions.command.repeat);
        PendingIntent prepeatIntent = PendingIntent.getService(this, 0,
                repeatIntent, 0);

        Intent closeIntent = new Intent(this,this.getClass());
        closeIntent.setAction(Actions.command.stop);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);
/*
        Intent badIntent = new Intent(this,this.getClass());
        badIntent.setAction(Actions.command.bad);
        PendingIntent pbadIntent = PendingIntent.getService(this, 0,
                badIntent, 0);

        Intent goodIntent = new Intent(this,this.getClass());
        goodIntent.setAction(Actions.command.good);
        PendingIntent pgoodIntent = PendingIntent.getService(this, 0,
                goodIntent, 0);
*/
        views1.setOnClickPendingIntent(R.id.notification_albumart, pendingIntent);

        views1.setOnClickPendingIntent(R.id.notification_play, pplayIntent);

        views1.setOnClickPendingIntent(R.id.notification_exit, pcloseIntent);

        views1.setOnClickPendingIntent(R.id.notification_prev, pprevIntent);

        views1.setOnClickPendingIntent(R.id.notification_next, pnextIntent);

        //views1.setOnClickPendingIntent(R.id.notification_good,pgoodIntent);

        //views1.setOnClickPendingIntent(R.id.notification_bad, pbadIntent);

        views2.setOnClickPendingIntent(R.id.big_notification_albumart, pendingIntent);

        views2.setOnClickPendingIntent(R.id.big_notification_play, pplayIntent);

        views2.setOnClickPendingIntent(R.id.big_notification_exit, pcloseIntent);

        views2.setOnClickPendingIntent(R.id.big_notification_prev, pprevIntent);

        views2.setOnClickPendingIntent(R.id.big_notification_next, pnextIntent);

        views2.setOnClickPendingIntent(R.id.big_notification_repeat, prepeatIntent);

        //views2.setOnClickPendingIntent(R.id.big_notification_good,pgoodIntent);

        //views2.setOnClickPendingIntent(R.id.big_notification_bad,pbadIntent);

        //views2.setOnClickPendingIntent(R.id.big_notification_mode, pmodeIntent);

        android.app.Notification n;
        Bitmap b=ApplicationClass.nowplaying.getAlbumArt();
        status = new android.app.Notification.Builder(this);
        n = status.setContentTitle("some string")
                .setContentText("Slide down on note to expand")
                .setSmallIcon(R.mipmap.icon2)
                .setLargeIcon(b)
                .build();
        n.contentView=views1;
        n.bigContentView = views2;
        n.flags = android.app.Notification.FLAG_ONGOING_EVENT;
        return n;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mAudioManager.unregisterMediaButtonEventReceiver(mReceiverComponent);
    }

    public static class HardButtonReceiver extends BroadcastReceiver {
        int currentstate=-1;
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Intent.ACTION_HEADSET_PLUG.equals(intent.getAction())){
            if(currentstate==1&&intent.getIntExtra("state", -1)==0){
                    Intent i=new Intent(context,MusicPlayBack.class);
                    i.setAction(Actions.command.playpause);
                    context.startService(i);
            }
            currentstate=intent.getIntExtra("state", -1);
        }
    }
    }

    public static class BReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action==null)
                return;

            if (action.equals("android.bluetooth.a2dp.profile.action." +
                    "CONNECTION_STATE_CHANGED") && MusicPlayBack.mMediaPlayer.isPlaying()) {
                Intent i=new Intent(context,MusicPlayBack.class);
                i.setAction(Actions.command.playpause);
                context.startService(i);
            }
        }
    }

    public static class MediaButtonReceiver extends BroadcastReceiver {
        public MediaButtonReceiver() {
            super();
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            int action;
            Log.i("testinput","aaa");
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            action = event.getAction();
            Intent i;

            if(action==0)
                return;

            if (!Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
                return;
            }

            int keycode = event.getKeyCode();
            if (action == KeyEvent.ACTION_UP) {
                switch (keycode) {
                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        //Log.d("MB", "BUTTON PLAY");
                        Intent i1=new Intent(context,MusicPlayBack.class);
                        i1.setAction(Actions.command.playpause);
                        context.startService(i1);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PAUSE:
                        //Log.d("MB", "BUTTON PAUSE");
                        Intent i2=new Intent(context,MusicPlayBack.class);
                        i2.setAction(Actions.command.playpause);
                        context.startService(i2);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        //Log.d("MB", "BUTTON PREV");
                        i = new Intent(context, MusicPlayBack.class);
                        i.setAction(Actions.command.prev);
                        context.startService(i);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        //Log.d("MB", "BUTTON NEXT");
                        i = new Intent(context, MusicPlayBack.class);
                        i.setAction(Actions.command.next);
                        context.startService(i);
                        break;
                }
            }
        }
    }
}
