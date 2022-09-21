package com.vulci.suedtirolwetter;


import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class ConfigureWidgetActivity extends Activity implements
		View.OnClickListener {

	@SuppressWarnings("unused")
	private static final String LOG_TAG = ConfigureWidgetActivity.class
			.getSimpleName();
	private int mAppWidgetId = 0;
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		prefs = getSharedPreferences("suedtirol", MODE_PRIVATE);
		editor = prefs.edit();

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.widgetoption);

		Button button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(this);

		Button button2 = (Button) findViewById(R.id.button2);
		button2.setOnClickListener(this);

		Button button3 = (Button) findViewById(R.id.button3);
		button3.setOnClickListener(this);

		Button button4 = (Button) findViewById(R.id.button4);
		button4.setOnClickListener(this);

		Button button5 = (Button) findViewById(R.id.button5);
		button5.setOnClickListener(this);

		Button button6 = (Button) findViewById(R.id.button6);
		button6.setOnClickListener(this);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}
	}

	@Override
	public void onClick(View v) {

		int id = 0;

		switch (v.getId()) {
			case R.id.button1:
				id = 642;
				break;

			case R.id.button2:
				id = 101;
				break;

			case R.id.button3:
				id = 100;
				break;

			case R.id.button4:
				id = 109;
				break;

			case R.id.button5:
				id = 963;
				break;

			case R.id.button6:
				id = 110;
				break;
		}

		final String city = ((Button) v).getText().toString();

		editor.putInt("" + mAppWidgetId, id);
		editor.putString("city" + mAppWidgetId, city);
		editor.commit();

		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);

		final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		final ComponentName comp = new ComponentName(getPackageName(),
				MyWidgetProvider.class.getName());

		final int widgetId = id;
		new Thread(){
			public void run(){
				UpdateWidgetService.updateAppWidget(ConfigureWidgetActivity.this, appWidgetManager, comp, widgetId,
						mAppWidgetId, city);
			}
		}.start();


		finish();
	}

}
