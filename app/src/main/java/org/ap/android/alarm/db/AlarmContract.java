package org.ap.android.alarm.db;

import android.provider.BaseColumns;

/**
 * Created by abhi on 28.02.15.
 */
public class AlarmContract {

    // http://developer.android.com/training/basics/data-storage/databases.html
    private AlarmContract() {
    }

    public static class AlarmEntry implements BaseColumns {
        public static final String TABLE_NAME = "alarm_entry";
        public static final String COLUMN_NAME_ALARM_TYPE = "type";
        public static final String COLUMN_NAME_ALARM_DESC = "desc";
        public static final String COLUMN_NAME_ALARM_WEEKDAYS = "weekdays";
        public static final String COLUMN_NAME_ALARM_START_TIME = "startTime";
        public static final String COLUMN_NAME_ALARM_TIME_ZONE = "timeZone";
        public static final String COLUMN_NAME_ALARM_NUM_OCCURRENCES = "numOccurrences";
        public static final String COLUMN_NAME_ALARM_INTERVAL = "interval";
        public static final String COLUMN_NAME_ALARM_IS_ENABLED = "isEnabled";

        public static final String[] ALL_COLUMNS = {_ID, COLUMN_NAME_ALARM_TYPE, COLUMN_NAME_ALARM_DESC, COLUMN_NAME_ALARM_WEEKDAYS,
                COLUMN_NAME_ALARM_START_TIME, COLUMN_NAME_ALARM_TIME_ZONE, COLUMN_NAME_ALARM_NUM_OCCURRENCES,
                COLUMN_NAME_ALARM_INTERVAL, COLUMN_NAME_ALARM_IS_ENABLED};
    }
}
