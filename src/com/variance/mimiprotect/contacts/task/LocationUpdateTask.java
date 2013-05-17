package com.variance.mimiprotect.contacts.task;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.variance.mimiprotect.util.Settings;

/**
 * Asynchronously updates the current longitude, latitude location
 * 
 * @author marembo
 * 
 */
public class LocationUpdateTask extends AsyncTask<String, Void, String>
		implements LocationListener {
	private Activity context;

	public LocationUpdateTask(Activity context) {
		super();
		this.context = context;
	}

	@Override
	protected String doInBackground(String... arg0) {
		try {
			context.runOnUiThread(new Runnable() {

				public void run() {
					LocationManager locationManager = (LocationManager) context
							.getSystemService(Context.LOCATION_SERVICE);
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, 0, 0,
							LocationUpdateTask.this);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void onLocationChanged(Location location) {
		Settings.setCurrentLatitude(location.getLatitude());
		Settings.setCurrentLongitude(location.getLongitude());
		Log.e("Latitude: ", "" + location.getLatitude());
		Log.e("Longitude: ", "" + location.getLongitude());

	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

}
