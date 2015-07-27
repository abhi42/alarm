package org.ap.android.alarm.ui;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;


public class TimePickerFragment extends BaseDialogFragment implements TimePickerDialog
        .OnTimeSetListener, DialogInterface
        .OnClickListener {

    private static final String TAG = TimePickerFragment.class.getName();

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final int hr = alarmDateTimeDto.getAlarmHr();
        final int min = alarmDateTimeDto.getAlarmMinute();

        final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hr,
                min, DateFormat.is24HourFormat(getActivity()));

        timePickerDialog.setCancelable(true);
        timePickerDialog.setCanceledOnTouchOutside(true);

        final int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt < Build.VERSION_CODES.LOLLIPOP) {
            timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", this);
            timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", this);
        }

        return timePickerDialog;
    }

    @Override
    public void onTimeSet(final TimePicker view, final int hourOfDay, final int minute) {
        final int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.LOLLIPOP) {
            updateValue = true;
        }
        Log.d(TAG, "hrOfDay: " + hourOfDay + ", min: " + minute + ", updateValue: " + updateValue
                + ", alarmDateTimeDto: " + alarmDateTimeDto.getClass().getName());
        if (alarmDateTimeDto != null && updateValue) {
            alarmDateTimeDto.setAlarmTime(hourOfDay, minute);
        }
    }

    @Override
    protected void postSetAlarmDateTimeDto() {
        final int alarmHr = alarmDateTimeDto.getAlarmHr();
        final int alarmMinute = alarmDateTimeDto.getAlarmMinute();
        if (alarmHr > 0 || alarmMinute > 0) {
            return;
        }
        final Calendar cal = Calendar.getInstance();
        final int hr = cal.get(Calendar.HOUR_OF_DAY);
        final int min = cal.get(Calendar.MINUTE);
        alarmDateTimeDto.setAlarmTime(hr, min);
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                updateValue = true;
                break;

            default:
                updateValue = false;
                break;
        }
    }
}
