package org.ap.android.alarm.ui;

import android.app.Fragment;
import android.view.MenuItem;

import org.ap.android.alarm.R;
import org.ap.android.alarm.common.IAlarmDateTime;
import org.ap.android.alarm.dto.AlarmDateTimeDto;
import org.ap.android.alarm.dto.AlarmDto;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by abhi on 03.05.15.
 */
public abstract class AbstractDateTimeFragment extends Fragment implements IAlarmDateTime {

    private IAlarmDateTime dateTimeDto = new AlarmDateTimeDto();

    @Override
    public void setAlarmTime(final int hr, final int min) {
        dateTimeDto.setAlarmTime(hr, min);
        doPostTimeSet(hr, min);
    }

    protected abstract void doPostTimeSet(final int hr, final int min);

    @Override
    public int getAlarmHr() {
        return dateTimeDto.getAlarmHr();
    }

    @Override
    public int getAlarmMinute() {
        return dateTimeDto.getAlarmMinute();
    }

    @Override
    public void setAlarmDate(final int yr, final int month, final int day) {
        dateTimeDto.setAlarmDate(yr, month, day);
        doPostDateSet(yr, month, day);
    }

    @Override
    public int getAlarmYear() {
        return dateTimeDto.getAlarmYear();
    }

    @Override
    public int getAlarmMonth() {
        return dateTimeDto.getAlarmMonth();
    }

    @Override
    public int getAlarmDay() {
        return dateTimeDto.getAlarmDay();
    }

    protected abstract void doPostDateSet(final int yr, final int month, final int day);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent alarmDateTimeDto in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void setDateTimeInAlarmDto(final AlarmDto dto) {
        final int alarmYear = getAlarmYear();
        final int alarmMonth = getAlarmMonth();
        final int alarmDay = getAlarmDay();
        final int alarmHr = getAlarmHr();
        final int alarmMinute = getAlarmMinute();

        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, alarmYear);
        c.set(Calendar.MONTH, alarmMonth);
        c.set(Calendar.DAY_OF_MONTH, alarmDay);
        c.set(Calendar.HOUR_OF_DAY, alarmHr);
        c.set(Calendar.MINUTE, alarmMinute);
        final long timeInMillis = c.getTimeInMillis();

        dto.setStartTimeWithoutTz(timeInMillis);
        dto.setTz(TimeZone.getDefault());
    }

    protected void setDateFromAlarmDto(final AlarmDto dto) {
        setDateTimeFromAlarmDto(dto, true, false);
    }

    protected void setTimeFromAlarmDto(final AlarmDto dto) {
        setDateTimeFromAlarmDto(dto, false, true);
    }

    protected void setDateTimeFromAlarmDto(final AlarmDto dto) {
        setDateTimeFromAlarmDto(dto, true, true);
    }

    private void setDateTimeFromAlarmDto(final AlarmDto dto, final boolean setDate, final boolean setTime) {
        if (!setDate && !setTime) {
            return;
        }

        final Calendar c = Calendar.getInstance(dto.getTz());
        c.setTimeInMillis(dto.getStartTimeWithoutTz());

        if (setDate) {
            setAlarmDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        }

        if (setTime) {
            setAlarmTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        }
    }
}
