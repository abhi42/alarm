package org.ap.android.alarm.dto;

import org.ap.android.alarm.common.IAlarmDateTime;

/**
 * Created by abhi on 03.05.15.
 */
public class AlarmDateTimeDto implements IAlarmDateTime {

    private int hr;
    private int min;
    private int yr;
    private int month;
    private int day;

    @Override
    public void setAlarmTime(int hr, int min) {
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
    public void setAlarmDate(int yr, int month, int day) {
        this.yr = yr;
        this.month = month;
        this.day = day;
    }

    @Override
    public int getAlarmYear() {
        return yr;
    }

    @Override
    public int getAlarmMonth() {
        return month;
    }

    @Override
    public int getAlarmDay() {
        return day;
    }
}
