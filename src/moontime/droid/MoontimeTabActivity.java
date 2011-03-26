package moontime.droid;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;

public class MoontimeTabActivity extends TabActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.tab_layout);

    TabHost tabHost = getTabHost(); // The activity TabHost
    TabHost.TabSpec spec; // Reusable TabSpec for each tab
    Intent intent; // Reusable Intent for each tab

    // Initialize a TabSpec for each tab and add it to the TabHost
    intent = new Intent().setClass(this, CalenderActivity.class);
    intent.fillIn(getIntent(), 0);
    spec = tabHost.newTabSpec("calender").setIndicator("Calender").setContent(intent);
    tabHost.addTab(spec);
    tabHost.getTabWidget().getChildAt(0).getLayoutParams().height = 33;

    // Do the same for the other tabs
    intent = new Intent().setClass(this, ReminderActivity.class);
    intent.fillIn(getIntent(), 0);
    spec = tabHost.newTabSpec("reminders").setIndicator("Reminders").setContent(intent);
    tabHost.addTab(spec);
    tabHost.getTabWidget().getChildAt(1).getLayoutParams().height = 33;

    tabHost.setCurrentTab(0);
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
