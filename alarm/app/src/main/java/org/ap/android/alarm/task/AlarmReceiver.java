package org.ap.android.alarm.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.ap.android.alarm.common.AlarmUtils;
import org.ap.android.alarm.ui.AlarmReceiverActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final Intent activityIntent = new Intent(context, AlarmReceiverActivity.class);

        final long alarmId = intent.getLongExtra(AlarmUtils.ALARM_ID_BEING_PASSED, -1);
        activityIntent.putExtra(AlarmUtils.ALARM_ID_BEING_PASSED, alarmId);

        final String alarmDesc = intent.getStringExtra(AlarmUtils.ALARM_DESC);
        activityIntent.putExtra(AlarmUtils.ALARM_DESC, alarmDesc);

        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activityIntent);
    }
}
