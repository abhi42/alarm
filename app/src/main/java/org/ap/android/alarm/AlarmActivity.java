package org.ap.android.alarm;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AlarmActivity extends ActionBarActivity {

    private Button timePickerBtn;
    private DatePickerFragment datePickerFragment;
    private Button datePickerBtn;
    private TimePickerFragment timePickerFragment;
    private AlarmIntervalFragment alarmIntervalPickerFragment;
    private Spinner occurencesPicker;
    private Button alarmIntervalBtn;

    private int day;
    private int month;
    private int yr;
    private int hr;
    private int min;
    private int alarmInterval;
    private int numOccurrences;

    private static final String TAG = AlarmActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        initUI();
    }

    private void initUI() {
        initDatePickerUi();
        initTimePickerUi();
        initAlarmInterval();
        initAlarmOccurrences();
    }

    private void initAlarmOccurrences() {
        occurencesPicker = (Spinner) findViewById(R.id.occurrencesPicker);
        final String[] occurrencesOptions = getOccurrencesOptions();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, occurrencesOptions);
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

    private void initDatePickerUi() {
        datePickerBtn = (Button) findViewById(R.id.datePickerBtn);
        final String currentDateStr = getCurrentDate();
        datePickerBtn.setText(currentDateStr);

        datePickerFragment = new DatePickerFragment();
        datePickerFragment.setActivity(this);
    }

    public void onDatePickerBtnClick(final View view) {
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    private String getCurrentDate() {
        return formatDate();
    }

    private String formatDate() {
        final Calendar cal = Calendar.getInstance();
        final String dateStr = new SimpleDateFormat("E, LLL dd, yyyy").format(cal.getTime());
        return dateStr;
    }

    private String formatDate(final int year, final int month, final int day) {
        final Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        final String dateStr = new SimpleDateFormat("E, LLL dd, yyyy").format(cal.getTime());
        return dateStr;
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
        final String currentTimeStr = getCurrentTime();
        timePickerBtn.setText(currentTimeStr);

        timePickerFragment = new TimePickerFragment();
        timePickerFragment.setActivity(this);
    }

    public void onTimePickerBtnClick(final View view) {
        timePickerFragment.show(getFragmentManager(), "timePicker");
    }

    private String getCurrentTime() {
        return formatTime();
    }

    void setTime(final int hr, final int minute) {
        this.hr = hr;
        this.min = minute;
        final String s = formatTime(hr, minute);
        timePickerBtn.setText(s);
    }

    private String formatTime() {
        final Calendar cal = Calendar.getInstance();
        return formatTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }

    private String formatTime(final int hr, final int minute) {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hr);
        c.set(Calendar.MINUTE, minute);
        return new SimpleDateFormat("HH:mm").format(c.getTime());
    }

    public void onIntervalPickerBtnClick(final View view) {
        alarmIntervalPickerFragment.show(getFragmentManager(), "intervalPicker");
    }

    void setAlarmInterval(final int days, final int hrs, final int minutes) {
        alarmInterval = (hrs * 60) + minutes;
        final StringBuilder b = new StringBuilder();
        boolean hasDays = writeDaysText(days, b);
        boolean hasHrs = writeHrsText(hrs, b, hasDays);
        writeMinutesText(minutes, b, hasHrs);
        alarmIntervalBtn.setText(b.toString());
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

    public void onSubmitAlarmClick(final View view) {
        final String occurrences = (String) this.occurencesPicker.getSelectedItem();
        numOccurrences = Integer.parseInt(occurrences);
        createAlarm();
    }

    private void createAlarm() {
        final Calendar c = Calendar.getInstance();
        c.set(yr, month, day, hr, min, 0);
        final EditText alarmDescTxt = (EditText) findViewById(R.id.alarmDescTxt);
        final CharSequence text = alarmDescTxt.getText();
        AlarmUtils.createInitialAlarm(this, c, alarmInterval, numOccurrences, text == null ? "" : text.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_alarm, menu);
        return true;
    }

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
