package org.ap.android.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by abhi on 15.02.15.
 */
public class AlarmUtils {

    public static final String NUM_OCCURRENCES = "numOccurrences";
    public static final String INTERVAL_IN_MINUTES = "intervalInMinutes";
    public static final String ALARM_DESC = "alarmDesc";

    private static final String TAG = AlarmUtils.class.getName();

    static PendingIntent createPendingIntent(final Context context, int
            intervalInMinutesToNextAlarm, final int numOccurrences, String alarmDesc) {
        // TODO when multiple alarms are allowed, their pending intents have to be different
        // see documentation of PendingIntent class. In this case, change the request code
        // so that it uniquely identifies the pending intent to a particular alarm.
        final Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(INTERVAL_IN_MINUTES, intervalInMinutesToNextAlarm);
        intent.putExtra(NUM_OCCURRENCES, numOccurrences);
        intent.putExtra(ALARM_DESC, alarmDesc);

        return PendingIntent.getBroadcast(context, 42, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    static void createAlarm(final Context context, final int intervalInMinutesToNextAlarm,
                            final int numOccurrences, final String alarmDesc) {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + intervalInMinutesToNextAlarm);
        createInitialAlarm(context, c, intervalInMinutesToNextAlarm, numOccurrences, alarmDesc);
    }

    static void createInitialAlarm(final Context context, final Calendar c,
                                   final int intervalInMinutesToNextAlarm,
                                   final int numOccurrences, final String alarmDesc) {
        final AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context
                .ALARM_SERVICE);
        final PendingIntent pendingIntent = createPendingIntent(context,
                intervalInMinutesToNextAlarm, numOccurrences, alarmDesc);
        logAlarmDetails(c, intervalInMinutesToNextAlarm, numOccurrences, alarmDesc);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, c.getTime().getTime(), pendingIntent);
    }

    private static void logAlarmDetails(final Calendar c, final int intervalInMinutesToNextAlarm,
                                        final int numOccurrences, final String alarmDesc) {
        final StringBuilder b = new StringBuilder();
        final String sep = "-";
        final String timeSep = ":";
        b.append("day: ");
        b.append(c.get(Calendar.DATE));
        b.append(sep);
        b.append(c.get(Calendar.MONTH) + 1);
        b.append(sep);
        b.append(c.get(Calendar.YEAR));
        b.append(", @ ");
        b.append(c.get(Calendar.HOUR_OF_DAY));
        b.append(timeSep);
        b.append(c.get(Calendar.MINUTE));
        b.append(", interval: ");
        b.append(intervalInMinutesToNextAlarm);
        b.append(", num occurrences: ");
        b.append(numOccurrences);
        b.append(", desc: ");
        b.append(alarmDesc);
        Log.d(TAG, "Alarm about to be created with the following details: " + b.toString());
    }
}
