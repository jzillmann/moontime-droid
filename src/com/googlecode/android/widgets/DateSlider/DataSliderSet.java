package com.googlecode.android.widgets.dateslider;

import java.util.Calendar;
import java.util.List;

import moontime.MoonUtil;

import com.google.inject.internal.Lists;

public abstract class DataSliderSet {

  private final List<ScrollLayout> _sliders = Lists.newArrayList();
  private final Calendar _currentTime = MoonUtil.newCalender(System.currentTimeMillis());

  public void addSlider(final ScrollLayout slider, Labeler labeler, int width, int height) {
    _sliders.add(slider);
    slider.setLabeler(labeler, _currentTime.getTimeInMillis(), width, height);
    slider.setOnScrollListener(new ScrollLayout.OnScrollListener() {
      @Override
      public void onScroll(long x) {
        _currentTime.setTimeInMillis(x);
        updateOtherElements(slider);
      }
    });
  }

  public void init() {
    onDateChange(_currentTime);
    updateOtherElements(null);
  };

  public void updateOtherElements(ScrollLayout source) {
    onDateChange(_currentTime);
    for (ScrollLayout scroller : _sliders) {
      if (scroller != source) {
        System.out.println("DataSliderSet.updateOtherElements()" + scroller + " _ " + _currentTime.getTime());
        scroller.setTime(_currentTime.getTimeInMillis(), 0);
        scroller.startLayoutAnimation();
      }
    }
  }

  protected abstract void onDateChange(Calendar currentTime);

  public static class YearLabeler extends Labeler {

    @Override
    public TimeObject add(long time, int val) {
      Calendar c = MoonUtil.newCalender(time);
      c.add(Calendar.YEAR, val);
      return timeObjectfromCalendar(c);
    }

    /**
     * creates an TimeObject from a CalendarInstance
     */
    @Override
    protected TimeObject timeObjectfromCalendar(Calendar c) {
      int year = c.get(Calendar.YEAR);
      System.out.println("DataSliderSet.YearLabeler.timeObjectfromCalendar()" + this + ": " + year);
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

  public static class MonthLabeler extends Labeler {
    @Override
    public TimeObject add(long time, int val) {
      Calendar c = MoonUtil.newCalender(time);
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
  }

}
