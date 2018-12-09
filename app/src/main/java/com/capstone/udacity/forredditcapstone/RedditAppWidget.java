package com.capstone.udacity.forredditcapstone;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.capstone.udacity.forredditcapstone.utils.Constants;

/**
 * Implementation of App Widget functionality.
 */
public class RedditAppWidget extends AppWidgetProvider {
    private static final String TAG = RedditAppWidget.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        //update happens on receive method no need to call this method.
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(Constants.UPDATE_ACTION.equals(intent.getAction())){
            Log.d(TAG, " onReceive updating widget");
            String header = intent.getStringExtra("widgetHeader");
            String body = intent.getStringExtra("widgetBody");
            String link = intent.getStringExtra("widgetOnClick");
            if(!TextUtils.isEmpty(header)) {
                // Construct the RemoteViews object
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.reddit_app_widget);
                ComponentName widget = new ComponentName(context, RedditAppWidget.class);
                views.setTextViewText(R.id.appwidget_header_text, header);
                views.setTextViewText(R.id.appwidget_body_text, body);
                Intent linkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, linkIntent, 0);
                views.setOnClickPendingIntent(R.id.appwidget_layout, pendingIntent);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                appWidgetManager.updateAppWidget(widget, views);
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.reddit_app_widget);
        views.setTextViewText(R.id.appwidget_header_text, widgetText);
        views.setTextViewText(R.id.appwidget_body_text, context.getString(R.string.widget_first_message));
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

