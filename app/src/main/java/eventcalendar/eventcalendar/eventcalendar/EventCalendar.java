package eventcalendar.eventcalendar.eventcalendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.LocalDate;

import butterknife.BindView;
import butterknife.ButterKnife;
import eventcalendar.eventcalendar.R;
import eventcalendar.eventcalendar.eventcalendar.util.HorizontalSpaceItemDecoration;
import eventcalendar.eventcalendar.eventcalendar.util.VerticalSpaceItemDecoration;

import static android.support.v4.content.ContextCompat.getColor;
import static android.support.v4.content.ContextCompat.getDrawable;
import static android.view.LayoutInflater.from;

public final class EventCalendar extends RecyclerView {

    public interface OnEventDayPickedListener {
        void onEventDayPicked(LocalDate pickedDay, boolean hasEvents);
    }

    public EventCalendar(Context context) {
        super(context);

        init();
    }

    public EventCalendar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public EventCalendar(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private Context mContext;
    private LocalDate mTargetDate;
    private LocalDate mTargetMonthDate;
    private LocalDate mTargetPickedDate;

    private void init() {
        mContext = getContext();

        setTargetDate(LocalDate.now());
        setLayoutManager(new GridLayoutManager(mContext, 7));

        addItemDecoration(new VerticalSpaceItemDecoration(24));
        addItemDecoration(new HorizontalSpaceItemDecoration(8));

        setAdapter(new EventCalendarAdapter());
    }

    private OnEventDayPickedListener mOnEventDayPickedListener;

    public void setOnEventDayPickedListener(@Nullable OnEventDayPickedListener listener) {
        mOnEventDayPickedListener = listener;
    }

    private void notifyEventDayPicked(@NonNull LocalDate pickedDay, boolean hasEvents) {
        if (mOnEventDayPickedListener != null) {
            mOnEventDayPickedListener.onEventDayPicked(pickedDay, hasEvents);
        }
    }

    public void setTargetDate(@NonNull LocalDate targetDate) {
        mTargetDate = targetDate;
        mTargetPickedDate = targetDate;
        mTargetMonthDate = mTargetDate.withDayOfMonth(1);
    }

    private class EventCalendarAdapter extends RecyclerView.Adapter<EventDayViewHolder> {

        private static final int EVENT_DAY_EMPTY = 1;
        private static final int EVENT_DAY = 2;

        @Override
        public int getItemViewType(int position) {
            if (position < mTargetMonthDate.getDayOfWeek()) {
                return EVENT_DAY_EMPTY;
            }

            if (getEventDay(position).getMonthOfYear() != mTargetMonthDate.getMonthOfYear()) {
                return EVENT_DAY_EMPTY;
            }

            return EVENT_DAY;
        }

        LocalDate getEventDay(int position) {
            return mTargetMonthDate.plusDays(position - mTargetMonthDate.getDayOfWeek());
        }

        @Override
        public EventDayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new EventDayViewHolder(from(mContext)
                    .inflate(R.layout.item_calendar_event_day, parent, false));
        }

        @Override
        public void onBindViewHolder(EventDayViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case EVENT_DAY_EMPTY: {
                    holder.bindEmptyEventDay();
                } break;
                case EVENT_DAY: {
                    holder.bindEventDay(getEventDay(position));
                } break;
            }
        }

        @Override
        public int getItemCount() {
            return mTargetMonthDate.dayOfMonth().getMaximumValue() + mTargetMonthDate.getDayOfWeek();
        }

    }

    class EventDayViewHolder extends ViewHolder {

        @BindView(R.id.event_day)
        TextView vEventDay;

        EventDayViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bindEmptyEventDay() {
            vEventDay.setText("");
            vEventDay.setTextColor(getColor(getContext(), R.color.greyish_brown));

            itemView.setVisibility(GONE);
            itemView.setBackground(null);
            itemView.setOnClickListener(null);
        }

        void bindEventDay(final LocalDate date) {
            vEventDay.setText(getContext().getString(R.string.event_day, date.getDayOfMonth()));

            if (mTargetPickedDate.equals(date)) {
                vEventDay.setTextColor(getColor(getContext(), R.color.white));

                itemView.setBackground(getDrawable(getContext(), R.drawable.picked_event_day));
            } else if (LocalDate.now().equals(date)) {
                vEventDay.setTextColor(getColor(getContext(), R.color.greyish_brown));

                itemView.setBackground(getDrawable(getContext(), R.drawable.today_event_day));
            } else {
                vEventDay.setTextColor(getColor(getContext(), R.color.greyish_brown));

                itemView.setBackground(null);
            }

            itemView.setVisibility(VISIBLE);
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mTargetPickedDate = date;

                    getAdapter().notifyDataSetChanged();
                    notifyEventDayPicked(date, false);
                }

            });
        }

    }

}
