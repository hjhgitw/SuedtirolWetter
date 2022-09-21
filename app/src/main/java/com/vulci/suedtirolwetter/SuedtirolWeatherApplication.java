package com.vulci.suedtirolwetter;


import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.vulci.suedtirolwetter.sax.ProvBulletin;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

public class SuedtirolWeatherApplication extends Application {

	private static final String LOG = SuedtirolWeatherApplication.class
			.getSimpleName();

	@Override
	public void onCreate() {

		super.onCreate();
	}

	public static ProvBulletin ladeWetterdaten(String lang)
			throws SAXException, ClientProtocolException, IOException {
		DefaultHttpClient sClient = getNewHttpClient();
		sClient.setCredentialsProvider(getCredentials());
		ProvBulletin provBulletin = new ProvBulletin();

		Log.w(LOG, "Fetching " + lang);
		HttpGet request = new HttpGet(
				"https://wetter.ws.siag.it/Weather_V1.svc/web/getLastProvBulletin?lang="
						+ lang);

		HttpResponse response = sClient.execute(request);

		StatusLine status = response.getStatusLine();
		Log.w(LOG, "Request returned status " + status.getStatusCode());
		if (status.getStatusCode() != HttpStatus.SC_OK) {
			return provBulletin;
		}

		HttpEntity entity = response.getEntity();
		String content = convertStreamToString(entity.getContent());
		Log.w(LOG, "" + content);
		Reader reader = new Reader(content) {
			@Override
			public int read(char[] cbuf, int off, int len) throws IOException {
				return 0;
			}

			@Override
			public void close() throws IOException {

			}
		};

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

	public static CredentialsProvider getCredentials() {
		String creds = "hhofer";
		Credentials loginCredentials = new UsernamePasswordCredentials(creds,
				creds);
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		AuthScope authScope = new AuthScope(AuthScope.ANY_HOST,
				AuthScope.ANY_PORT);
		credsProvider.setCredentials(authScope, loginCredentials);

		return credsProvider;
	}

	public static class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
											   String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
											   String authType) throws CertificateException {
				}

				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

				}

				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
								   boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	public static DefaultHttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	public static String convertStreamToString(InputStream is)
			throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		return sb.toString();
	}

}
