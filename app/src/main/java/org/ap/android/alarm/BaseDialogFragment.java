package org.ap.android.alarm;

import android.app.DialogFragment;
import android.content.DialogInterface;

/**
 * Created by abhi on 14.02.15.
 */
public abstract class BaseDialogFragment extends DialogFragment implements DialogInterface
        .OnClickListener {

    protected AlarmActivity activity;
    protected boolean updateValue;

    void setActivity(final AlarmActivity activity) {
        this.activity = activity;
        postSetActivity();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case -1:
                updateValue = true;
                break;
            default:
                updateValue = false;
                break;
        }
    }

    protected abstract void postSetActivity();
}
