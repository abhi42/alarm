package org.ap.android.alarm.ui;

import android.app.FragmentManager;
import android.view.View;

import org.ap.android.alarm.common.IAlarmDateTime;

/**
 * Created by abhi on 25.05.15.
 */
public class DateButtonOnClickListener implements View.OnClickListener {

    private final DatePickerFragment datePickerFragment;
    private final FragmentManager fragmentManager;

    DateButtonOnClickListener(final FragmentManager fragmentManager, final IAlarmDateTime dto) {
        this.fragmentManager = fragmentManager;
        datePickerFragment = new DatePickerFragment();
        datePickerFragment.setAlarmDateTimeDto(dto);
    }

    @Override
    public void onClick(final View view) {
        onDatePickerBtnClick();
    }

    private void onDatePickerBtnClick() {
        datePickerFragment.show(fragmentManager, "datePicker");
    }
}
