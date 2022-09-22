package com.vulci.suedtirolwetter;


import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class Bootreceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		ComponentName componentName = new ComponentName(context,
				MyWidgetProvider.class);
		AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		int[] allWidgetIds = mgr.getAppWidgetIds(componentName);

		// Build the intent to call the service
		Intent i = new Intent(context.getApplicationContext(),
				UpdateWidgetService.class);
		i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

		// Update the widgets via the service
		context.startService(i);
	}
}
