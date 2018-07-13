package ritwik.sample.gps.utilities;

import android.content.Context;
import android.widget.Toast;

public class UtilityMethods {
	public static void showToastMessage ( Context context, String message ) {
		Toast.makeText ( context, message, Toast.LENGTH_SHORT ).show ();
	}
}