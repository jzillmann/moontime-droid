package moontime.droid;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.inject.internal.Lists;

public class CalendarView extends TableLayout implements OnClickListener {

  private List<DayView> _dayViews = Lists.newArrayList();
  private OnDayClickListener _onDayClickListener;
  private Calendar _currentMonthYearToView;
  private DayView _selectedDay;

  public CalendarView(Context context) {
    super(context);
  }

  public CalendarView(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedArray styleAttributes = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);

    createHeaderRow(context, styleAttributes);
    createWeekRows(context, styleAttributes, generateLayoutParams(attrs));
  }

  public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
    _onDayClickListener = onDayClickListener;
  }

  public void update(Calendar monthYearToView) {
    _currentMonthYearToView = monthYearToView;
    int lastDay = monthYearToView.getActualMaximum(Calendar.DAY_OF_MONTH);
    int currentMonthDay = 1;
    for (int i = 0; i < _dayViews.size(); i++) {
      DayView dayView = _dayViews.get(i);
      dayView.clear();
      if (currentMonthDay == 1 && i + 1 < monthYearToView.get(Calendar.DAY_OF_WEEK)) {
        // before the month starts
      } else if (currentMonthDay > lastDay) {
        // after the month ends
      } else {
        dayView.setDay(currentMonthDay);
        currentMonthDay++;
      }
    }
  }

  private void createHeaderRow(Context context, TypedArray styleAttributes) {
    String[] shortWeekdays = new DateFormatSymbols().getShortWeekdays();
    TableRow headerRow = createTableRow(context);
    for (int i = 1; i < shortWeekdays.length; i++) {
      TextView textView = createHeaderView(context,
          styleAttributes.getColor(R.styleable.CalendarView_background_header, Color.BLACK));
      textView.setText(shortWeekdays[i]);
      headerRow.addView(textView);
    }
    addView(headerRow);
  }

  public TextView getDayView(int day) {
    for (int i = day - 1; i < _dayViews.size(); i++) {
      if (_dayViews.get(i).getDay() == day) {
        return _dayViews.get(i);
      }
    }
    throw new IllegalArgumentException("no day '" + day + "' found in calendar for " + _currentMonthYearToView);
  }

  private void createWeekRows(Context context, TypedArray styleAttributes, LayoutParams layoutParams) {
    layoutParams.setMargins(3, 3, 3, 3);
    int backgroundColor = styleAttributes.getColor(R.styleable.CalendarView_background_rows, Color.BLACK);
    int backgroundSelectedColor = styleAttributes
        .getColor(R.styleable.CalendarView_background_selectedDay, Color.BLACK);
    for (int week = 0; week < 6; week++) {
      TableRow tableRow = createTableRow(context);
      for (int weekDay = 0; weekDay < 7; weekDay++) {
        DayView textView = createDayView(context, backgroundColor, backgroundSelectedColor);
        tableRow.addView(textView);
        _dayViews.add(textView);
      }
      addView(tableRow);
    }
  }

  private TableRow createTableRow(Context context) {
    TableRow row = new TableRow(context);
    row.setGravity(Gravity.CENTER_HORIZONTAL);
    LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
    layoutParams.setMargins(0, 1, 0, 0);
    row.setLayoutParams(layoutParams);
    return row;
  }

  private TextView createHeaderView(Context context, int backgroundColor) {
    TextView textView = new TextView(context);
    textView.setGravity(Gravity.CENTER);
    textView.setBackgroundColor(backgroundColor);
    return textView;
  }

  private DayView createDayView(Context context, int backgroundColor, int backgroundSelectedColor) {
    DayView dayView = new DayView(context, backgroundColor, backgroundSelectedColor);
    dayView.setGravity(Gravity.CENTER);
    dayView.setOnClickListener(this);
    return dayView;
  }

  @Override
  public void onClick(View v) {
    DayView dayView = (DayView) v;
    if (dayView.getDay() < 1) {
      return;
    }
    if (_selectedDay != null) {
      _selectedDay.deselect();
    }
    _selectedDay = dayView;
    _selectedDay.select();
    if (_onDayClickListener != null) {
      _onDayClickListener.onClick(dayView, dayView.getDay());
    }
  }

  public static interface OnDayClickListener {
    public void onClick(TextView view, int day);
  }

  private static class DayView extends TextView {

    private final int _backgroundDefaultColor;
    private final int _backgroundSelectedColor;
    private Drawable _backgroundBeforeSelected;;
    private int _day;

    public DayView(Context context, int backgroundColor, int selectedColor) {
      super(context);
      _backgroundDefaultColor = backgroundColor;
      _backgroundSelectedColor = selectedColor;
      setBackgroundColor(backgroundColor);
    }

    public void clear() {
      setDay(-1);
      setText(" ");
      setBackgroundColor(_backgroundDefaultColor);
    }

    public int getDay() {
      return _day;
    }

    public void setDay(int day) {
      _day = day;
      setText(Integer.toString(day));
    }

    public void select() {
      _backgroundBeforeSelected = getBackground();
      setBackgroundColor(_backgroundSelectedColor);
    }

    public void deselect() {
      setBackgroundDrawable(_backgroundBeforeSelected);
      _backgroundBeforeSelected = null;
    }

  }
}
