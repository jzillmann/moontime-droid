package moontime.droid.store;

import java.io.IOException;
import java.util.List;

import moontime.droid.Reminder;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.introspect.VisibilityChecker;
import org.codehaus.jackson.type.TypeReference;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GlobalPreferences {

  public static final String GLOBAL_PREFERENCES = "moontime.droid.preferences";
  private static final String PREFERENCE_ENTRY_REMINDERS = "reminders";

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

  public List<Reminder> getReminders() {
    return this.<List<Reminder>> loadFromJson(PREFERENCE_ENTRY_REMINDERS, "[]", new TypeReference<List<Reminder>>() {
    });
  }

  public void addReminder(Reminder reminder) {
    List<Reminder> reminders = getReminders();
    reminders.add(reminder);
    storeAsJson(PREFERENCE_ENTRY_REMINDERS, reminders);
  }

  public void removeReminder(int position) {
    List<Reminder> reminders = getReminders();
    reminders.remove(position);
    storeAsJson(PREFERENCE_ENTRY_REMINDERS, reminders);
  }

  public void saveReminders(List<Reminder> reminders) {
    storeAsJson(PREFERENCE_ENTRY_REMINDERS, reminders);
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

  private <T> T loadFromJson(String preferenceKey, String defaultValue, TypeReference<T> typeReference) {
    try {
      String prefValue = _preferences.getString(preferenceKey, defaultValue);
      return _objectMapper.readValue(prefValue, typeReference);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
