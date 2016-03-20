package com.app.chengdazhi.todo.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.app.chengdazhi.todo.R;

/**
 * Created by chengdazhi on 2/21/16.
 */
public class MyWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int id : appWidgetIds) {
            RemoteViews remoteViews = updateWidgetListView(context, id);
            appWidgetManager.updateAppWidget(id, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent svcIntent = new Intent(context, MyWidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));//don't know why
        remoteViews.setRemoteAdapter(appWidgetId, R.id.listview_widget, svcIntent);
        remoteViews.setEmptyView(R.id.listview_widget, R.id.empty_view);

        return remoteViews;
    }
}
