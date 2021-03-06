package eventcalendar.eventcalendar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.joda.time.LocalDate;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eventcalendar.eventcalendar.eventcalendar.Event;
import eventcalendar.eventcalendar.eventcalendar.EventCalendar;
import eventcalendar.eventcalendar.eventcalendar.EventCalendar.OnDatePickedListener;

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
        vEventCalendar.setPickedDate(LocalDate.now().withDayOfMonth(13));
        vEventCalendar.setEvents(sEvents);
        vEventCalendar.setOnMonthChangedListener(new EventCalendar.OnMonthChangedListener() {
            @Override
            public void onMonthChanged(LocalDate date) {
                Toast.makeText(getApplicationContext(), date.toString(), LENGTH_SHORT).show();
            }
        });
        vEventCalendar.setOnDatePickedListener(new OnDatePickedListener() {
            @Override
            public void onDatePicked(LocalDate pickedDate) {
                Toast.makeText(getApplicationContext(), pickedDate.toString(), LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        mUnbinder.unbind();
    }

    private static List<Event> sEvents =
        Arrays.asList(
                new Event(LocalDate.now(), Color.parseColor("#ff3c41")),
                new Event(LocalDate.now(), Color.parseColor("#32c896")),
                new Event(LocalDate.now(), Color.parseColor("#4b9bf5")),

                new Event(LocalDate.now().plusDays(2), Color.parseColor("#ff3c41")),
                new Event(LocalDate.now().plusDays(2), Color.parseColor("#32c896")),
                new Event(LocalDate.now().plusDays(2), Color.parseColor("#ffc800")),
                new Event(LocalDate.now().plusDays(2), Color.parseColor("#4b9bf5")),

                new Event(LocalDate.now().plusDays(4), Color.parseColor("#ff3c41")),
                new Event(LocalDate.now().plusDays(4), Color.parseColor("#32c896")),
                new Event(LocalDate.now().plusDays(4), Color.parseColor("#ffc800")),
                new Event(LocalDate.now().plusDays(4), Color.parseColor("#4b9bf5")),

                new Event(LocalDate.now().plusDays(14), Color.parseColor("#ff3c41")),
                new Event(LocalDate.now().plusDays(14), Color.parseColor("#32c896")),
                new Event(LocalDate.now().plusDays(44), Color.parseColor("#ffc800")),
                new Event(LocalDate.now().plusDays(44), Color.parseColor("#4b9bf5")),

                new Event(LocalDate.now().withDayOfMonth(7), Color.parseColor("#32c896")),
                new Event(LocalDate.now().withDayOfMonth(7), Color.parseColor("#ffc800")),

                new Event(LocalDate.now().plusDays(11), Color.parseColor("#4b9bf5")));

}
