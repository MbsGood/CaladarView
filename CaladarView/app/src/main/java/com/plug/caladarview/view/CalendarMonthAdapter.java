package com.plug.caladarview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;

import com.plug.caladarview.R;
import com.plug.caladarview.data.CalendarDay;
import com.plug.caladarview.data.SelectedDays;

import java.util.Calendar;
import java.util.HashMap;

/*
* User: ChenCHaoXue
* Create date: 2016-10-25
* Time: 11:43
* From VCard
*
*/
public class CalendarMonthAdapter extends RecyclerView.Adapter<CalendarMonthAdapter.ViewHolder> implements CalendarMonthView.OnDayClickListener {
    private static final int MONTHS_IN_YEAR = 12;
    private final TypedArray typedArray;
    private final Context mContext;
    private final DatePickerController mController;
    private final Calendar calendar;
    private final SelectedDays selectedDays;
    private final Integer firstMonth;
    private final Integer lastMonth;
    private boolean isPrevEnabled;
    private boolean isLastEnabled;
    private boolean isScroll;
    private boolean isDouble;

    /*
    * hasValue 判断是否有开始与结束时间，flase 则不显示，true，一定要开始与结束时间
    * beginCalendar 开始时间
    * endCalendar 结束时间 2015, 5, 1
    * setYearMonthDay 起始日期
    * isDouble 双击选择
    * */
    public CalendarMonthAdapter(Context context, DatePickerController datePickerController,
                                TypedArray typedArray, boolean hasValue, CalendarDay beginCalendar,
                                CalendarDay endCalendar, Calendar setYearMonthDay, boolean isPrevEnabled, boolean isLastEnabled, boolean isScroll, boolean isDouble) {
        this.isPrevEnabled = isPrevEnabled;
        this.isLastEnabled = isLastEnabled;
        this.isScroll = isScroll;
        this.typedArray = typedArray;
        this.calendar = setYearMonthDay;
        firstMonth = typedArray.getInt(R.styleable.DayPickerView_firstMonth, calendar.get(Calendar.MONTH));
        lastMonth = typedArray.getInt(R.styleable.DayPickerView_lastMonth, (calendar.get(Calendar.MONTH) - 1) % MONTHS_IN_YEAR);
        selectedDays = new SelectedDays();
        mContext = context;
        mController = datePickerController;
        this.isDouble = isDouble;
        init(hasValue, beginCalendar, endCalendar);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final CalendarMonthView simpleMonthView = new CalendarMonthView(mContext, typedArray, isPrevEnabled, isLastEnabled);
        return new ViewHolder(simpleMonthView, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final CalendarMonthView v = viewHolder.simpleMonthView;
        final HashMap<String, Integer> drawingParams = new HashMap<String, Integer>();
        int month;
        int year;

        month = (firstMonth + (position % MONTHS_IN_YEAR)) % MONTHS_IN_YEAR;
        year = position / MONTHS_IN_YEAR + calendar.get(Calendar.YEAR) + ((firstMonth + (position % MONTHS_IN_YEAR)) / MONTHS_IN_YEAR);

        int selectedFirstDay = -1;
        int selectedLastDay = -1;
        int selectedFirstMonth = -1;
        int selectedLastMonth = -1;
        int selectedFirstYear = -1;
        int selectedLastYear = -1;

        if (selectedDays.getFirst() != null) {
            selectedFirstDay = selectedDays.getFirst().day;
            selectedFirstMonth = selectedDays.getFirst().month;
            selectedFirstYear = selectedDays.getFirst().year;
        }

        if (selectedDays.getLast() != null) {
            selectedLastDay = selectedDays.getLast().day;
            selectedLastMonth = selectedDays.getLast().month;
            selectedLastYear = selectedDays.getLast().year;
        }

        v.reuse();

        drawingParams.put(CalendarMonthView.VIEW_PARAMS_SELECTED_BEGIN_YEAR, selectedFirstYear);
        drawingParams.put(CalendarMonthView.VIEW_PARAMS_SELECTED_LAST_YEAR, selectedLastYear);
        drawingParams.put(CalendarMonthView.VIEW_PARAMS_SELECTED_BEGIN_MONTH, selectedFirstMonth);
        drawingParams.put(CalendarMonthView.VIEW_PARAMS_SELECTED_LAST_MONTH, selectedLastMonth);
        drawingParams.put(CalendarMonthView.VIEW_PARAMS_SELECTED_BEGIN_DAY, selectedFirstDay);
        drawingParams.put(CalendarMonthView.VIEW_PARAMS_SELECTED_LAST_DAY, selectedLastDay);
        drawingParams.put(CalendarMonthView.VIEW_PARAMS_YEAR, year);
        drawingParams.put(CalendarMonthView.VIEW_PARAMS_MONTH, month);
        drawingParams.put(CalendarMonthView.VIEW_PARAMS_WEEK_START, calendar.getFirstDayOfWeek());
        v.setMonthParams(drawingParams);
        v.invalidate();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        Calendar current = Calendar.getInstance();
        int itemCount;
        if (isScroll) {
            itemCount = (((current.get(Calendar.YEAR) - calendar.get(Calendar.YEAR)) + 1) * MONTHS_IN_YEAR);
        } else {
            itemCount = (((mController.getMaxYear() - calendar.get(Calendar.YEAR)) + 1) * MONTHS_IN_YEAR);
        }

        if (firstMonth != -1)
            itemCount -= firstMonth;

        if (lastMonth != -1)
            itemCount -= (MONTHS_IN_YEAR - current.get(Calendar.MONTH)) - 1;
        Log.d("","----count---->"+itemCount);
        return itemCount;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final CalendarMonthView simpleMonthView;

        public ViewHolder(View itemView, CalendarMonthView.OnDayClickListener onDayClickListener) {
            super(itemView);
            simpleMonthView = (CalendarMonthView) itemView;
            simpleMonthView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            simpleMonthView.setClickable(true);
            simpleMonthView.setOnDayClickListener(onDayClickListener);
        }
    }

    protected void init(boolean hasValue, CalendarDay beginCalendarDay, CalendarDay endCalendarDay) {
        if (hasValue) {
            selectedDays.setFirst(beginCalendarDay);
            setSelectedDay(endCalendarDay);
        }
    }

    @Override
    public void onDayClick(CalendarMonthView simpleMonthView, CalendarDay calendarDay) {
        if (calendarDay != null) {
            if (isDouble) {
                selectedDays.setFirst(calendarDay);
                selectedDays.setLast(calendarDay);
                mController.onDateRangeSelected(selectedDays);
                notifyDataSetChanged();
            } else {
                onDayTapped(calendarDay);
            }
        }
    }

    private void onDayTapped(CalendarDay calendarDay) {
        mController.onDayOfMonthSelected(calendarDay.year, calendarDay.month, calendarDay.day);
        setSelectedDay(calendarDay);
    }

    private void setSelectedDay(CalendarDay calendarDay) {
        if (selectedDays.getFirst() != null && selectedDays.getLast() == null) {
            if (selectedDays.getFirst().getDate().after(calendarDay.getDate())) {
                selectedDays.setLast(selectedDays.getFirst());
                selectedDays.setFirst(calendarDay);
            } else {
                selectedDays.setLast(calendarDay);
            }
            mController.onDateRangeSelected(selectedDays);
        } else if (selectedDays.getLast() != null) {
            selectedDays.setFirst(calendarDay);
            selectedDays.setLast(null);
        } else {
            selectedDays.setFirst(calendarDay);
        }
        notifyDataSetChanged();
    }
}