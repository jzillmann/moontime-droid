package moontime.droid.util;

import java.util.Calendar;

public class Util {

  public static long hoursToMillis(int hours) {
    return hours * 1000 * 60 * 60;
  }

  public static void resetFields(Calendar calendar, int... fields) {
    for (int field : fields) {
      if (field == Calendar.DATE || field == Calendar.DAY_OF_MONTH) {
        calendar.set(field, 1);
      } else {
        calendar.set(field, 0);
      }
    }
  }

  public static boolean equalsInFields(Calendar calendar1, Calendar calendar2, int... fields) {
    for (int field : fields) {
      if (calendar1.get(field) != calendar2.get(field)) {
        return false;
      }
    }
    return true;
  }

  public static void copyFields(Calendar calendarFrom, Calendar calendarTo, int... fields) {
    for (int field : fields) {
      calendarTo.set(field, calendarFrom.get(field));
    }
  }

  public static String wrap(String string, String tag) {
    return String.format("<%2$s>%0$s</%2$s>", string, tag);
  }

}
