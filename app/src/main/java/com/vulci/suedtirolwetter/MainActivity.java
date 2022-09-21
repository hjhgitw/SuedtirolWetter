package com.vulci.suedtirolwetter;

import static com.vulci.suedtirolwetter.SuedtirolWeatherApplication.convertStreamToString;
import static com.vulci.suedtirolwetter.SuedtirolWeatherApplication.getCredentials;
import static com.vulci.suedtirolwetter.SuedtirolWeatherApplication.getNewHttpClient;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.vulci.suedtirolwetter.databinding.ActivityMainBinding;
import com.vulci.suedtirolwetter.sax.ProvBulletin;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

	private static final String LOG = "de.vogella.android.widget.example";


	// Remove the below line after defining your own ad unit ID.
    private static final String TOAST_TEXT = "Test ads are being shown. "
            + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.";
    private static final String TAG = "MainActivity";

    private static final int START_LEVEL = 1;
    private int mLevel;
    private Button mNextLevelButton;
    private InterstitialAd mInterstitialAd;
    private TextView mLevelTextView;
    private ActivityMainBinding binding;

	/** Called when the activity is first created. */
	WebView mWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

		// Handle the splash screen transition.
	//	SplashScreen splashScreen = SplashScreen.installSplashScreen(this);


		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

			//https://guides.codepath.com/android/using-the-app-toolbar
		// Find the toolbar view inside the activity layout
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		// Sets the Toolbar to act as the ActionBar for this Activity window.
		// Make sure the toolbar exists in the activity and is not null
		setSupportActionBar(toolbar);
		TextView toolbar_title = findViewById(R.id.toolbar_title);
		toolbar_title.setText(getSupportActionBar().getTitle());
		getSupportActionBar().setTitle(null);

		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.setWebViewClient(new WebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setUserAgentString(
				Locale.getDefault().getLanguage());

		String baseUrl =  "https";

		String myUrl = "https://www.provinz.bz.it/wetter/mobile/";
		String myDisplayLang = Locale.getDefault().getDisplayLanguage()
				.toString();

		if (myDisplayLang.contentEquals("italiano")) {
			myUrl = "https://www.provincia.bz.it/meteo/mobile/";
		}else{
			myUrl = "https://www.provinz.bz.it/wetter/mobile/";
		}

		Bundle extras = getIntent().getExtras();
		if(extras!=null){
			int stationId = extras.getInt("stationId");
			mWebView = (WebView) findViewById(R.id.webview);
			mWebView.setWebViewClient(new WebViewClient());
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.getSettings().setUseWideViewPort(true);
			mWebView.getSettings().setUserAgentString(
					Locale.getDefault().getLanguage());
			 myUrl = "https://www.provinz.bz.it/wetter/mobile/";
			 myDisplayLang = Locale.getDefault().getDisplayLanguage()
					.toString();

			if (myDisplayLang.contentEquals("italiano")) {
				myUrl = "https://www.provincia.bz.it/meteo/mobile/";
			}else{
				myUrl = "https://www.provinz.bz.it/wetter/mobile/";
			}

			extras = getIntent().getExtras();
			if(extras!=null){
				stationId = extras.getInt("stationId");
				myUrl = myUrl + "akt_w.asp?stations_stid="+stationId;
			}

			mWebView.loadUrl(myUrl);
			myUrl = myUrl + "akt_w.asp?stations_stid="+stationId;
		}

		TextView toolbar_http= findViewById(R.id.toolbar_http);
		toolbar_http.setText(myUrl);

		mWebView.loadUrl(myUrl);



		///

        // Load the InterstitialAd and set the adUnitId (defined in values/strings.xml).
    //    loadInterstitialAd();

        // Create the next level button, which tries to show an interstitial when clicked.
        // Toasts the test ad message on the screen. Remove this after defining your own ad unit ID.
      //  Toast.makeText(this, TOAST_TEXT, Toast.LENGTH_LONG).show();
    }




	// Menu icons are inflated just as they were with actionbar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.menu_rate:
				goToMarket();
				return true;
			case R.id.menu_exit:
				System.exit(0);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

    private void showInterstitial() {
        // Show the ad if it"s ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
        }
    }


	/**
	 * Event Handling for Individual menu item selected
	 * Identify single menu item by it's id
	 * */
	public void goToMarket(){
		Uri uri = Uri.parse("market://details?id=" + getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		try {
			startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this,"couldnt launch market", Toast.LENGTH_LONG).show();
		}
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//moveTaskToBack(false);
			if(mWebView.canGoBack() == true){
				mWebView.goBack();
			}else
			{
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}



	public static ProvBulletin ladeWetterdaten(String lang)
			throws IOException, SAXException {
		DefaultHttpClient sClient = getNewHttpClient();
		sClient.setCredentialsProvider(getCredentials());
		ProvBulletin provBulletin = new ProvBulletin();

		Reader reader = null;
	//	Log.w(LOG, "Fetching " + lang);
		HttpGet request = new HttpGet(
				"https://wetter.ws.siag.it/Weather_V1.svc/web/getLastProvBulletin?lang="
						+ lang);

		HttpResponse response = sClient.execute(request);

		StatusLine status = response.getStatusLine();
//		Log.w(LOG, "Request returned status " + status.getStatusCode());
		if (status.getStatusCode() != HttpStatus.SC_OK) {
			return provBulletin;
		}

		HttpEntity entity = response.getEntity();
		String content = convertStreamToString(entity.getContent());
	//	Log.w(LOG, "" + content);
		reader = new StringReader(content);

		WetterdatenXmlHandler contentHandler = new WetterdatenXmlHandler(
				provBulletin);

		android.util.Xml.parse(reader, contentHandler);

		return provBulletin;
	}

	/**
	 * Check if there is any connectivity
	 *
	 * @param Context
	 *            context
	 * @return true if device is connected to any data network, false otherwise.
	 */
	public static boolean isDataConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}


}
