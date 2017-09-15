package eventcalendar.eventcalendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.joda.time.LocalDate;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eventcalendar.eventcalendar.eventcalendar.EventCalendar;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.event_calendar)
    EventCalendar vEventCalendar;

    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mUnbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        vEventCalendar.setTargetDate(LocalDate.now().withMonthOfYear(9));
        vEventCalendar.setOnEventDayPickedListener(new EventCalendar.OnEventDayPickedListener() {

            @Override
            public void onEventDayPicked(LocalDate pickedDay, boolean hasEvents) {
                Toast.makeText(getApplicationContext(), "pickedDay: " + pickedDay + " hasEvents:" + hasEvents, LENGTH_LONG).show();
            }

        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        mUnbinder.unbind();
    }

}
