package moontime.droid.util;

public class Util {

  public static long hoursToMillis(int hours) {
    return hours * 1000 * 60 * 60;
  }

  public static String wrap(String string, String tag) {
    return String.format("<%2$s>%0$s</%2$s>", string, tag);
  }

}
