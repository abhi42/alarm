package org.ap.android.alarm.dto;

import android.widget.Button;

/**
 * Created by abhi on 25.05.15.
 */
public class AlarmDailyIntervalDateTimeDto extends AlarmDateTimeDto {

    private final Button btn;
    private int alarmInterval;

    public AlarmDailyIntervalDateTimeDto(final Button b) {
        this.btn = b;
    }

    @Override
    public void setAlarmTime(final int hr, final int min) {
        super.setAlarmTime(hr, min);
        setAlarmInterval(0, hr, min);
    }

    public int getAlarmInterval() {
        return alarmInterval;
    }

    private void setAlarmInterval(final int days, final int hrs, final int minutes) {
        alarmInterval = days * 24 * 60 + (hrs * 60) + minutes;
        final StringBuilder b = new StringBuilder();
        boolean hasDays = writeDaysText(days, b);
        boolean hasHrs = writeHrsText(hrs, b, hasDays);
        writeMinutesText(minutes, b, hasHrs);
        btn.setText(b.toString());
    }

    private void writeMinutesText(final int minutes, final StringBuilder b, final boolean hasHrs) {
        if (minutes > 0) {
            if (hasHrs) {
                b.append(" ");
            }
            b.append(minutes);
            if (minutes > 1) {
                b.append(" minutes");
            } else {
                b.append(" minute");
            }
        }
    }

    private boolean writeHrsText(final int hrs, final StringBuilder b, final boolean hasDays) {
        if (hrs > 0) {
            if (hasDays) {
                b.append(" ");
            }
            b.append(hrs);
            if (hrs > 1) {
                b.append(" hours");
            } else {
                b.append(" hour");
            }
            return true;
        }
        return false;
    }

    private boolean writeDaysText(final int days, final StringBuilder b) {
        if (days > 0) {
            b.append(days);
            if (days > 1) {
                b.append(" days");
            } else {
                b.append(" day");
            }
            return true;
        }
        return false;
    }
}
