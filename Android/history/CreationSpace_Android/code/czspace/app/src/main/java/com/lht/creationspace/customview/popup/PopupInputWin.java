package com.lht.creationspace.customview.popup;

import android.app.Service;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.R;
import com.lht.creationspace.base.model.TextWatcherModel;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.creationspace.util.toast.ToastUtils;

/**
 * <p><b>Package</b> com.lht.creationspace.customview.popup
 * <p><b>Project</b> czspace
 * <p><b>Classname</b> PupupInputWin
 * <p><b>Description</b>: 输入浮层弹窗
 * <p>Created by leobert on 2017/2/28.
 */

public class PopupInputWin extends CustomPopupWindow implements View.OnClickListener {

    private TextView viewCancel;
    private TextView tvTitle;
    private TextView viewSubmit;
    private EditText etInput;

    private final AbsInputCallback callback;

    public PopupInputWin(IPopupHolder iPopupHolder, AbsInputCallback callback) {
        super(iPopupHolder);
        this.callback = callback;
    }

    private View contentView;

    private RelativeLayout animContent;

    @Override
    protected void init() {
        super.doDefaultSetting();
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setFocusable(true);
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        contentView = inflater.inflate(R.layout.popup_input, null);
        animContent = (RelativeLayout) contentView.findViewById(R.id.rl_anim_content);

        viewCancel = (TextView) contentView.findViewById(R.id.popup_input_cancel);
        viewSubmit = (TextView) contentView.findViewById(R.id.popup_input_submit);
        tvTitle = (TextView) contentView.findViewById(R.id.popup_input_title);
        etInput = (EditText) contentView.findViewById(R.id.popup_input_et);

        setClickEvent();
        setContentView(contentView);
        ColorDrawable cd = new ColorDrawable(0x000000);
        this.setBackgroundDrawable(cd);
    }

    @Override
    public int getGravity() {
        return Gravity.BOTTOM;
    }

    private void setClickEvent() {
        viewSubmit.setOnClickListener(this);
        viewCancel.setOnClickListener(this);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeOnInterrupt();
            }
        });
    }

    public void closeOnInterrupt() {
        closeIme();
        callback.onInputInterrupt();
        dismiss();
    }

    public void overrideCancelText(String text) {
        if (text.isEmpty())
            return;
        viewCancel.setText(text);
    }

    public void overrideSubmitText(String text) {
        if (StringUtil.isEmpty(text))
            return;
        viewSubmit.setText(text);
    }

    public void overrideTitle(String text) {
        if (StringUtil.isEmpty(text))
            return;
        tvTitle.setText(text);
    }

    public void autoFillInput(String input) {
        if (StringUtil.isEmpty(input))
            return;
        etInput.setText(input);
        etInput.setSelection(input.length());
    }

    public void overrideHint(String hint) {
        if (StringUtil.isEmpty(hint))
            return;
        etInput.setText(null);
        etInput.setHint(hint);
    }

//    @Override
//    protected int getMyAnim() {
//        return R.style.CustomIn_SlideOut;
//    }

    public void setInputMaxLength(int maxLength) {
        if (maxLength > 0) {
            TextWatcherModel model = new TextWatcherModel(new TextWatcherModel.TextWatcherModelCallback() {
                @Override
                public void onOverLength(int edittextId, int maxLength) {
                    ToastUtils.show(MainApplication.getOurInstance(), R.string.input_overlength, ToastUtils.Duration.s);
//                    Toast.makeText(MainApplication.getOurInstance(),
//                            R.string.input_overlength
//                            , Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onChanged(int edittextId, int currentCount, int remains) {

                }
            });
            model.doWatcher(etInput, maxLength);
        }
    }

    @Override
    protected int getMyWidth() {
        return mActivity.getWindowManager().getDefaultDisplay().getWidth();
    }

    @Override
    protected int getMyHeight() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected void onShow() {
        super.onShow();
//        animContent.startAnimation(dropdownAnimation(300));
        openIme();
    }

    private void openIme() {
        contentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getiPopupHolder().getHolderActivity()
                        .getSystemService(Service.INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                imm.showSoftInput(etInput,InputMethodManager.SHOW_IMPLICIT);
            }
        }, 0);
    }

    private void closeIme() {
//        contentView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
                InputMethodManager imm = (InputMethodManager) getiPopupHolder().getHolderActivity()
                        .getSystemService(Service.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etInput.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
//            }
//        }, 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.popup_input_cancel:
                closeIme();
                if (callback != null)
                    callback.onInputCancel();
                dismiss();
                break;
            case R.id.popup_input_submit:
                closeIme();
                if (callback != null)
                    callback.onInputSubmit(etInput.getText().toString());
                dismiss();
                break;
            default:
                break;
        }
    }


    public abstract static class AbsInputCallback
            implements OnInputCancelListener,
            OnInputInterruptListener,
            OnInputSubmitListener {

    }

    /**
     * 输入取消回调
     */
    interface OnInputCancelListener {
        void onInputCancel();
    }

    /**
     * 输入被打断回调，例如暂时隐藏
     */
    interface OnInputInterruptListener {
        void onInputInterrupt();
    }

    /**
     * 输入完成并提交
     */
    interface OnInputSubmitListener {
        void onInputSubmit(String textInput);
    }
}
