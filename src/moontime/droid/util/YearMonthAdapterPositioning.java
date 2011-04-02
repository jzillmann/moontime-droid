package moontime.droid.util;

public class YearMonthAdapterPositioning {

  private final int _initialYear;
  private final int _initialMonth;
  private final int _initialYearPosition;
  private final int _initialMonthPosition;
  private int _currentYearPosition;
  private int _currentMonthPosition;

  public YearMonthAdapterPositioning(int initialYear, int initialMonth, int initialYearPosition,
      int initialMonthPosition) {
    _initialYear = initialYear;
    _initialMonth = initialMonth;
    _initialYearPosition = initialYearPosition;
    _initialMonthPosition = initialMonthPosition;
    _currentYearPosition = initialYearPosition;
    _currentMonthPosition = initialMonthPosition;
  }

  public int getInitialYearPosition() {
    return _initialYearPosition;
  }

  public int getInitialMonthPosition() {
    return _initialMonthPosition;
  }

  public void setCurrentYearPosition(int yearPosition) {
    int yearDiff = yearPosition - _currentYearPosition;
    _currentMonthPosition += yearDiff * 12;
    _currentYearPosition = yearPosition;
  }

  public int getCurrentYearPosition() {
    return _currentYearPosition;
  }

  public void setCurrentMonthPosition(int currentMonthPosition) {
    int monthDiff = currentMonthPosition - _currentMonthPosition;
    int additionalYears = 0;
    if (getCurrentMonth() + monthDiff < 0) {
      additionalYears = Math.min(-1, monthDiff / 12);
    } else if (getCurrentMonth() + monthDiff > 11) {
      additionalYears = Math.max(1, monthDiff / 12);
    }
    _currentYearPosition += additionalYears;
    _currentMonthPosition = currentMonthPosition;
  }

  public int getCurrentMonthPosition() {
    return _currentMonthPosition;
  }

  public int getCurrentYear() {
    return getYear(_currentYearPosition);
  }

  public int getYear(int position) {
    int yearDiff = position - _initialYearPosition;
    return _initialYear + yearDiff;
  }

  public int getCurrentMonth() {
    return getMonth(_currentMonthPosition);
  }

  public int getMonth(int position) {
    int monthDiff = position - _initialMonthPosition;
    int diffModulo = (_initialMonth + monthDiff) % 12;
    if (monthDiff < 0) {
      if (diffModulo == 0) {
        return 0;
      }
      return 12 - Math.abs(diffModulo);
    }
    return diffModulo;
  }

}
