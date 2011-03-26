package moontime.droid;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import moontime.MoonEvent;
import moontime.MoonUtil;
import moontime.droid.store.WidgetPreferences;
import moontime.droid.util.Util;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;
import android.appwidget.AppWidgetManager;
import android.os.Bundle;
import android.widget.TextView;

import com.google.inject.Inject;
import com.googlecode.android.widgets.DateSlider.DateSlider.Labeler;
import com.googlecode.android.widgets.DateSlider.DateSlider.TimeObject;
import com.googlecode.android.widgets.DateSlider.ScrollLayout;

public class CalenderActivity extends RoboActivity {

  @Inject
  protected MoontimeService _moontimeService;
  protected List<ScrollLayout> mScrollerList = new ArrayList<ScrollLayout>();
  protected Calendar _currentTime = MoonUtil.newCalender(System.currentTimeMillis());
  protected Calendar _selectedYearMontCalender = MoonUtil.newCalender(0);
  private DateFormat _datePattern;

  @InjectExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)
  protected int _widgetId;
  @InjectView(R.id.sliderYear)
  protected ScrollLayout mYearScroller;
  @InjectView(R.id.sliderMonth)
  protected ScrollLayout mMonthScroller;
  @InjectView(R.id.calender_text_moons)
  private TextView _moonsText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.calender_layout);

    WidgetPreferences preferences = WidgetPreferences.initFromPreferences(this, _widgetId);
    _datePattern = new SimpleDateFormat(preferences.getDatePattern());

    Util.resetFields(_selectedYearMontCalender, Calendar.DATE, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND);
    mYearScroller.setLabeler(yearLabeler, _currentTime.getTimeInMillis(), 200, 60);
    mMonthScroller.setLabeler(monthLabeler, _currentTime.getTimeInMillis(), 150, 60);
    mScrollerList.add(mYearScroller);
    mScrollerList.add(mMonthScroller);
    setListeners();
    updateMoons();
  }

  // private DateSlider.OnDateSetListener mMonthYearSetListener = new
  // DateSlider.OnDateSetListener() {
  // @Override
  // public void onDateSet(DateSlider view, Calendar selectedDate) {
  // // update the dateText view with the corresponding date
  // // TODO _moonsText.setText(String.format("The chosen date:%n%tB %tY",
  // // selectedDate, selectedDate));
  // }
  // };

  /**
   * Sets the Scroll listeners for all ScrollLayouts in "mScrollerList"
   */
  protected void setListeners() {
    for (final ScrollLayout sl : mScrollerList) {
      sl.setOnScrollListener(new ScrollLayout.OnScrollListener() {
        @Override
        public void onScroll(long x) {
          _currentTime.setTimeInMillis(x);
          arrangeScroller(sl);
        }
      });
    }

  }

  protected void arrangeScroller(ScrollLayout source) {
    updateMoons();
    if (source != null) {
      for (ScrollLayout scroller : mScrollerList) {
        if (scroller == source) {
          continue;
        }
        scroller.setTime(_currentTime.getTimeInMillis(), 0);
      }
    }
  }

  protected void updateMoons() {
    if (!Util.equalsInFields(_currentTime, _selectedYearMontCalender, Calendar.YEAR, Calendar.MONTH)) {
      Util.copyFields(_currentTime, _selectedYearMontCalender, Calendar.YEAR, Calendar.MONTH);
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
  }

  // the year labeler takes care of providing each TimeTextView element in the
  // yearScroller
  // with the right label and information about its time representation
  protected static Labeler yearLabeler = new Labeler() {

    /**
     * add "val" year to the month object that contains "time" and returns the
     * new TimeObject
     */
    @Override
    public TimeObject add(long time, int val) {
      Calendar c = Calendar.getInstance();
      c.setTimeInMillis(time);
      c.add(Calendar.YEAR, val);
      return timeObjectfromCalendar(c);
    }

    /**
     * creates an TimeObject from a CalendarInstance
     */
    @Override
    protected TimeObject timeObjectfromCalendar(Calendar c) {
      int year = c.get(Calendar.YEAR);
      // set calendar to first millisecond of the year
      c.set(year, 0, 1, 0, 0, 0);
      c.set(Calendar.MILLISECOND, 0);
      long startTime = c.getTimeInMillis();
      // set calendar to last millisecond of the year
      c.set(year, 11, 31, 23, 59, 59);
      c.set(Calendar.MILLISECOND, 999);
      long endTime = c.getTimeInMillis();
      return new TimeObject(String.valueOf(year), startTime, endTime);
    }

  };

  // the month labeler takes care of providing each TimeTextView element in the
  // monthScroller
  // with the right label and information about its time representation
  protected Labeler monthLabeler = new Labeler() {

    /**
     * add "val" months to the month object that contains "time" and returns the
     * new TimeObject
     */
    @Override
    public TimeObject add(long time, int val) {
      Calendar c = Calendar.getInstance();
      c.setTimeInMillis(time);
      c.add(Calendar.MONTH, val);
      return timeObjectfromCalendar(c);
    }

    /**
     * creates an TimeObject from a CalendarInstance
     */
    @Override
    protected TimeObject timeObjectfromCalendar(Calendar c) {
      int year = c.get(Calendar.YEAR);
      int month = c.get(Calendar.MONTH);
      // set calendar to first millisecond of the month
      c.set(year, month, 1, 0, 0, 0);
      c.set(Calendar.MILLISECOND, 0);
      long startTime = c.getTimeInMillis();
      // set calendar to last millisecond of the month
      c.set(year, month, c.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
      c.set(Calendar.MILLISECOND, 999);
      long endTime = c.getTimeInMillis();
      return new TimeObject(String.format("%tB", c), startTime, endTime);
    }

  };

}
