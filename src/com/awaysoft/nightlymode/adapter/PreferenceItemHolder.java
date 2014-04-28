
package com.awaysoft.nightlymode.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.awaysoft.nightlymode.R;
import com.awaysoft.nightlymode.utils.Constant;
import com.awaysoft.nightlymode.utils.Preference;
import com.awaysoft.widget.Switch;

public class PreferenceItemHolder {
    public static final int ITEM_TYPE_NORMAL = 0X0000; // normal
    public static final int ITEM_TYPE_HEADER = 0X0001; // header
    public static final int ITEM_TYPE_SWITCHER = 0X0002; // switcher
    public static final int ITEM_TYPE_CHECKBOX = 0X0003; // check box
    public static final int ITEM_TYPE_COUNT = 0X0004; // count

    private final int[] sLayoutId = new int[] {
            R.layout.nightly_item_explain,
            R.layout.nightly_item_class,
            R.layout.nightly_item_switcher,
            R.layout.nightly_item_checkbox
    };

    private static String[] sModesId;

    private int type = ITEM_TYPE_NORMAL;
    private int labelResId;
    private int layoutResId;
    private int explainResId;
    private int targetItemId;

    private boolean enable = true;

    private Context mContext;
    private OnCheckedChangeListener mCheckBoxListener;

    private void newCheckBoxListener() {
        if (mCheckBoxListener == null) {
            mCheckBoxListener = new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    switch (targetItemId) {
                        case Constant.TAG_ID_GLOBAL:
                            Preference.sApplyAll = isChecked;
                            break;
                        case Constant.TAG_ID_ATUO_START:
                            Preference.sAutoStart = isChecked;
                            break;
                        case Constant.TAG_ID_NOTIFICATION:
                            Preference.sNotification = isChecked;
                            break;
                        case Constant.TAG_ID_FLOATWIDGET:
                            Preference.sFloatWidget = isChecked;
                            break;
                        default:
                            return;
                    }

                    PreferenceConfig.onPreferenceChanged(buttonView.getContext());
                }
            };
        }
    }

    public PreferenceItemHolder(int type, int labelResId, int explainResId, int targetItemId) {
        this.type = type;
        this.layoutResId = sLayoutId[type];
        this.labelResId = labelResId;
        this.explainResId = explainResId;
        this.targetItemId = targetItemId;
    }

    public View obtainView(Context context) {
        mContext = context;
        sModesId = mContext.getResources().getStringArray(R.array.night_mode);
        View view = null;
        try {
            view = LayoutInflater.from(context).inflate(layoutResId, null);
        } catch (Exception e) {
            view = LayoutInflater.from(context).inflate(R.layout.nightly_item_explain, null);
        }

        if (view.isEnabled()) {
            view.setBackgroundResource(R.drawable.list_item_drawable);
        }

        view.setTag(Integer.valueOf(targetItemId));

        Resources res = context.getResources();
        view.setPadding(res.getDimensionPixelSize(R.dimen.nightly_listitem_padding), 0,
                res.getDimensionPixelSize(R.dimen.nightly_listitem_padding), 0);

        if (type == ITEM_TYPE_CHECKBOX) {
            newCheckBoxListener();
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.nightly_checkbox);
            checkBox.setOnCheckedChangeListener(mCheckBoxListener);
        } else if (type == ITEM_TYPE_SWITCHER) {
            newCheckBoxListener();
            Switch switcher = (Switch) view.findViewById(R.id.nightly_switch);
            switcher.setOnCheckedChangeListener(mCheckBoxListener);
        }

        return view;
    }

    public void performClick(View view) {
        View check = null;
        if (type == ITEM_TYPE_CHECKBOX) {
            check = view.findViewById(R.id.nightly_checkbox);
        } else if (type == ITEM_TYPE_SWITCHER) {
            check = view.findViewById(R.id.nightly_switch);
        }

        if (check instanceof Checkable) {
            boolean checked = ((Checkable) check).isChecked();
            ((Checkable) check).setChecked(!checked);
        }
    }

    public boolean isEnable() {
        return enable;
    }

    public int getType() {
        return type;
    }

    public int getTargetId() {
        return targetItemId;
    }

    public boolean isChecked(View view) {
        if (type == ITEM_TYPE_SWITCHER || type == ITEM_TYPE_CHECKBOX) {
            if (type == ITEM_TYPE_CHECKBOX) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.nightly_checkbox);
                return checkBox.isChecked();
            } else {
                Switch switcher = (Switch) view.findViewById(R.id.nightly_switch);
                return switcher.isChecked();
            }
        }

        return false;
    }

    public void bindView(View view) {
        switch (type) {
            case ITEM_TYPE_HEADER: {
                ((TextView) view).setText(view.getResources().getString(labelResId));
                break;
            }

            default: {
                ViewGroup vp = (ViewGroup) view;
                ((TextView) vp.findViewById(R.id.item_label)).setText(view.getResources()
                        .getString(labelResId));

                TextView exp = (TextView) vp.findViewById(R.id.item_explain);
                String tips = getTips();
                if (TextUtils.isEmpty(tips)) {
                    exp.setVisibility(View.GONE);
                } else {
                    exp.setVisibility(View.VISIBLE);
                    exp.setText(tips);
                }
                break;
            }
        }

        if (type == ITEM_TYPE_CHECKBOX) {
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.nightly_checkbox);
            if (targetItemId == Constant.TAG_ID_GLOBAL) {
                checkBox.setChecked(Preference.sApplyAll);
            }
        } else if (type == ITEM_TYPE_SWITCHER) {
            Switch switcher = (Switch) view.findViewById(R.id.nightly_switch);
            boolean status = false;
            switch (targetItemId) {
                case Constant.TAG_ID_ATUO_START:
                    status = Preference.sAutoStart;
                    break;
                case Constant.TAG_ID_NOTIFICATION:
                    status = Preference.sNotification;
                    break;
                case Constant.TAG_ID_FLOATWIDGET:
                    status = Preference.sFloatWidget;
                    break;
            }
            switcher.setChecked(status);
        }
    }

    private String getTips() {
        StringBuilder sb = new StringBuilder();
        if (explainResId > 0) {
            sb.append(mContext.getString(explainResId));
        }

        switch (targetItemId) {
            case Constant.TAG_ID_MODE:
                return sModesId[Preference.sNightlyMode >> Constant.MODE_MASK];
            case Constant.TAG_ID_ALPHA: {
                sb.append(", ");
                sb.append(mContext.getString(R.string.current_string));
                sb.append("=");
                sb.append(Preference.sMatteAlpha);
                break;
            }
            case Constant.TAG_ID_COLOR: {
                sb.append(", ");
                sb.append(mContext.getString(R.string.current_string));
                sb.append("=#");
                sb.append(Integer.toHexString(Preference.sMatteColor));
                break;
            }
            case Constant.TAG_ID_AUTO_TIME: {
                sb.append(", ");
                sb.append(mContext.getString(R.string.current_string));
                sb.append(": ");
                sb.append(Preference.sTimeBuckets.replaceAll("\\|", " ~ "));
                break;
            }
        }

        return sb.toString();
    }

    public PreferenceItemHolder setEnable(View view, boolean enable) {
        this.enable = enable;
        if (view instanceof ViewGroup) {
            int count = ((ViewGroup) view).getChildCount();
            for (int i = 0; i < count; ++i) {
                setEnable(((ViewGroup) view).getChildAt(i), enable);
            }
            view.setEnabled(enable);
        } else if (view != null) {
            view.setEnabled(enable);
        }

        return this;
    }
}