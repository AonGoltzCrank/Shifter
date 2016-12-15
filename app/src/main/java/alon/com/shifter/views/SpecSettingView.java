package alon.com.shifter.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import alon.com.shifter.R;
import alon.com.shifter.base_classes.Consts;

public class SpecSettingView extends LinearLayout {

    private static final String HEADER_TAG = "Header_Check_Box";
    private static final String CHILD_TAG = "Child_Tag";
    private final ArrayList<String> childrenSelected = new ArrayList<>();
    private String header;
    private String children;
    private String restriction;
    private int restValue;
    private Context mCon;
    private Toast mToast;
    private ArrayList<CheckBox> mBoxes = new ArrayList<>();

    @SuppressLint("ShowToast")
    public SpecSettingView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        mCon = context;
        mToast = Toast.makeText(context, R.string.spec_setting_limit_amount, Toast.LENGTH_SHORT);
    }

    public void setChildren(String children) {
        this.children = children;
    }

    public void setRestrictions(String rest, int value) {
        restriction = rest;
        restValue = value;
    }

    public void genViews() {
        if (header != null && children != null) {
            LayoutParams mHeaderParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LayoutParams mChildParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LayoutParams mDefaultParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            LinearLayout mHeader = new LinearLayout(mCon);
            mHeader.setOrientation(HORIZONTAL);
            mHeader.setWeightSum(1);

            final LinearLayout mContainer = new LinearLayout(mCon);
            mContainer.setOrientation(VERTICAL);
            mContainer.setVisibility(View.GONE);

            final CheckBox mActiveCat = new CheckBox(mCon);
            TextView mTitle = new TextView(mCon);
            mTitle.setText(header);
            mTitle.setTextAppearance(mCon, android.R.style.TextAppearance_DeviceDefault_Medium);
            mTitle.setTypeface(Typeface.DEFAULT_BOLD);
            mTitle.setGravity(Gravity.CENTER);
            mTitle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActiveCat.setChecked(!mActiveCat.isChecked());
                }
            });

            mHeaderParams.weight = 0.925f;

            mHeader.addView(mTitle, mHeaderParams);

            mActiveCat.setText("");
            mActiveCat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mContainer.setVisibility(View.VISIBLE);
                        for (CheckBox cb : mBoxes) {
                            if (cb.isChecked()) {
                                for (int i = 0; i < mContainer.getChildCount(); i++) {
                                    LinearLayout mLayout = (LinearLayout) mContainer.getChildAt(i);
                                    if (mLayout.getChildAt(0) instanceof CheckBox) {
                                        if (((CheckBox) mLayout.getChildAt(0)).isChecked())
                                            if (!childrenSelected.contains(((TextView) mLayout.getChildAt(1)).getText().toString()))
                                                childrenSelected.add(((TextView) mLayout.getChildAt(1)).getText().toString());
                                    } else if (mLayout.getChildAt(1) instanceof CheckBox) {
                                        if (((CheckBox) mLayout.getChildAt(1)).isChecked())
                                            if (!childrenSelected.contains(((TextView) mLayout.getChildAt(0)).getText().toString()))
                                                childrenSelected.add(((TextView) mLayout.getChildAt(0)).getText().toString());

                                    }
                                }
                            }
                        }
                    } else {
                        mContainer.setVisibility(View.GONE);
                        childrenSelected.clear();
                    }
                }
            });

            mHeaderParams.weight = 1 - mHeaderParams.weight;

            mHeader.addView(mActiveCat, mHeaderParams);
            mHeader.setTag(HEADER_TAG);

            addView(mHeader, mDefaultParams);

            View mSep = new View(mCon);
            float scale = mCon.getResources().getDisplayMetrics().density;
            float height = (2 * scale + 0.5f);
            LayoutParams mSepParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) height);

            mSep.setBackgroundColor(Color.LTGRAY);

            addView(mSep, mSepParams);

            String[] childrenTemp = this.children.split("~");
            final String[] childrenSet = new String[childrenTemp.length - 1];
            System.arraycopy(childrenTemp, 1, childrenSet, 0, childrenTemp.length - 1);
            for (String child : childrenSet) {
                LinearLayout mAddedLayout = new LinearLayout(mCon);
                mAddedLayout.setOrientation(HORIZONTAL);
                mAddedLayout.setWeightSum(1);


                final CheckBox mBox = new CheckBox(mCon);
                mBox.setText("");
                final TextView mChildName = new TextView(mCon);

                child = "\t" + child;
                mChildName.setText(child);
                mChildName.setTextAppearance(mCon, android.R.style.TextAppearance_DeviceDefault_Medium);
                mChildName.setGravity(Gravity.RIGHT);
                mChildParams.weight = 0.925f;

                mChildName.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBox.setChecked(!mBox.isChecked());
                    }
                });

                mAddedLayout.addView(mChildName, mChildParams);
                mChildParams.weight = 1 - mChildParams.weight;

                mBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        switch (restriction) {
                            case Consts.Strings.MGR_SEC_SPEC_SETTING_REST_LIMIT_AMOUNT:
                                if (getCheckedAmount() <= restValue)
                                    if (isChecked) {
                                        childrenSelected.add(mChildName.getText().toString());
                                    } else
                                        childrenSelected.remove(mChildName.getText().toString());
                                else {
                                    mBox.setChecked(false);
                                    if (!mToast.getView().isShown())
                                        ((Activity) mCon).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mToast.show();
                                            }
                                        });
                                }
                                break;
                            case Consts.Strings.MGR_SEC_SPEC_SETTING_REST_RADIO_BTN_BEHAVIOUR:
                                if (isChecked)
                                    for (CheckBox cb : mBoxes) {
                                        if (cb != mBox)
                                            cb.setChecked(false);
                                        else {
                                            childrenSelected.clear();
                                            childrenSelected.add(mChildName.getText().toString());
                                        }
                                    }

                                break;
                            default:
                                if (isChecked) {
                                    childrenSelected.add(mChildName.getText().toString());
                                } else
                                    childrenSelected.remove(mChildName.getText().toString());
                                break;
                        }
                    }
                });

                mBoxes.add(mBox);

                mAddedLayout.addView(mBox, mChildParams);
                mContainer.addView(mAddedLayout, mDefaultParams);
            }
            mContainer.setTag(CHILD_TAG);
            addView(mContainer, mDefaultParams);
        } else
            throw new RuntimeException("Header or children are not setGate.");
    }

    public boolean isSelected() {
        return childrenSelected.isEmpty();
    }

    public String getChildrenSelected() {
        if (!childrenSelected.isEmpty()) {
            String children = "";
            for (String str : childrenSelected)
                children += str.trim() + "~";
            return children.substring(0, children.lastIndexOf('~'));
        } else return "";
    }

    private int getCheckedAmount() {
        int count = 0;
        for (CheckBox mBox : mBoxes)
            if (mBox.isChecked())
                count++;
        return count;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setHeaderChecked(boolean flag) {
        LinearLayout mHeaderLayout = (LinearLayout) findViewWithTag(HEADER_TAG);
        for (int i = 0; i < mHeaderLayout.getChildCount(); i++) {
            View mChild = mHeaderLayout.getChildAt(i);
            if (mChild instanceof CheckBox) {
                ((CheckBox) mChild).setChecked(flag);
                return;
            }
        }
    }

    public void setChildChecked(String str, boolean flag) {
        LinearLayout mCorrectLayout = null;

        LinearLayout mChildLayout = (LinearLayout) findViewWithTag(CHILD_TAG);
        for (int i = 0; i < mChildLayout.getChildCount(); i++) {
            LinearLayout mGrandChild = (LinearLayout) mChildLayout.getChildAt(i);
            for (int j = 0; j < mGrandChild.getChildCount(); j++) {
                View mView = mGrandChild.getChildAt(j);
                if (mView instanceof TextView)
                    if (((TextView) mView).getText().toString().trim().equals(str)) {
                        mCorrectLayout = mGrandChild;
                        break;
                    }
            }
            if (mCorrectLayout != null)
                break;
        }

        if (mCorrectLayout == null) {
            Log.d("Shifter_SSView", "setChildChecked: mCorrectLayout is null, exiting.");
            return;
        }
        for (int i = 0; i < mCorrectLayout.getChildCount(); i++) {
            View mView = mCorrectLayout.getChildAt(i);
            if (mView instanceof CheckBox) {
                ((CheckBox) mView).setChecked(flag);
                return;
            }
        }
    }

    public boolean isActive() {
        LinearLayout mHeader = (LinearLayout) findViewWithTag(HEADER_TAG);
        for (int i = 0; i < mHeader.getChildCount(); i++) {
            View mView = mHeader.getChildAt(i);
            if (mView instanceof CheckBox)
                return ((CheckBox) mView).isChecked();
        }
        return false;
    }

    public boolean isAnyChildChecked() {
        LinearLayout mChildLayout = (LinearLayout) findViewWithTag(CHILD_TAG);
        for (int i = 0; i < mChildLayout.getChildCount(); i++) {
            LinearLayout mGrandChild = (LinearLayout) mChildLayout.getChildAt(i);
            for (int j = 0; j < mGrandChild.getChildCount(); j++) {
                View mView = mGrandChild.getChildAt(j);
                if (mView instanceof CheckBox)
                    if (((CheckBox) mView).isChecked())
                        return true;
            }
        }
        return false;
    }
}
