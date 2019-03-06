package com.whz.customkeyboard.view;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.whz.customkeyboard.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by kevin on 17/9/20.
 */

public class KeyboardUtil {

    private Activity mActivity;
    //是否是随机数字键盘
    private boolean isRandom;
    private EditText mEditText;
    private Keyboard mKeyboardNumber;
    private CustomKeyboardView mKeyboardView;

    public KeyboardUtil(Activity activity) {
        this(activity, false);
    }

    public KeyboardUtil(Activity activity, boolean isRandom) {
        this.mActivity = activity;
        this.isRandom = isRandom;

        initSetting();
    }

    /**
     * 初始化设置
     */
    private void initSetting() {
        if (mKeyboardNumber == null) {
            mKeyboardNumber = new Keyboard(mActivity, R.xml.keyboard_number);
        }

        if (mKeyboardView == null) {
            mKeyboardView = mActivity.findViewById(R.id.keyboard_view);
        }

        if (isRandom) {
            randomKeyboard();
        }
    }

    /**
     * 需要绑定的EditText
     *
     * @param editText
     */
    public void attachTo(EditText editText) {
        this.mEditText = editText;
        hideSoftKeyboard(mActivity.getApplicationContext(), mEditText);
        showSoftKeyboard();
    }

    /**
     * 隐藏系统键盘
     *
     * @param aCtx
     * @param editText
     */
    private void hideSoftKeyboard(Context aCtx, EditText editText) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            try {
                Method method = EditText.class.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(editText, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            editText.setInputType(InputType.TYPE_NULL);
        }

        //若键盘已显示，取消显示
        InputMethodManager imm = (InputMethodManager) aCtx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * 显示软键盘
     */
    private void showSoftKeyboard() {
        mKeyboardView.setKeyboard(mKeyboardNumber);
        mKeyboardView.setEnabled(true);
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setOnKeyboardActionListener(getKeyboardActionListener());
    }


    /**
     * 获取按键监听
     *
     * @return KeyboardView.OnKeyboardActionListener
     */
    private KeyboardView.OnKeyboardActionListener getKeyboardActionListener() {
        KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {

            @Override
            public void onKey(int i, int[] ints) {
                Editable editable = mEditText.getText();
                int start = mEditText.getSelectionStart();

                //删除键
                if (i == Keyboard.KEYCODE_DELETE) {
                    if (null != editable && editable.length() > 0) {
                        if (start > 0) {
                            editable.delete(start - 1, start);
                        }
                    }
                }

                //取消键
                else if (i == Keyboard.KEYCODE_CANCEL) {
                    hideKeyboard();
                    if (null != mListener) {
                        mListener.onCancel(true);
                    }
                }

                //回车键
                else if (i == Keyboard.KEYCODE_DONE) {
                    hideKeyboard();
                    if (null != mListener) {
                        mListener.onEnter(true);
                    }
                } else {
                    editable.insert(start, Character.toString((char) i));
                }
            }

            @Override
            public void onPress(int i) {

            }

            @Override
            public void onRelease(int i) {

            }

            @Override
            public void onText(CharSequence charSequence) {

            }

            @Override
            public void swipeLeft() {

            }

            @Override
            public void swipeRight() {

            }

            @Override
            public void swipeDown() {

            }

            @Override
            public void swipeUp() {

            }
        };
        return listener;
    }

    /**
     * 随机排列数字键
     */
    private void randomKeyboard() {
        //获取键盘所有按键
        List<Keyboard.Key> keyLists = mKeyboardNumber.getKeys();
        //存储0～9数字
        List<Keyboard.Key> numberkeyLists = new ArrayList<>();

        for (int i = 0; i < keyLists.size(); i++) {
            if (null != keyLists.get(i).label && isNumber(keyLists.get(i).label.toString())) {
                numberkeyLists.add(keyLists.get(i));
            }
        }
        //临时存储
        LinkedList<KeyModel> tempLists = new LinkedList<>();
        for (int i = 0; i < numberkeyLists.size(); i++) {
            tempLists.add(new KeyModel(48 + i, String.valueOf(i)));
        }

        //结果集
        List<KeyModel> resultLists = new ArrayList<>();

        //取数
        Random random = new Random();
        for (int i = 0; i < numberkeyLists.size(); i++) {
            int num = random.nextInt(numberkeyLists.size() - i);
            resultLists.add(new KeyModel(tempLists.get(num).getCode(), tempLists.get(num).getLable()));
            tempLists.remove(num);
        }

        for (int i = 0; i < numberkeyLists.size(); i++) {
            numberkeyLists.get(i).label = resultLists.get(i).getLable();
            numberkeyLists.get(i).codes[0] = resultLists.get(i).getCode();
        }
    }

    /**
     * 判断是否是数字
     *
     * @param number
     * @return boolean
     */
    private boolean isNumber(String number) {
        String numbers = "0123456789";
        return numbers.contains(number);
    }


    /**
     * 显示自定义键盘
     */
    public void showKeyboard() {
        int visibility = mKeyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.VISIBLE) {
            mKeyboardView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏自定义键盘
     */
    public void hideKeyboard() {
        int visibility = mKeyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            mKeyboardView.setVisibility(View.GONE);
        }
    }


    private OnCuntomKeyboardListener mListener;

    public void setOnCuntomKeyboardListener(OnCuntomKeyboardListener listener) {
        this.mListener = listener;
    }

    public interface OnCuntomKeyboardListener {
        void onCancel(boolean aBoolean);

        void onEnter(boolean aBoolean);
    }
}
