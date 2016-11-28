package com.plug.caladarview.view;


import com.plug.caladarview.data.SelectedDays;

/*
* User: ChenCHaoXue
* Create date: 2016-10-25
* Time: 11:43
* From VCard
*
*/
public interface DatePickerController {
    public abstract int getMaxYear();

    public abstract void onDayOfMonthSelected(int year, int month, int day);

    public abstract void onDateRangeSelected(final SelectedDays selectedDays);

}