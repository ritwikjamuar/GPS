package ritwik.sample.gps.main.di;

import dagger.Component;
import ritwik.sample.gps.main.MainActivity;

@MainScope @Component ( modules = MainModule.class ) public interface MainComponent {
	void injectMainActivity ( MainActivity activity );
}