package com.plug.caladarview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;


import com.plug.caladarview.R;
import com.plug.caladarview.data.CalendarDay;

import java.util.Calendar;

/*
* User: ChenCHaoXue
* Create date: 2016-10-25
* Time: 11:43
* From VCard
*
*/
public class DayPickerView extends RecyclerView {
    protected Context mContext;
    protected CalendarMonthAdapter mAdapter;
    private DatePickerController mController;
    private TypedArray typedArray;

    public DayPickerView(Context context) {
        this(context, null);
    }

    public DayPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DayPickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.DayPickerView);
            setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            init(context);
        }
    }

    /**
     * mController 监听点击事件
     * isHasValue 判断是否有值 false 不显示选中日期  true 要传开始与结束日期
     * beginCalendarDay  开始日期
     * endCalendarDay   结束日期
     * yearMonthDay  设置日历开始时间
     * isPrevEnabled  当天之前日期置灰开关
     * isLastEnabled 当天之后日期置灰开关
     * isScroll 滚动  true 最后   false  置顶
     * isDouble true 双击  false 点击两次
     */
    public void setController(DatePickerController mController, boolean isHasValue, CalendarDay beginCalendarDay,
                              CalendarDay endCalendarDay, Calendar yearMonthDay, boolean isPrevEnabled, boolean isLastEnabled, boolean isScroll, boolean isDouble) {
        this.mController = mController;
        setUpAdapter(isHasValue, beginCalendarDay, endCalendarDay, yearMonthDay, isPrevEnabled, isLastEnabled, isScroll, isDouble);
        setAdapter(mAdapter);
    }

    public void init(Context paramContext) {
        setLayoutManager(new LinearLayoutManager(paramContext));//采用类似ListView的列表
        mContext = paramContext;
        setHasFixedSize(true);
        setVerticalScrollBarEnabled(false);// 取消Vertical ScrollBar显示
        setFadingEdgeLength(0);//设置边框渐变宽度
    }

    protected void setUpAdapter(boolean isHasValue, CalendarDay beginCalendarDay, CalendarDay endCalendarDay,
                                Calendar setYearMonthDay, boolean isPrevEnabled, boolean isLastEnabled, boolean isScroll, boolean isDouble) {
        if (mAdapter == null) {
            mAdapter = new CalendarMonthAdapter(getContext(), mController, typedArray, isHasValue, beginCalendarDay,
                    endCalendarDay, setYearMonthDay, isPrevEnabled, isLastEnabled, isScroll, isDouble);
        }
        mAdapter.notifyDataSetChanged();
    }

    public CalendarMonthAdapter getAdapter() {
        return mAdapter;
    }
}