package com.shang.games.Activity;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shang.games.R;
import com.shang.games.Data.Globle;
import com.shang.games.Data.MyWebViewClient;
import com.shang.games.Interface.onWebviewLoadFinish;
import com.shang.games.Utils.BrightUtils;
import com.shang.games.Utils.LogUtil;
import com.shang.games.Utils.PrefUtil;
import com.shang.games.Utils.Utils;
import com.shang.games.Views.SegmentBar;
import com.shang.games.Views.SettingPopWindow;
import com.shang.games.http.AsyncHttpClient;
import com.shang.games.http.JsonHttpResponseHandler;
import com.shang.games.http.RequestParams;

public class MainActivity extends Activity {
	/**
	 * 顶部标题栏
	 */
	RelativeLayout titleLayout;
	WebView mWebView;
	/**
	 * 广告界面 404界面
	 */
	ImageView mImageView;
	TextView mTextView;
	TextView mTitle;
	/**
	 * 底部标签栏 轻触屏幕时出现
	 */
	RelativeLayout bottomLayout;
	/**
	 * 底部标签栏 出现时间 默认5秒 5秒后消失
	 */
	final int showBottomBarTime = 5000;

	mHandler handler;// = new mHandler();

	/**
	 * 首页广告倒计时 默认是5秒
	 */
	int time = 3;

	/**
	 * 60秒倒计时 模拟软件升级
	 */
	int recLen = 0;
	/**
	 * 计时器
	 */
	Timer timer = new Timer();

	/**
	 * 手指按下屏幕时间
	 */
	long downTouchTime = 0l;
	/**
	 * 手指弹起来时间
	 */
	long upTouchTime = 0l;

	PrefUtil prefUtil;

	/**
	 * 设置对话框
	 */
	SettingPopWindow settingPopWindow;

	/**
	 * 等待对话框
	 */
	ProgressBar mProgressBar;
	int mProgress;

	// RelativeLayout mainLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏
		setContentView(R.layout.activity_main);
		prefUtil = new PrefUtil(this);

		initViews();

		handler = new mHandler();
		if (getResources().getString(R.string.isNeedShowAD).equals("true")) {
			new mThread().run();
		} else {
			mTextView.setVisibility(View.GONE);
			mImageView.setVisibility(View.GONE);
		}

//		checkUpdate();
		 randomUpdate();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mWebView != null && Globle.isNeedShowBottomBar) {
			Utils.setWebViewSize(prefUtil.getInt(PrefUtil.TextSizePref, 1),
					mWebView.getSettings());
		}
	}

	private void initViews() {
		titleLayout = (RelativeLayout) findViewById(R.id.layout);
		mWebView = (WebView) findViewById(R.id.webView);
		mImageView = (ImageView) findViewById(R.id.imageView);
		mTextView = (TextView) findViewById(R.id.load_textView);
		mTitle = (TextView) findViewById(R.id.slidebar_gift_detail_title);
		bottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
		mProgressBar = (ProgressBar) findViewById(R.id.main_progress);
		// mainLayout = (RelativeLayout) findViewById(R.id.main_layout);

		mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Utils.isOnline(MainActivity.this)) {
					mWebView.reload();
				}else{
					mProgressBar.setVisibility(View.VISIBLE);
					mTextView.setVisibility(View.GONE);
				}
			}
		});

		String isNeedShowTitleBar = getResources().getString(
				R.string.isNeedShowTitleBar);
		titleLayout
				.setVisibility(isNeedShowTitleBar.equals("true") ? View.VISIBLE
						: View.GONE);

		/**
		 * 如果需要显示底部栏，则对网页轻触事件监听
		 */
		if (Globle.isNeedShowBottomBar) {
			mWebView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						downTouchTime = System.currentTimeMillis();
						break;
					case MotionEvent.ACTION_MOVE:
						break;
					case MotionEvent.ACTION_UP:
						upTouchTime = System.currentTimeMillis();
						if ((upTouchTime - downTouchTime) < 200) {
							bottomLayout.setVisibility(View.VISIBLE);
							Message msg = new Message();
							msg.arg1 = 1;
							handler.sendMessageDelayed(msg, showBottomBarTime);
						}
						break;
					}
					return false;
				}
			});
		}

		findViewById(R.id.webview_forword).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (Globle.webHistory.size() > 0) {
							Intent intent = new Intent();
							intent.setClass(MainActivity.this,
									ContentActivity.class);
							intent.putExtra("homeUrl",
									Globle.webHistory.getLast());
							Globle.webHistory.removeLast();
							startActivity(intent);
							// overridePendingTransition(R.anim.push_left_in,
							// R.anim.push_left_out);
						}
						// if (mWebView != null && mWebView.canGoForward())
						// mWebView.goForward();
					}
				});

		findViewById(R.id.webview_back).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mWebView != null && mWebView.canGoBack()) {
							mWebView.goBack();
						} else {
							onMyBackPressed();
						}
					}
				});

		findViewById(R.id.webview_refresh_btn).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mWebView != null)
							mWebView.reload();
					}
				});

		findViewById(R.id.slidebar_gift_detail_back).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onLogoClicked();
						// onMyBackPressed();
					}
				});

		findViewById(R.id.slidebar_gift_detail_back_to_game)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Utils.shareText(MainActivity.this, getResources()
								.getString(R.string.share_title),
								getResources()
										.getString(R.string.share_content));
					}
				});

		findViewById(R.id.webview_setting_btn).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onSettingBtnClick();
					}
				});

		WebSettings webSettings = mWebView.getSettings();

		webSettings.setJavaScriptEnabled(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webSettings.setBlockNetworkImage(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		// mWebView.setBackgroundResource(R.drawable.welcome1);

		Utils.setWebViewSize(prefUtil.getInt(PrefUtil.TextSizePref, 1),
				webSettings);

		mWebView.setBackgroundColor(Color.WHITE);
		mWebView.setWebViewClient(new MyWebViewClient(this, onLoadFinish));
		mWebView.setWebChromeClient(new WebChromeClient() {
			// For Android 3.0+
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String acceptType) {
				// if (mUploadMessage != null)
				// return;
				// mUploadMessage = uploadMsg;
				// choosePic();
			}

			// For Android < 3.0
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				openFileChooser(uploadMsg, "");
			}

			// For Android > 4.1.1
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String acceptType, String capture) {
				openFileChooser(uploadMsg, acceptType);
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				mTitle.setText(title);
			}

			public void onProgressChanged(WebView view, int progress) {
				setProgress(progress * 100);
				mProgress = progress;
				LogUtil.e("progrss = " + progress);
				if (progress == 100) {
					mProgressBar.setVisibility(View.GONE);
					view.setVisibility(View.VISIBLE);
				}else{
					view.setVisibility(View.GONE);
				}
			}
		});

		CookieSyncManager.createInstance(this);
		CookieManager.getInstance().removeAllCookie();

		mWebView.loadUrl("http://shanggames.com/news_list.php");
	}

//	/**
//	 * 清除WebView缓存
//	 */
//	public void clearWebViewCache() {
//
//		// 清理Webview缓存数据库
//		try {
//			deleteDatabase("webview.db");
//			deleteDatabase("webviewCache.db");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		// WebView 缓存文件
//		File appCacheDir = new File(getFilesDir().getAbsolutePath()
//				+ Globle.APP_CACAHE_DIRNAME);
//		LogUtil.e("appCacheDir path=" + appCacheDir.getAbsolutePath());
//
//		File webviewCacheDir = new File(getCacheDir().getAbsolutePath()
//				+ Globle.APP_CACAHE_DIRNAME);
//		LogUtil.e("webviewCacheDir path=" + webviewCacheDir.getAbsolutePath());
//
//		// 删除webview 缓存目录
//		if (webviewCacheDir.exists()) {
//			LogUtil.e("del webviewCacheDir dir");
//			deleteFile(webviewCacheDir);
//		} else {
//			LogUtil.e("no found 1");
//		}
//
//		// 删除webview 缓存 缓存目录
//		if (appCacheDir.exists()) {
//			LogUtil.e("del app cache dir");
//			deleteFile(appCacheDir);
//		} else {
//			LogUtil.e("no found 2");
//		}
//	}
//
//	/**
//	 * 递归删除 文件/文件夹
//	 *
//	 * @param file
//	 */
//	public void deleteFile(File file) {
//
//		LogUtil.e("delete file path=" + file.getAbsolutePath());
//
//		if (file.exists()) {
//			if (file.isFile()) {
//				file.delete();
//			} else if (file.isDirectory()) {
//				File files[] = file.listFiles();
//				for (int i = 0; i < files.length; i++) {
//					deleteFile(files[i]);
//				}
//			}
//			file.delete();
//		} else {
//			LogUtil.e("delete file no exists " + file.getAbsolutePath());
//		}
//	}

	onWebviewLoadFinish onLoadFinish = new onWebviewLoadFinish() {
		@Override
		public void isPass(boolean isPass) {
			LogUtil.e("444 is pass = " + isPass);
			if (time <= 0) {
				if (isPass) {
					mWebView.setVisibility(View.VISIBLE);
					mImageView.setVisibility(View.GONE);
				} else {
					LogUtil.e("laod fail");
					mProgressBar.setVisibility(View.GONE);
					mWebView.setVisibility(View.GONE);
					mImageView.setImageResource(R.drawable.load_fail);
					mImageView.setVisibility(View.VISIBLE);
					// mTextView.setText("加载失败");
					// mTextView.setVisibility(View.VISIBLE);
				}
			}
		}

		@Override
		public void launchActivity(String url) {
			// Globle.webHistory.add(homeUrl);
			// Globle.webHistory.add(url);

			Intent intent = new Intent();
			intent.setClass(MainActivity.this, ContentActivity.class);
			intent.putExtra("homeUrl", url);
			startActivity(intent);
			// overridePendingTransition(R.anim.push_left_in,
			// R.anim.push_left_out);
		}
	};

	// === function ====

	private void randomUpdate() {
//		timer.schedule(task, 5000);
		timer.schedule(task, 5000, 1000);
	}

	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			LogUtil.e("run");
			checkUpdate();
//			runOnUiThread(new Runnable() { // UI thread
//				@Override
//				public void run() {
//					recLen--;
//					if (recLen < 0) {
//						AlertDialog.Builder dialog = new AlertDialog.Builder(
//								MainActivity.this);
//						dialog.setTitle("更新");
//						dialog.setMessage("后台检测有新版本可以更新，是否现在更新？");
//						dialog.setNegativeButton("稍后更新", null);
//						dialog.setPositiveButton("现在更新",
//								new DialogInterface.OnClickListener() {
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
//										Utils.startDownload(
//												MainActivity.this,
//												"http://222.73.3.43/sqdd.myapp.com/myapp/qqteam/AndroidQQ/Android_QQ.apk?mkey=55021f0afd0cc590&f=d410&p=.apk");
//									}
//								});
//						dialog.show();
						timer.cancel();
//					}
//				}
//			});
		}
	};
	
	private void checkUpdate(){
		LogUtil.e("update");
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams(); // 绑定参数
		String url = "http://update.shanggames.com/update.json";
		client.get(url, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject arg0) {
				LogUtil.e("onSuccess" + arg0.toString());

				String isNeedUpdate;//: "true",
				String versionCode;//: "2.0",
				final String updateUrl;//: "http://update.shanggames.com/ver2.apk"
				
				try {
					JSONObject json = new JSONObject(arg0.toString());
					isNeedUpdate = json.getString("isNeedUpdate");
					versionCode = json.getString("versionCode");
					updateUrl = json.getString("updateUrl");
					
					if (isNeedUpdate.equalsIgnoreCase("true")) {
						if(Float.valueOf(versionCode) > Utils.getVersionCode(MainActivity.this)){
							
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									AlertDialog.Builder dialog = new AlertDialog.Builder(
											MainActivity.this);
									dialog.setTitle("更新");
									dialog.setMessage("后台检测有新版本可以更新，是否现在更新？");
									dialog.setNegativeButton("稍后更新", null);
									dialog.setPositiveButton("现在更新",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog,
														int which) {
													Utils.startDownload(
															MainActivity.this,
															updateUrl);
												}
											});
									dialog.show();
								}
							});
							
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(Throwable error) {
				super.onFailure(error);
				LogUtil.e("on fail");
			}
		});
		
	}

	class mHandler extends Handler {

		public mHandler() {
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			LogUtil.e("run mhandler");
			if (msg != null && msg.arg1 == 1) {
				bottomLayout.setVisibility(View.GONE);
			} else {
				if (time > 0) {
					time--;
					// mTextView.setText("广告即将消失在 " + time + " 秒后");
					handler.sendEmptyMessageDelayed(0, 1000);
				} else {
					getWindow().clearFlags(
							WindowManager.LayoutParams.FLAG_FULLSCREEN);
					if (mProgress != 100)
						mProgressBar.setVisibility(View.VISIBLE);

					// 如果有网络
					if (Utils.isOnline(MainActivity.this)) {
						TranslateAnimation mHiddenAction = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0.0f,
								Animation.RELATIVE_TO_SELF, 100.0f,
								Animation.RELATIVE_TO_SELF, 0.0f,
								Animation.RELATIVE_TO_SELF, 0.0f);

						mHiddenAction.setDuration(1000);
						mImageView.setAnimation(mHiddenAction);
						mTextView.setVisibility(View.GONE);
						mImageView.setVisibility(View.GONE);
						mImageView.setOnClickListener(null);
					} else {
						mWebView.setVisibility(View.GONE);
						// mTextView.setVisibility(View.VISIBLE);
						// mTextView.setText("网络连接不可用，请稍后重试");
						mImageView.setImageResource(R.drawable.load_fail);
						mImageView.setVisibility(View.VISIBLE);
					}
				}
			}
		}
	}

	class mThread implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}

			Message msg = new Message();
			msg.arg1 = 0;
			handler.sendMessage(msg);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
			mWebView.goBack();// 返回前一个页面
			return true;
		} else {
			onMyBackPressed();
			return true;
		}
	}

	private void onLogoClicked() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, ContentActivity.class);
		intent.putExtra("homeUrl", getResources().getString(R.string.logo_url));
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	private void onMyBackPressed() {
		final AlertDialog.Builder exitDialog = new AlertDialog.Builder(
				MainActivity.this);
		exitDialog.setTitle(getResources().getString(R.string.exit));
		exitDialog.setMessage(getResources().getString(R.string.exit_msg));
		exitDialog.setPositiveButton(
				getResources().getString(R.string.confirm),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		exitDialog.setNegativeButton(getResources().getString(R.string.cancal),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		exitDialog.setNeutralButton(getResources().getString(R.string.min),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Utils.startHome(MainActivity.this);
					}
				});
		exitDialog.show();
	}

	private void onSettingBtnClick() {
		settingPopWindow = new SettingPopWindow(this, seekBarChangeListener,
				onCheckedChangeListener, onSegmentBarChangedListener);
		// 显示窗口
		settingPopWindow.showAtLocation(
				MainActivity.this.findViewById(R.id.bottom_layout),
				Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	// 亮度 监听器
	SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			BrightUtils.SetLightness(MainActivity.this, progress);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}
	};

	// 夜间模式 监听器
	CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			prefUtil.saveBoolean(PrefUtil.BrightPref, isChecked);
			if (isChecked) {// night mode
			} else {
			}
		}
	};

	// 字体大小 监听器
	SegmentBar.OnSegmentBarChangedListener onSegmentBarChangedListener = new SegmentBar.OnSegmentBarChangedListener() {
		@Override
		public void onBarItemChanged(int segmentItemIndex) {
			prefUtil.saveInt(PrefUtil.TextSizePref, segmentItemIndex);
			Utils.setWebViewSize(segmentItemIndex, mWebView.getSettings());
		}
	};

}
