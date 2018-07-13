package ritwik.sample.gps.main.di;

import android.content.Context;
import android.location.LocationManager;

import dagger.Module;
import dagger.Provides;
import ritwik.sample.gps.main.MainActivity;
import ritwik.sample.gps.main.MainPresenter;
import ritwik.sample.gps.main.MainRepository;

@Module public class MainModule {
	private MainActivity mActivity;

	public MainModule ( MainActivity activity ) { this.mActivity = activity; }

	@Provides public MainActivity providesMainActivity () { return mActivity; }

	@Provides public LocationManager providesLocationManager () {
		return ( LocationManager ) mActivity.getSystemService ( Context.LOCATION_SERVICE );
	}

	@Provides public MainPresenter providesMainPresenter (
			MainRepository repository,
			LocationManager locationManager ) {
		return new MainPresenter ( mActivity, repository, locationManager );
	}
}