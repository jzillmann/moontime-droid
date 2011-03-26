package moontime.droid;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import moontime.MoonEvent;
import moontime.MoonUtil;
import moontime.droid.service.MoontimeService;
import moontime.droid.store.WidgetPreferences;
import moontime.droid.util.Util;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;
import android.appwidget.AppWidgetManager;
import android.os.Bundle;
import android.widget.TextView;

import com.google.inject.Inject;
import com.googlecode.android.widgets.DateSlider.DataSliderSet;
import com.googlecode.android.widgets.DateSlider.DataSliderSet.MonthLabeler;
import com.googlecode.android.widgets.DateSlider.DataSliderSet.YearLabeler;
import com.googlecode.android.widgets.DateSlider.ScrollLayout;

public class CalenderActivity extends RoboActivity {

  @Inject
  protected MoontimeService _moontimeService;
  protected final Calendar _selectedYearMontCalender = MoonUtil.newCalender(0);
  private DateFormat _datePattern;

  @InjectExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)
  protected int _widgetId;
  @InjectView(R.id.sliderYear)
  protected ScrollLayout _yearSlider;
  @InjectView(R.id.sliderMonth)
  protected ScrollLayout _monthSlider;
  @InjectView(R.id.calender_text_moons)
  private TextView _moonsText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.calender_layout);

    WidgetPreferences preferences = WidgetPreferences.initFromPreferences(this, _widgetId);
    _datePattern = new SimpleDateFormat(preferences.getDatePattern());

    Util.resetFields(_selectedYearMontCalender, Calendar.DATE, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND);
    _dataSliderSet.addSlider(_yearSlider, new YearLabeler(), 200, 60);
    _dataSliderSet.addSlider(_monthSlider, new MonthLabeler(), 150, 60);
    _dataSliderSet.init();
  }

  @Override
  protected void onResume() {
    setTitle("Calender");
    super.onResume();
  }

  private DataSliderSet _dataSliderSet = new DataSliderSet() {

    @Override
    protected void onDateChange(Calendar currentTime) {
      if (Util.equalsInFields(currentTime, _selectedYearMontCalender, Calendar.YEAR, Calendar.MONTH)) {
        return;
      }
      Util.copyFields(currentTime, _selectedYearMontCalender, Calendar.YEAR, Calendar.MONTH);
      List<MoonEvent> moonEvents = _moontimeService.getNextMoonEvents(_selectedYearMontCalender.getTimeInMillis(), 3);
      StringBuilder builder = new StringBuilder();
      for (Iterator<MoonEvent> iterator = moonEvents.iterator(); iterator.hasNext();) {
        MoonEvent moonEvent = (MoonEvent) iterator.next();
        if (moonEvent.getDate().getMonth() == _selectedYearMontCalender.get(Calendar.MONTH)) {
          builder.append(moonEvent.getType().getDisplayName() + ": " + _datePattern.format(moonEvent.getDate()));
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
