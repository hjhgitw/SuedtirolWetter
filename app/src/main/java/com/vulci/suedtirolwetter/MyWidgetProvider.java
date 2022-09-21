package com.vulci.suedtirolwetter;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;


public class MyWidgetProvider extends AppWidgetProvider {

	private static final String LOG = "de.vogella.android.widget.example";

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		for (int appWidgetId : appWidgetIds) {
			appWidgetManager.updateAppWidget(appWidgetId, updateViews);
		}
	}
}
