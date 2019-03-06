package com.whz.customkeyboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.whz.customkeyboard.view.KeyboardUtil;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;
    private CheckBox mCbFlag;
    private KeyboardUtil mKeyboardUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initEvent();
        initListener();
    }


    /**
     * 初始化监听事件
     */
    private void initListener() {
        mKeyboardUtil.setOnCuntomKeyboardListener(new KeyboardUtil.OnCuntomKeyboardListener() {
            @Override
            public void onCancel(boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(MainActivity.this, "键盘取消了", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onEnter(boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(MainActivity.this, "键盘按确定了", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * 初始化View事件
     */
    private void initEvent() {
        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mKeyboardUtil.attachTo(mEditText);
            }
        });

        mCbFlag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mKeyboardUtil = new KeyboardUtil(MainActivity.this, true);
                } else {
                    mKeyboardUtil = new KeyboardUtil(MainActivity.this);
                }
                mKeyboardUtil.hideKeyboard();
            }
        });
    }

    /**
     * 初始化View数据
     */
    private void initData() {
        mKeyboardUtil = new KeyboardUtil(MainActivity.this, false);
    }

    /**
     * 初始化View
     */
    private void initView() {
        mEditText = (EditText) findViewById(R.id.et_num);
        mCbFlag = (CheckBox) findViewById(R.id.cb_flag);
    }
}
