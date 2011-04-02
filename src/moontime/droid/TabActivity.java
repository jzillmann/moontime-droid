package moontime.droid;

import roboguice.activity.RoboTabActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class TabActivity extends RoboTabActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tab_layout);

    // Initialize a TabSpec for each tab and add it to the TabHost
    getTabHost().addTab(createTab("Calendar", CalendarActivity.class));
    getTabHost().addTab(createTab("Reminders", ReminderActivity.class));

    getTabHost().setCurrentTab(0);
    for (int i = 0; i < getTabHost().getTabWidget().getChildCount(); i++) {
      getTabHost().getTabWidget().getChildAt(i).getLayoutParams().height /= 2;
    }
  }

  private TabSpec createTab(String title, Class<? extends Activity> activityClass) {
    Intent intent = new Intent().setClass(this, activityClass);
    intent.fillIn(getIntent(), 0);

    TabHost.TabSpec spec = getTabHost().newTabSpec(title.toLowerCase());
    spec.setIndicator(title);
    spec.setContent(intent);
    return spec;
  }

  @Override
  protected void onChildTitleChanged(Activity childActivity, CharSequence title) {
    super.onChildTitleChanged(childActivity, title);
    setTitle(getResources().getString(R.string.app_name) + " - " + title);
  }
  //
  // @Override
  // public void onContentChanged() {
  // super.onContentChanged();
  // System.out.println("MoontimeTabActivity.onContentChanged()" +
  // getLocalActivityManager().getCurrentActivity());
  // }
  //
  // @Override
  // protected void onChildTitleChanged(Activity childActivity, CharSequence
  // title) {
  // super.onChildTitleChanged(childActivity, title);
  // System.out.println("MoontimeTabActivity.onChildTitleChanged()" +
  // getLocalActivityManager().getCurrentActivity());
  // }

}
