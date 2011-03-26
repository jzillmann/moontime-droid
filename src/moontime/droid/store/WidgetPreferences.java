package moontime.droid.store;

import moontime.droid.R;
import moontime.droid.WidgetConfigurationActivity;
import moontime.droid.entity.WidgetTheme;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class WidgetPreferences {

  private static final String PREFS_PREFIX = "moontime.droid.widget-preferences";
  private final int _widgetId;
  private String _datePattern;
  private WidgetTheme _theme;

  private WidgetPreferences(int widgetId) {
    _widgetId = widgetId;
  }

  public int getWidgetId() {
    return _widgetId;
  }

  public String getDatePattern() {
    return _datePattern;
  }

  public WidgetTheme getTheme() {
    return _theme;
  }

  public String getPreferencesKey() {
    return PREFS_PREFIX + "." + getWidgetId();
  }

  public void storeToPreferences(Context context) {
    SharedPreferences preferences = context.getSharedPreferences(getPreferencesKey(), Context.MODE_PRIVATE);
    Editor editor = preferences.edit();
    editor.putString("datePatern", _datePattern);
    editor.putString("theme", _theme.toString());
    editor.commit();
    Log.i("config", "stored preferences for widget '" + getWidgetId() + "' to '" + getPreferencesKey() + "'");
  }

  public static WidgetPreferences initFromView(WidgetConfigurationActivity activity) {
    WidgetPreferences widgetPreferences = new WidgetPreferences(activity.getWidgetId());
    widgetPreferences._datePattern = activity.getDatePatternText().getText().toString();
    widgetPreferences._theme = (WidgetTheme) activity.getThemeSpinner().getSelectedItem();
    return widgetPreferences;
  }

  public static WidgetPreferences initFromPreferences(Context context, int widgetId) {
    WidgetPreferences widgetPreferences = new WidgetPreferences(widgetId);
    SharedPreferences preferences = context.getSharedPreferences(widgetPreferences.getPreferencesKey(),
        Context.MODE_PRIVATE);
    widgetPreferences._datePattern = preferences.getString("datePattern",
        context.getString(R.string.conf_default_datePattern));
    widgetPreferences._theme = WidgetTheme.valueOf(preferences.getString("theme", WidgetTheme.DEFAULT.name()));
    return widgetPreferences;
  }

}
