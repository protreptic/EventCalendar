package eventcalendar.eventcalendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.joda.time.LocalDate;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eventcalendar.eventcalendar.eventcalendar.EventCalendarView;
import eventcalendar.eventcalendar.eventcalendar.EventCalendar;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.event_calendar)
    EventCalendar vEventCalendar;

    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.a_main);

        mUnbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        vEventCalendar.setInitialDate(LocalDate.now());
        vEventCalendar.setEventCalendarSwipeListener(new EventCalendar.OnMonthChangedListener() {
            @Override
            public void onMonthChanged(LocalDate date) {
                Toast.makeText(getApplicationContext(), date.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        vEventCalendar.setOnEventDayPickedListener(new EventCalendarView.OnEventDayPickedListener() {

            @Override
            public void onEventDayPicked(LocalDate pickedDay, boolean hasEvents) {
                Toast.makeText(getApplicationContext(), pickedDay.toString(), LENGTH_SHORT).show();
            }

        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        mUnbinder.unbind();
    }

}
