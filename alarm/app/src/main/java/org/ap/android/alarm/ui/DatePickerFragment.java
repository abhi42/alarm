package org.ap.android.alarm.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by abhi on 14.02.15.
 */
public class DatePickerFragment extends BaseDialogFragment implements DatePickerDialog.OnDateSetListener, DialogInterface
        .OnClickListener {

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final int day = alarmDateTimeDto.getAlarmDay();
        final int month = alarmDateTimeDto.getAlarmMonth();
        final int yr = alarmDateTimeDto.getAlarmYear();

        final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, yr,
                month, day);

        datePickerDialog.setCancelable(true);
        datePickerDialog.setCanceledOnTouchOutside(true);

        final int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt < Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", this);
            datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", this);
        }

        return datePickerDialog;
    }

    @Override
    public void onDateSet(final DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
        final int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.LOLLIPOP) {
            updateValue = true;
        }
        if (alarmDateTimeDto != null && updateValue) {
            alarmDateTimeDto.setAlarmDate(year, monthOfYear, dayOfMonth);
        }
    }

    @Override
    protected void postSetAlarmDateTimeDto() {
        final Calendar cal = Calendar.getInstance();
        alarmDateTimeDto.setAlarmDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
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
