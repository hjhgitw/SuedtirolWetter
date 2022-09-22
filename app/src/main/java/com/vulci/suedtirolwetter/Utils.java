package com.vulci.suedtirolwetter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utils {

	/**
	 * Check if there is any connectivity
	 *
	 * @param  context
	 * @return true if device is connected to any data network, false otherwise.
	 */
	public static boolean isDataConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}

	public static String formatTimestamp(String timestamp) {
		try {
			SimpleDateFormat fromDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss");
			SimpleDateFormat toDateFormat = new SimpleDateFormat(
					"MM.d.yyyy", Locale.getDefault());
			return toDateFormat.format(fromDateFormat.parse(timestamp));
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
	}

}
