package org.ap.android.alarm.db;

import org.ap.android.alarm.dto.AlarmDto;

/**
 * Created by abhi on 07.06.15.
 */
public interface IAlarmOperationInDbListener {

    void onInsertOperationPerformed(final long rowId);

    void onUpdateOperationPerformed(final boolean success);

    void onRetrieveOperationPerformed(final AlarmDto dto);
}
