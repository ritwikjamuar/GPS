package ritwik.sample.gps.main;

import android.Manifest;

import android.annotation.SuppressLint;

import android.content.pm.PackageManager;

import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;

import android.os.Bundle;

import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;

import butterknife.ButterKnife;
import ritwik.sample.gps.R;

import ritwik.sample.gps.common.BaseActivity;

import ritwik.sample.gps.utilities.UtilityMethods;
import ritwik.sample.gps.utilities.Utterances;

import ritwik.sample.gps.main.di.DaggerMainComponent;
import ritwik.sample.gps.main.di.MainComponent;
import ritwik.sample.gps.main.di.MainModule;

@SuppressLint ( "MissingPermission" ) public class MainActivity
		extends BaseActivity
		implements MainContract.View {
	// Views.
	@BindView ( R.id.activity_main_location ) TextView mTvLocation;

	// Presenter.
	@Inject MainPresenter mPresenter;

	// Constants.
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
		initializeViews ();
		injectActivity ();
		mPresenter.continueApp ();
	}

	@Override protected void onResume () {
		super.onResume ();
	}

	@Override protected void onPause () {
		super.onPause ();
		/*mManager.removeUpdates ( MainActivity.this );*/
	}

	@Override public void onRequestPermissionsResult (
			int requestCode,
			@NonNull String [] permissions,
			@NonNull int [] grantResults ) {
		super.onRequestPermissionsResult ( requestCode, permissions, grantResults );
		if ( PERMISSION_CODE == requestCode ) {
			boolean isAllPermissionsGranted = false;

			for ( int permissionResult : grantResults ) {
				isAllPermissionsGranted = PERMISSION_GRANTED == permissionResult;
			}

			if ( isAllPermissionsGranted ) mPresenter.requestLocation ();
			else {
				showToast ( Utterances.LOCATION_PERMISSION_DENIED );
				finish ();
			}
		}
	}

	/********************************** {@code private} Methods ***********************************/

	private void initializeViews () {
		ButterKnife.bind ( MainActivity.this );
	}

	private void injectActivity () {
		MainComponent component =
				DaggerMainComponent
						.builder ()
						.mainModule ( new MainModule ( MainActivity.this ) )
						.build ();
		component.injectMainActivity ( MainActivity.this );
	}

	private boolean isLocationPermissionEnabled () {
		return checkPermission ( FINE_LOCATION ) == PERMISSION_GRANTED
			       ||
		       checkPermission ( COARSE_LOCATION ) == PERMISSION_GRANTED;
	}

	private int checkPermission ( String permission ) {
		return ActivityCompat.checkSelfPermission ( this, permission );
	}

	/***************************** {@link MainContract.View} Callbacks ****************************/

	@Override public boolean isLocationPermissionGranted () { return isLocationPermissionEnabled (); }

	@Override public void requestLocationPermission () {
		ActivityCompat.requestPermissions (
				MainActivity.this,
				PERMISSIONS,
				PERMISSION_CODE
		);
	}

	@Override public void setText ( String text ) { mTvLocation.setText ( text ); }

	@Override public void showToast ( String message ) {
		UtilityMethods.showToastMessage ( MainActivity.this, message );
	}
}