package alon.com.shifter.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import alon.com.shifter.R;
import alon.com.shifter.base_classes.BaseActivity;
import alon.com.shifter.base_classes.FinishableTask;
import alon.com.shifter.base_classes.Linker;
import alon.com.shifter.shift_utils.Shift;
import alon.com.shifter.views.ShiftSubmissionView;

import static alon.com.shifter.utils.FlowController.addGateOpenListener;
import static alon.com.shifter.utils.FlowController.getIsGateOpen;

public class Activity_Select_Shifts_For_Week extends BaseActivity {


    private ShiftSubmissionView mShifts;

    private Shift mShift;

    private boolean mChangeText = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.act_transition_in, R.anim.act_transition_out);
        setContentView(R.layout.activity_first_shift_setup);

        TAG = "Shifter_FirstShiftSelection";
        getUtil(this);

        if (getIntent().getExtras() != null) {
            mShift = (Shift) getIntent().getExtras().getSerializable(Strings.FILE_SHIFT_OBJECT);
            mChangeText = getIntent().getExtras().getBoolean(Strings.KEY_SHOULD_CHANGE_BACK_BUTTON);
        }
        if (mShifts == null)
            mShift = (Shift) mUtil.readObject(this, Strings.FILE_SHIFT_OBJECT);

        boolean allGatesOpen = getIsGateOpen(Fc_Keys.SPEC_SETTINGS_TYPES_PULLED) && getIsGateOpen(Fc_Keys.SPEC_SETTING_RESTS_PULLED) && getIsGateOpen(Fc_Keys.SPEC_SETTINGS_EXPANDABLE_INFO_PULLED);
        if (allGatesOpen)
            setupUI();
        else {
            final ProgressDialog mDialog = mUtil.generateStandbyDialog(this);
            FinishableTask mTask = new FinishableTask() {
                int gateOpenCount = 0;

                @Override
                public void onFinish() {
                    gateOpenCount++;
                    if (gateOpenCount == 3) {
                        mDialog.dismiss();
                        setupUI();
                    }
                }
            };
            addGateOpenListener(Fc_Keys.SPEC_SETTINGS_TYPES_PULLED, mTask);
            addGateOpenListener(Fc_Keys.SPEC_SETTING_RESTS_PULLED, mTask);
            addGateOpenListener(Fc_Keys.SPEC_SETTINGS_EXPANDABLE_INFO_PULLED, mTask);
        }
    }

    @Override
    protected void setupUI() {
        final FinishableTask mAcceptedShiftSetting = new FinishableTask() {
            @Override
            public void onFinish() {
                AlertDialog mDialog;
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Activity_Select_Shifts_For_Week.this);
                mBuilder.setTitle(R.string.dialog_uploading_data)
                        .setMessage(getString(R.string.mgr_uploading_data_add_spec_settings))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.title_special_settings), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mUtil.writePref(Activity_Select_Shifts_For_Week.this, Pref_Keys.MGR_SEC_SCHEDULE_SET, true);
                                Bundle mExtras = new Bundle();
                                mExtras.putBoolean(Strings.KEY_SHOULD_CHANGE_BACK_BUTTON, false);
                                mUtil.changeScreen(Activity_Select_Shifts_For_Week.this, Activity_Special_Settings.class, mExtras);
                                Activity_Select_Shifts_For_Week.this.finish();
                            }
                        });
                mBuilder.setNegativeButton(getString(R.string.title_shift_hours), new DialogInterface.OnClickListener() { //Indentation is messed up for some reason.
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUtil.writePref(Activity_Select_Shifts_For_Week.this, Pref_Keys.MGR_SEC_SCHEDULE_SET, true);
                        Bundle mExtras = new Bundle();
                        mExtras.putBoolean(Strings.KEY_SHOULD_CHANGE_BACK_BUTTON, false);
                        mUtil.changeScreen(Activity_Select_Shifts_For_Week.this, Activity_Shift_Hour_Setting.class, mExtras);
                        Activity_Select_Shifts_For_Week.this.finish();
                    }
                });
                mDialog = mBuilder.create();
                mDialog.show();
                ((TextView) mDialog.findViewById(android.R.id.title)).setGravity(Gravity.RIGHT);

                new AsyncTaskWrapper(mDialog, mShifts.getSelectedShifts()) {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {

                            Linker linker = Linker.getLinker(Activity_Select_Shifts_For_Week.this, Linker_Keys.TYPE_UPLOAD_SHIFT_SCHEDULE);
                            linker.addParam(Linker_Keys.KEY_SHIFT_UPLOAD_SHIFT_OBJECT, mShift);
                            linker.addParam(Linker_Keys.KEY_SHIFT_UPLOAD_DIALOG, mDialog);
                            linker.execute();
                        } catch (Linker.ProductionLineException | Linker.InsufficientParametersException e)

                        {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();
            }
        };

        mShifts = (ShiftSubmissionView) findViewById(R.id.FSS_shiftSubmissionView);
        if (mShift != null)
            mShifts.constructViewFromShift(mShift);
        Button mDone = (Button) findViewById(R.id.FSS_done);
        if (mChangeText)
            mDone.setText(getString(R.string.back));
        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FinishableTask mDoneSelectingShifts = new FinishableTask() {
                    @Override
                    public void onFinish() {
                        if (!mChangeText) {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(Activity_Select_Shifts_For_Week.this);
                            mBuilder.setTitle(R.string.are_you_sure_eng).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mAcceptedShiftSetting.onFinish();
                                    dialog.dismiss();
                                }
                            }).setCancelable(false).setMessage(R.string.mgr_set_shifts);
                            AlertDialog mDialog = mBuilder.create();
                            mDialog.show();
                        } else
                            mUtil.changeScreen(Activity_Select_Shifts_For_Week.this, Activity_Shifter_Manager_Settings.class);
                    }
                };
                if (getIsGateOpen(Fc_Keys.LOGIN_OR_REGISTER_FINISHED))
                    mDoneSelectingShifts.onFinish();
                else {
                    addGateOpenListener(Fc_Keys.LOGIN_OR_REGISTER_FINISHED, mDoneSelectingShifts);
                }
            }
        });
    }

    private abstract class AsyncTaskWrapper extends AsyncTask<Void, Void, Void> {

        protected AlertDialog mDialog;
        protected Shift mShift;

        AsyncTaskWrapper(AlertDialog dialog, Shift shift) {
            mDialog = dialog;
            mShift = shift;
        }
    }

}
