package org.ap.android.alarm;

import java.util.Date;

/**
 * Created by abhi on 03.05.15.
 */
public class AlarmDateTimeDto implements IAlarmDateTime {

    private int hr;
    private int min;
    private Date date;

    @Override
    public void setTime(int hr, int min) {
        this.hr = hr;
        this.min = min;
    }

    @Override
    public int getAlarmHr() {
        return hr;
    }

    @Override
    public int getAlarmMinute() {
        return min;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public Date getAlarmDate() {
        return date;
    }
}
