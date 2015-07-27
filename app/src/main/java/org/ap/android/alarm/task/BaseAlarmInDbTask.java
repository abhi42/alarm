package org.ap.android.alarm.task;

import android.content.ContentValues;

import org.ap.android.alarm.common.AlarmType;
import org.ap.android.alarm.common.AlarmUtils;
import org.ap.android.alarm.db.AlarmContract;
import org.ap.android.alarm.dto.AlarmDto;

import java.util.Calendar;

/**
 * Created by abhi on 07.03.15.
 */
public class BaseAlarmInDbTask {

    String getTimezone() {
        return Calendar.getInstance().getTimeZone().getID();
    }

    public ContentValues createContentValues(final AlarmDto dto) {
        final ContentValues values = new ContentValues();
        final long id = dto.getId();
        if (id == -1) {
            values.put(AlarmContract.AlarmEntry._ID, (byte[]) null);
        } else {
            values.put(AlarmContract.AlarmEntry._ID, id);
        }
        values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_TYPE, dto.getType().name());
        values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_DESC, dto.getDescription());
        if (AlarmType.WEEKLY == dto.getType()) {
            values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_WEEKDAYS, dto.getWeekdaysRepAsString());
        }
        values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_START_TIME,
                dto.getStartTimeWithoutTz());
        values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_TIME_ZONE, getTimezone());
        if (AlarmType.DAILY == dto.getType()) {
            values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_NUM_OCCURRENCES,
                    dto.getNumOccurrences());
            values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_INTERVAL, dto.getInterval());
        }
        values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_IS_ENABLED, dto.isEnabled());

        return values;
    }

    public String getAlarmIdToUpdate(final ContentValues values) {
        return values.getAsString(AlarmContract.AlarmEntry._ID);
    }

    public String getContentValuesAsString(final ContentValues values) {
        final String alarmTypeStr = values.getAsString(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_TYPE);
        final String desc = values.getAsString(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_DESC);
        final String weekDays = values.getAsString(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_WEEKDAYS);
        final Long startTime = values.getAsLong(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_START_TIME);
        final String tz = values.getAsString(AlarmContract.AlarmEntry
                .COLUMN_NAME_ALARM_TIME_ZONE);
        final Integer numOccurences = values.getAsInteger(AlarmContract.AlarmEntry
                .COLUMN_NAME_ALARM_NUM_OCCURRENCES);
        final Integer interval = values.getAsInteger(AlarmContract.AlarmEntry
                .COLUMN_NAME_ALARM_INTERVAL);
        final Boolean isEnabled = values.getAsBoolean(AlarmContract.AlarmEntry
                .COLUMN_NAME_ALARM_IS_ENABLED);

        final StringBuilder b = new StringBuilder();

        b.append("Alarm type: ");
        b.append(alarmTypeStr);

        b.append(", Alarm description: ");
        b.append(desc);

        b.append(", Alarm weekdays: ");
        b.append(weekDays);

        b.append(", alarm start time: ");
        final String formattedDateTime = AlarmUtils.formatDateTime(startTime, tz);
        b.append(formattedDateTime);

        b.append(", number of occurrences: ");
        b.append(numOccurences);

        b.append(", interval: ");
        b.append(interval);

        b.append(", isEnabled: ");
        b.append(isEnabled);

        return b.toString();
    }
}
