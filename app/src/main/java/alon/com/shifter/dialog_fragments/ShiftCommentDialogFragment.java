package alon.com.shifter.dialog_fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import alon.com.shifter.R;
import alon.com.shifter.base_classes.Consts;
import alon.com.shifter.base_classes.FinishableTaskWithParams;

/**
 * Created by Alon on 11/22/2016.
 */

public class ShiftCommentDialogFragment extends DialogFragment {


    private EditText mMsg;
    private Button mDelete;
    private Button mSubmit;

    private FinishableTaskWithParams mTaskParam;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.dialog_fragment_shift_comment, container);

        mMsg = (EditText) mView.findViewById(R.id.DF_SC_comment);

        mDelete = (Button) mView.findViewById(R.id.DF_SC_cancel);
        mSubmit = (Button) mView.findViewById(R.id.DF_SC_submit);

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTaskParam.addParam(Consts.Param_Keys.KEY_SHIFT_COMMENT, mMsg.getText());
                mTaskParam.onFinish();
            }
        });

        mMsg.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    mSubmit.callOnClick();
                return true;
            }
        });
        return mView;
    }

    public void setTask(FinishableTaskWithParams mTaskParam) {
        this.mTaskParam = mTaskParam;
    }
}
