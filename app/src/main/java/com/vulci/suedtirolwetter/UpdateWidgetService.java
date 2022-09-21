package com.vulci.suedtirolwetter;


import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.vulci.suedtirolwetter.sax.ProvBulletin;
import com.vulci.suedtirolwetter.sax.StationData;

import org.apache.http.client.ClientProtocolException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class UpdateWidgetService extends IntentService {
	private static final String LOG_TAG = UpdateWidgetService.class
			.getSimpleName();

	public UpdateWidgetService() {
		super("UpdateWidgetService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(LOG_TAG, "onStart");

		ComponentName componentName = new ComponentName(this,
				MyWidgetProvider.class);
		AppWidgetManager mgr = AppWidgetManager.getInstance(this);
		int  appWidgetId = -1;

		Bundle extras = intent.getExtras();
		if (extras != null) {

			Log.d(LOG_TAG, "Extras: " + intent.getExtras().keySet());

			appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
			Log.d(LOG_TAG, "appWidgetId: " + appWidgetId);


			int selectedStationId = extras.getInt("open_browser", -1);
			if(selectedStationId>=0){
				Intent clickIntent = new Intent(this,
						MainActivity.class);
				clickIntent.putExtra("stationId", selectedStationId);
				clickIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(clickIntent);
			}
		}

		updateAppWidget(this, mgr, componentName, 642, appWidgetId, "");
	}

	public static void updateAppWidget(Context context,
									   AppWidgetManager appWidgetManager, ComponentName componentName,
									   int stationId, int configWidgetId, String city) {
		SharedPreferences prefs = context.getSharedPreferences("suedtirol",
				MODE_PRIVATE);
		HashMap<Integer, StationData> stationDataMap = null;
		ProvBulletin provBulletin = null;

		if (SuedtirolWeatherApplication.isDataConnected(context)) {

			String myDisplayLang = Locale.getDefault().getLanguage();

			Log.d(LOG_TAG, "Locale: " + myDisplayLang);

			try {
				provBulletin = SuedtirolWeatherApplication
						.ladeWetterdaten(myDisplayLang);
				stationDataMap = provBulletin.getStationDataMap();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			int[] appWidgetIds = appWidgetManager
					.getAppWidgetIds(componentName);
			int length = appWidgetIds.length;

			for (int i = 0; i < length; ++i) {
				RemoteViews remoteViews = new RemoteViews(context
						.getApplicationContext().getPackageName(),
						R.layout.widget_layout);

				int appWidgetId = appWidgetIds[i];
				String cityName = prefs.getString("city" + appWidgetId, "");
				int selectedStationId = prefs.getInt("" + appWidgetId, -1);

				if (selectedStationId < 0) {
					selectedStationId = stationId;
					appWidgetId = configWidgetId;
				}

				if (TextUtils.isEmpty(cityName)) {
					cityName = city;
				}

				Log.d("XXX", "id: " + selectedStationId);

				StationData stationData = stationDataMap.get(selectedStationId);

				remoteViews.setTextViewText(R.id.textViewdescription,
						stationData.getSymbolData().getDescription());

				remoteViews
						.setTextViewText(
								R.id.textViewWetterdatenUeberschrift,
								cityName
										+ " - "
										+ SuedtirolWeatherApplication
										.formatTimestamp(provBulletin
												.getDate()));

				remoteViews.setTextViewText(R.id.textViewHoechsttemperatur,
						"Max : " + stationData.getTemperatureData().getMax()
								+ " �C");
				remoteViews.setTextViewText(R.id.textViewTiefsttemperatur,
						"Min : " + stationData.getTemperatureData().getMin()
								+ " �C");

				String imageUrl = stationData.getSymbolData().getImageUrl();

				if (!TextUtils.isEmpty(imageUrl)) {
					String icon = imageUrl.substring(
							imageUrl.lastIndexOf("/") + 1,
							imageUrl.lastIndexOf('.'));
					int path = context.getResources().getIdentifier(icon,
							"drawable", context.getPackageName());
					remoteViews.setImageViewResource(R.id.imageViewIcon, path);
				}

				/*Intent clickIntent = new Intent(context,
						SuedtirolWetterActivity.class);
				clickIntent.putExtra("stationId", selectedStationId);*/

				Intent clickIntent = new Intent(context, UpdateWidgetService.class);
				clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
				clickIntent.putExtra("open_browser", selectedStationId);

				PendingIntent pendingIntent = PendingIntent.getService(
						context, (int) System.currentTimeMillis(), clickIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				remoteViews.setOnClickPendingIntent(R.id.layout, pendingIntent);
				appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
			}
		} else {
			Log.d(LOG_TAG, "no internet");
		}

	}
}
