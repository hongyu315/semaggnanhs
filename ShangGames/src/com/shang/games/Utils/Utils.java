package com.shang.games.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebSettings;

/**
 * Created by rain on 15/3/16.
 */
public class Utils {

	/**
	 * 开始下载
	 *
	 * @param mActivity
	 * @param downloadUrl
	 */
	public static void startDownload(Context mActivity, String downloadUrl) {
		try {
			Uri uri = Uri.parse(downloadUrl.toString());
			Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
			mActivity.startActivity(downloadIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 分享文字
	 * 
	 * @param context
	 * @param title
	 * @param text
	 */
	public static void shareText(Context context, String title, String text) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, title);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		context.startActivity(Intent.createChooser(intent, title));
	}

	public static void choosePic(Activity context) {
		Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
		String IMAGE_UNSPECIFIED = "image/*";
		innerIntent.setType(IMAGE_UNSPECIFIED); // 查看类型
		Intent wrapperIntent = Intent.createChooser(innerIntent, "上传方式选择");
		if (Build.VERSION.SDK_INT < 19) {
			context.startActivityForResult(wrapperIntent, 2);
		} else {
			context.startActivityForResult(wrapperIntent, 3);
		}
	}

	public static void setWebViewSize(int size, WebSettings settings) {
		switch (size) {
		case 0:
			settings.setTextSize(WebSettings.TextSize.SMALLER);
			break;
		case 1:// normal
			settings.setTextSize(WebSettings.TextSize.NORMAL);
			break;
		case 2:// larger
			settings.setTextSize(WebSettings.TextSize.LARGER);
			break;
		case 3:// largerest
			settings.setTextSize(WebSettings.TextSize.LARGEST);
			break;
		default:
			settings.setTextSize(WebSettings.TextSize.NORMAL);
			break;
		}
	}

	public static void startHome(Context ctx) {
		Intent home = new Intent(Intent.ACTION_MAIN);
		home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		home.addCategory(Intent.CATEGORY_HOME);
		ctx.startActivity(home);
	}

	/**
	 * 判断是否联网
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isOnline(Context context) {
		try {
			ConnectivityManager connMgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			return (networkInfo != null && networkInfo.isConnected());
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	/**
	 * 判断是否wifi在线
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiLine(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return networkInfo.isConnected();
	}

	/**
	 * 判断是否手机网络在线
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isMobieLine(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return networkInfo.isConnected();
	}

	// public static boolean is2Gnetwork(Context context) {
	// String type = getNetworkType(context);
	// if (!StringUtil.isEmpty(type) && type.endsWith("2G")) {
	// return true;
	// }
	// return false;
	// }

	// 获取当前应用的版本号：
	public static int getVersionCode(Context ctx) {
		// 获取packagemanager的实例
		PackageManager packageManager = ctx.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(ctx.getPackageName(),
					0);
			return packInfo.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}
