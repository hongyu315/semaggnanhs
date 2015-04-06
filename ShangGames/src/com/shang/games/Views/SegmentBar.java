package com.shang.games.Views;

/**
 * Created by rain on 15/3/18.
 */

import com.shang.games.R;
import com.shang.games.R.drawable;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class SegmentBar extends LinearLayout implements View.OnClickListener
{
    private String[] stringArray;

    public SegmentBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SegmentBar(Context context)
    {
        super(context);
    }

    /**
     * determine the number of segment bar items by the string array.
     *
     * 根据传递进来的数组，决定分段的数量
     *
     *
     * */
    public void setValue(Context context, String[] stringArray)
    {
        this.stringArray = stringArray;
        if(stringArray.length <= 1)
        {
            throw new RuntimeException("the length of String array must bigger than welcome1");
        }
        this.stringArray = stringArray;
        resolveStringArray(context);
    }

    /**
     * resolve the array and generate the items.
     *
     * 对数组进行解析，生成具体的每个分段
     *
     * */
    private void resolveStringArray(Context context)
    {
        int length = this.stringArray.length;
        for(int i = 0; i < length; i++)
        {
            Button button = new Button(context);
            button.setText(stringArray[i]);
            button.setGravity(Gravity.CENTER);
            button.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
            button.setTag(i);
            button.setOnClickListener(this);

            if(i == 0)//左边的按钮
            {
                button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.left_button_bg_selector));
                //button.setBackgroundResource(R.drawable.left_button_bg_selector);
            }
            else if(i != 0 && i < (length-1))//右边的按钮
            {
                button.setBackgroundResource(R.drawable.middle_button_bg_selector);
            }
            else//中间的按钮
            {
                button.setBackgroundResource(R.drawable.right_button_bg_selector);
            }

            this.addView(button);
        }
    }

    private int lastIndex = 0;//记录上一次点击的索引
    @Override
    public void onClick(View v)
    {
        int index = (Integer)v.getTag();
        onSegmentBarChangedListener.onBarItemChanged(index);
        this.getChildAt(lastIndex).setSelected(false);
        this.getChildAt(index).setSelected(true);
        lastIndex = index;
    }

    /**
     * set the default bar item of selected
     *
     * 设置默认选中的SegmentBar
     *
     * */
    public void setDefaultBarItem(int index)
    {
        if(index > stringArray.length)
        {
            throw new RuntimeException("the value of default bar item can not bigger than string array's length");
        }
        lastIndex = index;
        this.getChildAt(index).setSelected(true);
    }

    /**
     * set the text size of every item
     *
     * 设置文字的大小
     *
     * */
    public void setTextSize(float sizeValue)
    {
        int index = this.getChildCount();
        for(int i = 0; i < index; i++)
        {
            ((Button)this.getChildAt(i)).setTextSize(sizeValue);
        }
    }

    /**
     * set the text color of every item
     *
     * 设置文字的颜色
     *
     * */
    public void setTextColor(int color)
    {
        int index = this.getChildCount();
        for(int i = 0; i < index; i++)
        {
            ((Button)this.getChildAt(i)).setTextColor(color);
        }
    }

    private OnSegmentBarChangedListener onSegmentBarChangedListener;

    /**
     * define a interface used for listen the change event of Segment bar
     *
     * 定义一个接口，用来实现分段控件Item的监听
     *
     * */
    public interface OnSegmentBarChangedListener
    {
        public void onBarItemChanged(int segmentItemIndex);
    }

    /**
     * set the segment bar item changed listener,if the bar item changed,
     * the method onBarItemChanged()will be called.
     *
     * 设置分段控件的监听器，当分段改变的时候，onBarItemChanged()会被调用
     *
     * */
    public void setOnSegmentBarChangedListener(OnSegmentBarChangedListener onSegmentBarChangedListener)
    {
        this.onSegmentBarChangedListener = onSegmentBarChangedListener;
    }
}