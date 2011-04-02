package moontime.droid;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import moontime.MoonEvent;
import moontime.MoonEventType;
import moontime.MoonUtil;
import moontime.droid.CalendarView.OnDayClickListener;
import moontime.droid.service.MoontimeService;
import moontime.droid.util.Util;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;
import android.appwidget.AppWidgetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.inject.Inject;

public class CalendarActivity extends RoboActivity {

  @Inject
  protected MoontimeService _moontimeService;
  protected final Calendar _selectedYearMonth = MoonUtil.newCalender(0);
  private DateFormat _datePattern = new SimpleDateFormat("dd - HH:mm");

  @InjectExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)
  protected int _widgetId;
  @InjectView(R.id.calendar_calendarView)
  private CalendarView _calendarView;
  @InjectView(R.id.calendar_text_moons)
  private TextView _moonsText;
  @InjectView(R.id.calendar_text_selectedDay)
  private TextView _selectedDayText;

  @InjectView(R.id.calendar_year_slider)
  private InfiniteSlider _yearSlider;
  @InjectView(R.id.calendar_month_slider)
  private InfiniteSlider _monthSlider;
  public CalendarSliders _calendarSlider;
  private List<MoonEvent> _moonEvents;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.calendar_layout);
    _calendarView.setOnDayClickListener(new OnDayClickListener() {
      @Override
      public void onClick(TextView view, int day) {
        String month = new DateFormatSymbols().getMonths()[_selectedYearMonth.get(Calendar.MONTH)];
        _selectedDayText.setText(day + " " + month + " :");
      }
    });

    _calendarSlider = createCalendarSlider(MoonUtil.newCalender(System.currentTimeMillis()));
  }

  private CalendarSliders createCalendarSlider(Calendar date) {
    return new CalendarSliders(this, _yearSlider, _monthSlider, date) {
      @Override
      protected void onDateChange(Calendar newDate) {
        Log.d("calendar", "onDateChange: " + _selectedYearMonth.getTime() + " / " + newDate.getTime());
        if (Util.equalsInFields(newDate, _selectedYearMonth, Calendar.YEAR, Calendar.MONTH)) {
          return;
        }
        Util.copyFields(newDate, _selectedYearMonth, Calendar.YEAR, Calendar.MONTH);
        _calendarView.update(_selectedYearMonth);
        _selectedDayText.setText("");
        _moonEvents = _moontimeService.getNextMoonEvents(_selectedYearMonth.getTimeInMillis(), 3);
        StringBuilder builder = new StringBuilder();
        for (Iterator<MoonEvent> iterator = _moonEvents.iterator(); iterator.hasNext();) {
          MoonEvent moonEvent = (MoonEvent) iterator.next();
          if (moonEvent.getDate().getMonth() == _selectedYearMonth.get(Calendar.MONTH)) {
            TextView dayView = _calendarView.getDayView(moonEvent.getDate().getDate());
            dayView.setBackgroundResource(R.color.calendar_event_day_background);
            String eventName;
            if (moonEvent.getType() == MoonEventType.FULL_MOON) {
              eventName = moonEvent.getType().getDisplayName() + ":  ";
              dayView.setText(dayView.getText() + " / FM");
            } else {
              eventName = moonEvent.getType().getDisplayName() + ": ";
              dayView.setText(dayView.getText() + " / NM");

            }
            builder.append(eventName + _datePattern.format(moonEvent.getDate()));
            builder.append("\n");
          }
        }
        _moonsText.setText(builder);
      }
    };
  }

  public void setCurrentDate(Calendar calendar) {
    _calendarSlider = createCalendarSlider(calendar);
  }

  @Override
  protected void onResume() {
    setTitle("Calendar");
    super.onResume();
  }

}
