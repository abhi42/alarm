package org.ap.android.alarm.common;

/**
 * Created by abhi on 03.05.15.
 */
public interface IAlarmDateTime {

    void setAlarmTime(final int hr, final int min);

    int getAlarmHr();

    int getAlarmMinute();

    void setAlarmDate(final int yr, final int month, final int day);

    int getAlarmYear();

    int getAlarmMonth();

    int getAlarmDay();
}
