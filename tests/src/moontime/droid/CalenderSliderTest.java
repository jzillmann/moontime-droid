package moontime.droid;

import java.util.Calendar;

import moontime.MoonUtil;
import moontime.droid.CalenderSlider.InfiniteMonthAdapter;
import moontime.droid.CalenderSlider.InfiniteYearAdapter;
import moontime.droid.util.Util;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

//@RunWith(RobolectricTestRunner.class)
public class CalenderSliderTest extends ActivityInstrumentationTestCase2<CalenderActivity> {

  public CalenderSliderTest() {
    super(CalenderActivity.class.getPackage().getName(), CalenderActivity.class);
  }

  @SmallTest
  public void testYearSlider() throws Exception {
    Intent openEvent = new Intent();
    openEvent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1);
    setActivityIntent(openEvent);

    CalenderActivity activity = getActivity();
    CalenderSlider calenderSlider = activity._calenderSlider;
    InfiniteMonthAdapter monthAdapter = calenderSlider._monthAdapter;
    InfiniteYearAdapter yearAdapter = calenderSlider._yearAdapter;

    Calendar currentTime = MoonUtil.newCalender(System.currentTimeMillis());

    assertEquals(currentTime.get(Calendar.YEAR) + "",
        yearAdapter.getItem((int) calenderSlider._yearSlider.getSelectedItemId()));
    assertEquals(Util.MONTH_SHORT_NAMES.get(currentTime.get(Calendar.MONTH)) + "",
        monthAdapter.getItem((int) calenderSlider._monthSlider.getSelectedItemId()));

    // currentTime.set(Calendar.YEAR, 2010);
    // currentTime.set(Calendar.MONTH, 3);
    // assertEquals("2010", yearAdapter.getItem((int)
    // calenderSlider._yearSlider.getSelectedItemId()));
    // assertEquals("Apr", monthAdapter.getItem((int)
    // calenderSlider._monthSlider.getSelectedItemId()));
    System.out.println("SliderTest.testIt()");
  }

}
