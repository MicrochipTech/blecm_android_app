package android.ble.wcm.microchip.com.microchip.widget;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by jossayjacobo on 11/5/14
 */
public class MicrochipDrawerLayout extends DrawerLayout {

    private View currentDrawerView;

    public MicrochipDrawerLayout(Context context) {
        super(context);
    }

    public MicrochipDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MicrochipDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean result = super.onInterceptTouchEvent(event);

        if (currentDrawerView != null && isDrawerOpen(currentDrawerView)) {
            DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams) currentDrawerView.getLayoutParams();

            if (layoutParams.gravity == Gravity.END) {
                if (event.getX() < currentDrawerView.getX()) {
                    result = false;
                }
            }
            else if (layoutParams.gravity == Gravity.START) {
                if (event.getX() > currentDrawerView.getX() + currentDrawerView.getWidth()) {
                    result = false;
                }
            }

        }
        return result;
    }

    public void setDrawerViewWithoutIntercepting(View view) {
        this.currentDrawerView = view;
    }
}
