package org.ap.android.alarm;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;

/**
 * Created by abhi on 14.02.15.
 */
public abstract class BaseDialogFragment extends DialogFragment /*implements DialogInterface
        .OnClickListener*/ {

    protected BaseAlarmActivity activity;
    protected boolean updateValue;

    void setActivity(final BaseAlarmActivity activity) {
        this.activity = activity;
        postSetActivity();
    }

    protected abstract void postSetActivity();
}
