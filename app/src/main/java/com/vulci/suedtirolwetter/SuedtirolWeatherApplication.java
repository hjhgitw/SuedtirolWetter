package com.vulci.suedtirolwetter;


import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.vulci.suedtirolwetter.sax.ProvBulletin;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SuedtirolWeatherApplication extends Application {

	private static final String LOG = SuedtirolWeatherApplication.class
			.getSimpleName();

	@Override
	public void onCreate() {

		super.onCreate();
	}


	public static String getData(String lang){
		// 		//

		String url = "https://wetter.ws.siag.it/Weather_V1.svc/web/getLastProvBulletin?lang=" + lang;
		Log.w(LOG, "Fetching " + lang);


		OkHttpClient client = new OkHttpClient();


		Request request = new Request.Builder()
				.url(url)
				.build();

		try (Response response = client.newCall(request).execute()) {

						if (response.isSuccessful()){
							return response.body().string();
						}
			//TODO show error

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ProvBulletin ladeWetterdaten(String lang)
			throws SAXException, IOException {
			ProvBulletin provBulletin = new ProvBulletin();

		String content = getData(lang);
		Log.w(LOG, "" + content);

		Reader reader = new StringReader(content);

		WetterdatenXmlHandler contentHandler = new WetterdatenXmlHandler(
				provBulletin);

		android.util.Xml.parse(reader, contentHandler);

		return provBulletin;
	}



}
