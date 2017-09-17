package eventcalendar.eventcalendar.eventcalendar;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eventcalendar.eventcalendar.R;

import static android.graphics.Typeface.createFromAsset;
import static android.support.v4.content.ContextCompat.getColor;
import static android.support.v4.content.ContextCompat.getDrawable;
import static android.view.LayoutInflater.from;

public final class EventCalendarView extends RecyclerView {

    public interface OnEventDayPickedListener {
        void onEventDayPicked(LocalDate pickedDay, boolean hasEvents);
    }

    public EventCalendarView(Context context) {
        super(context);

        init();
    }

    public EventCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public EventCalendarView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private Context mContext;

    private LocalDate mMonthDate;
    private LocalDate mPickedDate;

    private void init() {
        mContext = getContext();

        setDate(LocalDate.now());
        setLayoutManager(new GridLayoutManager(mContext, 7));
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

    public void setDate(@NonNull LocalDate date) {
        mPickedDate = null;
        mMonthDate = date.withDayOfMonth(1);
    }

    @Nullable
    public LocalDate getPickedEventDay() {
        return mPickedDate;
    }

    public void setPickedEventDay(LocalDate pickedDay) {
        mPickedDate = pickedDay;

        getAdapter().notifyDataSetChanged();
    }

    private List<EventDay> mEventDays = new ArrayList<>();

    public void setSchedule(List<EventDay> schedule) {
        mEventDays = Collections.unmodifiableList(schedule);
    }

    final class EventDay {

        private final LocalDate date;
        private final List<Color> evens;

        public EventDay(LocalDate date, List<Color> evens) {
            this.date = date;
            this.evens = evens;
        }

        public LocalDate getDate() {
            return date;
        }

        public List<Color> getEvens() {
            return evens;
        }

        public boolean hasEvents() {
            return !evens.isEmpty();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EventDay eventDay = (EventDay) o;

            return date != null ? date.equals(eventDay.date) : eventDay.date == null;

        }

        @Override
        public int hashCode() {
            return date != null ? date.hashCode() : 0;
        }

    }

    private class EventCalendarAdapter extends RecyclerView.Adapter<EventDayViewHolder> {

        private static final int EVENT_DAY_EMPTY = 1;
        private static final int EVENT_DAY = 2;

        @Override
        public int getItemViewType(int position) {
            if (position + 1 < mMonthDate.getDayOfWeek()) {
                return EVENT_DAY_EMPTY;
            }

            if (getEventDay(position).getMonthOfYear() != mMonthDate.getMonthOfYear()) {
                return EVENT_DAY_EMPTY;
            }

            return EVENT_DAY;
        }

        LocalDate getEventDay(int position) {
            return mMonthDate.plusDays(position - mMonthDate.getDayOfWeek() + 1);
        }

        @Override
        public EventDayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new EventDayViewHolder(from(mContext)
                    .inflate(R.layout.l_event_calendar_event_day, parent, false));
        }

        @Override
        public void onBindViewHolder(EventDayViewHolder holder, int position) {
            LocalDate eventDay = getEventDay(position);

            switch (getItemViewType(position)) {
                case EVENT_DAY_EMPTY: {
                    holder.bindEmptyEventDay();
                } break;
                case EVENT_DAY: {
                    holder.bindEventDay(eventDay);
                } break;
            }
        }

        @Override
        public int getItemCount() {
            return mMonthDate.dayOfMonth().getMaximumValue() + mMonthDate.getDayOfWeek();
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

        void bindEventDay(final LocalDate eventDay) {
            vEventDay.setText(getContext().getString(R.string.event_day, eventDay.getDayOfMonth()));
            vEventDay.setTextColor(getColor(getContext(),
                    isPicked(eventDay) ? R.color.white : R.color.greyish_brown));
            vEventDay.setTypeface(
                    isPicked(eventDay) ?
                            createFromAsset(getContext().getAssets(), "font/Gilroy-Bold.ttf") :
                            createFromAsset(getContext().getAssets(), "font/Gilroy-Medium.ttf"));

            if (isPicked(eventDay)) {
                itemView.setBackground(getDrawable(getContext(), R.drawable.picked_event_day));
            } else if (isToday(eventDay)) {
                itemView.setBackground(getDrawable(getContext(), R.drawable.today_event_day));
            } else {
                itemView.setBackground(null);
            }

            if (mEventDays.contains(new EventDay(eventDay, null))) {

            }

            itemView.setVisibility(VISIBLE);
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isPicked(eventDay)) return;

                    mPickedDate = eventDay;

                    getAdapter().notifyDataSetChanged();
                    notifyEventDayPicked(eventDay, false);
                }

            });
        }

        private boolean isToday(LocalDate eventDay) {
            return LocalDate.now().equals(eventDay);
        }

        private boolean isPicked(LocalDate eventDay) {
            return mPickedDate != null && mPickedDate.equals(eventDay);
        }

    }

}
