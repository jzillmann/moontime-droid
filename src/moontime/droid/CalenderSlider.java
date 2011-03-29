package moontime.droid;

import java.util.Calendar;

import moontime.MoonUtil;
import moontime.droid.InfiniteSlider.InfiniteAdapter;
import moontime.droid.util.Util;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

public abstract class CalenderSlider {

  private final InfiniteSlider _yearSlider;
  private final InfiniteSlider _monthSlider;
  private final Context _context;
  private int _lastSelectedMonthPosition;
  private InfiniteMonthAdapter _monthAdapter;
  private Calendar _currentDate;

  public CalenderSlider(Context context, InfiniteSlider yearSlider, InfiniteSlider monthSlider) {
    _currentDate = MoonUtil.newCalender(System.currentTimeMillis());
    _context = context;
    _yearSlider = yearSlider;
    _monthSlider = monthSlider;
    InfiniteYearAdapter yearAdapter = new InfiniteYearAdapter(this, _currentDate.get(Calendar.YEAR));
    _yearSlider.init(yearAdapter);
    _yearSlider.setCallbackDuringFling(false);
    _yearSlider.setOnItemSelectedListener(yearAdapter);
    _yearSlider.setSelection(_yearSlider.getCount() / 2);

    _monthAdapter = new InfiniteMonthAdapter(this, _currentDate.get(Calendar.YEAR), _currentDate.get(Calendar.MONTH));
    _monthSlider.init(_monthAdapter);
    _monthSlider.setCallbackDuringFling(false);
    _monthSlider.setOnItemSelectedListener(_monthAdapter);
    _lastSelectedMonthPosition = _monthSlider.getCount() / 2 + _currentDate.get(Calendar.MONTH);
    _monthSlider.setSelection(_lastSelectedMonthPosition);
  }

  public Context getContext() {
    return _context;
  }

  public void onDateChange(int calenderField, int newDateValue, int newPosition) {
    if (calenderField == Calendar.MONTH) {
      _currentDate.set(Calendar.MONTH, newDateValue);
      int year = _monthAdapter.getYear(_lastSelectedMonthPosition, newPosition);
      if (year == _currentDate.get(Calendar.YEAR)) {
        onDateChange(_currentDate);
      } else {
        int yearDiff = year - _currentDate.get(Calendar.YEAR);
        int currentYearPosition = _yearSlider.getPositionForView(_yearSlider.getSelectedView());
        _yearSlider.setSelection(currentYearPosition + yearDiff);
      }
    } else if (calenderField == Calendar.YEAR) {
      _currentDate.set(Calendar.YEAR, newDateValue);
      onDateChange(_currentDate);
    } else {
      throw new UnsupportedOperationException(calenderField + "");
    }

  }

  protected abstract void onDateChange(Calendar date);

  private static class InfiniteYearAdapter extends InfiniteCalenderAdapter {

    private final int _initialYear;

    public InfiniteYearAdapter(CalenderSlider calenderSlider, int initialYear) {
      super(calenderSlider, Calendar.YEAR);
      _initialYear = initialYear;
    }

    @Override
    protected String getItem(int midPosition, int position) {
      int difference = midPosition - position;
      return Integer.toString(_initialYear - difference);
    }

    @Override
    protected int getDateValue(TextView view) {
      return Integer.parseInt(view.getText().toString());
    }

  }

  private static class InfiniteMonthAdapter extends InfiniteCalenderAdapter {

    private final int _initialMonth;
    private final int _initialYear;

    public InfiniteMonthAdapter(CalenderSlider calenderSlider, int initialYear, int initialMonth) {
      super(calenderSlider, Calendar.MONTH);
      _initialMonth = initialMonth;
      _initialYear = initialYear;
    }

    @Override
    protected String getItem(int midPosition, int position) {
      int difference = Math.abs(midPosition - position);
      int index;
      if (position < midPosition) {
        int mod = difference % 12;
        if (mod == 0) {
          index = 0;
        } else {
          index = 12 - mod;
        }
      } else {
        index = difference % 12;
      }
      return Util.MONTH_SHORT_NAMES.get(index);
    }

    @Override
    protected int getDateValue(TextView view) {
      return Util.getMonth(view.getText().toString());
    }

    public int getYear(int oldMonthPosition, int newMonthPosition) {
      return _initialYear + getYearsChanged(newMonthPosition);
    }

    private int getYearsChanged(int newMonthPosition) {
      int midPosition = getCount() / 2;
      int diff = newMonthPosition - midPosition;
      return diff / (12 - _initialMonth + 1);
    }

  }

  private static abstract class InfiniteCalenderAdapter extends InfiniteAdapter implements OnItemSelectedListener {

    private final CalenderSlider _calenderSlider;
    private final int _calenderField;
    private final int _midPosition = getCount() / 2;

    public InfiniteCalenderAdapter(CalenderSlider calenderSlider, int calenderField) {
      super(calenderSlider.getContext());
      _calenderSlider = calenderSlider;
      _calenderField = calenderField;
    }

    @Override
    public final String getItem(int position) {
      return getItem(_midPosition, position);
    }

    protected abstract String getItem(int midPosition, int position);

    protected abstract int getDateValue(TextView view);

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      TextView textView = (TextView) view;
      int dateValue = getDateValue(textView);
      _calenderSlider.onDateChange(_calenderField, dateValue, position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
      // nothing todo
    }

  }
}
