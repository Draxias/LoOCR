package com.example.webloader;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

public class WebloaderActivity extends Activity {
	private final int CAMERA_PIC_REQUEST = 1337;
	private Bitmap basicBit, convertedMap;
	String one = "blank", two = "blank", three = "blank";
	String jsonResults;
	static TextView text;
	Button retake, browser, searchMap, searchPlaces, filterSearch;
	Context context;
	int count;
	int distance;
	int[] legend = new int[5];
	String[] entry = new String[5];

	BuildURL httpCall;
	GetPlaces lookup;

	HashMap<Integer, Places> placesMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webloader);
		text = (TextView) findViewById(R.id.view);
		retake = (Button) findViewById(R.id.basic);
		browser = (Button) findViewById(R.id.webtime);
		searchMap = (Button) findViewById(R.id.mapSearch);
		searchPlaces = (Button) findViewById(R.id.placesSearch);
		filterSearch = (Button) findViewById(R.id.filtered);

		context = this;
		// Intent cameraIntent = new Intent(
		// android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		// startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);

		retake.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent retakeIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(retakeIntent, CAMERA_PIC_REQUEST);

			}
		});

		filterSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent retakeIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(retakeIntent, 1336);

			}
		});

		browser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				 String search = text.getText().toString();
				 search.replace(" ", "+");
				 search.replace("\n", "+");
				 Uri uri = Uri.parse("https://www.google.com/#q=" + search);
				
				 Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				 startActivity(intent);

			}
		});

		searchMap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					Class<?> ourClass = Class
							.forName("com.example.webloader.MapTime");
					Intent maps = new Intent(WebloaderActivity.this, ourClass);
					startActivity(maps);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});

		searchPlaces.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				constructNearby();

				String placeList = "There are ";
				placeList = placeList.concat(Integer.toString(placesMap.size())
						+ " places.\n");
				for (int i = 1; i <= placesMap.size(); i++) {
					placeList = placeList.concat("Place #" + i + ": Name=");
					placeList = placeList.concat(placesMap.get(i).getName()
							+ ",  Address=");
					placeList = placeList.concat(placesMap.get(i).getVic()
							+ " .\n");
				}
				text.setText(placeList);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1336) {
			// Get camera data
			basicBit = (Bitmap) data.getExtras().get("data");
			// Build string data with Tesseract
			/***/
			File myDir = Environment.getExternalStorageDirectory();
			TessBaseAPI baseApi = new TessBaseAPI();
			String DATA_PATH = myDir.toString() + "/";
			String lang = "eng";
			baseApi.init(DATA_PATH, lang, 2);
			baseApi.setVariable("tessedit_char_blacklist", "/@*()\"");
			baseApi.setPageSegMode(1);
			convertedMap = convert(basicBit, Bitmap.Config.ARGB_8888);//convert to greyscale
			baseApi.setImage(convertedMap);
			String recognizedText = baseApi.getUTF8Text();
			recognizedText.toLowerCase(Locale.ENGLISH);
			baseApi.end();

			// Build list of places
			constructNearby();
			// initialize arrays
			for (int k = 0; k < 5; k++) {
				legend[k] = 99;
			}
			for (int k = 0; k < 5; k++) {
				entry[k] = "";
			}
			int max = 0, min = 0;
			int i, j;

			// Find the top 5 - MAIN LOOP
			for (i = 1; i <= placesMap.size(); i++) {
				// set max to the index of the maximum value in the array
				for (j = 0; j < legend.length - 1; j++) {
					if (legend[j] >= legend[max]) {
						max = j;
					}
				}
				// get distance from  name
				distance = LevenshteinDistance.computeLD(recognizedText,
						placesMap.get(i).getName());
				if (distance < legend[max]) {// if distance is less than the
												// value at index max:
					legend[max] = distance;// replace that value with distance;
					entry[max] = placesMap.get(i).getName();// enter that
															// place's name into
															// entry[] at index
															// max
				}
				// find new maximum distance
				for (j = 0; j < legend.length - 1; j++) {
					if (legend[j] >= legend[max]) {
						max = j;
					}
				}
				// get distance from vicinity
				distance = LevenshteinDistance.computeLD(recognizedText,
						placesMap.get(i).getVic());
				if (distance < legend[max]) {
					legend[max] = distance;
					entry[max] = placesMap.get(i).getVic();
				}
			}// END MAIN LOOP

			// find the smallest distance
			for (j = 0; j < legend.length - 1; j++) {
				if (legend[j] <= legend[min]) {
					min = j;
				}
			}
			text.setText("Original text: " + recognizedText
					+ ". Top 5 guesses: " + entry[0] + " , " + entry[1] + " , "
					+ entry[2] + " , " + entry[3] + " , " + entry[4]
					+ "; the most accurate choice is " + min);
			// popMenu(entry, this);
			// Toast t = Toast.makeText(getApplicationContext(),
			// "Cannot retrieve location. Check settings.",
			// Toast.LENGTH_SHORT);
			// t.show();
		} else if (requestCode == CAMERA_PIC_REQUEST) {// Unfiltered
			// Get camera data
			basicBit = (Bitmap) data.getExtras().get("data");
			// Build string data with Tesseract
			/***/
			File myDir = Environment.getExternalStorageDirectory();
			TessBaseAPI baseApi = new TessBaseAPI();
			String DATA_PATH = myDir.toString() + "/";
			String lang = "eng";
			baseApi.init(DATA_PATH, lang, 2);
			baseApi.setVariable("tessedit_char_blacklist", "[]{}˜/@*()\"");
			// baseApi.setPageSegMode(1);
			convertedMap = convert(basicBit, Bitmap.Config.ARGB_8888);
			baseApi.setImage(convertedMap);
			String recognizedText = baseApi.getUTF8Text();
			text.setText(recognizedText);
			baseApi.end();
		}
	}

	private void constructNearby() {
		jsonResults = null;
		httpCall = new BuildURL(context);
		try {
			jsonResults = new GetPlaces().execute(httpCall.getURL()).get(10L,
					TimeUnit.SECONDS);// give generous 10 seconds for results.
		} catch (InterruptedException e) {
			text.setText("Interruption Error");
			e.printStackTrace();
		} catch (ExecutionException e) {
			text.setText("Execution Error");
			e.printStackTrace();
		} catch (TimeoutException e) {
			text.setText("Operation timed out.");
			e.printStackTrace();
		} catch (StringIndexOutOfBoundsException s) {
			s.printStackTrace();
		} catch (Exception e) {
		} finally {
			if (jsonResults.isEmpty()) {// An error occurred
				text.setText("Lookup Failed");
			} else {// successful call, parse json
				try {
					int startIndex = 0, endIndex = 0, keyCount = 1;
					String findName = "name", findVic = "vicinity", retName, retVic;
					placesMap = new HashMap();
					while (startIndex != -1) {
						Places entry = new Places();
						// NAME
						// find the next instance of tag "name"
						startIndex = jsonResults.indexOf(findName, startIndex);
						if (startIndex != -1) {// check if EOF
							// move index to the beginning of desired value
							startIndex = jsonResults.indexOf(":", startIndex) + 3;
							// establish the ending index of the value
							endIndex = jsonResults.indexOf("\"", startIndex);
							// set name
							retName = jsonResults.substring(startIndex,
									endIndex).toLowerCase(Locale.ENGLISH);
							entry.setName(retName);

							// VICINITY
							// move index to the beginning of desired value
							startIndex = jsonResults.indexOf(findVic,
									endIndex + 1);
							startIndex = jsonResults.indexOf(":", startIndex) + 3;
							// establish the ending index of the value
							endIndex = jsonResults.indexOf("\"", startIndex);
							// set vic
							retVic = jsonResults
									.substring(startIndex, endIndex)
									.toLowerCase(Locale.ENGLISH);
							entry.setVic(retVic);

							placesMap.put(keyCount, entry);
							keyCount++;
						}
					}
				} catch (StringIndexOutOfBoundsException s) {
					s.printStackTrace();
				}
			}
		}
	}

	protected void onResume() {
		super.onResume();
	}

	private Bitmap convert(Bitmap bitmap, Bitmap.Config config) {
		/** Shift to greyscale for better results **/

		Bitmap convertedBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), config);
		Canvas canvas = new Canvas(convertedBitmap);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		return convertedBitmap;
	}

	//
	// Popup menu snippet - WIP
	// private void popMenu(String[] entries, Context context) {
	// PopupMenu popupMenu;
	// popupMenu = new PopupMenu(this, findViewById(R.id.view));
	//
	// popupMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, entries[0]);
	// popupMenu.getMenu().add(Menu.NONE, 2, Menu.NONE, entries[1]);
	// popupMenu.getMenu().add(Menu.NONE, 3, Menu.NONE, entries[2]);
	// popupMenu.getMenu().add(Menu.NONE, 4, Menu.NONE, entries[3]);
	// popupMenu.getMenu().add(Menu.NONE, 5, Menu.NONE, entries[4]);
	// popupMenu.setOnMenuItemClickListener((OnMenuItemClickListener) this);
	// popupMenu.show();
	// }

	// public boolean onMenuItemClick(MenuItem item) {
	// switch (item.getItemId()) {
	// case 1:
	// text.setText(entry[0]);
	// break;
	// case 2:
	// text.setText(entry[1]);
	// break;
	// case 3:
	// text.setText(entry[2]);
	// break;
	// case 4:
	// text.setText(entry[3]);
	// break;
	// case 0:
	// text.setText(entry[4]);
	// break;
	// }
	// return false;
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.webloader, menu);
		return true;
	}

}
