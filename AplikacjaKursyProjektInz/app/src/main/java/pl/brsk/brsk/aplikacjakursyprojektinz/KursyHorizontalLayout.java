package pl.brsk.brsk.aplikacjakursyprojektinz;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by borse on 22.02.2017.
 */

public class KursyHorizontalLayout extends LinearLayoutManager {
    private int mParentWidth;
    private int mItemWidth;

    public KursyHorizontalLayout(Context context, int parentWidth, int itemWidth) {
        super(context);
        mParentWidth = parentWidth;
        mItemWidth = itemWidth;
    }

    @Override
    public int getPaddingLeft() {
        return Math.round(mParentWidth / 2f - mItemWidth / 2f);
    }

    @Override
    public int getPaddingRight() {
        return getPaddingLeft();
    }
}
