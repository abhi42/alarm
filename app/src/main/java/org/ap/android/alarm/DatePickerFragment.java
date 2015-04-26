package org.ap.android.alarm;

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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar cal = activity.getSelectedDateTime();
        final int day = cal.get(Calendar.DAY_OF_MONTH);
        final int month = cal.get(Calendar.MONTH);
        final int yr = cal.get(Calendar.YEAR);

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
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        final int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.LOLLIPOP) {
            updateValue = true;
        }
        if (activity != null && updateValue) {
            activity.setDate(year, monthOfYear, dayOfMonth);
        }
    }

    @Override
    protected void postSetActivity() {
        final Calendar cal = Calendar.getInstance();
        activity.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
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
