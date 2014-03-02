package com.example.webloader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

public class GetPlaces extends AsyncTask<String, Void, String> {
	// fetch and parse place data
	@Override
	protected String doInBackground(String... placesURL) {
		// fetch places
		StringBuilder placesBuilder = new StringBuilder();
		String json;

		// process search parameter string(s)
		for (String placeSearchURL : placesURL) {
			HttpClient placesClient = new DefaultHttpClient();
			try {
				// try to fetch data
				HttpGet placesGet = new HttpGet(placeSearchURL);
				HttpResponse placesResponse = placesClient.execute(placesGet);
				StatusLine placeSearchStatus = placesResponse.getStatusLine();
				if (placeSearchStatus.getStatusCode() == 200) {
					// received the go-ahead
					HttpEntity placesEntity = placesResponse.getEntity();
					InputStream placesContent = placesEntity.getContent();
					InputStreamReader placesInput = new InputStreamReader(
							placesContent);
					BufferedReader placesReader = new BufferedReader(
							placesInput);
					String lineIn;
					while ((lineIn = placesReader.readLine()) != null) {
						placesBuilder.append(lineIn);
					}
					//
					//json = EntityUtils.toString(placesEntity);
					//
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return placesBuilder.toString();
		// execute search
	}

	protected void onPostExecute(String jsonResults) {
			
	}
}