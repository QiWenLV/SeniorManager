package com.zqw.appmanger.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.zqw.appmanger.R;


/**
 * Created by 启文 on 2017/2/7.
 */
public class SildingMenu extends HorizontalScrollView
{
    private LinearLayout mWapper;
    private ViewGroup mMenu;
    private ViewGroup mContent;
    private int mScreenWidth;   //屏幕宽度

    boolean once = false;

    boolean isOpen;

    private int mMenuRightpading = 50;  //单位dp
    private int mMenuWidth;

    public SildingMenu(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**
     * 当使用了自定义属性时，调用此构造方法
     * @param context
     * @param attrs
     * @param defStyleAttr
     */

    public SildingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.SlidingMenu,defStyleAttr,0);
        int n = a.getIndexCount();
        for(int i = 0;i<n;i++){
            int attr = a.getIndex(i);
            switch(attr){
                case R.styleable.SlidingMenu_rightPadding:
                    mMenuRightpading = a.getDimensionPixelSize(attr,    //剩余主页面的宽度,将sp转化为px
                            (int)TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP,50,context.getResources().getDisplayMetrics()));
                break;
            }
        }

        a.recycle();
        //获取屏幕的宽
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
    }

    /**
     * 设置字view的宽和高
     * 设置自己的宽和高
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if(!once) {
            mWapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) mWapper.getChildAt(0);
            mContent = (ViewGroup) mWapper.getChildAt(1);
            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightpading; //菜单的宽度为屏幕宽度减去剩余主页面的宽度
            mContent.getLayoutParams().width = mScreenWidth;    //容器宽度
            once = true;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 通过设置偏移量，将menu隐藏
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        super.onLayout(changed, l, t, r, b);
        if(changed) {
            this.scrollTo(mMenuWidth, 0);       //隐藏菜单
        }
    }

    /**
     * 监听用户的操作
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        switch (action){

            case MotionEvent.ACTION_UP:     //当用户释放屏幕时
                int scrollX = getScrollX();
                if(scrollX >= mMenuWidth/2){    //判断菜单拖出超过1/2就显示菜单，否则隐藏
                    this.smoothScrollTo(mMenuWidth,0);
                    isOpen = false;
                }else{
                    this.smoothScrollTo(0,0);
                    isOpen = true;
                }
                return true;
        }

        return super.onTouchEvent(ev);
    }
    //打开菜单
    public void openMenu(){
        if(isOpen)
            return;
        this.smoothScrollTo(0,0);
        isOpen = true;
    }
    //关闭菜单
    public void closeMenu(){
        if(!isOpen)
            return;
        this.smoothScrollTo(mMenuWidth,0);
        isOpen = false;
    }
    //切换菜单
    public void toggle(){
        if(isOpen){
            closeMenu();
        }else {
            openMenu();
        }
    }

    /**
     * 抽屉式动画
     *
     */

//    public void onScrollChanged(int l,int t,int oldl,int oldt){
//        super.onScrollChanged(l,t,oldl,oldt);
//
//        //调用属性动画，设置TranslationX
//        ObjectAnimator animator = ObjectAnimator.ofFloat(mMenu,"TranslationX",oldl,l);
//        animator.setDuration(0);
//        animator.start();
//    }

    /**
     * 缩放抽屉式动画
     *
     */
//    public void onScrollChanged(int l,int t,int oldl,int oldt){
//        super.onScrollChanged(l,t,oldl,oldt);
//
//        //调用属性动画，设置TranslationX
//        ObjectAnimator animator = ObjectAnimator.ofFloat(mMenu,"TranslationX",oldl,l);
//        animator.setDuration(0);
//        animator.start();
//    }
}

