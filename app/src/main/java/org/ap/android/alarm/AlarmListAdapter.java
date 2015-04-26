package org.ap.android.alarm;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by abhi on 22.02.15.
 */
public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.ViewHolder> {

    private final AlarmDbHelper dbHelper;
    private final MainAlarmActivity activity;
    private Cursor c;
    private final List<AlarmDto> alarms = new ArrayList<>();

    private static final String[] COLUMNS_TO_BE_RETRIEVED = {
            AlarmContract.AlarmEntry._ID,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_DESC,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_START_TIME,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_TIME_ZONE,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_NUM_OCCURRENCES,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_INTERVAL,
            AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_IS_ENABLED};

    private static final String SORT_ORDER = AlarmContract.AlarmEntry
            ._ID + " ASC";

    private static final String TAG = AlarmListAdapter.class.getName();

    public AlarmListAdapter(final MainAlarmActivity activity, final AlarmDbHelper dbHelper) {
        this.activity = activity;
        this.dbHelper = dbHelper;
        getDbContents();
    }

    @Override
    public AlarmListAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent,
                                                          final int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .main_alarm_list_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AlarmListAdapter.ViewHolder holder, final int position) {
        final AlarmDto alarmDto = alarms.get(position);
        setInView(holder, alarmDto);
    }

    private void setInView(final ViewHolder holder, final AlarmDto dto) {
        final String alarmDesc = dto.getDescription();
        setAlarmDescInView(holder, alarmDesc);
        holder.mStartTime.setText(getStartTime(dto));
        final String isEnabledStr = dto.isEnabled() ? activity.getString(R.string
                .alarmEnabledLabel) : activity.getString(R.string.alarmDisabledLabel);
        holder.mIsAlarmEnabled.setText(isEnabledStr);
        final long alarmId = dto.getId();
        Log.d(TAG, "alarmId: " + alarmId);
        holder.setAlarmId(alarmId);
        holder.setAlarmDesc(alarmDesc);
        handleAlarmsInThePast(holder, dto);
    }

    private String getStartTime(final AlarmDto dto) {
        final long startTime = dto.getStartTimeWithoutTz();
        final String timeZoneId = dto.getTz().getID();
        return AlarmUtils.formatDateTime(startTime, timeZoneId);
    }

    private void handleAlarmsInThePast(final ViewHolder holder, final AlarmDto dto) {
        final long startTime = dto.getStartTimeWithoutTz();
        final String tzId = dto.getTz().getID();
        final Calendar c = Calendar.getInstance(TimeZone.getTimeZone(tzId));
        c.setTime(new Date(startTime));
        final Calendar now = Calendar.getInstance();
        if (c.before(now)) {
            // the alarm is in the past, check if any occurences are remaining
            final int numOccurences = dto.getNumOccurrences();
            if (numOccurences < 2) {
                // the start date is in the past and there is no further occurence of this alarm
                holder.mParentView.setBackgroundColor(Color.GRAY);
            }
        }
    }

    private void setAlarmDescInView(final ViewHolder holder, final String text) {
        final String trimmedText = text.trim();
        if (text != null) {
            final int trimmedLen = trimmedText.length();
            final int maxLength = activity.getResources().getInteger(R.integer.list_alarm_desc_max_length);
            final int diff = trimmedLen - maxLength;
            if (diff > 0) {
                final String textToBeDisplayed = trimmedText.substring(0, maxLength - 3) + "...";
                holder.mAlarmDesc.setText(textToBeDisplayed);
            } else {
                holder.mAlarmDesc.setText(trimmedText);
            }
        } else {
            holder.mAlarmDesc.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    private void getDbContents() {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        c = db.query(AlarmContract.AlarmEntry.TABLE_NAME, COLUMNS_TO_BE_RETRIEVED,
                null, null, null, null, SORT_ORDER);
        createDtos(c);
        final String str = c == null ? "cursor is null" : "cursor has " + c.getCount() + " item(s)";
        Log.d(TAG, "Obtained cursor, " + str);
    }

    private void createDtos(final Cursor c) {
        alarms.clear();
        boolean hasMore = c.moveToFirst();
        while (hasMore) {
            final AlarmDto dto = AlarmDto.createDto(c);
            alarms.add(dto);
            hasMore = c.moveToNext();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final View mParentView;
        private final TextView mAlarmDesc;
        private final TextView mStartTime;
        private final TextView mIsAlarmEnabled;
        private final ImageView deleteAlarmIcon;
        private long alarmId;
        private String alarmDesc;

        public ViewHolder(final View v) {
            super(v);
            mParentView = v;
            mAlarmDesc = (TextView) v.findViewById(R.id.alarmListAlarmDescField);
            mStartTime = (TextView) v.findViewById(R.id.alarmListAlarmStartTimeField);
            mIsAlarmEnabled = (TextView) v.findViewById(R.id.alarmListAlarmEnabledField);
            deleteAlarmIcon = (ImageView) v.findViewById(R.id.alarmListDeleteAlarmIcon);

            deleteAlarmIcon.setOnClickListener(this);

            // http://www.android4devs.com/2015/01/recycler-view-handling-onitemtouch-for.html
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            if (R.id.alarmListDeleteAlarmIcon == v.getId()) {
                handleAlarmDelete();
                return;
            }
            activity.onRowClicked(alarmId);
        }

        private void handleAlarmDelete() {
            AlarmUtils.deleteAlarm(activity, alarmId, alarmDesc);
            notifyItemRemoved(getPosition());
            alarms.remove(getPosition());
//            notifyItemRangeChanged(getPosition(), getItemCount());
        }

        private void setAlarmId(final long alarmId) {
            this.alarmId = alarmId;
        }

        private void setAlarmDesc(final String alarmDesc) {
            this.alarmDesc = alarmDesc;
        }
    }
}
