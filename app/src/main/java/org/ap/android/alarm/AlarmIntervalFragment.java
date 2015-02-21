package org.ap.android.alarm;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

/**
 * Created by abhi on 15.02.15.
 */
public class AlarmIntervalFragment extends BaseDialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, 0,
                30, DateFormat.is24HourFormat(getActivity()));

        timePickerDialog.setCancelable(true);
        timePickerDialog.setCanceledOnTouchOutside(true);

        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", this);
        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", this);

        return timePickerDialog;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (activity != null && updateValue) {
            activity.setAlarmInterval(-1, hourOfDay, minute);
        }
    }

    @Override
    protected void postSetActivity() {
        // default value of 1 hr
        activity.setAlarmInterval(-1, 1, 0);
    }
}
