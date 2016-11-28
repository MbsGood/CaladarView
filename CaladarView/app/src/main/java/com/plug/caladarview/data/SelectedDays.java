package com.plug.caladarview.data;

import java.io.Serializable;

/*
* User: ChenCHaoXue
* Create date: 2016-10-26 
* Time: 16:41 
* From VCard
*/
public class SelectedDays implements Serializable {
    private static final long serialVersionUID = 155258847878L;
    private CalendarDay firstCalendarDay;
    private CalendarDay lastCalendarDay;

    public SelectedDays() {
    }

    public CalendarDay getFirst() {
        return firstCalendarDay;
    }

    public void setFirst(CalendarDay firstCalendarDay) {
        this.firstCalendarDay = firstCalendarDay;
    }

    public CalendarDay getLast() {
        return lastCalendarDay;
    }

    public void setLast(CalendarDay lastCalendarDay) {
        this.lastCalendarDay = lastCalendarDay;
    }
}
