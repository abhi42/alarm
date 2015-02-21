package org.ap.android.alarm;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by abhi on 14.02.15.
 */
public class DatePickerFragment extends BaseDialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // use current date as default value
        final Calendar cal = Calendar.getInstance();
        final int day = cal.get(Calendar.DAY_OF_MONTH);
        final int month = cal.get(Calendar.MONTH);
        final int yr = cal.get(Calendar.YEAR);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, yr,
                month, day);

        datePickerDialog.setCancelable(true);
        datePickerDialog.setCanceledOnTouchOutside(true);

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", this);
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", this);

        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (activity != null && updateValue) {
            activity.setDate(year, monthOfYear, dayOfMonth);
        }
    }

    @Override
    protected void postSetActivity() {
        final Calendar cal = Calendar.getInstance();
        activity.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }
}
