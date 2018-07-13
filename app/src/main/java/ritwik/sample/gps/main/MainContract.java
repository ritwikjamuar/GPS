package ritwik.sample.gps.main;

public interface MainContract {
	interface View {
		boolean isLocationPermissionGranted ();
		void requestLocationPermission ();
		void setText ( String text );
		void showToast ( String message );
	}

	interface Action {
		void continueApp ();
		void requestLocation ();
	}
}