package com.shang.games.Utils;

import android.util.Log;

public class LogUtil {
	private static final String LOG_KEY = "Store";
	public static final boolean DEBUG_MODE = true;

	public static void v(String msg) {
		if (DEBUG_MODE)
			Log.v(LOG_KEY, msg);
	}

	public static void d(String msg) {
		if (DEBUG_MODE)
			Log.d(LOG_KEY, msg);
	}

	public static void i(String msg) {
		if (DEBUG_MODE)
			Log.i(LOG_KEY, msg);
	}

	public static void w(String msg) {
		if (DEBUG_MODE)
			Log.w(LOG_KEY, msg);
	}

	public static void e(String msg) {
		if (DEBUG_MODE)
			Log.e(LOG_KEY, msg);
	}
}
