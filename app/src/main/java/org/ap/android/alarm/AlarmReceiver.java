package org.ap.android.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getName();

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final int numOccurrences = intent.getIntExtra(AlarmUtils.NUM_OCCURRENCES, -1);
        final String alarmDesc = intent.getStringExtra(AlarmUtils.ALARM_DESC);
        Log.d(TAG, "in onReceive(), number of alarm occurrences: " + numOccurrences);
        if (numOccurrences > 1) {
            final int intervalInMinutes = intent.getIntExtra(AlarmUtils.INTERVAL_IN_MINUTES, -1);
            AlarmUtils.createAlarm(context, intervalInMinutes, numOccurrences - 1,
                    alarmDesc);
        }
        final Intent activityIntent = new Intent(context, AlarmReceiverActivity.class);
        activityIntent.putExtra(AlarmUtils.ALARM_DESC, alarmDesc);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activityIntent);
    }
}
