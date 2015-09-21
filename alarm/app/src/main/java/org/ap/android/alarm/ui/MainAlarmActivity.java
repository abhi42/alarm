package org.ap.android.alarm.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import org.ap.android.alarm.R;
import org.ap.android.alarm.common.AlarmUtils;
import org.ap.android.alarm.db.AlarmDbHelper;


public class MainAlarmActivity extends ActionBarActivity {

    private static final String TAG = MainAlarmActivity.class.getName();
    private RecyclerView mRecyclerView;
    private AlarmListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alarm);

        initialiseList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseList();
    }

    private void initialiseList() {
        mRecyclerView = (RecyclerView) findViewById(R.id.main_alarm_activity_recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final AlarmDbHelper dbHelper = new AlarmDbHelper(this);
        mAdapter = new AlarmListAdapter(this, dbHelper);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // this is being done in the list adapter
//        addListItemClickHandling();
    }

    public void onClickAddAlarm(final View view) {
        final Intent intent = new Intent(this, AddSingleAlarmActivity.class);
        startActivity(intent);
    }

    private void addListItemClickHandling() {

        final GestureDetector.SimpleOnGestureListener simpleGestureListener = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
            // by default all other gestures return false in the base class
        };

        final GestureDetector gestureDetector = new GestureDetector(this, simpleGestureListener);

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                // this is called before the touch event is passed to the RecyclerView for handling
                // we only want to handle a touch gesture, not other gestures like swiping

                if (gestureDetector.onTouchEvent(e)) {
                    // if the gesture is a tap gesture, then intercept
                    final View viewClicked = rv.findChildViewUnder(e.getX(), e.getY());
                    if (viewClicked != null) {
                        final int position = mRecyclerView.getChildPosition(viewClicked);
                        handleListItemClicked(position);
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }
        });
    }

    private void handleListItemClicked(final int position) {
        onRowClicked(position);
    }

    void onRowClicked(final long alarmId) {
        final Intent intent = new Intent(this, EditSingleAlarmActivity.class);
        intent.putExtra(AlarmUtils.ALARM_ID_BEING_PASSED, alarmId);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent alarmDateTimeDto in AndroidManifest.xml.
        final int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                final Intent intent = new Intent(this, AlarmSettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
