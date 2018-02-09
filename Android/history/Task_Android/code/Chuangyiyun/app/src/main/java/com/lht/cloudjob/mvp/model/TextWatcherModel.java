package com.lht.cloudjob.mvp.model;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> TextWatcherModel
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/11.
 */
public class TextWatcherModel implements IWatcher {

    private final TextWatcherModelCallback modelCallback;

    public TextWatcherModel(TextWatcherModelCallback modelCallback) {
        this.modelCallback = modelCallback;
    }

    @Override
    public void doWatcher(EditText editText, int maxLength) {
        editText.addTextChangedListener(new WatcherImpl(editText, maxLength));
    }


    public interface TextWatcherModelCallback {
        void onOverLength(int edittextId, int maxLength);

        void onChanged(int edittextId,int currentCount,int remains);

        class EmptyCallbackImpl implements TextWatcherModelCallback {

            @Override
            public void onOverLength(int edittextId, int maxLength) {

            }

            @Override
            public void onChanged(int edittextId, int currentCount, int remains) {

            }
        }
    }

    private final class WatcherImpl implements TextWatcher {

        private final EditText editText;

        private final int maxLength;

        WatcherImpl(EditText editText, int maxLength) {
            this.editText = editText;
            this.maxLength = maxLength;
        }

        private CharSequence temp;
        private int editStart;
        private int editEnd;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = editText.getSelectionStart();
            editEnd = editText.getSelectionEnd();
            if (temp.length() > maxLength) {
                s.delete(editStart - 1, editEnd);
                int tempSelection = editStart;
                editText.setText(s);
                editText.setSelection(tempSelection);
                modelCallback.onOverLength(editText.getId(), maxLength);
            } else {
                modelCallback.onChanged(editText.getId(), temp.length(), maxLength - temp.length());
            }
        }
    }
}

interface IWatcher {
    void doWatcher(EditText editText, int maxLength);
}

