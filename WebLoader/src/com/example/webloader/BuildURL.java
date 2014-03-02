package com.example.webloader;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class BuildURL {
	String url;
	private LocationManager locMan;
	private LocationListener locList;
	private Location currentLoc;
	
	Criteria criteria;
	String bestProvider;
	Context thisCont;
	
	private long minTime = 1000;
	private float minDistance = (float) 10;
	private double lat, lng;
	// EXAMPLE1:
	// http://maps.googleapis.com/maps/api/place/search/json?location=40.717859,-73.9577937&radius=1600&client=clientId&sensor=true_or_false&signature=SIGNATURE

	//WORKING EXAMPLE
	//https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&types=food&name=harbour&sensor=false&key=************************************
	
	//MY URL
	//https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.867,151.206&radius=150&sensor=false&key=***************************
	
	private static String placesRadius = "1000"; // distance in meters
	private static String placesLocation;
	private static String mapsAPI = "************************************8***"; // browser
																				// api key;

	public BuildURL(Context context) {
		thisCont = context;
		locMan = (LocationManager) thisCont.getSystemService(Context.LOCATION_SERVICE);
		locList = new LocationListener() {
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}

			public void onLocationChanged(Location loc) {
				Criteria criteria = new Criteria();
				String bestProvider = locMan.getBestProvider(criteria, false);
				loc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (loc != null) {
					lng = loc.getLongitude();
					lat = loc.getLatitude();
					locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
							minTime, minDistance, locList);
				} else {
					Toast t = Toast.makeText(thisCont.getApplicationContext(),
							"Cannot retrieve location. Check settings.",
							Toast.LENGTH_SHORT);
					t.show();
				}
			}
		};

		criteria = new Criteria();
		bestProvider = locMan.getBestProvider(criteria, false);
		currentLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (currentLoc != null) {
			lng = currentLoc.getLongitude();
			lat = currentLoc.getLatitude();
			locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					minTime, minDistance, locList);
		} else {
			Toast t = Toast.makeText(thisCont.getApplicationContext(),
					"Cannot retrieve location. Check settings.",
					Toast.LENGTH_SHORT);
			t.show();
		}
		url = createURL();

	}

	public String getURL() {
		return url;
	}

	public String createURL() {
		// currentLoc.setLatitude(lat);
		// currentLoc.setLongitude(lng);
		// placesLocation = currentLoc.getLatitude() + ","
		// + currentLoc.getLongitude();
		String finalURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
				+ currentLoc.getLatitude()
				+ ","
				+ currentLoc.getLongitude()
				+ "&radius="
				+ placesRadius
				+ "&sensor=true&key=" + mapsAPI;
		// Convert the string to a URL so we can parse it
		return finalURL;

	}

	
	
}