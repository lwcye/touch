package com.byid.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 随着软键盘弹出和收回,自动滑动的ScrollView
 *
 * @author wuao
 * @date 2017.07.04
 * @note Activity需要在清单里添加属性windowSoftInputMode
 * ---------------------------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class AutoScrollView extends ScrollView {

    /**
     * 构造函数
     *
     * @param context 上下文
     */
    public AutoScrollView(Context context) {
        this(context, null);
    }

    /**
     * 构造函数
     *
     * @param context 上下文
     * @param attrs 属性
     */
    public AutoScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造函数
     *
     * @param context 上下文
     * @param attrs 属性
     * @param defStyleAttr 样式
     */
    public AutoScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        // 添加布局改变监听
        addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                smoothScrollTo(0, getHeight());
            }
        });
    }
}
