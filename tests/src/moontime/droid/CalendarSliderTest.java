package moontime.droid;

import java.util.Calendar;

import moontime.MoonUtil;
import moontime.droid.CalendarSliders.InfiniteMonthAdapter;
import moontime.droid.CalendarSliders.InfiniteYearAdapter;
import moontime.droid.util.Util;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

//@RunWith(RobolectricTestRunner.class)
public class CalendarSliderTest extends
		ActivityInstrumentationTestCase2<CalendarActivity> {

	public CalendarSliderTest() {
		super(CalendarActivity.class.getPackage().getName(),
				CalendarActivity.class);
	}

	@SmallTest
	public void testYearSlider() throws Exception {
		Intent openEvent = new Intent();
		openEvent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1);
		setActivityIntent(openEvent);

		CalendarActivity activity = getActivity();
		CalendarSliders calendarSlider = activity._calendarSlider;
		InfiniteMonthAdapter monthAdapter = calendarSlider._monthAdapter;
		InfiniteYearAdapter yearAdapter = calendarSlider._yearAdapter;

		Calendar currentTime = MoonUtil.newCalender(System.currentTimeMillis());

		assertEquals(currentTime.get(Calendar.YEAR) + "",
				yearAdapter.getItem((int) calendarSlider._yearSlider
						.getSelectedItemId()));
		assertEquals(
				Util.MONTH_SHORT_NAMES.get(currentTime.get(Calendar.MONTH))
						+ "",
				monthAdapter.getItem((int) calendarSlider._monthSlider
						.getSelectedItemId()));

		// currentTime.set(Calendar.YEAR, 2010);
		// currentTime.set(Calendar.MONTH, 3);
		// assertEquals("2010", yearAdapter.getItem((int)
		// calendarSlider._yearSlider.getSelectedItemId()));
		// assertEquals("Apr", monthAdapter.getItem((int)
		// calendarSlider._monthSlider.getSelectedItemId()));
		System.out.println("SliderTest.testIt()");
	}

}
