package com.app.chengdazhi.todo.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.app.chengdazhi.todo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengdazhi on 2/21/16.
 */
public class MyListFactory implements RemoteViewsService.RemoteViewsFactory {
    private List itemList = new ArrayList();

    private Context context = null;

    private int appWidgetId;

    public MyListFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        populateListItem();
    }

    private void populateListItem() {
        itemList.add(0);
        itemList.add(1);
        itemList.add(2);
        itemList.add(3);
        itemList.add(4);
        itemList.add(5);
        itemList.add(6);
        itemList.add(7);
        itemList.add(8);
        itemList.add(9);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_item);

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
