package moontime.droid;

import java.util.Calendar;

import moontime.droid.InfiniteSlider.InfiniteAdapter;
import moontime.droid.util.Util;
import moontime.droid.util.YearMonthAdapterPositioning;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public abstract class CalenderSlider {

  final InfiniteSlider _yearSlider;
  final InfiniteSlider _monthSlider;
  final Context _context;
  final InfiniteMonthAdapter _monthAdapter;
  final InfiniteYearAdapter _yearAdapter;
  final Calendar _currentDate;

  public CalenderSlider(Context context, InfiniteSlider yearSlider, InfiniteSlider monthSlider, Calendar initDate) {
    _currentDate = initDate;
    int initialYear = _currentDate.get(Calendar.YEAR);
    int initialMonth = _currentDate.get(Calendar.MONTH);
    YearMonthAdapterPositioning positioning = new YearMonthAdapterPositioning(initialYear, initialMonth,
        InfiniteAdapter.MID_POSITION, InfiniteAdapter.MID_POSITION);

    _context = context;
    _yearSlider = yearSlider;
    _monthSlider = monthSlider;
    _yearAdapter = new InfiniteYearAdapter(this, positioning);
    _yearSlider.init(_yearAdapter);
    _yearSlider.setCallbackDuringFling(false);
    _yearSlider.setOnItemSelectedListener(_yearAdapter);
    _yearSlider.setSelection(positioning.getCurrentYearPosition());

    _monthAdapter = new InfiniteMonthAdapter(this, positioning);
    _monthSlider.init(_monthAdapter);
    _monthSlider.setCallbackDuringFling(false);
    _monthSlider.setOnItemSelectedListener(_monthAdapter);
    _monthSlider.setSelection(positioning.getCurrentMonthPosition());
  }

  public Context getContext() {
    return _context;
  }

  public void onDateChange(YearMonthAdapterPositioning adapterPositioning) {
    Log.d("calender", adapterPositioning.getCurrentYear() + ", " + adapterPositioning.getCurrentMonth());
    _yearSlider.setSelection(adapterPositioning.getCurrentYearPosition());
    _monthSlider.setSelection(adapterPositioning.getCurrentMonthPosition());
    _currentDate.set(Calendar.YEAR, adapterPositioning.getCurrentYear());
    _currentDate.set(Calendar.MONTH, adapterPositioning.getCurrentMonth());
    onDateChange(_currentDate);
  }

  protected abstract void onDateChange(Calendar date);

  static class InfiniteYearAdapter extends InfiniteAdapter implements OnItemSelectedListener {

    private final CalenderSlider _calenderSlider;
    private YearMonthAdapterPositioning _adapterPositioning;

    public InfiniteYearAdapter(CalenderSlider calenderSlider, YearMonthAdapterPositioning adapterPositioning) {
      super(calenderSlider.getContext());
      _calenderSlider = calenderSlider;
      _adapterPositioning = adapterPositioning;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      _adapterPositioning.setCurrentYearPosition(position);
      _calenderSlider.onDateChange(_adapterPositioning);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
      // nothing todo
    }

    @Override
    public String getItem(int position) {
      return Integer.toString(_adapterPositioning.getYear(position));
    }

  }

  static class InfiniteMonthAdapter extends InfiniteAdapter implements OnItemSelectedListener {

    private final YearMonthAdapterPositioning _adapterPositioning;
    private final CalenderSlider _calenderSlider;

    public InfiniteMonthAdapter(CalenderSlider calenderSlider, YearMonthAdapterPositioning adapterPositioning) {
      super(calenderSlider.getContext());
      _calenderSlider = calenderSlider;
      _adapterPositioning = adapterPositioning;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      _adapterPositioning.setCurrentMonthPosition(position);
      _calenderSlider.onDateChange(_adapterPositioning);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
      // nothing todo

    }

    @Override
    public String getItem(int position) {
      int index = _adapterPositioning.getMonth(position);
      return Util.MONTH_SHORT_NAMES.get(index);
    }

  }

}
