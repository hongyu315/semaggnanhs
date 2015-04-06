package com.shang.games.Activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shang.games.R;
import com.shang.games.Data.Globle;
import com.shang.games.Data.ViewPagerAdapter;
import com.shang.games.Utils.PrefUtil;

public class SplashActivity extends Activity implements OnClickListener,
		OnPageChangeListener {

	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;
	private PrefUtil prefUtil;

	// 引导图片资源
	private static final int[] pics = { R.drawable.welcome1,
			R.drawable.welcome2, R.drawable.welcome3 };

	// 底部小店图片
	private ImageView[] dots;

	// 记录当前选中位置
	private int currentIndex;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏
		prefUtil = new PrefUtil(this);

		if (!prefUtil.getBoolean(PrefUtil.isFirstLaunch, false)) {
			prefUtil.saveBoolean(PrefUtil.isFirstLaunch, true);
			setContentView(R.layout.splash_layout);

			views = new ArrayList<View>();

			LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);

			LayoutInflater inflater = getLayoutInflater();

			View view3 = inflater.inflate(R.layout.guide3, null);
			// 初始化引导图片列表
			views.add(inflater.inflate(R.layout.guide1, null));
			views.add(inflater.inflate(R.layout.guide2, null));
			views.add(view3);

			view3.findViewById(R.id.welcome_btn).setOnClickListener(
					new OnClickListener() {
						@SuppressLint("NewApi")
						@Override
						public void onClick(View v) {
							startActivity(new Intent(SplashActivity.this,
									MainActivity.class));
							overridePendingTransition(R.anim.push_left_in,
									R.anim.push_left_out);
							finish();
						}
					});

			vp = (ViewPager) findViewById(R.id.viewpager);
			// 初始化Adapter
			vpAdapter = new ViewPagerAdapter(views);
			vp.setAdapter(vpAdapter);
			// 绑定回调
			vp.setOnPageChangeListener(this);

			// 初始化底部小点
			initDots();
		} else {
			startActivity(new Intent(this, MainActivity.class));
			// overridePendingTransition(R.anim.activity_close_enter,R.anim.activity_open_enter);
			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Globle.HomeURL = getResources().getString(R.string.home_url);
		Globle.isNeedShowBottomBar = getResources().getString(
				R.string.isNeedShowBottomBar).equals("true");
	}

	private void initDots() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

		dots = new ImageView[pics.length];
		// 循环取得小点图片
		for (int i = 0; i < pics.length; i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setEnabled(true);// 都设为灰色
			dots[i].setOnClickListener(this);
			dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
		}

		currentIndex = 0;
		dots[currentIndex].setEnabled(false);// 设置为白色，即选中状态
	}

	/**
	 * 设置当前的引导页
	 */
	private void setCurView(int position) {
		if (position < 0 || position >= pics.length) {
			return;
		}

		vp.setCurrentItem(position);
	}

	/**
	 * 这只当前引导小点的选中
	 */
	private void setCurDot(int positon) {
		if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
			return;
		}

		dots[positon].setEnabled(false);
		dots[currentIndex].setEnabled(true);

		currentIndex = positon;
	}

	int status = 0;

	// 当滑动状态改变时调用
	@Override
	public void onPageScrollStateChanged(int arg0) {
		status = arg0;
	}

	// 当当前页面被滑动时调用
	@SuppressLint("NewApi")
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		if ((status == 1) && (arg0 == pics.length - 1)) {
			startActivity(new Intent(this, MainActivity.class));
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			finish();
		}
	}

	// 当新的页面被选中时调用
	@Override
	public void onPageSelected(int arg0) {
		// 设置底部小点选中状态
		setCurDot(arg0);
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		setCurView(position);
		setCurDot(position);
	}
}
