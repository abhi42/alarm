package org.ap.android.alarm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by abhi on 01.03.15.
 */
abstract class BaseAlarmActivity extends ActionBarActivity implements AlarmRetrievedListener, AlarmUpdatedListener {

    private Button timePickerBtn;
    private DatePickerFragment datePickerFragment;
    private Button datePickerBtn;
    private TimePickerFragment timePickerFragment;
    private AlarmIntervalFragment alarmIntervalPickerFragment;
    private Spinner occurencesPicker;
    private Button alarmIntervalBtn;

    private int yr;
    private int month;
    private int day;
    private int hr;
    private int min;
    private int alarmInterval;
    private boolean isAlarmEnabled;

    private AlarmDbHelper alarmDbHelper;
    private AlarmDto dto;
    private int mode;

    private static final int MODE_NEW_ALARM = 0;

    private static final int MODE_EDIT_ALARM = 1;
    private static final String TAG = BaseAlarmActivity.class.getName();

    protected abstract int getLayoutId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        setMode();
        initUi();
    }

    private void setMode() {
        final long alarmId = getAlarmId();
        if (alarmId == -1) {
            mode = MODE_NEW_ALARM;
        } else {
            mode = MODE_EDIT_ALARM;
        }
    }

    protected long getAlarmId() {
        return getIntent().getLongExtra(AlarmUtils.ALARM_ID_BEING_PASSED, -1);
    }

    private void initUi() {
        getInitialUiValues();
    }

    void populateUi(final AlarmDto dto) {
        if (dto == null) {
            Toast.makeText(this, "Unable to retrieve alarm details", Toast.LENGTH_LONG).show();
            return;
        }
        this.dto = dto;

        initDatePickerUi();
        initTimePickerUi();
        initAlarmInterval();
        initAlarmOccurrences();

        final EditText alarmDescView = (EditText) findViewById(R.id.alarmDescTxt);
        alarmDescView.setText(dto.getDescription());

        Date startDateTime;
        final long startTimeWithoutTz = dto.getStartTimeWithoutTz();
        final TimeZone tz = dto.getTz();
        final Calendar c = Calendar.getInstance(tz);
        c.setTime(new Date(startTimeWithoutTz));
        startDateTime = c.getTime();
        this.yr = c.get(Calendar.YEAR);
        this.month = c.get(Calendar.MONTH);
        this.day = c.get(Calendar.DAY_OF_MONTH);
        this.hr = c.get(Calendar.HOUR_OF_DAY);
        this.min = c.get(Calendar.MINUTE);


        final Button startDateBtn = (Button) findViewById(R.id.datePickerBtn);
        final String startDateTxt = formatDateTimeTxt(startDateTime.getTime(), AlarmUtils.DEFAULT_DATE_FORMAT);
        startDateBtn.setText(startDateTxt);

        final Button startTimeBtn = (Button) findViewById(R.id.timePickerBtn);
        final String startTimeTxt = formatDateTimeTxt(startDateTime.getTime(), AlarmUtils.DEFAULT_TIME_FORMAT);
        startTimeBtn.setText(startTimeTxt);

        final Button intervalBtn = (Button) findViewById(R.id.intervalPickerBtn);
        intervalBtn.setText(String.valueOf(dto.getInterval()));
        final View intervalTextView = findViewById(R.id.alarmIntervalTxt);

        final Spinner occurencesPicker = (Spinner) findViewById(R.id.occurrencesPicker);
        final String[] occurrencesOptions = getOccurrencesOptions();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, occurrencesOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        occurencesPicker.setAdapter(adapter);
        occurencesPicker.setSelection(dto.getNumOccurrences() - 1);
        occurencesPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                if (id > 0) {
                    // 0 based i.e. id = 0 = 1
                    intervalTextView.setVisibility(View.VISIBLE);
                    intervalBtn.setVisibility(View.VISIBLE);
                } else {
                    intervalTextView.setVisibility(View.GONE);
                    intervalBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                intervalTextView.setVisibility(View.GONE);
                intervalBtn.setVisibility(View.GONE);
            }
        });

        final Switch alarmEnableToggler = (Switch) findViewById(R.id.enableSwitcher);
        alarmEnableToggler.setChecked(dto.isEnabled());

        final Button b = (Button) findViewById(R.id.submitAlarmBtn);
        if (MODE_EDIT_ALARM == mode) {
            b.setText(R.string.submitEditAlarmBtnText);
        } else {
            b.setText(R.string.submitAlarmBtnText);
        }
    }

    private String formatDateTimeTxt(final long dateTime, final DateFormat formatter) {
        final Date d = new Date(dateTime);
        return formatter.format(d);
    }

    protected abstract void getInitialUiValues();

    private void initAlarmOccurrences() {
        occurencesPicker = (Spinner) findViewById(R.id.occurrencesPicker);
        final String[] occurrencesOptions = getOccurrencesOptions();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, occurrencesOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        occurencesPicker.setAdapter(adapter);
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

    private void initAlarmInterval() {
        alarmIntervalBtn = (Button) findViewById(R.id.intervalPickerBtn);
        alarmIntervalPickerFragment = new AlarmIntervalFragment();
        alarmIntervalPickerFragment.setActivity(this);
    }

    protected void initDatePickerUi() {
        datePickerBtn = (Button) findViewById(R.id.datePickerBtn);
        datePickerFragment = new DatePickerFragment();
        datePickerFragment.setActivity(this);
    }

    public void onDatePickerBtnClick(final View view) {
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    private String formatDate(final int year, final int month, final int day) {
        final Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return AlarmUtils.DEFAULT_DATE_FORMAT.format(cal.getTime());
    }

    void setDate(final int year, final int month, final int day) {
        this.yr = year;
        this.month = month;
        this.day = day;
        final String s = formatDate(year, month, day);
        datePickerBtn.setText(s);
    }

    private void initTimePickerUi() {
        timePickerBtn = (Button) findViewById(R.id.timePickerBtn);
        timePickerFragment = new TimePickerFragment();
        timePickerFragment.setActivity(this);
    }

    public void onTimePickerBtnClick(final View view) {
        timePickerFragment.show(getFragmentManager(), "timePicker");
    }

    @Nullable
    @Override
    public ActionBarDrawerToggle.Delegate getV7DrawerToggleDelegate() {
        return super.getV7DrawerToggleDelegate();
    }

    void setTime(final int hr, final int minute) {
        this.hr = hr;
        this.min = minute;
        final String s = formatTime(hr, minute);
        timePickerBtn.setText(s);
    }

    private String formatTime(final int hr, final int minute) {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hr);
        c.set(Calendar.MINUTE, minute);
        return AlarmUtils.DEFAULT_TIME_FORMAT.format(c.getTime());
    }

    public void onIntervalPickerBtnClick(final View view) {
        alarmIntervalPickerFragment.show(getFragmentManager(), "intervalPicker");
    }

    void setAlarmInterval(final int days, final int hrs, final int minutes) {
        alarmInterval = days * 24 * 60 + (hrs * 60) + minutes;
        final StringBuilder b = new StringBuilder();
        boolean hasDays = writeDaysText(days, b);
        boolean hasHrs = writeHrsText(hrs, b, hasDays);
        writeMinutesText(minutes, b, hasHrs);
        alarmIntervalBtn.setText(b.toString());
    }

    protected int getSelectedNumOccurrences() {
        final String occurrences = (String) this.occurencesPicker.getSelectedItem();
        return Integer.parseInt(occurrences);
    }

    protected String getSelectedAlarmDesc() {
        final EditText alarmDescTxt = (EditText) findViewById(R.id.alarmDescTxt);
        final CharSequence text = alarmDescTxt.getText();
        final String alarmDesc = text == null ? "" : text.toString();
        return alarmDesc;
    }

    protected int getSelectedInterval() {
        return alarmInterval;
    }

    protected Calendar getSelectedDateTime() {
        final Calendar calForStartTime = Calendar.getInstance();
        calForStartTime.set(yr, month, day, hr, min, 0);
        return calForStartTime;
    }

    protected boolean isSelectedAlarmStatusEqualToEnabled() {
        return isAlarmEnabled;
    }

    private void writeMinutesText(final int minutes, final StringBuilder b, final boolean hasHrs) {
        if (minutes > 0) {
            if (hasHrs) {
                b.append(" ");
            }
            b.append(minutes);
            if (minutes > 1) {
                b.append(" minutes");
            } else {
                b.append(" minute");
            }
        }
    }

    private boolean writeHrsText(final int hrs, final StringBuilder b, final boolean hasDays) {
        if (hrs > 0) {
            if (hasDays) {
                b.append(" ");
            }
            b.append(hrs);
            if (hrs > 1) {
                b.append(" hours");
            } else {
                b.append(" hour");
            }
            return true;
        }
        return false;
    }

    private boolean writeDaysText(final int days, final StringBuilder b) {
        if (days > 0) {
            b.append(days);
            if (days > 1) {
                b.append(" days");
            } else {
                b.append(" day");
            }
            return true;
        }
        return false;
    }

    protected AlarmDbHelper getOrCreateDbHelper() {
        if (alarmDbHelper == null) {
            alarmDbHelper = new AlarmDbHelper(this);
        }
        return alarmDbHelper;
    }

    protected AlarmDto createAlarmDtoFromSelection() {
        if (MODE_EDIT_ALARM == mode) {
            return new AlarmDto(getAlarmId(), getSelectedAlarmDesc(), getSelectedDateTime().getTime(),
                    getSelectedNumOccurrences(), getSelectedInterval(),
                    isSelectedAlarmStatusEqualToEnabled());
        }
        return new AlarmDto(getSelectedAlarmDesc(), getSelectedDateTime().getTime(),
                getSelectedNumOccurrences(), getSelectedInterval(),
                isSelectedAlarmStatusEqualToEnabled());
    }

    public void onAlarmToggleClick(final View v) {
        final Switch s = (Switch) v;
        isAlarmEnabled = s.isChecked() ? true : false;
    }

    protected void setAlarmStatus(final boolean status) {
        isAlarmEnabled = status;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();
        Log.d(TAG, "Id of menu item selected: " + id);
        if (id == R.id.menuDeleteAlarm) {
            deleteAlarm();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAlarm() {
        cancelAlarmInSystem();
        final AlarmDbHelper alarmDbHelper = getOrCreateDbHelper();
        final AlarmDto dto = createAlarmDtoFromSelection();
        new DeleteAlarmInDbTask(this, alarmDbHelper).execute(dto);
    }

    protected void cancelAlarmInSystem() {
        AlarmUtils.cancelAlarm(this, getAlarmId(), getSelectedAlarmDesc());
    }

    void onAlarmDeleted(final boolean isDeleted) {
        if (isDeleted) {
            Toast.makeText(this, "Alarm deleted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void handleAlarmObtainedFromDb(final AlarmDto dto) {
        populateUi(dto);
    }

    @Override
    public void handleAlarmUpdatedInDb(boolean updated) {
        afterOperationPerformed(updated);
    }

    void afterOperationPerformed(final boolean success) {
        if (success) {
            Toast.makeText(this, "Alarm updated", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
