package org.ap.android.alarm.dto;

import android.database.Cursor;
import android.util.Log;

import org.ap.android.alarm.common.AlarmType;
import org.ap.android.alarm.common.AlarmUtils;
import org.ap.android.alarm.db.AlarmContract;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by abhi on 28.02.15.
 */
public final class AlarmDto {

    private static final String TAG = AlarmDto.class.getName();
    private static final String WEEKDAY_SEP = ",";
    private static AlarmDto currentDto;
    private long id;
    private AlarmType type;
    private boolean isEnabled;
    private String desc;
    private boolean[] weekDays;
    private long startTimeWithoutTz;
    private TimeZone tz;
    private int numOccurrences;
    private int interval;

    public AlarmDto() {
    }

    public AlarmDto(final long id, final String desc, final Date startTime, final int numOccurrences, final int interval, final boolean isEnabled) {
        this.id = id;
        this.desc = desc;
        startTimeWithoutTz = startTime != null ? startTime.getTime() : -1;
        tz = Calendar.getInstance().getTimeZone();
        this.numOccurrences = numOccurrences;
        this.interval = interval;
        this.isEnabled = isEnabled;
    }

    public static AlarmDto createDto(final Cursor c) {
        final long id = c.getLong(c.getColumnIndex(AlarmContract.AlarmEntry._ID));
        final AlarmType type = AlarmType.valueOf(c.getString(c.getColumnIndex(AlarmContract.AlarmEntry
                .COLUMN_NAME_ALARM_TYPE)));
        final String desc = c.getString(c.getColumnIndex(AlarmContract.AlarmEntry
                .COLUMN_NAME_ALARM_DESC));
        final String weekdays = c.getString(c.getColumnIndexOrThrow(AlarmContract.AlarmEntry
                .COLUMN_NAME_ALARM_WEEKDAYS));
        Log.d(TAG, "weekdays from cursor being written to dto: " + weekdays);
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

        final AlarmDto dto = new AlarmDto();
        dto.setId(id);
        dto.setType(type);
        dto.setWeekDays(weekdays);
        dto.setDesc(desc);
        dto.setStartTimeWithoutTz(startTime);
        dto.setTz(TimeZone.getTimeZone(timeZoneId));
        dto.setNumOccurrences(numOccurrences);
        dto.setInterval(interval);
        dto.setIsEnabled(isEnabled);
//        Log.d(TAG, dto.toString());
        return dto;
    }

    public static AlarmDto getCurrentDto() {
        return currentDto;
    }

    public static void setCurrentDto(final AlarmDto currentDto) {
        AlarmDto.currentDto = currentDto;
    }

    public String getDescription() {
        return desc;
    }

    public long getStartTimeWithoutTz() {
        return startTimeWithoutTz;
    }

    public void setStartTimeWithoutTz(long startTimeWithoutTz) {
        this.startTimeWithoutTz = startTimeWithoutTz;
    }

    public TimeZone getTz() {
        return tz;
    }

    public void setTz(TimeZone tz) {
        this.tz = tz;
    }

    public int getNumOccurrences() {
        return numOccurrences;
    }

    public void setNumOccurrences(int numOccurrences) {
        this.numOccurrences = numOccurrences;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AlarmType getType() {
        return type;
    }

    public void setType(AlarmType type) {
        this.type = type;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean[] getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(boolean[] weekDays) {
        this.weekDays = weekDays;
    }

    public String getWeekdaysRepAsString() {
        final StringBuilder b = new StringBuilder(7);
        for (int i = 0; i < 7; i++) {
            b.append(weekDays[i] == true ? "1" : "0");
            if (i < 6) {
                b.append(WEEKDAY_SEP);
            }
        }
        return b.toString();
    }

    private void setWeekDays(final String weekdaysRepAsString) {
        // expected rep: comma separated integers, exactly 7 in number.
        if (weekdaysRepAsString != null) {
            final String[] split = weekdaysRepAsString.split(WEEKDAY_SEP);
            weekDays = new boolean[7];
            int i = 0;
            for (String str : split) {
                weekDays[i] = str.equals("1");
                i++;
            }
        }
    }

    public boolean isAnyWeekdayEnabled() {
        if (AlarmType.WEEKLY == type) {
            for (int i = 0; i < 7; i++) {
                if (weekDays[i]) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();

        b.append("Alarm Type: ");
        b.append(type.name());

        b.append(", alarm description: ");
        b.append(desc);

        switch (type) {
            case DAILY:
                getDateTimeForLogging(b);

                b.append(", number of occurrences: ");
                b.append(this.numOccurrences);

                b.append(", interval: ");
                b.append(interval);
                break;

            case WEEKLY:
                b.append(", days of the week: ");
                b.append(Arrays.toString(weekDays));
                final Calendar calendar = AlarmUtils.getCalendar(startTimeWithoutTz, tz);
                final int hr = calendar.get(Calendar.HOUR_OF_DAY);
                final int min = calendar.get(Calendar.MINUTE);
                b.append(", start time: ");
                b.append(AlarmUtils.formatTime(hr, min));
                break;

            case MONTHLY:
                getDateTimeForLogging(b);
                break;

            default:
                break;
        }

        b.append(", isEnabled: ");
        b.append(isEnabled);
        return b.toString();
    }

    private void getDateTimeForLogging(final StringBuilder b) {
        b.append(", alarm start time: ");
        final String formattedDateTime = AlarmUtils.formatDateTime(startTimeWithoutTz, tz.getID());
        b.append(formattedDateTime);
    }
}
