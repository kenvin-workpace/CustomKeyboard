package com.whz.customkeyboard.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import com.whz.customkeyboard.R;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by kevin on 17/9/20.
 */

public class CustomKeyboardView extends KeyboardView {

    private Context mContext;
    private Keyboard mKeyboard;
    private Paint mPaint;

    public CustomKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        initPaint();
    }

    public CustomKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        initPaint();
    }


    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(Color.WHITE);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取键盘
        mKeyboard = this.getKeyboard();

        //获取按键
        List<Keyboard.Key> keys = null;
        if (null != mKeyboard) {
            keys = mKeyboard.getKeys();
        }

        //获取数字建
        if (null != keys) {
            for (Keyboard.Key key : keys) {
                //对数字键盘进行处理
                if (key.codes[0] == -4) {
                    drawBackground(R.drawable.bg_keyboard_enter, canvas, key);
                    drawText(canvas, key);
                }
            }
        }
    }

    /**
     * 设置显示数字键大小
     *
     * @param canvas
     * @param key
     */
    private void drawText(Canvas canvas, Keyboard.Key key) {
        Rect bounds = new Rect();

        if (null != key.label) {
            String label = key.label.toString();

            if (label.length() > 1 && key.codes.length < 2) {
                mPaint.setTextSize(getLabelTextSize());
                mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                mPaint.setTextSize(getLabelTextSize());
                mPaint.setTypeface(Typeface.DEFAULT);
            }

            mPaint.getTextBounds(label, 0, label.length(), bounds);
            canvas.drawText(label, key.x + key.width / 2, key.y + key.height / 2 + bounds.height() / 2, mPaint);

        } else if (null != key.icon) {

            int left = key.x + (key.width - key.icon.getIntrinsicWidth()) / 2;
            int top = key.y + (key.height - key.icon.getIntrinsicHeight()) / 2;
            int rigth = left + key.icon.getIntrinsicWidth();
            int bottom = top + key.icon.getIntrinsicHeight();

            key.icon.setBounds(left, top, rigth, bottom);
            key.icon.draw(canvas);
        }
    }

    /**
     * 获取数字建显示文本大小
     */
    private int getLabelTextSize() {
        try {
            Field field = KeyboardView.class.getDeclaredField("mLabelTextSize");
            field.setAccessible(true);
            return (int) field.get(this);
        } catch (Exception e) {

        }
        return -1;
    }

    /**
     * 数字键盘背景
     *
     * @param bg
     * @param canvas
     * @param key
     */
    private void drawBackground(int bg, Canvas canvas, Keyboard.Key key) {
        Drawable drawable = mContext.getResources().getDrawable(bg);
        int[] drawableState = key.getCurrentDrawableState();

        if (key.codes[0] != 0) {
            drawable.setState(drawableState);
        }
        drawable.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        drawable.draw(canvas);
    }
}
