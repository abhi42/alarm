package org.ap.android.alarm.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;

import org.ap.android.alarm.R;
import org.ap.android.alarm.common.AlarmType;
import org.ap.android.alarm.common.AlarmUtils;
import org.ap.android.alarm.db.AlarmDbHelper;
import org.ap.android.alarm.db.IAlarmOperationInDbListener;
import org.ap.android.alarm.dto.AlarmDto;

/**
 * Created by abhi on 17.05.15.
 * The activity containing the common layout. It adds the appropriate fragments.
 */
abstract class SingleAlarmActivity extends ActionBarActivity implements IAlarmOperationInDbListener {

    private static final int MODE_NEW_ALARM = 0;
    private static final int MODE_EDIT_ALARM = 1;
    private static final String TAG = SingleAlarmActivity.class.getName();
    private int mode;
    private AlarmDbHelper alarmDbHelper;
    private IAlarmUiAdditionalData additionalDataProviderFromUi;
    private Switch alarmEnabledWidget;
    private EditText alarmDescWidget;
    private RadioButton alarmTypeDailyWidget;
    private RadioButton alarmTypeWeeklyWidget;
    private RadioButton alarmTypeMonthlyWidget;

    protected abstract int getLayoutId();

    protected abstract void handleAlarmOperation();

    protected abstract void getValuesToPopulateUi();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMode();
        initUi();
    }

    private void initUi() {
        setContentView(getLayoutId());
        initCommonUi();
        // default alarm type
        if (MODE_NEW_ALARM == mode) {
            loadUiSpecificFragment(AlarmType.WEEKLY);
        }
        setSubmitBtnLabel();
        getValuesToPopulateUi();
    }

    private void initCommonUi() {
        alarmDescWidget = (EditText) findViewById(R.id.alarmDescTxt);
        alarmEnabledWidget = (Switch) findViewById(R.id.enableSwitcher);
        alarmTypeDailyWidget = (RadioButton) findViewById(R.id.choiceAlarmTypeDaily);
        alarmTypeWeeklyWidget = (RadioButton) findViewById(R.id.choiceAlarmTypeWeekly);
        alarmTypeMonthlyWidget = (RadioButton) findViewById(R.id.choiceAlarmTypeMonthly);
    }

    private void setSubmitBtnLabel() {
        final Button btn = (Button) findViewById(R.id.submitAlarmBtn);
        if (mode == MODE_NEW_ALARM) {
            btn.setText(R.string.submitAlarmBtnText);
        } else {
            btn.setText(R.string.submitEditAlarmBtnText);
        }
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

    public void onClickChooseAlarmType(final View v) {
        final boolean checked = ((RadioButton) v).isChecked();
        if (!checked) {
            return;
        }

        switch (v.getId()) {
            case R.id.choiceAlarmTypeWeekly:
                loadWeeklyAlarmUi();
                break;

            case R.id.choiceAlarmTypeMonthly:
                loadMonthlyAlarmUi();
                break;

            case R.id.choiceAlarmTypeDaily:
            default:
                loadDailyAlarmUi();
                break;
        }
    }

    private void loadDailyAlarmUi() {
        final AlarmDailyOccurrenceFragment alarmDailyOccurrenceFragment = new AlarmDailyOccurrenceFragment();
        additionalDataProviderFromUi = alarmDailyOccurrenceFragment;
        loadFragment(alarmDailyOccurrenceFragment);
    }

    private void loadWeeklyAlarmUi() {
        final AlarmWeeklyOccurrenceFragment alarmWeeklyOccurrenceFragment = new AlarmWeeklyOccurrenceFragment();
        additionalDataProviderFromUi = alarmWeeklyOccurrenceFragment;
        loadFragment(alarmWeeklyOccurrenceFragment);
    }

    private void loadMonthlyAlarmUi() {
        final AlarmMonthlyOccurrenceFragment alarmMonthlyOccurrenceFragment = new AlarmMonthlyOccurrenceFragment();
        additionalDataProviderFromUi = alarmMonthlyOccurrenceFragment;
        loadFragment(alarmMonthlyOccurrenceFragment);
    }

    private void loadFragment(final Fragment fragment) {
        final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.specificAlarmTypeFragmentContainer, fragment);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void onSubmitAlarmClick(final View v) {
        final AlarmDto dto = gatherAlarmData();
        handleAlarmOperation();
//        logAlarmData(dto);
    }

    private AlarmDto gatherAlarmData() {
        final AlarmDto dto = new AlarmDto();

        setCommonData(dto);
        additionalDataProviderFromUi.addAlarmData(dto);
        AlarmDto.setCurrentDto(dto);
        return dto;
    }

    private void setCommonData(final AlarmDto dto) {
        dto.setDesc(alarmDescWidget.getText().toString());
        dto.setIsEnabled(alarmEnabledWidget.isChecked());
        final long alarmId = getIntent().getLongExtra(AlarmUtils.ALARM_ID_BEING_PASSED, -1);
        dto.setId(alarmId);
    }

    private void logAlarmData(final AlarmDto dto) {
        Log.d(TAG, dto.toString());
    }

    protected AlarmDbHelper getOrCreateDbHelper() {
        if (alarmDbHelper == null) {
            alarmDbHelper = new AlarmDbHelper(this);
        }
        return alarmDbHelper;
    }

    @Override
    public void onUpdateOperationPerformed(final boolean success) {
        // do nothing here, sub classes may override
    }

    @Override
    public void onRetrieveOperationPerformed(final AlarmDto dto) {
        AlarmDto.setCurrentDto(dto);
        this.alarmDescWidget.setText(dto.getDesc());
        this.alarmEnabledWidget.setChecked(dto.isEnabled());
        final AlarmType type = dto.getType();
        checkCorrectAlarmTypeInUi(type);
        loadUiSpecificFragment(type);

        // the fragment's onCreate() method is only called after the activity's onCreate() has completed
        // this method is being called as part of the activity's onCreate().
        // hence we cannot call the method to popualate the fragment's ui from here, since the fragment's onCreate() has not yet been called.
    }

    private void checkCorrectAlarmTypeInUi(final AlarmType type) {
        switch (type) {
            case DAILY:
                alarmTypeDailyWidget.setChecked(true);
                break;

            case WEEKLY:
                alarmTypeWeeklyWidget.setChecked(true);
                break;

            case MONTHLY:
                alarmTypeMonthlyWidget.setChecked(true);
                break;
        }
    }

    private void loadUiSpecificFragment(final AlarmType type) {
        switch (type) {
            case DAILY:
                loadDailyAlarmUi();
                break;

            case WEEKLY:
            default:
                loadWeeklyAlarmUi();
                break;

            case MONTHLY:
                loadMonthlyAlarmUi();
                break;
        }
    }
}
