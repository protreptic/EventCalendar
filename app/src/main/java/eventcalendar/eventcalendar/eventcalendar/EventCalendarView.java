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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eventcalendar.eventcalendar.R;

import static android.graphics.Typeface.createFromAsset;
import static android.support.v4.content.ContextCompat.getColor;
import static android.support.v4.content.ContextCompat.getDrawable;
import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.applyDimension;
import static android.view.LayoutInflater.from;

public final class EventCalendarView extends RecyclerView {

    public interface OnDatePickedListener {
        void onEventDayPicked(EventCalendarView calendar, LocalDate pickedDate);
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

    private LocalDate mDate;
    private LocalDate mPickedDate;

    private void init() {
        mContext = getContext();

        setDate(LocalDate.now());
        setAdapter(new EventCalendarAdapter());
        setLayoutManager(new GridLayoutManager(mContext, 7));
    }

    private OnDatePickedListener mOnDatePickedListener;

    public void setOnEventDayPickedListener(@Nullable OnDatePickedListener listener) {
        mOnDatePickedListener = listener;
    }

    private void notifyDatePicked(@NonNull LocalDate pickedDay) {
        if (mOnDatePickedListener != null) {
            mOnDatePickedListener.onEventDayPicked(this, pickedDay);
        }
    }

    public void setDate(@NonNull LocalDate date) {
        mDate = date.withDayOfMonth(1);
        mPickedDate = null;
    }

    @Nullable
    public LocalDate getPickedDate() {
        return mPickedDate;
    }

    public void setPickedDate(@NonNull LocalDate pickedDate) {
        mPickedDate = pickedDate;

        getAdapter().notifyDataSetChanged();
    }

    private List<Event> mEvents = new ArrayList<>();

    public void setEvents(@Nullable List<Event> events) {
        if (events == null) {
            events = Collections.emptyList();
        }

        mEvents = events;
    }

    private class EventCalendarAdapter extends RecyclerView.Adapter<EventDayViewHolder> {

        private static final int TYPE_DATE_EMPTY = 1;
        private static final int TYPE_DATE = 2;

        @Override
        public int getItemViewType(int position) {
            if ((position + 1 < mDate.getDayOfWeek()) ||
                    (getDate(position).getMonthOfYear() != mDate.getMonthOfYear())) {
                return TYPE_DATE_EMPTY;
            }

            return TYPE_DATE;
        }

        LocalDate getDate(int position) {
            return mDate.plusDays(position - mDate.getDayOfWeek() + 1);
        }

        @Override
        public EventDayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new EventDayViewHolder(from(mContext)
                    .inflate(R.layout.view_event_calendar_date, parent, false));
        }

        @Override
        public void onBindViewHolder(EventDayViewHolder holder, int position) {
            LocalDate eventDay = getDate(position);

            switch (getItemViewType(position)) {
                case TYPE_DATE_EMPTY: {
                    holder.bindEmptyDate();
                } break;
                case TYPE_DATE: {
                    holder.bindDate(eventDay);
                } break;
            }
        }

        @Override
        public int getItemCount() {
            return mDate.dayOfMonth().getMaximumValue() + mDate.getDayOfWeek();
        }

    }

    class EventDayViewHolder extends ViewHolder {

        @BindView(R.id.event_date)
        TextView vDate;

        @BindView(R.id.event_indicator)
        ViewGroup vDateIndicator;

        EventDayViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bindEmptyDate() {
            vDate.setText("");
            vDate.setTextColor(getColor(getContext(), R.color.greyish_brown));

            vDateIndicator.setVisibility(GONE);
            vDateIndicator.removeAllViews();

            vDate.setBackground(null);
        }

        void bindDate(final LocalDate date) {
            prepareText(date);
            prepareIndicator(date);
            prepareBackground(date);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isPickedDate(date)) return;

                    mPickedDate = date;

                    getAdapter().notifyDataSetChanged();
                    notifyDatePicked(date);
                }

            });
        }

        private void prepareText(LocalDate date) {
            vDate.setText(getContext().getString(R.string.event_day, date.getDayOfMonth()));
            vDate.setTextColor(getColor(getContext(),
                    isPickedDate(date) ?
                            R.color.white :
                            R.color.greyish_brown));
            vDate.setTypeface(
                    isPickedDate(date) ?
                            createFromAsset(getContext().getAssets(), "font/Gilroy-Bold.ttf") :
                            createFromAsset(getContext().getAssets(), "font/Gilroy-Medium.ttf"));
        }

        private void prepareIndicator(LocalDate date) {
            vDateIndicator.setVisibility(VISIBLE);
            vDateIndicator.removeAllViews();

            for (Event event : mEvents)  {
                if (date.equals(event.getDate())) {
                    View indicator = new View(getContext());
                    indicator.setLayoutParams(new LayoutParams(getDp(6), getDp(6)));
                    indicator.setPadding(0, 0, getDp(2), 0);
                    indicator.setBackgroundColor(event.getColor());

                    vDateIndicator.addView(indicator);
                }
            }
        }

        private void prepareBackground(LocalDate date) {
            if (isPickedDate(date)) {
                vDate.setBackground(getDrawable(getContext(), R.drawable.picked_event_day));
            } else if (isTodayDate(date)) {
                vDate.setBackground(getDrawable(getContext(), R.drawable.today_event_day));
            } else {
                vDate.setBackground(getDrawable(getContext(), R.drawable.ripple));
            }
        }

        private boolean isTodayDate(LocalDate date) {
            return LocalDate.now().equals(date);
        }

        private boolean isPickedDate(LocalDate date) {
            return mPickedDate != null && mPickedDate.equals(date);
        }

        private int getDp(int px) {
            return (int) applyDimension(COMPLEX_UNIT_DIP, px,
                    getResources().getDisplayMetrics());
        }

    }

}
