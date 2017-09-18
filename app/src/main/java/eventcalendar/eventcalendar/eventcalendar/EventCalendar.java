package eventcalendar.eventcalendar.eventcalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eventcalendar.eventcalendar.R;

import static android.graphics.Typeface.createFromAsset;
import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;

@SuppressWarnings("FieldCanBeLocal")
public final class EventCalendar extends LinearLayout {

    public interface OnMonthChangedListener {
        void onMonthChanged(LocalDate date);
    }

    public interface OnDatePickedListener {
        void onDatePicked(LocalDate pickedDate);
    }

    public EventCalendar(Context context) {
        super(context);

        init();
    }

    public EventCalendar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public EventCalendar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private ImageButton vPrevious;
    private ImageButton vNext;

    private TextView vMonthAndYear;

    private ViewPager vCalendarSlider;

    private void init() {
        LayoutInflater.from(getContext())
                .inflate(R.layout.l_event_calendar, this, true);

        vPrevious = (ImageButton) findViewById(R.id.previous);
        vPrevious.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toPreviousMonth();
            }

        });

        vNext = (ImageButton) findViewById(R.id.next);
        vNext.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toNextMonth();
            }

        });

        vMonthAndYear = (TextView) findViewById(R.id.month_and_year);
        vMonthAndYear.setTypeface(createFromAsset(getContext().getAssets(), "font/Gilroy-Bold.ttf"));

        TextView tvMonday = (TextView) findViewById(R.id.monday);
        tvMonday.setTypeface(createFromAsset(getContext().getAssets(), "font/Gilroy-Medium.ttf"));

        TextView tvTuesday = (TextView) findViewById(R.id.tuesday);
        tvTuesday.setTypeface(createFromAsset(getContext().getAssets(), "font/Gilroy-Medium.ttf"));

        TextView tvWednesday = (TextView) findViewById(R.id.wednesday);
        tvWednesday.setTypeface(createFromAsset(getContext().getAssets(), "font/Gilroy-Medium.ttf"));

        TextView tvThursday = (TextView) findViewById(R.id.thursday);
        tvThursday.setTypeface(createFromAsset(getContext().getAssets(), "font/Gilroy-Medium.ttf"));

        TextView tvFriday = (TextView) findViewById(R.id.friday);
        tvFriday.setTypeface(createFromAsset(getContext().getAssets(), "font/Gilroy-Medium.ttf"));

        TextView tvSaturday = (TextView) findViewById(R.id.saturday);
        tvSaturday.setTypeface(createFromAsset(getContext().getAssets(), "font/Gilroy-Medium.ttf"));

        TextView tvSunday = (TextView) findViewById(R.id.sunday);
        tvSunday.setTypeface(createFromAsset(getContext().getAssets(), "font/Gilroy-Medium.ttf"));

        vCalendarSlider = (ViewPager) findViewById(R.id.calendar_slider);
        vCalendarSlider.setAdapter(new EventCalendarPagerAdapter());
        vCalendarSlider.setCurrentItem(Integer.MAX_VALUE / 2);
        vCalendarSlider.addOnPageChangeListener(new OnPageChangeListenerImpl() {

            @Override
            public void onPageScrollStateChanged(int state) {
                if (SCROLL_STATE_IDLE == state) {
                    notifyMonthChanged();
                }
            }
        });
    }

    private LocalDate mInitialDate;

    public void setInitialDate(LocalDate date) {
        mInitialDate = date;
        mPickedDate = date;
    }

    private OnDatePickedListener mOnDatePickedListener;

    public void setOnDatePickedListener(OnDatePickedListener listener) {
        mOnDatePickedListener = listener;
    }

    private void notifyDatePicked() {
        if (mOnDatePickedListener != null) {
            mOnDatePickedListener.onDatePicked(mPickedDate);
        }
    }

    private EventCalendarView.OnDatePickedListener mOnEventDayPickedListener =
            new EventCalendarView.OnDatePickedListener() {

        @Override
        public void onEventDayPicked(EventCalendarView calendar, LocalDate pickedDate) {
            setPickedDate(pickedDate);
            dropPickedDate(calendar);

            notifyDatePicked();
        }

    };

    private OnMonthChangedListener mOnMonthChangedListener;

    public void setOnMonthChangedListener(OnMonthChangedListener listener) {
        mOnMonthChangedListener = listener;
    }

    private void notifyMonthChanged() {
        if (mOnMonthChangedListener != null) {
            mOnMonthChangedListener.onMonthChanged(getCurrentDate());
        }
    }

    private void toPreviousMonth() {
        vCalendarSlider.setCurrentItem(
                vCalendarSlider.getCurrentItem() - 1, true);
    }

    private void toNextMonth() {
        vCalendarSlider.setCurrentItem(
                vCalendarSlider.getCurrentItem() + 1, true);
    }

    private void updateCalendarHeader() {
        LocalDate date = getCurrentDate();

        vMonthAndYear.setText(getContext().getString(R.string.month_and_year,
                getResources().getStringArray(R.array.months)[date.getMonthOfYear() - 1],
                date.getYear()));
    }

    private LocalDate getCurrentDate() {
        return mCache.get(vCalendarSlider.getCurrentItem());
    }

    private EventCalendarView vCalendar;

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, LocalDate> mCache = new HashMap<>();
    private List<EventCalendarView> mCalendars = new ArrayList<>();

    private LocalDate mPickedDate;

    public void setPickedDate(LocalDate pickedDate) {
        mPickedDate = pickedDate;
    }

    public void dropPickedDate(EventCalendarView view) {
        for (EventCalendarView calendar : mCalendars) {
            if (calendar == view) continue;

            calendar.setPickedEventDay(null);
        }
    }

    private class EventCalendarPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup group, int position) {
            LocalDate date = mInitialDate;

            if (mCache.containsKey(position)) {
                date = mCache.get(position);
            } else {
                if (vCalendarSlider.getCurrentItem() == position) {
                    date = mInitialDate;

                    mCache.put(position, date);
                } else {
                    if (vCalendarSlider.getCurrentItem() > position) {
                        if (mCache.containsKey(position + 1)) {
                            date = mCache.get(position + 1).minusMonths(1);
                        }
                    }

                    if (vCalendarSlider.getCurrentItem() < position) {
                        if (mCache.containsKey(position - 1)) {
                            date = mCache.get(position - 1).plusMonths(1);
                        }
                    }

                    mCache.put(position, date);
                }
            }

            vCalendar = new EventCalendarView(getContext());
            vCalendar.setOnEventDayPickedListener(mOnEventDayPickedListener);
            vCalendar.setDate(date);

            if (date.withDayOfMonth(1).equals(mPickedDate.withDayOfMonth(1))) {
                vCalendar.setPickedEventDay(mPickedDate);
            }

            group.addView(vCalendar);

            mCalendars.add(vCalendar);

            updateCalendarHeader();

            return vCalendar;
        }

        @SuppressWarnings("RedundantCast")
        @Override
        public void destroyItem(ViewGroup group, int position, Object view) {
            mCalendars.remove((EventCalendarView) view);
            group.removeView((View) view);
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

}