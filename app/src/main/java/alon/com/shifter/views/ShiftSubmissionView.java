package alon.com.shifter.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import alon.com.shifter.R;
import alon.com.shifter.base_classes.Consts;
import alon.com.shifter.base_classes.FinishableTaskWithParams;
import alon.com.shifter.dialog_fragments.ShiftCommentDialogFragment;
import alon.com.shifter.shift_utils.Shift;
import alon.com.shifter.shift_utils.ShiftInfo;
import alon.com.shifter.utils.Util;

public class ShiftSubmissionView extends RelativeLayout implements View.OnLongClickListener {

    private final int[] IDS = {R.id.V_SS_SLV_morn, R.id.V_SS_SLV_afr_non, R.id.V_SS_SLV_evening, R.id.V_SS_SLV_night};

    private ShiftLineView[] mViews;

    private boolean isManager;

    public ShiftSubmissionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ShiftSubmissionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context mCon, AttributeSet attrs) {
        TypedArray arr = mCon.obtainStyledAttributes(attrs, R.styleable.ShiftSubmissionView, 0, 0);
        isManager = arr.getBoolean(R.styleable.ShiftSubmissionView_isManager, false);
        arr.recycle();
        inflate(mCon, R.layout.view_shift_submission, this);
        mViews = new ShiftLineView[4];
        for (int i = 0; i < 4; i++)
            (mViews[i] = (ShiftLineView) findViewById(IDS[i])).setTag(i);
        if (!isInEditMode() && !isManager) {
            Util mUtil = Util.getInstance(mCon);
            String shifts = mUtil.readPref(mCon, Consts.Pref_Keys.USR_SHIFT_SCHEDULE, Consts.Strings.NULL).toString();
            try {
                JSONObject mJson = new JSONObject(shifts);
                for (int i = 0; i < 7; i++) {
                    String dayName = mUtil.getDayString(mCon, i + 1);
                    try {
                        JSONObject mObj = (JSONObject) mJson.get(dayName);
                        for (int j = 0; j < 4; j++) {
                            String dayShiftName = mUtil.getShiftTitle(mCon, j);
                            boolean canSubmit = !(mObj.get(dayShiftName).equals("-1"));
                            mViews[j].setEnabled(i, canSubmit);
                            mViews[j].setLongClickListener(i, this);
                        }
                    } catch (ClassCastException ex) {
                        for (int j = 0; j < 4; j++)
                            mViews[j].setEnabled(i, false);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onLongClick(final View v) {
        if (!isManager) {
            ShiftCommentDialogFragment mFrag = new ShiftCommentDialogFragment();
            mFrag.setTask(new FinishableTaskWithParams() {
                @Override
                public void onFinish() {
                    String comment = (String) getParams().get(Consts.Param_Keys.KEY_SHIFT_COMMENT);
                    if (comment != null && !comment.isEmpty()) {
                        String tag = (String) v.getTag();
                        for (int i = 0; i < 7; i++) {

                        }

                    }
                }
            });
            return true;
        } else
            return false;
    }

    public Shift getSelectedShifts() {
        Shift mShift = new Shift();
        for (int i = 0; i < 7; i++)
            for (int j = 0; j < 4; j++)
                mShift.getInfoComplete()[i].set(j, mViews[j].getShifts()[i]);
        return mShift;
    }

    public void constructViewFromShift(Shift shift) {
        ShiftInfo[] mInfo = shift.getInfoComplete();
        for (int i = 0; i < 7; i++)
            for (int j = 0; j < 4; j++)
                mViews[j].setToggleState(i, mInfo[i].getForBool(j));
    }
}
