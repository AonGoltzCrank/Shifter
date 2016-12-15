package alon.com.shifter.activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import alon.com.shifter.R;
import alon.com.shifter.base_classes.BaseActivity;
import alon.com.shifter.base_classes.Linker;
import alon.com.shifter.shift_utils.SpecSettings;
import alon.com.shifter.views.SpecSettingView;

public class Activity_Special_Settings extends BaseActivity {

    private LinearLayout mLayout;
    private Button mDone;

    private boolean mChangeText = false;

    private SpecSettings mSpecSettingsObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.act_transition_in, R.anim.act_transition_out);
        setContentView(R.layout.activity_special_settings);

        getUtil(this);
        TAG = "Shifter_SpecialSettings";

        if (getIntent().getExtras() != null) {
            mSpecSettingsObj = (SpecSettings) getIntent().getExtras().getSerializable(Strings.FILE_SPEC_SETTINGS_OBJECT);
            mChangeText = getIntent().getExtras().getBoolean(Strings.KEY_SHOULD_CHANGE_BACK_BUTTON);
        }
        if (mSpecSettingsObj == null)
            mSpecSettingsObj = (SpecSettings) mUtil.readObject(this, Strings.FILE_SPEC_SETTINGS_OBJECT);

        setupUI();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void setupUI() {
        String mCompleteSpecSettingInfo = mUtil.readPref(this, Pref_Keys.MGR_SEC_COMPLETE_SPEC_SETTINGS, Strings.NULL).toString();
        String[] mSpecSettings = mCompleteSpecSettingInfo.split(";");

        mLayout = (LinearLayout) findViewById(R.id.SS_options);
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        String rests = mUtil.readPref(this, Pref_Keys.MGR_SEC_SPEC_SETTINGS_RESTRICTIONS, Strings.NULL).toString();
        String[] restrictions = rests.split(";");
        for (int i = 1; i < mSpecSettings.length; i++) {
            String setting = mSpecSettings[0].split("~")[i - 1];
            SpecSettingView mView = constSettingView(setting, mSpecSettings[i], restrictions[i - 1].split("~")[1]);
            if (mSpecSettingsObj != null)
                if (mSpecSettingsObj.containsHeader(setting)) {
                    mView.setHeaderChecked(true);
                    for (String str : mSpecSettingsObj.getChildrenArrayList(setting))
                        mView.setChildChecked(str, true);
                }
            mLayout.addView(mView, mParams);
        }
        mDone = (Button) findViewById(R.id.SS_done);
        if (mChangeText)
            mDone.setText(getString(R.string.back));
        mDone.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mDone.getId()) {
            ArrayList<String> mItems = new ArrayList<>();
            for (int i = 0; i < mLayout.getChildCount(); i++) {
                String item = "";
                View mView = mLayout.getChildAt(i);
                if (mView instanceof SpecSettingView) {
                    if (((SpecSettingView) mView).isActive())
                        if (((SpecSettingView) mView).isAnyChildChecked()) {
                            item += ((SpecSettingView) mLayout.getChildAt(i)).getHeader() + "=";
                            item += ((SpecSettingView) mLayout.getChildAt(i)).getChildrenSelected() + "~";

                        }
                }
                int childAmount = item.lastIndexOf("~");
                if (!item.isEmpty())
                    mItems.add(item.substring(0, childAmount == -1 ? item.lastIndexOf("=") : childAmount));
            }
            SpecSettings mSettings = SpecSettings.generateFromList(mItems);
            try {
                Linker linker = Linker.getLinker(Activity_Special_Settings.this, Linker_Keys.TYPE_UPLOAD_SPEC_SETTINGS);
                linker.addParam(Linker_Keys.KEY_SPEC_SETTINGS, mSettings);
                linker.execute();
            } catch (Linker.ProductionLineException | Linker.InsufficientParametersException e) {
                e.printStackTrace();
            }
            mUtil.writeObject(this, Strings.FILE_SPEC_SETTINGS_OBJECT, mSettings);
            if (mChangeText)
                mUtil.changeScreen(this, Activity_Shifter_Manager_Settings.class);
            else
                mUtil.changeScreen(this, Activity_Shift_Hour_Setting.class);
        }
    }

    private SpecSettingView constSettingView(String header, String children, String rest) {
        SpecSettingView mSpecView = new SpecSettingView(this);
        mSpecView.setHeader(header);
        mSpecView.setChildren(children);
        mSpecView.setRestrictions(rest.split("=")[0], Integer.parseInt(rest.split("=")[1]));
        mSpecView.genViews();
        return mSpecView;
    }

}
