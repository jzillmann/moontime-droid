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
    // TODO +/- hours to event ? in titel
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
    menu.add(Menu.NONE, 0, 0, "Delete");
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    int menuItemIndex = item.getItemId();
    if (menuItemIndex == 0) {
      // delete
      _globalPreferences.removeReminder(_nextMoonEvent, info.position);
      getListAdapter().remove(getListAdapter().getItem(info.position));
    } else {
      throw new UnsupportedOperationException(menuItemIndex + "");
    }
    return true;
  }

  public void clearCheckboxes(View view) {
    for (int i = 0; i < getListAdapter().getCount(); i++) {
      getListView().setItemChecked(i, false);
    }
  }

  public void newReminder(View view) {
    final Dialog dialog = new Dialog(this);
    dialog.setContentView(R.layout.new_reminder_dialog);
    dialog.setTitle("Create Reminder");
    dialog.setOwnerActivity(this);

    Button submitButton = (Button) dialog.findViewById(R.id.Submit);
    Button cancelButton = (Button) dialog.findViewById(R.id.Cancel);
    submitButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        EditText reminderText = (EditText) dialog.findViewById(R.id.newReminderText);
        Reminder reminder = new Reminder(reminderText.getText().toString());
        _globalPreferences.addReminder(_nextMoonEvent, reminder);
        ReminderActivity.this.getListAdapter().add(reminder);
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
}
