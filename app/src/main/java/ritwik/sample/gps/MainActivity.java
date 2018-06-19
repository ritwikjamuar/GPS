package ritwik.sample.gps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class MainActivity
		extends AppCompatActivity
		implements LocationListener {
	private TextView mTvLocation;

	private LocationManager mManager;

	private List < String > mProviders;
	private String          mProvider;
	private String          mText;

	// Constants.
	private static final String   TAG                = MainActivity.class.getSimpleName ();
	private static final String   COARSE_LOCATION    = Manifest.permission.ACCESS_COARSE_LOCATION;
	private static final String   FINE_LOCATION      = Manifest.permission.ACCESS_FINE_LOCATION;
	private static final int      PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;
	private static final int      PERMISSION_CODE    = 101;
	private static final String[] PERMISSIONS        = new String[] {
			FINE_LOCATION,
			COARSE_LOCATION
	};

	/*************************** {@link android.app.Activity} Callbacks ***************************/

	@Override protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.activity_main );

		mTvLocation = findViewById ( R.id.activity_main_location );

		if ( ! isLocationPermissionEnabled () ) requestLocationPermission ();
		else continueApp ();
	}

	@Override protected void onResume () {
		super.onResume ();
		if (
				ActivityCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
		            &&
				ActivityCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED
		) { return; }
		if ( mProvider == null ) mProvider = LocationManager.GPS_PROVIDER;
		mManager.requestLocationUpdates ( LocationManager.PASSIVE_PROVIDER, 400, 1, MainActivity.this );
		mManager.requestLocationUpdates ( LocationManager.NETWORK_PROVIDER, 400, 1, MainActivity.this );
		mManager.requestLocationUpdates ( LocationManager.GPS_PROVIDER, 400, 1, MainActivity.this );
	}

	@Override protected void onPause () {
		super.onPause ();
		mManager.removeUpdates ( MainActivity.this );
	}

	@Override public void onRequestPermissionsResult (
			int requestCode,
			@NonNull String[] permissions,
			@NonNull int[] grantResults
	) {
		super.onRequestPermissionsResult ( requestCode, permissions, grantResults );
		if ( PERMISSION_CODE == requestCode ) {
			boolean isAllPermissionsGranted = false;

			for ( int permissionResult : grantResults ) {
				isAllPermissionsGranted = PERMISSION_GRANTED == permissionResult;
			}

			if ( isAllPermissionsGranted ) continueApp ();
			else {
				UtilityMethods.showToastMessage (
						MainActivity.this, "Without Location Permission this app can't run" );
				finish ();
			}
		}
	}

	/********************************** {@code private} Methods ***********************************/

	private void requestLocationPermission () {
		ActivityCompat.requestPermissions ( MainActivity.this, PERMISSIONS, PERMISSION_CODE );
	}

	private boolean isLocationPermissionEnabled () {
		return checkPermission ( FINE_LOCATION ) == PERMISSION_GRANTED
			       ||
		       checkPermission ( COARSE_LOCATION ) == PERMISSION_GRANTED;
	}

	private int checkPermission ( String permission ) {
		return ActivityCompat.checkSelfPermission ( this, permission );
	}

	private void continueApp () {
		mManager = ( LocationManager ) getSystemService ( Context.LOCATION_SERVICE );
		if ( mManager != null ) {
			if ( mManager.isProviderEnabled ( LocationManager.GPS_PROVIDER ) ) {
				// If last known location not found,
				// then get all providers and request location from each one of providers.

				if ( isLocationPermissionEnabled () ) {
					Location location = getLastKnownLocation ();
					if ( location != null ) {
						UtilityMethods.showToastMessage (
								MainActivity.this,
								"Provider " + mProvider + " has been selected!"
						);
						onLocationChanged ( location );
					} else {
						Criteria criteria = new Criteria ();
						mProvider = mManager.getBestProvider ( criteria, false );

						List < String > mProviders = mManager.getAllProviders ();
						if ( mProviders == null || mProviders.size () == 0 ) {
							mText = "Location Providers not available";
							mTvLocation.setText ( mText );
						} else {
							android.util.Log.e ( TAG, mProviders.toString () );
						}
					}
				}
			} else {
				Intent intent = new Intent ( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
				startActivity ( intent );
			}
		} else UtilityMethods.showToastMessage ( MainActivity.this, "Location Manager not available" );
	}

	private Location getLastKnownLocation () {
		Location        bestLocation = null;
		List < String > providers    = mManager.getAllProviders ();
		for ( String provider : providers ) {
			if ( ActivityCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
			        &&
			     ActivityCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED
			) { return null; }
			Location location = mManager.getLastKnownLocation ( provider );
			if ( location == null ) continue;
			if ( bestLocation == null || location.getAccuracy () > bestLocation.getAccuracy () ) {
				mProvider = provider;
				bestLocation = location;
			}
		}
		return bestLocation;
	}

	/***************************** {@link LocationListener} Callbacks *****************************/

	@Override public void onLocationChanged ( Location location ) {
		android.util.Log.e ( MainActivity.class.getSimpleName (), "onLocationChanged ()" );
		android.util.Log.e ( MainActivity.class.getSimpleName (), location.toString () );

		String locationText = location.getLatitude () + "," + location.getLongitude ();
		mTvLocation.setText ( locationText );
	}

	@Override public void onStatusChanged ( String s, int i, Bundle bundle ) {
		android.util.Log.e ( MainActivity.class.getSimpleName (), "onStatusChanged ()" );
		android.util.Log.e ( MainActivity.class.getSimpleName (), s );
		android.util.Log.e ( MainActivity.class.getSimpleName (), String.valueOf ( i ) );
		android.util.Log.e ( MainActivity.class.getSimpleName (), bundle.toString () );
	}

	@Override public void onProviderEnabled ( String s ) {
		android.util.Log.e ( MainActivity.class.getSimpleName (), "onProviderEnabled ()" );
		android.util.Log.e ( MainActivity.class.getSimpleName (), s );
	}

	@Override public void onProviderDisabled ( String s ) {
		android.util.Log.e ( MainActivity.class.getSimpleName (), "onProviderDisabled ()" );
		android.util.Log.e ( MainActivity.class.getSimpleName (), s );
	}
}