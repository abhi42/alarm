package org.ap.android.alarm;

import android.database.Cursor;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by abhi on 28.02.15.
 */
public final class AlarmDto {

    private final String desc;
    private final long startTimeWithoutTz;
    private final TimeZone tz;
    private final int numOccurrences;
    private final int interval;
    private final long id;
    private final boolean isEnabled;

    private static final String TAG = AlarmDto.class.getName();

    // use this constructor when creating a new alarm in the db
    public AlarmDto(final String desc, final Date startTime, final int numOccurrences, final int interval, final boolean isEnabled) {
        this.id = -1;
        this.desc = desc;
        this.startTimeWithoutTz = startTime.getTime();
        this.tz = Calendar.getInstance().getTimeZone();
        this.numOccurrences = numOccurrences;
        this.interval = interval;
        this.isEnabled = isEnabled;
    }

    // use this constructor when updating an alarm in the db
    public AlarmDto(final long id, final String desc, final Date startTime, final int numOccurrences, final int interval, final boolean isEnabled) {
        this.id = id;
        this.desc = desc;
        startTimeWithoutTz = startTime != null ? startTime.getTime() : -1;
        tz = Calendar.getInstance().getTimeZone();
        this.numOccurrences = numOccurrences;
        this.interval = interval;
        this.isEnabled = isEnabled;
    }

    // use this constructor when retrieving an alarm from the db
    public AlarmDto(final long id, final String desc, final long startTimeWithoutTz, final TimeZone tz, final int numOccurrences, final int interval, final boolean isEnabled) {
        this.id = id;
        this.desc = desc;
        this.startTimeWithoutTz = startTimeWithoutTz;
        this.tz = tz;
        this.numOccurrences = numOccurrences;
        this.interval = interval;
        this.isEnabled = isEnabled;
    }

    public String getDescription() {
        return desc;
    }

    public long getStartTimeWithoutTz() {
        return startTimeWithoutTz;
    }

    public TimeZone getTz() {
        return tz;
    }

    public int getNumOccurrences() {
        return numOccurrences;
    }

    public int getInterval() {
        return interval;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public long getId() {
        return id;
    }

    static AlarmDto createDto(final Cursor c) {
        final long id = c.getLong(c.getColumnIndex(AlarmContract.AlarmEntry._ID));
        final String desc = c.getString(c.getColumnIndex(AlarmContract.AlarmEntry
                .COLUMN_NAME_ALARM_DESC));
        final long startTime = c.getLong(c.getColumnIndexOrThrow(AlarmContract.AlarmEntry
                .COLUMN_NAME_ALARM_START_TIME));
        final String timeZoneId = c.getString(c.getColumnIndexOrThrow(AlarmContract.AlarmEntry
                .COLUMN_NAME_ALARM_TIME_ZONE));
        final int numOccurrences = c.getInt(c.getColumnIndexOrThrow(AlarmContract.AlarmEntry
                .COLUMN_NAME_ALARM_NUM_OCCURRENCES));
        final int interval = c.getInt(c.getColumnIndexOrThrow(AlarmContract.AlarmEntry
                .COLUMN_NAME_ALARM_INTERVAL));
        final int isEnabledInt = c.getInt(c.getColumnIndexOrThrow(AlarmContract.AlarmEntry
                .COLUMN_NAME_ALARM_IS_ENABLED));
        final boolean isEnabled = isEnabledInt == 1 ? true : false;

        final TimeZone tz = TimeZone.getTimeZone(timeZoneId);
        return new AlarmDto(id, desc, startTime, tz, numOccurrences, interval, isEnabled);
    }
}
