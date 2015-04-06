package com.shang.games.Views;

import com.shang.games.R;
import com.shang.games.R.color;
import com.shang.games.R.id;
import com.shang.games.R.layout;
import com.shang.games.R.style;
import com.shang.games.Utils.BrightUtils;
import com.shang.games.Utils.PrefUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Switch;

/**
 * Created by rain on 15/3/17.
 */
public class SettingPopWindow extends PopupWindow {


    SeekBar seekBar;
    Switch nightMode;
    SegmentBar segmentBar;
    Button done;
    private View mMenuView;

    @SuppressLint({ "NewApi", "ResourceAsColor" })
	public SettingPopWindow(Activity context, SeekBar.OnSeekBarChangeListener itemsOnClick,
                            CompoundButton.OnCheckedChangeListener onCheckedChangeListener,
                            SegmentBar.OnSegmentBarChangedListener onSegmentBarChangedListener) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.setting_pop_window, null);
        seekBar = (SeekBar) mMenuView.findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(itemsOnClick);
        seekBar.setProgress(BrightUtils.GetLightness(context));

        nightMode = (Switch)mMenuView.findViewById(R.id.night_mode_switch);
        segmentBar = (SegmentBar)mMenuView.findViewById(R.id.segment_bar);
        done = (Button)mMenuView.findViewById(R.id.done);

        PrefUtil prefUtil = new PrefUtil(context);
        nightMode.setChecked(prefUtil.getBoolean(PrefUtil.BrightPref,false));
        nightMode.setOnCheckedChangeListener(onCheckedChangeListener);
        segmentBar.setValue(context, new String[]{"小", "中", "大", "特大"});
        segmentBar.setTextColor(R.color.slidebar_gray);
        segmentBar.setTextSize(12.0f);
        segmentBar.setDefaultBarItem(prefUtil.getInt(PrefUtil.TextSizePref,1));
        segmentBar.setOnSegmentBarChangedListener(onSegmentBarChangedListener);
        //取消按钮
        done.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                //销毁弹出框
                dismiss();
            }
        });
        //设置按钮监听
//        btn_pick_photo.setOnClickListener(itemsOnClick);
//        btn_take_photo.setOnClickListener(itemsOnClick);
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

    }


}
