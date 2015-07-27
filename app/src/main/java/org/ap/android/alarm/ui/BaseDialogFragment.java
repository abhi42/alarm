package org.ap.android.alarm.ui;

import android.app.DialogFragment;

import org.ap.android.alarm.common.IAlarmDateTime;

/**
 * Created by abhi on 14.02.15.
 */
public abstract class BaseDialogFragment extends DialogFragment {

    protected IAlarmDateTime alarmDateTimeDto;
    protected boolean updateValue;

    public void setAlarmDateTimeDto(final IAlarmDateTime dto) {
        this.alarmDateTimeDto = dto;
        postSetAlarmDateTimeDto();
    }

    protected abstract void postSetAlarmDateTimeDto();
}
