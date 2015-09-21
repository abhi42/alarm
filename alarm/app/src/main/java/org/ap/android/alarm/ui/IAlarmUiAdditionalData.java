package org.ap.android.alarm.ui;

import org.ap.android.alarm.dto.AlarmDto;

/**
 * Created by abhi on 07.06.15.
 * Provides additional data for the alarm based on a specific ui (e.g. daily/weekly/monthly)
 */
public interface IAlarmUiAdditionalData {

    void addAlarmData(final AlarmDto dto);

    void setUi(final AlarmDto dto);
}
