package org.ap.android.alarm.common;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by abhi on 05.07.15.
 */
public class WeekDayHelper {

    private static final String TAG = WeekDayNode.class.getName();
    private final WeekDayNode first;
    private final List<WeekDayNode> list = new ArrayList<>();

    public WeekDayHelper(final boolean... weekdays) {
        this.first = buildCircularList(weekdays);
//       log();
    }

    public static int getNumberOfDaysToNextEnabled(final TimeZone tz, final long timeToCompareAgainst, final boolean... weekdays) {
        if (weekdays == null) {
            return -1;
        }
        final WeekDayHelper weekDayHelper = new WeekDayHelper(weekdays);
        return weekDayHelper.getNumberOfDaysToNextEnabled(tz, timeToCompareAgainst);
    }

    public static String getEnabledWeekDaysString(final String separator, final boolean... weekdays) {
        if (weekdays == null) {
            return "";
        }

        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            if (weekdays[i]) {
                if (b.length() > 0) {
                    b.append(separator);
                }
                b.append(getShortWeekDayString(i));
            }
        }
        return b.toString();
    }

    private static String getShortWeekDayString(final int weekdayOffset) {
        switch (weekdayOffset + 1) {
            case Calendar.SUNDAY:
                return "Sun";

            case Calendar.MONDAY:
                return "Mon";

            case Calendar.TUESDAY:
                return "Tue";

            case Calendar.WEDNESDAY:
                return "Wed";

            case Calendar.THURSDAY:
                return "Thu";

            case Calendar.FRIDAY:
                return "Fri";

            case Calendar.SATURDAY:
            default:
                return "Sat";
        }
    }

    private void log() {
        Log.d(TAG, "weekdays info: " + list.size());
        for (WeekDayNode node : list) {
            Log.d(TAG, node.toString() + ", ");
        }
    }

    private WeekDayNode buildCircularList(final boolean... weekdays) {
        WeekDayNode first = null;
        WeekDayNode current = null;
        WeekDayNode previous = null;
        for (int i = 0; i < 7; i++) {
            if (weekdays[i]) {
                current = new WeekDayNode(i, true);
            } else {
                current = new WeekDayNode(i, false);
            }
            list.add(current);
            if (i == 0) {
                first = current;
            }
            if (previous != null) {
                previous.setNext(current);
            }
            previous = current;
        }
        current.setNext(first);
        return first;
    }

    private int getNumberOfDaysToNextEnabled(final TimeZone tz, final long timeToCompareAgainst) {
        final Calendar calendar = Calendar.getInstance(tz);
        final int dayofWeekToday = calendar.get(Calendar.DAY_OF_WEEK);
        final WeekDayNode weekDayNodeForToday = list.get(dayofWeekToday - 1); // calendar week day constants start from 1
        if (weekDayNodeForToday.isEnabled) {
            if (calendar.getTimeInMillis() < timeToCompareAgainst) {
                // alarm is at a time in the future (but still today)
                return 0;
            }
        }
        int i = 1;
        for (WeekDayNode current = weekDayNodeForToday.getNext(); i <= 7; current = current.getNext()) {
            if (current.isEnabled) {
                return i;
            }
            i++;
        }
        return -1; // should each here only if no week day has been selected
    }

    private class WeekDayNode {

        private final int weekday;
        private final boolean isEnabled;
        private WeekDayNode next;

        private WeekDayNode(final int weekday, final boolean isEnabled) {
            if (weekday < 0 || weekday > 7) {
                throw new IllegalArgumentException("Week days should have ordinals from 0 to 6; ordinal provided is " + weekday);
            }
            this.weekday = weekday;
            this.isEnabled = isEnabled;
        }

        private WeekDayNode getNext() {
            return next;
        }

        private void setNext(final WeekDayNode next) {
            this.next = next;
        }

        @Override
        public String toString() {
            final StringBuilder s = new StringBuilder();
            s.append(getWeekDayString());
            if (next != null) {
                s.append(", next: ");
                s.append(next.getWeekDayString());
            }
            return s.toString();
        }

        private String getWeekDayString() {
            switch (weekday + 1) {
                case Calendar.SUNDAY:
                    return "Sunday: " + isEnabled;

                case Calendar.MONDAY:
                    return "Monday: " + isEnabled;

                case Calendar.TUESDAY:
                    return "Tuesday: " + isEnabled;

                case Calendar.WEDNESDAY:
                    return "Wednesday: " + isEnabled;

                case Calendar.THURSDAY:
                    return "Thursday: " + isEnabled;

                case Calendar.FRIDAY:
                    return "Friday: " + isEnabled;

                case Calendar.SATURDAY:
                default:
                    return "Saturday: " + isEnabled;
            }
        }
    }
}
