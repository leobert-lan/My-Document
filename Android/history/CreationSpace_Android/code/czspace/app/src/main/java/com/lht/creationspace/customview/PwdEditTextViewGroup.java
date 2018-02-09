package com.lht.creationspace.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.lht.creationspace.R;

/**
 * <p><b>Package</b> com.lht.vsocyy.customview
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> PwdEditTextViewGroup
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/2/15.
 */

public class PwdEditTextViewGroup extends FrameLayout {
    public PwdEditTextViewGroup(Context context) {
        this(context, null);

    }

    public PwdEditTextViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PwdEditTextViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private View contentView;

    private EditText etPassword;

    private CheckBox cbTogglePwdVisible;

    private String hint;

    private void init(AttributeSet attrs, int defStyleAttr) {
        contentView = inflate(getContext(), R.layout.view_pwd_etvg, this);

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.PwdEditTextViewGroup);

        hint = a.getString(R.styleable.PwdEditTextViewGroup_override_hint);

        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        etPassword = (EditText) contentView.findViewById(R.id.et_pwd);
        cbTogglePwdVisible = (CheckBox) contentView.findViewById(R.id.cb_viewpwd);

        cbTogglePwdVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        if (hint != null)
            etPassword.setHint(hint);
    }

    public void setHint(String hint) {
        this.hint = hint;
        etPassword.setHint(hint);
    }

    public EditText getPwdEditText() {
        return etPassword;
    }

    public void updatePwdEditTextDrawable(int resId) {
        Drawable drawable = getContext().getDrawable(resId);
        if (drawable == null) {
            return;
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        etPassword.setCompoundDrawables(drawable, null, null, null);
    }

    public void updateHint(int stringId) {
        etPassword.setHint(stringId);
    }

    public String getInput() {
        return etPassword.getText().toString();
    }
}
