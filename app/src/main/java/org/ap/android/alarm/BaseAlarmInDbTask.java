package org.ap.android.alarm;

import android.content.ContentValues;

import java.util.Calendar;

/**
 * Created by abhi on 07.03.15.
 */
public class BaseAlarmInDbTask {

    String getTimezone() {
        return Calendar.getInstance().getTimeZone().getID();
    }

    ContentValues createContentValues(final AlarmDto dto) {
        final ContentValues values = new ContentValues();
        final long id = dto.getId();
        if (id == -1) {
            values.put(AlarmContract.AlarmEntry._ID, (byte[]) null);
        } else {
            values.put(AlarmContract.AlarmEntry._ID, id);
        }
        values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_DESC, dto.getDescription());
        values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_START_TIME,
                dto.getStartTimeWithoutTz());
        values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_TIME_ZONE, getTimezone());
        values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_NUM_OCCURRENCES,
                dto.getNumOccurrences());
        values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_INTERVAL, dto.getInterval());
        values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_IS_ENABLED, dto.isEnabled());

        return values;
    }

    String getAlarmIdToUpdate(final ContentValues values) {
        return values.getAsString(AlarmContract.AlarmEntry._ID);
    }

    String getContentValuesAsString(final ContentValues values) {
        final String desc = values.getAsString(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_DESC);
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

        b.append("Alarm description: ");
        b.append(desc);

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
