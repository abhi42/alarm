package org.ap.android.alarm;

/**
 * Created by abhi on 19.03.15.
 */
public interface AlarmRetrievedListener {

    void handleAlarmObtainedFromDb(final AlarmDto dto);
}
