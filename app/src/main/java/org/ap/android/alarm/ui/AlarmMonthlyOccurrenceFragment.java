package org.ap.android.alarm.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.ap.android.alarm.R;
import org.ap.android.alarm.common.AlarmType;
import org.ap.android.alarm.common.AlarmUtils;
import org.ap.android.alarm.dto.AlarmDto;

import java.util.TimeZone;

/**
 * Created by abhi on 07.06.15.
 */
public class AlarmMonthlyOccurrenceFragment extends AbstractDateTimeFragment implements IAlarmUiAdditionalData {

    private Button datePickerBtn;
    private Button timePickerBtn;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_monthly_alarm, container, false);
        init(v);
        return v;
    }

    private void init(final View v) {
        initUi(v);
        final AlarmDto currentDto = AlarmDto.getCurrentDto();
        if (currentDto != null) {
            setUi(currentDto);
        }
    }

    private void initUi(final View v) {
        initDatePickerUi(v);
        initTimePickerUi(v);
    }

    private void initTimePickerUi(final View v) {
        timePickerBtn = (Button) v.findViewById(R.id.timePickerBtn);
        timePickerBtn.setOnClickListener(new TimeButtonOnClickListenener(getFragmentManager(), this));
    }

    private void initDatePickerUi(final View v) {
        datePickerBtn = (Button) v.findViewById(R.id.datePickerBtn);
        datePickerBtn.setOnClickListener(new DateButtonOnClickListener(getFragmentManager(), this));
    }

    @Override
    protected void doPostTimeSet(final int hr, final int min) {
        final String s = AlarmUtils.formatTime(hr, min);
        timePickerBtn.setText(s);
    }

    @Override
    protected void doPostDateSet(final int yr, final int month, final int day) {
        final String s = AlarmUtils.formatDate(yr, month, day);
        datePickerBtn.setText(s);
    }

    @Override
    public void addAlarmData(final AlarmDto dto) {
        dto.setType(AlarmType.MONTHLY);
        setDateTimeInAlarmDto(dto);
    }

    @Override
    public void setUi(final AlarmDto dto) {
        setDateTimeFromAlarmDto(dto);

        final long startTimeWithoutTz = dto.getStartTimeWithoutTz();
        final TimeZone tz = dto.getTz();

        final String time = AlarmUtils.formatTime(startTimeWithoutTz, tz.getID());
        timePickerBtn.setText(time);

        final String date = AlarmUtils.formatDate(startTimeWithoutTz, tz.getID());
        datePickerBtn.setText(date);
    }
}
