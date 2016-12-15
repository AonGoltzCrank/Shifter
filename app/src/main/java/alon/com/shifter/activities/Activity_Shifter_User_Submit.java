package alon.com.shifter.activities;

import android.app.ProgressDialog;
import android.os.Bundle;

import alon.com.shifter.R;
import alon.com.shifter.base_classes.BaseActivity;
import alon.com.shifter.base_classes.FinishableTask;
import alon.com.shifter.views.ShiftSubmissionView;

import static alon.com.shifter.utils.FlowController.addGateOpenListener;
import static alon.com.shifter.utils.FlowController.getIsGateOpen;

public class Activity_Shifter_User_Submit extends BaseActivity {

    private ShiftSubmissionView mShiftSubmissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_submission);

        TAG = "Shifter_user_submission";
        getUtil(this);

        if (!getIsGateOpen(Fc_Keys.SPEC_SETTINGS_SET_PULLED) || !getIsGateOpen(Fc_Keys.SHIFT_SCHEDULE_SETTINGS_PULLED)) {
            final ProgressDialog mDialog = mUtil.generateStandbyDialog(this);
            FinishableTask mTask = new FinishableTask() {

                int count = 0;

                @Override
                public void onFinish() {
                    count++;
                    if (count == 2) {
                        setupUI();
                        mDialog.dismiss();
                    }
                }
            };
            addGateOpenListener(Fc_Keys.SPEC_SETTINGS_SET_PULLED, mTask);
            addGateOpenListener(Fc_Keys.SHIFT_SCHEDULE_SETTINGS_PULLED, mTask);
        } else
            setupUI();
    }

    @Override
    protected void setupUI() {
        mShiftSubmissions = (ShiftSubmissionView) findViewById(R.id.USR_SS_shiftSubmissionView);

    }
}
