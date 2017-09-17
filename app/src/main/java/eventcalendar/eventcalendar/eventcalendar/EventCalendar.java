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

import java.util.HashMap;

import eventcalendar.eventcalendar.R;
import eventcalendar.eventcalendar.eventcalendar.EventCalendarView.OnEventDayPickedListener;

import static android.graphics.Typeface.createFromAsset;
import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;

@SuppressWarnings("FieldCanBeLocal")
public final class EventCalendar extends LinearLayout {

    public interface OnMonthChangedListener {
        void onMonthChanged(LocalDate date);
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
                .inflate(R.layout.w_event_calendar, this, true);

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
    }

    private OnEventDayPickedListener mOnEventDayPickedListener;

    public void setOnEventDayPickedListener(@Nullable OnEventDayPickedListener listener) {
        mOnEventDayPickedListener = listener;
    }

    private OnMonthChangedListener mOnMonthChangedListener;

    public void setEventCalendarSwipeListener(OnMonthChangedListener listener) {
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

    @Nullable
    public EventCalendarView getEventCalendar() {
        return vCalendar;
    }

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, LocalDate> mCache = new HashMap<>();

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

            group.addView(vCalendar);

            updateCalendarHeader();

            return vCalendar;
        }

        @Override
        public void destroyItem(ViewGroup group, int position, Object view) {
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
