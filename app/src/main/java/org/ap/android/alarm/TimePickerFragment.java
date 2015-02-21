package org.ap.android.alarm;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends BaseDialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // use the current time as the default value
        final Calendar cal = Calendar.getInstance();
        final int hr = cal.get(Calendar.HOUR_OF_DAY);
        final int min = cal.get(Calendar.MINUTE);

        final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hr,
                min, DateFormat.is24HourFormat(getActivity()));

        timePickerDialog.setCancelable(true);
        timePickerDialog.setCanceledOnTouchOutside(true);

        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", this);
        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", this);

        return timePickerDialog;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (activity != null && updateValue) {
            activity.setTime(hourOfDay, minute);
        }
    }

    @Override
    protected void postSetActivity() {
        final Calendar cal = Calendar.getInstance();
        final int hr = cal.get(Calendar.HOUR_OF_DAY);
        final int min = cal.get(Calendar.MINUTE);
        activity.setTime(hr, min);
    }
}
