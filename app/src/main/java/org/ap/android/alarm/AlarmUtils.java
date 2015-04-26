package org.ap.android.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by abhi on 15.02.15.
 */
public class AlarmUtils {

    // don't use these variable directly, use their getOrCreate() methods
    private static String prefSnoozeIntervalKey;
    private static Integer prefDefaultSnoozeInterval;

    public static final String ALARM_DESC = "alarmDesc";

    public static final String ALARM_ID_BEING_PASSED = "alarmId";
    public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("E, LLL dd, yyyy");
    public static final DateFormat DEFAULT_TIME_FORMAT = new SimpleDateFormat("HH:mm");

    public static final DateFormat DEFAULT_DATE_TIME_FORMAT = new SimpleDateFormat("E, LLL dd, " +
            "HH:mm");

    private static final String TAG = AlarmUtils.class.getName();

    private static PendingIntent createPendingIntent(final Context context, final long alarmId, final String alarmDesc) {
        // TODO when multiple alarms are allowed, their pending intents have to be different
        // see documentation of PendingIntent class. In this case, change the request code
        // so that it uniquely identifies the pending intent to a particular alarm.
        final Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ALARM_ID_BEING_PASSED, alarmId);
        intent.putExtra(ALARM_DESC, alarmDesc);

        return PendingIntent.getBroadcast(context, 42, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    static void createAlarm(final Context context, final long alarmId,
                            Calendar selectedDateTime, final int intervalInMinutesToNextAlarm,
                            final int numOccurrences, final String alarmDesc) {
        createInitialAlarm(context, alarmId, selectedDateTime, intervalInMinutesToNextAlarm,
                numOccurrences,
                alarmDesc);
    }

    static void createInitialAlarm(final Context context, final long alarmId, final Calendar c,
                                   final int intervalInMinutesToNextAlarm,
                                   final int numOccurrences, final String alarmDesc) {
        final AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context
                .ALARM_SERVICE);
        final PendingIntent pendingIntent = createPendingIntent(context,
                alarmId, alarmDesc);
        logAlarmDetails(c, alarmId, intervalInMinutesToNextAlarm, numOccurrences, alarmDesc);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, c.getTime().getTime(), pendingIntent);
    }

    static void cancelAlarm(final Context context, final long alarmId, final String alarmDesc) {
        final AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context
                .ALARM_SERVICE);
        final PendingIntent pendingIntent = createPendingIntent(context,
                alarmId, alarmDesc);
        alarmMgr.cancel(pendingIntent);
    }

    static String formatDateTime(final long time, final String timeZoneId) {
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
        cal.setTime(new Date(time));
        return AlarmUtils.DEFAULT_DATE_TIME_FORMAT.format(cal.getTime());
    }

    static Calendar getCalendar(final long timeWithoutTimezone, final TimeZone tz) {
        final Calendar c = Calendar.getInstance(tz);
        c.setTime(new Date(timeWithoutTimezone));
        return c;
    }

    static void deleteAlarm(final Context context, final long alarmId, final String alarmDesc) {
        deleteAlarm(context, new AlarmDbHelper(context), alarmId, alarmDesc);
    }

    static void deleteAlarm(final Context context, final AlarmDbHelper dbHelper, final long alarmId, final String alarmDesc) {
        cancelAlarm(context, alarmId, alarmDesc);
        final AlarmDto dto = new AlarmDto(alarmId, alarmDesc, null, -1, -1, false);
        new DeleteAlarmInDbTask(dbHelper).execute(dto);
    }

    static int getSnoozeIntervalInMilliSeconds(final Activity activity) {
        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(activity);
        final String snoozeIntervalKeyLocal = getOrCreateSnoozeIntervalKey(activity);
        final int defaultSnoozeIntervalLocal = getOrCrateDefaultSnoozeInterval(activity);
        final String currentSnoozeInterval = sharedPreferences.getString(snoozeIntervalKeyLocal, String.valueOf
                (defaultSnoozeIntervalLocal));
        return Integer.valueOf(currentSnoozeInterval) * 60 *1000;
    }

    static String getOrCreateSnoozeIntervalKey(final Activity activity) {
        if (prefSnoozeIntervalKey == null) {
            createSnoozeIntervalKey(activity);
        }
        return prefSnoozeIntervalKey;
    }

    private static void createSnoozeIntervalKey(final Activity activity) {
        final Resources resources = activity.getResources();
        prefSnoozeIntervalKey = resources.getString(R.string
                .pref_snoozeIntervalKey);
    }

    static int getOrCrateDefaultSnoozeInterval(final Activity activity) {
        if (prefDefaultSnoozeInterval == null) {
            createDefaultSnoozeInterval(activity);
        }
        return prefDefaultSnoozeInterval;
    }

    private static void createDefaultSnoozeInterval(final Activity activity) {
        final Resources resources = activity.getResources();
        try {
            prefDefaultSnoozeInterval = resources.getInteger(R.integer
                    .pref_snoozeIntervalDefaultValue);
        } catch (Resources.NotFoundException e) {
            prefDefaultSnoozeInterval = 5;
        }
    }

    private static void logAlarmDetails(final Calendar c, final long alarmId,
                                        final int intervalInMinutesToNextAlarm,
                                        final int numOccurrences, final String alarmDesc) {
        final StringBuilder b = new StringBuilder();
        final String sep = "-";
        final String timeSep = ":";
        b.append("id: ");
        b.append(alarmId);
        b.append(", day: ");
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
