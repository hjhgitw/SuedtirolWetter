package com.vulci.suedtirolwetter;



import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;


public class MyWidgetProvider extends AppWidgetProvider {

	private static final String LOG = "de.vogella.android.widget.example";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
						 int[] appWidgetIds) {

		Log.d(LOG, "onUpdate method called");
		// Get all ids

	ComponentName thisWidget = new ComponentName(context,
			MyWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

		// Build the intent to call the service
		Intent intent = new Intent(context.getApplicationContext(),
				UpdateWidgetService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

		// Update the widgets via the service
		context.startService(intent);
	}

	public void onEnabled(Context context, AppWidgetManager appWidgetManager,
						  int[] appWidgetIds) {
		super.onEnabled(context);

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
	}

}
