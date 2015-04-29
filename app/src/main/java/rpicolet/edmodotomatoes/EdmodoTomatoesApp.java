package rpicolet.edmodotomatoes;

import android.app.Application;

public class EdmodoTomatoesApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		MovieVolley.init(this);
	}
}
