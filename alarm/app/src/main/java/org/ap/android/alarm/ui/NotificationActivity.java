package org.ap.android.alarm.ui;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import org.ap.android.alarm.R;
import org.ap.android.alarm.common.AlarmUtils;

/**
 * Created by abhi on 31.07.15.
 */
public class NotificationActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final long alarmId = getIntent().getLongExtra(AlarmUtils.ALARM_ID_BEING_PASSED, -1);
        final String alarmDesc = getIntent().getStringExtra(AlarmUtils.ALARM_DESC);
        createNotification(alarmId, alarmDesc);
    }

    private void createNotification(final long alarmId, final String alarmDesc) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.alarm_logo)
                .setContentTitle("Missed Alarm")
                .setContentText(alarmDesc)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(createPendingIntent(alarmId, alarmDesc));

        final Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL; // when the notification is clicked, it is removed

        final NotificationManager m = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        m.notify(0, notification);
        finish();
    }

    private PendingIntent createPendingIntent(final long alarmId, final String alarmDesc) {
        final TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);

        // so that when the back button is clicked, the parent activity of this activity (as specified in manifest) is navigated to
        taskStackBuilder.addParentStack(EditSingleAlarmActivity.class);

        // this is the activity that will be called when the notification is touched.
        final Intent intent = new Intent(this, EditSingleAlarmActivity.class);
        intent.putExtra(AlarmUtils.ALARM_ID_BEING_PASSED, alarmId);
        intent.putExtra(AlarmUtils.ALARM_DESC, alarmDesc);
        taskStackBuilder.addNextIntent(intent);

        return taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
