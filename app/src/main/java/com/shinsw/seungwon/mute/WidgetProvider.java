package com.shinsw.seungwon.mute;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        RemoteViews updateViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_provider);

        Intent intent = new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);
        updateViews.setOnClickPendingIntent(R.id.widget_albumart, pendingIntent);
/*
        Intent settingIntent = new Intent(context, ControlActivity.class);
        PendingIntent psettingIntent = PendingIntent.getActivity(context, 0,
                settingIntent, 0);
        updateViews.setOnClickPendingIntent(R.id.widget_mode, psettingIntent);
*/
        Intent playIntent = new Intent(context, MusicPlayBack.class);
        playIntent.setAction(Actions.command.playpause);
        PendingIntent pplayIntent = PendingIntent.getService(context, 0,
                playIntent, 0);
        updateViews.setOnClickPendingIntent(R.id.widget_play, pplayIntent);

        Intent nextIntent = new Intent(context, MusicPlayBack.class);
        nextIntent.setAction(Actions.command.next);
        PendingIntent pnextIntent = PendingIntent.getService(context, 0,
                nextIntent,0);
        updateViews.setOnClickPendingIntent(R.id.widget_next, pnextIntent);

        Intent prevIntent = new Intent(context, MusicPlayBack.class);
        prevIntent.setAction(Actions.command.prev);
        PendingIntent pprevIntent = PendingIntent.getService(context, 0,
                prevIntent,0);
        updateViews.setOnClickPendingIntent(R.id.widget_prev, pprevIntent);

        Intent repeatIntent = new Intent(context, MusicPlayBack.class);
        repeatIntent.setAction(Actions.command.repeat);
        PendingIntent prepeatIntent = PendingIntent.getService(context, 0,
                repeatIntent,0);
        updateViews.setOnClickPendingIntent(R.id.widget_repeat, prepeatIntent);
/*
        Intent goodIntent = new Intent(context, MusicPlayBack.class);
        goodIntent.setAction(Actions.command.good);
        PendingIntent pgoodIntent = PendingIntent.getService(context, 0,
                goodIntent, 0);
        updateViews.setOnClickPendingIntent(R.id.widget_good, pgoodIntent);

        Intent badIntent = new Intent(context, MusicPlayBack.class);
        badIntent.setAction(Actions.command.bad);
        PendingIntent pbadIntent = PendingIntent.getService(context, 0,
                badIntent, 0);
        updateViews.setOnClickPendingIntent(R.id.widget_bad, pbadIntent);
*/
        Intent stopIntent = new Intent(context, MusicPlayBack.class);
        stopIntent.setAction(Actions.command.stop);
        PendingIntent sstopIntent = PendingIntent.getService(context, 0,
                stopIntent, 0);
        updateViews.setOnClickPendingIntent(R.id.widget_exit, sstopIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, updateViews);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

