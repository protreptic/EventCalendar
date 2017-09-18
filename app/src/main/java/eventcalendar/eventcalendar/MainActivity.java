package eventcalendar.eventcalendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.joda.time.LocalDate;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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
        vEventCalendar.setPickedDate(LocalDate.now().plusYears(1));
        vEventCalendar.setOnMonthChangedListener(new EventCalendar.OnMonthChangedListener() {
            @Override
            public void onMonthChanged(LocalDate date) {}
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

}
