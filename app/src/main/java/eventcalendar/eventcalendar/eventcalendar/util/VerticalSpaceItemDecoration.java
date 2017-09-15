package eventcalendar.eventcalendar.eventcalendar.util;

import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public final class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int mVerticalSpace;

    public VerticalSpaceItemDecoration(@IntRange(from = 0) int verticalSpace) {
        mVerticalSpace = verticalSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = mVerticalSpace;
        }
    }

}