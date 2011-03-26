package moontime.droid.store;

import java.io.IOException;
import java.util.List;

import moontime.MoonEvent;
import moontime.MoonEventType;
import moontime.droid.entity.Reminder;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.introspect.VisibilityChecker;
import org.codehaus.jackson.type.TypeReference;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GlobalPreferences {

  public static final String GLOBAL_PREFERENCES = "moontime.droid.preferences";

  private final ObjectMapper _objectMapper = new ObjectMapper();
  private final SharedPreferences _preferences;

  @Inject
  public GlobalPreferences(SharedPreferences preferences) {
    _preferences = preferences;
    _objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    _objectMapper.configure(DeserializationConfig.Feature.AUTO_DETECT_FIELDS, true);
    _objectMapper.setVisibilityChecker(VisibilityChecker.Std.defaultInstance().withFieldVisibility(
        JsonAutoDetect.Visibility.ANY));
  }

  private static String getRemindersKey(MoonEvent event) {
    return "reminders." + event.getType().name();
  }

  private static String getRemindersLastUpdated(MoonEventType eventType) {
    return "reminders." + eventType.name().toLowerCase() + ".last-updated";
  }

  public void resetLastReminderUpdate() {
    Editor editor = _preferences.edit();
    for (MoonEventType moonEventType : MoonEventType.values()) {
      editor.remove(getRemindersLastUpdated(moonEventType));
    }
    editor.commit();
  }

  public List<Reminder> getReminders(MoonEvent event, boolean checkExpiration) {
    List<Reminder> reminders = this.<List<Reminder>> loadFromJson(getRemindersKey(event), "[]",
        new TypeReference<List<Reminder>>() {
        });
    if (checkExpiration && !reminders.isEmpty()) {
      long eventTime = event.getDate().getTime();
      long lastUpdated = _preferences.getLong(getRemindersLastUpdated(event.getType()), eventTime);
      Log.d("debug", "check expiration: " + lastUpdated + " / " + eventTime);
      if (Math.abs(eventTime - lastUpdated) > 1000 * 60 * 60 * 24) {
        Log.d("debug", "expire checked reminders for " + event.getType().getDisplayName());
        for (Reminder reminder : reminders) {
          reminder.setChecked(false);
        }
      }
    }
    return reminders;
  }

  public void saveReminders(MoonEvent event, List<Reminder> reminders) {
    Editor editor = _preferences.edit();
    editor.putLong(getRemindersLastUpdated(event.getType()), event.getDate().getTime());
    editor.commit();
    storeAsJson(getRemindersKey(event), reminders);
  }

  private void storeAsJson(String prefKey, Object prefValue) {
    try {
      Editor editor = _preferences.edit();
      editor.putString(prefKey, _objectMapper.writeValueAsString(prefValue));
      editor.commit();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T loadFromJson(String preferenceKey, String defaultValue, TypeReference<T> typeReference) {
    try {
      String prefValue = _preferences.getString(preferenceKey, defaultValue);
      return (T) _objectMapper.readValue(prefValue, typeReference);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
