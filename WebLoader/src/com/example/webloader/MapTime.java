package com.example.webloader;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapTime extends Activity {

	LocationManager locMan;
	LocationListener locList;
	Location currentLoc;
	Location curloc;
	LatLng currentLtLg;

	Criteria criteria;
	String bestProvider;

	long minTime = 1000;
	float minDistance = (float) 10;
	double lng, lat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maptime);
		locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		curloc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (curloc != null) {
			lng = curloc.getLongitude();
			lat = curloc.getLatitude();
		}

		// Update map on location listener change
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
				loc = locMan
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (loc != null) {
					lng = loc.getLongitude();
					lat = loc.getLatitude();
					locMan.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, minTime,
							minDistance, locList);

				} else {
					Toast t = Toast.makeText(getApplicationContext(),
							"Cannot retrieve location. Check settings.",
							Toast.LENGTH_SHORT);
					t.show();
				}
			}
		};

		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();
		currentLtLg = new LatLng(lat, lng);
		map.setMyLocationEnabled(true);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLtLg, 18));

	}
}
