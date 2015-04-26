package org.ap.android.alarm;

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
        public static final String COLUMN_NAME_ALARM_DESC = "desc";
        public static final String COLUMN_NAME_ALARM_START_TIME = "startTime";
        public static final String COLUMN_NAME_ALARM_TIME_ZONE = "timeZone";
        public static final String COLUMN_NAME_ALARM_NUM_OCCURRENCES = "numOccurrences";
        public static final String COLUMN_NAME_ALARM_INTERVAL = "interval";
        public static final String COLUMN_NAME_ALARM_IS_ENABLED = "isEnabled";
    }
}
