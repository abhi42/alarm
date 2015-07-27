package org.ap.android.alarm.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

import org.ap.android.alarm.R;
import org.ap.android.alarm.common.AlarmType;
import org.ap.android.alarm.common.AlarmUtils;
import org.ap.android.alarm.dto.AlarmDto;

import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Fragment providing the layout handling for an alarm that occurs weekly
 * Created by abhi on 09.05.15.
 */
public class AlarmWeeklyOccurrenceFragment extends AbstractDateTimeFragment implements IAlarmUiAdditionalData {

    private static final String TAG = AlarmWeeklyOccurrenceFragment.class.getName();
    private final Button[] weekdaysButtons = new Button[7];
    private final WeekButtonOnClickListener weekButtonOnClickListener = new WeekButtonOnClickListener();
    private Button timePickerBtn;
    private boolean[] selectedWeekDays;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_weekly_alarm, container, false);
        init(v);
        return v;
    }

    private void init(final View v) {
        if (selectedWeekDays == null) {
            selectedWeekDays = new boolean[7];
        }
        initUi(v);
        final AlarmDto currentDto = AlarmDto.getCurrentDto();
        if (currentDto != null) {
            setUi(currentDto);
        }
    }

    private void initUi(final View v) {
        /*
        In a fragment that is inflated, the views defined in the xml are not available after it has been attached to the activity.
        They cannot be obtained later in the fragment using getActivity().findViewById(). That can only be done in the activity.
        If they need to be obtained in the fragment, they have to be done using the root view obtained from the inflater.
         */
        initTimePickerUi(v);
        initWeekSelection(v);
    }


    private void initWeekSelection(final View v) {
        weekdaysButtons[0] = (Button) v.findViewById(R.id.Sun);
        weekdaysButtons[0].setOnClickListener(weekButtonOnClickListener);

        weekdaysButtons[1] = (Button) v.findViewById(R.id.Mon);
        weekdaysButtons[1].setOnClickListener(weekButtonOnClickListener);

        weekdaysButtons[2] = (Button) v.findViewById(R.id.Tue);
        weekdaysButtons[2].setOnClickListener(weekButtonOnClickListener);

        weekdaysButtons[3] = (Button) v.findViewById(R.id.Wed);
        weekdaysButtons[3].setOnClickListener(weekButtonOnClickListener);

        weekdaysButtons[4] = (Button) v.findViewById(R.id.Thu);
        weekdaysButtons[4].setOnClickListener(weekButtonOnClickListener);

        weekdaysButtons[5] = (Button) v.findViewById(R.id.Fri);
        weekdaysButtons[5].setOnClickListener(weekButtonOnClickListener);

        weekdaysButtons[6] = (Button) v.findViewById(R.id.Sat);
        weekdaysButtons[6].setOnClickListener(weekButtonOnClickListener);

        setWeekDaysWidgets(selectedWeekDays);
    }

    private void initTimePickerUi(final View v) {
        timePickerBtn = (Button) v.findViewById(R.id.weeklyAlarmtimePickerBtn);
        timePickerBtn.setOnClickListener(new TimeButtonOnClickListenener(getFragmentManager(), this));
    }

    public void onWeekDayClick(final View v) {
        final int id = v.getId();
        // cant use AlarmUtils.getDayOfWeekRep since the case statements here depend on the id of the week day widgets
        // todo change the ids to be the same as Calendar.SUNDAY etc....
        int dayOfWeekClicked = 0;
        switch (id) {
            case R.id.Sun:
                dayOfWeekClicked = 0;
                break;

            case R.id.Mon:
                dayOfWeekClicked = 1;
                break;

            case R.id.Tue:
                dayOfWeekClicked = 2;
                break;

            case R.id.Wed:
                dayOfWeekClicked = 3;
                break;

            case R.id.Thu:
                dayOfWeekClicked = 4;
                break;

            case R.id.Fri:
                dayOfWeekClicked = 5;
                break;

            case R.id.Sat:
                dayOfWeekClicked = 6;
                break;
        }
        handleClickOfWeekDay(dayOfWeekClicked, ((ToggleButton) v).isChecked());
    }

    private void handleClickOfWeekDay(final int dayOfWeek, final boolean isSelected) {
        selectedWeekDays[dayOfWeek] = isSelected;
    }

    @Override
    protected void doPostTimeSet(final int hr, final int min) {
        final String time = AlarmUtils.formatTime(hr, min);
        timePickerBtn.setText(time);
    }

    @Override
    protected void doPostDateSet(final int yr, final int month, final int day) {
        // nothing to do since there is no date field
    }

    @Override
    public void addAlarmData(final AlarmDto dto) {
        dto.setType(AlarmType.WEEKLY);
        dto.setWeekDays(selectedWeekDays);
        setTimeInAlarmDto(dto);
    }

    @Override
    public void setUi(final AlarmDto dto) {
        setTimeFromAlarmDto(dto); // only set time, the days is taken care of by weekdays member variable

        final long startTimeWithoutTz = dto.getStartTimeWithoutTz();
        final TimeZone tz = dto.getTz();
        final String s = AlarmUtils.formatTime(startTimeWithoutTz, tz.getID());
        timePickerBtn.setText(s);
        selectedWeekDays = dto.getWeekDays();
        Log.d(TAG, "week days obtained from dto (i.e. from db): " + Arrays.toString(selectedWeekDays));
        setWeekDaysWidgets(selectedWeekDays);
    }

    private void setWeekDaysWidgets(final boolean[] weekDays) {
        if (weekDays == null) {
            return;
        }
        for (int i = 0; i < 7; i++) {
            selectedWeekDays[i] = weekDays[i];
            if (weekDays[i]) {
                weekdaysButtons[i].performClick();
            }
        }
    }

    private void setTimeInAlarmDto(final AlarmDto dto) {
        final int alarmHr = getAlarmHr();
        final int alarmMinute = getAlarmMinute();

        final Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, alarmHr);
        c.set(Calendar.MINUTE, alarmMinute);
        final long timeInMillis = c.getTimeInMillis();

        dto.setStartTimeWithoutTz(timeInMillis);
        dto.setTz(TimeZone.getDefault());

        // important to call this api after dto.setStartTime(..) and dto.setTz() are called
        final long nextAlarmTimeForWeeklyAlarm = AlarmUtils.getNextAlarmTimeForWeeklyAlarm(dto);
        if (nextAlarmTimeForWeeklyAlarm < 0) {
            dto.setIsEnabled(false);
        } else {
            dto.setStartTimeWithoutTz(nextAlarmTimeForWeeklyAlarm);
        }
        final String s = AlarmUtils.formatDateTime(dto.getStartTimeWithoutTz(), dto.getTz().getID());
        Log.d(TAG, "time that will be set for weekly alarm: " + s);
    }

    private class WeekButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View view) {
            onWeekDayClick(view);
        }
    }
}
