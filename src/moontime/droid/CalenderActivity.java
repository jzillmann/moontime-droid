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
import moontime.droid.CalenderView.OnDayClickListener;
import moontime.droid.service.MoontimeService;
import moontime.droid.util.Util;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;
import android.appwidget.AppWidgetManager;
import android.os.Bundle;
import android.widget.TextView;

import com.google.inject.Inject;
import com.googlecode.android.widgets.dateslider.DataSliderSet;
import com.googlecode.android.widgets.dateslider.DataSliderSet.MonthLabeler;
import com.googlecode.android.widgets.dateslider.DataSliderSet.YearLabeler;
import com.googlecode.android.widgets.dateslider.ScrollLayout;

public class CalenderActivity extends RoboActivity {

  @Inject
  protected MoontimeService _moontimeService;
  protected final Calendar _selectedYearMonth = MoonUtil.newCalender(0);
  private DateFormat _datePattern = new SimpleDateFormat("dd - HH:mm");

  @InjectExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)
  protected int _widgetId;
  @InjectView(R.id.sliderYear)
  protected ScrollLayout _yearSlider;
  @InjectView(R.id.sliderMonth)
  protected ScrollLayout _monthSlider;
  @InjectView(R.id.calender_calenderView)
  private CalenderView _calenderView;
  @InjectView(R.id.calender_text_moons)
  private TextView _moonsText;
  @InjectView(R.id.calender_text_selectedDay)
  private TextView _selectedDayText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.calender_layout);
    updateSelectedDate(_selectedYearMonth);
    _calenderView.setOnDayClickListener(new OnDayClickListener() {
      @Override
      public void onClick(TextView view, int day) {
        String month = new DateFormatSymbols().getMonths()[_selectedYearMonth.get(Calendar.MONTH)];
        _selectedDayText.setText(day + " " + month + " :");
      }
    });

    _dataSliderSet.addSlider(_yearSlider, new YearLabeler(), 200, 60);
    _dataSliderSet.addSlider(_monthSlider, new MonthLabeler(), 150, 60);
    _dataSliderSet.init();

  }

  @Override
  protected void onResume() {
    setTitle("Calender");
    super.onResume();
  }

  private void updateSelectedDate(Calendar currentTime) {
    Util.copyFields(currentTime, _selectedYearMonth, Calendar.YEAR, Calendar.MONTH);
    _calenderView.update(_selectedYearMonth);
  }

  private List<MoonEvent> _moonEvents;
  private DataSliderSet _dataSliderSet = new DataSliderSet() {

    @Override
    protected void onDateChange(Calendar currentTime) {
      if (Util.equalsInFields(currentTime, _selectedYearMonth, Calendar.YEAR, Calendar.MONTH)) {
        return;
      }
      updateSelectedDate(currentTime);
      _selectedDayText.setText("");
      _moonEvents = _moontimeService.getNextMoonEvents(_selectedYearMonth.getTimeInMillis(), 3);
      StringBuilder builder = new StringBuilder();
      for (Iterator<MoonEvent> iterator = _moonEvents.iterator(); iterator.hasNext();) {
        MoonEvent moonEvent = (MoonEvent) iterator.next();
        if (moonEvent.getDate().getMonth() == _selectedYearMonth.get(Calendar.MONTH)) {
          TextView dayView = _calenderView.getDayView(moonEvent.getDate().getDate());
          dayView.setBackgroundResource(R.color.calender_event_day_background);
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

  // private DateSlider.OnDateSetListener mMonthYearSetListener = new
  // DateSlider.OnDateSetListener() {
  // @Override
  // public void onDateSet(DateSlider view, Calendar selectedDate) {
  // // update the dateText view with the corresponding date
  // // TODO _moonsText.setText(String.format("The chosen date:%n%tB %tY",
  // // selectedDate, selectedDate));
  // }
  // };

}
