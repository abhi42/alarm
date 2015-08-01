package org.ap.android.alarm.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.ap.android.alarm.R;
import org.ap.android.alarm.common.AlarmType;
import org.ap.android.alarm.common.AlarmUtils;
import org.ap.android.alarm.dto.AlarmDailyIntervalDateTimeDto;
import org.ap.android.alarm.dto.AlarmDto;

/**
 * Fragment providing the layout handling for an alarm that occurs daily
 * Created by abhi on 25.05.15.
 */
public class AlarmDailyOccurrenceFragment extends AbstractDateTimeFragment implements IAlarmUiAdditionalData {

    private Button datePickerBtn;
    private Button timePickerBtn;
    private Button alarmIntervalBtn;
    private Spinner occurencesPicker;
    private int alarmInterval;
    private AlarmDailyIntervalDateTimeDto alarmIntervalDto;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_daily_alarm, container, false);
        init(v);
        return v;
    }

    private void init(final View v) {
        initUi(v);
    }

    private void initUi(final View v) {
        /*
        In a fragment that is inflated, the views defined in the xml are not available after it has been attached to the activity.
        They cannot be obtained later in the fragment using getActivity().findViewById(). That can only be done in the activity.
        If they need to be obtained in the fragment, they have to be done using the root view obtained from the inflater.
         */
        initDatePickerUi(v);
        initTimePickerUi(v);
        initIntervalUi(v);
        initNumOccurrencesUi(v);
        final AlarmDto currentDto = AlarmDto.getCurrentDto();
        if (currentDto != null) {
            setUi(currentDto);
        }
    }

    private void initIntervalUi(final View v) {
        alarmIntervalBtn = (Button) v.findViewById(R.id.intervalPickerBtn);
        alarmIntervalDto = new AlarmDailyIntervalDateTimeDto(alarmIntervalBtn);
        // set the default interval
        alarmIntervalDto.setAlarmTime(0, 30);
        alarmIntervalBtn.setOnClickListener(new TimeButtonOnClickListenener(getFragmentManager(), alarmIntervalDto));
    }

    private void initNumOccurrencesUi(final View v) {
        occurencesPicker = (Spinner) v.findViewById(R.id.occurrencesPicker);
        final String[] occurrencesOptions = getOccurrencesOptions();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(v.getContext(),
                android.R.layout.simple_spinner_item, occurrencesOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        occurencesPicker.setAdapter(adapter);

        final View intervalTextView = v.findViewById(R.id.alarmIntervalTxt);
        occurencesPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                if (id > 0) {
                    // 0 based i.e. id = 0 = 1
                    intervalTextView.setVisibility(View.VISIBLE);
                    alarmIntervalBtn.setVisibility(View.VISIBLE);
                } else {
                    intervalTextView.setVisibility(View.GONE);
                    alarmIntervalBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                intervalTextView.setVisibility(View.GONE);
                alarmIntervalBtn.setVisibility(View.GONE);
            }
        });
    }

    private String[] getOccurrencesOptions() {
        // eventually use preferences for populating this list
        final int maxOccurrences = 12;
        final String[] arr = new String[12];
        for (int i = 0; i < maxOccurrences; i++) {
            arr[i] = String.valueOf(i + 1);
        }
        return arr;
    }

    private int getSelectedNumOccurrences() {
        final String occurrences = (String) this.occurencesPicker.getSelectedItem();
        return Integer.parseInt(occurrences);
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
        final String time = AlarmUtils.formatTime(hr, min);
        timePickerBtn.setText(time);
    }

    @Override
    protected void doPostDateSet(final int yr, final int month, final int day) {
        final String date = AlarmUtils.formatDate(yr, month, day);
        datePickerBtn.setText(date);
    }

    @Override
    public void addAlarmData(final AlarmDto dto) {
        dto.setType(AlarmType.DAILY);
        setDateTimeInAlarmDto(dto);
        final int numOccurrences = getSelectedNumOccurrences();
        dto.setNumOccurrences(numOccurrences);
        if (numOccurrences > 1) {
            dto.setInterval(alarmIntervalDto.getAlarmInterval());
        }
    }

    @Override
    public void setUi(final AlarmDto dto) {
        setDateTimeFromAlarmDto(dto);
    }
}
