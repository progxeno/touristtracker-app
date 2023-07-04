package htwg.trackingapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class UpdateHandler {

	private String url;
	private ReleaseVersionListener listener;
	private static final String JSON_RELEASE_TAG = "release";
	
	public interface ReleaseVersionListener {
		public void onReleaseVersionReceived(int releaseVersion);
		public void onReleaseVersionError(Exception e);
	}

	public UpdateHandler(String url, ReleaseVersionListener listener) {
		this.url = url;
		this.listener = listener;
	}
	
	public void checkReleaseVersion() {
		new ReleaseVersionLoader().execute(url);
	}

	private class ReleaseVersionLoader extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... urls) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpRequest = new HttpGet(urls[0]);
            BufferedReader in;
			String jsonResponse;
			int release = 0;
			try {
				HttpResponse response = httpClient.execute(httpRequest);
				in = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = in.readLine()) != null) {
					sb.append(line + "\n");
				}
				in.close();
				jsonResponse = sb.toString();
				JSONObject json = new JSONObject(jsonResponse);
				release = json.getInt(JSON_RELEASE_TAG);
			} catch (ClientProtocolException e) {
                Log.d("catch1", "");
				listener.onReleaseVersionError(e);
			} catch (IOException e) {
                super.onCancelled();
                new ReleaseVersionLoader().execute("http://htwg-tourist-tracker.herokuapp.com/release");
				//listener.onReleaseVersionError(e);
			} catch (JSONException e) {
                Log.d("catch3", "");
				listener.onReleaseVersionError(e);
			}
			return release;
		}

		@Override
		protected void onPostExecute(Integer result) {
			listener.onReleaseVersionReceived(result);
		}
	}
}
