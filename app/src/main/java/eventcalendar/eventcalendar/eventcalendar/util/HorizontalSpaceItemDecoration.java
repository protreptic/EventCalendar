package eventcalendar.eventcalendar.eventcalendar.util;

import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public final class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int mHorizontalSpace;

    public HorizontalSpaceItemDecoration(@IntRange(from = 0) int horizontalSpace) {
        mHorizontalSpace = horizontalSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if ((parent.getChildAdapterPosition(view) % 2) == 0) {
            outRect.right = mHorizontalSpace / 2;
        } else {
            outRect.left = mHorizontalSpace / 2;
        }
    }

}