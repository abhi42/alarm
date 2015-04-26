package org.ap.android.alarm;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

/**
 * Created by abhi on 15.02.15.
 */
public class AlarmIntervalFragment extends BaseDialogFragment implements TimePickerDialog.OnTimeSetListener, DialogInterface
        .OnClickListener  {

    private int day = 0;
    private int hr = 1;
    private int min = 0;

    private static final String TAG = AlarmIntervalFragment.class.getName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, 0,
                30, DateFormat.is24HourFormat(getActivity()));

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
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        final int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.LOLLIPOP) {
            updateValue = true;
        }
        Log.d(TAG, "in onTimeSet: hr: " + hourOfDay + ", min: " + minute);
        if (activity != null && updateValue) {
            activity.setAlarmInterval(0, hourOfDay, minute);
        }
    }

    @Override
    protected void postSetActivity() {
        // default value of 1 hr
        activity.setAlarmInterval(day, hr, min);
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
