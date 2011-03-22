package moontime.droid;

import java.util.List;

import moontime.MoonEvent;
import moontime.droid.store.GlobalPreferences;
import roboguice.activity.RoboListActivity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.inject.Inject;

public class ReminderActivity extends RoboListActivity {

  @Inject
  protected MoontimeService _moontimeService;
  @Inject
  protected GlobalPreferences _globalPreferences;
  private MoonEvent _nextMoonEvent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.reminder_layout);
    _nextMoonEvent = _moontimeService.getNextMoonEvent();
    // TODO +/- hours to event ? in title
    setTitle(_nextMoonEvent.getType().getDisplayName() + " - Reminders");

    getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    List<Reminder> reminders = _globalPreferences.getReminders(_nextMoonEvent, true);
    setListAdapter(new ArrayAdapter<Reminder>(this, android.R.layout.simple_list_item_multiple_choice, reminders));
    for (int i = 0; i < reminders.size(); i++) {
      getListView().setItemChecked(i, reminders.get(i).isChecked());
    }
    registerForContextMenu(getListView());
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
    menu.setHeaderTitle(getListAdapter().getItem(info.position).getText());
    for (ReminderMenu reminderMenu : ReminderMenu.values()) {
      menu.add(Menu.NONE, reminderMenu.ordinal(), reminderMenu.ordinal(), reminderMenu.getDisplayName());
    }
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    int menuItemIndex = item.getItemId();
    ReminderMenu reminderMenu = ReminderMenu.values()[menuItemIndex];
    reminderMenu.execute(this, info.position);
    return true;
  }

  public void clearCheckboxes(View view) {
    for (int i = 0; i < getListAdapter().getCount(); i++) {
      getListView().setItemChecked(i, false);
    }
  }

  public void newReminder(View view) {
    newReminder(view, null, -1);
  }

  protected void newReminder(View view, final Reminder existingReminder, final int existingReminderIndex) {
    final Dialog dialog = new Dialog(this);
    dialog.setContentView(R.layout.new_reminder_dialog);
    dialog.setTitle("Create Reminder");
    dialog.setOwnerActivity(this);

    Button submitButton = (Button) dialog.findViewById(R.id.Submit);
    Button cancelButton = (Button) dialog.findViewById(R.id.Cancel);
    final EditText reminderText = (EditText) dialog.findViewById(R.id.newReminderText);
    if (existingReminder != null) {
      reminderText.setText(existingReminder.getText());
    }
    submitButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        String text = reminderText.getText().toString();
        if (existingReminder != null) {
          List<Reminder> reminders = _globalPreferences.getReminders(_nextMoonEvent, false);
          reminders.get(existingReminderIndex).setText(text);
          _globalPreferences.saveReminders(_nextMoonEvent, reminders);
          getListAdapter().clear();
          for (Reminder reminder : reminders) {
            getListAdapter().add(reminder);
          }
        } else {
          Reminder reminder = new Reminder(text);
          _globalPreferences.addReminder(_nextMoonEvent, reminder);
          ReminderActivity.this.getListAdapter().add(reminder);
        }
        dialog.dismiss();
      }
    });
    cancelButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
      }
    });
    dialog.show();
  }

  @SuppressWarnings("unchecked")
  @Override
  public ArrayAdapter<Reminder> getListAdapter() {
    return (ArrayAdapter<Reminder>) super.getListAdapter();
  }

  @Override
  protected void onPause() {
    List<Reminder> reminders = _globalPreferences.getReminders(_nextMoonEvent, false);
    for (int i = 0; i < reminders.size(); i++) {
      reminders.get(i).setChecked(getListView().isItemChecked(i));
    }
    _globalPreferences.saveReminders(_nextMoonEvent, reminders);
    super.onPause();
  }

  private static enum ReminderMenu {
    EDIT("Edit") {
      @Override
      public void execute(ReminderActivity activity, int reminderIndex) {
        Reminder reminder = activity.getListAdapter().getItem(reminderIndex);
        activity.newReminder(null, reminder, reminderIndex);
      }
    },
    DELETE("Delete") {
      @Override
      public void execute(ReminderActivity activity, int reminderIndex) {
        activity._globalPreferences.removeReminder(activity._nextMoonEvent, reminderIndex);
        activity.getListAdapter().remove(activity.getListAdapter().getItem(reminderIndex));
      }
    },
    MOVE_UP("Move Up") {
      @Override
      public void execute(ReminderActivity activity, int reminderIndex) {
        if (reminderIndex > 0) {
          moveReminderPostion(activity, reminderIndex, reminderIndex - 1);
        }
      }
    },
    MOVE_DOWN("Move Down") {
      @Override
      public void execute(ReminderActivity activity, int reminderIndex) {
        if (reminderIndex < activity.getListAdapter().getCount() - 1) {
          moveReminderPostion(activity, reminderIndex, reminderIndex + 1);
        }
      }
    };

    private final String _displayName;

    private ReminderMenu(String displayName) {
      _displayName = displayName;
    }

    public String getDisplayName() {
      return _displayName;
    }

    public abstract void execute(ReminderActivity activity, int reminderIndex);

    private static void moveReminderPostion(ReminderActivity activity, int reminderIndex, int toIndex) {
      List<Reminder> reminders = activity._globalPreferences.getReminders(activity._nextMoonEvent, false);
      Reminder reminder = reminders.remove(reminderIndex);
      reminders.add(toIndex, reminder);
      activity._globalPreferences.saveReminders(activity._nextMoonEvent, reminders);

      ArrayAdapter<Reminder> listAdapter = activity.getListAdapter();
      reminder = listAdapter.getItem(reminderIndex);
      listAdapter.remove(reminder);
      listAdapter.insert(reminder, toIndex);
    }
  }
}
