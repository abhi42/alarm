package org.ap.android.alarm.ui;

import android.app.FragmentManager;
import android.view.View;

import org.ap.android.alarm.common.IAlarmDateTime;

/**
 * Created by abhi on 25.05.15.
 */
public class TimeButtonOnClickListenener implements View.OnClickListener {

    private final TimePickerFragment timePickerFragment;
    private final FragmentManager fragmentManager;

    TimeButtonOnClickListenener(final FragmentManager fragmentManager, final IAlarmDateTime dto) {
        this.fragmentManager = fragmentManager;
        timePickerFragment = new TimePickerFragment();
        timePickerFragment.setAlarmDateTimeDto(dto);
    }

    @Override
    public void onClick(final View view) {
        onTimePickerBtnClick(view);
    }

    public void onTimePickerBtnClick(final View v) {
        timePickerFragment.show(fragmentManager, "timePicker");
    }
}
