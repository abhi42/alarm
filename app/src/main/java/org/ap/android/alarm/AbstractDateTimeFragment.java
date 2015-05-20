package org.ap.android.alarm;

import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

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
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
