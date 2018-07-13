package ritwik.sample.gps.main;

import android.annotation.SuppressLint;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;

import java.util.List;

import javax.inject.Inject;

import ritwik.sample.gps.common.BasePresenter;

import ritwik.sample.gps.utilities.Utterances;

public class MainPresenter
		extends BasePresenter
		implements MainContract.Action {
	private MainContract.View mView;
	@SuppressWarnings ( "FieldCanBeLocal" ) private MainRepository    mRepository;
	private LocationManager   mLocationManager;

	// Constants.
	private static final String TAG = MainPresenter.class.getSimpleName ();

	private interface LocationRequest {
		int MINIMUM_TIME = 0;
		int MINIMUM_DISTANCE = 100;
	}

	@Inject public MainPresenter (
			MainContract.View view,
			MainRepository repository,
			LocationManager locationManager ) {
		this.mView = view;
		this.mRepository = repository;
		this.mRepository.onAttach ( MainPresenter.this );
		this.mLocationManager = locationManager;
	}

	/***************************** {@link LocationListener} Callbacks *****************************/

	private LocationListener mLocationListener = new LocationListener () {
		@Override public void onLocationChanged ( Location location ) {
			android.util.Log.e ( TAG, "onLocationChanged ()" );
			android.util.Log.e ( TAG, location.toString () );
			android.util.Log.e ( TAG, location.getProvider () );

			String locationText = location.getLatitude () + "," + location.getLongitude ();
			mView.setText ( locationText );
		}

		@Override public void onStatusChanged ( String s, int i, Bundle bundle ) {
			android.util.Log.e ( TAG, "onStatusChanged ()" );
			android.util.Log.e ( TAG, s );
			android.util.Log.e ( TAG, String.valueOf ( i ) );
			android.util.Log.e ( TAG, bundle.toString () );
		}

		@Override public void onProviderEnabled ( String s ) {
			android.util.Log.e ( TAG, "onProviderEnabled ()" );
			android.util.Log.e ( TAG, s );
		}

		@Override public void onProviderDisabled ( String s ) {
			android.util.Log.e ( TAG, "onProviderDisabled ()" );
			android.util.Log.e ( TAG, s );
		}
	};

	/**************************** {@link MainContract.Action} Callbacks ***************************/

	@Override public void continueApp () {
		if ( ! mView.isLocationPermissionGranted () ) mView.requestLocationPermission ();
		else requestLocation ();
	}

	@SuppressLint ( "MissingPermission" ) @Override public void requestLocation () {
		List< String > providers = mLocationManager.getAllProviders ();
		if ( providers == null || providers.size () == 0 ) {
			mView.setText ( Utterances.NO_PROVIDERS );
		} else {
			for ( String provider : providers ) {
				if ( mLocationManager.isProviderEnabled ( provider ) ) {
					mLocationManager.requestLocationUpdates (
							provider,
							LocationRequest.MINIMUM_TIME,
							LocationRequest.MINIMUM_DISTANCE,
							mLocationListener
					);
					mLocationManager.getLastKnownLocation ( provider );
				} else {
					mView.showToast ( provider + " " + Utterances.PROVIDER_DISABLED );
					android.util.Log.e ( TAG, provider + " " + Utterances.PROVIDER_DISABLED );
				}
			}
		}
	}
}